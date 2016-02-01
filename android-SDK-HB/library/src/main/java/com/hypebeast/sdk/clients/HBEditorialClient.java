package com.hypebeast.sdk.clients;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.google.gson.GsonBuilder;
import com.hypebeast.sdk.Constants;
import com.hypebeast.sdk.Util.Connectivity;
import com.hypebeast.sdk.Util.UrlCache;
import com.hypebeast.sdk.api.gson.GsonFactory;
import com.hypebeast.sdk.api.gson.MissingCharacterConversion;
import com.hypebeast.sdk.api.gson.RealmExclusion;
import com.hypebeast.sdk.api.gson.WordpressConversion;
import com.hypebeast.sdk.api.model.hbeditorial.Foundation;
import com.hypebeast.sdk.api.model.hbeditorial.ResponsePostW;
import com.hypebeast.sdk.api.resources.hypebeast.feedhost;
import com.hypebeast.sdk.api.resources.hypebeast.overhead;
import com.hypebeast.sdk.application.hypebeast.ConfigurationSync;
import com.hypebeast.sdk.application.hypebeast.DisqusComment;
import com.hypebeast.sdk.application.hypebeast.syncBookmark;

import java.io.File;
import java.io.IOException;

import io.realm.RealmConfiguration;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by hesk on 2/7/15.
 */
public class HBEditorialClient extends Client {

    /**
     * Base URL for all Disqus endpoints
     */
    public static final String BASE_EN = "http://hypebeast.com/";
    public static final String BASE_JP = "http://jp.hypebeast.com/";
    public static final String BASE_CN = "http://cn.hypebeast.com/";
    public static final String BASE_LOGIN = "https://disqus.com/api";
    private String endpoint;
    /**
     * User agent
     */
    private static final String USER_AGENT = "HypebeastStoreApp/1.0 Android" + Build.VERSION.SDK_INT;
    /**
     * login adapter
     */
    private RestAdapter mLoginAdapter;

    private static HBEditorialClient static_instance;
    private syncBookmark bookmark_instance;
    private DisqusComment dis_comment_instance;

    @Deprecated
    public static HBEditorialClient newInstance() {
        return new HBEditorialClient();
    }

    public static HBEditorialClient getInstance(Context c) {
        if (static_instance == null) {
            static_instance = new HBEditorialClient(c);
            return static_instance;
        } else {
            return static_instance;
        }
    }


    public static HBEditorialClient newInstance(Context c) {
        return new HBEditorialClient(c);
    }

    @Deprecated
    public HBEditorialClient() {
        super();
    }

    public HBEditorialClient(Context c) {
        super(c);
        bookmark_instance = new syncBookmark(c);
        dis_comment_instance = new DisqusComment(c);
    }


    @Override
    protected RequestInterceptor getIn() {
        return new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("User-Agent", get_USER_AGENT());
                request.addHeader("Accept", "application/json");
                request.addHeader("X-Api-Version", "2.0");
                // request.addHeader("Cookie", getCookieClient().getRaw());
                try {
                    if (Connectivity.isConnected(context)) {
                        request.addHeader("Cache-Control", "public, max-age=" + timeByMins(1));
                    } else {
                        request.addHeader("Cache-Control", "public, only-if-cached, max-stale=" + timeByWeeks(1));
                    }
                } catch (Exception e) {

                }
            }
        };
    }

    public DisqusComment getDisqusComments() {
        return dis_comment_instance;
    }

    public syncBookmark getBookmarkInstance() {
        return bookmark_instance;
    }

    @Override
    protected String get_USER_AGENT() {
        return USER_AGENT;
    }

    @Override
    protected void jsonCreate() {
        gsonsetup = new GsonBuilder()
                .setDateFormat(Constants.DATE_FORMAT)
   /*             .registerTypeAdapter(Usage.class, new ApplicationsUsageDeserializer())
                .registerTypeAdapterFactory(new BlacklistsEntryTypeAdapterFactory())
                .registerTypeAdapterFactory(new PostTypeAdapterFactory())
                .registerTypeAdapterFactory(new ThreadTypeAdapterFactory())*/
                .registerTypeAdapterFactory(new GsonFactory.NullStringToEmptyAdapterFactory())
                .registerTypeAdapter(String.class, new MissingCharacterConversion())
                .setExclusionStrategies(new RealmExclusion())

                        //.registerTypeAdapter(String.class, new WordpressConversion())
                .create();
    }

    public Foundation fromsavedConfiguration(String data) {
        return gsonsetup.fromJson(data, Foundation.class);
    }

    public String fromJsonToString(Foundation mfound) {
        return gsonsetup.toJson(mfound);
    }


    @Override
    protected void registerAdapter() {
        if (endpoint == null) {
            this.endpoint = BASE_EN;
        }
        mAdapter = new RestAdapter.Builder()
                .setEndpoint(endpoint)
                .setLogLevel(RestAdapter.LogLevel.HEADERS)
                .setErrorHandler(handlerError)
                .setRequestInterceptor(getIn())
                .setConverter(new GsonConverter(gsonsetup))
                .build();

        // buildCompletCacheRestAdapter(endpoint, context, RestAdapter.LogLevel.HEADERS);
    }


    public HBEditorialClient setLanguageBase(String from_hb_editorial_base_url) {
        this.endpoint = from_hb_editorial_base_url;
        registerAdapter();
        return this;
    }

    public HBEditorialClient build() {
        jsonCreate();
        registerAdapter();
        return this;
    }

    /**
     * the overhead creation
     *
     * @param fullPath the path in string
     * @return in the overhead in interface to be triggered.
     */
    public overhead createOverHead(final String fullPath) {
        final RestAdapter mAdapter = new RestAdapter.Builder()
                .setEndpoint(fullPath)
                .setLogLevel(RestAdapter.LogLevel.HEADERS)
                .setErrorHandler(handlerError)
                .setRequestInterceptor(getIn())
                .setConverter(new GsonConverter(gsonsetup))
                .build();

        return mAdapter.create(overhead.class);
    }

    public feedhost createFeedInterface() {
        return mAdapter.create(feedhost.class);
    }

    /**
     * please request with 'full_path_list' as the universal request will work eventually
     *
     * @param full_path the full head part
     * @return the feedhost object
     */
    public feedhost createAPIUniversal(final String full_path) {
        final RestAdapter mAdapter = new RestAdapter.Builder()
                .setEndpoint(full_path)
                .setLogLevel(RestAdapter.LogLevel.HEADERS)
                .setErrorHandler(handlerError)
                .setRequestInterceptor(getIn())
                .setConverter(new GsonConverter(gsonsetup))
                .build();
        // buildCompletCacheRestAdapter(endpoint, context, RestAdapter.LogLevel.HEADERS);
        return mAdapter.create(feedhost.class);
    }


    public void getCSSLocal(UrlCache.readDone done_load) throws IOException {
        String root = Environment.getExternalStorageDirectory().toString() + File.separator;
        UrlCache.loadFromLocalFileText(ConfigurationSync.folder_name_local, ConfigurationSync.local_css_file_name, done_load);
    }

    public String getCSSFast() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String data = sharedPreferences.getString(ConfigurationSync.PREFERENCE_CSS, "");
        return data;
    }


    private static final String LANG = "lang_val";

    public void saveLanguage(String choice) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(LANG, choice).apply();
    }

    public String getLanguagePref() {
        try {
            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            return sharedPreferences.getString(LANG, "en");
        } catch (NullPointerException e) {
            return "en";
        }
    }

}
