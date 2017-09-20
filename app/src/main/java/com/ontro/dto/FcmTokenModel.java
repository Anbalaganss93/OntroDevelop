package com.ontro.dto;

/**
 * Created by IDEOMIND02 on 19-06-2017.
 */

public class FcmTokenModel {
    private int keyId;
    private String token;

    public FcmTokenModel(int keyId, String token) {
        this.keyId = keyId;
        this.token = token;
    }

    public FcmTokenModel() {

    }

    public int getKeyId() {
        return keyId;
    }

    public void setKeyId(int keyId) {
        this.keyId = keyId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
