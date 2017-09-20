package com.ontro.dto;

import java.io.Serializable;

/**
 * Created by IDEOMIND02 on 17-06-2017.
 */

public class ChatUser implements Serializable {
    private String uniqueId;
    private String userName;
    private String imageUrl;
    private String lastChatTime;
    private String playerFcmToken;

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLastChatTime() {
        return lastChatTime;
    }

    public void setLastChatTime(String lastChatTime) {
        this.lastChatTime = lastChatTime;
    }

    public String getPlayerFcmToken() {
        return playerFcmToken;
    }

    public void setPlayerFcmToken(String playerFcmToken) {
        this.playerFcmToken = playerFcmToken;
    }
}
