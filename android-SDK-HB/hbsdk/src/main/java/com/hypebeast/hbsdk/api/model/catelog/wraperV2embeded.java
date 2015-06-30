package com.hypebeast.hbsdk.api.model.catelog;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by hesk on 2/18/15.
 */
public class wraperV2embeded {
    @SerializedName("products")
    private ArrayList<Product> products;

    public wraperV2embeded() {

    }

    public ArrayList<Product> getlist() {
        return products;
    }
}
