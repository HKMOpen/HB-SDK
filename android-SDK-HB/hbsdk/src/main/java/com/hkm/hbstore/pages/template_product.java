package com.hkm.hbstore.pages;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.hkm.hbstore.DetailProduct;
import com.hkm.hbstore.R;
import com.hkm.hbstore.life.Config;
import com.hkm.hbstore.life.LifeCycleApp;
import com.hkm.slidingmenulib.layoutdesigns.fragment.catelog;
import com.hkm.slidingmenulib.layoutdesigns.singleDetailPost;
import com.hkm.slidingmenulib.menucontent.sectionPlate.touchItems.midUltimateAdapter;
import com.hypebeast.hbsdk.api.exception.ApiException;
import com.hypebeast.hbsdk.api.model.commonterms.Product;
import com.hypebeast.hbsdk.api.model.head.ReponseNormal;
import com.hypebeast.hbsdk.api.resources.hbstore.Products;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.neopixl.pixlui.components.textview.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by hesk on 30/6/15.
 */
public class template_product extends catelog<template_product.cateadapter, template_product.binder> implements Callback<ReponseNormal> {

    private int current_page = 1;
    protected final Handler h = new Handler();
    final static int
            MAJOR = -1,
            MAJOR_FILTERED = -2,
            CATE = -5,
            CATE_FILTERED = -6,
            UNSET = -9,
            BROWSERABLE_FULL_URL = -7;
    protected String adapter_url, cate_title, slugtag;
    protected int requestType;


    private static final String[] samples = new String[]
            {
                    "diddy-unveils-a-custom-pair-of-yeezy-boost-350s-1.jpg",
                    "nike-sb-dunk-high-premium-familia-1.jpg",
                    "shoes2390.jpeg"
            };

    protected String getlocalsample() {
        Random r = new Random();
        return "file:///android_asset/sampleimages/" + samples[r.nextInt(samples.length)];
    }

    protected static Bundle con_cate(final String cate_item) {
        final Bundle n = new Bundle();
        n.putInt(template_product.REQUEST_TYPE, template_product.CATE);
        n.putString(template_product.SLUG, cate_item);
        return n;
    }

    protected static Bundle con_major(final String major_tab) {
        final Bundle n = new Bundle();
        n.putInt(template_product.REQUEST_TYPE, template_product.MAJOR);
        n.putString(template_product.SLUG, major_tab);
        return n;
    }

    protected List<Product> getlistsource() {
        final List<Product> ed = new ArrayList<>();
        /*
        for (int i = 0; i < 30; i++) {
            template_product.datab g = new template_product.datab();
            g.setAction_route("soinfisneoif");
            g.setImage_url(getlocalsample());
            ed.add(g);
        } */
        return ed;
    }


    @Override
    protected boolean onArguments(Bundle r) {
        requestType = r.getInt(REQUEST_TYPE, UNSET);
        adapter_url = r.getString(URL, "");
        slugtag = r.getString(SLUG, "");
        cate_title = r.getString(FRAGMENTTITLE, "");
        return !adapter_url.equalsIgnoreCase("") || requestType != UNSET;
    }

    @Override
    protected void onClickItem(final String route) {
        Log.d(TAG, route + " now");
        Intent n = new Intent(getActivity(), DetailProduct.class);
        final Bundle b = new Bundle();
        b.putString(singleDetailPost.requestURL, route);
        getActivity().startActivity(n);
    }

    @Override
    protected int getColumn() {
        return 2;
    }

    @Override
    protected cateadapter getAdatperWithdata() {
        return new cateadapter(getlistsource());
    }

