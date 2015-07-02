package com.hypebeast.hbsdk.api.resources.hbstore;

import com.hypebeast.hbsdk.api.exception.ApiException;
import com.hypebeast.hbsdk.api.model.head.ResponseBrandList;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by hesk on 7/1/2015.
 */
public interface Brand {

    @GET("/brands")
    ResponseBrandList getAll() throws ApiException;

    @GET("/brands")
    ResponseBrandList getBy(final @Query("category") String cate) throws ApiException;

}
