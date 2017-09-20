package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IDEOMIND02 on 25-05-2017.
 */

public class FootballScoreUpdateRequest {
    @SerializedName("match_id")
    private String matchId;
    @SerializedName("sport_type")
    private String sportType;
    @SerializedName("total_score")
    private String teamScore;
    @SerializedName("player_score")
    private String playerScore;
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

    public String getPlayerScore() {
        return playerScore;
    }

    public void setPlayerScore(String playerScore) {
        this.playerScore = playerScore;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }
}
