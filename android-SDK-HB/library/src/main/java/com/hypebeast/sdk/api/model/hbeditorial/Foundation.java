package com.hypebeast.sdk.api.model.hbeditorial;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hesk on 9/6/15.
 */
public class Foundation {
    @SerializedName("en")
    public HBmobileConfig english;
    @SerializedName("cnt")
    public HBmobileConfig chinese_traditional;
    @SerializedName("cns")
    public HBmobileConfig chinese_simplified;
    @SerializedName("ja")
    public HBmobileConfig japanese;
}
