package com.hypebeast.sdk.api.model.hbeditorial;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hesk on 22/4/15.
 */
public class underscorelinks {
    @SerializedName("self")
    public Links self;
    @SerializedName("first")
    public Links first;
    @SerializedName("last")
    public Links last;
    @SerializedName("next")
    public Links next;
    @SerializedName("previous")
    public Links previous;

    public underscorelinks() {
    }
}
