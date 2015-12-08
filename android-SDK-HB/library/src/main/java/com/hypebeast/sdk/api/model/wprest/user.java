package com.hypebeast.sdk.api.model.wprest;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hesk on 8/12/15.
 */
public class user {
    @SerializedName("id")
    public long id;

    @SerializedName("username")
    public String username;

    @SerializedName("full_name")
    public String fullname;

    @SerializedName("email")
    public String email;


}
