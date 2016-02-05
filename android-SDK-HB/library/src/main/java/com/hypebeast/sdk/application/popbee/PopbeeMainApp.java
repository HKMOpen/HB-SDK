package com.hypebeast.sdk.application.popbee;

import android.app.Application;

import com.hypebeast.sdk.Util.CacheManager;
import com.hypebeast.sdk.application.ApplicationBase;
import com.hypebeast.sdk.clients.PBEditorialClient;

import java.util.ArrayList;

import bolts.CancellationTokenSource;
import bolts.Continuation;
import bolts.Task;

import static com.hypebeast.sdk.Constants.*;

/**
 * Created by hesk on 4/2/16.
 */
public class PopbeeMainApp extends ApplicationBase {

    private static final String ACCESS_FILE_URL = "http://popbee.com/wp-content/themes/popbee-v6/app/main.css";
    public static final String local_css_file_name = "pb.css";

    private PBEditorialClient client;
    public static PopbeeMainApp instance;
    private com.hypebeast.sdk.application.popbee.sync mListener;

    public static PopbeeMainApp with(Application app, sync mListener) {
        if (instance == null) {
            instance = new PopbeeMainApp(app, mListener);
            instance.init();
        } else {
            instance.addInterface(mListener);
            instance.init();
        }
        return instance;
    }


    public static PopbeeMainApp getInstance() throws Exception {
        if (instance == null) {
            throw new Exception("please init a new instance. or go to the slash screen again");
        }
        return instance;
    }


    public PopbeeMainApp(Application app, sync mListener) {
        super(app);
        client = PBEditorialClient.getInstance(app);
        //client.setLanguageBase(HBEditorialClient.BASE_EN);
        //mOverheadRequest = client.createOverHead();
        //request_login = client.createAuthenticationHBX();
        addInterface(mListener);
    }

    private void addInterface(sync listenerSync) {
        mListener = listenerSync;
    }

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
                mListener.syncDone(PopbeeMainApp.this, "done");
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
