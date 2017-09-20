package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Android on 13-May-17.
 */

public class ConfirmBookingInputModel {
    @SerializedName("venue_id")
    private String venue_id;
    @SerializedName("from_time")
    private String from_time;
    @SerializedName("to_time")
    private String to_time;
    @SerializedName("match_date")
    private String match_date;
    @SerializedName("match_id")
    private String match_id;
    @SerializedName("team_id")
    private String team_id;

    public String getTeam_id() {
        return team_id;
    }

    public void setTeam_id(String team_id) {
        this.team_id = team_id;
    }

    public String getMatch_id() {
        return match_id;
    }

    public void setMatch_id(String match_id) {
        this.match_id = match_id;
    }

    public String getVenue_id() {
        return venue_id;
    }

    public void setVenue_id(String venue_id) {
        this.venue_id = venue_id;
    }

    public String getFrom_time() {
        return from_time;
    }

    public void setFrom_time(String from_time) {
        this.from_time = from_time;
    }

    public String getTo_time() {
        return to_time;
    }

    public void setTo_time(String to_time) {
        this.to_time = to_time;
    }

    public String getMatch_date() {
        return match_date;
    }

    public void setMatch_date(String match_date) {
        this.match_date = match_date;
    }
}
