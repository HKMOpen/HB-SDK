package com.hbexperiement.trk;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.greysonparrelli.permiso.Permiso;
import com.hbexperiement.trk.Dialog.ErrorMessage;
import com.hypebeast.sdk.application.Splash;
import com.hypebeast.sdk.application.hypetrak.HypetrakMainApp;
import com.hypebeast.sdk.application.hypetrak.sync;

public class MainActivity extends Splash {
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


    public static String getLanguagePref(Context context) {
        try {
            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            return sharedPreferences.getString("languagecode", "en");
        } catch (NullPointerException e) {
            return "en";
        }
    }

    @Override
    protected void synchronizeData() {


        HypetrakMainApp.with(getApplication(), new sync() {
            @Override
            public void syncDone(HypetrakMainApp self, String data) {
                StringBuilder h = new StringBuilder();
                h.append("Successfully synced data for hypetrak. Now it will be closed ");
                h.append(data);
                ErrorMessage.alert(h.toString(), getFragmentManager(), new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
            }

            @Override
            public void error(String txt) {
                ErrorMessage.alert(txt, getFragmentManager(), new Runnable() {
                    @Override
                    public void run() {
                        synchronizeData();
                    }
                });
            }
        });

    }
}
