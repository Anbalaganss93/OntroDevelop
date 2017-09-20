package com.ontro.dto;

/**
 * Created by Android on 08-May-17.
 */

public class MyTeamDataBaseModel {
    private String teamid,teamname,sportid;

    public String getSportid() {
        return sportid;
    }

    public void setSportid(String sportid) {
        this.sportid = sportid;
    }

    public String getTeamid() {
        return teamid;
    }

    public void setTeamid(String teamid) {
        this.teamid = teamid;
    }

    public String getTeamname() {
        return teamname;
    }

    public void setTeamname(String teamname) {
        this.teamname = teamname;
    }
}
