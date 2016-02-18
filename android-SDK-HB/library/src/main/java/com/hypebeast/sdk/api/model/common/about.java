package com.hypebeast.sdk.api.model.common;

import com.google.gson.annotations.SerializedName;
import com.hypebeast.sdk.api.model.popbees.popbeeconfig;

/**
 * Created by hesk on 18/2/16.
 */
public class about {


    @SerializedName("display")
    public String display_bar;

    /**
     * it could be email embeded
     */
    @SerializedName("href")
    public String link;


}
