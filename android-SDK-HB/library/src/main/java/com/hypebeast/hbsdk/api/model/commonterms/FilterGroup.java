package com.hypebeast.hbsdk.api.model.commonterms;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hesk on 2/9/15.
 */
public class FilterGroup {
    @SerializedName("size")
    public filterpart size;
    @SerializedName("brand")
    public filterpart brand;
    @SerializedName("category")
    public filterpart category;
    @SerializedName("color")
    public filterpart color;
    @SerializedName("price")
    public filterpart priceRange;
}
