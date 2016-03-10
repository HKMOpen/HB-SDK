package com.hypebeast.sdk.application.hbx;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

import com.hypebeast.sdk.Constants;
import com.hypebeast.sdk.Util.CacheManager;
import com.hypebeast.sdk.Util.Connectivity;
import com.hypebeast.sdk.api.exception.ApiException;
import com.hypebeast.sdk.api.model.hypebeaststore.ResLoginCheck;
import com.hypebeast.sdk.api.model.hypebeaststore.ResLoginPassword;
import com.hypebeast.sdk.api.model.hypebeaststore.ResponseBrandList;
import com.hypebeast.sdk.api.model.hypebeaststore.ResponseMobileOverhead;
import com.hypebeast.sdk.api.resources.hbstore.Authentication;
import com.hypebeast.sdk.api.resources.hbstore.Overhead;
import com.hypebeast.sdk.application.ApplicationBase;
import com.hypebeast.sdk.clients.HBStoreApiClient;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import bolts.Task;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.hypebeast.sdk.Constants.*;

/**
 * Created by hesk on 1/9/15.
 */
public class ConfigurationSync extends ApplicationBase {
    public static ConfigurationSync instance;
    public static final String ACCOUNT_USER_ID = "hbx_user_uid";
    public static final String ACCOUNT_SIG = "hbx_PHPSYLIUSID";
    public static final String ACCOUNT_USER = "hbx_username";
    public static final String ACCOUNT_PASS = "hbx_password";
    public static final String USER_COUNTRY_CODE = "country_code_use";
    private String error_messages;
    private Overhead mOverheadRequest;
    private ResponseMobileOverhead mFoundation;
    private ResponseBrandList brandList;
    private HBStoreApiClient client;
    private ArrayList<sync> mListeners = new ArrayList<>();
    private Authentication request_login;
    private sync mListener;
    private boolean login_mechanism_done = false;

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

    public static ConfigurationSync getInstance() throws Exception {
        if (instance == null) {
            throw new Exception("please init a new instance. or go to the slash screen again");
        }
        return instance;
    }

    public ConfigurationSync(Application app, sync mListener) {
        super(app);
        client = HBStoreApiClient.getInstance(app);
        //client.setLanguageBase(HBEditorialClient.BASE_EN);
        mOverheadRequest = client.createOverHead();
        request_login = client.createAuthenticationHBX();
        addInterface(mListener);
    }

    @Override
    protected void removeAllData() {
        CacheManager.trimCache(app);
        saveInfo(PREFERENCE_FOUNDATION_FILE_CONTENT, "");
        saveInfo(PREFERENCE_BRAND_LIST, "");
        saveInfo(PREFERENCE_FOUNDATION_REGISTRATION, "");
        saveInfo(ACCOUNT_USER_ID, "");
        saveInfo(ACCOUNT_SIG, "");
        saveInfo(ACCOUNT_USER, "");
        saveInfo(ACCOUNT_PASS, "");
        client.removeAllCache();
    }

    public HBStoreApiClient getInstanceHBClient() {
        return client;
    }

    private void addInterface(sync listenerSync) {
        // mListeners.add(listenerSync);
        mListener = listenerSync;
    }

    public void clearListeners() {
        //mListeners.clear();
    }

    public void logout() {
        saveInfo(ACCOUNT_USER_ID, "");
        saveInfo(ACCOUNT_SIG, "");
        saveInfo(ACCOUNT_USER, "");
        saveInfo(ACCOUNT_PASS, "");
        isLogin = false;
    }

    private void syncWorkerThread() {
        syncCheckLogined();
        syncBrandList();
        syncAppBaseInfo();
    }

    /**
     * completed the actions from the login process
     */
    private void executeListeners() {
        if (mFoundation == null) return;
        if (brandList == null) return;
        if (!login_mechanism_done) return;
        if (mListener != null) {
            if (mListener instanceof sync) {
                mListener.syncDone(instance, mFoundation);
            }
        }
    }

    private boolean isLogin = false;

    public boolean isLoginStatusValid() {
        return isLogin;
    }

    public void setLoginSuccess(final @Nullable String session_key_id) {
        saveInfo(ACCOUNT_SIG, session_key_id);
        isLogin = true;
    }

