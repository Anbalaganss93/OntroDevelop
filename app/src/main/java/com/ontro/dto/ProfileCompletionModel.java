package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Android on 10-Apr-17.
 */

public class ProfileCompletionModel {
    @SerializedName("name")
    private String name;
    @SerializedName("phone")
    private String phone;
    @SerializedName("dob")
    private String dob;
    @SerializedName("gender")
    private String gender;
    @SerializedName("locality")
    private String locality;
    @SerializedName("city")
    private String city;
    @SerializedName("sport_type")
    private String sport_type;
    @SerializedName("fav_sport")
    private String fav_sport;
    @SerializedName("height")
    private String height;
    @SerializedName("profile_image")
    private String profileimage;

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getSport_type() {
        return sport_type;
    }

    public void setSport_type(String sport_type) {
        this.sport_type = sport_type;
    }

    public String getFav_sport() {
        return fav_sport;
    }

    public void setFav_sport(String fav_sport) {
        this.fav_sport = fav_sport;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
