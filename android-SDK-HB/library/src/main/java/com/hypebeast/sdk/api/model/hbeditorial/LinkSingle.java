package com.hypebeast.sdk.api.model.hbeditorial;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hesk on 21/4/15.
 */
public class LinkSingle {
    @SerializedName("self")
    public href self;
    @SerializedName("thumbnail")
    public href thumbnail;
    @SerializedName("shorthen_url")
    public href short_url;
    @SerializedName("categories")
    public List<href> categories;
    @SerializedName("tags")
    public List<href> tags;

    public LinkSingle() {
    }

    public String getSelf() {
        return self.getHref();
    }

    public String getThumbnail() {
        return thumbnail.getHref();
    }

    public List<href> getCategories() {
        return categories;
    }

    public List<href> getTags() {
        return tags;
    }
}
