package com.hypebeast.sdk.Util;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebResourceResponse;

import com.hypebeast.sdk.api.exception.NotFoundException;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import okio.BufferedSink;
import okio.Okio;

/**
 * Created by hesk on 3/6/15.
 * Local storage file supported
 * API 23 up need permission check.
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
    protected String url_internal;
    public static String LOG_TAG = "cacheEntry";

    public UrlCache(AppCompatActivity activity) {
        this.activity = activity;
        this.rootDir = this.activity.getFilesDir();
    }

    public UrlCache(Application activity, File rootDir) {
        this.activity = activity;
        this.rootDir = rootDir;
    }


    public void register(final String url,
                         final String cacheFileName,
                         final String mimeType,
                         final String encoding,
                         final long maxAgeMillis) {
        CacheEntry entry = new CacheEntry(url, cacheFileName, mimeType, encoding, maxAgeMillis);
        this.cacheEntries.put(url, entry);
        url_internal = url;
    }

    public String getErrorMessage() {
        return error_message_thrown;
    }


    /**
     * this is a blocking code
     *
     * @return downloaded file with the formate web resource response
     */
    public WebResourceResponse load() {
        final CacheEntry cacheEntry = this.cacheEntries.get(url_internal);
        if (cacheEntry == null) return null;
        final File cachedFile = new File(this.rootDir.getPath() + File.separator + cacheEntry.fileName);
        if (cachedFile.exists()) {
            long cacheEntryAge = System.currentTimeMillis() - cachedFile.lastModified();
            if (cacheEntryAge > cacheEntry.maxAgeMillis) {
                cachedFile.delete();
                //cached file deleted, call load() again.
                Log.d(LOG_TAG, "Deleting from cache: " + url_internal);
                return load();
            }

            //cached file exists and is not too old. Return file.
            Log.d(LOG_TAG, "Loading from cache: " + url_internal);
            try {
                WebResourceResponse file = new WebResourceResponse(cacheEntry.mimeType, cacheEntry.encoding, new FileInputStream(cachedFile));
                if (file.getData().available() == 0) {
                    cachedFile.delete();
                    cachedFile.createNewFile();
                    downladAndStoreOkHttp(url_internal, cacheEntry, cachedFile);
                }

                /**
                 * end of the story
                 */
                return file;
            } catch (FileNotFoundException e) {
                String m = "Error loading cached file: " +
                        cachedFile.getPath() +
                        " : " + e.getMessage();
                Log.d(LOG_TAG, m, e);
                //throw new Exception(m);
                error_message_thrown = m;
            } catch (IOException e) {
                String m = "Error loading cached file: " + cachedFile.getPath() + " : " + e.getMessage();
                Log.d(LOG_TAG, m, e);
                //throw new Exception(m);
                error_message_thrown = m;
            }

        } else {
            try {

                if (!rootDir.exists()) {
                    boolean result = rootDir.mkdir();
                    if (!result) {
                        error_message_thrown = "Cannot create the folder at: " + rootDir.getCanonicalPath();
                        return null;
                    }
                }
                cachedFile.createNewFile();
                // downloadAndStore(url, cacheEntry, cachedFile);
                downladAndStoreOkHttp(url_internal, cacheEntry, cachedFile);
                //now the file exists in the cache, so we can just call this method again to read it.
                return load();
            } catch (IOException e) {
                error_message_thrown = e.getLocalizedMessage();
            } catch (Exception e) {
                error_message_thrown = "Error reading file over network: " + cachedFile.getPath();
            }
        }

        return null;
    }

    private String error_message_thrown;

    /**
     * download the file form the internet and store it in the specific path
     * the is the blocking code too
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

    public interface readDone {
        void stringBlock(String here_article);
    }

    /**
     * THE ASYNC TASK FOR LOADING THE FILE FROM THE LOCAL DIRECTORY INTO STRING
     *
     * @param folder_name the name of the folder
     * @param file_name   the name of the file
     * @param action      the action for callbacks
     * @throws IOException the exception
     */
    public static void loadFromLocalFileText(String folder_name, String file_name, final readDone action) throws IOException {
        String root = Environment.getExternalStorageDirectory().toString() + File.separator;
        File myDir = new File(root + folder_name + File.separator + file_name);
        loadFromLocalFileText(myDir, action);
    }

    /**
     * THE ASYNC TASK FOR LOADING THE FILE FROM THE LOCAL DIRECTORY INTO STRING
     *
     * @param full_path_cachedFile the name of the file in full path
     * @param action               the action for callbacks
     * @throws IOException the exception
     */
    public static void loadFromLocalFileText(String full_path_cachedFile, final readDone action) throws IOException {
        File myDir = new File(full_path_cachedFile);
        loadFromLocalFileText(myDir, action);
    }

    /**
     * the blocking task
     *
     * @param folder_name the folder name only
     * @param file_name   just the name of the file
     * @return string
     * @throws IOException exception
     */
    public static String loadFromLocalFileTextTask(String folder_name, String file_name) throws IOException {
        String root = Environment.getExternalStorageDirectory().toString() + File.separator;
        File myDir = new File(root + folder_name + File.separator + file_name);
        return loadFromLocalFileTextTask(myDir);
    }

    public static String loadFromLocalFileTextTask(String full_path_cachedFile) throws IOException {
        File myDir = new File(full_path_cachedFile);
        return loadFromLocalFileTextTask(myDir);
    }

    public static String loadFromLocalFileTextTask(File cachedFile) throws IOException {
        String UTF8 = "utf8";
        int BUFFER_SIZE = 8192;
        final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(cachedFile), UTF8), BUFFER_SIZE);

        String temp_line;
        StringBuilder sb = new StringBuilder();

        while ((temp_line = br.readLine()) != null) {
            sb.append(temp_line);
        }
        return sb.toString();
    }

    public static void loadFromLocalFileText(File cachedFile, final readDone read_done) throws IOException {
        String UTF8 = "utf8";
        int BUFFER_SIZE = 8192;
        final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(cachedFile), UTF8), BUFFER_SIZE);

        new AsyncTask<Void, Void, Void>() {
            protected String temp_line;
            protected StringBuilder sb = new StringBuilder();

            protected String line() throws IOException {
                temp_line = br.readLine();
                return temp_line;
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    while (line() != null) {
                        sb.append(temp_line);
                    }
                } catch (IOException e) {
                    // error_message_thrown = e.getMessage();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (read_done != null) {
                    read_done.stringBlock(sb.toString());
                }
            }
        }.execute();
    }
}
