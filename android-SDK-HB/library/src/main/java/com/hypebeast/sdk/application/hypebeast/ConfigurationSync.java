package com.hypebeast.sdk.application.hypebeast;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.hypebeast.sdk.Constants;
import com.hypebeast.sdk.Util.LoadCacheCss;
import com.hypebeast.sdk.api.exception.ApiException;
import com.hypebeast.sdk.api.model.hbeditorial.Foundation;
import com.hypebeast.sdk.api.model.hbeditorial.configbank;
import com.hypebeast.sdk.Util.UrlCache;
import com.hypebeast.sdk.api.resources.hypebeast.overhead;
import com.hypebeast.sdk.application.ApplicationBase;
import com.hypebeast.sdk.clients.HBEditorialClient;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import static com.hypebeast.sdk.Constants.*;
/**
 * Created by hesk on 1/9/15.
 */
public class ConfigurationSync extends ApplicationBase {
    public static ConfigurationSync instance;
    public static final String local_css_file_name = "hb_article_content.css";
    private static final String ACCESS_FILE_URL = "http://hypebeast.com/bundles/hypebeasteditorial/app/main.css";
    private static final String CONFIG_ENDPOINT = "http://hypebeast.com/api/mobile-app-config?version=2.1";

    private overhead clientRequest;
    private Foundation mFoundation;
    private HBEditorialClient client;
    private ArrayList<sync> mListeners = new ArrayList<>();
    private sync mListener;
    private UrlCache mUrlCache;
    private LoadCacheCss cssLoader;
    private boolean isFailure;
    private String failure_message;

    public static ConfigurationSync with(Application app, sync mListener) {
        if (instance == null) {
            instance = new ConfigurationSync(app, mListener);
            instance.init();

        } else {
            instance.addInterface(mListener);
            instance.init();
        }

        return instance;
    }

    @Override
    protected void removeAllData() {
        saveInfo(PREFERENCE_FOUNDATION_FILE_CONTENT, "");
        saveInfo(PREFERENCE_CSS_FILE_CONTENT, "");
        saveInfo(PREFERENCE_FOUNDATION_REGISTRATION, "");
    }

    public static ConfigurationSync getInstance() throws Exception {
        if (instance == null) {
            throw new Exception("please init a new instance");
        }
        return instance;
    }

    public ConfigurationSync(Application app, sync mListener) {
        super(app);
        isFailure = false;
        client = HBEditorialClient.newInstance(app);
        addInterface(mListener);
    }

    public void switchToLanguage(String mLanguage) {
        if (mLanguage.equals("en")) {
            client.setLanguageBase(HBEditorialClient.BASE_EN);
        } else if (mLanguage.equals("zh_CN")) {
            client.setLanguageBase(HBEditorialClient.BASE_CN);
        } else if (mLanguage.equals("ja")) {
            client.setLanguageBase(HBEditorialClient.BASE_JP);
        } else if (mLanguage.equals("zh_TW")) {
            client.setLanguageBase(HBEditorialClient.BASE_CN);
        }
    }

    public HBEditorialClient getInstanceHBClient() {
        return client;
    }

    private void addInterface(sync listenerSync) {
        mListener = listenerSync;
    }


    private void complete_first_stage() {
        cssLoader.setTargetGet(ACCESS_FILE_URL).execute();
    }


    private class LoadCacheCssN extends LoadCacheCss {

        public LoadCacheCssN(UrlCache cache, SharedPreferences SP) {
            super(cache, SP);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            complete_second_stage();
        }

        @Override
        protected void onError(String m) {
            failure_message = m;
            isFailure = true;
        }

        @Override
        protected String getSaveTag() {
            return PREFERENCE_CSS_FILE_CONTENT;
        }
    }

    private void prepareCacheConfiguration() {
        final String root = Environment.getExternalStorageDirectory().toString() + File.separator;
        final File myDir = new File(root + APP_FOLDER_NAME);
        mUrlCache = new UrlCache(app, myDir);
        mUrlCache.register(ACCESS_FILE_URL, local_css_file_name, "text/css", "UTF-8", 5 * UrlCache.ONE_DAY);
        cssLoader = new LoadCacheCssN(mUrlCache, PreferenceManager.getDefaultSharedPreferences(app));
    }

    private void complete_second_stage() {
        if (mListener != null) {
            if (!isFailure) {
                if (mListener instanceof syncDebug) {
                    ((syncDebug) mListener).syncDone(instance, mFoundation, getVersionMessage());
                } else
                    mListener.syncDone(instance, mFoundation);
            } else {
                mListener.initFailure(failure_message);
            }
        }
    }


    private void syncWorkerThread() {
        try {
            clientRequest = client.createOverHead(CONFIG_ENDPOINT);
            clientRequest.mobile_config_get(new Callback<Foundation>() {
                @Override
                public void success(Foundation foundation, Response response) {
                    mFoundation = foundation;
                    saveInfo(PREFERENCE_FOUNDATION_FILE_CONTENT, client.fromJsonToString(foundation));
                    Date date = new Date();
                    Timestamp timestamp = new Timestamp(date.getTime());
                    saveInfo(PREFERENCE_FOUNDATION_REGISTRATION, timestamp.toString());
                    complete_first_stage();
                }

                @Override
                public void failure(RetrofitError error) {
                    failure_message = error.getMessage();
                    isFailure = true;
                }
            });
        } catch (ApiException e) {
            failure_message = e.getMessage();
            isFailure = true;
        }
    }


    protected void init() {
        super.init();
        prepareCacheConfiguration();
        String data = loadRef(PREFERENCE_FOUNDATION_FILE_CONTENT);
        String time = loadRef(PREFERENCE_FOUNDATION_REGISTRATION);
        if (!data.equalsIgnoreCase(EMPTY_FIELD) && !time.equalsIgnoreCase(EMPTY_FIELD)) {
            Timestamp past = Timestamp.valueOf(time);
            Date date = new Date();
            //   Calendar cal1 = Calendar.getInstance();
            Timestamp now = new Timestamp(date.getTime());
            long pastms = past.getTime();
            long nowms = now.getTime();
            if (nowms - pastms > Constants.ONE_DAY) {
                syncWorkerThread();
            } else {
                if (data.equalsIgnoreCase("")) {
                    syncWorkerThread();
                } else {
                    mFoundation = client.fromsavedConfiguration(data);
                    complete_first_stage();
                }
            }
        } else if (data.equalsIgnoreCase(EMPTY_FIELD) || time.equalsIgnoreCase(EMPTY_FIELD)) {
            syncWorkerThread();
        }
    }

    public Foundation getFoundation() {
        return mFoundation;
    }


    public configbank getByLanguage(String lang) {
        if (lang.equals("en")) {
            return mFoundation.english;
        } else if (lang.equals("zh_CN")) {
            return mFoundation.chinese_simplified;
        } else if (lang.equals("ja")) {
            return mFoundation.japanese;
        } else if (lang.equals("zh_TW")) {
            return mFoundation.chinese_traditional;
        } else
            return mFoundation.english;
    }

    public String getVersionMessage() {
        return debug_version;
    }
}
