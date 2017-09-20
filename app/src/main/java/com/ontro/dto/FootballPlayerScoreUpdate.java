package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by IDEOMIND02 on 12-07-2017.
 */

public class FootballPlayerScoreUpdate implements Serializable {
    private String playerId;
    @SerializedName("player_name")
    private String playerName;
    @SerializedName("no_of_goal")
    private String playerGoals;
    @SerializedName("assists")
    private String playerAssists;
    @SerializedName("goal_keeper")
    private String isGolfKeeper;

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerGoals() {
        return playerGoals;
    }

    public void setPlayerGoals(String playerGoals) {
        this.playerGoals = playerGoals;
    }

    public String getPlayerAssists() {
        return playerAssists;
    }

    public void setPlayerAssists(String playerAssists) {
        this.playerAssists = playerAssists;
    }

    public String getIsGolfKeeper() {
        return isGolfKeeper;
    }

    public void setIsGolfKeeper(String isGolfKeeper) {
        this.isGolfKeeper = isGolfKeeper;
    }

}
