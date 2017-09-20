package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Android on 19-May-17.
 */

public class ScoreUpdate {
    @SerializedName("match_id")
    private String matchId;
    @SerializedName("sport_type")
    private String sporttype;
    @SerializedName("first_team_score")
    private String team_one_score;
    @SerializedName("second_team_score")
    private
    String team_two_score;
    @SerializedName("team_id")
    private
    String teamid;

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchid) {
        this.matchId = matchid;
    }

    public String getSporttype() {
        return sporttype;
    }

    public void setSporttype(String sporttype) {
        this.sporttype = sporttype;
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
}
