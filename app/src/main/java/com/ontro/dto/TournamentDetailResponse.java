package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IDEOMIND02 on 28-06-2017.
 */

public class TournamentDetailResponse implements Serializable {

    @SerializedName("tournament_name")
    private String tournamentName;
    @SerializedName("sports_type")
    private String sportsType;
    @SerializedName("logo")
    private String logo;
    @SerializedName("advert_pic")
    private String advertPic;
    @SerializedName("form_id")
    private Integer formId;
    @SerializedName("person1")
    private String person1;
    @SerializedName("person2")
    private String person2;
    @SerializedName("overview")
    private Overview overview;
    @SerializedName("rulesAndRegulations")
    private RulesAndRegulations rulesAndRegulations;
    @SerializedName("dateAndVenue")
    private DateAndVenue dateAndVenue;
    @SerializedName("price")
    private List<TournamentPrice> price = null;

    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    public String getSportsType() {
        return sportsType;
    }

    public void setSportsType(String sportsType) {
        this.sportsType = sportsType;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getAdvertPic() {
        return advertPic;
    }

    public void setAdvertPic(String advertPic) {
        this.advertPic = advertPic;
    }

    public Integer getFormId() {
        return formId;
    }

    public void setFormId(Integer formId) {
        this.formId = formId;
    }

    public String getPerson1() {
        return person1;
    }

    public void setPerson1(String person1) {
        this.person1 = person1;
    }

    public String getPerson2() {
        return person2;
    }

    public void setPerson2(String person2) {
        this.person2 = person2;
    }

    public Overview getOverview() {
        return overview;
    }

    public void setOverview(Overview overview) {
        this.overview = overview;
    }

    public RulesAndRegulations getRulesAndRegulations() {
        return rulesAndRegulations;
    }

    public void setRulesAndRegulations(RulesAndRegulations rulesAndRegulations) {
        this.rulesAndRegulations = rulesAndRegulations;
    }

    public DateAndVenue getDateAndVenue() {
        return dateAndVenue;
    }

    public void setDateAndVenue(DateAndVenue dateAndVenue) {
        this.dateAndVenue = dateAndVenue;
    }

    public List<TournamentPrice> getPrice() {
        return price;
    }

    public void setPrice(List<TournamentPrice> price) {
        this.price = price;
    }

}
