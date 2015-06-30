package com.hypebeast.hbsdk.api.model.catelog;


import com.google.gson.annotations.SerializedName;

/**
 * Created by hesk on 2/6/15.
 */
public class outputV2 {
    @SerializedName("page")
    private int page;
    @SerializedName("limit")
    private int limit;
    @SerializedName("pages")
    private int pages;
    @SerializedName("total")
    private int total;
    @SerializedName("_embedded")
    private wraperV2embeded _embedded;
    @SerializedName("facets")
    private products_facets facets;


    public int totalpages() {
        return pages;
    }

    public int current_page() {
        return page;
    }

    public wraperV2embeded getProducts() {
        return _embedded;
    }

    public products_facets getfacets() {
        return facets;
    }
}
