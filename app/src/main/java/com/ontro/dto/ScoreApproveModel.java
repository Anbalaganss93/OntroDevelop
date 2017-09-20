package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Android on 23-May-17.
 */

public class ScoreApproveModel {
    @SerializedName("match_id")
    private String match_id;

    @SerializedName("team_id")
    private String team_id;

    @SerializedName("status")
    private String status;

    public String getMatch_id() {
        return match_id;
    }

    public void setMatch_id(String match_id) {
        this.match_id = match_id;
    }

    public String getTeam_id() {
        return team_id;
    }

    public void setTeam_id(String team_id) {
        this.team_id = team_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
