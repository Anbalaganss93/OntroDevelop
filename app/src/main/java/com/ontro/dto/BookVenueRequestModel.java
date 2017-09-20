package com.ontro.dto;

/**
 * Created by IDEOMIND02 on 5/10/2017.
 */

public class BookVenueRequestModel {
    private String matchType;
    private String matchLocation;
    private String matchDate;

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public String getMatchLocation() {
        return matchLocation;
    }

    public void setMatchLocation(String matchLocation) {
        this.matchLocation = matchLocation;
    }

    public String getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(String matchDate) {
        this.matchDate = matchDate;
    }
}
