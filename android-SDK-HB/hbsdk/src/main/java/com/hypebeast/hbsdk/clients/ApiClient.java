/*
 * Copyright 2014 Phil Bayfield
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hypebeast.hbsdk.clients;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hypebeast.hbsdk.DisqusConstants;
import com.hypebeast.hbsdk.api.exception.APIIncorrectException;
import com.hypebeast.hbsdk.api.exception.ApiException;
import com.hypebeast.hbsdk.api.exception.BadRequestException;
import com.hypebeast.hbsdk.api.exception.ForbiddenException;
import com.hypebeast.hbsdk.api.exception.NotFoundException;
import com.hypebeast.hbsdk.api.gson.GsonFactory;
import com.hypebeast.hbsdk.api.gson.samples.ApplicationsUsageDeserializer;
import com.hypebeast.hbsdk.api.gson.samples.BlacklistsEntryTypeAdapterFactory;
import com.hypebeast.hbsdk.api.gson.samples.PostTypeAdapterFactory;
import com.hypebeast.hbsdk.api.gson.samples.ThreadTypeAdapterFactory;
import com.hypebeast.hbsdk.api.modelzero.applications.Usage;
import com.hypebeast.hbsdk.api.modelzero.posts.Media;
import com.hypebeast.hbsdk.api.resources.AccessTokenService;
import com.hypebeast.hbsdk.api.resources.Applications;
import com.hypebeast.hbsdk.api.resources.Blacklists;
import com.hypebeast.hbsdk.api.resources.Categories;
import com.hypebeast.hbsdk.api.resources.Exports;
import com.hypebeast.hbsdk.api.resources.Feeds;
import com.hypebeast.hbsdk.api.resources.Forums;
import com.hypebeast.hbsdk.api.resources.Imports;
import com.hypebeast.hbsdk.api.resources.Notes;
import com.hypebeast.hbsdk.api.resources.Posts;
import com.hypebeast.hbsdk.api.resources.Threads;
import com.hypebeast.hbsdk.api.resources.Users;
import com.hypebeast.hbsdk.api.resources.notes.Templates;
import com.hypebeast.hbsdk.api.retrofitworker.Interceptor;

import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * A wrapper round the Retrofit {@link RestAdapter}
 */
public class ApiClient {

    /**
     * Base URL for all Disqus endpoints
     */
    private static final String BASE_URL = "https://disqus.com/api/3.0";
    private static final String BASE_LOGIN = "https://disqus.com/api";

    /**
     * User agent
     */
    private static final String USER_AGENT = "Disqus Android/0.1";

    /**
     * Rest adapter
     */
    private RestAdapter mAdapter;
    /**
     * login adapter
     */
    private RestAdapter mLoginAdapter;
    /**
     * the API configurations
     */
    private final ApiConfig _config;
    private final Gson gsonsetup;
    private final ErrorHandler handlerError;

    /**
     * Set config and set up the {@link RestAdapter}
     *
     * @param config the collection of configuration
     */
    public ApiClient(final ApiConfig config) {
        // Build Gson with Disqus date format and type adapters
        gsonsetup = new GsonBuilder()
                .setDateFormat(DisqusConstants.DATE_FORMAT)
                .registerTypeAdapter(Usage.class, new ApplicationsUsageDeserializer())
                .registerTypeAdapterFactory(new BlacklistsEntryTypeAdapterFactory())
                .registerTypeAdapterFactory(new PostTypeAdapterFactory())
                .registerTypeAdapterFactory(new ThreadTypeAdapterFactory())
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
                    }
                }
                return new ApiException(cause);
            }
        };

        // Build RestAdapter for after login
        mAdapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL)
                .setLogLevel(config != null ? config.getLogLevel() : RestAdapter.LogLevel.NONE)
                .setErrorHandler(handlerError)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("User-Agent", USER_AGENT);
                        if (config != null) {
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
                        }
                    }

                })
                .setConverter(new GsonConverter(gsonsetup))
                .build();

        mLoginAdapter = new RestAdapter
                .Builder()
                .setLogLevel(config != null ? config.getLogLevel() : RestAdapter.LogLevel.NONE)
                .setEndpoint(BASE_LOGIN)
                .setConverter(new GsonConverter(GsonFactory.newGsonInstance()))
                .setRequestInterceptor(new Interceptor(config))
                .setErrorHandler(new ErrorHandler() {
                    @Override
                    public Throwable handleError(RetrofitError cause) {
                        Response response = cause.getResponse();
                        if (response != null) {
                            switch (response.getStatus()) {
                                case 400:
                                    return new APIIncorrectException(cause);
                                case 401:
                                    return new ForbiddenException(cause);
                                case 404:
                                    return new NotFoundException(cause);
                            }
                        }
                        return new ApiException(cause);
                    }
                })
                .build();

        _config = config;
    }

    /**
     * Create applications resource
     *
     * @return the object
     */
    public Applications createApplications() {
        return mAdapter.create(Applications.class);
    }

    /**
     * Create blacklists resource
     *
     * @return the object
     */
    public Blacklists createBlacklists() {
        return mAdapter.create(Blacklists.class);
    }

    /**
     * Create categories resource
     *
     * @return the object
     */
    public Categories createCategories() {
        return mAdapter.create(Categories.class);
    }

    /**
     * Create exports resource
     *
     * @return the object
     */
    public Exports createExports() {
        return mAdapter.create(Exports.class);
    }

    /**
     * Create feeds resource
     *
     * @return the object
     */
    public Feeds createFeeds() {
        return mAdapter.create(Feeds.class);
    }

    /**
     * Create forums resource
     *
     * @return the object
     */
    public Forums createForums() {
        return mAdapter.create(Forums.class);
    }

    /**
     * Create imports resource
     *
     * @return the object
     */
    public Imports createImports() {
        return mAdapter.create(Imports.class);
    }

    /**
     * Create media resource
     *
     * @return the object
     */
    public Media createMedia() {
        return mAdapter.create(Media.class);
    }

    /**
     * Create notes resource
     *
     * @return the object
     */
    public Notes createNotes() {
        return mAdapter.create(Notes.class);
    }

    /**
     * Create notes/templates resource
     *
     * @return the object
     */
    public Templates createNotesTemplates() {
        return mAdapter.create(Templates.class);
    }

    /**
     * Create posts resource
     *
     * @return the object
     */
    public Posts createPosts() {
        return mAdapter.create(Posts.class);
    }

    /**
     * Create threads resource
     *
     * @return Threads
     */
    public Threads createThreads() {
        return mAdapter.create(Threads.class);
    }

    /**
     * Create users resource
     *
     * @return users
     */
    public Users createUsers() {
        return mAdapter.create(Users.class);
    }

    /**
     * create the OAuth 2.0 mechanism
     *
     * @return access token service
     */
    private AccessTokenService createTokenService() {
        return mLoginAdapter.create(AccessTokenService.class);
    }

    public AuthMgr createAuthenticationManager(Context contex) {
        return new AuthMgr(contex, createTokenService(), _config);
    }
}
