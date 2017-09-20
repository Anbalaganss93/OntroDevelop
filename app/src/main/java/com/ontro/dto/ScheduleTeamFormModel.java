package com.ontro.dto;

/**
 * Created by Android on 11-May-17.
 */

public class ScheduleTeamFormModel {
    private String playerid;
    private String player_name;
    private String player_location;
    private String player_photo;
    private boolean ischecked;

    public String getPlayerid() {
        return playerid;
    }

    public void setPlayerid(String playerid) {
        this.playerid = playerid;
    }

    public boolean ischecked() {
        return ischecked;
    }

    public void setIschecked(boolean ischecked) {
        this.ischecked = ischecked;
    }

    public String getPlayer_name() {
        return player_name;
    }

    public void setPlayer_name(String player_name) {
        this.player_name = player_name;
    }

    public String getPlayer_location() {
        return player_location;
    }

    public void setPlayer_location(String player_location) {
        this.player_location = player_location;
    }

    public String getPlayer_photo() {
        return player_photo;
    }

    public void setPlayer_photo(String player_photo) {
        this.player_photo = player_photo;
    }
}
