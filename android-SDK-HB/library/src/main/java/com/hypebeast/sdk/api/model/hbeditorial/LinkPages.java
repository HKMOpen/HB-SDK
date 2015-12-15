package com.hypebeast.sdk.api.model.hbeditorial;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hesk on 22/4/15.
 */
public class LinkPages {
    @SerializedName("self")
    public href self;
    @SerializedName("first")
    public href first;
    @SerializedName("last")
    public href last;
    @SerializedName("next")
    public href next;
    @SerializedName("previous")
    public href previous;

    public LinkPages() {
    }
}
