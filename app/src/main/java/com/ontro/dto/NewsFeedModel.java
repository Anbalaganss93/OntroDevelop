package com.ontro.dto;

import java.io.Serializable;

/**
 * Created by Android on 22-Feb-17.
 */

public class NewsFeedModel implements Serializable {
    private String description,hours_ago,name,likes,newsfeed_id,image,is_liked;
    private int type, tournamentId;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHours_ago() {
        return hours_ago;
    }

    public void setHours_ago(String hours_ago) {
        this.hours_ago = hours_ago;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getNewsfeed_id() {
        return newsfeed_id;
    }

    public void setNewsfeed_id(String newsfeed_id) {
        this.newsfeed_id = newsfeed_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIs_liked() {
        return is_liked;
    }

    public void setIs_liked(String is_liked) {
        this.is_liked = is_liked;
    }

    public int getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(int tournamentId) {
        this.tournamentId = tournamentId;
    }
}
