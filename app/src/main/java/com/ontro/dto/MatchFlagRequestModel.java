package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IDEOMIND02 on 12-08-2017.
 */

public class MatchFlagRequestModel {
    @SerializedName("match_id")
    private String matchId;
    @SerializedName("flag_type")
    private Integer flagType;
    @SerializedName("reason")
    private String reason;

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public Integer getFlagType() {
        return flagType;
    }

    public void setFlagType(Integer flagType) {
        this.flagType = flagType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}