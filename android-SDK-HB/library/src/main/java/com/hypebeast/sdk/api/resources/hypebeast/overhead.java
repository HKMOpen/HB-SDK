package com.hypebeast.sdk.api.resources.hypebeast;

import com.hypebeast.sdk.api.exception.ApiException;
import com.hypebeast.sdk.api.model.hbeditorial.Foundation;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by hesk on 12/1/16.
 */
public interface overhead {

    @GET("/api/mobile-app-config")
    void mobile_config(final Callback<Foundation> cb) throws ApiException;

    @GET("/")
    void mobile_config_get(final Callback<Foundation> cb) throws ApiException;
}
