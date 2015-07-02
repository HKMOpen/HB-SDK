package com.hypebeast.hbsdk.api.model.hypebeast;

import com.google.gson.annotations.SerializedName;
import com.hypebeast.hbsdk.api.model.commonterms.ProductGroupContainer;

import java.util.ArrayList;

/**
 * Created by hesk on 2/23/15.
 */
public class connection {
    @SerializedName("self")
    public LinkContainer self;
    @SerializedName("brand")
    public LinkContainer brand;
    @SerializedName("group_products")
    public ArrayList<ProductGroupContainer> group_products = new ArrayList<>();

}
