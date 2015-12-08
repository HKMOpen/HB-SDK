package com.hypebeast.sdk.api.model.hypebeaststore;

import com.google.gson.annotations.SerializedName;
import com.hypebeast.sdk.api.model.Alternative;
import com.hypebeast.sdk.api.model.wprest.user;

/**
 * Created by hesk on 8/12/15.
 */
public class ResLoginCheck extends Alternative {
    /**
     * when the login item returns an object to represent properly for the login user
     */
    @SerializedName("user")
    public user itemuser;
}
