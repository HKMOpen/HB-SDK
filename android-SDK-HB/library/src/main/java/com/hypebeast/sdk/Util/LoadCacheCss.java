package com.hypebeast.sdk.Util;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.webkit.WebResourceResponse;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by hesk on 13/11/15.
 */
public abstract class LoadCacheCss extends AsyncTask<Void, Void, String> {
    private UrlCache cache;
    private String target;
    private SharedPreferences ssp;

    public LoadCacheCss(UrlCache cache, SharedPreferences SP) {
        this.cache = cache;
        ssp = SP;
    }

    public LoadCacheCss setTargetGet(String path) {
        target = path;
        return this;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result.length() > 0) {
            ssp.edit().putString(getSaveTag(), result).commit();
        } else {
            onError(error_message);
        }
    }

    protected abstract String getSaveTag();

    private String error_message;

    protected abstract void onError(String m);

    @Override
    protected String doInBackground(Void... params) {
        StringWriter writer = new StringWriter();
        try {
            WebResourceResponse loadedcontent = cache.load(target);
            IOUtils.copy(loadedcontent.getData(), writer, "UTF-8");
        } catch (IOException e) {
            error_message = e.getMessage();
        } catch (Exception e) {
            error_message = e.getMessage();
        }
        return writer.toString().trim();
    }
}
