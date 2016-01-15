package com.hkm.hbxtest;

import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hkm.ezwebview.app.BasicWebViewNormal;
import com.hkm.hbxtest.Dialog.ErrorMessage;
import com.hypebeast.sdk.api.model.symfony.Product;
import com.hypebeast.sdk.api.model.symfony.wish;
import com.hypebeast.sdk.application.hbx.WishlistSync;
import com.hypebeast.sdk.clients.HBStoreApiClient;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import java.util.List;

/**
 * Created by hesk on 10/12/15.
 */
public class login_view extends BasicWebViewNormal implements WishlistSync.syncResult {
    @Override
    protected int LayoutID() {
        return R.layout.login_screen;
    }

    Button logout_b, wish_list_b, store_products, upsync;
    TextView console;
    Handler mHandler = new Handler();
    HBStoreApiClient client;

    @Override
    protected void initBinding(View v) {
        client = HBStoreApiClient.getInstance(getActivity());
        betterCircleBar = (CircleProgressBar) v.findViewById(com.hkm.ezwebview.R.id.wv_simple_process);
        logout_b = (Button) v.findViewById(R.id.logoutbutton);
        wish_list_b = (Button) v.findViewById(R.id.getwishlist);
        store_products = (Button) v.findViewById(R.id.store_products_w);
        upsync = (Button) v.findViewById(R.id.up_sync);
        console = (TextView) v.findViewById(R.id.console);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                completeloading();
            }
        }, 100);

    }

    private void runbind() {
        logout_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HBStoreApiClient.getInstance(getActivity()).logout();
                ErrorMessage.alert("you have logged out now!!", getFragmentManager());
            }
        });


        wish_list_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewCompat.animate(betterCircleBar).alpha(1f).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        client
                                .setInterceptWishListProcess(login_view.this)
                                .requestWishList();

                    }
                });
            }
        });

        store_products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewCompat.animate(betterCircleBar).alpha(1f).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        client
                                .store_all_items_to_wishlist_from_uri(
                                        "http://store.hypebeast.com/categories/accessories/bags",
                                        new HBStoreApiClient.store_all_items_mock() {
                                            @Override
                                            public void success(List<Product> list, int total_items_processed, String URL) {
                                                String mms = "the stream of products has been downloaded." + URL + ". processed total items of " + total_items_processed + " added to the wish list. Retrieved items: " + list.size();
                                                common_process_done("SUCCESS:" + mms, false);
                                            }

                                            @Override
                                            public void falilure(String failed_message) {
                                                String mms = "error: " + failed_message;
                                                common_process_done(mms, false);
                                            }
                                        });

                    }
                });
            }
        });

        upsync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewCompat.animate(betterCircleBar).alpha(1f).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        client
                                .setInterceptWishListProcess(login_view.this)
                                .requestUpSyncWishList();

                    }
                });
            }
        });
    }


    private void common_process_done(final String message, final boolean withDialog) {
        if (withDialog) ErrorMessage.alert(message, getFragmentManager());
        addMessage(message);
        completeloading();
    }

    @Override
    protected void completeloading() {
        ViewCompat.animate(betterCircleBar).alpha(0f).withEndAction(new Runnable() {
            @Override
            public void run() {
                runbind();
            }
        });
    }

    private void addMessage(final String mMessage) {
        console.setText(console.getText().toString() + "\n" + mMessage);
    }

    @Override
    public void successDownStream(List<wish> wistlist) {
        String message = "down done! and there is " + wistlist.size() + " items found";
        common_process_done(message, false);
    }

    @Override
    public void successUpStream(final int skipped) {
        String message = "up done! and there is " + skipped + " items on hold";
        common_process_done(message, false);
    }

    @Override
    public void failure(String error_message_out) {
        ErrorMessage.alert(error_message_out, getFragmentManager());
        completeloading();
    }


}
