package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IDEOMIND02 on 21-06-2017.
 */

public class VenueResponseModel {

    @SerializedName("venue_id")
    private Integer venueId;
    @SerializedName("venue_name")
    private String venueName;
    @SerializedName("location_name")
    private String locationName;
    @SerializedName("open_from")
    private String openFrom;
    @SerializedName("open_to")
    private String openTo;
    @SerializedName("avg_cost")
    private String avgCost;
    @SerializedName("sport")
    private String sport;
    @SerializedName("Venue_images")
    private String venueImages;

    public Integer getVenueId() {
        return venueId;
    }

    public void setVenueId(Integer venueId) {
        this.venueId = venueId;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getOpenFrom() {
        return openFrom;
    }

    public void setOpenFrom(String openFrom) {
        this.openFrom = openFrom;
    }

    public String getOpenTo() {
        return openTo;
    }

    public void setOpenTo(String openTo) {
        this.openTo = openTo;
    }

    public String getAvgCost() {
        return avgCost;
    }

    public void setAvgCost(String avgCost) {
        this.avgCost = avgCost;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getVenueImages() {
        return venueImages;
    }

    public void setVenueImages(String venueImages) {
        this.venueImages = venueImages;
    }

}
