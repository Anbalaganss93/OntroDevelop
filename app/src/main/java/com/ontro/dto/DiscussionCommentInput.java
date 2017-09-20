package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Android on 03-May-17.
 */

public class DiscussionCommentInput {
    @SerializedName("comment")
    private String comment;
    @SerializedName("discussion_id")
    private String discussion_id;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDiscussion_id() {
        return discussion_id;
    }

    public void setDiscussion_id(String discussion_id) {
        this.discussion_id = discussion_id;
    }
}
