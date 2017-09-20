package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by IDEOMIND02 on 5/8/2017.
 */

public class PlayerPersonalSport implements Serializable {

    @SerializedName("sport")
    private int sport;
    @SerializedName("position")
    private String position;
    @SerializedName("handedness")
    private String handedness;
    @SerializedName("have_team")
    private int haveTeam;

    public int getSport() {
        return sport;
    }

    public void setSport(int sport) {
        this.sport = sport;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getHandedness() {
        return handedness;
    }

    public void setHandedness(String handedness) {
        this.handedness = handedness;
    }

    public int getHaveTeam() {
        return haveTeam;
    }

    public void setHaveTeam(int haveTeam) {
        this.haveTeam = haveTeam;
    }
}