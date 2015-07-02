package com.hkm.hbstore;

import android.app.Fragment;
import android.os.Bundle;
import android.view.MenuItem;

import com.hkm.hbstore.pages.tab_controller;
import com.hkm.hbstore.menu.mainMenuModule;
import com.hkm.slidingmenulib.gestured.SlidingMenu;
import com.hkm.slidingmenulib.layoutdesigns.app.SlidingAppCompactActivity;

/**
 * Created by hesk on 18/6/15.
 */
public class StoreStartNew extends SlidingAppCompactActivity<Fragment> {

    @Override
    protected mainMenuModule getFirstMenuFragment() {
        return new mainMenuModule();
    }

    @Override
    protected void customizeSlideMenuEdge(SlidingMenu sm) {
        sm.setFadeDegree(0.35f);
        sm.setMode(SlidingMenu.LEFT);
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        sm.setBehindScrollScale(0.5f);
        sm.setFadeDegree(0.34f);
        sm.setBehindWidth(800);
        sm.requestLayout();
        sm.invalidate();
    }

    @Override
    protected tab_controller getInitFragment() {
        return new tab_controller();
    }

    @Override
    protected int getDefaultMainActivityLayoutId() {
        return BODY_LAYOUT.actionbar.getResID();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                toggle();
                return true;
            // case R.id.github:
            //    Util.goToGitHub(this);
            //    return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
