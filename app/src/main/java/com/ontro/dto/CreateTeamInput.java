package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Android on 03-May-17.
 */

public class CreateTeamInput {
    @SerializedName("team_name")
    private String team_name;
    @SerializedName("sport")
    private String sport;
    @SerializedName("team_location")
    private String team_location;
    @SerializedName("team_logo")
    private String team_logo;
    @SerializedName("team_about")
    private String team_about;

    public String getTeam_name() {
        return team_name;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getTeam_location() {
        return team_location;
    }

    public void setTeam_location(String team_location) {
        this.team_location = team_location;
    }

    public String getTeam_logo() {
        return team_logo;
    }

    public void setTeam_logo(String team_logo) {
        this.team_logo = team_logo;
    }

    public String getTeam_about() {
        return team_about;
    }

    public void setTeam_about(String team_about) {
        this.team_about = team_about;
    }
}
