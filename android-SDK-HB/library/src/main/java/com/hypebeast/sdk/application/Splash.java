package com.hypebeast.sdk.application;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.greysonparrelli.permiso.Permiso;

/**
 * Created by hesk on 18/11/15.
 */
public abstract class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Permiso.getInstance().setActivity(this);
        this.setContentView(getLayoutId());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            V23permission_request();
        } else {
            synchronizeData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Permiso.getInstance().setActivity(this);
    }

    protected abstract void onPermissionGranted();

    protected abstract void onPermissionDenied();

    protected abstract void synchronizeData();

    @LayoutRes
    protected abstract int getLayoutId();

    protected void V23permission_request() {
        Permiso.getInstance().requestPermissions(
                new Permiso.IOnPermissionResult() {
                    @Override
                    public void onPermissionResult(Permiso.ResultSet resultSet) {
                        if (resultSet.areAllPermissionsGranted()) {
                            // Permission granted!
                            onPermissionGranted();
                        } else {
                            // Permission denied.
                            onPermissionDenied();
                        }
                    }

                    @Override
                    public void onRationaleRequested(Permiso.IOnRationaleProvided callback, String... permissions) {
                        Permiso.getInstance().showRationaleInDialog("Request permissions", permission_message(), null, callback);
                    }
                },
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE

        );
    }

    protected String permission_message() {
        return "You need to grant those permissions";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permiso.getInstance().onRequestPermissionResult(requestCode, permissions, grantResults);
    }
}
