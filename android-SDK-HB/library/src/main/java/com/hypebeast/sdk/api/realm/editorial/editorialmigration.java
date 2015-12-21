package com.hypebeast.sdk.api.realm.editorial;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;

/**
 * Created by hesk on 21/12/15.
 */
public class editorialmigration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm dynamicRealm, long l, long l1) {
        Log.d("realm", "oldverison." + l + " newversion." + l1);
    }
}
