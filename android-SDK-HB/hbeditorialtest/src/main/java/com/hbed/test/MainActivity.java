package com.hbed.test;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.greysonparrelli.permiso.Permiso;
import com.hbed.test.Dialog.ErrorMessage;
import com.hypebeast.sdk.api.model.hbeditorial.Foundation;
import com.hypebeast.sdk.application.Splash;
import com.hypebeast.sdk.application.hypebeast.ConfigurationSync;
import com.hypebeast.sdk.application.hypebeast.syncDebug;
import com.hypebeast.sdk.clients.HBEditorialClient;

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
        ConfigurationSync.with(getApplication(), new syncDebug() {
            @Override
            public void syncDone(ConfigurationSync conf, Foundation data, String msg) {
                final String language_prefrence = getLanguagePref(getApplication());
                // final configbank mConfig = conf.getByLanguage(language_prefrence);
                conf.switchToLanguage(language_prefrence);
                HBEditorialClient client = conf.getInstanceHBClient();
                //  Intent d = new Intent(Slash.this, MainScreen.class);
                //  startActivity(d);
                //finish();

                StringBuilder h = new StringBuilder();
                h.append("Successfully synced data. Now it will be closed ");
                h.append(msg);
                ErrorMessage.alert(h.toString(), getFragmentManager(), new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
            }

            @Override
            public void initFailure(String message) {
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
