package com.hypebeast.sdk.api.model.hbeditorial;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by hesk on 13/8/15.
 */
public class PostsObject {


    @SerializedName("page")
    public int page;
    @SerializedName("limit")
    public int limit;
    @SerializedName("pages")
    public int pages;
    @SerializedName("total")
    public int total;
    @SerializedName("_links")
    public LinkPages _links;
    @SerializedName("_embedded")
    public EmbedPayload _embedded;
    @SerializedName("comment_status")
    public String comment_status;

    public PostsObject() {
    }

    public ArrayList<ArticleData> getArticles() {
        return _embedded.getItems();
    }

    public int getPage() {
        return page;
    }

    public int getLimit() {
        return limit;
    }

    public int getPages() {
        return pages;
    }

    public int getTotal() {
        return total;
    }
}
