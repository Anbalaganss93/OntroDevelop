package com.ontro.rest;

import com.google.gson.annotations.SerializedName;

/**
 * Created by umm
 */

public class SocialAuthendicationInput {

    @SerializedName("email")
    private
    String email;
    @SerializedName("accessToken")
    private
    String token;
    @SerializedName("social_id")
    private
    String id;
    @SerializedName("name")
    private
    String name;
    @SerializedName("image")
    private
    String image;

    public SocialAuthendicationInput(String email, String token, String id, String name, String image) {
        this.email = email;
        this.token = token;
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
