package com.hkm.hbstore.pages;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.StringRes;

import com.hkm.hbstore.R;

/**
 * Created by hesk on 2/7/15.
 */
public enum tB {
    home(R.string.tabpage1, template_home.class) {
        @Override
        protected Bundle bundle() {
            return null;
        }
    },
    newarrivals(R.string.tabpage2, template_product.class) {
        @Override
        protected Bundle bundle() {
            return template_product.con_major("new-arrivals");
        }
    },
    backinstock(R.string.tabpage3, template_product.class) {
        @Override
        protected Bundle bundle() {
            return template_product.con_major("back-in-stock");
        }
    },
    sale(R.string.tabpage5, template_product.class) {
        @Override
        protected Bundle bundle() {
            return template_product.con_major("sale");
        }
    },
    brand(R.string.brands, template_product.class) {
        @Override
        protected Bundle bundle() {
            return template_product.con_major("new-arrivals");
        }
    };

    private final Class<? extends Fragment> clazz;
    private final int name_title;

    tB(final @StringRes int name_title, final Class<? extends Fragment> clazz) {
        this.clazz = clazz;
        this.name_title = name_title;

    }

    protected abstract Bundle bundle();

    public Class<? extends Fragment> getClazz() {
        return clazz;
    }

    public int getStringId() {
        return name_title;
    }
}
