package com.hypebeast.sdk.application.hypebeast;

import com.hypebeast.sdk.api.model.hbeditorial.Foundation;

/**
 * Created by hesk on 20/11/15.
 */
public class syncDebug implements sync {
    @Override
    public void syncDone(ConfigurationSync me, Foundation data) {

    }

    @Override
    public void initFailure(String message) {

    }

    public void syncDone(ConfigurationSync me, Foundation data, String additionalMessage) {
        syncDone(me, data);
    }
}
