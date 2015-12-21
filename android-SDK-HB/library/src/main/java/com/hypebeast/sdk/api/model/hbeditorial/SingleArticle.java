package com.hypebeast.sdk.api.model.hbeditorial;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hesk on 15/12/15.
 */
public class SingleArticle {

    @SerializedName("id")
    public long article_id;

    @SerializedName("title")
    public String single_article_title;
    @SerializedName("content")
    public String single_article_content;
    @SerializedName("slug")
    public String single_article_slug;
    @SerializedName("date")
    public String single_article_date;
    @SerializedName("_links")
    public LinkSingle _links;
    @SerializedName("_embedded")
    public EmbedPayload _embedded;

    public SingleArticle() {
    }

}
