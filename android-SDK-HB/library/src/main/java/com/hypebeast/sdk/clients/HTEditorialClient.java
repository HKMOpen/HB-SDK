package com.hypebeast.sdk.clients;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.google.gson.GsonBuilder;
import com.hypebeast.sdk.Constants;
import com.hypebeast.sdk.Util.UrlCache;
import com.hypebeast.sdk.api.exception.ApiException;
import com.hypebeast.sdk.api.gson.GsonFactory;
import com.hypebeast.sdk.api.gson.RealmExclusion;
import com.hypebeast.sdk.api.gson.WordpressConversion;
import com.hypebeast.sdk.api.model.popbees.PBmobileConfig;
import com.hypebeast.sdk.api.resources.ht.hTrak;
import com.hypebeast.sdk.application.hypebeast.ConfigurationSync;
import com.hypebeast.sdk.clients.basic.Client;
import com.hypebeast.sdk.clients.basic.apiInterceptor;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

import static com.hypebeast.sdk.Constants.APP_FOLDER_NAME;
import static com.hypebeast.sdk.Constants.PREFERENCE_CSS_FILE_CONTENT;

/**
 * Created by hesk on 21/8/15.
 */
public class HTEditorialClient extends Client {
    /**
     * Base URL for all PB endpoints
     */
    private static final String BASE_URL_PB = "http://hypetrak.com/";
    private static final String API_VERSION = "1.0";
    /**
     * Date format
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    //http://www.datameer.com/documentation/display/DAS20/Date+and+Time+Parse+Patterns
    public static final String REFERENCE_MOBILE_CONFIG = "mConfig";
    public static final String REFERENCE_MOBILE_CONFIG_TIME = "mConfigTime";
    public static final String ISO_FORMAT1 = "yyyy-MM-dd'T'HH:mm:ss.SSS zzz";
    public static final String ISO_FORMAT2 = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final String ISO_FORMAT3 = "yyyy-MM-dd HH:mm:ss z";
    public static final String ISO_FORMAT4 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String ISO_FORMAT5 = "yyyy-MM-dd'T'HH:mm:ssZ";
    private hTrak interfaceHTrak;

    private static HTEditorialClient static_instance;


    public static HTEditorialClient newInstance() {
        return new HTEditorialClient();
    }

    @Deprecated
    public static HTEditorialClient getInstance() {
        if (static_instance == null) {
            static_instance = new HTEditorialClient();
            return static_instance;
        } else {
            return static_instance;
        }
    }

    public static HTEditorialClient getInstance(Application context) {
        if (static_instance == null) {
            static_instance = new HTEditorialClient(context);
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
                .setRequestInterceptor(gatewayRequest())
                .setConverter(new GsonConverter(gsonsetup))
                .build();
    }


    @Override
    protected void jsonCreate() {
        gsonsetup = new GsonBuilder()
                .setDateFormat(DATE_FORMAT)
                .setExclusionStrategies(new RealmExclusion())
                .registerTypeAdapterFactory(new GsonFactory.NullStringToEmptyAdapterFactory())
                .registerTypeAdapter(String.class, new WordpressConversion())
                .create();
    }

    @Override
    protected void createInterfaces() {
        interfaceHTrak = mAdapter.create(hTrak.class);
    }

    public HTEditorialClient() {
        super();
    }

    private apiInterceptor interterceptor;

    @Override
    protected RequestInterceptor gatewayRequest() {
        if (interterceptor == null) {
            interterceptor = new apiInterceptor();
            interterceptor.setCacheMinutes(5);
            interterceptor.setAPIVersion(API_VERSION);
        }
        return interterceptor;
    }

    public HTEditorialClient(Application context) {
        super(context);
    }

    public hTrak createPostsFeed() {
        return interfaceHTrak;
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

    /**
     * this is the splash screen data sync task for prestarting the app
     *
     * @param context  the application context
     * @param callback the call back event
     */
    public void retentData(final Context context, final Runnable callback) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String data = sharedPreferences.getString(REFERENCE_MOBILE_CONFIG, "none");
        final String time = sharedPreferences.getString(REFERENCE_MOBILE_CONFIG_TIME, "none");
        final Runnable n = new Runnable() {
            @Override
            public void run() {

                try {
                    interfaceHTrak.mobile_config(new Callback<PBmobileConfig>() {
                        @Override
                        public void success(PBmobileConfig mMobileconfig, Response response) {


                            sharedPreferences
                                    .edit()
                                    .putString(REFERENCE_MOBILE_CONFIG, gsonsetup.toJson(mMobileconfig))
                                    .commit();

                            Date date = new Date();
                            Timestamp timestamp = new Timestamp(date.getTime());

                            sharedPreferences
                                    .edit()
                                    .putString(REFERENCE_MOBILE_CONFIG_TIME, timestamp.toString())
                                    .commit();
                            callback.run();
                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        };

        if (!data.equalsIgnoreCase("none") && !time.equalsIgnoreCase("none")) {
            final Timestamp past = Timestamp.valueOf(time);
            final Date date = new Date();
            //   Calendar cal1 = Calendar.getInstance();
            final Timestamp now = new Timestamp(date.getTime());
            long pastms = past.getTime();
            long nowms = now.getTime();
            if (nowms - pastms > Constants.ONE_DAY) {
                n.run();
            } else {
                if (data.equalsIgnoreCase("")) {
                    n.run();
                } else {
                    callback.run();
                }
            }
        } else if (data.equalsIgnoreCase("none") || time.equalsIgnoreCase("none")) {
            n.run();
        }


    }

    public PBmobileConfig readConfig(final Context context) {
        final SharedPreferences msharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String data = msharedPreferences.getString(REFERENCE_MOBILE_CONFIG, "");
        return gsonsetup.fromJson(data, PBmobileConfig.class);

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
