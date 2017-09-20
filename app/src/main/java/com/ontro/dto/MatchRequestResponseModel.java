package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Android on 09-May-17.
 */

public class MatchRequestResponseModel {
    @SerializedName("match_status")
    private String matchStatus;

    @SerializedName("match_id")
    private String matchId;

    public String getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }
}
