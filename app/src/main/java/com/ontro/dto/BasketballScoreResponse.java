package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IDEOMIND02 on 21-07-2017.
 */

public class BasketballScoreResponse implements Serializable{
    @SerializedName("team_id")
    private Integer teamId;
    @SerializedName("team_name")
    private String teamName;
    @SerializedName("team_logo")
    private String teamLogo;
    @SerializedName("team_location")
    private String teamLocation;
    @SerializedName("quarter_score")
    private List<BasketballSetDTO> quarterScore = new ArrayList<>();
    @SerializedName("player_score")
    private List<BasketballScoreUpdateDTO> playerScore = new ArrayList<>();
    @SerializedName("bench_players")
    private List<String> benchPlayer = new ArrayList<>();

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

    public List<BasketballScoreUpdateDTO> getPlayerScore() {
        return playerScore;
    }

    public void setPlayerScore(List<BasketballScoreUpdateDTO> playerScore) {
        this.playerScore = playerScore;
    }

    public List<BasketballSetDTO> getQuarterScore() {
        return quarterScore;
    }

    public void setQuarterScore(List<BasketballSetDTO> quarterScore) {
        this.quarterScore = quarterScore;
    }

    public List<String> getBenchPlayer() {
        return benchPlayer;
    }

    public void setBenchPlayer(List<String> benchPlayer) {
        this.benchPlayer = benchPlayer;
    }
}
