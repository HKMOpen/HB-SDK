package com.hypebeast.sdk.api.model.hbeditorial;

import com.google.gson.annotations.SerializedName;
import com.hypebeast.sdk.api.model.common.about;
import com.hypebeast.sdk.api.model.common.referralApp;

import java.util.ArrayList;

/**
 * Created by hesk on 1/9/15.
 */
public class HBmobileConfig {
    @SerializedName("base")
    public String base;


    @SerializedName("splash_screen")
    public splash splash_screen;


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
