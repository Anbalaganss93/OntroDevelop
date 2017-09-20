package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Android on 25-May-17.
 */

public class ScoreUpdateBasketball {
    @SerializedName("match_id")
    private String match_id;
    @SerializedName("sport_type")
    private String sport_type;
    @SerializedName("match_won")
    private String match_won;
    @SerializedName("team_score")
    private String team_score;
    @SerializedName("quarter_score")
    private String quarter_score;

    public String getMatch_id() {
        return match_id;
    }

    public void setMatch_id(String match_id) {
        this.match_id = match_id;
    }

    public String getSport_type() {
        return sport_type;
    }

    public void setSport_type(String sport_type) {
        this.sport_type = sport_type;
    }

    public String getMatch_won() {
        return match_won;
    }

    public void setMatch_won(String match_won) {
        this.match_won = match_won;
    }

    public String getTeam_score() {
        return team_score;
    }

    public void setTeam_score(String team_score) {
        this.team_score = team_score;
    }

    public String getQuarter_score() {
        return quarter_score;
    }

    public void setQuarter_score(String quarter_score) {
        this.quarter_score = quarter_score;
    }
}
