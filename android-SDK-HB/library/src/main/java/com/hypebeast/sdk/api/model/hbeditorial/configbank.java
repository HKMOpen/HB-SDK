package com.hypebeast.sdk.api.model.hbeditorial;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by hesk on 1/9/15.
 */
public class configbank {
    @SerializedName("base")
    public String base;
    @SerializedName("menu")
    public ArrayList<Menuitem> menu;
    //starting from v2.1
    @SerializedName("categories")
    public ArrayList<Menuitem> cateitems;
    @SerializedName("featurebanner")
    public ArrayList<Slide> featurebanner;
    @SerializedName("main_feature_banner")
    public Slide table_only_feature_banner;
    @SerializedName("splash_screen")
    public splash splash_screen;
    @SerializedName("navbar")
    public ArrayList<Menuitem> nav_bar;
}
