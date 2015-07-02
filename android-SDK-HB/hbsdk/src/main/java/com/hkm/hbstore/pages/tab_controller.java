package com.hkm.hbstore.pages;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.hkm.hbstore.R;
import com.hkm.hbstore.adapters.ItemList;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v13.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v13.FragmentPagerItems;

import java.lang.reflect.Field;

/**
 * Created by hesk on 21/4/15.
 */
public class tab_controller extends Fragment {

    private SmartTabLayout mTab;
    private ViewPager pager;
    private ItemList pagerAdapter;
    private FragmentManager mChildFragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.homepage, container, false);
    }

    public FragmentPagerItemAdapter getAdapter() {
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), FragmentPagerItems.with(getActivity())


                .add(tB.home.getStringId(),
                        tB.home.getClazz())

                .add(tB.newarrivals.getStringId(),
                        tB.newarrivals.getClazz(),
                        tB.newarrivals.bundle())

                .add(tB.backinstock.getStringId(),
                        tB.backinstock.getClazz(),
                        tB.backinstock.bundle())

                .add(tB.brand.getStringId(),
                        tB.brand.getClazz(),
                        tB.brand.bundle())


                .add(tB.sale.getStringId(),
                        tB.sale.getClazz(),
                        tB.sale.bundle())


                .add(R.string.tabpage6, testpage.class)
                .create());

        return adapter;
    }

    @SuppressLint("ResourceAsColor")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onViewCreated(View v, Bundle b) {
        mTab = (SmartTabLayout) v.findViewById(R.id.materialTabHost);
        pager = (ViewPager) v.findViewById(R.id.viewpager);
        pager.setAdapter(getAdapter());
        pager.setOffscreenPageLimit(10);
        mTab.setViewPager(pager);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


}
