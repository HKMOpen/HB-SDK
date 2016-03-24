package com.hypebeast.sdk.api.model.hbeditorial;

import com.google.gson.annotations.SerializedName;
import com.hypebeast.sdk.api.model.common.about;
import com.hypebeast.sdk.api.model.common.referralApp;

import java.util.ArrayList;

/**
 * Created by hesk on 1/9/15.
 */
public class HBmobileConfig {


    @SerializedName("splash_screen")
    public splash splash_screen;

    @SerializedName("base")
    public String base;

    @SerializedName("ad_per_post")
    public int ad_per_post;

    @SerializedName("ad_minimum_reload_time")
    public int ad_min_reload_time;

    /**
     * deprecated since v2.1
     */
    @Deprecated
    @SerializedName("menu")
    public ArrayList<Menuitem> menu;


    /**
     * added since v2.1
     */
    @SerializedName("categories")
    public ArrayList<Menuitem> cateitems;


    @SerializedName("navbar")
    public ArrayList<Menuitem> nav_bar;


    @SerializedName("other_apps")
    public ArrayList<referralApp> applist;


    @SerializedName("about")
    public ArrayList<about> aboutlist;


    @SerializedName("featurebanner")
    public ArrayList<Slide> featurebanner;


    @SerializedName("main_feature_banner")
    public Slide table_only_feature_banner;


    /**
     * added since v2.2
     */
    @SerializedName("post_html_template")
    public String html_base;


}
