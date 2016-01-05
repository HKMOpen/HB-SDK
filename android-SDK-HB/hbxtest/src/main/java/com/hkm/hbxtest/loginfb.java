package com.hkm.hbxtest;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import com.hkm.hbxtest.Dialog.ErrorMessage;
import com.hypebeast.sdk.clients.HBStoreApiClient;


import com.hkm.ezwebview.Util.Fx9C;
import com.hkm.ezwebview.Util.In32;
import com.hkm.ezwebview.app.BasicWebViewNormal;
import com.hkm.ezwebview.webviewclients.PaymentClient;

/**
 * -- 10/12/15.
 * -- 28/10/15.
 */
public class loginfb extends BasicWebViewNormal {
    public final static String URL_TAG = "REMOTE_URL";

    protected <T extends PaymentClient> void setup_payment_gateway(T pay, String full_url) {
        Fx9C.setup_payment_gateway(pay, framer, block, betterCircleBar, full_url, "HypebeastStoreApp/1.0", 1600);
    }

    public void refresh_cart() {
        if (block != null) {
            String root = block.getOriginalUrl();
            block.loadUrl(root);
        }
    }

    public static Bundle getURL(String url_initial) {
        final Bundle b = new Bundle();
        b.putString(URL_TAG, url_initial);
        return b;
    }

    public static loginfb newInstance(Bundle b) {
        final loginfb wl = new loginfb();
        wl.setArguments(b);
        return wl;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle b = getArguments();
        String url = b.getString(URL_TAG, "");
        final login client = new login(getActivity(), block);
        setup_payment_gateway(client, url);
    }

    protected void saveToken(Uri loc) {
        try {
            HBStoreApiClient.getInstance(getActivity()).saveTokenAfterSuccessLogin();
            ErrorMessage.alert(getString(R.string.login_notice_success), getChildFragmentManager(), new Runnable() {
                @Override
                public void run() {
                    getFragmentManager().beginTransaction().replace(R.id.contentholder,
                            new LoginView(), "afterlogin")
                            .addToBackStack(null)
                            .commit();
                }
            });
        } catch (NullPointerException e) {
            Log.d("loginHBX", "unable to save info =" + e.getMessage());
        }
    }

    private class login extends PaymentClient {


        public login(Activity context, WebView fmWebView) {
            super(context, fmWebView);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(TAG, "redirect URI =" + url);
            if (url.equalsIgnoreCase(getString(R.string.url_store_domain))) {
                saveToken(Uri.parse(url));
                return true;
            }
            return false;
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            // Log.i(TAG, "shouldInterceptRequest path:" + request.getUrl().getPath());
            Log.i(TAG, "header info =" + request.getRequestHeaders().toString());
            WebResourceResponse returnResponse = null;
            return returnResponse;
        }
    }


}
