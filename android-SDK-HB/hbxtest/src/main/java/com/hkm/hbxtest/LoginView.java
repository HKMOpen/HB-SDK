package com.hkm.hbxtest;

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
public class LoginView extends BasicWebViewNormal implements WishlistSync.syncResult {
    @Override
    protected int LayoutID() {
        return R.layout.login_screen;
    }

    Button logout_b, wish_list_b;
    TextView console;

    @Override
    protected void initBinding(View v) {
        betterCircleBar = (CircleProgressBar) v.findViewById(com.hkm.ezwebview.R.id.wv_simple_process);
        logout_b = (Button) v.findViewById(R.id.logoutbutton);
        wish_list_b = (Button) v.findViewById(R.id.getwishlist);
        console = (TextView) v.findViewById(R.id.console);
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
                HBStoreApiClient
                        .getInstance(getActivity())
                        .setInterceptWishListProcess(LoginView.this)
                        .requestWishList();

                ViewCompat.animate(betterCircleBar).alpha(1f);
            }
        });

    }

    private void addMessage(final String mMessage) {
        console.setText(console.getText().toString() + "\n" + mMessage);
    }

    @Override
    public void successDownStream(List<wish> wistlist) {
        String message = "down done! and there is " + wistlist.size() + " items found";
        ErrorMessage.alert(message, getFragmentManager());
        addMessage(message);
        completeloading();
    }

    @Override
    public void successUpStream() {
        String message = "up done! and there is";
        ErrorMessage.alert(message, getFragmentManager());
        addMessage(message);
        completeloading();
    }

    @Override
    public void failure(String error_message_out) {
        ErrorMessage.alert(error_message_out, getFragmentManager());
        completeloading();
    }
}
