package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IDEOMIND02 on 28-07-2017.
 */

public class CricketTeamScoreUpdateModel {
    @SerializedName("team_id")
    private Integer teamId;
    @SerializedName("score")
    private Integer score;
    @SerializedName("wickets")
    private Integer wickets;
    @SerializedName("overs")
    private Integer overs;
    @SerializedName("bat_innings")
    private Integer batInnings;
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

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
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

    public Integer getOvers() {
        return overs;
    }

    public void setOvers(Integer overs) {
        this.overs = overs;
    }

    public Integer getBatInnings() {
        return batInnings;
    }

    public void setBatInnings(Integer batInnings) {
        this.batInnings = batInnings;
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
}
