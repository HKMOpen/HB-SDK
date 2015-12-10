package com.hypebeast.sdk.application.hbx;

import android.content.Context;
import android.support.annotation.Nullable;

import com.hypebeast.sdk.api.exception.ApiException;
import com.hypebeast.sdk.api.model.hypebeaststore.ResponseProductList;
import com.hypebeast.sdk.api.model.symfony.Product;
import com.hypebeast.sdk.api.model.symfony.wish;
import com.hypebeast.sdk.api.realm.hbx.rProduct;
import com.hypebeast.sdk.api.resources.hbstore.Products;
import com.hypebeast.sdk.clients.HBStoreApiClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by hesk on 10/12/15.
 */
public class WishlistSync implements Callback<ResponseProductList> {
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
    public static int
            STATUS_IDEAL = 1,
            STATUS_DOWN_STREAM = 2,
            STATUS_UP_STREAM = 3;

    public WishlistSync(Context _context) {
        context = _context;
        worker_status = STATUS_IDEAL;
    }

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

            downstreamDoneTrigger();
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
            downstreamDoneTrigger();
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
        try {
            this.client.wishlist(current_page, this);
        } catch (ApiException e) {
            //there is an error to get the list. maybe it is the login issue.
            error_message = e.getMessage() + " current page:" + current_page;
            errorTrigger();
        }
    }

    public void syncUp() {
        worker_status = STATUS_UP_STREAM;
        final List<rProduct> product_f = getWishListAll();
        final Iterator<rProduct> iterator = product_f.iterator();
        if (iterator.hasNext()) {
            continueAddItem(iterator);
        } else {
            upstreamDoneTrigger();
        }
    }

    public int getStatus() {
        return worker_status;
    }

    /**
     * locally add to the wish list
     *
     * @param copyproduct the client interface
     */
    public void addToWishList(Product copyproduct) {
        Realm realm = Realm.getInstance(context);
        if (check_saved_wishlist(realm, copyproduct.product_id)) return;
        realm.beginTransaction();
        createAndConvertFromProduct(realm, copyproduct);
        realm.commitTransaction();
    }

    private rProduct createAndConvertFromProduct(final Realm r, final Product copyproduct) {
        rProduct _product = r.createObject(rProduct.class);
        _product.setLinks(copyproduct._links.self.href);
        _product.setImageHead(copyproduct.images.get(0).data.medium.href);
        _product.setProduct_id(copyproduct.product_id);
        _product.setCreated_at(copyproduct.created_at);
        _product.setDescription(copyproduct.description);
        _product.setName(copyproduct.name);
        _product.setPrice(copyproduct.price);
        _product.setBrandname(copyproduct.get_brand_name());
        return _product;
    }

    public void flushWishList() {
        Realm realm = Realm.getInstance(context);
        RealmResults<rProduct> copies = realm.where(rProduct.class).findAll();
        realm.beginTransaction();
        copies.clear();
        realm.commitTransaction();
    }

    public void removeItem(long product_id) {
        Realm realm = Realm.getInstance(context);
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
        Realm realm = Realm.getInstance(context);
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
        Realm realm = Realm.getInstance(context);
        return check_saved_wishlist(realm, product_id);
    }

    private void errorTrigger() {
        if (message_channel != null) message_channel.failure(error_message);
    }

    private void downstreamDoneTrigger() {
        Iterator<wish> i = server_list.iterator();
        while (i.hasNext()) {
            wish p = (wish) i.next();
            if (!check_saved_wishlist(p.wish_item.product_id)) {
                addToWishList(p.wish_item);
            }
        }
        worker_status = STATUS_IDEAL;
        if (message_channel != null) message_channel.successDownStream(server_list);
    }

    private void upstreamDoneTrigger() {
        worker_status = STATUS_IDEAL;
        if (message_channel != null) message_channel.successUpStream();
    }


    private void continueAddItem(Iterator<rProduct> iterator) {
        try {
            client.addItemWishList(iterator.next().getProduct_id(), new upStreamCallback(iterator));
        } catch (ApiException e) {
            error_message = e.getMessage() + " on initiating upstream.";
            errorTrigger();
        }
    }

    private class upStreamCallback implements Callback<String> {
        private final Iterator<rProduct> iterator;

        upStreamCallback(Iterator<rProduct> item) {
            iterator = item;
        }

        /**
         * Successful HTTP response.
         *
         * @param s        body
         * @param response result
         */
        @Override
        public void success(String s, Response response) {
            if (iterator.hasNext()) {
                continueAddItem(iterator);
            } else {
                upstreamDoneTrigger();
            }
        }

        /**
         * Unsuccessful HTTP response due to network failure, non-2XX status code, or unexpected
         * exception.
         *
         * @param error the error of not able to add this item
         */
        @Override
        public void failure(RetrofitError error) {
            skipped_upstream_items++;
            continueAddItem(iterator);
        }
    }


    /**
     * the callback interface for the wishlist sync
     */
    public interface syncResult {
        void successDownStream(final List<wish> wistlist);

        void successUpStream();

        void failure(String error_message_out);
    }

}
