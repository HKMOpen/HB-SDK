package com.hypebeast.sdk.api.model.wprest;

import com.google.gson.annotations.SerializedName;

import java.sql.Date;

/**
 * Created by hesk on 4/5/16.
 */
public abstract class wpPostBase {
    @SerializedName("ID")
    public long id;
    @SerializedName("menu_order")
    public int menu_order;
    @SerializedName("status")
    public status mstatus;
    @SerializedName("type")
    public posttype mposttype;
    @SerializedName("author")
    public author author;
    @SerializedName("content")
    public String htmlcontent;
    @SerializedName("title")
    public String title;
    @SerializedName("link")
    public String finalenpoint;
    @SerializedName("slug")
    public String slug;
    @SerializedName("guid")
    public String guid;
    @SerializedName("excerpt")
    public String excerpt;
    @SerializedName("sticky")
    public boolean isSticky;
    @SerializedName("date")
    public Date mdate;
    @SerializedName("modified")
    public Date mmodified;
    @SerializedName("date_tz")
    public String date_tz;
    @SerializedName("date_gmt")
    public Date date_gmt;
    @SerializedName("attachment_meta")
    public attachmentImage image;
    @SerializedName("source")
    public String imageSrcUrl;
    @SerializedName("is_image")
    public boolean isImage;
}
