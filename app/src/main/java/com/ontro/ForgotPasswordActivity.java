package com.ontro;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ontro.dto.ResetPasswordRequest;
import com.ontro.rest.ApiClient;
import com.ontro.rest.ApiInterface;
import com.ontro.utils.CommonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener {
    private EditText mUserEmailEditText;
    private Button mSendButton;
    private Dialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        overridePendingTransition(R.anim.slide_in_right, R.anim.pause);
        initView();
        setTypeface();
        mSendButton.setOnClickListener(this);
        mSendButton.setEnabled(true);
        mUserEmailEditText.setOnEditorActionListener(this);
    }

    private void initView() {
        mUserEmailEditText = (EditText) findViewById(R.id.activity_forgot_password_et_email);
        mSendButton = (Button) findViewById(R.id.activity_forgot_password_btn_send);

        mProgressDialog = new Dialog(ForgotPasswordActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setContentView(R.layout.progressdialog_layout);
    }

    private void setTypeface() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/roboto_regular.ttf");
        mSendButton.setTypeface(typeface);
        mUserEmailEditText.setTypeface(typeface);
    }

    @Override
    public void onClick(View v) {
        onSendClicked(mUserEmailEditText.getText().toString().trim());
    }

    private void onSendClicked(String email) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(ForgotPasswordActivity.this, Constants.Messages.ENTER_EMAIL, Toast.LENGTH_LONG).show();
            return;
        } else if (!CommonUtils.isValidEmail(email)) {
            Toast.makeText(ForgotPasswordActivity.this, Constants.Messages.ENTER_VALID_EMAIL, Toast.LENGTH_LONG).show();
            return;
        } else {
            mSendButton.setEnabled(false);
            mProgressDialog.show();
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest();
            resetPasswordRequest.setEmail(email);
            Call<ResponseBody> call = apiInterface.getResetPasswordResponse(resetPasswordRequest);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code() == 200) {
                        if (response.body() != null) {
                            try {
                                String data = response.body().string();
                                JSONObject jsonObject = new JSONObject(data);
                                String msg = jsonObject.getString("message");
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                                finish();
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                String error = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(error);
                                String msg = jsonObject.getString("message");
                                String code = jsonObject.getString("code");
                                if (!code.equals("500")) {
                                    Toast.makeText(ForgotPasswordActivity.this, msg, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ForgotPasswordActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                String error = response.message();
                                Toast.makeText(ForgotPasswordActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            String error = response.message();
                            Toast.makeText(ForgotPasswordActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    mSendButton.setEnabled(true);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    mSendButton.setEnabled(true);
                    try {
                        if (t instanceof SocketTimeoutException) {
                            Toast.makeText(ForgotPasswordActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                }
            });
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            onSendClicked(mUserEmailEditText.getText().toString().trim());
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
    }
}
