package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IDEOMIND02 on 21-07-2017.
 */

public class FootballScoreResponse implements Serializable{
    @SerializedName("team_id")
    private Integer teamId;
    @SerializedName("team_name")
    private String teamName;
    @SerializedName("team_logo")
    private String teamLogo;
    @SerializedName("team_location")
    private String teamLocation;
    @SerializedName("total_goal")
    private Integer totalGoal;
    @SerializedName("player_score")
    private List<FootballPlayerScoreUpdate> playerScore = new ArrayList<>();

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

    public Integer getTotalGoal() {
        return totalGoal;
    }

    public void setTotalGoal(Integer totalGoal) {
        this.totalGoal = totalGoal;
    }

    public List<FootballPlayerScoreUpdate> getPlayerScore() {
        return playerScore;
    }

    public void setPlayerScore(List<FootballPlayerScoreUpdate> playerScore) {
        this.playerScore = playerScore;
    }

}
