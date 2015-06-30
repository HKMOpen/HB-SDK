package com.hypebeast.hbsdk.api.model.catelog;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by hesk on 2/3/15.
 */
public class CTFilter {
    @SerializedName("terms")
    public ArrayList<TermWrap> terms;

}
