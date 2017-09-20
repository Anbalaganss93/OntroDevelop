package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IDEOMIND02 on 12-06-2017.
 */

public class NotificationTokenRequest {
    @SerializedName("fcm_token")
    private String fcmToken;

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
