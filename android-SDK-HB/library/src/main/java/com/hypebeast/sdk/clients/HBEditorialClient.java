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
import com.hypebeast.sdk.api.model.hbeditorial.Foundation;
import com.hypebeast.sdk.api.resources.hypebeast.feedhost;
import com.hypebeast.sdk.api.resources.hypebeast.overhead;
import com.hypebeast.sdk.application.hypebeast.ConfigurationSync;
import com.hypebeast.sdk.application.hypebeast.DisqusComment;
import com.hypebeast.sdk.application.hypebeast.syncBookmark;
import com.hypebeast.sdk.clients.basic.Client;
import com.hypebeast.sdk.clients.basic.apiInterceptor;

import java.io.File;
import java.io.IOException;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

import static com.hypebeast.sdk.Constants.*;

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
    private static final String API_VERSION = "2.2";
    /**
     * login adapter
     */
    private RestAdapter mLoginAdapter;
    private volatile static HBEditorialClient static_instance;
    private syncBookmark bookmark_instance;
    private DisqusComment dis_comment_instance;


    @Deprecated
    public static HBEditorialClient newInstance() {
        return new HBEditorialClient();
    }

    public static HBEditorialClient getInstance(Context c) {
        HBEditorialClient local = static_instance;
        if (local == null) {
            synchronized (HBEditorialClient.class) {
                local = static_instance;
                if (local == null) {
                    static_instance = local = new HBEditorialClient(c);
                }
            }
        }
        return local;
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

    private apiInterceptor interterceptor;

    @Override
    protected RequestInterceptor gatewayRequest() {
        if (interterceptor == null) {
            interterceptor = new apiInterceptor();
            interterceptor.setCacheMinutes(5);
        }
        return interterceptor;
    }

    public DisqusComment getDisqusComments() {
        return dis_comment_instance;
    }

    public syncBookmark getBookmarkInstance() {
        return bookmark_instance;
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
                .setRequestInterceptor(gatewayRequest())
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
                .setRequestInterceptor(gatewayRequest())
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
                .setRequestInterceptor(gatewayRequest())
                .setConverter(new GsonConverter(gsonsetup))
                .build();
        // buildCompletCacheRestAdapter(endpoint, context, RestAdapter.LogLevel.HEADERS);
        return mAdapter.create(feedhost.class);
    }


    public void getCSSLocal(UrlCache.readDone done_load) throws IOException {
        String root = Environment.getExternalStorageDirectory().toString() + File.separator;
        UrlCache.loadFromLocalFileText(APP_FOLDER_NAME, ConfigurationSync.local_css_file_name, done_load);
    }

    public String getCSSFast() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String data = sharedPreferences.getString(PREFERENCE_CSS_FILE_CONTENT, "");
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

    public String getSavedTemplate() {
        try {
            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            return sharedPreferences.getString(ConfigurationSync.TEMPLATE_FILE_HTML, "");
        } catch (NullPointerException e) {
            return "en";
        }
    }


}
