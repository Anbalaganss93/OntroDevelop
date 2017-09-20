package com.ontro.dto;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by IDEOMIND02 on 07-06-2017.
 */

@IgnoreExtraProperties
public class Chat {
    private String id;
    private String text;
    private String name;
    private String photoUrl;
    private String date;
    private String fcmToken;
    private String chatType;
    private int userColor;
    private String senderFcmId;

    public Chat() {
    }

    public Chat(String text, String name, String photoUrl, String imageUrl) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.date = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String imageUrl) {
        this.date = imageUrl;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getChatType() {
        return chatType;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }

    public int getUserColor() {
        return userColor;
    }

    public void setUserColor(int userColor) {
        this.userColor = userColor;
    }

    public String getSenderFcmId() {
        return senderFcmId;
    }

    public void setSenderFcmId(String senderFcmId) {
        this.senderFcmId = senderFcmId;
    }
}
