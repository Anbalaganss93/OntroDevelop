package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by IDEOMIND02 on 10-06-2017.
 */

public class FirebaseAuthUser implements Serializable {
    @SerializedName("fcm_uid")
    private String uniqueId;
    @SerializedName("user_id")
    private Integer userId;
    @SerializedName("fcm_token")
    private String fcmToken;

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
