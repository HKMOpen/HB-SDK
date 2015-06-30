package com.hypebeast.hbsdk.api.model.catelog;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by hesk on 2/9/15.
 */
public class products_facets {
    @SerializedName("size")
    private CTFilter size;
    @SerializedName("brand")
    private CTFilter brand;
    @SerializedName("category")
    private CTFilter category;
    @SerializedName("price")
    private CTPrice price;

    public products_facets() {

    }

    public CTFilter getSize() {
        return size;
    }

    public ArrayList<TermWrap> getBrand() {
        return brand.terms;
    }

    public CTFilter getCategory() {
        return category;
    }

    public ArrayList<TermWrap> getPrice() {
        return price.getRanges();
    }

    public CTPrice getPriceFilter() {
        return price;
    }
}
