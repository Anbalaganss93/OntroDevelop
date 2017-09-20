package com.ontro;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.ontro.rest.ApiClient;
import com.ontro.rest.ApiInterface;
import com.ontro.rest.LoginInputDTO;
import com.ontro.rest.SocialAuthendicationInput;
import com.ontro.utils.CommonUtils;
import com.ontro.utils.PreferenceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, TextView.OnEditorActionListener {
    private static final int RC_SIGN_IN = 0;
    private static final String TAG = "FirebaseAuth";
    // Firebase instance variables
    private static FirebaseAuth mFirebaseAuth;
    private static FirebaseAuth.AuthStateListener mFirebaseAuthListener;
    private EditText login_email, login_password;
    private GoogleApiClient mGoogleApiClient;
    private CallbackManager mcallbackmanager;
    private ApiInterface apiInterface;
    private PreferenceHelper preferenceHelper;
    private ProgressDialog mProgressDialog;
    private String password, username, personPhotoUrl, social_id, usermail, email;
    private MixpanelAPI mMixpanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        overridePendingTransition(R.anim.slide_in_right, R.anim.pause);
        mMixpanel = MixpanelAPI.getInstance(this, getResources().getString(R.string.mixpanel_token));
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Uri photoUrl = user.getPhotoUrl();
                    if (photoUrl == null) {
                        photoUrl = Uri.parse("http://ideomind.in/demo/ontro/public/img/default.jpg");
                    }
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(user.getDisplayName())
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
                } else {
                    // User is signed out
                    Log.d(TAG, "signed_out");
                }
            }
        };

        String serverClientId = BuildConfig.GOOGLE_SERVER_CLIENT;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(serverClientId)
                .requestEmail()
                .build();

        // Initializing google plus api client
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        preferenceHelper = new PreferenceHelper(this, Constants.APP_NAME, 0);

        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        mProgressDialog = new ProgressDialog(LoginActivity.this);
        mProgressDialog.setMessage("Verifying credential...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, 10);

        Typeface typeface_regular = Typeface.createFromAsset(getAssets(), "fonts/roboto_regular.ttf");
        CheckBox cb_password = (CheckBox) findViewById(R.id.checkBox);
        ImageView imageView2 = (ImageView) findViewById(R.id.imageView2);
        login_email = (EditText) findViewById(R.id.login_email);
        login_password = (EditText) findViewById(R.id.login_password);
        Button btn_login = (Button) findViewById(R.id.login);
        RelativeLayout outertouch = (RelativeLayout) findViewById(R.id.outertouch);
        RelativeLayout google = (RelativeLayout) findViewById(R.id.google);

        TextView facebook_text = (TextView) findViewById(R.id.facebook_text);
        TextView google_text = (TextView) findViewById(R.id.google_text);
        TextView forgot_password = (TextView) findViewById(R.id.forgot_password);
        RelativeLayout facebook = (RelativeLayout) findViewById(R.id.facebook);
        TextView signup_in_login = (TextView) findViewById(R.id.signup_in_login);

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params2.topMargin = (int) (height * 0.09);
        params2.gravity = Gravity.CENTER_HORIZONTAL;
        imageView2.setLayoutParams(params2);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // only for LOLLIPOP and newer versions
            btn_login.setBackground(ContextCompat.getDrawable(this, R.drawable.button_bg));
        } else {
            btn_login.setBackground(ContextCompat.getDrawable(this, R.drawable.button_bg_normal));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // only for LOLLIPOP and newer versions
            google.setBackground(ContextCompat.getDrawable(this, R.drawable.google_button_bg));
        } else {
            google.setBackground(ContextCompat.getDrawable(this, R.drawable.google_button_bg_normal));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // only for LOLLIPOP and newer versions
            facebook.setBackground(ContextCompat.getDrawable(this, R.drawable.facebook_button_bg));
        } else {
            facebook.setBackground(ContextCompat.getDrawable(this, R.drawable.facebook_button_bg_normal));
        }

        login_email.setTypeface(typeface_regular);
        login_password.setTypeface(typeface_regular);
        google_text.setTypeface(typeface_regular);
        facebook_text.setTypeface(typeface_regular);
        btn_login.setTypeface(typeface_regular);

        signup_in_login.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        login_password.setOnEditorActionListener(this);
        forgot_password.setOnClickListener(this);
        outertouch.setOnClickListener(this);
        google.setOnClickListener(this);
        facebook.setOnClickListener(this);

        cb_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int start, end;
                if (!b) {
                    start = login_password.getSelectionStart();
                    end = login_password.getSelectionEnd();
                    login_password.setTransformationMethod(new PasswordTransformationMethod());
                    login_password.setSelection(start, end);
                } else {
                    start = login_password.getSelectionStart();
                    end = login_password.getSelectionEnd();
                    login_password.setTransformationMethod(null);
                    login_password.setSelection(start, end);
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

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        mFirebaseAuth.addAuthStateListener(mFirebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }
        if (mFirebaseAuthListener != null) {
            mFirebaseAuth.removeAuthStateListener(mFirebaseAuthListener);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (mcallbackmanager != null) {
            mcallbackmanager.onActivityResult(requestCode, resultCode, data);
        }

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            mProgressDialog.show();
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount account = result.getSignInAccount();
            firebaseAuthWithGoogle(account);
        } else {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGooogle:" + acct.getId());
        AuthCredential mAuthCretential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(mAuthCretential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Log.e(TAG, "Authentication failed.");
                        } else {
                            FirebaseUser mUser = mFirebaseAuth.getCurrentUser();
                            mUser.getToken(true)
                                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                                            if (task.isSuccessful()) {
                                                String idToken = task.getResult().getToken();
                                                Log.d("Firebase Token", idToken);
                                                preferenceHelper.save("firebase_token", idToken);
                                                assert acct != null;
                                                username = acct.getGivenName();

                                                assert username != null;
                                                if (username.isEmpty()) {
                                                    username = acct.getDisplayName();
                                                }
                                                usermail = acct.getEmail();
                                                social_id = acct.getId();
                                                Uri photoUrl = acct.getPhotoUrl();
                                                assert photoUrl != null;
                                                personPhotoUrl = photoUrl.toString();
                                                preferenceHelper.save("user_profilepic", personPhotoUrl);
                                                try {
                                                    JSONObject googleJsonObject = new JSONObject();
                                                    googleJsonObject.put("UserName", username);
                                                    googleJsonObject.put("Email", usermail);
                                                    googleJsonObject.put("Type", "google");
                                                    mMixpanel.track("Login", googleJsonObject);
                                                } catch (JSONException e) {
                                                    Log.e("Ontro", "Unable to add properties to JSONObject", e);
                                                }
                                                new RetrieveTokenTask().execute(usermail);
                                            }
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseAuthInvalidUserException) {
                            ((FirebaseAuthInvalidUserException) e).getErrorCode();
                            Toast.makeText(LoginActivity.this, "Invalid user", Toast.LENGTH_LONG).show();
                        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            ((FirebaseAuthInvalidCredentialsException) e).getErrorCode();
                            Toast.makeText(LoginActivity.this, "Invalid user credential", Toast.LENGTH_LONG).show();
                        } else if (e instanceof FirebaseAuthUserCollisionException) {
                            FirebaseAuthUserCollisionException exception = (FirebaseAuthUserCollisionException) e;
                            Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            e.printStackTrace();
                        }
                        if (mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signup_in_login:
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                break;
            case R.id.forgot_password:
                Intent forgot_intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(forgot_intent);
                break;
            case R.id.login:
                onLoginClicked();
                break;

            case R.id.outertouch:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                break;

            case R.id.facebook:
                if (CommonUtils.isNetworkAvailable(this)) {
                    mProgressDialog.show();
                    mcallbackmanager = CallbackManager.Factory.create();

                    LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "email"));
                    LoginManager.getInstance().registerCallback(mcallbackmanager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            AccessToken accessToken = loginResult.getAccessToken();
                            handleFacebookAccessToken(accessToken);
                        }

                        @Override
                        public void onCancel() {
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                            }
                            LoginManager mLoginManager = LoginManager.getInstance();
                            mLoginManager.logOut();
                            Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(FacebookException e) {
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                            }
                            Toast.makeText(getApplicationContext(), "Error occurred try again !!!" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), Constants.INTERNET_ERROR, Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.google:
                if (CommonUtils.isNetworkAvailable(this)) {
                    signInWithGplus();
                } else {
                    Toast.makeText(getApplicationContext(), Constants.INTERNET_ERROR, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void handleFacebookAccessToken(final AccessToken accessToken) {
        Log.d(TAG, "handleFacebookAccessToken:" + accessToken);
        AuthCredential mAuthCretential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mFirebaseAuth.signInWithCredential(mAuthCretential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            user.getToken(true)
                                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                                            if (task.isSuccessful()) {
                                                String idToken = task.getResult().getToken();
                                                Log.d("Firebase Token", idToken);
                                                preferenceHelper.save("firebase_token", idToken);
                                                try {
                                                    GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                                                        @Override
                                                        public void onCompleted(JSONObject object, GraphResponse response) {
                                                            try {
                                                                String token = accessToken.getToken();
                                                                social_id = object.getString("id");
                                                                URL imgUrl = new URL("https://graph.facebook.com/" + social_id + "/picture?type=large");
                                                                personPhotoUrl = imgUrl.toString();
                                                                preferenceHelper.save("user_profilepic", personPhotoUrl);
                                                                username = object.getString("first_name");
                                                                if (object.has("email")) {
                                                                    usermail = object.getString("email");
                                                                }
                                          /*  String birthday = object.getString("birthday");
                                            preferenceHelper.save("user_birthday", birthday);*/
                                                                String gender = object.getString("gender");
                                                                preferenceHelper.save("gender", gender);

                                                                if (usermail == null || usermail.isEmpty()) {
                                                                    LoginManager.getInstance().logInWithReadPermissions(
                                                                            LoginActivity.this,
                                                                            Collections.singletonList("email"));
                                                                } else {
                                                                    try {
                                                                        JSONObject facebookJsonObject = new JSONObject();
                                                                        facebookJsonObject.put("UserName", username);
                                                                        facebookJsonObject.put("Email", usermail);
                                                                        facebookJsonObject.put("Type", "facebook");
                                                                        mMixpanel.track("Login", facebookJsonObject);
                                                                    } catch (JSONException e) {
                                                                        Log.e("Ontro", "Unable to add properties to JSONObject", e);
                                                                    }
                                                                    AuthendicateFB(usermail, token, social_id, username, personPhotoUrl);
                                                                }

                                                            } catch (JSONException | MalformedURLException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                    Bundle parameters = new Bundle();
                                                    parameters.putString("fields", "id,name,link,email,first_name,gender,last_name,locale,timezone,updated_time,verified"); //birthday
                                                    request.setParameters(parameters);
                                                    request.executeAsync();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                                    mProgressDialog.dismiss();
                                                }
                                            }
                                        }
                                    });
                        } else {
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                            }
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Log.d(TAG, "Authentication failed.");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseAuthInvalidUserException) {
                            ((FirebaseAuthInvalidUserException) e).getErrorCode();
                            Toast.makeText(LoginActivity.this, "Invalid user", Toast.LENGTH_LONG).show();
                        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            FirebaseAuthInvalidCredentialsException invalidCredentialsException = (FirebaseAuthInvalidCredentialsException) e;
                            Toast.makeText(LoginActivity.this, "Invalid user credential", Toast.LENGTH_LONG).show();
                        } else if (e instanceof FirebaseAuthUserCollisionException) {
                            FirebaseAuthUserCollisionException exception = (FirebaseAuthUserCollisionException) e;
                            Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            e.printStackTrace();
                        }
                        if (mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                    }
                });
    }

    private void onLoginClicked() {
        if (CommonUtils.isNetworkAvailable(this)) {
            mProgressDialog.show();
            User_login();
        } else {
            Toast.makeText(LoginActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }
    }

    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
            return;
        }
    }

    private void User_login() {
        boolean value = Validate();
        if (value) {
            mFirebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (task.isSuccessful()) {
                                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                                user.getToken(true)
                                        .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                            public void onComplete(@NonNull Task<GetTokenResult> task) {
                                                if (task.isSuccessful()) {
                                                    String idToken = task.getResult().getToken();
                                                    Log.d("Firebase Token", idToken);
                                                    preferenceHelper.save("firebase_token", idToken);
                                                    LoginInputDTO loginInputDTO = new LoginInputDTO(email, password);
                                                    Call<ResponseBody> call1 = apiInterface.User_login(loginInputDTO);
                                                    call1.enqueue(new Callback<ResponseBody>() {
                                                        @Override
                                                        public void onResponse(Call<ResponseBody> call1, Response<ResponseBody> response) {
                                                            if (response.code() == 200) {
                                                                if (response.body() != null) {
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
                                                                        String sports = jsonObject.getString("fav_sport");
                                                                        preferenceHelper.save("user_profilepic", "");
                                                                        try {
                                                                            JSONObject normalJsonObject = new JSONObject();
                                                                            normalJsonObject.put("UserName", name);
                                                                            normalJsonObject.put("Email", email);
                                                                            normalJsonObject.put("Type", "normal");
                                                                            mMixpanel.track("Login", normalJsonObject);
                                                                        } catch (JSONException e) {
                                                                            Log.e("Ontro", "Unable to add properties to JSONObject", e);
                                                                        }

                                                                        UpdateToken(userId, name, email, "", is_profile, is_phone, token, sports);
                                                                    } catch (IOException | JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                } else {
                                                                    String error = response.message();
                                                                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                                                                }
                                                            } else {
                                                                if (response.code() != 200) {
                                                                    if (response.errorBody() != null) {
                                                                        try {
                                                                            String error = response.errorBody().string();
                                                                            JSONObject jsonObject = new JSONObject(error);
                                                                            String msg = jsonObject.getString("message");
                                                                            if (response.code() == 401) {
                                                                                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                                                                            }
                                                                            String code = jsonObject.getString("code");
                                                                            if (!code.equals("500")) {
                                                                                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                                                                            } else {
                                                                                Toast.makeText(LoginActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        } catch (JSONException | IOException e) {
                                                                            e.printStackTrace();
                                                                            String error = response.message();
//                                                                            Toast.makeText(LoginActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    } else {
                                                                        String error = response.message();
                                                                        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            }
                                                            if (mProgressDialog.isShowing()) {
                                                                mProgressDialog.dismiss();
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(Call<ResponseBody> call1, Throwable t) {
                                                            // Log error here since request failed
                                                            CommonUtils.ServerFailureHandleMethod(LoginActivity.this, t);
                                                            if (mProgressDialog.isShowing()) {
                                                                mProgressDialog.dismiss();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    Log.d(TAG, "Authentication failed.", task.getException());
                                                }
                                            }
                                        });
                            } else {
                                Log.d(TAG, "Authentication failed.", task.getException());
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (e instanceof FirebaseAuthInvalidUserException) {
                                ((FirebaseAuthInvalidUserException) e).getErrorCode();
                                Toast.makeText(LoginActivity.this, "Invalid user", Toast.LENGTH_LONG).show();
                            } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                ((FirebaseAuthInvalidCredentialsException) e).getErrorCode();
                                Toast.makeText(LoginActivity.this, "Invalid user credential", Toast.LENGTH_LONG).show();
                            } else if (e instanceof FirebaseAuthUserCollisionException) {
                                FirebaseAuthUserCollisionException exception = (FirebaseAuthUserCollisionException) e;
                                Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                            } else {
                                e.printStackTrace();
                            }
                            if (mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                            }
                        }
                    });
        } else {
            mProgressDialog.dismiss();
        }
    }

    private boolean Validate() {
        email = login_email.getText().toString();
        password = login_password.getText().toString();

        if (email.isEmpty() && password.isEmpty()) {
            Toast.makeText(LoginActivity.this, Constants.Messages.EMPTY_FIELDS, Toast.LENGTH_SHORT).show();
            return false;
        } else if (email.isEmpty())  {
            Toast.makeText(LoginActivity.this, Constants.Messages.ENTER_EMAIL, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!CommonUtils.isValidEmail(email)) {
            Toast.makeText(LoginActivity.this, Constants.Messages.ENTER_VALID_EMAIL, Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.isEmpty()) {
            Toast.makeText(LoginActivity.this, Constants.Messages.ENTER_PASSWORD, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void UpdateToken(String userId, String name, String email, String phone, String is_profile, String is_phone,
                             String token, String sports) {
        preferenceHelper.save("user_id", userId);
        preferenceHelper.save("user_name", name);
        preferenceHelper.save("user_email", email);
        preferenceHelper.save("user_phone", phone);
        preferenceHelper.save("is_profile", is_profile);
        preferenceHelper.save("is_phone", is_phone);
        preferenceHelper.save("is_logged", true);
        preferenceHelper.save("user_token", token);
        preferenceHelper.save("player_sports", sports);

        String isProfile = preferenceHelper.getString("is_profile", "0");
        String isOtp = preferenceHelper.getString("is_phone", "0");
        if (isProfile.equals("0")) {
            Intent intent = new Intent(LoginActivity.this, ProfileCompletionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else if (isOtp.equals("0")) {
            Intent otpintent = new Intent(LoginActivity.this, OTPActivity.class);
            otpintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(otpintent);
            finish();
        } else {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void AuthendicateFB(String usermail, String token, String social_id, String name, String image) {
//        mProgressDialog.show();
        SocialAuthendicationInput socialAuthendicationInput = new SocialAuthendicationInput(usermail, token, social_id, name, image);
        Call<ResponseBody> call1 = apiInterface.FBAuthendication(socialAuthendicationInput);
        call1.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call1, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
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
                            String sports = jsonObject.getString("fav_sport");
                            UpdateToken(userId, name, email, "", is_profile, is_phone, token, sports);

                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    CommonUtils.ErrorHandleMethod(LoginActivity.this, response);
                }
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call1, Throwable t) {
                // Log error here since request failed
                CommonUtils.ServerFailureHandleMethod(LoginActivity.this, t);
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
        });
    }

    private void AuthendicateGplus(String usermail, String token, String social_id, String name, String img) {
        SocialAuthendicationInput socialAuthendicationInput = new SocialAuthendicationInput(usermail, token, social_id, name, img);
        Call<ResponseBody> call1 = apiInterface.Gplus_Authendication(socialAuthendicationInput);
//        mProgressDialog.show();
        call1.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call1, Response<ResponseBody> response) {
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
                        String sports = jsonObject.getString("fav_sport");
                        UpdateToken(userId, name, email, "", is_profile, is_phone, token, sports);

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    CommonUtils.ErrorHandleMethod(LoginActivity.this, response);
                }
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call1, Throwable t) {
                // Log error here since request failed
                CommonUtils.ServerFailureHandleMethod(LoginActivity.this, t);
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
        });
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            onLoginClicked();
            return true;
        }
        return false;
    }

    private class RetrieveTokenTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String accountName = params[0];
            String scopes = "oauth2:profile email";
            String token = null;
            try {
                token = GoogleAuthUtil.getToken(getApplicationContext(), accountName, scopes);
            } catch (IOException | GoogleAuthException e) {
                e.printStackTrace();
            }
            return token;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            AuthendicateGplus(usermail, s, social_id, username, personPhotoUrl);
        }
    }
}
