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
import com.hypebeast.hbsdk.api.gson.samples.ApplicationsUsageDeserializer;
import com.hypebeast.hbsdk.api.gson.samples.BlacklistsEntryTypeAdapterFactory;
import com.hypebeast.hbsdk.api.gson.samples.PostTypeAdapterFactory;
import com.hypebeast.hbsdk.api.gson.samples.ThreadTypeAdapterFactory;
import com.hypebeast.hbsdk.api.modelzero.applications.Usage;

import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * Created by hesk on 30/6/15.
 */
public class HBStoreApiClient {

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
     * Rest adapter
     */
    private RestAdapter mAdapter;
    /**
     * login adapter
     */
    private RestAdapter mLoginAdapter;

    private final Gson gsonsetup;
    private final ErrorHandler handlerError;

    public HBStoreApiClient() {
        gsonsetup = new GsonBuilder()
                .setDateFormat(DisqusConstants.DATE_FORMAT)
                .registerTypeAdapter(Usage.class, new ApplicationsUsageDeserializer())
                .registerTypeAdapterFactory(new BlacklistsEntryTypeAdapterFactory())
                .registerTypeAdapterFactory(new PostTypeAdapterFactory())
                .registerTypeAdapterFactory(new ThreadTypeAdapterFactory())
                .setExclusionStrategies(new RealmExclusion())
                .create();

        handlerError = new ErrorHandler() {
            @Override
            public Throwable handleError(RetrofitError cause) {
                Response response = cause.getResponse();
                if (response != null) {
                    switch (response.getStatus()) {
                        case 400:
                            return new BadRequestException(cause);
                        case 401:
                            return new ForbiddenException(cause);
                        case 404:
                            return new NotFoundException(cause);
                        case 500:
                            return new InternalServerError(cause);
                    }
                }
                return new ApiException(cause);
            }
        };
        registerAdapterStore();
    }

    protected void overhead() {

    }

    protected void registerAdapterStore() {
        mAdapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL_STORE)
                .setLogLevel(RestAdapter.LogLevel.HEADERS)
                .setErrorHandler(handlerError)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("User-Agent", USER_AGENT);
                        request.addHeader("Accept", "application/json");
                        request.addHeader("X-Api-Version", "2.0");
                       /* if (config != null) {
                            // Public/secret key query params
                            if (config.getApiSecret() != null) {
                                request.addQueryParam("api_secret", config.getApiSecret());
                            } else if (config.getApiKey() != null) {
                                request.addQueryParam("api_key", config.getApiKey());
                            } else {

                            }

                            // Access token query param
                            if (config.getAccessToken() != null) {
                                request.addQueryParam("access_token", config.getAccessToken());
                            }

                            // Referrer
                            if (config.getReferrer() != null) {
                                request.addHeader("Referer", config.getReferrer());
                            }
                        }*/
                    }

                })
                .setConverter(new GsonConverter(gsonsetup))
                .build();
    }
}
