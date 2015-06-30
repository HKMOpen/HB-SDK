package com.hypebeast.hbsdk.api.model.catelog;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by hesk on 2/3/15.
 */
public class CTPrice {

    @SerializedName("ranges")
    private ArrayList<Range> ranges;

    public CTPrice() {
    }

    public Range getRangeAt(int which) {
        return ranges.get(which);
    }

    public ArrayList<TermWrap> getRanges() {
        final ArrayList<TermWrap> newList = new ArrayList<>();
        for (Range r : ranges) {
            newList.add(r.getTerm());
        }
        return newList;
    }
}
