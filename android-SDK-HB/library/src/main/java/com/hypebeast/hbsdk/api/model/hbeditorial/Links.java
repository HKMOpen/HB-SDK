package com.hypebeast.hbsdk.api.model.hbeditorial;

import java.util.ArrayList;

/**
 * Created by hesk on 21/4/15.
 */
public class Links {
    private href self;
    private href thumbnail;
    private ArrayList<href> categories;
    private ArrayList<href> tags;

    public Links() {
    }

    public String getSelf() {
        return self.getHref();
    }

    public String getThumbnail() {
        return thumbnail.getHref();
    }

    public ArrayList<href> getCategories() {
        return categories;
    }

    public ArrayList<href> getTags() {
        return tags;
    }
}
