package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IDEOMIND02 on 02-06-2017.
 */

public class BadgeLevel {

    @SerializedName("over_all_points")
    private Integer sport;
    @SerializedName("badge")
    private Integer badge;
    @SerializedName("level")
    private Integer level;

    public Integer getSport() {
        return sport;
    }

    public void setSport(Integer sport) {
        this.sport = sport;
    }

    public Integer getBadge() {
        return badge;
    }

    public void setBadge(Integer badge) {
        this.badge = badge;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

}