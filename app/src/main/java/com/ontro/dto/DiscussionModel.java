package com.ontro.dto;

/**
 * Created by Android on 20-Feb-17.
 */

public class DiscussionModel {
    private String player_id, user_comment,user_question,user_name,user_seen_hours,user_image,discussionid,comment_count,iscommented;
    public String getUser_comment() {
        return user_comment;
    }

    public void setUser_comment(String user_comment) {
        this.user_comment = user_comment;
    }

    public String getUser_question() {
        return user_question;
    }

    public void setUser_question(String user_question) {
        this.user_question = user_question;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_seen_hours() {
        return user_seen_hours;
    }

    public void setUser_seen_hours(String user_seen_hours) {
        this.user_seen_hours = user_seen_hours;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getDiscussionid() {
        return discussionid;
    }

    public void setDiscussionid(String discussionid) {
        this.discussionid = discussionid;
    }

    public String getComment_count() {
        return comment_count;
    }

    public void setComment_count(String comment_count) {
        this.comment_count = comment_count;
    }

    public String getIscommented() {
        return iscommented;
    }

    public void setIscommented(String iscommented) {
        this.iscommented = iscommented;
    }

    public String getPlayer_id() {
        return player_id;
    }

    public void setPlayer_id(String player_id) {
        this.player_id = player_id;
    }
}
