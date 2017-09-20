package com.ontro.dto;

import java.util.List;

/**
 * Created by Android on 20-Feb-17.
 */

public class SportModel {
    private String sportname, progress_percent, sportid, location, badge,teamlogo,playerpositionstatus;
    private int sportimage;
    private int selected,iswoner,playerid;
    private int batchimage;
    private String teamAbout;
    private int haveTeam;

    public int getPlayerid() {
        return playerid;
    }

    public void setPlayerid(int playerid) {
        this.playerid = playerid;
    }

    public int getSelectedposition() {
        return selectedposition;
    }

    public void setSelectedposition(int selectedposition) {
        this.selectedposition = selectedposition;
    }

    private int selectedposition;
    private List<String> mPlayerposition;

    public String getPlayerpositionstatus() {
        return playerpositionstatus;
    }

    public void setPlayerpositionstatus(String playerpositionstatus) {
        this.playerpositionstatus = playerpositionstatus;
    }

    public List<String> getmPlayerposition() {
        return mPlayerposition;
    }

    public void setmPlayerposition(List<String> mPlayerposition) {
        this.mPlayerposition = mPlayerposition;
    }

    public int getIswoner() {
        return iswoner;
    }

    public void setIswoner(int iswoner) {
        this.iswoner = iswoner;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public String getSportname() {
        return sportname;
    }

    public void setSportname(String sportname) {
        this.sportname = sportname;
    }

    public int getSportimage() {
        return sportimage;
    }

    public void setSportimage(int sportimage) {
        this.sportimage = sportimage;
    }

    public String getProgress_percent() {
        return progress_percent;
    }

    public void setProgress_percent(String progress_percent) {
        this.progress_percent = progress_percent;
    }

    public String getSportid() {
        return sportid;
    }

    public void setSportid(String sportid) {
        this.sportid = sportid;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public int getBatchimage() {
        return batchimage;
    }

    public void setBatchimage(int batchimage) {
        this.batchimage = batchimage;
    }

    public String getTeamlogo() {
        return teamlogo;
    }

    public void setTeamlogo(String teamlogo) {
        this.teamlogo = teamlogo;
    }

    public String getTeamAbout() {
        return teamAbout;
    }

    public void setTeamAbout(String teamAbout) {
        this.teamAbout = teamAbout;
    }

    public int getHaveTeam() {
        return haveTeam;
    }

    public void setHaveTeam(int haveTeam) {
        this.haveTeam = haveTeam;
    }
}
