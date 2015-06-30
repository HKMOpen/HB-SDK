package com.hypebeast.hbsdk.application;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hypebeast.hbsdk.DisqusConstants;
import com.hypebeast.hbsdk.R;
import com.hypebeast.hbsdk.api.modelzero.oauth2.AccessToken;
import com.hypebeast.hbsdk.clients.ApiConfig;
import com.hypebeast.hbsdk.clients.AuthMgr;


/**
 * Created by hesk on 21/5/15.
 */
public abstract class AuthorizeFragment extends Fragment {
    /**
     * on press
     */
    private static final String allowBUttonPressUrl = "https://disqus.com/api/oauth/2.0/grant/";
    /**
     * Logging tag
     */
    protected static final String TAG = AuthorizeFragment.class.getName();

    /**
     * Arguments
     */
    protected static final String ARG_API_KEY = "api_key";
    protected static final String ARG_SCOPES = "scope";
    protected static final String ARG_REDIRECT_URI = "redirect_uri";
    protected static final String ARG_SECRET = "secret";

    protected ACTION maction;

    enum ACTION {
        REFRESH_TOKEN, STEP1, STEP2
    }

    /**
     * The Disqus API key
     */
    private String mApiKey;
    /**
     * The Disqus secret id
     */
    private String mSecret;

    /**
     * Scopes
     */
    private String[] mScopes;

    /**
     * Authorize redirect URI
     */
    private String mRedirectUri;

    /**
     * Authorization listener
     */
    // private AuthorizeListener mListener;

    /**
     * A {@link WebView} to display the Disqus login
     */
    private WebView mWebView;
    private boolean isDialogShown = false;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mWebView = (WebView) view.findViewById(R.id.disqus_login_webview);
        routeAuth();
        isDialogShown = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.loginwebview, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_API_KEY)) {
            mApiKey = getArguments().getString(ARG_API_KEY);
        }
        if (getArguments().containsKey(ARG_SCOPES)) {
            mScopes = getArguments().getStringArray(ARG_SCOPES);
        }
        if (getArguments().containsKey(ARG_REDIRECT_URI)) {
            mRedirectUri = getArguments().getString(ARG_REDIRECT_URI);
        }
        if (getArguments().containsKey(ARG_SECRET)) {
            mSecret = getArguments().getString(ARG_SECRET);
        }
    }

    //automatically attached;
    private AuthMgr authentication_manager;
    private AuthorizeActivity getListenercontext;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            getListenercontext = (AuthorizeActivity) activity;
            //  mListener = (AuthorizeListener) activity;
            authentication_manager = getListenercontext.getManager();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement AuthorizeCallback");
        }
    }

    protected abstract String getNativeCallBack();

    protected abstract ApiConfig getConf();

    private void dislogProcessNotice() {
        if (!isDialogShown) {
            ProgressDialog.show(getActivity(), "one moment", "progressing");
            isDialogShown = true;
        }
    }

    private boolean usewebview = false;

    private void routeAuth() {
        // Setup custom client to catch the redirect
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                final String coderequeststart = mRedirectUri + DisqusConstants.authorizeCode;
                Log.d(TAG, "base change: " + url);
                if (url.startsWith(coderequeststart)) {
                    Log.d("", "Got Url " + url);
                    Uri uri = Uri.parse(url);
                    final String code = uri.getQueryParameter("code");
                    Log.d(TAG, "Acquiring Code: " + code);


                    authentication_manager.authorizeAsync(code);


                   // mWebView.postUrl(DisqusConstants.AUTHORIZE_ACCESS_TOKEN, EncodingUtils.getBytes(AuthorizeUtils.buildCodeRequestJustBody(code, mApiKey, mSecret, mRedirectUri), "BASE64"));


                    return true;
                } else if (url.startsWith(allowBUttonPressUrl)) {
                    dislogProcessNotice();
                    //continue
                } else if (url.startsWith(mRedirectUri)) {
                    // Get fragment from url
                    Log.d(TAG, "Acquiring Processing redirect: " + url);
                    //  mListener.onSuccess(AuthorizeUtils.getDataToken(url));
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        Uri uri = AuthorizeUtils.buildAuthorizeUri(mApiKey, getScopes(), mRedirectUri);
        String fftoken = "Loading authorize url: " + uri.toString();
        Log.d(TAG, fftoken);
        mWebView.loadUrl(uri.toString());
    }


    /**
     * Load authorize url
     *
     * @return the string
     */
    private String getScopes() {
        String scope;
        try {
            scope = AuthorizeUtils.buildScope(mScopes);
        } catch (Exception e) {
            scope = "read,write";
        }
        return scope;
    }
    // abstract protected void posttextview(String something);

    /**
     * Listener interface, must be implemented by calling activity
     */
    public interface AuthorizeListener {

        void onSuccess(AccessToken accessToken);

        void onFailure();
    }


}