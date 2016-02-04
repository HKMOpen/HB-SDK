package com.hypebeast.sdk.application.hypetrak;

import android.app.Application;

import com.hypebeast.sdk.Constants;
import com.hypebeast.sdk.application.ApplicationBase;
import com.hypebeast.sdk.clients.HTEditorialClient;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by hesk on 4/2/16.
 */
public class HypetrakMainApp extends ApplicationBase {
    public static HypetrakMainApp instance;
    public static final String PREFERENCE_FOUNDATION = "foundationfile";
    public static final String PREFERENCE_BRAND_LIST = "brand_list";
    public static final String ACCOUNT_USER_ID = "hbx_user_uid";
    public static final String ACCOUNT_SIG = "hbx_PHPSYLIUSID";
    public static final String ACCOUNT_USER = "hbx_username";
    public static final String ACCOUNT_PASS = "hbx_password";
    public static final String PREFERENCE_FOUNDATION_REGISTRATION = "regtime";
    private sync mListener;

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

    }

    @Override
    protected void removeAllData() {

    }


}
