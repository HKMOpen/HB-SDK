package com.hypebeast.sdk.Util;

import android.annotation.TargetApi;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

/**
 * Created by hesk on 29/9/15.
 */
public class CookieHanger {
    protected String _raw_cookie;

    private String domain;
    private CookieManager instance;

    public CookieHanger() {
    }

    public static CookieHanger base(String url) {
        final CookieHanger c = new CookieHanger();
        c.setDomain(url);
        return c;
    }

    private void setDomain(String domain) {
        this.domain = domain;
        instance = CookieManager.getInstance();

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void invalidate() {
        instance.flush();
    }

    public String getRaw() {
        if (_raw_cookie == null) {
            _raw_cookie = instance.getCookie(this.domain);
            if (_raw_cookie == null) return "";
            else return _raw_cookie;
        } else return _raw_cookie;
    }

    public String getValue(String key_name) {
        _raw_cookie = instance.getCookie(this.domain);
        //System.out.println("cookie - " + cookie);
        final String[] _raw_cookie_array = _raw_cookie.split(";");
        for (String keyValue : _raw_cookie_array) {
            keyValue = keyValue.trim();
            String[] cookieSet = keyValue.split("=");
            final String cookie_key = cookieSet[0];
            final String cookie_val = cookieSet[1];
            if (cookie_key.trim().equalsIgnoreCase(key_name)) {
                return cookie_val.trim();
            }
        }
        return "";
    }

    public void set_cookie_value(final String session_id, final String session_token_val) {
        final StringBuilder sb = new StringBuilder();
        sb.append(session_id);
        sb.append("=");
        sb.append(session_token_val);
        sb.append(";");
        //Clear old cookies
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            System.out.println(CookieManager.getInstance().hasCookies());
            instance.removeAllCookies(null);
            instance.flush();
            instance.setAcceptCookie(true);
            instance.setCookie(domain, sb.toString());
            System.out.println(CookieManager.getInstance().hasCookies());
        } else {
            CookieManager.getInstance().removeAllCookie();
            CookieSyncManager.getInstance().sync();
            CookieManager.getInstance().setCookie(domain, session_id);
            //Save the two cookies: auth token and session info
            // List<Cookie> cookies = httpclient.getCookieStore().getCookies();
            // if (cookies != null) {
            //  for (Cookie cookie : cookies) {
            //  String cookieString = cookie.getName() + "=" + cookie.getValue() + "; Domain=" + cookie.getDomain();
            CookieManager.getInstance().setCookie(domain, sb.toString());
            //  }
            System.out.println(CookieManager.getInstance().hasCookies()); //Prints false in 2.3,  true in 4.0.3
            CookieSyncManager.getInstance().sync();
            System.out.println(CookieManager.getInstance().hasCookies()); //Also prints false in 2.3 and true in 4.0.3
        }
    }
}

