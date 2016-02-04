package com.hypebeast.sdk.application.popbee;

import android.app.Application;

import com.hypebeast.sdk.Constants;
import com.hypebeast.sdk.application.ApplicationBase;
import com.hypebeast.sdk.clients.PBEditorialClient;

import java.sql.Timestamp;
import java.util.Date;

import bolts.CancellationToken;
import bolts.Task;
import bolts.TaskCompletionSource;

/**
 * Created by hesk on 4/2/16.
 */
public class PopbeeMainApp extends ApplicationBase {
    public static PopbeeMainApp instance;
    public static final String PREFERENCE_FOUNDATION = "foundationfile";
    public static final String PREFERENCE_BRAND_LIST = "brand_list";
    public static final String ACCOUNT_USER_ID = "hbx_user_uid";
    public static final String ACCOUNT_SIG = "hbx_PHPSYLIUSID";
    public static final String ACCOUNT_USER = "hbx_username";
    public static final String ACCOUNT_PASS = "hbx_password";
    public static final String PREFERENCE_FOUNDATION_REGISTRATION = "regtime";
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

    private PBEditorialClient client;

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


    }

    @Override
    protected void removeAllData() {

    }


    public Task<String> getIntAsync(final CancellationToken ct) {
        // Create a new Task
        final TaskCompletionSource<String> tcs = new TaskCompletionSource<>();
        new Thread() {
            @Override
            public void run() {
                // Check if cancelled at start
                if (ct.isCancellationRequested()) {
                    tcs.setCancelled();
                    return;
                }

                int result = 0;
                while (result < 100) {
                    // Poll isCancellationRequested in a loop
                    if (ct.isCancellationRequested()) {
                        tcs.setCancelled();
                        return;
                    }
                    result++;
                }


                tcs.setResult("done");
            }
        }.start();

        return tcs.getTask();
    }

}
