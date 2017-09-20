package com.ontro;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.RemoteMessage;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.ontro.dto.FcmTokenModel;
import com.ontro.dto.FirebaseAuthUser;
import com.ontro.dto.LogoutRequest;
import com.ontro.dto.NotificationTokenRequest;
import com.ontro.dto.PlayerProfileData;
import com.ontro.firebase.MyFirebaseMessagingService;
import com.ontro.fragments.ChatFragment;
import com.ontro.fragments.DiscussionFragment;
import com.ontro.fragments.ExploreHome;
import com.ontro.fragments.MyMatchesHome;
import com.ontro.fragments.MyTeamFragment;
import com.ontro.fragments.NewsFeedFragment;
import com.ontro.rest.ApiClient;
import com.ontro.rest.ApiInterface;
import com.ontro.utils.CommonUtils;
import com.ontro.utils.PreferenceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        MyFirebaseMessagingService.FirebaseNotificationListener {

    private static final String TAG = "Firebase";
    public static int Exploretabposition = 0;
    public TextView pd_toolbar_text;
    public ResideMenu resideMenu;
    public LinearLayout fullviewcontainer;
    public ImageView mNotificationView;
    private Handler mainHandler;
    private Runnable myRunnable;
    private DisplayMetrics dm;
    private PreferenceHelper preferenceHelper;
    private ApiInterface apiInterface;
    private String authToken;
    private int navigation2_click_action = 0;
    private Button newsfeed, discussion;
    private boolean doubleBackToExitPressedOnce = false;
    private Typeface typeface;
    private ImageView home_icon;
    private MyTeamDataBaseHelper myTeamDataBaseHelper;
    private FcmTokenDataBaseHelper fcmTokenDataBaseHelper;
    int[] id = {12, 13, 14, 15, 16, 17, 18, 19};

    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {
        }

        @Override
        public void closeMenu() {
        }
    };

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUsername;
    private String mPhotoUrl;
    private ImageView dotimage;
    private MixpanelAPI mMixpanel;
    private ImageView mPlayerInviteImageView;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        overridePendingTransition(R.anim.slide_in_right, R.anim.pause);
        Fabric.with(this, new Crashlytics());
        mMixpanel = MixpanelAPI.getInstance(this, getResources().getString(R.string.mixpanel_token));
        preferenceHelper = new PreferenceHelper(HomeActivity.this, Constants.APP_NAME, 0);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        myTeamDataBaseHelper = new MyTeamDataBaseHelper(HomeActivity.this);
        authToken = "Bearer " + preferenceHelper.getString("user_token", "");
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        preferenceHelper.save("is_logged", true);

        if (preferenceHelper.contains("user_profilepic")) {
            preferenceHelper.remove("user_profilepic");
        }

        Typeface typeface_regular = Typeface.createFromAsset(getAssets(), "fonts/roboto_regular.ttf");
        ImageView home_icon = (ImageView) findViewById(R.id.home_icon);
        newsfeed = (Button) findViewById(R.id.btn_stream);
        discussion = (Button) findViewById(R.id.btn_buzz);
        pd_toolbar_text = (TextView) findViewById(R.id.pd_toolbar_text);
        mNotificationView = (ImageView) findViewById(R.id.iv_notification);
        mPlayerInviteImageView = (ImageView) findViewById(R.id.activity_home_iv_player_invite);
        fullviewcontainer = (LinearLayout) findViewById(R.id.fullviewcontainer);

        Init();
        setMenu();
        Intent intent = getIntent();

        homeButtonSelection(newsfeed, discussion);
        if (intent.hasExtra("Fromchat")) {
            fullviewcontainer.setVisibility(View.GONE);
            mNotificationView.setVisibility(View.GONE);
            mPlayerInviteImageView.setVisibility(View.VISIBLE);
            if (Constants.invitecountindicator == 1) {
                mPlayerInviteImageView.setImageResource(R.drawable.ic_invite_request_inticator);
            } else {
                mPlayerInviteImageView.setImageResource(R.drawable.ic_invite_request);
            }
            pd_toolbar_text.setText("Chat");
            try {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, new ChatFragment(), Constants.FragmentTag.CHAT_FRAGMENT);
                fragmentTransaction.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (intent.hasExtra("newdiscussion")) {
            homeButtonSelection(discussion, newsfeed);
            try {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, new DiscussionFragment(), Constants.FragmentTag.DISCUSSION_FRAGMENT);
                fragmentTransaction.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (intent.hasExtra("FromCreateteam")) {
            fullviewcontainer.setVisibility(View.GONE);
            pd_toolbar_text.setText("My Teams");
            mNotificationView.setVisibility(View.GONE);
            mPlayerInviteImageView.setVisibility(View.GONE);
            navigation2_click_action = 1;
            mNotificationView.setImageResource(R.drawable.ic_create_team_white);
            try {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, new MyTeamFragment(), Constants.FragmentTag.MY_TEAM_FRAGMENT);
                fragmentTransaction.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (intent.hasExtra("Fromteaminfo")) {
            preferenceHelper.save("Fromfilter", "true");
            preferenceHelper.save("searchvisible", "false");
            fullviewcontainer.setVisibility(View.GONE);
            pd_toolbar_text.setText("Explore");
            mNotificationView.setVisibility(View.VISIBLE);
            mPlayerInviteImageView.setVisibility(View.GONE);
            mNotificationView.setImageResource(R.drawable.filter);
            navigation2_click_action = 2;
            Exploretabposition = 1;
            try {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, new ExploreHome(), Constants.FragmentTag.EXPLORE_FRAGMENT);
//                    fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (intent.hasExtra(Constants.BundleKeys.MY_MATCH)) {
            fullviewcontainer.setVisibility(View.GONE);
            mNotificationView.setVisibility(View.GONE);
            mPlayerInviteImageView.setVisibility(View.GONE);
            pd_toolbar_text.setText("My Matches");
            int tabPosition;
            if (intent.hasExtra(Constants.BundleKeys.MY_MATCH_INVITES)) {
                tabPosition = 1;
            } else {
                tabPosition = 2;
            }

            try {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                MyMatchesHome myMatchesHome = new MyMatchesHome();
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.BundleKeys.MATCH_STATUS_POSITION, tabPosition);
                myMatchesHome.setArguments(bundle);
                fragmentTransaction.replace(R.id.container, myMatchesHome, Constants.FragmentTag.MY_MATCH_FRAGMENT);
                fragmentTransaction.addToBackStack("mymatches");
                fragmentTransaction.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, new NewsFeedFragment(), Constants.FragmentTag.NEWS_FEED_FRAGMENT);
                fragmentTransaction.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        home_icon.setOnClickListener(this);
        newsfeed.setOnClickListener(this);
        discussion.setOnClickListener(this);
        mNotificationView.setOnClickListener(this);
        mPlayerInviteImageView.setOnClickListener(this);
        newsfeed.setTypeface(typeface);
        discussion.setTypeface(typeface);
    }

    private void homeButtonSelection(Button button1, Button button2) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // only for LOLLIPOP and newer versions
            button1.setBackground(ContextCompat.getDrawable(this, R.drawable.button_bg));
            button2.setBackground(ContextCompat.getDrawable(this, R.drawable.login_edittext_bg));
        } else {
            button1.setBackground(ContextCompat.getDrawable(this, R.drawable.button_bg_normal));
            button2.setBackground(ContextCompat.getDrawable(this, R.drawable.login_edittext_bg));
        }
    }

    private void Init() {
        /*To initialize firebase notification listener */
        MyFirebaseMessagingService firebaseMessagingService = MyFirebaseMessagingService.getNewInstance(this);
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Firebase", "Refreshed token: " + refreshedToken);
        fcmTokenDataBaseHelper = new FcmTokenDataBaseHelper(this);
        FcmTokenModel fcmTokenModel = fcmTokenDataBaseHelper.getFcmToken();
        if (fcmTokenModel.getToken() == null || fcmTokenModel.getToken().isEmpty()) {
            FcmTokenModel model = new FcmTokenModel();
            model.setKeyId(1);
            model.setToken(refreshedToken);
            fcmTokenDataBaseHelper.addToken(model);
            sendRegistrationToServer(refreshedToken);
        }
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        typeface = Typeface.createFromAsset(getAssets(), "fonts/roboto_regular.ttf");
        home_icon = (ImageView) findViewById(R.id.home_icon);
        newsfeed = (Button) findViewById(R.id.btn_stream);
        discussion = (Button) findViewById(R.id.btn_buzz);
        pd_toolbar_text = (TextView) findViewById(R.id.pd_toolbar_text);
        mNotificationView = (ImageView) findViewById(R.id.iv_notification);
        fullviewcontainer = (LinearLayout) findViewById(R.id.fullviewcontainer);

        pd_toolbar_text.setText("Home");
        mNotificationView.setVisibility(View.VISIBLE);
        if (Constants.notificationview == 1) {
            mNotificationView.setImageResource(R.drawable.ic_home_notification_indicator);
        } else {
            mNotificationView.setImageResource(R.drawable.ic_home_notification);
        }

        mPlayerInviteImageView.setVisibility(View.VISIBLE);
        if (Constants.invitecountindicator == 1) {
            mPlayerInviteImageView.setImageResource(R.drawable.ic_invite_request_inticator);
        } else {
            mPlayerInviteImageView.setImageResource(R.drawable.ic_invite_request);
        }

        mUsername = preferenceHelper.getString("user_name", "name");
        String email = preferenceHelper.getString("user_email", "email");

        Crashlytics.setUserName(mUsername);
        Crashlytics.setUserEmail(email);

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        } else {
            Uri photoUrl = mFirebaseUser.getPhotoUrl();
            if (photoUrl == null) {
                photoUrl = Uri.parse(CommonUtils.default_image);
            }
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(mUsername)
                    .setPhotoUri(photoUrl)
                    .build();
            mFirebaseUser.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User profile updated.");
                            }
                        }
                    });

            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
            sendFcmUniqueIdToServer(mFirebaseUser.getUid());
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Initializing google plus api client
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
    }

    private void sendFcmUniqueIdToServer(String uniqueId) {
        String playerId = preferenceHelper.getString("user_id", "");
        String fcmToken = preferenceHelper.getString("firebase_token", "");
        Integer userId = 0;
        if (!playerId.equals("")) userId = Integer.valueOf(playerId);
        FirebaseAuthUser firebaseAuthUser = new FirebaseAuthUser();
        firebaseAuthUser.setUniqueId(uniqueId);
        firebaseAuthUser.setUserId(userId);
        firebaseAuthUser.setFcmToken(fcmToken);
        Call<ResponseBody> call = apiInterface.sendFcmUniqueIdToServer(authToken, firebaseAuthUser);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                        JSONObject jsonObject = new JSONObject(data);
                        String msg = jsonObject.getString("message");
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                String error = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(error);
                                String msg = jsonObject.getString("message");
                                String code = jsonObject.getString("code");
                                if (!code.equals("500")) {
                                    Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(HomeActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                String error = response.message();
                                Toast.makeText(HomeActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String error = response.message();
                            Toast.makeText(HomeActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    public void setMenu() {
        resideMenu = new ResideMenu(this, R.layout.activity_menu, R.layout.activity_menu);
        resideMenu.setBackground(R.color.app_background_color);
        resideMenu.attachToActivity(this);
        resideMenu.setScaleValue(0.62f);
        resideMenu.setMenuListener(menuListener);

        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);

        View leftMenu = resideMenu.getLeftMenuView();
        final ImageView profilehome = (ImageView) leftMenu.findViewById(R.id.homeimage);
        LinearLayout sidemenu = (LinearLayout) leftMenu.findViewById(R.id.sidemenu);

        LinearLayout profile = (LinearLayout) leftMenu.findViewById(R.id.menu_profile);
        LinearLayout teams = (LinearLayout) leftMenu.findViewById(R.id.menu_teams);
        LinearLayout explore = (LinearLayout) leftMenu.findViewById(R.id.menu_explore);
        LinearLayout matches = (LinearLayout) leftMenu.findViewById(R.id.menu_matches);
        LinearLayout chat = (LinearLayout) leftMenu.findViewById(R.id.menu_chat);
        LinearLayout signout = (LinearLayout) leftMenu.findViewById(R.id.menu_sign_out);

        final LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        param1.width = (int) (dm.widthPixels * 0.55);
        param1.height = (int) (dm.heightPixels);
        sidemenu.setLayoutParams(param1);

        profilehome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.closeMenu();
                homeButtonSelection(newsfeed, discussion);
                fullviewcontainer.setVisibility(View.VISIBLE);
                pd_toolbar_text.setText("Home");
                mNotificationView.setVisibility(View.VISIBLE);
                navigation2_click_action = 0;
                if (Constants.notificationview == 1) {
                    mNotificationView.setImageResource(R.drawable.ic_home_notification_indicator);
                } else {
                    mNotificationView.setImageResource(R.drawable.ic_home_notification);
                }

                mPlayerInviteImageView.setVisibility(View.VISIBLE);
                if (Constants.invitecountindicator == 1) {
                    mPlayerInviteImageView.setImageResource(R.drawable.ic_invite_request_inticator);
                } else {
                    mPlayerInviteImageView.setImageResource(R.drawable.ic_invite_request);
                }
                try {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container, new NewsFeedFragment(), Constants.FragmentTag.NEWS_FEED_FRAGMENT);
                    fragmentTransaction.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.closeMenu();
                Intent intent = new Intent(HomeActivity.this, PlayerProfileActivity.class);
                startActivity(intent);
            }
        });

        teams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.closeMenu();
                fullviewcontainer.setVisibility(View.GONE);
                pd_toolbar_text.setText("My Teams");
                mNotificationView.setVisibility(View.GONE);
                mPlayerInviteImageView.setVisibility(View.GONE);
                navigation2_click_action = 1;
                mNotificationView.setImageResource(R.drawable.ic_create_team_white);
                try {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container, new MyTeamFragment(), Constants.FragmentTag.MY_TEAM_FRAGMENT);
//                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.closeMenu();
                preferenceHelper.save("Fromfilter", "true");
                preferenceHelper.save("searchvisible", "false");
                fullviewcontainer.setVisibility(View.GONE);
                pd_toolbar_text.setText("Explore");
                mNotificationView.setVisibility(View.VISIBLE);
                mPlayerInviteImageView.setVisibility(View.GONE);
                mNotificationView.setImageResource(R.drawable.filter);
                navigation2_click_action = 2;
                Exploretabposition = 0;
                try {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container, new ExploreHome(), Constants.FragmentTag.EXPLORE_FRAGMENT);
//                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        matches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.closeMenu();
                fullviewcontainer.setVisibility(View.GONE);
                mNotificationView.setVisibility(View.GONE);
                mPlayerInviteImageView.setVisibility(View.GONE);
                pd_toolbar_text.setText("My Matches");
                try {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container, new MyMatchesHome(), Constants.FragmentTag.MY_MATCH_FRAGMENT);
                    fragmentTransaction.addToBackStack("mymatches");
//                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.closeMenu();
                fullviewcontainer.setVisibility(View.GONE);
                mNotificationView.setVisibility(View.GONE);
                mPlayerInviteImageView.setVisibility(View.GONE);
                pd_toolbar_text.setText("Chat");
                try {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container, new ChatFragment(), Constants.FragmentTag.CHAT_FRAGMENT);
                    fragmentTransaction.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.closeMenu();
                try {
                    final Dialog logoutdialog = new Dialog(HomeActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
                    logoutdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    logoutdialog.setContentView(R.layout.logout_layout);
                    LinearLayout logout_container = (LinearLayout) logoutdialog.findViewById(R.id.logout_container);
                    CardView card_view = (CardView) logoutdialog.findViewById(R.id.card_view);
                    TextView title = (TextView) logoutdialog.findViewById(R.id.title);
                    title.setText(R.string.logout);

                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.width = (int) (metrics.widthPixels * 0.8);
                    params.gravity = Gravity.CENTER;
                    card_view.setLayoutParams(params);

                    TextView yes = (TextView) logoutdialog.findViewById(R.id.yes);
                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            logoutdialog.dismiss();
                            String userId = preferenceHelper.getString("user_id", "");
                            if (!userId.equals("")) {
                                onUserLoggedOut(Integer.valueOf(userId));
                            }
                            SignOut();
                        }
                    });
                    TextView no = (TextView) logoutdialog.findViewById(R.id.no);
                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            logoutdialog.dismiss();
                        }
                    });
                    logoutdialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void onUserLoggedOut(int userId) {
        LogoutRequest logoutRequest = new LogoutRequest();
        logoutRequest.setUserId(userId);
        Call<ResponseBody> call = apiInterface.getLogoutResponse(authToken, logoutRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                        JSONObject jsonObject = new JSONObject(data);
                        String msg = jsonObject.getString("message");
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                String error = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(error);
                                String msg = jsonObject.getString("message");
                                String code = jsonObject.getString("code");
                                if (!code.equals("500")) {
                                    Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(HomeActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                String error = response.message();
                                Toast.makeText(HomeActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String error = response.message();
                            Toast.makeText(HomeActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                    preferenceHelper.removeAll();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                preferenceHelper.removeAll();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                preferenceHelper.removeAll();
            }
        });
    }

    @Override
    public void onBackPressed() {
        NewsFeedFragment newsFeedFragment = (NewsFeedFragment) getSupportFragmentManager().findFragmentByTag(Constants.FragmentTag.NEWS_FEED_FRAGMENT);
        DiscussionFragment discussionFragment = (DiscussionFragment) getSupportFragmentManager().findFragmentByTag(Constants.FragmentTag.DISCUSSION_FRAGMENT);

        MyTeamFragment myTeamFragment = (MyTeamFragment) getSupportFragmentManager().findFragmentByTag(Constants.FragmentTag.MY_TEAM_FRAGMENT);
        ExploreHome exploreHome = (ExploreHome) getSupportFragmentManager().findFragmentByTag(Constants.FragmentTag.EXPLORE_FRAGMENT);
        MyMatchesHome myMatchesHome = (MyMatchesHome) getSupportFragmentManager().findFragmentByTag(Constants.FragmentTag.MY_MATCH_FRAGMENT);
        ChatFragment chatFragment = (ChatFragment) getSupportFragmentManager().findFragmentByTag(Constants.FragmentTag.CHAT_FRAGMENT);

        if (resideMenu.isOpened()) {
            resideMenu.closeMenu();
        } else {
            if (newsFeedFragment != null && newsFeedFragment.isVisible() || discussionFragment != null && discussionFragment.isVisible()) {
                if (doubleBackToExitPressedOnce) {
//                    super.onBackPressed();
                    while (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStackImmediate();
                    }
                    finishAffinity();
                    System.exit(0);
                    overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
                    return;
                }

                doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "press again to exit", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);

            } else {
                homeButtonSelection(newsfeed, discussion);
                navigation2_click_action = 3;
                fullviewcontainer.setVisibility(View.VISIBLE);
                pd_toolbar_text.setText("Home");
                mNotificationView.setVisibility(View.VISIBLE);
                mPlayerInviteImageView.setVisibility(View.VISIBLE);
                if (Constants.notificationview == 1) {
                    mNotificationView.setImageResource(R.drawable.ic_home_notification_indicator);
                } else {
                    mNotificationView.setImageResource(R.drawable.ic_home_notification);
                }
                try {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container, new NewsFeedFragment(), Constants.FragmentTag.NEWS_FEED_FRAGMENT);
                    fragmentTransaction.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home_icon:
                if (resideMenu.isOpened()) {
                    resideMenu.closeMenu();
                } else {
                    resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
                }
                break;
            case R.id.btn_stream:
                homeButtonSelection(newsfeed, discussion);
                try {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container, new NewsFeedFragment(), "newsfeed");
                    fragmentTransaction.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_buzz:
                homeButtonSelection(discussion, newsfeed);
                try {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container, new DiscussionFragment(), "discussion");
                    fragmentTransaction.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.iv_notification:
                if (navigation2_click_action == 0 || navigation2_click_action == 3) {
                    Constants.notificationview = 0;
                    Intent notificationIntent = new Intent(HomeActivity.this, NotificationActivity.class);
                    startActivity(notificationIntent);
                } else if (navigation2_click_action == 1) {
                    preferenceHelper.save("user_location", "");
                    try {
                        JSONObject eventJsonObject = new JSONObject();
                        eventJsonObject.put("UserId", preferenceHelper.getString("user_id", ""));
                        eventJsonObject.put("UserName", preferenceHelper.getString("user_name", ""));
                        eventJsonObject.put("UserEmail", preferenceHelper.getString("user_email", ""));
                        mMixpanel.track("CreateTeam", eventJsonObject);
                    } catch (JSONException e) {
                        Log.e("Ontro", "Unable to add properties to JSONObject", e);
                    }
                    Intent intent = new Intent(HomeActivity.this, CreateTeamActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else if (navigation2_click_action == 2) {
                    Intent filterIntent = new Intent(HomeActivity.this, FilterActivity.class);
                    startActivity(filterIntent);
                }
                break;
            case R.id.activity_home_iv_player_invite:
                if (navigation2_click_action == 0 || navigation2_click_action == 3) {
                    Constants.invitecountindicator = 0;
                    Intent notificationIntent = new Intent(HomeActivity.this, PlayerInviteApprovalActivity.class);
                    startActivity(notificationIntent);
                }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("player_id")) {
            Constants.notificationview = 1;
            Constants.invitecountindicator = 1;
        } else if (bundle != null && bundle.containsKey("payment_status")) {
            Constants.notificationview = 1;
        }
        if (navigation2_click_action == 0 || navigation2_click_action == 3) {
            if (Constants.notificationview == 1) {
                mNotificationView.setImageResource(R.drawable.ic_home_notification_indicator);
            } else {
                mNotificationView.setImageResource(R.drawable.ic_home_notification);
            }

            if (Constants.invitecountindicator == 1) {
                mPlayerInviteImageView.setImageResource(R.drawable.ic_invite_request_inticator);
            } else {
                mPlayerInviteImageView.setImageResource(R.drawable.ic_invite_request);
            }

        }
    }

    public void disconnectFromFacebook() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }

        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                LoginManager.getInstance().logOut();
                preferenceHelper.remove("is_logged");
            }
        }).executeAsync();
    }

    private void SignOut() {
        FcmTokenModel fcmTokenModel = fcmTokenDataBaseHelper.getFcmToken();
        if (fcmTokenModel.getToken() != null) fcmTokenDataBaseHelper.deleteFcmToken(fcmTokenModel);
        ProfileDataBaseHelper profileDataBaseHelper = new ProfileDataBaseHelper(this);
        PlayerProfileData profileData = profileDataBaseHelper.getProfile();
        if (profileData.getPlayerInfo() != null) profileDataBaseHelper.deleteProfile(profileData);
        mFirebaseAuth.signOut();
        disconnectFromFacebook();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        preferenceHelper.remove("is_logged");
                    }
                });
        try {
            myTeamDataBaseHelper.deletealldata();
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        }
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void sendNotification(RemoteMessage remoteMessage) {
        showNotification(remoteMessage);
    }

    private void showNotification(RemoteMessage remoteMessage) {
        Constants.isChatNotification = false;
        String messageBody = remoteMessage.getNotification().getBody();
        String notificationTitle = remoteMessage.getNotification().getTitle();
        Intent notificationIntent;
        PendingIntent notificationPendingIntent = null;
        Constants.notificationview = 1;
        if (remoteMessage.getData().containsKey("click_action")) {
            String typeOfNotification = remoteMessage.getData().get("click_action");
            switch (typeOfNotification) {
                case Constants.DefaultText.NOTIFICATION:
                    notificationIntent = new Intent(this, NotificationActivity.class);
                    notificationIntent.putExtra(Constants.BundleKeys.FIREBASE_MESSAGE, messageBody);
                    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    notificationPendingIntent = PendingIntent.getActivity(this, 0,
                            notificationIntent, PendingIntent.FLAG_ONE_SHOT);
                    break;
                case Constants.NotificationTag.PLAYER_INVITE_REQUEST:
                    notificationIntent = new Intent(this, PlayerInviteApprovalActivity.class);
                    notificationPendingIntent = PendingIntent.getActivity(this, 0,
                            notificationIntent, PendingIntent.FLAG_ONE_SHOT);
                    break;
                case Constants.NotificationTag.PLAYER_INVITE_RESPONSE:
                    notificationIntent = new Intent(this, HomeActivity.class);
                    notificationIntent.putExtra("FromCreateteam", "true");
                    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    notificationPendingIntent = PendingIntent.getActivity(this, 0,
                            notificationIntent, PendingIntent.FLAG_ONE_SHOT);
                    break;
                case Constants.NotificationTag.MATCH_INVITE_REQUEST:
                case Constants.NotificationTag.MATCH_INVITE_RESPONSE:
                case Constants.NotificationTag.MATCH_EXPIRED:
                    notificationIntent = new Intent(this, HomeActivity.class);
                    notificationIntent.putExtra(Constants.BundleKeys.MY_MATCH, "true");
                    notificationIntent.putExtra(Constants.BundleKeys.MY_MATCH_INVITES, "true");
                    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    notificationPendingIntent = PendingIntent.getActivity(this, 0,
                            notificationIntent, PendingIntent.FLAG_ONE_SHOT);
                    break;
                default:
                    notificationIntent = new Intent(this, SplashActivity.class);
                    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    notificationPendingIntent = PendingIntent.getActivity(this, 0,
                            notificationIntent, PendingIntent.FLAG_ONE_SHOT);
                    break;
            }
        } else {
            if (remoteMessage.getData().containsKey("player_id")) {
                Constants.invitecountindicator = 1;
            } else if (remoteMessage.getData().containsKey("chat")) {
                if (remoteMessage.getData().containsKey("senderFcmId")) {
                    if (mFirebaseUser != null) {
                        if (remoteMessage.getData().get("senderFcmId").equals(mFirebaseUser.getUid())) {
                            return;
                        } else {
                            Constants.isChatNotification = true;
                        }
                    }
                } else {
                    Constants.isChatNotification = true;
                }
            }
            Constants.notificationview = 1;
            notificationIntent = new Intent(this, SplashActivity.class);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            notificationPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        }

        if (Constants.isChatNotification) {
            ShowGroupNotification(messageBody, notificationTitle, notificationPendingIntent);
        } else {
            ShowSingleNotification(messageBody, notificationTitle, notificationPendingIntent);
        }

    }

    private void ShowGroupNotification(String messageBody, String notificationTitle, PendingIntent notificationPendingIntent) {
        int noti_id = id[count];
        Notification.InboxStyle inboxStyle = new Notification.InboxStyle();

        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.ic_notification);
        //show large icon in notification
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_app_notification);
        builder.setLargeIcon(bitmap);
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        builder.setContentTitle(notificationTitle);
        builder.setContentText(messageBody);

        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setAutoCancel(true); //For hiding the notification after selection
        builder.setContentIntent(notificationPendingIntent);
        builder.setStyle(inboxStyle);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(Constants.NOTIFICATION_ID, builder.build());
        count++;
    }

    private void ShowSingleNotification(String messageBody, String notificationTitle, PendingIntent notificationPendingIntent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(getNotificationIcon(builder, R.drawable.ic_notification));
        //show large icon in notification
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), getNotificationIcon(builder, R.drawable.ic_app_notification));
        builder.setLargeIcon(bitmap);
      /*  NotificationCompat.BigPictureStyle pictureStyle = new NotificationCompat.BigPictureStyle();
        builder.setStyle(pictureStyle.bigPicture(bitmap));*/
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        builder.setContentTitle(notificationTitle);
        builder.setContentText(messageBody);
        //show pop up in notification
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setAutoCancel(true); //For hiding the notification after selection
        builder.setContentIntent(notificationPendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }

    private int getNotificationIcon(NotificationCompat.Builder notificationBuilder, int notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int color = 0x686868;
            notificationBuilder.setColor(color);
            return notification;
        }
        return notification;
    }

    private void sendRegistrationToServer(String refreshedToken) {
        NotificationTokenRequest notificationTokenRequest = new NotificationTokenRequest();
        notificationTokenRequest.setFcmToken(refreshedToken);
        Call<ResponseBody> call = apiInterface.sendFcmTokenToServer(authToken, notificationTokenRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                        JSONObject jsonObject = new JSONObject(data);
                        String msg = jsonObject.getString("message");
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                String error = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(error);
                                String msg = jsonObject.getString("message");
                                String code = jsonObject.getString("code");
                                if (!code.equals("500")) {
                                    Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(HomeActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                String error = response.message();
                                Toast.makeText(HomeActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String error = response.message();
                            Toast.makeText(HomeActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
