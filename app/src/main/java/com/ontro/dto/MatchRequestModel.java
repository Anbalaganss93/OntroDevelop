package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Android on 08-May-17.
 */

public class MatchRequestModel implements Serializable{
    @SerializedName("match_id")
    private String matchId;
    @SerializedName("match_status")
    private String matchStatus;
    @SerializedName("match_type")
    private String matchType;
    @SerializedName("match_date")
    private String matchDate;
    @SerializedName("team_id")
    private String opponentTeamId;
    @SerializedName("team_name")
    private String opponentTeamName;
    @SerializedName("team_logo")
    private String opponentTeamLogo;
    @SerializedName("team_sport")
    private String teamSport;
    @SerializedName("your_team_id")
    private String myTeamId;
    @SerializedName("your_team_name")
    private String myTeamName;
    @SerializedName("your_team_logo")
    private String myTeamLogo;
    @SerializedName("request_date")
    private String requestDate;
    @SerializedName("booking_date")
    private String bookingDate;
    @SerializedName("from_time")
    private String fromTime;
    @SerializedName("to_time")
    private String toTime;
    @SerializedName("status_message")
    private String statusMessage;
    @SerializedName("status")
    private String status;
    @SerializedName("location_name")
    private String matchLocation;

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
    }

    public String getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(String matchDate) {
        this.matchDate = matchDate;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getOpponentTeamId() {
        return opponentTeamId;
    }

    public void setOpponentTeamId(String opponentTeamId) {
        this.opponentTeamId = opponentTeamId;
    }

    public String getOpponentTeamName() {
        return opponentTeamName;
    }

    public void setOpponentTeamName(String opponentTeamName) {
        this.opponentTeamName = opponentTeamName;
    }

    public String getOpponentTeamLogo() {
        return opponentTeamLogo;
    }

    public void setOpponentTeamLogo(String opponentTeamLogo) {
        this.opponentTeamLogo = opponentTeamLogo;
    }

    public String getTeamSport() {
        return teamSport;
    }

    public void setTeamSport(String teamSport) {
        this.teamSport = teamSport;
    }

    public String getMyTeamId() {
        return myTeamId;
    }

    public void setMyTeamId(String myTeamId) {
        this.myTeamId = myTeamId;
    }

    public String getMyTeamName() {
        return myTeamName;
    }

    public void setMyTeamName(String myTeamName) {
        this.myTeamName = myTeamName;
    }

    public String getMyTeamLogo() {
        return myTeamLogo;
    }

    public void setMyTeamLogo(String myTeamLogo) {
        this.myTeamLogo = myTeamLogo;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
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

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMatchLocation() {
        return matchLocation;
    }

    public void setMatchLocation(String matchLocation) {
        this.matchLocation = matchLocation;
    }
}
