package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Android on 19-May-17.
 */

public class BasketballScoreDTO {
    @SerializedName("match_id")
    private String matchId;
    @SerializedName("sport_type")
    private String sportType;
    @SerializedName("team_one_score")
    private String team_one_score;
    @SerializedName("team_two_score")
    private String team_two_score;
    @SerializedName("team_id")
    private String teamid;
    @SerializedName("player_score")
    private String player_score;

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

    public String getTeam_one_score() {
        return team_one_score;
    }

    public void setTeam_one_score(String team_one_score) {
        this.team_one_score = team_one_score;
    }

    public String getTeam_two_score() {
        return team_two_score;
    }

    public void setTeam_two_score(String team_two_score) {
        this.team_two_score = team_two_score;
    }

    public String getTeamid() {
        return teamid;
    }

    public void setTeamid(String teamid) {
        this.teamid = teamid;
    }

    public String getPlayer_score() {
        return player_score;
    }

    public void setPlayer_score(String player_score) {
        this.player_score = player_score;
    }
}
