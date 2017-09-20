package com.ontro.dto;

/**
 * Created by Android on 25-Feb-17.
 */

public class DiscussionsCommentModel {
    private String name,seen_before,comment,likecount,likestatus,flagstatus,comment_id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeen_before() {
        return seen_before;
    }

    public void setSeen_before(String seen_before) {
        this.seen_before = seen_before;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getLikecount() {
        return likecount;
    }

    public void setLikecount(String likecount) {
        this.likecount = likecount;
    }

    public String getLikestatus() {
        return likestatus;
    }

    public void setLikestatus(String likestatus) {
        this.likestatus = likestatus;
    }

    public String getFlagstatus() {
        return flagstatus;
    }

    public void setFlagstatus(String flagstatus) {
        this.flagstatus = flagstatus;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }
}
