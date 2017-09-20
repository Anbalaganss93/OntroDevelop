package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IDEOMIND02 on 28-06-2017.
 */


public class DateAndVenue implements Serializable {
    @SerializedName("start_date")
    private String startDate;
    @SerializedName("end_date")
    private String endDate;
    @SerializedName("from_time")
    private String fromTime;
    @SerializedName("to_time")
    private String toTime;
    @SerializedName("address")
    private List<TournamentVenueAddress> address = null;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public List<TournamentVenueAddress> getAddress() {
        return address;
    }

    public void setAddress(List<TournamentVenueAddress> address) {
        this.address = address;
    }

}
