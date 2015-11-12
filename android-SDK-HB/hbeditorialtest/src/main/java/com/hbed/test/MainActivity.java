package com.hbed.test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hypebeast.sdk.api.model.hbeditorial.Foundation;
import com.hypebeast.sdk.application.hypebeast.ConfigurationSync;
import com.hypebeast.sdk.application.hypebeast.sync;
import com.hypebeast.sdk.clients.HBEditorialClient;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.splash);
        synchronizData();
    }

    public static String getLanguagePref(Context context) {
        try {
            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            return sharedPreferences.getString("languagecode", "en");
        } catch (NullPointerException e) {
            return "en";
        }
    }

    protected void synchronizData() {

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
        });
    }
}
