package com.hypebeast.hbsdk.api.model;

import com.google.gson.annotations.SerializedName;


/**
 * Created by hesk on 30/6/15.
 */
public class Response<T> {
    /**
     * brands
     */
    @SerializedName("brands")
    public Object brands;

    /**
     * allBrands
     */
    @SerializedName("allBrands")
    public Object allbrands;

    @SerializedName("current_page")
    public int page;

    @SerializedName("taxon")
    public Object taxondata;

    /**
     * Response data
     * Object, array of objects or string for errors
     */
    @SerializedName("products")
    public Object productsdata;

    @SerializedName("product")
    public Object singelproduct;

    @SerializedName("error")
    public Object error;
}
