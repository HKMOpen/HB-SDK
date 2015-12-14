package com.hypebeast.sdk.api.model.hbeditorial;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by hesk on 13/8/15.
 */
public class PostsObject {

    public PostsObject() {
    }


    @SerializedName("title")
    public String single_article_title;
    @SerializedName("content")
    public String single_article_content;
    @SerializedName("slug")
    public String single_article_slug;
    @SerializedName("date")
    public String single_article_date;




    @SerializedName("page")
    public int page;
    @SerializedName("limit")
    public int limit;
    @SerializedName("pages")
    public int pages;
    @SerializedName("total")
    public int total;
    @SerializedName("_links")
    public underscorelinks _links;
    @SerializedName("_embedded")
    public underscoreembedded _embedded;


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
