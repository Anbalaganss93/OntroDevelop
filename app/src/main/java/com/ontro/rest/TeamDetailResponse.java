package com.ontro.rest;

import com.google.gson.annotations.SerializedName;
import com.ontro.dto.SquadInfo;
import com.ontro.dto.TeamRecord;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by umm
 */

public class TeamDetailResponse implements Serializable {
    @SerializedName("team_id")
    private String teamId;
    @SerializedName("team_owner")
    private String teamOwner;
    @SerializedName("team_name")
    private String teamName;
    @SerializedName("team_about")
    private String teamAbout;
    @SerializedName("sport")
    private String sport;
    @SerializedName("badge")
    private String badge;
    @SerializedName("level")
    private String level;
    @SerializedName("points")
    private Double points;
    @SerializedName("team_logo")
    private String teamLogo;
    @SerializedName("team_location")
    private String teamLocation;
    @SerializedName("location_id")
    private String locationId;
    @SerializedName("is_woner")
    private String isOwner;
    @SerializedName("login_user_allow_invites")
    private String loginUserAllowInvites;
    @SerializedName("its_invited")
    private String itsInvited;
    @SerializedName("team_record")
    private TeamRecord teamRecord;
    @SerializedName("team_players")
    private List<SquadInfo> squadInfos = null;
    @SerializedName("progress")
    private String progress;

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getTeamOwner() {
        return teamOwner;
    }

    public void setTeamOwner(String teamOwner) {
        this.teamOwner = teamOwner;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamAbout() {
        return teamAbout;
    }

    public void setTeamAbout(String teamAbout) {
        this.teamAbout = teamAbout;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Double getPoints() {
        return points;
    }

    public void setPoints(Double points) {
        this.points = points;
    }

    public String getTeamLogo() {
        return teamLogo;
    }

    public void setTeamLogo(String teamLogo) {
        this.teamLogo = teamLogo;
    }

    public String getTeamLocation() {
        return teamLocation;
    }

    public void setTeamLocation(String teamLocation) {
        this.teamLocation = teamLocation;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getIsOwner() {
        return isOwner;
    }

    public void setIsOwner(String isOwner) {
        this.isOwner = isOwner;
    }

    public String getLoginUserAllowInvites() {
        return loginUserAllowInvites;
    }

    public void setLoginUserAllowInvites(String loginUserAllowInvites) {
        this.loginUserAllowInvites = loginUserAllowInvites;
    }

    public String getItsInvited() {
        return itsInvited;
    }

    public void setItsInvited(String itsInvited) {
        this.itsInvited = itsInvited;
    }

    public TeamRecord getTeamRecord() {
        return teamRecord;
    }

    public void setTeamRecord(TeamRecord teamRecord) {
        this.teamRecord = teamRecord;
    }

    public List<SquadInfo> getSquadInfos() {
        return squadInfos;
    }

    public void setSquadInfos(List<SquadInfo> teamPlayers) {
        this.squadInfos = teamPlayers;
    }
    
    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

}

