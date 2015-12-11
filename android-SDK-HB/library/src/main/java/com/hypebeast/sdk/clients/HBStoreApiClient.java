package com.hypebeast.sdk.clients;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.hypebeast.sdk.Constants;
import com.hypebeast.sdk.Util.Connectivity;
import com.hypebeast.sdk.Util.CookieHanger;
import com.hypebeast.sdk.api.exception.ApiException;
import com.hypebeast.sdk.api.gson.GsonFactory;
import com.hypebeast.sdk.api.gson.MissingCharacterConversion;
import com.hypebeast.sdk.api.gson.RealmExclusion;
import com.hypebeast.sdk.api.gson.StringConverter;
import com.hypebeast.sdk.api.gson.WordpressConversion;
import com.hypebeast.sdk.api.model.hypebeaststore.ReponseNormal;
import com.hypebeast.sdk.api.model.hypebeaststore.ResponseMobileOverhead;
import com.hypebeast.sdk.api.model.symfony.Product;
import com.hypebeast.sdk.api.model.symfony.wish;
import com.hypebeast.sdk.api.realm.hbx.rProduct;
import com.hypebeast.sdk.api.resources.hbstore.Authentication;
import com.hypebeast.sdk.api.resources.hbstore.Brand;
import com.hypebeast.sdk.api.resources.hbstore.Overhead;
import com.hypebeast.sdk.api.resources.hbstore.Products;
import com.hypebeast.sdk.api.resources.hbstore.SingleProduct;
import com.hypebeast.sdk.application.hbx.ConfigurationSync;
import com.hypebeast.sdk.application.hbx.WishlistSync;

import net.sjava.advancedasynctask.AdvancedAsyncTask;

import java.util.Iterator;
import java.util.List;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * Created by hesk on 30/6/15.
 */
public class HBStoreApiClient extends Client {
    /**
     * Base URL for all Disqus endpoints
     */
    private static final String BASE_URL_STORE = "https://store.hypebeast.com/";
    private static final String AUTHENTICATION = "http://hypebeast.com/";
    private static final String BASE_LOGIN = "https://disqus.com/api";
    /**
     * User agent
     */
    private static final String USER_AGENT = "HypebeastStoreApp/1.0 Android" + Build.VERSION.SDK_INT;

    /**
     * login adapter
     */
    private RestAdapter mLoginAdapter;
    private static HBStoreApiClient static_instance;
    private ConfigurationSync data;
    private WishlistSync mWishlist;
    protected WishlistSync.syncResult sync_result_wishlist;

    @Deprecated
    public static HBStoreApiClient newInstance() {
        return new HBStoreApiClient();
    }

    public static HBStoreApiClient newInstance(Context context) {
        return new HBStoreApiClient(context);
    }

    @Deprecated
    public static HBStoreApiClient getInstance() {
        if (static_instance == null) {
            static_instance = newInstance();
            return static_instance;
        } else {
            return static_instance;
        }
    }

    public static HBStoreApiClient getInstance(Context context) {
        if (static_instance == null) {
            static_instance = newInstance(context);
            return static_instance;
        } else {
            static_instance.setContext(context);
            return static_instance;
        }
    }

    /**
     * please do not use this as this is no longer supported.
     * It need to initiate with Context
     */
    @Deprecated
    public HBStoreApiClient() {
        super();
    }

    public HBStoreApiClient(Context context) {
        super(context);
        mWishlist = new WishlistSync(context);
    }

    private void setContext(Context c) {
        this.context = c;
    }

    @Override
    protected void registerAdapter() {
        buildCompletCacheRestAdapter(
                BASE_URL_STORE,
                context,
                RestAdapter.LogLevel.FULL);
    }

    @Override
    protected String get_USER_AGENT() {
        return USER_AGENT;
    }

