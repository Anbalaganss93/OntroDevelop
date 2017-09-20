package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IDEOMIND02 on 26-07-2017.
 */

public class CricketTeamScoreModel implements Serializable{
    @SerializedName("team_id")
    private String teamId;
    @SerializedName("team_name")
    private String teamName;
    @SerializedName("score")
    private String score;
    @SerializedName("wickets")
    private String wickets;
    @SerializedName("overs")
    private String overs;
    @SerializedName("bat_innings")
    private String batInnings;
    @SerializedName("extras_score")
    private List<CricketExtrasScoreModel> extrasScore = null;

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getWickets() {
        return wickets;
    }

    public void setWickets(String wickets) {
        this.wickets = wickets;
    }

    public String getOvers() {
        return overs;
    }

    public void setOvers(String overs) {
        this.overs = overs;
    }

    public List<CricketExtrasScoreModel> getExtrasScore() {
        return extrasScore;
    }

    public void setExtrasScore(List<CricketExtrasScoreModel> extrasScore) {
        this.extrasScore = extrasScore;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getBatInnings() {
        return batInnings;
    }

    public void setBatInnings(String batInnings) {
        this.batInnings = batInnings;
    }
}
