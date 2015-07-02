package com.hkm.hbstore.menu;

import android.view.View;

import com.hkm.hbstore.R;
import com.hkm.hbstore.pages.tab_controller;
import com.hkm.slidingmenulib.menucontent.LAYOUT_DRAWER;
import com.hkm.slidingmenulib.menucontent.MenuBuildHelper;
import com.hkm.slidingmenulib.menucontent.MaterialMenu;
import com.hkm.slidingmenulib.menucontent.sectionPlate.MaterialSection;
import com.hkm.slidingmenulib.menucontent.materialMenuConstructorFragmentBase;

import java.util.List;
import java.util.Random;

/**
 * Created by hesk on 23/6/15.
 * This is the One In the Dark Only
 */
public class mainMenuModule extends materialMenuConstructorFragmentBase<SectionSubMenu, SubItem, RendeBind> {
    @Override
    protected void configurationNavigation(MenuBuildHelper navi) {

    }

    protected LAYOUT_DRAWER getLayoutId() {
        return LAYOUT_DRAWER.STICKY_UP;
    }

    @Override
    protected void configurationMenu(MaterialMenu menu) {
        // NavBuilder().newImageLabel("home", ContextCompat.getDrawable(getActivity(), R.mipmap.cross_mi), menu);
        NavBuilder().newImageLabelSticky("home", R.mipmap.ic_action_hb_icon_256x, menu);
        NavBuilder().DividerBigSticky(menu);
        NavBuilder().newSectionSticky(R.string.loginandregister, new tab_controller(), menu);
        NavBuilder().DividerSmallSticky(menu);
        NavBuilder().newSectionSticky(R.string.newarrivals, new tab_controller(), menu);
        NavBuilder().DividerSmallSticky(menu);
        NavBuilder().newSectionSticky(R.string.backinstock, new tab_controller(), menu);
        NavBuilder().DividerSmallSticky(menu);
        NavBuilder().newSectionSticky(R.string.brands, new tab_controller(), menu);
        NavBuilder().DividerSmallSticky(menu);
        NavBuilder().newListSection(R.string.clothing, withmenu(), menu);


        NavBuilder().DividerSmall(menu);
        NavBuilder().newListSection(R.string.accessories, withmenu(), menu);
        NavBuilder().DividerSmall(menu);
        NavBuilder().newListSection(R.string.shoes, withmenu(), menu);


        NavBuilder().DividerSmall(menu);
        NavBuilder().newListSection(R.string.backinstock, withmenu(), menu);
        NavBuilder().DividerSmall(menu);
        NavBuilder().newListSection(R.string.brands, withmenu(), menu);

        NavBuilder().DividerSmall(menu);
        NavBuilder().newListSection(R.string.reg_notice, withmenu(), menu);
        NavBuilder().DividerSmall(menu);
        NavBuilder().newListSection(R.string.homeandtech, withmenu(), menu);

        NavBuilder().DividerSmall(menu);
        NavBuilder().newListSection(R.string.grooming, withmenu(), menu);
        NavBuilder().DividerSmall(menu);
        NavBuilder().newListSection(R.string.giftcards, withmenu(), menu);
    }

    @Override
    protected RendeBind newBinderRenderForListSection(List<SubItem> data) {
        return new RendeBind(data);
    }

    @Override
    protected SubItem genNewSampleDataobject(Random e, int atPos) {
        return new SubItem(mSampleData[8][0], "http://sample");
    }

    @Override
    public void onBeforeChangeSection(MaterialSection newSection) {

    }

    @Override
    public void onAfterChangeSection(MaterialSection newSection) {

    }

    @Override
    public void onClick(MaterialSection section, View v) {

    }
}
