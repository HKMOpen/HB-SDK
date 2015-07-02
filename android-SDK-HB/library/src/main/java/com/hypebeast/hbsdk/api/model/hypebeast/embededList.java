package com.hypebeast.hbsdk.api.model.hypebeast;

import com.google.gson.annotations.SerializedName;
import com.hypebeast.hbsdk.api.model.commonterms.Product;
import com.hypebeast.hbsdk.api.model.head.ResponseProductList;

import java.util.List;

/**
 * Created by hesk on 7/1/2015.
 */
public class embededList {
    @SerializedName("products")
    public List<Product> productswrap;
 /*
 since we dont know what to do with the taxon
    @SerializedName("taxon")
    public ResponseProductList productswrap;*/
}
