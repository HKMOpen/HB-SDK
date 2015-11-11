package com.hypebeast.sdk.api.model.wprest;

import android.graphics.Color;
import android.net.Uri;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hesk on 15/7/15.
 */
public class sliderItem {
    @SerializedName("post_id")
    public long post_pid;

    @SerializedName("text")
    public String description;

    @SerializedName("image")
    public String image;

    @SerializedName("url")
    public String url;

    @SerializedName("text_color")
    private String textcolorconfiguration;

    public sliderItem() {
    }


    public int getColor() {
        if (textcolorconfiguration == null)
            return 0;
        else
            return Color.parseColor(textcolorconfiguration);
    }

    public Uri getUri() {
        return Uri.parse(url);
    }
}
