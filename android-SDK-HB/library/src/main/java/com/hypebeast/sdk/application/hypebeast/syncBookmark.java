package com.hypebeast.sdk.application.hypebeast;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hypebeast.sdk.api.RealmUtil;
import com.hypebeast.sdk.api.exception.ApiException;
import com.hypebeast.sdk.api.model.hbeditorial.ResponsePostW;
import com.hypebeast.sdk.api.model.hbeditorial.ResponseSingle;
import com.hypebeast.sdk.api.model.symfony.Product;
import com.hypebeast.sdk.api.realm.editorial.articlebookmark;
import com.hypebeast.sdk.clients.HBEditorialClient;
import com.hypebeast.sdk.clients.HBStoreApiClient;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by hesk on 18/12/15.
 */
public class syncBookmark {
    private Context context;
    public static final int
            STATUS_IDEAL = 1,
            STATUS_SEARCH_ARTICLE = 2,
            STATUS_NOT_FOUND_ITEM = 3;

    @IntDef({STATUS_IDEAL, STATUS_SEARCH_ARTICLE, STATUS_NOT_FOUND_ITEM})
    public @interface bookmarkStatus {

    }

    private int worker_status, current_saved_articles;
    private final RealmConfiguration conf;

    public syncBookmark(Context _context) {
        context = _context;
        worker_status = STATUS_IDEAL;
        conf = RealmUtil.realmCfg(_context);
    }

    public void ClearAllBookmarks() {
        Realm realm = Realm.getInstance(conf);
        RealmResults<articlebookmark> copies = realm.where(articlebookmark.class).findAll();
        realm.beginTransaction();
        copies.clear();
        realm.commitTransaction();
    }

    public boolean checkSaved(Realm realm, long article_id) {
        RealmQuery<articlebookmark> query = realm.where(articlebookmark.class);
        query.equalTo("article_id", article_id);
        // Execute the query:
        RealmResults<articlebookmark> result = query.findAll();
        return result.size() > 0;
    }

    public articlebookmark getArticleFromdb(long article_id) {
        Realm realm = Realm.getInstance(conf);
        RealmQuery<articlebookmark> query = realm.where(articlebookmark.class);
        query.equalTo("article_id", article_id);
        // Execute the query:
        //RealmResults<articlebookmark> result = query.findAll();
        return query.findFirst();
    }

    public boolean checkExisting(long article_id) {
        Realm realm = Realm.getInstance(conf);
        return checkSaved(realm, article_id);
    }

    private articlebookmark createAndConvertFromPost(final Realm r, final ResponseSingle page) {
        articlebookmark read = r.createObject(articlebookmark.class);
        read.set_links(page.post._links.getSelf());
        read.setArticle_id(page.post.article_id);
        read.setAuthor(page.post._embedded.author);
        read.setVideo_embed_code(page.post._embedded.video_embed_code);
        read.setDisqus_identifier(page.post._embedded.disqus_identifier);
        read.setSingle_article_content(page.post.single_article_content);
        read.setSingle_article_date(page.post.single_article_date);
        read.setSingle_article_slug(page.post.single_article_slug);
        read.setSingle_article_title(page.post.single_article_title);
        return read;
    }


    public void removeItem(long product_id) {
        Realm realm = Realm.getInstance(conf);
        RealmResults<articlebookmark> copies = realm.where(articlebookmark.class).equalTo("article_id", product_id).findAll();
        realm.beginTransaction();
        copies.clear();
        realm.commitTransaction();
    }


    public List<articlebookmark> getAllBookmarks() {
        Realm realm = Realm.getInstance(conf);
        RealmResults<articlebookmark> copies = realm.where(articlebookmark.class).findAll();
        current_saved_articles = copies.size();
        return copies;
    }

    public int getSavedArticles() {
        return current_saved_articles;
    }

    public interface addBookMark {
        void success_add_bookmark(long id);

        void failure_add(String error);
    }

    public interface returnArticle {
        void article_from_net(ResponseSingle article);

        void article_from_bm(articlebookmark article);

        void failure(String message);
    }

    private class request_call_back implements Callback<ResponseSingle> {
        private final returnArticle ccb;

        public request_call_back(returnArticle cb) {
            ccb = cb;
        }

        @Override
        public void failure(RetrofitError error) {
            ccb.failure(error.getMessage());
        }


        @Override
        public void success(ResponseSingle responseSingle, Response response) {
            ccb.article_from_net(responseSingle);
        }
    }

    public void hitBy(final long article_id, final @Nullable String fullpath, final @NonNull returnArticle cb) {
        try {
            HBEditorialClient client = HBEditorialClient.getInstance(context);
            if (article_id > 0) {
                if (checkExisting(article_id)) {
                    cb.article_from_bm(getArticleFromdb(article_id));
                } else {
                    client.createFeedInterface().the_post(article_id, new request_call_back(cb));
                }
            } else {
                if (fullpath == null) cb.failure("the full path endpoint is missing.");
                client.createAPIUniversal(fullpath).getSingleArticle(new request_call_back(cb));
            }
        } catch (ApiException e) {
            cb.failure(e.getMessage());
        }
    }

}
