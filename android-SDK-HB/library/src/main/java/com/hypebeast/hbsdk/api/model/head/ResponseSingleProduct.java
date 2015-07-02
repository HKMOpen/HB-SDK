package com.hypebeast.hbsdk.api.model.head;

import com.google.gson.annotations.SerializedName;
import com.hypebeast.hbsdk.api.model.Alternative;
import com.hypebeast.hbsdk.api.model.commonterms.Product;

/**
 * Created by hesk on 7/1/2015.
 */
public class ResponseSingleProduct extends Alternative {
    @SerializedName("product")
    public Product mproduct;
}