    @Override
    protected void jsonCreate() {
        gsonsetup = new GsonBuilder()
                .setDateFormat(Constants.DATE_FORMAT)
                .registerTypeAdapterFactory(new GsonFactory.NullStringToEmptyAdapterFactory())
                .registerTypeAdapter(String.class, new WordpressConversion())
                .setExclusionStrategies(new RealmExclusion())
                .create();
    }

    public Products generalProducts(final String endpoint) {
        final RestAdapter adp = byURL(endpoint);
        return adp.create(Products.class);
    }

    private RestAdapter byURL(final String endpoint) {
        return new RestAdapter.Builder()
                .setEndpoint(endpoint)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setErrorHandler(handlerError)
                .setRequestInterceptor(getIn())
                .setConverter(new GsonConverter(gsonsetup))
                .build();
    }

    /**
     * this will only returns the authentication in string as result
     *
     * @return Authentication
     */
    @Deprecated
    public Authentication createAuthentication() {
        RestAdapter mAdapter = new RestAdapter.Builder()
                .setEndpoint(AUTHENTICATION)
                .setLogLevel(RestAdapter.LogLevel.HEADERS)
                .setErrorHandler(handlerError)
                .setRequestInterceptor(getIn())
                .setConverter(new StringConverter())
                .build();

        return mAdapter.create(Authentication.class);
    }

    public Authentication createAuthenticationHBX() {
        /**
         * create the authentication in here
         */
        RestAdapter mAdapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL_STORE)
                .setLogLevel(RestAdapter.LogLevel.HEADERS)
                .setErrorHandler(handlerError)
                .setRequestInterceptor(getIn())
                .setConverter(new GsonConverter(gsonsetup))
                .build();

