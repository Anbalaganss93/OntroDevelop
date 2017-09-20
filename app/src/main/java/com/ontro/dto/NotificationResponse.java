package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IDEOMIND02 on 24-06-2017.
 */

public class NotificationResponse {
    @SerializedName("title")
    private String title;
    @SerializedName("content")
    private String content;
    @SerializedName("click_action")
    private String clickAction;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("team_id")
    private String teamId;
    @SerializedName("team_logo")
    private String teamLogo;
    @SerializedName("team_name")
    private String teamName;
    @SerializedName("sport")
    private String teamSport;
    @SerializedName("player_invite_id")
    private Integer inviteId;

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

    public String getClickAction() {
        return clickAction;
    }

    public void setClickAction(String clickAction) {
        this.clickAction = clickAction;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getTeamLogo() {
        return teamLogo;
    }

    public void setTeamLogo(String teamLogo) {
        this.teamLogo = teamLogo;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }


    public String getTeamSport() {
        return teamSport;
    }

    public void setTeamSport(String teamSport) {
        this.teamSport = teamSport;
    }

    public Integer getInviteId() {
        return inviteId;
    }

    public void setInviteId(Integer inviteId) {
        this.inviteId = inviteId;
    }
}

