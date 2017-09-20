package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IDEOMIND02 on 27-07-2017.
 */

public class CricketScoreResponse implements Serializable {
    @SerializedName("team_id")
    private Integer teamId;
    @SerializedName("team_name")
    private String teamName;
    @SerializedName("team_logo")
    private String teamLogo;
    @SerializedName("team_location")
    private String teamLocation;
    @SerializedName("score")
    private Integer score;
    @SerializedName("wickets")
    private Integer wickets;
    @SerializedName("overs")
    private Double overs;
    @SerializedName("extras")
    private Integer extras;
    @SerializedName("wide")
    private Integer wide;
    @SerializedName("no_ball")
    private Integer noBall;
    @SerializedName("leg_bye")
    private Integer legBye;
    @SerializedName("bye")
    private Integer bye;
    @SerializedName("penalty")
    private Integer penalty;
    @SerializedName("bat_innings")
    private Integer batInnings;
    @SerializedName("batting_score")
    private List<CricketBatsmanScoreModel> battingScore = null;
    @SerializedName("bowling_score")
    private List<CricketBowlerScoreModel> bowlingScore = null;

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
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

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getWickets() {
        return wickets;
    }

    public void setWickets(Integer wickets) {
        this.wickets = wickets;
    }

    public Double getOvers() {
        return overs;
    }

    public void setOvers(Double overs) {
        this.overs = overs;
    }

    public Integer getExtras() {
        return extras;
    }

    public void setExtras(Integer extras) {
        this.extras = extras;
    }

    public Integer getWide() {
        return wide;
    }

    public void setWide(Integer wide) {
        this.wide = wide;
    }

    public Integer getNoBall() {
        return noBall;
    }

    public void setNoBall(Integer noBall) {
        this.noBall = noBall;
    }

    public Integer getLegBye() {
        return legBye;
    }

    public void setLegBye(Integer legBye) {
        this.legBye = legBye;
    }

    public Integer getBye() {
        return bye;
    }

    public void setBye(Integer bye) {
        this.bye = bye;
    }

    public Integer getPenalty() {
        return penalty;
    }

    public void setPenalty(Integer penalty) {
        this.penalty = penalty;
    }

    public Integer getBatInnings() {
        return batInnings;
    }

    public void setBatInnings(Integer batInnings) {
        this.batInnings = batInnings;
    }

    public List<CricketBatsmanScoreModel> getBattingScore() {
        return battingScore;
    }

    public void setBattingScore(List<CricketBatsmanScoreModel> battingScore) {
        this.battingScore = battingScore;
    }

    public List<CricketBowlerScoreModel> getBowlingScore() {
        return bowlingScore;
    }

    public void setBowlingScore(List<CricketBowlerScoreModel> bowlingScore) {
        this.bowlingScore = bowlingScore;
    }

}
