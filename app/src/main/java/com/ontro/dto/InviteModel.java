package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Android on 06-May-17.
 */

public class InviteModel {
    @SerializedName("team_id")
    private String team_id;
    @SerializedName("opponent_team_id")
    private String opponent_team_id;
    @SerializedName("match_type")
    private String match_type;
    @SerializedName("location")
    private String location;
    @SerializedName("match_date")
    private String match_date;
    @SerializedName("sport_type")
    private String sport_type;
    @SerializedName("game_type")
    private String gametype;

    public String getGametype() {
        return gametype;
    }

    public void setGametype(String gametype) {
        this.gametype = gametype;
    }

    public String getSport_type() {
        return sport_type;
    }

    public void setSport_type(String sport_type) {
        this.sport_type = sport_type;
    }

    public String getTeam_id() {
        return team_id;
    }

    public void setTeam_id(String team_id) {
        this.team_id = team_id;
    }

    public String getOpponent_team_id() {
        return opponent_team_id;
    }

    public void setOpponent_team_id(String opponent_team_id) {
        this.opponent_team_id = opponent_team_id;
    }

    public String getMatch_type() {
        return match_type;
    }

    public void setMatch_type(String match_type) {
        this.match_type = match_type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMatch_date() {
        return match_date;
    }

    public void setMatch_date(String match_date) {
        this.match_date = match_date;
    }
}
