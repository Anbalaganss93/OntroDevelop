package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IDEOMIND02 on 5/12/2017.
 */

public class VenueAmenityModel {
    @SerializedName("amenitie")
    private String amenity;

    public String getAmenity() {
        return amenity;
    }

    public void setAmenity(String amenity) {
        this.amenity = amenity;
    }
}
