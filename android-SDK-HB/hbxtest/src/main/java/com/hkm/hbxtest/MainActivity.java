package com.hkm.hbxtest;

import android.Manifest;

import com.greysonparrelli.permiso.Permiso;
import com.hkm.hbxtest.Dialog.ErrorMessage;
import com.hypebeast.sdk.api.model.hypebeaststore.ResponseMobileOverhead;
import com.hypebeast.sdk.application.Splash;
import com.hypebeast.sdk.application.hbx.ConfigurationSync;
import com.hypebeast.sdk.application.hbx.sync;
import com.hypebeast.sdk.clients.HBStoreApiClient;

public class MainActivity extends Splash {


    @Override
    protected void onPermissionGranted() {
        synchronizeData();
    }

    @Override
    protected void onPermissionDenied() {
        finish();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.splash;
    }

    @Override
    protected void V23permission_request() {
        Permiso.getInstance().requestPermissions(
                getPermProcess(),
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        );
    }

    @Override
    protected void synchronizeData() {
        ConfigurationSync.with(getApplication(), new sync() {
            @Override
            public void syncDone(final ConfigurationSync cdata, ResponseMobileOverhead data) {
                StringBuilder h = new StringBuilder();
                h.append("Successfully synced data. Now you will continue with the testing menu for the APIs");
                HBStoreApiClient.getInstance(getApplication()).hookSyncTasker(cdata);
                ErrorMessage.alert(h.toString(), getFragmentManager(), new Runnable() {
                    @Override
                    public void run() {
                        general_test();
                        /*
                        if (conf.isLoginStatusValid()) {
                            bind_has_login(conf);
                        } else {
                            bind_not_login(conf);
                        }*/
                    }
                });
            }

            @Override
            public void error(String message) {
                ErrorMessage.alert(message, getFragmentManager(), new Runnable() {
                    @Override
                    public void run() {
                        synchronizeData();
                    }
                });
            }
        });
    }

    private void general_test() {
        getFragmentManager()
                .beginTransaction()
                .add(R.id.contentholder, new general_test(), "general")
                .addToBackStack(null)
                .commit();
    }

    /**
     * extensive conten start from here
     */
    private void bind_not_login(ConfigurationSync data) {
        StringBuilder sb = new StringBuilder();
        sb.append("https://");
        sb.append(data.getFoundation().data.host);
        sb.append("/login");
        getFragmentManager()
                .beginTransaction()
                .add(R.id.contentholder, loginfb.newInstance(
                        //   loginfb.getURL(getString(R.string.url_account_web_login))
                        loginfb.getURL(sb.toString())
                ), "fblogin")
                .addToBackStack(null)
                .commit();
    }

    private void bind_has_login(ConfigurationSync data) {
        getFragmentManager()
                .beginTransaction()
                .add(R.id.contentholder,
                        new login_view(),
                        "haslogin")
                .addToBackStack(null)
                .commit();
    }

}
