package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IDEOMIND02 on 21-08-2017.
 */

public class ChatDetailModel {
    @SerializedName("team_uid")
    private String chatUniqueId;
    @SerializedName("latest_chat_time")
    private String latestChatTime;

    public String getChatUniqueId() {
        return chatUniqueId;
    }

    public void setChatUniqueId(String chatUniqueId) {
        this.chatUniqueId = chatUniqueId;
    }

    public String getLatestChatTime() {
        return latestChatTime;
    }

    public void setLatestChatTime(String latestChatTime) {
        this.latestChatTime = latestChatTime;
    }

}
