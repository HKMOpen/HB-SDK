package com.hypebeast.sdk.application.hbx;

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.hypebeast.sdk.api.RealmUtil;
import com.hypebeast.sdk.api.exception.ApiException;
import com.hypebeast.sdk.api.model.hypebeaststore.ResponseProductList;
import com.hypebeast.sdk.api.model.symfony.Product;
import com.hypebeast.sdk.api.model.symfony.wish;
import com.hypebeast.sdk.api.realm.hbx.rProduct;
import com.hypebeast.sdk.api.resources.hbstore.Products;
import com.hypebeast.sdk.clients.HBStoreApiClient;


import net.sjava.advancedasynctask.AdvancedAsyncTask;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by hesk on 10/12/15.
 */


public class WishlistSync {
    private static final int LIMIT = 10;
    private int totalpages = 1;
    private int current_page = 1;
    private int skipped_upstream_items = 0;
    private String error_message;
    private Products client;
    private syncResult message_channel;
    private List<wish> server_list = new ArrayList<wish>();
    private Context context;
    private int worker_status;
    private final RealmConfiguration conf;
    public static final int
            STATUS_IDEAL = 1,
            STATUS_DOWN_STREAM = 2,
            STATUS_UP_STREAM = 3;

    @IntDef({STATUS_IDEAL, STATUS_DOWN_STREAM, STATUS_UP_STREAM})
    public @interface WishlistSyncStatus {

    }

    public WishlistSync(Context _c) {
        context = _c;
        worker_status = STATUS_IDEAL;
        conf = RealmUtil.realmCfg(_c);
    }

    /**
     * start to initiate a down stream request to the server side
     *
     * @param client the api client
     * @param result the optional call back
     */
    public void syncInit(HBStoreApiClient client, final @Nullable syncResult result) {
        current_page = 1;
        worker_status = STATUS_DOWN_STREAM;
        this.client = client.createProducts();
        message_channel = result;
        server_list.clear();
        syncDown();
    }


    public void syncUp() {
        worker_status = STATUS_UP_STREAM;
        construct_removal_list();
        final List<rProduct> product_f = getWishListAll();
        final Iterator<rProduct> iterator = product_f.iterator();
        final Iterator<Long> removal_iter = remove_list.iterator();
        if (iterator.hasNext() || removal_iter.hasNext()) {
            continueAddItem(iterator.next().getProduct_id(), iterator, removal_iter);
        } else {
            upstreamDoneTrigger();
        }
    }

    private void syncDown() {
        try {
            this.client.wishlist(current_page, new DownSyncCallBack());
        } catch (ApiException e) {
            //there is an error to get the list. maybe it is the login issue.
            error_message = e.getMessage() + " current page:" + current_page;
            errorTrigger();
        }
    }

    @WishlistSyncStatus
    public int getStatus() {
        return worker_status;
    }

    /**
     * locally add to the wish list
     *
     * @param copyproduct the client interface
     * @return bool result of the wishlist being added.
     */
    public boolean addToWishList(Product copyproduct) {
        Realm realm = Realm.getInstance(conf);
        if (check_saved_wishlist(realm, copyproduct.product_id)) return false;
        realm.beginTransaction();
        createAndConvertFromProduct(realm, copyproduct);
        realm.commitTransaction();
        return true;
    }

    private rProduct createAndConvertFromProduct(final Realm r, final Product copyproduct) {
        rProduct _p = r.createObject(rProduct.class);
        _p.setLinks(copyproduct._links.self.href);
        _p.setImageHead(copyproduct.images.get(0).data.medium.href);
        _p.setProduct_id(copyproduct.product_id);
        _p.setCreated_at(copyproduct.created_at);
        _p.setDescription(copyproduct.description);
        _p.setName(copyproduct.name);
        _p.setPrice(copyproduct.price);
        _p.setBrandname(copyproduct.get_brand_name());
        return _p;
    }

    public void flushWishList() {
        Realm realm = Realm.getInstance(conf);
        RealmResults<rProduct> copies = realm.where(rProduct.class).findAll();
        realm.beginTransaction();
        copies.clear();
        realm.commitTransaction();
    }

    public void removeItem(long product_id) {
        Realm realm = Realm.getInstance(conf);
        RealmResults<rProduct> copies = realm.where(rProduct.class).equalTo("product_id", product_id).findAll();
        realm.beginTransaction();
        copies.clear();
        realm.commitTransaction();
    }

    public void removeItem(Context basethread, rProduct item) {
        Realm realm = Realm.getInstance(basethread);
        RealmResults<rProduct> copies = realm.where(rProduct.class).equalTo("product_id", item.getProduct_id()).findAll();
        realm.beginTransaction();
        copies.clear();
        realm.commitTransaction();
    }

    public List<rProduct> getWishListAll() {
        Realm realm = Realm.getInstance(conf);
        RealmResults<rProduct> copies = realm.where(rProduct.class).findAll();
        return copies;
    }

    /**
     * the result TRUE = found!!
     *
     * @param realm      the REALM
     * @param product_id the product ID
     * @return BOOL
     */
    public boolean check_saved_wishlist(Realm realm, long product_id) {
        RealmQuery<rProduct> query = realm.where(rProduct.class);
        query.equalTo("product_id", product_id);
        // Execute the query:
        RealmResults<rProduct> result = query.findAll();
        return result.size() > 0;
    }

