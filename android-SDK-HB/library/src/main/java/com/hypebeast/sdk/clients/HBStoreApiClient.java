package com.hypebeast.sdk.clients;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.hypebeast.sdk.Constants;
import com.hypebeast.sdk.Util.Connectivity;
import com.hypebeast.sdk.Util.CookieHanger;
import com.hypebeast.sdk.api.gson.GsonFactory;
import com.hypebeast.sdk.api.gson.MissingCharacterConversion;
import com.hypebeast.sdk.api.gson.RealmExclusion;
import com.hypebeast.sdk.api.gson.StringConverter;
import com.hypebeast.sdk.api.gson.WordpressConversion;
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

import java.util.List;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by hesk on 30/6/15.
 */
public class HBStoreApiClient extends Client implements WishlistSync.syncResult {
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

    public void addToWishList(Context basethread, Product copyproduct) {
        this.mWishlist.addToWishList(copyproduct);
    }

    public void flushWishList(Context basethread) {
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

    public HBStoreApiClient hookSyncTasker(ConfigurationSync instance) {
        data = instance;
        data.load_dictionary_auto();
        return this;
    }

    protected WishlistSync.syncResult sync_result_wishlist;

    public HBStoreApiClient setInterceptWishListProcess(WishlistSync.syncResult sync) {
        sync_result_wishlist = sync;
        return this;
    }

    public void requestWishList() {
        if (isCurrentLogin()) {
            if (sync_result_wishlist == null) {
                mWishlist.syncInit(this, this);
            } else {
                mWishlist.syncInit(this, sync_result_wishlist);
            }
        }
    }

    public void requestUpSyncWishList() {
        if (isCurrentLogin()) {
            mWishlist.syncUp();
        }
    }

    @Override
    public void successDownStream(List<wish> wistlist) {

    }

    @Override
    public void successUpStream() {

    }

    @Override
    public void failure(String error_message_out) {

    }

/*
    optimizelyEndUserId=oeu1427704786547r0.16302457707934082; PHPSYLIUSID=hotrhic7v0renb44306oc60dl7; wordpress_logged_in_48e9f22ffa424b200d04b29b8dcb2a90=ooxhesk%40yahoo.com.hk%7C1460701032%7C97c1e6d85a010990a1cfcfc42c1b8c6d; __gads=ID=80b6bbb1c91b0d2e:T=1431330005:S=ALNI_MaTg8WA4ot-TFrR9dcH1kz2o1cxTw; optimizelySegments=%7B%222652000965%22%3A%22false%22%2C%222671940073%22%3A%22gc%22%2C%222673440032%22%3A%22false%22%2C%222682660012%22%3A%22referral%22%2C%222685030137%22%3A%22campaign%22%2C%222686260265%22%3A%22gc%22%7D; optimizelyBuckets=%7B%7D; __qca=P0-479511623-1431489547842; geo_redirect_off=yes; wooTracker=CbeRWN9ZlwnU; wooTracker=CbeRWN9ZlwnU; _chartbeat2=CORluQBqZeUOKr5xy.1427704788401.1437459868424.0000000000000011; _ga=GA1.2.434876542.1427704787; gsScrollPos=; hbx_catalog_country=HK; __zlcmid=U0eXYMtAwZnotG; _store_item_count=1*/

}
