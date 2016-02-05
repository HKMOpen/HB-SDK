package com.hypebeast.sdk.application.hypetrak;

import android.app.Application;

import com.hypebeast.sdk.Util.CacheManager;
import com.hypebeast.sdk.application.ApplicationBase;
import com.hypebeast.sdk.clients.HTEditorialClient;

import java.util.ArrayList;

import bolts.CancellationTokenSource;
import bolts.Continuation;
import bolts.Task;

import static com.hypebeast.sdk.Constants.*;

/**
 * Created by hesk on 4/2/16.
 */
public class HypetrakMainApp extends ApplicationBase {
    public static HypetrakMainApp instance;
    private sync mListener;
    private static final String ACCESS_FILE_URL =
            "http://hypetrak.com/wp-content/themes/hypetrak-v3/app/main.css";
    public static final String local_css_file_name = "ht.css";

    public static HypetrakMainApp with(Application app, sync mListener) {
        if (instance == null) {
            instance = new HypetrakMainApp(app, mListener);
            instance.init();
        } else {
            instance.addInterface(mListener);
            instance.init();
        }
        return instance;
    }

    public static HypetrakMainApp getInstance() throws Exception {
        if (instance == null) {
            throw new Exception("please init a new instance. or go to the slash screen again");
        }
        return instance;
    }

    private HTEditorialClient client;

    public HypetrakMainApp(Application app, sync mListener) {
        super(app);
        client = HTEditorialClient.getInstance(app);
        //client.setLanguageBase(HBEditorialClient.BASE_EN);
        //mOverheadRequest = client.createOverHead();
        addInterface(mListener);
    }

    private void addInterface(sync listenerSync) {
        mListener = listenerSync;
    }

    /**
     * this is the initiation of the app
     */
    protected void init() {
        super.init();
        CancellationTokenSource cts = new CancellationTokenSource();
        getIntAsync(cts.getToken()).continueWithTask(new Continuation<String, Task<Void>>() {
            @Override
            public Task<Void> then(Task<String> task) throws Exception {
                ArrayList<Task<Void>> tasks = new ArrayList<Task<Void>>();
                tasks.add(setCssFile(ACCESS_FILE_URL, local_css_file_name));
                return Task.whenAll(tasks);
            }
        }).onSuccess(new Continuation<Void, Void>() {
            @Override
            public Void then(Task<Void> ignored) throws Exception {
                // Every comment was deleted.
                mListener.syncDone(HypetrakMainApp.this, "done");
                return null;
            }
        });
    }


    @Override
    protected void removeAllData() {
        CacheManager.trimCache(app);
        saveInfo(PREFERENCE_FOUNDATION_FILE_CONTENT, "");
        saveInfo(PREFERENCE_BRAND_LIST, "");
        saveInfo(PREFERENCE_FOUNDATION_REGISTRATION, "");
        client.removeAllCache();
    }

}
