package com.hypebeast.hbsdk.api.model.head;

import com.google.gson.annotations.SerializedName;
import com.hypebeast.hbsdk.api.model.Alternative;
import com.hypebeast.hbsdk.api.model.commonterms.Product;
import com.hypebeast.hbsdk.api.model.hypebeast.embededList;
import com.hypebeast.hbsdk.api.model.commonterms.FilterGroup;

import java.util.List;

/**
 * Created by hesk on 2/6/15.
 */
public class ResponseProductList extends Alternative {
    @SerializedName("page")
    private int page;
    @SerializedName("limit")
    private int limit;
    @SerializedName("pages")
    private int pages;
    @SerializedName("total")
    private int total;
    @SerializedName("_embedded")
    private embededList embededitems;
    @SerializedName("facets")
    private FilterGroup filters;

    public int totalpages() {
        return pages;
    }

    public int current_page() {
        return page;
    }

    public List<Product> getlist() {
        return embededitems.productswrap;
    }

    public FilterGroup getfacets() {
        return filters;
    }
}
