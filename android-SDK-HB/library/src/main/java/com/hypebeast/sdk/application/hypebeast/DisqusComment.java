package com.hypebeast.sdk.application.hypebeast;

import android.content.Context;

import com.hypebeast.sdk.Constants;
import com.hypebeast.sdk.api.RealmUtil;
import com.hypebeast.sdk.api.realm.disqus.commentcount;
import com.hypebeast.sdk.api.realm.editorial.articlebookmark;

import java.sql.Timestamp;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;

/**
 * Created by hesk on 1/2/16.
 */
public class DisqusComment {
    private final RealmConfiguration conf;
    private Context context;
    private final Realm realm;

    public DisqusComment(Context _context) {
        context = _context;
        conf = RealmUtil.realmCfg(_context);
        realm = Realm.getInstance(conf);
    }

    private RealmQuery<commentcount> getQuery() {
        RealmQuery<commentcount> query = realm.where(commentcount.class);
        return query;
    }

    public int getExistingCount(long articleId) {
        RealmQuery<commentcount> q = getQuery().equalTo("article_id", articleId);
        if (q.findFirst() == null) {
            return 0;
        } else {
            return q.findFirst().getCount();
        }
    }

    public boolean shouldMakeRequest(long articleId) {
        RealmQuery<commentcount> q = getQuery().equalTo("article_id", articleId);
        if (q.findFirst() == null) {
            return true;
        } else {
            Date date = new Date();
            Timestamp timestamp_now = new Timestamp(date.getTime());
            commentcount count = q.findFirst();
            long timestamp_history = count.getRegister_date();
            return timestamp_now.getTime() - timestamp_history > Constants.ONE_MIN;
        }
    }

    public void updateCommentCount(final int count, final long articleId) {
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        long now = timestamp.getTime();
        RealmQuery<commentcount> q = getQuery().equalTo("article_id", articleId);
        realm.beginTransaction();
        if (q.findFirst() == null) {
            commentcount newobject = realm.createObject(commentcount.class);
            newobject.setArticle_id(articleId);
            newobject.setCount(count);
            newobject.setRegister_date(now);
        } else {
            commentcount revision = q.findFirst();
            revision.setCount(count);
            revision.setRegister_date(now);
        }
        realm.commitTransaction();
    }
}
