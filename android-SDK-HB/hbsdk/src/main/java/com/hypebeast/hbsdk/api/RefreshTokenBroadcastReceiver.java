package com.hypebeast.hbsdk.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hypebeast.hbsdk.clients.AuthMgr;


public class RefreshTokenBroadcastReceiver extends BroadcastReceiver {


  private AuthMgr authManager;

    public RefreshTokenBroadcastReceiver() {
        //DisqusSdkProvider.getInstance().getObjectGraph().inject(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {


        authManager.postRefreshTokenAsync();
    }
}
