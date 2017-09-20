package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by umm
 */

public class SquadInfo implements Serializable {
    @SerializedName("fcm_uid")
    private String fcmUid;
    @SerializedName("player_id")
    private String playerId;
    @SerializedName("player_name")
    private String playerName;
    @SerializedName("player_photo")
    private Object playerPhoto;
    @SerializedName("player_location")
    private String playerLocation;

    public String getFcmUid() {
        return fcmUid;
    }

    public void setFcmUid(String fcmUid) {
        this.fcmUid = fcmUid;
    }

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

    public Object getPlayerPhoto() {
        return playerPhoto;
    }

    public void setPlayerPhoto(Object playerPhoto) {
        this.playerPhoto = playerPhoto;
    }

    public String getPlayerLocation() {
        return playerLocation;
    }

    public void setPlayerLocation(String playerLocation) {
        this.playerLocation = playerLocation;
    }

}
