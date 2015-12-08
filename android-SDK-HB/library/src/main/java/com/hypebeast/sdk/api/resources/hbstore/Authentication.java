package com.hypebeast.sdk.api.resources.hbstore;

import android.telecom.Call;

import com.hypebeast.sdk.api.exception.ApiException;
import com.hypebeast.sdk.api.model.hypebeaststore.ResLoginCheck;
import com.hypebeast.sdk.api.model.hypebeaststore.ResLoginPassword;
import com.hypebeast.sdk.api.model.hypebeaststore.ResponseBrandList;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by hesk on 29/10/15.
 */
public interface Authentication {

    @FormUrlEncoded
    @POST("/login_check")
    void checkLoginStatus(final @Field("_username") String user,
                          final @Field("_password") String pass,
                          final Callback<String> raw
    ) throws ApiException;

    @FormUrlEncoded
    @POST("/register")
    void checkLoginV2(final @Field("_username") String user,
                      final @Field("_password") String pass,
                      final Callback<ResLoginPassword> raw
    ) throws ApiException;

    /**
     * this is developed to use for checking the user login
     * the session_cookie_id the previously stored session id / cookie token should be embeded on the http request
     *
     * @param item the input item
     * @throws ApiException the error
     */
    @GET("/api/v1/users/me")
    void checkLoginV1(final Callback<ResLoginCheck> item) throws ApiException;
}
