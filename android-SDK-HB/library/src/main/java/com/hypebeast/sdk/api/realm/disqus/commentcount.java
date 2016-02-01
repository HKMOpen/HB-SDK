package com.hypebeast.sdk.api.realm.disqus;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by hesk on 1/2/16.
 */
public class commentcount extends RealmObject {
    @PrimaryKey
    private long article_id;
    private int count;
    private long register_date;

    public long getArticle_id() {
        return article_id;
    }

    public void setArticle_id(long article_id) {
        this.article_id = article_id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getRegister_date() {
        return register_date;
    }

    public void setRegister_date(long register_date) {
        this.register_date = register_date;
    }
}
