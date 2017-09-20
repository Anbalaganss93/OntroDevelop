package com.ontro.dto;

import java.io.Serializable;

/**
 * Created by IDEOMIND02 on 26-07-2017.
 */

public class CricketBatsmanScoreModel implements Serializable{
    private String playerName;
    private String playerId;
    private String runs;
    private String fours;
    private String sixs;
    private String balls;
    private String strikeRate;

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getRuns() {
        return runs;
    }

    public void setRuns(String runs) {
        this.runs = runs;
    }

    public String getFours() {
        return fours;
    }

    public void setFours(String fours) {
        this.fours = fours;
    }

    public String getSixs() {
        return sixs;
    }

    public void setSixs(String sixs) {
        this.sixs = sixs;
    }

    public String getBalls() {
        return balls;
    }

    public void setBalls(String balls) {
        this.balls = balls;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getStrikeRate() {
        return strikeRate;
    }

    public void setStrikeRate(String strikeRate) {
        this.strikeRate = strikeRate;
    }
}
