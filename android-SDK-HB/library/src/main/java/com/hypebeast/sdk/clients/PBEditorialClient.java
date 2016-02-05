package com.hypebeast.sdk.clients;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.google.gson.GsonBuilder;
import com.hypebeast.sdk.Util.UrlCache;
import com.hypebeast.sdk.api.gson.GsonFactory;
import com.hypebeast.sdk.api.gson.MissingCharacterConversion;
import com.hypebeast.sdk.api.gson.RealmExclusion;
import com.hypebeast.sdk.api.gson.WordpressConversion;
import com.hypebeast.sdk.api.resources.pb.pbPost;
import com.hypebeast.sdk.application.hypebeast.ConfigurationSync;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.client.Header;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

import static com.hypebeast.sdk.Constants.APP_FOLDER_NAME;
import static com.hypebeast.sdk.Constants.PREFERENCE_CSS_FILE_CONTENT;

/**
 * Created by hesk on 3/7/15.
 */
public class PBEditorialClient extends Client {
    /**
     * Base URL for all PB endpoints
     */
    private static final String BASE_URL_PB = "http://popbee.com/";
    /**
     * User agent
     */
    private static final String USER_AGENT = "PB/1.0 Android" + Build.VERSION.SDK_INT;
    /**
     * Date format
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    //http://www.datameer.com/documentation/display/DAS20/Date+and+Time+Parse+Patterns

    public static final String ISO_FORMAT1 = "yyyy-MM-dd'T'HH:mm:ss.SSS zzz";
    public static final String ISO_FORMAT2 = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final String ISO_FORMAT3 = "yyyy-MM-dd HH:mm:ss z";
    public static final String ISO_FORMAT4 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String ISO_FORMAT5 = "yyyy-MM-dd'T'HH:mm:ssZ";


    private static PBEditorialClient static_instance;


    public static PBEditorialClient newInstance() {
        return new PBEditorialClient();
    }

    @Deprecated
    public static PBEditorialClient getInstance() {
        if (static_instance == null) {
            static_instance = new PBEditorialClient();
            return static_instance;
        } else {
            return static_instance;
        }
    }

    public static PBEditorialClient getInstance(Application context) {
        if (static_instance == null) {
            static_instance = new PBEditorialClient(context);
            return static_instance;
        } else {
            return static_instance;
        }
    }

    @Override
    protected void registerAdapter() {
        mAdapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL_PB)
                .setLogLevel(RestAdapter.LogLevel.HEADERS)
                .setErrorHandler(handlerError)
                .setRequestInterceptor(getIn())
                .setConverter(new GsonConverter(gsonsetup))
                .build();
    }

    @Override
    protected String get_USER_AGENT() {
        return USER_AGENT;
    }

    @Override
    protected void jsonCreate() {
        gsonsetup = new GsonBuilder()
                .setDateFormat(DATE_FORMAT)
                .setExclusionStrategies(new RealmExclusion())
                .registerTypeAdapterFactory(new GsonFactory.NullStringToEmptyAdapterFactory())
                        //  .registerTypeAdapter(String.class, new MissingCharacterConversion())
                .registerTypeAdapter(String.class, new WordpressConversion())
                .create();
    }

    public PBEditorialClient() {
        super();
    }
    public PBEditorialClient(Application context) {
        super(context);
    }

    public pbPost createPostsFeed() {
        return mAdapter.create(pbPost.class);
    }

    public static int totalPages(Response mResp) {
        List<Header> list = mResp.getHeaders();
        Iterator<Header> f = list.iterator();
        while (f.hasNext()) {
            Header h = f.next();
            if (h.getName().equalsIgnoreCase("X-WP-TotalPages")) {
                return Integer.parseInt(h.getValue());
            }
        }
        return -1;
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

}
