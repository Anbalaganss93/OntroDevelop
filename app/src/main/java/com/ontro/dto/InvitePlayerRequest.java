package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IDEOMIND02 on 05-06-2017.
 */

public class InvitePlayerRequest {
    @SerializedName("team_id")
    private Integer teamId;
    @SerializedName("player_id")
    private Integer playerId;

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public Integer getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }
}
