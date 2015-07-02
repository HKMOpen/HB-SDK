package com.hypebeast.hbsdk.api.model.head;

import com.google.gson.annotations.SerializedName;
import com.hypebeast.hbsdk.api.model.Alternative;
import com.hypebeast.hbsdk.api.model.hypebeast.embededList;

/**
 * Created by hesk on 7/1/2015.
 */
public class ReponseNormal extends Alternative {
    @SerializedName("products")
    public ResponseProductList product_list;
}
