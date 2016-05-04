package com.hypebeast.sdk.api.model.wprest;

import com.google.gson.annotations.SerializedName;
import com.hypebeast.sdk.api.gson.Excludoo;

/**
 * Created by hesk on 3/7/15.
 */
public class featuredImage extends wpPostBase {
    @Excludoo
    @SerializedName("popbee_specific")
    public Object nothing;
    @Excludoo
    @SerializedName("terms")
    public Object terms;
}
