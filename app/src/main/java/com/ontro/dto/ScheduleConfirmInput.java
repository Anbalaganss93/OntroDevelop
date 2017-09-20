package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Android on 13-May-17.
 */

public class ScheduleConfirmInput {
    @SerializedName("venues_booking_id")
    private
    String venues_booking_id;

    @SerializedName("venue_status")
    private
    String venue_status;

    public String getVenues_booking_id() {
        return venues_booking_id;
    }

    public void setVenues_booking_id(String venues_booking_id) {
        this.venues_booking_id = venues_booking_id;
    }

    public String getVenue_status() {
        return venue_status;
    }

    public void setVenue_status(String venue_status) {
        this.venue_status = venue_status;
    }
}
