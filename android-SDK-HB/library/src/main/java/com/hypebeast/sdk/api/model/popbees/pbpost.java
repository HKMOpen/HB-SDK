package com.hypebeast.sdk.api.model.popbees;

import com.google.gson.annotations.SerializedName;
import com.hypebeast.sdk.api.model.wprest.post;
import com.hypebeast.sdk.api.model.wprest.terms;
import com.hypebeast.sdk.api.model.wprest.wpPostBase;

/**
 * Created by hesk on 6/7/15.
 */
public class pbpost extends wpPostBase {
    @SerializedName("popbee_specific")
    public popbeeconfig popbee_specific;
    @SerializedName("terms")
    public terms terms;

}
