package com.hypebeast.sdk.application;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.WebResourceResponse;
import android.widget.TextView;

import com.hypebeast.sdk.BuildConfig;
import com.hypebeast.sdk.Util.UrlCache;
import com.hypebeast.sdk.api.exception.NotFoundException;
import com.hypebeast.sdk.api.realm.hbx.rProduct;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bolts.CancellationToken;
import bolts.Task;
import bolts.TaskCompletionSource;
import io.realm.Realm;
import io.realm.RealmConfiguration;

import static com.hypebeast.sdk.Constants.APP_CSS_FILE_PATH;
import static com.hypebeast.sdk.Constants.APP_FOLDER_NAME;
import static com.hypebeast.sdk.Constants.PREFERENCE_CSS_FILE_CONTENT;

import static com.hypebeast.sdk.Constants.*;

/**
 * Created by hesk on 18/11/15.
 */
public abstract class ApplicationBase {

    protected final Application app;
    protected final SharedPreferences sharedPreferences;
    public static final String INSTALLATION_VERSION = "version_sdk";
    public static final String EMPTY_FIELD = "none";
    public static final String DEFAULT_DICTIONARY = "default_dictionary";
    protected String debug_version;
    protected ArrayList<String> default_dictionary_list = new ArrayList<>();


    public ApplicationBase(Application app) {
        this.app = app;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(app);

    }

    protected void init() {
        checkDataVersioning();
    }

    protected String loadRef(final String tag) {
        String data = sharedPreferences.getString(tag, EMPTY_FIELD);
        if (data.equalsIgnoreCase("")) {
            return EMPTY_FIELD;
        } else
            return data;
    }

    protected void saveInfo(final String tag, final String data) {
        sharedPreferences.edit()
                .putString(tag, data)
                .apply();
    }

    protected void saveInt(final String tag, final int data) {
        sharedPreferences.edit()
                .putInt(tag, data)
                .apply();
    }

    protected abstract void removeAllData();

    protected void checkDataVersioning() {
        if (sharedPreferences.getInt(INSTALLATION_VERSION, -1) == -1) {
            // there is no data set
            saveInt(INSTALLATION_VERSION, BuildConfig.VERSION_CODE);
            debug_version = "there is no data set";
        } else if (sharedPreferences.getInt(INSTALLATION_VERSION, -1) < BuildConfig.VERSION_CODE) {
            // the upgrade data is needed
            removeAllData();
            saveInt(INSTALLATION_VERSION, BuildConfig.VERSION_CODE);
            debug_version = "the upgrade data is needed";
        } else if (sharedPreferences.getInt(INSTALLATION_VERSION, -1) > BuildConfig.VERSION_CODE) {
            //the app is older and the data is for the newer version
            removeAllData();
            saveInt(INSTALLATION_VERSION, BuildConfig.VERSION_CODE);
            debug_version = "the app is older and the data is for the newer version";
        } else {
            //same normal data set
            debug_version = "normal data set";
        }
    }

    protected void remove_file(String folder_name, String file_name) {
        final String root = Environment.getExternalStorageDirectory().toString() + File.separator;
        final File myDir = new File(root + folder_name + File.separator + file_name);
        if (myDir.exists()) {
            myDir.delete();
        }
    }

    private String copyBundledRealmFile(InputStream inputStream, String outFileName) {
        try {
            File file = new File(app.getFilesDir(), outFileName);
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, bytesRead);
            }
            outputStream.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * save dictionary for a group of keywords
     *
     * @param tag  the tag name
     * @param list the list string array
     */
    protected void save_dictionary(String tag, ArrayList<String> list) {
        if (list.size() > 0) {
            String singleString = list.toString();
            saveInfo(tag, singleString);
        }
    }

    /**
     * loading dictionary keywords
     *
     * @param tag the tag name
     * @return the list of strings
     * @throws NotFoundException the not found exception
     */
    protected List<String> load_dictionary(String tag) throws NotFoundException {
        if (loadRef(tag).equalsIgnoreCase(EMPTY_FIELD))
            throw new NotFoundException("There is no data found from the field " + tag);
        final String block = loadRef(tag);
        List<String> res_str = Arrays.asList(block.substring(0, block.length() - 1).substring(1).split("[\\s,]+"));
        return res_str;
    }

    /**
     * automatically save the dictionary list
     */
    public void save_dictionary_auto() {
        save_dictionary(DEFAULT_DICTIONARY, default_dictionary_list);
    }

    /**
     * automatically load the list of the keywords from the dictionary
     */
    public void load_dictionary_auto() {
        try {
            load_dictionary(DEFAULT_DICTIONARY);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }

    public void addKeyword(String keyword) {
        if (default_dictionary_list.indexOf(keyword) == -1) {
            default_dictionary_list.add(keyword);
        }
    }

    public void removeKeyword(String keyword) {
        if (default_dictionary_list.indexOf(keyword) > -1) {
            default_dictionary_list.remove(default_dictionary_list.indexOf(keyword));
        }
    }


    /*
    blocking tasking in here
     */
    public Task<Void> setCssFile(final String access_endpoint, String local_css_name) {
        TaskCompletionSource<Void> successful = new TaskCompletionSource<>();
        StringWriter writer = new StringWriter();
        final String root = Environment.getExternalStorageDirectory().toString() + File.separator;
        final String path_final = root + APP_FOLDER_NAME;
        final File myDir = new File(path_final);
        UrlCache mUrlCache = new UrlCache(app, myDir);
        mUrlCache.register(access_endpoint, local_css_name, "text/css", "UTF-8", 5 * UrlCache.ONE_DAY);
        //  SharedPreferences share = PreferenceManager.getDefaultSharedPreferences(app);
        if (loadRef(APP_CSS_FILE_PATH).equalsIgnoreCase(EMPTY_FIELD)) {
            WebResourceResponse loadedcontent = mUrlCache.load();
            if (loadedcontent != null) {
                try {
                    IOUtils.copy(loadedcontent.getData(), writer, "UTF-8");
                    saveInfo(PREFERENCE_CSS_FILE_CONTENT, writer.toString());
                    saveInfo(APP_CSS_FILE_PATH, path_final);
                    successful.setResult(null);
                } catch (IOException e) {
                    successful.setError(e);
                }
            } else {
                successful.setError(new Exception(mUrlCache.getErrorMessage()));
            }
        } else {
            /**
             * there is a file already saved
             */
            successful.setResult(null);
        }
        return successful.getTask();
    }

    public Task<String> getIntAsync(final CancellationToken ct) {
        // Create a new Task
        final TaskCompletionSource<String> tcs = new TaskCompletionSource<>();
        new Thread() {
            @Override
            public void run() {
                // Check if cancelled at start
                if (ct.isCancellationRequested()) {
                    tcs.setCancelled();
                    return;
                }

                int result = 0;
                while (result < 100) {
                    // Poll isCancellationRequested in a loop
                    if (ct.isCancellationRequested()) {
                        tcs.setCancelled();
                        return;
                    }
                    result++;
                }
                tcs.setResult("done");
            }
        }.start();

        return tcs.getTask();
    }
}
