package com.ontro.dto;

import java.io.Serializable;

/**
 * Created by IDEOMIND02 on 31-08-2017.
 */

public class PlayerInviteStatus implements Serializable {
    private String teamId;
    private String inviteStatus;
    private String inviteId;

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getInviteStatus() {
        return inviteStatus;
    }

    public void setInviteStatus(String inviteStatus) {
        this.inviteStatus = inviteStatus;
    }

    public String getInviteId() {
        return inviteId;
    }

    public void setInviteId(String inviteId) {
        this.inviteId = inviteId;
    }
}
