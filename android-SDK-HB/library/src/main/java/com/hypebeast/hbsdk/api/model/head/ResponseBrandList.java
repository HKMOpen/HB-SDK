package com.hypebeast.hbsdk.api.model.head;

import com.google.gson.annotations.SerializedName;
import com.hypebeast.hbsdk.api.model.Alternative;
import com.hypebeast.hbsdk.api.model.hypebeast.taxonomy;

import java.util.List;

/**
 * Created by hesk on 30/6/15.
 */
public class ResponseBrandList extends Alternative {
    /**
     * brands
     */
    @SerializedName("brands")
    public List<taxonomy> brands;

    /**
     * allBrands
     */
    @SerializedName("allBrands")
    public List<taxonomy> all;

}
