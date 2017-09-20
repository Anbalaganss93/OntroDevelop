package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IDEOMIND02 on 17-08-2017.
 */

public class TeamEditRequestModel {
    @SerializedName("team_id")
    private String teamId;
    @SerializedName("team_name")
    private String teamName;
    @SerializedName("team_logo")
    private String teamLogo;
    @SerializedName("team_location")
    private String teamLocation;
    @SerializedName("team_about")
    private String teamAbout;

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamLogo() {
        return teamLogo;
    }

    public void setTeamLogo(String teamLogo) {
        this.teamLogo = teamLogo;
    }

    public String getTeamLocation() {
        return teamLocation;
    }

    public void setTeamLocation(String teamLocation) {
        this.teamLocation = teamLocation;
    }

    public String getTeamAbout() {
        return teamAbout;
    }

    public void setTeamAbout(String teamAbout) {
        this.teamAbout = teamAbout;
    }
}
