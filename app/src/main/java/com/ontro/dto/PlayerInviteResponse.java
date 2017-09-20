package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IDEOMIND02 on 03-06-2017.
 */

public class PlayerInviteResponse {

    @SerializedName("invite_id")
    private int inviteId;
    @SerializedName("team_name")
    private String teamName;
    @SerializedName("team_id")
    private int teamId;
    @SerializedName("invite_status")
    private int inviteStatus;
    @SerializedName("team_logo")
    private String teamLogo;
    @SerializedName("team_sport")
    private int teamSport;
    @SerializedName("created_at")
    private String createdAt;

    public int getInviteId() {
        return inviteId;
    }

    public void setInviteId(int inviteId) {
        this.inviteId = inviteId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int getInviteStatus() {
        return inviteStatus;
    }

    public void setInviteStatus(int inviteStatus) {
        this.inviteStatus = inviteStatus;
    }

    public String getTeamLogo() {
        return teamLogo;
    }

    public void setTeamLogo(String teamLogo) {
        this.teamLogo = teamLogo;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getTeamSport() {
        return teamSport;
    }

    public void setTeamSport(int teamSport) {
        this.teamSport = teamSport;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

