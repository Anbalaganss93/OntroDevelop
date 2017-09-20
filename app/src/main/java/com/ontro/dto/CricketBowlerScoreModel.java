package com.ontro.dto;

import java.io.Serializable;

/**
 * Created by IDEOMIND02 on 26-07-2017.
 */

public class CricketBowlerScoreModel implements Serializable {
   private String playerName;
    private String playerId;
    private String over;
    private String bowlingRun;
    private String maiden;
    private String wickets;
    private String economyRate;

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getOver() {
        return over;
    }

    public void setOver(String over) {
        this.over = over;
    }

    public String getBowlingRun() {
        return bowlingRun;
    }

    public void setBowlingRun(String bowlingRun) {
        this.bowlingRun = bowlingRun;
    }

    public String getMaiden() {
        return maiden;
    }

    public void setMaiden(String maiden) {
        this.maiden = maiden;
    }

    public String getWickets() {
        return wickets;
    }

    public void setWickets(String wickets) {
        this.wickets = wickets;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getEconomyRate() {
        return economyRate;
    }

    public void setEconomyRate(String economyRate) {
        this.economyRate = economyRate;
    }
}
