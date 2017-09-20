package com.ontro.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Android on 20-Feb-17.
 */

public class ExploreModel implements Serializable {
    private String exploreName;
    private String exploreLocation;
    private String exploreId;
    private String exploreImage;
    private String exploreSport;
    private String isOwner;
    private int exploreBatch;
    private int isChecked;
    private List<PlayerInviteStatus> inviteStatuses;

    public String getExploreName() {
        return exploreName;
    }

    public void setExploreName(String exploreName) {
        this.exploreName = exploreName;
    }

    public String getExploreLocation() {
        return exploreLocation;
    }

    public void setExploreLocation(String exploreLocation) {
        this.exploreLocation = exploreLocation;
    }

    public String getExploreImage() {
        return exploreImage;
    }

    public void setExploreImage(String exploreImage) {
        this.exploreImage = exploreImage;
    }

    public int getExploreBatch() {
        return exploreBatch;
    }

    public void setExploreBatch(int exploreBatch) {
        this.exploreBatch = exploreBatch;
    }

    public String getExploreId() {
        return exploreId;
    }

    public void setExploreId(String exploreId) {
        this.exploreId = exploreId;
    }

    public int getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(int isChecked) {
        this.isChecked = isChecked;
    }

    public String getExploreSport() {
        return exploreSport;
    }

    public void setExploreSport(String exploreSport) {
        this.exploreSport = exploreSport;
    }

    public String getIsOwner() {
        return isOwner;
    }

    public void setIsOwner(String isOwner) {
        this.isOwner = isOwner;
    }


    public List<PlayerInviteStatus> getInviteStatuses() {
        return inviteStatuses;
    }

    public void setInviteStatuses(List<PlayerInviteStatus> inviteStatuses) {
        this.inviteStatuses = inviteStatuses;
    }
}
