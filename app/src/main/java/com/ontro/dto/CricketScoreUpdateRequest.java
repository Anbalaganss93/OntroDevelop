package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IDEOMIND02 on 25-05-2017.
 */

public class CricketScoreUpdateRequest {
    @SerializedName("match_id")
    private String matchId;
    @SerializedName("sport_type")
    private String sportType;
    @SerializedName("team_score")
    private String teamScore;
    @SerializedName("batting")
    private String batting;
    @SerializedName("bowling")
    private String bowling;
    @SerializedName("extras")
    private String extras;
    @SerializedName("team_id")
    private String teamId;

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getSportType() {
        return sportType;
    }

    public void setSportType(String sportType) {
        this.sportType = sportType;
    }

    public String getTeamScore() {
        return teamScore;
    }

    public void setTeamScore(String teamScore) {
        this.teamScore = teamScore;
    }

    public String getBatting() {
        return batting;
    }

    public void setBatting(String batting) {
        this.batting = batting;
    }

    public String getBowling() {
        return bowling;
    }

    public void setBowling(String bowling) {
        this.bowling = bowling;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

}
