package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IDEOMIND02 on 5/8/2017.
 */

public class PlayerProfilePersonalModel implements Serializable {

    @SerializedName("player_id")
    private String playerId;
    @SerializedName("player_name")
    private String playerName;
    @SerializedName("player_sports")
    private String playerSports;
    @SerializedName("phone")
    private String phone;
    @SerializedName("gender")
    private String gender;
    @SerializedName("locality")
    private String locality;
    @SerializedName("city")
    private String city;
    @SerializedName("sport_type")
    private String sportType;
    @SerializedName("player_dob")
    private String playerDob;
    @SerializedName("profile_image")
    private String profileImage;
    @SerializedName("height")
    private String height;
    @SerializedName("location_name")
    private String locationName;
    @SerializedName("city_name")
    private String cityName;
    @SerializedName("sports")
    private List<PlayerPersonalSport> sports = null;

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerSports() {
        return playerSports;
    }

    public void setPlayerSports(String playerSports) {
        this.playerSports = playerSports;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getSportType() {
        return sportType;
    }

    public void setSportType(String sportType) {
        this.sportType = sportType;
    }

    public String getPlayerDob() {
        return playerDob;
    }

    public void setPlayerDob(String playerDob) {
        this.playerDob = playerDob;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public List<PlayerPersonalSport> getSports() {
        return sports;
    }

    public void setSports(List<PlayerPersonalSport> sports) {
        this.sports = sports;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
