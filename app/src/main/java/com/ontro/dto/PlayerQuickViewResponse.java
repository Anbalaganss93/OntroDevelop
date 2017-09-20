package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IDEOMIND02 on 02-06-2017.
 */

public class PlayerQuickViewResponse {

    @SerializedName("player_id")
    private Integer playerId;
    @SerializedName("player_name")
    private String playerName;
    @SerializedName("profile_image")
    private String profileImage;
    @SerializedName("height")
    private Integer height;
    @SerializedName("location_name")
    private String locationName;
    @SerializedName("city_name")
    private String cityName;
    @SerializedName("progress")
    private Integer progress;
    @SerializedName("badge_level")
    private BadgeLevel badgeLevel;
    @SerializedName("match_stats")
    private MatchStats matchStats;

    public Integer getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public BadgeLevel getBadgeLevel() {
        return badgeLevel;
    }

    public void setBadgeLevel(BadgeLevel badgeLevel) {
        this.badgeLevel = badgeLevel;
    }

    public MatchStats getMatchStats() {
        return matchStats;
    }

    public void setMatchStats(MatchStats matchStats) {
        this.matchStats = matchStats;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}