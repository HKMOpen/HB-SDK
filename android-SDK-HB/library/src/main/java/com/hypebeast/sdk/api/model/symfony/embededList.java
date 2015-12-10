package com.hypebeast.sdk.api.model.symfony;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hesk on 7/1/2015.
 */
public class embededList {
    /**
     * the normal product listing only
     */
    @SerializedName("products")
    public List<Product> productswrap;


    /**
     * this is the server wish list items only
     */
    @SerializedName("items")
    public List<wish> wishlist;

}
