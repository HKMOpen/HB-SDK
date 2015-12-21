package com.hkm.hbxtest;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hypebeast.sdk.api.realm.hbx.rProduct;

import io.realm.Realm;

/**
 * Created by hesk on 21/12/15.
 */
public class databaseTest extends Fragment {

    private LinearLayout rootLayout;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }


    private String realmString(Realm realm) {
        StringBuilder stringBuilder = new StringBuilder();
        for (rProduct person : realm.allObjects(rProduct.class)) {
            stringBuilder.append(person.toString()).append("\n");
        }

        return (stringBuilder.length() == 0) ? "<data was deleted>" : stringBuilder.toString();
    }

    private void showStatus(Realm realm) {
        showStatus(realmString(realm));
    }

    private void showStatus(String txt) {
        Log.i("statustext", txt);
        TextView tv = new TextView(getActivity());
        tv.setText(txt);
        rootLayout.addView(tv);
    }
}
