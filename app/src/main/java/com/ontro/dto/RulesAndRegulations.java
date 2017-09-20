package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by IDEOMIND02 on 28-06-2017.
 */

public class RulesAndRegulations implements Serializable {
    @SerializedName("tournament_rules")
    private String tournamentRules;

    public String getTournamentRules() {
        return tournamentRules;
    }

    public void setTournamentRules(String tournamentRules) {
        this.tournamentRules = tournamentRules;
    }
}