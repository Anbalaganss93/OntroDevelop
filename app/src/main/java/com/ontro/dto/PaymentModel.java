package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Android on 09-Jun-17.
 */

public class PaymentModel {
    @SerializedName("match_id")
    private String paymentMatchId;
    @SerializedName("team_id")
    private String paymentTeamId;
    @SerializedName("amount")
    private String paymentAmount;

    public String getPaymentMatchId() {
        return paymentMatchId;
    }

    public void setPaymentMatchId(String paymentMatchId) {
        this.paymentMatchId = paymentMatchId;
    }

    public String getPaymentTeamId() {
        return paymentTeamId;
    }

    public void setPaymentTeamId(String paymentTeamId) {
        this.paymentTeamId = paymentTeamId;
    }

    public String getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(String paymentAmount) {
        this.paymentAmount = paymentAmount;
    }
}