    protected Products interfacerequest;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        interfacerequest = ((LifeCycleApp) activity.getApplication()).getRequestProducts();
    }


    @Override
    protected void setUltimateRecyclerViewExtra(final UltimateRecyclerView listview, final cateadapter madapter) {
        listview.setClipToPadding(false);
        listview.setOnLoadMoreListener(new UltimateRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMore(int i, int i1) {
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // if (mLayoutManager.canScrollVertically()) {
                        /*    mLayoutManager.smoothScrollToPosition(listview.mRecyclerView, null,
                            madapter.getItemCount() - 2);*/
                        Log.d(TAG, "count:" + madapter.getItemCount());
                        current_page = current_page < Config.pageLimit ? ++current_page : current_page;
                        final int total = madapter.getItemCount();
                        try {

                            if (requestType == MAJOR) {
                                interfacerequest.mainList(slugtag, current_page, Config.pageLimit, template_product.this);
                            } else if (requestType == CATE) {
                                interfacerequest.bycate(slugtag, current_page, Config.pageLimit, template_product.this);
                            }

                        } catch (ApiException e) {
                            e.printStackTrace();
                        }
                    }
                }, 3000);

            }
        });
        listview.enableLoadmore();
        madapter.setCustomLoadMoreView(LayoutInflater.from(getActivity())
                .inflate(R.layout.custom_bottom_progressbar, null));

    }

    @Override
    protected void loadDataInitial(final cateadapter adapter) {
        Log.d(TAG, "load data: " + " external now");
        try {
            if (requestType == MAJOR) {
                interfacerequest.mainList(slugtag, 1, Config.pageLimit, this);
            } else if (requestType == CATE) {
                interfacerequest.bycate(slugtag, 1, Config.pageLimit, this);
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Successful HTTP response.
     *
     * @param reponseNormal the normnal return
     * @param response      the response in object
     */
    @Override
    public void success(ReponseNormal reponseNormal, Response response) {
        try {
            final List<Product> list = reponseNormal.product_list.getlist();
            Iterator<Product> e = list.iterator();
            while (e.hasNext()) {
                Product g = e.next();
                madapter.insert(g);
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        // Log.d(TAG, e.toString());
    }

    /**
     * Unsuccessful HTTP response due to network failure, non-2XX status code, or unexpected
     * exception.
     *
     * @param error the error object
     */
    @Override
    public void failure(RetrofitError error) {
        Log.d(TAG, error.getMessage());
    }


    public static class binder extends UltimateRecyclerviewViewHolder {
        public final ImageView im;
        public final TextView tvtitle, tvdesc, tvprice;

        public binder(View itemView) {
            super(itemView);
            im = (ImageView) itemView.findViewById(R.id.imageholder);
            tvtitle = (TextView) itemView.findViewById(R.id.product_title);
            tvdesc = (TextView) itemView.findViewById(R.id.product_short_desc);
            tvprice = (TextView) itemView.findViewById(R.id.final_price);
        }
    }

    public class cateadapter extends midUltimateAdapter<Product, binder> {

        /**
         * dynamic object to start
         *
         * @param list the list source
         */
        public cateadapter(List list) {
            super(list);
        }

        /**
         * the layout id for the normal data
         *
         * @return the ID
         */
        @Override
        protected int getNormalLayoutResId() {
            return R.layout.product_cate_item_display;
        }

        @Override
        protected template_product.binder newViewHolder(View view) {
            return new template_product.binder(view);
        }

        @Override
        protected void withBindHolder(final binder holder, final Product data, int position) {


            picasso.load(data.get_cover_image()).into(holder.im);
            holder.im.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickItem(data.getSingleEndPoint());
                }
            });
            holder.tvtitle.setText(data.get_brand_name());
            holder.tvdesc.setText(data.getTitle());
            holder.tvprice.setText(data.price());
        }
    }


    public class sample {
        String image_url;
        String action_route;

        public sample() {

        }

        public void setImage_url(String url) {
            image_url = url;
        }

        public void setAction_route(String route) {
            action_route = route;
        }

        public String getImage_url() {
            return image_url;
        }

        public String getAction_route() {
            return action_route;
        }
    }
}
