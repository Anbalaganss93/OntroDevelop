package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IDEOMIND02 on 15-06-2017.
 */

public class LogoutRequest {
    @SerializedName("user_id")
    private int userId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
