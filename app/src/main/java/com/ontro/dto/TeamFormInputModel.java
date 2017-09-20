package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Android on 11-May-17.
 */

public class TeamFormInputModel {
    @SerializedName("match_id")
    private
    String matchid;
    @SerializedName("players_id")
    private
    String playerid;
    @SerializedName("team_id")
    private
    String opponentid;

    public String getMatchid() {
        return matchid;
    }

    public void setMatchid(String matchid) {
        this.matchid = matchid;
    }

    public String getPlayerid() {
        return playerid;
    }

    public void setPlayerid(String playerid) {
        this.playerid = playerid;
    }

    public String getOpponentid() {
        return opponentid;
    }

    public void setOpponentid(String opponentid) {
        this.opponentid = opponentid;
    }
}
