package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by IDEOMIND02 on 11-09-2017.
 */

public class TeamRecord implements Serializable {
    @SerializedName("total_match")
    private String totalMatch;
    @SerializedName("won")
    private String won;
    @SerializedName("lost")
    private String lost;

    public String getTotalMatch() {
        return totalMatch;
    }

    public void setTotalMatch(String totalMatch) {
        this.totalMatch = totalMatch;
    }

    public String getWon() {
        return won;
    }

    public void setWon(String won) {
        this.won = won;
    }

    public String getLost() {
        return lost;
    }

    public void setLost(String lost) {
        this.lost = lost;
    }

}
