package com.hkm.hbstore;

import android.app.Fragment;
import android.support.annotation.IdRes;

import com.hkm.hbstore.pages.template_single;
import com.hkm.slidingmenulib.layoutdesigns.singleDetailPost;

/**
 * Created by hesk on 2/7/15.
 */
public class DetailProduct extends singleDetailPost<template_single> {
    /**
     * setting the first initial fragment at the beginning
     *
     * @return generic type fragment
     */
    @Override
    protected template_single getInitFragment() throws Exception {
        if (getIntent().getExtras() == null)
            throw new Exception("there is no url request from the incoming content");

        if (getIntent().getExtras().getString(requestURL, "").equalsIgnoreCase(""))
            throw new Exception("there is no url request from the incoming content");


        return template_single.newInstance(getIntent().getExtras().getString(requestURL));
    }

    @Override
    protected void onMenuItemSelected(@IdRes int Id) {

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
