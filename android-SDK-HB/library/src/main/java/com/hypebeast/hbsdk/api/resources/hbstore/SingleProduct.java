package com.hypebeast.hbsdk.api.resources.hbstore;

import com.hypebeast.hbsdk.api.exception.ApiException;
import com.hypebeast.hbsdk.api.model.head.ResponseSingleProduct;

import retrofit.http.GET;

/**
 * Created by hesk on 7/1/2015.
 */
public interface SingleProduct {
    @GET("")
    ResponseSingleProduct getIt() throws ApiException;

}
