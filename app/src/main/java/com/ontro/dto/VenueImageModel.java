package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IDEOMIND02 on 5/11/2017.
 */

public class VenueImageModel {
    @SerializedName("gallery_id")
    private Integer galleryId;
    @SerializedName("venues_id")
    private Integer venuesId;
    @SerializedName("image")
    private String image;

    public Integer getGalleryId() {
        return galleryId;
    }

    public void setGalleryId(Integer galleryId) {
        this.galleryId = galleryId;
    }

    public Integer getVenuesId() {
        return venuesId;
    }

    public void setVenuesId(Integer venuesId) {
        this.venuesId = venuesId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
