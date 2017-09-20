package com.ontro.rest;

import com.google.gson.annotations.SerializedName;

/**
 * Created by umm
 */

public class RegisterInputDTO {

    @SerializedName("fullName")
    private String name;
    @SerializedName("email")
    private String email;
    @SerializedName("phone")
    private String number;
    @SerializedName("password")
    private String password;

    public RegisterInputDTO(String name, String email, String number, String password) {
        this.name = name;
        this.email = email;
        this.number = number;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
