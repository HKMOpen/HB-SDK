package com.hbed.test;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hbed.test.Dialog.ErrorMessage;
import com.hypebeast.sdk.api.model.hbeditorial.Foundation;
import com.hypebeast.sdk.application.Splash;
import com.hypebeast.sdk.application.hypebeast.ConfigurationSync;
import com.hypebeast.sdk.application.hypebeast.sync;
import com.hypebeast.sdk.clients.HBEditorialClient;

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
        ConfigurationSync.with(getApplication(), new sync() {
            @Override
            public void syncDone(ConfigurationSync conf, Foundation data) {
                final String language_prefrence = getLanguagePref(getApplication());
                // final configbank mConfig = conf.getByLanguage(language_prefrence);
                conf.switchToLanguage(language_prefrence);
                HBEditorialClient client = conf.getInstanceHBClient();
                //  Intent d = new Intent(Slash.this, MainScreen.class);
                //  startActivity(d);
                finish();
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
