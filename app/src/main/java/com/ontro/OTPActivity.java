package com.ontro;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ontro.dto.OTPinput;
import com.ontro.rest.ApiClient;
import com.ontro.rest.ApiInterface;
import com.ontro.utils.CommonUtils;
import com.ontro.utils.PreferenceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPActivity extends AppCompatActivity implements View.OnClickListener {
    EditText et_otp_code;
    private PreferenceHelper preferenceHelper;
    private ApiInterface apiInterface;
    private Dialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        overridePendingTransition(R.anim.slide_in_right, R.anim.pause);
        Typeface typeface_regular = Typeface.createFromAsset(getAssets(), "fonts/roboto_regular.ttf");
        progress = new Dialog(OTPActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progress.setContentView(R.layout.progressdialog_layout);

        Button submit = (Button) findViewById(R.id.activity_explore_player_list_btn_done);
        et_otp_code = (EditText) findViewById(R.id.et_otp);
        TextView resendotp = (TextView) findViewById(R.id.resendotp);
        LinearLayout activity_otp = (LinearLayout) findViewById(R.id.activity_otp);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        preferenceHelper = new PreferenceHelper(OTPActivity.this, Constants.APP_NAME, 0);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // only for LOLLIPOP and newer versions
            submit.setBackground(ContextCompat.getDrawable(this, R.drawable.button_bg));
        } else {
            submit.setBackground(ContextCompat.getDrawable(this, R.drawable.button_bg_normal));
        }
        if (CommonUtils.isNetworkAvailable(OTPActivity.this)) {
            opt_generation_servercall();
        } else {
            Toast.makeText(OTPActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }
        submit.setTypeface(typeface_regular);
        et_otp_code.setTypeface(typeface_regular);

        submit.setOnClickListener(this);
        resendotp.setOnClickListener(this);
        activity_otp.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_explore_player_list_btn_done:
                if (CommonUtils.isNetworkAvailable(OTPActivity.this)) {
                    opt_verify_servercall();
                } else {
                    Toast.makeText(OTPActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.activity_otp:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                break;
            case R.id.resendotp:
                if (CommonUtils.isNetworkAvailable(OTPActivity.this)) {
                    opt_generation_servercall();
                } else {
                    Toast.makeText(OTPActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void opt_generation_servercall() {
        progress.show();
        String auth_token = "Bearer " + preferenceHelper.getString("user_token", "");
        Call<ResponseBody> call = apiInterface.GenerateOTP(auth_token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        JSONObject json = new JSONObject(data);
                        JSONObject otpjson = new JSONObject(json.getString("data"));
                        String otp_data = otpjson.getString("otp");
                        Toast.makeText(OTPActivity.this, "Enter received OTP", Toast.LENGTH_SHORT).show();
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                String error = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(error);
                                String msg = jsonObject.getString("message");
                                String code = jsonObject.getString("code");
                                if (!code.equals("500")) {
                                    Toast.makeText(OTPActivity.this, msg, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(OTPActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                String error = response.message();
                                Toast.makeText(OTPActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            String error = response.message();
                            Toast.makeText(OTPActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (progress.isShowing()) {
                    progress.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //  Log error here since request failed
                try {
                    if (t instanceof SocketTimeoutException) {
                        Toast.makeText(OTPActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(OTPActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (progress.isShowing()) {
                    progress.dismiss();
                }
            }
        });
    }

    public void opt_verify_servercall() {
        String otp_no = et_otp_code.getText().toString().trim();
        if (otp_no.length() == 0) {
            Toast.makeText(OTPActivity.this, "Enter received OTP", Toast.LENGTH_SHORT).show();
            return;
        }
        progress.show();
        String auth_token = "Bearer " + preferenceHelper.getString("user_token", "");
        OTPinput otPinput = new OTPinput();
        otPinput.setOtp(otp_no);
        Call<ResponseBody> call = apiInterface.VerifyOTP(auth_token, otPinput);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        JSONObject json = new JSONObject(data);
                        String message = json.getString("message");
                        Toast.makeText(OTPActivity.this, "OTP successfully verified", Toast.LENGTH_SHORT).show();
                        preferenceHelper.save("is_phone", "1");
                        Intent profile_intent = new Intent(OTPActivity.this, HomeActivity.class);
                        profile_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(profile_intent);

                    } else {
                        if (response.body() != null) {
                            String error = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(error);
                            String msg = jsonObject.getString("message");
                            String code = jsonObject.getString("code");
                            if (!code.equals("500")) {
                                Toast.makeText(OTPActivity.this, msg, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(OTPActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String error = response.message();
                            if(error.equals("Bad Request"))  {
                                error = "Invalid OTP";
                            }
                            Toast.makeText(OTPActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (progress.isShowing()) {
                    progress.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //  Log error here since request failed
                try {
                    if (t instanceof SocketTimeoutException) {
                        Toast.makeText(OTPActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(OTPActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (progress.isShowing()) {
                    progress.dismiss();
                }
            }
        });
    }
}
