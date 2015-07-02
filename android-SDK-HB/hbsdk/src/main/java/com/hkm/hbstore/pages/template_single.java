package com.hkm.hbstore.pages;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hkm.hbstore.R;

/**
 * Created by hesk on 2/7/15.
 */
public class template_single extends Fragment {

    public static final String requestType = "REQUESTTYPE";
    public static int FULL_URL = -13, SLUG = -31;
    public static final String requestURL = "URL";
    public String location_url;

    public static template_single newInstance(final String full_url_product) {
        final Bundle b = new Bundle();
        b.putInt(requestType, FULL_URL);
        b.putString(requestURL, full_url_product);
        final template_single fragment = new template_single();
        fragment.setArguments(b);
        return fragment;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // listener = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.detail_product, container, false);
    }


}