    private void syncCheckLogined() {
        try {
            final Handler h = new Handler();
            final String user = loadRef(ACCOUNT_USER);
            final String pass = loadRef(ACCOUNT_PASS);
            final String sig = loadRef(ACCOUNT_SIG);
            boolean online = Connectivity.isConnected(app);
            if (online) {
                if (!sig.equalsIgnoreCase(EMPTY_FIELD)) {
                    login_v1_authentication(sig);
                } else {
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("loginHBX", "no authentication found and continue..");
                            login_mechanism_done = true;
                            executeListeners();
                        }
                    }, 100);
                }
            } else {
                login_mechanism_done = true;
                executeListeners();
            }

        } catch (ApiException e) {
            error_messages = "errors from the login process: " + e.getMessage();
            Log.d("loginHBX", error_messages);
            triggerFatalError(error_messages);
            //  if (mListener != null) mListener.error(e.getMessage());
        }
    }

    /*
    if (user.equalsIgnoreCase(EMPTY_FIELD) || pass.equalsIgnoreCase(EMPTY_FIELD)) {

    } else {
        login_v2_authentication(user, pass, null);
    }
    */

    /**
     * checking the login via session ID or token
     *
     * @param sig the session id in the cookie
     * @throws ApiException the error
     */
    private void login_v1_authentication(final String sig) throws ApiException {
        Log.d("loginHBX", "start authentication with session ID");
        getInstanceHBClient().setCookieSessionId(sig);
        request_login.checkLoginV1(new Callback<ResLoginCheck>() {
            @Override
            public void success(ResLoginCheck s, Response response) {
                isLogin = true;
                login_mechanism_done = true;
                Log.d("loginHBX", "login result : " + s);
                executeListeners();
            }

            @Override
            public void failure(RetrofitError e) {
                login_mechanism_done = true;
                Log.d("loginHBX", "failure to login= " + e.getMessage());
                executeListeners();
            }
        });
    }


    /**
     * checking the login via user credential
     *
     * @param user   user name or email
     * @param pass   the password
     * @param mlogin the login call back
     * @throws ApiException the exception
     */
    public void login_v2_authentication(final String user, final String pass, final @Nullable logincb mlogin) throws ApiException {
        Log.d("loginHBX", "start authentication with user pass and id");
        request_login.checkLoginV2(user, pass, new Callback<ResLoginPassword>() {
            @Override
            public void success(ResLoginPassword s, Response response) {
                isLogin = true;
                //save user password and the username
                saveInfo(ACCOUNT_USER, user);
                saveInfo(ACCOUNT_PASS, pass);
                saveInfo(ACCOUNT_SIG, s.session_id);
                instance.getInstanceHBClient().setCookieSessionId(s.session_id);
                Log.d("loginHBX",
                        "login result successful: " + user
                                + "\nPassword:" + pass +
                                "\nSessionID:" + s.session_id);

                if (mlogin != null) mlogin.success();
                else executeListeners();
            }

            @Override
            public void failure(RetrofitError e) {
                Log.d("loginHBX", e.getMessage());
                if (mlogin != null) mlogin.failure();
                else executeListeners();
            }
        });
    }


    private void registerTimeBaseInfo() {
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        saveInfo(PREFERENCE_FOUNDATION_REGISTRATION, timestamp.toString());
    }

    private void syncAppBaseInfo() {
        try {
            mOverheadRequest.mobile_config(new Callback<ResponseMobileOverhead>() {
                @Override
                public void success(ResponseMobileOverhead foundation, Response response) {
                    mFoundation = foundation;
                    saveInfo(PREFERENCE_FOUNDATION_FILE_CONTENT, client.fromJsonToString(foundation));
                    registerTimeBaseInfo();
                    executeListeners();
                }

                @Override
                public void failure(RetrofitError error) {
                    triggerFatalError(error.getMessage());
                }
            });
        } catch (ApiException e) {
            triggerFatalError(e.getMessage());
        }
    }

    private void syncBrandList() {
        try {
            boolean online = Connectivity.isConnected(app);
            if (brandList == null || loadRef(PREFERENCE_BRAND_LIST).equalsIgnoreCase(EMPTY_FIELD)) {
                if (online) {
                    client.createBrand().getAll(new Callback<ResponseBrandList>() {
                        @Override
                        public void success(ResponseBrandList responseBrandList, Response response) {
                            brandList = responseBrandList;
                            saveInfo(PREFERENCE_BRAND_LIST, client.fromJsonToString(responseBrandList));
                            executeListeners();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            triggerFatalError(error.getMessage());
                        }
                    });
                }
            } else {
                executeListeners();
            }

        } catch (ApiException e) {
            triggerFatalError(e.getMessage());
        }
    }

    private void triggerFatalError(final String error_messages) {
        if (mListener != null) mListener.error(error_messages);
    }

    public void saveCountryCode(String code) {
        saveInfo(USER_COUNTRY_CODE, code);
    }

    public String loadCountryCode() {
        return loadRef(USER_COUNTRY_CODE);
    }

    class threadWork extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            return null;
        }
    }

    private void checkTime(String time, String data, String data_brand) {
        Timestamp past = Timestamp.valueOf(time);
        Date date = new Date();
        //   Calendar cal1 = Calendar.getInstance();
        Timestamp now = new Timestamp(date.getTime());
        long pastms = past.getTime();
        long nowms = now.getTime();
        if (nowms - pastms > Constants.ONE_HOUR) {
            syncWorkerThread();
        } else {
            if (data.equalsIgnoreCase(EMPTY_FIELD) || data_brand.equalsIgnoreCase(EMPTY_FIELD)) {
                syncWorkerThread();
            } else {
                mFoundation = client.converttoConfig(data);
                brandList = client.converttoBrandList(data_brand);
                syncCheckLogined();
                executeListeners();
            }
        }
    }

    protected void init() {
        super.init();
        String data = loadRef(PREFERENCE_FOUNDATION_FILE_CONTENT);
        String data_brand = loadRef(PREFERENCE_BRAND_LIST);
        String time = loadRef(PREFERENCE_FOUNDATION_REGISTRATION);
        login_mechanism_done = false;
        boolean online = Connectivity.isConnected(app);
        boolean emptydatabase = data.equalsIgnoreCase(EMPTY_FIELD) || time.equalsIgnoreCase(EMPTY_FIELD) || data_brand.equalsIgnoreCase(EMPTY_FIELD);
        if (online && emptydatabase || !online && emptydatabase) {
            syncWorkerThread();
        } else if (!online && emptydatabase) {
            removeAllData();
            //throw error
            triggerFatalError("This app cannot be started because there is no internet detected.");
        } else if (online && !emptydatabase) {
            checkTime(time, data, data_brand);
        } else {
            mFoundation = client.converttoConfig(data);
            brandList = client.converttoBrandList(data_brand);
            login_mechanism_done = true;
            executeListeners();
        }
    }

    public ResponseMobileOverhead getFoundation() {
        return mFoundation;
    }

    public ResponseBrandList getBrandList() {
        return brandList;
    }
}
