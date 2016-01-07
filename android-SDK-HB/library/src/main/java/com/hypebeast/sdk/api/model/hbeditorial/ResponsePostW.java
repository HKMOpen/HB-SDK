package com.hypebeast.sdk.api.model.hbeditorial;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by hesk on 13/8/15.
 */
public class ResponsePostW {
    @SerializedName("posts")
    public PostsObject postList;
    @SerializedName("title")
    public String title;

    public int page_limit = 0;
    private ArrayList<ArticleData> list;

    public ResponsePostW() {
        if (postList != null) {
            page_limit = postList.limit;
            list = postList._embedded.getItems();
        }
    }

    public ArrayList<ArticleData> getListFeed() {
        return list;
    }


}
