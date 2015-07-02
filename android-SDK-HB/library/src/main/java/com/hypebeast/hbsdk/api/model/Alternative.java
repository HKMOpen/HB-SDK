package com.hypebeast.hbsdk.api.model;

import com.google.gson.annotations.SerializedName;
import com.hypebeast.hbsdk.api.model.commonterms.ErrorWrap;

/**
 * Created by hesk on 30/6/15.
 */
public abstract class Alternative {
    @SerializedName("error")
    public ErrorWrap error;
}