    public boolean check_saved_wishlist(long product_id) {
        Realm realm = Realm.getInstance(conf);
        return check_saved_wishlist(realm, product_id);
    }

    private void errorTrigger() {
        if (message_channel != null) message_channel.failure(error_message);
    }

    private ArrayList<Long> remove_list = new ArrayList<>();

    private ArrayList<Long> construct_removal_list() {
        remove_list.clear();
        Iterator<wish> i = server_list.iterator();
        while (i.hasNext()) {
            wish p = (wish) i.next();
            if (!check_saved_wishlist(p.wish_item.product_id)) {
                remove_list.add(p.wish_item.product_id);
            }
        }
        return remove_list;
    }

    public class processAddToWishList extends AdvancedAsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Iterator<wish> i = server_list.iterator();
            while (i.hasNext()) {
                wish p = (wish) i.next();
                if (!check_saved_wishlist(p.wish_item.product_id)) {
                    addToWishList(p.wish_item);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
            downstreamDoneTrigger();
        }

    }

    private void downstreamDoneTrigger() {
        worker_status = STATUS_IDEAL;
        if (message_channel != null) message_channel.successDownStream(server_list);
    }

    private void upstreamDoneTrigger() {
        worker_status = STATUS_IDEAL;
        if (message_channel != null) message_channel.successUpStream(skipped_upstream_items);
    }

    private void continueAddItem(final long product_id, Iterator<rProduct> iterator, Iterator<Long> off) {
        try {
            client.addItemWishList(product_id, new UpSyncCallBack(iterator, off));
        } catch (ApiException e) {
            error_message = e.getMessage() + " on initiating upstream.";
            errorTrigger();
        }
    }

    private void continueRemoveItem(final long product_id, Iterator<rProduct> iterator, Iterator<Long> off) {
        try {
            client.removeItemWishList(product_id, new UpSyncCallBack(iterator, off));
        } catch (ApiException e) {
            error_message = e.getMessage() + " on initiating upstream.";
            errorTrigger();
        }
    }

    private class DownSyncCallBack implements Callback<ResponseProductList> {

        /**
         * Successful HTTP response.
         *
         * @param responseProductList the product list
         * @param response            the response retrofit
         */
        @Override
        public void success(ResponseProductList responseProductList, Response response) {
            totalpages = responseProductList.totalpages();
            if (responseProductList.getWishes().size() < LIMIT) {
                server_list.addAll(responseProductList.getWishes());
                new processAddToWishList().execute();
                return;
            } else if (current_page < totalpages) {
                server_list.addAll(server_list.size(), responseProductList.getWishes());
                ++current_page;
                try {
                    client.wishlist(current_page, this);
                } catch (ApiException e) {
                    //there is an error to get the list. maybe it is the login issue.
                    error_message = e.getMessage() + " current page:" + current_page;
                    errorTrigger();
                }
            } else {
                //downstream done
                new processAddToWishList().execute();
            }
        }


        /**
         * Unsuccessful HTTP response due to network failure, non-2XX status code, or unexpected
         * exception.
         *
         * @param error the error output
         */
        @Override
        public void failure(RetrofitError error) {
            error_message = "request error:" + error.getMessage() + " current page:" + current_page;
            errorTrigger();
        }

    }


    private class UpSyncCallBack implements Callback<String> {
        private final Iterator<rProduct> offline_list;
        private final Iterator<Long> remove_offline;

        UpSyncCallBack(Iterator<rProduct> item, Iterator<Long> itemoff) {
            offline_list = item;
            remove_offline = itemoff;
        }

        /**
         * Successful HTTP response.
         *
         * @param s        body
         * @param response result
         */
        @Override
        public void success(String s, Response response) {
            Log.d("upsync", "Added:" + s);
            nextItem();
        }

        /**
         * Unsuccessful HTTP response due to network failure, non-2XX status code, or unexpected
         * exception.
         *
         * @param error the error of not able to add this item
         */
        @Override
        public void failure(RetrofitError error) {
            if (offline_list.hasNext()) skipped_upstream_items++;
            Log.d("upsync", "UpSync:" + error.getMessage());
            nextItem();
        }

        private boolean is_found_on_the_server_side(final long offline_product_id) {
            if (server_list.size() > 0) {
                Iterator<wish> i = server_list.iterator();
                while (i.hasNext()) {
                    wish p = (wish) i.next();
                    if (p.wish_item.product_id == offline_product_id) {
                        nextItem();
                        return true;
                    }
                }
                return false;
            } else return false;
        }

        private void nextItem() {
            if (offline_list.hasNext()) {
                final long ID = offline_list.next().getProduct_id();
                if (!is_found_on_the_server_side(ID))
                    continueAddItem(ID, offline_list, remove_offline);
            } else if (remove_offline.hasNext()) {
                final long ID = remove_offline.next();
                continueRemoveItem(ID, offline_list, remove_offline);
            } else {
                upstreamDoneTrigger();
            }
        }
    }


    /**
     * the callback interface for the wishlist sync
     */
    public interface syncResult {
        void successDownStream(final List<wish> wistlist);

        void successUpStream(final int skipped_items_on_hold);

        void failure(String error_message_out);
    }

}
