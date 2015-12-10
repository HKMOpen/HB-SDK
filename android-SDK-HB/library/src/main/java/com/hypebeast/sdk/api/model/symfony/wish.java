package com.hypebeast.sdk.api.model.symfony;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hesk on 10/12/15.
 */
public class wish {
    @SerializedName("id")
    public long wish_id;
    @SerializedName("product")
    public Product wish_item;

}
