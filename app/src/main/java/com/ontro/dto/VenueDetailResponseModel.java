package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by IDEOMIND02 on 5/10/2017.
 */

public class VenueDetailResponseModel {
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
    @SerializedName("amenities")
    private List<VenueAmenityModel> amenities;
    @SerializedName("sport")
    private String sport;
    @SerializedName("images")
    private List<VenueImageModel> images = null;

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

    public List<VenueAmenityModel> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<VenueAmenityModel> amenities) {
        this.amenities = amenities;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public List<VenueImageModel> getImages() {
        return images;
    }

    public void setImages(List<VenueImageModel> images) {
        this.images = images;
    }

}
