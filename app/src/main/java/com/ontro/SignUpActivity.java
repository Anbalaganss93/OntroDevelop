package com.ontro;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.ontro.rest.ApiClient;
import com.ontro.rest.ApiInterface;
import com.ontro.rest.RegisterInputDTO;
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

import static com.ontro.firebase.MyFirebaseMessagingService.TAG;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener {
    boolean cancel = false;
    private EditText et_email, et_name, et_password, et_number, et_confirm_password;
    private CheckBox cb_password, cb_confirm_password;
    private ApiInterface apiInterface;
    private String name;
    private String email;
    private String phone;
    private String password;
    private PreferenceHelper preferenceHelper;
    private Dialog progress;
    private FirebaseAuth mFirebaseAuth;
    private Button signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        overridePendingTransition(R.anim.slide_in_right, R.anim.pause);
        Init();
        mFirebaseAuth = FirebaseAuth.getInstance();
        cb_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int start, end;
                if (!b) {
                    start = et_password.getSelectionStart();
                    end = et_password.getSelectionEnd();
                    et_password.setTransformationMethod(new PasswordTransformationMethod());
                    et_password.setSelection(start, end);
                } else {
                    start = et_password.getSelectionStart();
                    end = et_password.getSelectionEnd();
                    et_password.setTransformationMethod(null);
                    et_password.setSelection(start, end);
                }
            }
        });

        cb_confirm_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int start, end;
                if (!b) {
                    start = et_confirm_password.getSelectionStart();
                    end = et_confirm_password.getSelectionEnd();
                    et_confirm_password.setTransformationMethod(new PasswordTransformationMethod());
                    et_confirm_password.setSelection(start, end);
                } else {
                    start = et_confirm_password.getSelectionStart();
                    end = et_confirm_password.getSelectionEnd();
                    et_confirm_password.setTransformationMethod(null);
                    et_confirm_password.setSelection(start, end);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
    }

    private void Init() {
        preferenceHelper = new PreferenceHelper(this, Constants.APP_NAME, 0);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        progress = new Dialog(SignUpActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progress.setContentView(R.layout.progressdialog_layout);

        Typeface typeface_regular = Typeface.createFromAsset(getAssets(), "fonts/roboto_regular.ttf");
        ImageView imageView2 = (ImageView) findViewById(R.id.imageView2);
        et_email = (EditText) findViewById(R.id.signup_email);
        et_name = (EditText) findViewById(R.id.signup_username);
        et_password = (EditText) findViewById(R.id.signup_password);
        et_number = (EditText) findViewById(R.id.signup_phonenumber);
        et_confirm_password = (EditText) findViewById(R.id.signup_confirm_password);

        TextView login_in_singup = (TextView) findViewById(R.id.login_in_singup);
        cb_password = (CheckBox) findViewById(R.id.view_password);
        cb_confirm_password = (CheckBox) findViewById(R.id.view_cpassword);

        RelativeLayout outertouch = (RelativeLayout) findViewById(R.id.outertouch);
        signup = (Button) findViewById(R.id.signup);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // only for LOLLIPOP and newer versions
            signup.setBackground(ContextCompat.getDrawable(this, R.drawable.button_bg));
        } else {
            signup.setBackground(ContextCompat.getDrawable(this, R.drawable.button_bg_normal));
        }

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params2.topMargin = (int) (height * 0.09);
        params2.gravity = Gravity.CENTER_HORIZONTAL;
        imageView2.setLayoutParams(params2);

        et_name.setTypeface(typeface_regular);
        et_email.setTypeface(typeface_regular);
        et_password.setTypeface(typeface_regular);
        et_number.setTypeface(typeface_regular);
        signup.setTypeface(typeface_regular);
        et_confirm_password.setTypeface(typeface_regular);
        signup.setEnabled(true);

        signup.setOnClickListener(this);
        et_number.setOnEditorActionListener(this);
        login_in_singup.setOnClickListener(this);
        outertouch.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signup:
                onSignUpClicked();
                break;

            case R.id.login_in_singup:
//                finish();
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;

            case R.id.outertouch:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                break;
        }
    }

    private void onSignUpClicked() {
        if (CommonUtils.isNetworkAvailable(this)) {
            Registration();
        } else {
            signup.setEnabled(true);
            Toast.makeText(SignUpActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }
    }

    private void Registration() {
        boolean value = Validate();

        if (value) {
            progress.show();
            signup.setEnabled(false);
            mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                            if (task.isSuccessful()) {
                                final FirebaseUser user = mFirebaseAuth.getCurrentUser();
                                Uri photoUrl = user.getPhotoUrl();
                                if (photoUrl == null) {
                                    photoUrl = Uri.parse("http://ideomind.in/demo/ontro/public/img/default.jpg");
                                }
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .setPhotoUri(photoUrl)
                                        .build();
                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "User profile updated.");
                                                }
                                            }
                                        });
                                user.getToken(true)
                                        .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                            public void onComplete(@NonNull Task<GetTokenResult> task) {
                                                if (task.isSuccessful()) {
                                                    String idToken = task.getResult().getToken();
                                                    Log.d("Firebase Token", idToken);
                                                    preferenceHelper.save("firebase_token", idToken);
                                                    RegisterInputDTO registerInputDTO = new RegisterInputDTO(name, email, phone, password);
                                                    Call<ResponseBody> call1 = apiInterface.User_registeration(registerInputDTO);
                                                    call1.enqueue(new Callback<ResponseBody>() {
                                                        @Override
                                                        public void onResponse(Call<ResponseBody> call1, Response<ResponseBody> response) {
                                                            signup.setEnabled(true);
                                                            if (response.body() != null && response.code() == 200) {
                                                                try {
                                                                    String user = response.body().string();
                                                                    JSONObject jsonObject = new JSONObject(user);
                                                                    String userId = jsonObject.getString("user_id");
                                                                    String name = jsonObject.getString("name");
                                                                    String email = jsonObject.getString("email");
                                                                    String phone = jsonObject.getString("phone");
                                                                    String is_profile = jsonObject.getString("is_profile_completed");
                                                                    String is_phone = jsonObject.getString("is_phone_verified");
                                                                    String token = jsonObject.getString("access_token");
                                                                    UpdateToken(userId, name, email, phone, is_profile, is_phone, token);

                                                                } catch (IOException | JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            } else {
                                                                if (response.errorBody() != null) {
                                                                    try {
                                                                        String error = response.errorBody().string();
                                                                        JSONObject jsonObject = new JSONObject(error);
                                                                        String msg = jsonObject.getString("message");
                                                                        String code = jsonObject.getString("code");
                                                                        if (!code.equals("500")) {
                                                                            Toast.makeText(SignUpActivity.this, msg, Toast.LENGTH_SHORT).show();
                                                                        } else {
                                                                            Toast.makeText(SignUpActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                                                                        }
                                                                        if(code.equals("1050")) {
                                                                            user.delete()
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                Log.d(TAG, "User account deleted.");
                                                                                            }
                                                                                        }
                                                                                    });
                                                                        }
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                        String error = response.message();
                                                                        Toast.makeText(SignUpActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                                                                    } catch (IOException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                } else {
                                                                    String error = response.message();
                                                                    Toast.makeText(SignUpActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                            if (progress.isShowing()) {
                                                                progress.dismiss();
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(Call<ResponseBody> call1, Throwable t) {
                                                            // Log error here since request failed
                                                            signup.setEnabled(true);
                                                            CommonUtils.ServerFailureHandleMethod(SignUpActivity.this, t);
                                                            if (progress.isShowing()) {
                                                                progress.dismiss();
                                                            }
                                                        }
                                                    });

                                                } else {
                                                    signup.setEnabled(true);
                                                    Log.e(TAG, "Authentication failed.");
                                                }
                                            }
                                        });
                            } else {
                                signup.setEnabled(true);
                                Log.e(TAG, "Authentication failed.");
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (e instanceof FirebaseAuthInvalidUserException) {
                                ((FirebaseAuthInvalidUserException) e).getErrorCode();
                            } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                ((FirebaseAuthInvalidCredentialsException) e).getErrorCode();
                            } else if (e instanceof FirebaseAuthUserCollisionException) {
                                FirebaseAuthUserCollisionException exception = (FirebaseAuthUserCollisionException) e;
                                Toast.makeText(SignUpActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                            } else {
                                e.printStackTrace();
                            }
                            if (progress.isShowing()) {
                                progress.dismiss();
                            }
                        }
                    });
        }
    }

    private void UpdateToken(String userId, String name, String email, String phone, String is_profile, String is_phone, String token) {
        preferenceHelper.save("user_id", userId);
        preferenceHelper.save("user_name", name);
        preferenceHelper.save("user_email", email);
        preferenceHelper.save("user_phone", phone);
        preferenceHelper.save("is_profile", is_profile);
        preferenceHelper.save("is_phone", is_phone);
        preferenceHelper.save("is_logged", true);
        preferenceHelper.save("user_token", token);

        String isProfile = preferenceHelper.getString("is_profile", "0");
        String isOtp = preferenceHelper.getString("is_phone", "0");
        if (isProfile.equals("0")) {
            Intent intent = new Intent(SignUpActivity.this, ProfileCompletionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Constants.BundleKeys.PROFILE_COMPLETION, Constants.Messages.SIGN_UP);
            if (preferenceHelper.contains("user_location"))
                preferenceHelper.save("user_location", "");
            if (preferenceHelper.contains("user_dob"))
                preferenceHelper.save("user_dob", "");
            if (preferenceHelper.contains("user_location_name"))
                preferenceHelper.save("user_location_name", "");
            if (preferenceHelper.contains("user_height"))
                preferenceHelper.save("user_height", "");
            if (preferenceHelper.contains("user_gender"))
                preferenceHelper.save("user_gender", "");
            startActivity(intent);
        } else if (isOtp.equals("0")) {
            Intent otpintent = new Intent(SignUpActivity.this, OTPActivity.class);
            otpintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(otpintent);
        } else {
            Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }


    private boolean Validate() {
        name = et_name.getText().toString();
        email = et_email.getText().toString();
        phone = et_number.getText().toString();
        password = et_password.getText().toString();
        String r_pasword = et_confirm_password.getText().toString();

        if (name.isEmpty() && phone.isEmpty() && password.isEmpty() && phone.isEmpty()) {
            Toast.makeText(SignUpActivity.this, Constants.Messages.EMPTY_FIELDS, Toast.LENGTH_SHORT).show();
            return false;
        } else if (email.isEmpty()) {
            Toast.makeText(SignUpActivity.this, Constants.Messages.ENTER_EMAIL, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!CommonUtils.isValidEmail(email)) {
            Toast.makeText(SignUpActivity.this, Constants.Messages.ENTER_VALID_EMAIL, Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.isEmpty()) {
            Toast.makeText(SignUpActivity.this, Constants.Messages.ENTER_PASSWORD, Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() < 8) {
            Toast.makeText(SignUpActivity.this, Constants.Messages.PASSWORD_LENGTH_ERROR, Toast.LENGTH_SHORT).show();
            return false;
        } else if (r_pasword.isEmpty()) {
            Toast.makeText(SignUpActivity.this, Constants.Messages.ENTER_RE_ENTER_PASSWORD, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!password.equalsIgnoreCase(r_pasword)) {
            Toast.makeText(SignUpActivity.this, Constants.Messages.PASSWORD_MISMATCH, Toast.LENGTH_SHORT).show();
            return false;
        } else if (phone.isEmpty()) {
            Toast.makeText(SignUpActivity.this, Constants.Messages.ENTER_MOBILE_NUMBER, Toast.LENGTH_SHORT).show();
            return false;
        } else if (phone.length() < 10 || !phone.matches("[7-9][0-9]{9}")) {
            Toast.makeText(SignUpActivity.this, Constants.Messages.ENTER_VALID_MOBILE_NO, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            onSignUpClicked();
            return true;
        }
        return false;
    }
}
