package com.elm.test;

import android.test.ActivityInstrumentationTestCase2;
import com.elm.*;

public class HelloAndroidActivityTest extends ActivityInstrumentationTestCase2<CCVideoActivity> {

    public HelloAndroidActivityTest() {
        super(CCVideoActivity.class);
    }

    public void testActivity() {
        CCVideoActivity activity = getActivity();
        assertNotNull(activity);
    }
}

