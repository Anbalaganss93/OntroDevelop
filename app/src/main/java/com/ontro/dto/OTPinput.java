package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Android on 03-May-17.
 */

public class OTPinput {
    @SerializedName("otp")
    private String otp;

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
