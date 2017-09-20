package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IDEOMIND02 on 23-06-2017.
 */

public class MatchStats {
    @SerializedName("total_match")
    private Integer totalMatch;
    @SerializedName("won")
    private Integer won;
    @SerializedName("lost")
    private Integer lost;

    public Integer getTotalMatch() {
        return totalMatch;
    }

    public void setTotalMatch(Integer totalMatch) {
        this.totalMatch = totalMatch;
    }

    public Integer getWon() {
        return won;
    }

    public void setWon(Integer won) {
        this.won = won;
    }

    public Integer getLost() {
        return lost;
    }

    public void setLost(Integer lost) {
        this.lost = lost;
    }
}
