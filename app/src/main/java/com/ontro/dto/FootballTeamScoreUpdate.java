package com.ontro.dto;

import java.io.Serializable;

/**
 * Created by IDEOMIND02 on 21-07-2017.
 */

public class FootballTeamScoreUpdate implements Serializable {
    private String teamId;
    private String teamName;
    private String teamGoals;

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamGoals() {
        return teamGoals;
    }

    public void setTeamGoals(String teamGoals) {
        this.teamGoals = teamGoals;
    }

}
