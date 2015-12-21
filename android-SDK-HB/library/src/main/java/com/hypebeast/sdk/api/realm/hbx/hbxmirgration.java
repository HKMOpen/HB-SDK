package com.hypebeast.sdk.api.realm.hbx;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;

/**
 * Created by hesk on 21/12/15.
 */
public class hbxmirgration implements RealmMigration {

    @Override
    public void migrate(DynamicRealm dynamicRealm, long l, long l1) {
        Log.d("realm", "oldverison." + l + " newversion." + l1);
    }
}
