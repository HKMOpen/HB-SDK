package com.hkm.hbxtest;

import android.Manifest;

import com.greysonparrelli.permiso.Permiso;
import com.hkm.hbxtest.Dialog.ErrorMessage;
import com.hypebeast.sdk.api.model.hypebeaststore.ResponseMobileOverhead;
import com.hypebeast.sdk.application.Splash;
import com.hypebeast.sdk.application.hbx.ConfigurationSync;
import com.hypebeast.sdk.application.hbx.sync;

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
            public void syncDone(ConfigurationSync conf, ResponseMobileOverhead data) {
                StringBuilder h = new StringBuilder();
                h.append("Successfully synced data. Now it will be closed ");
                ErrorMessage.alert(h.toString(), getFragmentManager(), new Runnable() {
                    @Override
                    public void run() {
                        finish();
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
}
