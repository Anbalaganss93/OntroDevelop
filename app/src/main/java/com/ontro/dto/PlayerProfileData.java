package com.ontro.dto;

import java.io.Serializable;

/**
 * Created by IDEOMIND02 on 18-05-2017.
 */

public class PlayerProfileData implements Serializable {
    private int id;
    private String playerInfo;

    public PlayerProfileData(int id, String playerInfo) {
        this.id = id;
        this.playerInfo = playerInfo;
    }

    public PlayerProfileData() {

    }

    public int getKeyId() {
        return id;
    }

    public void setKeyId(int id) {
        this.id = id;
    }

    public String getPlayerInfo() {
        return playerInfo;
    }

    public void setPlayerInfo(String playerInfo) {
        this.playerInfo = playerInfo;
    }
}
