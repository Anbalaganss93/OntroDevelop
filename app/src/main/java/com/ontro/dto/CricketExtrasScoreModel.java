package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by IDEOMIND02 on 26-07-2017.
 */

public class CricketExtrasScoreModel implements Serializable{
    @SerializedName("wide")
    private String wide;
    @SerializedName("no_ball")
    private String noBall;
    @SerializedName("leg_bye")
    private String legBye;
    @SerializedName("bye")
    private String bye;
    @SerializedName("penalty")
    private String penalty;
    @SerializedName("extras")
    private String extras;

    public String getWide() {
        return wide;
    }

    public void setWide(String wide) {
        this.wide = wide;
    }

    public String getNoBall() {
        return noBall;
    }

    public void setNoBall(String noBall) {
        this.noBall = noBall;
    }

    public String getLegBye() {
        return legBye;
    }

    public void setLegBye(String legBye) {
        this.legBye = legBye;
    }

    public String getBye() {
        return bye;
    }

    public void setBye(String bye) {
        this.bye = bye;
    }

    public String getPenalty() {
        return penalty;
    }

    public void setPenalty(String penalty) {
        this.penalty = penalty;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }
}
