package com.hkm.hbstore.pages;

import android.util.Log;

import com.hkm.slidingmenulib.layoutdesigns.fragment.menubigbanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by hesk on 29/6/15.
 */
public class template_home extends menubigbanner<menubigbanner.udp> {


    @Override
    protected void onClickItem(String route) {
        Log.d(TAG, route);
    }

    @Override
    protected menubigbanner.udp getAdatperWithdata() {
        return new udp(getlistsource());
    }

    private static final String[] samples = new String[]
            {
                    "diddy-unveils-a-custom-pair-of-yeezy-boost-350s-1.jpg",
                    "nike-sb-dunk-high-premium-familia-1.jpg", "shoes2390.jpeg"
            };

    protected String getlocalsample() {
        Random r = new Random();
        return "file:///android_asset/sampleimages/" + samples[r.nextInt(samples.length)];
    }

    protected List<datab> getlistsource() {
        final List<datab> ed = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            datab g = new datab();
            g.setAction_route("soinfisneoif");
            g.setImage_url(getlocalsample());
            ed.add(g);
        }
        return ed;
    }
}
