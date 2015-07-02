package com.hypebeast.hbsdk.api.model.commonterms;

import android.support.annotation.StringRes;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hesk on 2/7/15.
 */
public class ErrorWrap {
    @SerializedName("code")
    public int size;
    @SerializedName("message")
    public StringRes message;
}
