package com.hypebeast.sdk.Util;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebResourceResponse;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import okio.BufferedSink;
import okio.Okio;

/**
 * Created by hesk on 3/6/15.
 */
public class UrlCache {
    public static final long ONE_SECOND = 1000L;
    public static final long ONE_MINUTE = 60L * ONE_SECOND;
    public static final long ONE_HOUR = 60L * ONE_MINUTE;
    public static final long ONE_DAY = 24 * ONE_HOUR;

    private static class CacheEntry {
        public String url;
        public String fileName;
        public String mimeType;
        public String encoding;
        public long maxAgeMillis;

        private CacheEntry(String url, String fileName,
                           String mimeType, String encoding, long maxAgeMillis) {

            this.url = url;
            this.fileName = fileName;
            this.mimeType = mimeType;
            this.encoding = encoding;
            this.maxAgeMillis = maxAgeMillis;
        }
    }


    protected Map<String, CacheEntry> cacheEntries = new HashMap<String, CacheEntry>();
    protected Context activity = null;
    protected File rootDir = null;

    public static String LOG_TAG = "cacheEntry";

    public UrlCache(AppCompatActivity activity) {
        this.activity = activity;
        this.rootDir = this.activity.getFilesDir();
    }

    public UrlCache(Application activity, File rootDir) {
        this.activity = activity;
        this.rootDir = rootDir;
        if (!rootDir.exists()) {
            rootDir.mkdir();
        }
    }


    public void register(final String url,
                         final String cacheFileName,
                         final String mimeType,
                         final String encoding,
                         final long maxAgeMillis) {
        CacheEntry entry = new CacheEntry(url, cacheFileName, mimeType, encoding, maxAgeMillis);
        this.cacheEntries.put(url, entry);
    }

    public WebResourceResponse load(final String url) {
        final CacheEntry cacheEntry = this.cacheEntries.get(url);
        if (cacheEntry == null) return null;
        final File cachedFile = new File(this.rootDir.getPath() + File.separator + cacheEntry.fileName);
        if (cachedFile.exists()) {
            long cacheEntryAge = System.currentTimeMillis() - cachedFile.lastModified();
            if (cacheEntryAge > cacheEntry.maxAgeMillis) {
                cachedFile.delete();
                //cached file deleted, call load() again.
                Log.d(LOG_TAG, "Deleting from cache: " + url);
                return load(url);
            }

            //cached file exists and is not too old. Return file.
            Log.d(LOG_TAG, "Loading from cache: " + url);
            try {
                return new WebResourceResponse(
                        cacheEntry.mimeType, cacheEntry.encoding, new FileInputStream(cachedFile));
            } catch (FileNotFoundException e) {
                String m = "Error loading cached file: " +
                        cachedFile.getPath() +
                        " : " + e.getMessage();
                Log.d(LOG_TAG, m, e);
                //throw new Exception(m);
            }

        } else {
            try {
                cachedFile.createNewFile();
                // downloadAndStore(url, cacheEntry, cachedFile);
                downladAndStoreOkHttp(url, cacheEntry, cachedFile);
                //now the file exists in the cache, so we can just call this method again to read it.
                return load(url);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                Log.d(LOG_TAG, "Error reading file over network: " + cachedFile.getPath(), e);
            }
        }

        return null;
    }

    /**
     * download the file form the internet and store it in the specific path
     *
     * @param url        the url
     * @param cacheEntry the object entry
     * @param cachedFile the cached file
     * @throws IOException the message in exception
     */
    private void downladAndStoreOkHttp(String url, CacheEntry cacheEntry, File cachedFile) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request =
                new Request.Builder().url(url)
                        // .addHeader("X-CSRFToken", csrftoken)
                        .addHeader("Content-Type", "text/css")
                        .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        Headers responseHeaders = response.headers();
        for (int i = 0; i < responseHeaders.size(); i++) {
            System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
        }

        BufferedSink sink = Okio.buffer(Okio.sink(cachedFile));
        sink.writeAll(response.body().source());
        sink.close();

    }

}
