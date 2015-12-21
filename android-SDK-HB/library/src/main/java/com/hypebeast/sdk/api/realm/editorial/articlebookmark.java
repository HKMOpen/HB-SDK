package com.hypebeast.sdk.api.realm.editorial;

import com.google.gson.annotations.SerializedName;
import com.hypebeast.sdk.api.model.hbeditorial.EmbedPayload;
import com.hypebeast.sdk.api.model.hbeditorial.LinkSingle;
import com.hypebeast.sdk.api.realm.QLRealmString;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by hesk on 18/12/15.
 */
public class articlebookmark extends RealmObject {
    @PrimaryKey
    private long article_id;


    private String disqus_identifier;
    private String video_embed_code;
    private String author;

    public RealmList<QLRealmString> getImages() {
        return images;
    }

    public void setImages(RealmList<QLRealmString> images) {
        this.images = images;
    }

    private RealmList<QLRealmString> images;
    private String single_article_title;
    private String single_article_content;
    private String single_article_slug;
    private String single_article_date;
    private String _links;

    public String get_links() {
        return _links;
    }

    public void set_links(String _links) {
        this._links = _links;
    }

    public long getArticle_id() {
        return article_id;
    }

    public void setArticle_id(long article_id) {
        this.article_id = article_id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDisqus_identifier() {
        return disqus_identifier;
    }

    public void setDisqus_identifier(String disqus_identifier) {
        this.disqus_identifier = disqus_identifier;
    }

    public String getSingle_article_content() {
        return single_article_content;
    }

    public void setSingle_article_content(String single_article_content) {
        this.single_article_content = single_article_content;
    }

    public String getSingle_article_date() {
        return single_article_date;
    }

    public void setSingle_article_date(String single_article_date) {
        this.single_article_date = single_article_date;
    }

    public String getSingle_article_slug() {
        return single_article_slug;
    }

    public void setSingle_article_slug(String single_article_slug) {
        this.single_article_slug = single_article_slug;
    }

    public String getSingle_article_title() {
        return single_article_title;
    }

    public void setSingle_article_title(String single_article_title) {
        this.single_article_title = single_article_title;
    }

    public String getVideo_embed_code() {
        return video_embed_code;
    }

    public void setVideo_embed_code(String video_embed_code) {
        this.video_embed_code = video_embed_code;
    }


}
