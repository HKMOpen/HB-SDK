package com.hypebeast.sdk.application;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by hesk on 18/11/15.
 */
public abstract class ApplicationBase {

    protected final Application app;
    protected final SharedPreferences sharedPreferences;

    public ApplicationBase(Application app) {
        this.app = app;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(app);
    }

    protected String loadRef(final String tag) {
        String data = sharedPreferences.getString(tag, "none");
        return data;
    }

    protected void saveInfo(final String tag, final String data) {
        sharedPreferences.edit()
                .putString(tag, data)
                .apply();
    }
}
