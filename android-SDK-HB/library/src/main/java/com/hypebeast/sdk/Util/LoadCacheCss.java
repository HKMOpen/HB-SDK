package com.hypebeast.sdk.Util;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.webkit.WebResourceResponse;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

/**
 * Created by hesk on 13/11/15.
 */
public abstract class LoadCacheCss extends AsyncTask<Void, Void, String> {
    private UrlCache cache;
    private String target;
    private SharedPreferences ssp;
    private String error_message;

    protected abstract void onError(String m);

    protected abstract String getSaveTag();

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
            if (error_message == null) {
                error_message = "there is an unknown error found.";
            }
            onError(error_message);
        }
    }

    // convert InputStream to String
    private static StringBuilder getStringFromInputStream(InputStream is) throws IOException {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (br != null) {
                br.close();
            }
        }
        return sb;
    }

    @Override
    protected String doInBackground(Void... params) {
        StringWriter writer = new StringWriter();
        // StringBuilder sb = new StringBuilder();
        try {
            WebResourceResponse loadedcontent = cache.load(target);
            if (loadedcontent == null) throw new IOException(cache.getErrorMessage());
            IOUtils.copy(loadedcontent.getData(), writer, "UTF-8");
            // sb = getStringFromInputStream(loadedcontent.getData());
        } catch (IOException e) {
            error_message = e.getMessage();
        } catch (Exception e) {
            error_message = "failure to load data from the URL: " + e.getMessage();
        }
        return writer.toString().trim();
    }
}
