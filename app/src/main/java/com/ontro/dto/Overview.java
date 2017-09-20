package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by IDEOMIND02 on 28-06-2017.
 */

public class Overview implements Serializable {
    @SerializedName("organization_name")
    private String organizationName;
    @SerializedName("description")
    private String description;
    @SerializedName("fb_link")
    private String fbLink;
    @SerializedName("twitter_link")
    private String twitterLink;
    @SerializedName("web_url")
    private String webUrl;
    @SerializedName("fixtures")
    private String fixtures;

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFbLink() {
        return fbLink;
    }

    public void setFbLink(String fbLink) {
        this.fbLink = fbLink;
    }

    public String getTwitterLink() {
        return twitterLink;
    }

    public void setTwitterLink(String twitterLink) {
        this.twitterLink = twitterLink;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getFixtures() {
        return fixtures;
    }

    public void setFixtures(String fixtures) {
        this.fixtures = fixtures;
    }
}