        return mAdapter.create(Authentication.class);
    }

    public Products createProducts() {
        return mAdapter.create(Products.class);
    }

    public Overhead createOverHead() {
        return mAdapter.create(Overhead.class);
    }

    public Brand createBrand() {
        return mAdapter.create(Brand.class);
    }

    public SingleProduct createRequest() {
        return mAdapter.create(SingleProduct.class);
    }

    public String fromJsonToString(ResponseMobileOverhead mFoundation) {
        return "";
    }

    public ResponseMobileOverhead fromsavedConfiguration(String mFoundation_string) {
        return gsonsetup.fromJson(mFoundation_string, ResponseMobileOverhead.class);
    }

    private CookieHanger getCookieClient() {
        return CookieHanger.base(BASE_URL_STORE);
    }


    @Override
    protected RequestInterceptor getIn() {
        return new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("User-Agent", get_USER_AGENT());
                request.addHeader("Accept", "application/json");
                request.addHeader("X-Api-Version", "2.0");
                //String cookietst = getCookieClient().getRaw();
                //Log.d("loginHBX", "cookie set=" + cookietst);
                request.addHeader("Cookie", getCookieClient().getRaw());
                try {
                    if (Connectivity.isConnected(context)) {
                        request.addHeader("Cache-Control", "public, max-age=" + timeByMins(1));
                    } else {
                        request.addHeader("Cache-Control", "public, only-if-cached, max-stale=" + timeByWeeks(1));
                    }
                } catch (Exception e) {

                }
            }
        };
    }

    /**
     * set the cookie session Id out of the method
     *
     * @param session_code session code
     */
    public void setCookieSessionId(final String session_code) {
        getCookieClient().set_cookie_value("PHPSYLIUSID", session_code);
    }

    public void logout() throws NullPointerException {
        if (data == null)
            throw new NullPointerException("ConfigurationSync is not defined. Please make sure the {@ hookSyncTask} is triggered.");
        data.logout();
    }

    public void saveTokenAfterSuccessLogin() throws NullPointerException {
        final String session_code = getCookieClient().getValue("PHPSYLIUSID");
        if (data == null)
            throw new NullPointerException("ConfigurationSync is not defined. Please make sure the {@ hookSyncTask} is triggered.");
        Log.d("loginHBX", "retrieve cookie @ " + session_code);
        data.setLoginSuccess(session_code);
    }


    public boolean isCurrentLogin() throws NullPointerException {
        if (data == null)
            throw new NullPointerException("ConfigurationSync is not defined. Please make sure the {@ hookSyncTask} is triggered.");
        return data.isLoginStatusValid();
    }

    /**
     * the get the number of current shopping cart item count
     *
     * @return the count in number
     */
    public int retrieve_current_shopping_cart_items() {
        final String number = getCookieClient().getValue("_store_item_count");
        return Integer.parseInt(number);
    }

    @Deprecated
    public void addToWishList(Context basethread, Product copyproduct) {
        this.mWishlist.addToWishList(copyproduct);
    }

    /**
     * adding this product to the local wishlist
     *
     * @param copyproduct the product item
     * @return added or skipped
     */
    public boolean addToWishList(Product copyproduct) {
        return this.mWishlist.addToWishList(copyproduct);
    }

    @Deprecated
    public void flushWishList(Context basethread) {
        mWishlist.flushWishList();
    }

    public void flushWishList() {
        mWishlist.flushWishList();
    }

    public void removeItem(Context basethread, long product_id) {
        mWishlist.removeItem(product_id);
    }

    public void removeItem(Context basethread, rProduct item) {
        mWishlist.removeItem(basethread, item);
    }

    public List<rProduct> getWishListAll(Context basethread) {
        return mWishlist.getWishListAll();
    }

    public boolean check_saved_wishlist(long product_id) {
        return mWishlist.check_saved_wishlist(product_id);
    }

    public void addKeyword(String keyword) {
        if (data != null) {
            data.addKeyword(keyword);
            data.save_dictionary_auto();
        }
    }

    /**
     * bind with the Configuration instance for login and logout functions
     *
     * @param instance the instance
     * @return continue
     */
    public HBStoreApiClient hookSyncTasker(ConfigurationSync instance) {
        data = instance;
        data.load_dictionary_auto();
        return this;
    }

    public HBStoreApiClient setInterceptWishListProcess(final @Nullable WishlistSync.syncResult sync) {
        sync_result_wishlist = sync;
        return this;
    }

    /**
     * start downloading the wish list
     */
    public void requestWishList() {
        if (isCurrentLogin() && mWishlist.getStatus() == WishlistSync.STATUS_IDEAL) {
            mWishlist.syncInit(this, sync_result_wishlist);
        }
    }

    /**
     * uploading the wish list items
     */
    public void requestUpSyncWishList() {
        if (isCurrentLogin() && mWishlist.getStatus() == WishlistSync.STATUS_IDEAL) {
            mWishlist.syncUp();
        }
    }

    @WishlistSync.WishlistSyncStatus
    public int wishListWorkerStatus() {
        return mWishlist.getStatus();
    }

    /**
     * this is working for the testing purposes
     *
     * @param URL              the URI in string
     * @param trigger_complete the action with it is completed to store the items to the wishlist locally
     */
    public void store_all_items_to_wishlist_from_uri(final String URL, final store_all_items_mock trigger_complete) {
        try {
            generalProducts(URL).general(new Callback<ReponseNormal>() {
                @Override
                public void success(ReponseNormal reponseNormal, Response response) {
                    int processed = 0;
                    Iterator<Product> c = reponseNormal.product_list.getlist().iterator();
                    while (c.hasNext()) {
                        Product h = (Product) c.next();
                        boolean j = addToWishList(h);
                        if (j) processed++;
                    }

                    trigger_complete.success(reponseNormal.product_list.getlist(), processed, URL);
                }

                @Override
                public void failure(RetrofitError error) {
                    trigger_complete.falilure("Failure on request: " + error.getMessage());
                }
            });
        } catch (ApiException e) {
            trigger_complete.falilure("fail to add" + e.getMessage());
        }
    }

    /**
     * works for the mock object test
     */
    public interface store_all_items_mock {
        void success(List<Product> list, int total_items_processed, String URL);

        void falilure(String failed_message);
    }


}
