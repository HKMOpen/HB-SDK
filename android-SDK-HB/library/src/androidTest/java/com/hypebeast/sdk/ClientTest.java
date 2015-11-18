package com.hypebeast.sdk;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

import junit.framework.TestCase;

/**
 * Created by hesk on 18/11/15.
 */
public class ClientTest extends ActivityInstrumentationTestCase2<Activity> {
    /**
     * Creates an {@link ActivityInstrumentationTestCase2}.
     *
     * @param activityClass The activity to test. This must be a class in the instrumentation
     *                      targetPackage specified in the AndroidManifest.xml
     */
    public ClientTest(Class<Activity> activityClass) {
        super(activityClass);
    }
}
