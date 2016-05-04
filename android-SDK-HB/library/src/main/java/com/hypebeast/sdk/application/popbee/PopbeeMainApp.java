package com.hypebeast.sdk.application.popbee;

import android.app.Application;

import com.hypebeast.sdk.Util.CacheManager;
import com.hypebeast.sdk.api.exception.ApiException;
import com.hypebeast.sdk.api.model.popbees.PBmobileConfig;
import com.hypebeast.sdk.api.resources.pb.pbPost;
import com.hypebeast.sdk.application.ApplicationBase;
import com.hypebeast.sdk.clients.PBEditorialClient;

import java.util.ArrayList;

import bolts.CancellationTokenSource;
import bolts.Continuation;
import bolts.Task;
import bolts.TaskCompletionSource;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.hypebeast.sdk.Constants.*;

/**
 * Created by hesk on 4/2/16.
 */
public class PopbeeMainApp extends ApplicationBase {

    private static final String ACCESS_FILE_URL = "http://popbee.com/wp-content/themes/popbee-v6/app/main.css";
    public static final String local_css_file_name = "pb.css";

    private PBEditorialClient client;
    private pbPost postrequest;
    public static PopbeeMainApp instance;
    private com.hypebeast.sdk.application.popbee.sync mListener;
    private PBmobileConfig configuration;

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

    private Task<Void> checkconfigurationoverhead() {
        final TaskCompletionSource<Void> tks = new TaskCompletionSource<>();
        if (configuration == null) {
            try {
                postrequest = client.createPostsFeed();
                postrequest.mobile_config(new Callback<PBmobileConfig>() {
                    @Override
                    public void success(PBmobileConfig pBmobileConfig, Response response) {
                        configuration = pBmobileConfig;
                        tks.setResult(null);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        tks.setError(error);
                    }
                });
            } catch (ApiException e) {
                tks.setError(e);
            }
        } else {
            tks.setError(new Exception("configuration is defined."));
        }
        return tks.getTask();
    }

    protected void init() {
        super.init();
        CancellationTokenSource cts = new CancellationTokenSource();
        getIntAsync(cts.getToken())


                /**
                 *   adding task
                 *
                 */


                .continueWithTask(new Continuation<String, Task<Void>>() {
                    @Override
                    public Task<Void> then(Task<String> task) throws Exception {
                        ArrayList<Task<Void>> tasks = new ArrayList<Task<Void>>();
                        tasks.add(checkconfigurationoverhead());
                        //tasks.add(setCssFile(ACCESS_FILE_URL, local_css_file_name));
                        return Task.whenAll(tasks);
                    }
                })

                /**
                 .continueWithTask(new Continuation<Void, Task<Void>>() {
                @Override public Task<Void> then(Task<Void> task) throws Exception {
                if (configuration == null) {
                configuration = postrequest.mobile_config();
                }
                return null;
                }
                })*/


              /*  .continueWithTask(new Continuation<Void, Task<Void>>() {
                    @Override
                    public Task<Void> then(Task<Void> task) throws Exception {
                        if (task.isFaulted()) {
                            mListener.error(task.getError().getMessage());
                            throw new ApiException("not found");
                        }
                        return null;
                    }
                })
*/

                /**
                 *
                 *
                 */

                .onSuccess(new Continuation<Void, Void>() {
                    @Override
                    public Void then(Task<Void> task) throws Exception {
                        // Every comment was deleted.
                        if (task.isFaulted()) {
                            mListener.error(task.getError().getMessage());
                        } else {
                            mListener.syncDone(PopbeeMainApp.this, "done");
                        }
                        return null;
                    }
                });
    }

    public PBmobileConfig getConfiguration() {
        return configuration;
    }

    @Override
    protected void removeAllData() {
        //CacheManager.trimCache(app);
        saveInfo(PREFERENCE_FOUNDATION_FILE_CONTENT, "");
        saveInfo(PREFERENCE_BRAND_LIST, "");
        saveInfo(PREFERENCE_FOUNDATION_REGISTRATION, "");
        if (client == null) return;
        client.removeAllCache();
    }
}
