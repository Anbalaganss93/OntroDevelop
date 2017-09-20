package com.ontro.dto;

import java.io.Serializable;

/**
 * Created by umm
 */

public class MySquadInfo implements Serializable {
    private String playerId;
    private String playerName;
    private String playerLocation;
    private String playerImage;

    public MySquadInfo(String id, String name, String position, String logo) {
        this.playerId = id;
        this.playerName = name;
        this.playerLocation = position;
        this.playerImage = logo;
    }

    public MySquadInfo() {

    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String id) {
        this.playerId = id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String name) {
        this.playerName = name;
    }

    public String getPlayerLocation() {
        return playerLocation;
    }

    public void setPlayerLocation(String position) {
        this.playerLocation = position;
    }

    public String getPlayerImage() {
        return playerImage;
    }

    public void setPlayerImage(String logo) {
        this.playerImage = logo;
    }
}
