package com.hypebeast.sdk.application.hypetrak;


/**
 * Created by hesk on 4/2/16.
 */
public interface sync {
    void syncDone(HypetrakMainApp self, String data);

    void error(String txt);
}
