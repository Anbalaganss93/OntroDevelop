package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IDEOMIND02 on 02-09-2017.
 */

public class PlayerInviteCancelRequest {
    @SerializedName("invite_id")
    private int inviteId;

    public int getInviteId() {
        return inviteId;
    }

    public void setInviteId(int inviteId) {
        this.inviteId = inviteId;
    }
}
