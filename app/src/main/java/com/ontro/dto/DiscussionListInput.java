package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Android on 03-May-17.
 */

public class DiscussionListInput {
    @SerializedName("show")
    private String show;

    public String getShow() {
        return show;
    }

    public void setShow(String show) {
        this.show = show;
    }
}
