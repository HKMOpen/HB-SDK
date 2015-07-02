package com.hypebeast.hbsdk.clients;

import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hypebeast.hbsdk.DisqusConstants;
import com.hypebeast.hbsdk.api.exception.ApiException;
import com.hypebeast.hbsdk.api.exception.BadRequestException;
import com.hypebeast.hbsdk.api.exception.ForbiddenException;
import com.hypebeast.hbsdk.api.exception.InternalServerError;
import com.hypebeast.hbsdk.api.exception.NotFoundException;
import com.hypebeast.hbsdk.api.gson.RealmExclusion;
import com.hypebeast.hbsdk.api.model.commonterms.Product;
import com.hypebeast.hbsdk.api.resources.hbstore.Brand;
import com.hypebeast.hbsdk.api.resources.hbstore.Products;
import com.hypebeast.hbsdk.api.resources.hbstore.SingleProduct;

import retrofit.ErrorHandler;
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
    private static final String BASE_LOGIN = "https://disqus.com/api";
    /**
     * User agent
     */
    private static final String USER_AGENT = "HypebeastStoreApp/1.0 Android" + Build.VERSION.SDK_INT;

    /**
     * login adapter
     */
    private RestAdapter mLoginAdapter;


    public HBStoreApiClient() {
        gsonsetup = new GsonBuilder()
                .setDateFormat(DisqusConstants.DATE_FORMAT)
   /*             .registerTypeAdapter(Usage.class, new ApplicationsUsageDeserializer())
                .registerTypeAdapterFactory(new BlacklistsEntryTypeAdapterFactory())
                .registerTypeAdapterFactory(new PostTypeAdapterFactory())
                .registerTypeAdapterFactory(new ThreadTypeAdapterFactory())*/
                .setExclusionStrategies(new RealmExclusion())
                .create();
        registerAdapter();
    }

    protected void overhead() {

    }

    @Override
    protected void registerAdapter() {
        mAdapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL_STORE)
                .setLogLevel(RestAdapter.LogLevel.HEADERS)
                .setErrorHandler(handlerError)
                .setRequestInterceptor(getIn())
                .setConverter(new GsonConverter(gsonsetup))
                .build();
    }

    @Override
    protected String get_USER_AGENT() {
        return USER_AGENT;
    }

    private RestAdapter fullEndpoint(final String endpoint) {
        return new RestAdapter.Builder()
                .setEndpoint(endpoint)
                .setLogLevel(RestAdapter.LogLevel.HEADERS)
                .setErrorHandler(handlerError)
                .setRequestInterceptor(getIn())
                .setConverter(new GsonConverter(gsonsetup))
                .build();
    }

    public Products createProducts() {
        return mAdapter.create(Products.class);
    }



    public Brand createBrand() {
        return mAdapter.create(Brand.class);
    }

    public SingleProduct createRequest(String url_full_product) {
        return fullEndpoint(url_full_product).create(SingleProduct.class);
    }
}
