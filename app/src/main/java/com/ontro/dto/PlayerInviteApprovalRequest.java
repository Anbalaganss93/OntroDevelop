package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IDEOMIND02 on 03-06-2017.
 */

public class PlayerInviteApprovalRequest {
    @SerializedName("invite_id")
    private Integer inviteId;
    @SerializedName("status")
    private Integer inviteStatus;

    public Integer getInviteId() {
        return inviteId;
    }

    public void setInviteId(Integer inviteId) {
        this.inviteId = inviteId;
    }

    public Integer getInviteStatus() {
        return inviteStatus;
    }

    public void setInviteStatus(Integer inviteStatus) {
        this.inviteStatus = inviteStatus;
    }
}
