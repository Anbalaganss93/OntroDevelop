package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by umm on 26-Jul-17.
 */

public class BasketballSetDTO {

    @SerializedName("set_no")
    private String setnumber;

    @SerializedName("score")
    private String score;

    @SerializedName("won")
    private String won;

    public String getSetnumber() {
        return setnumber;
    }

    public void setSetnumber(String setnumber) {
        this.setnumber = setnumber;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getWon() {
        return won;
    }

    public void setWon(String won) {
        this.won = won;
    }
}
