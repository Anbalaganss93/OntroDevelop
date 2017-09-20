package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Android on 03-May-17.
 */

public class StartDiscussionInput {
    @SerializedName("title")
    private String title;
    @SerializedName("content")
    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
