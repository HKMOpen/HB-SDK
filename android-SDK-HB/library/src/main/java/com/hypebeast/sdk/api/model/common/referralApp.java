package com.hypebeast.sdk.api.model.common;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hesk on 18/2/16.
 */
public class referralApp {
    @SerializedName("name")
    public String app_name;
    @SerializedName("icon")
    public String icon_image;
    @SerializedName("description")
    public String desc;
    @SerializedName("android_app_id")
    public String package_name;
}
