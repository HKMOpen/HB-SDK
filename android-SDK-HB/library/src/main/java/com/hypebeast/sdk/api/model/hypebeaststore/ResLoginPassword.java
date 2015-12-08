package com.hypebeast.sdk.api.model.hypebeaststore;

import com.google.gson.annotations.SerializedName;
import com.hypebeast.sdk.api.model.symfony.taxonomy;

import java.util.List;

/**
 * Created by hesk on 8/12/15.
 */
public class ResLoginPassword {


    /**
     * message for the irrelvant item
     */
    @SerializedName("message")
    public String message;

    /**
     * session_id
     */
    @SerializedName("session_id")
    public String session_id;



}
