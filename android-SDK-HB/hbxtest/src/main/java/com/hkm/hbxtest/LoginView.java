package com.hkm.hbxtest;

import android.view.View;
import android.widget.Button;

import com.hkm.ezwebview.app.BasicWebViewNormal;
import com.hkm.hbxtest.Dialog.ErrorMessage;
import com.hypebeast.sdk.clients.HBStoreApiClient;

/**
 * Created by hesk on 10/12/15.
 */
public class LoginView extends BasicWebViewNormal {
    @Override
    protected int LayoutID() {
        return R.layout.login_screen;
    }

    Button logout_b;

    @Override
    protected void initBinding(View v) {
        logout_b = (Button) v.findViewById(R.id.logoutbutton);


        logout_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HBStoreApiClient.getInstance(getActivity()).logout();
                ErrorMessage.alert("you have logged out now!!", getFragmentManager());
            }
        });
    }
}
