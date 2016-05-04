package com.hypebeast.sdk.api.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * Created by hesk on 4/5/16.
 */
public class AnnotationExclusionStrategy implements ExclusionStrategy {

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        boolean isSkip = false;
        if (f.getAnnotation(Excludoo.class) != null) {
            isSkip = true;
        }
        return isSkip;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
}