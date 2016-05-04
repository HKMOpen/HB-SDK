package com.hbexperiement.pbclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by hesk on 17/12/15.
 */
public class sampleLoad extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.framefragment);

        getFragmentManager().beginTransaction().add(R.id.hereframelayout, new general_test()).commit();


    }


}
