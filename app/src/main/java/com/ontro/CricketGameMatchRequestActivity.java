package com.ontro;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ontro.customui.ProfileImageView;
import com.ontro.dto.ChatUser;
import com.ontro.dto.MatchRequestModel;
import com.ontro.dto.MySquadInfo;
import com.ontro.dto.PaymentModel;
import com.ontro.dto.SquadInfo;
import com.ontro.fragments.FirstTeamSquadFragment;
import com.ontro.fragments.SecondTeamSquadFragment;
import com.ontro.rest.ApiClient;
import com.ontro.rest.ApiInterface;
import com.ontro.utils.CommonUtils;
import com.ontro.utils.PreferenceHelper;
import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Payu.PayuConstants;
import com.payu.payuui.Activity.PayUBaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CricketGameMatchRequestActivity extends AppCompatActivity implements View.OnClickListener, TabLayout.OnTabSelectedListener {
    private TabLayout mTeamsTitleTabLayout;
    private ViewPager mTeamScoreViewPager;
    private TextView mMatchDateView, mMatchVenueView, mMatchStatusView;
    private TextView mFirstTeamNameView, mSecondTeamNameView;
    private ProfileImageView mFirstTeamImageView, mSecondTeamImageView;
    private FloatingActionButton mOpponentChatFabView;
    private Button mUpdateScoreButton;
    private Dialog mProgressDialog;
    private ApiInterface mApiInterface;
    private String mAuthToken;
    private ChatUser chatUser;
    private List<MySquadInfo> mMySquadInfos;
    private String mMatchType, mMatchStatus;
    private String mMatchId, mMyTeamId, mOpponentTeamId;
    private String mPaymentAmount, mOpponentPaymentStatus, mLoginUserPaymentStatus;
    private String mMyTeamName, mOpponentTeamName, mMyTeamLogo, mOpponentTeamLogo, mSportId;
    private String mTeamsScoreUpdate, mOpponentScoreUpdateStatus, mMyTeamScoreUpdateStatus;
    private PaymentParams mPaymentParams;
    private PayuConfig payuConfig;
    private List<SquadInfo> mOpponentSquadInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cricket_game_match_request);
        initView();
        if (CommonUtils.isNetworkAvailable(CricketGameMatchRequestActivity.this)) {
            mProgressDialog.show();
            Intent intent = getIntent();
            if (intent != null) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    MatchRequestModel matchRequestModel = (MatchRequestModel) bundle.getSerializable(Constants.BundleKeys.MY_MATCH);
                    getMatchDetails(matchRequestModel);
                }
            }
        } else {
            Toast.makeText(CricketGameMatchRequestActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }
        setListener();
    }

    private void initView() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.activity_cricket_game_match_request_toolbar);
        mTeamsTitleTabLayout = (TabLayout) findViewById(R.id.activity_cricket_game_match_request_tl);
        mTeamScoreViewPager = (ViewPager) findViewById(R.id.activity_cricket_game_match_request_pager);
        mOpponentChatFabView = (FloatingActionButton) findViewById(R.id.activity_cricket_game_match_request_fab_chat);
        mUpdateScoreButton = (Button) findViewById(R.id.activity_cricket_game_match_request_btn_update_score);

        mMatchDateView = (TextView) findViewById(R.id.cricket_score_header_tv_match_date);
        mMatchVenueView = (TextView) findViewById(R.id.cricket_score_header_tv_match_location);
        mMatchStatusView = (TextView) findViewById(R.id.cricket_score_header_tv_team_score_update_status);
        mFirstTeamNameView = (TextView) findViewById(R.id.cricket_score_header_tv_first_team_name);
        mSecondTeamNameView = (TextView) findViewById(R.id.cricket_score_header_tv_second_team_name);
        mFirstTeamImageView = (ProfileImageView) findViewById(R.id.cricket_score_header_iv_first_team_logo);
        mSecondTeamImageView = (ProfileImageView) findViewById(R.id.cricket_score_header_iv_second_team_logo);

        mProgressDialog = new Dialog(CricketGameMatchRequestActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setContentView(R.layout.progressdialog_layout);

        mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        PreferenceHelper mPreferenceHelper = new PreferenceHelper(CricketGameMatchRequestActivity.this, Constants.APP_NAME, 0);
        mAuthToken = "Bearer " + mPreferenceHelper.getString("user_token", "");

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Typeface regularTypeface = Typeface.createFromAsset(getAssets(), "fonts/roboto_regular.ttf");
        mUpdateScoreButton.setTypeface(regularTypeface);
        Typeface semiBoldTypeface = Typeface.createFromAsset(getAssets(), "fonts/roboto_medium.ttf");
        ViewGroup vg = (ViewGroup) mTeamsTitleTabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(semiBoldTypeface);
                }
            }
        }
    }

    private void setListener() {
        mUpdateScoreButton.setOnClickListener(this);
        mOpponentChatFabView.setOnClickListener(this);
        mTeamsTitleTabLayout.addOnTabSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_cricket_game_match_request_fab_chat:
                Intent chatIntent = new Intent(CricketGameMatchRequestActivity.this, ChatDetailActivity.class);
                chatIntent.putExtra(Constants.BundleKeys.FCM_UID, (Serializable) chatUser);
                startActivity(chatIntent);
                finish();
                break;
            case R.id.activity_cricket_game_match_request_btn_update_score:
                if (mUpdateScoreButton.getText().toString().trim().equals(getResources().getString(R.string.schedule_match))) {
                    Intent intent = new Intent(CricketGameMatchRequestActivity.this, ScheduleActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.BundleKeys.MATCH_TYPE, CommonUtils.MatchType(mMatchType));
                    bundle.putSerializable(Constants.BundleKeys.OPPONENT_SQUADS, (Serializable) mOpponentSquadInfos);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else if (mUpdateScoreButton.getText().toString().trim().equals(getResources().getString(R.string.form_team))) {
                    Intent intent = new Intent(CricketGameMatchRequestActivity.this, MyTeamFormationActivity.class);
                    startActivity(intent);
                } else if (mUpdateScoreButton.getText().toString().trim().equals(getResources().getString(R.string.score_update))) {
                    Intent cricketScoreUpdateIntent = new Intent(CricketGameMatchRequestActivity.this, CricketScoreUpdateActivity.class);
                    Bundle cricketScoreUpdateBundle = new Bundle();
                    cricketScoreUpdateBundle.putString(Constants.BundleKeys.MY_TEAM_NAME, mMyTeamName);
                    cricketScoreUpdateBundle.putString(Constants.BundleKeys.OPPONENT_TEAM_NAME, mOpponentTeamName);
                    cricketScoreUpdateBundle.putString(Constants.BundleKeys.MY_TEAM_IMAGE, mMyTeamLogo);
                    cricketScoreUpdateBundle.putString(Constants.BundleKeys.OPPONENT_TEAM_IMAGE, mOpponentTeamLogo);
                    cricketScoreUpdateBundle.putString(Constants.BundleKeys.SCORE_UPDATE_STATUS, mMatchStatusView.getText().toString().trim());
                    cricketScoreUpdateBundle.putString(Constants.BundleKeys.MATCH_DATE, mMatchDateView.getText().toString().trim());
                    cricketScoreUpdateBundle.putString(Constants.BundleKeys.MATCH_VENUE, mMatchVenueView.getText().toString().trim());
                    cricketScoreUpdateBundle.putString(Constants.BundleKeys.SPORT_ID, mSportId);
                    cricketScoreUpdateBundle.putString(Constants.BundleKeys.TEAM_ID, mMyTeamId);
                    cricketScoreUpdateBundle.putString(Constants.BundleKeys.OPPONENT_TEAM_ID, mOpponentTeamId);
                    cricketScoreUpdateBundle.putString(Constants.BundleKeys.MATCH_ID, mMatchId);
                    cricketScoreUpdateBundle.putString(Constants.BundleKeys.OPPONENT_USER_SCORE_STATUS, mOpponentScoreUpdateStatus);
                    cricketScoreUpdateBundle.putString(Constants.BundleKeys.LOGIN_USER_SCORE_STATUS, mMyTeamScoreUpdateStatus);
                    cricketScoreUpdateBundle.putSerializable(Constants.BundleKeys.MY_TEAM_SQUAD, (Serializable) mMySquadInfos);
                    cricketScoreUpdateIntent.putExtras(cricketScoreUpdateBundle);
                    startActivityForResult(cricketScoreUpdateIntent,Constants.SCORE_UPDATED);
                    break;
                }
                break;
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mTeamScoreViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
        }
        return super.onOptionsItemSelected(item);
    }

    private void getMatchDetails(MatchRequestModel matchRequestModel) {
        mMatchId = matchRequestModel.getMatchId();
        mMyTeamId = matchRequestModel.getMyTeamId();
        mOpponentTeamId = matchRequestModel.getOpponentTeamId();
        mFirstTeamNameView.setText(matchRequestModel.getMyTeamName());
        mSecondTeamNameView.setText(matchRequestModel.getOpponentTeamName());
        mMyTeamName = matchRequestModel.getMyTeamName();
        mOpponentTeamName = matchRequestModel.getOpponentTeamName();
        mOpponentTeamLogo = matchRequestModel.getOpponentTeamLogo();
        mMyTeamLogo = matchRequestModel.getMyTeamLogo();
        if (mOpponentTeamLogo != null) {
            Glide.with(CricketGameMatchRequestActivity.this).load(mOpponentTeamLogo).placeholder(R.drawable.profiledefaultimg).dontAnimate().into(mSecondTeamImageView);
        } else {
            Glide.with(CricketGameMatchRequestActivity.this).load(R.drawable.profiledefaultimg).dontAnimate().into(mSecondTeamImageView);
        }
        if (mMyTeamLogo != null) {
            Glide.with(CricketGameMatchRequestActivity.this).load(mMyTeamLogo).placeholder(R.drawable.profiledefaultimg).dontAnimate().into(mFirstTeamImageView);
        } else {
            Glide.with(CricketGameMatchRequestActivity.this).load(R.drawable.profiledefaultimg).dontAnimate().into(mFirstTeamImageView);
        }
        Call<ResponseBody> call = mApiInterface.MatchDetail(mAuthToken, mMatchId, mOpponentTeamId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        try {
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                            }
                            mUpdateScoreButton.setEnabled(true);
                            mUpdateScoreButton.setTextColor(ContextCompat.getColor(CricketGameMatchRequestActivity.this, R.color.white));
                            mOpponentChatFabView.setEnabled(true);
                            String data = response.body().string();
                            JSONObject json = new JSONObject(data);
                            JSONObject object = new JSONObject(json.getString("data"));
                            mMatchType = object.getString("match_type");
                            mSportId = object.getString("sport");
                            String location = object.getString("match_location");
                            String matchDate = CommonUtils.convertDateFormat(object.getString("match_date"), Constants.DefaultText.YEAR_MONTH_DATE, Constants.DefaultText.DATE_MONTH_YEAR);
                            String venueDate = object.getString("venue_booking_date");
                            String statusOfMatch = object.getString("status_of_match");
                            String mVenueAddress = "";
                            if (object.has("venue_address")) {
                                mVenueAddress = object.getString("venue_address");
                            }
                            String venueBookingId, venueName = "", venueBookingDate = "", venueBookingFromTime = "", venueBookingToTime = "";
                            if (json.has("venue_booking_id")) {
                                venueBookingId = json.getString("venue_booking_id");
                            }
                            if (object.has("venue_name")) {
                                venueName = object.getString("venue_name");
                            }
                            if (object.has("venue_booking_date")) {
                                if (venueDate.equals("null")) {
                                    venueBookingDate = object.getString("venue_booking_date");
                                }
                            }
                            if (object.has("venue_booking_from_time")) {
                                venueBookingFromTime = CommonUtils.convertTimeFormatIntoAmOrPmFormat(object.getString("venue_booking_from_time"));
                            }
                            if (object.has("venue_booking_to_time")) {
                                venueBookingToTime = CommonUtils.convertTimeFormatIntoAmOrPmFormat(object.getString("venue_booking_to_time"));
                            }
                            if (!venueBookingDate.equals("") && !venueBookingDate.equals("null")) {
                                String bookingDate = CommonUtils.convertDateFormat(venueBookingDate, Constants.DefaultText.YEAR_MONTH_DATE, Constants.DefaultText.DATE_MONTH_LETTER);
                                venueBookingFromTime = CommonUtils.convertTimeFormatIntoAmOrPmFormat(venueBookingFromTime);
                                venueBookingToTime = CommonUtils.convertTimeFormatIntoAmOrPmFormat(venueBookingToTime);
                                mMatchDateView.setText(bookingDate + " " + venueBookingFromTime + "-" + venueBookingToTime);
                                enableScoreUpdateButton(bookingDate);
                            } else {
                                mMatchDateView.setText(CommonUtils.convertDateFormat(matchDate, Constants.DefaultText.DATE_MONTH_YEAR, Constants.DefaultText.DATE_MONTH_LETTER));
                                enableScoreUpdateButton(matchDate);
                            }

                            int flagType = object.getInt("flag_type");
                            if (flagType > 0) {
                                mUpdateScoreButton.setEnabled(false);
                                mUpdateScoreButton.setTextColor(ContextCompat.getColor(CricketGameMatchRequestActivity.this, R.color.white_25));
                            }

                            if (object.has("pay_amount")) {
                                mPaymentAmount = object.getString("pay_amount");
                            }
                            mMatchStatusView.setText(statusOfMatch);
                            if (mVenueAddress.length() != 0 && !mVenueAddress.equals("null")) {
                                mMatchVenueView.setText(mVenueAddress);
                            } else if (!venueName.equals("") && !venueName.equals("null")) {
                                mMatchVenueView.setText(venueName + " ," + location);
                            } else {
                                mMatchVenueView.setText(location);
                            }
                            chatUser = new ChatUser();
                            chatUser.setUniqueId(object.getString("player_fcm_uid"));
                            chatUser.setPlayerFcmToken(object.getString("player_fcm_token"));
                            chatUser.setUserName(object.getString("player_name"));
                            chatUser.setImageUrl(object.getString("profile_image"));
                            if (object.has("status_of_match")) {
                                mMatchStatusView.setText(object.getString("status_of_match"));
                            }
                            if (object.has("matchstatus")) {
                                mMatchStatus = object.getString("matchstatus");
                            }
                            if (object.has("payment_status")) {
                                JSONObject payjson = new JSONObject(object.getString("payment_status"));
                                mOpponentPaymentStatus = payjson.getString("opponent_user_pay_status");
                                mLoginUserPaymentStatus = payjson.getString("login_user_pay_status");
                            }
                            if (json.has("set_score")) {
                                mTeamsScoreUpdate = json.getString("set_score");
                            } else {
                                mTeamsScoreUpdate = "";
                            }

                            JSONArray squadArray = new JSONArray(object.getString("players"));
                            mOpponentSquadInfos = new ArrayList<>();
                            if (squadArray.length() > 0) {
                                for (int i = 0; i < squadArray.length(); i++) {
                                    SquadInfo squadInfo = new SquadInfo();
                                    squadInfo.setPlayerName(squadArray.getJSONObject(i).getString("player_name"));
                                    squadInfo.setPlayerLocation(squadArray.getJSONObject(i).getString("location_name"));
                                    squadInfo.setPlayerPhoto(squadArray.getJSONObject(i).getString("profile_image"));
                                    squadInfo.setPlayerId(squadArray.getJSONObject(i).getString("player_id"));
                                    mOpponentSquadInfos.add(squadInfo);
                                }
                            }

                            mMySquadInfos = new ArrayList<>();
                            if (object.has("my_players")) {
                                JSONArray myPlayerArray = new JSONArray(object.getString("my_players"));
                                if (myPlayerArray.length() > 0) {
                                    for (int i = 0; i < myPlayerArray.length(); i++) {
                                        String s_name = myPlayerArray.getJSONObject(i).getString("player_name");
                                        String s_position = myPlayerArray.getJSONObject(i).getString("location_name");
                                        String s_logo = myPlayerArray.getJSONObject(i).getString("profile_image");
                                        String s_id = myPlayerArray.getJSONObject(i).getString("player_id");
                                        MySquadInfo mysquadInfo = new MySquadInfo(s_id, s_name, s_position, s_logo);
                                        mMySquadInfos.add(mysquadInfo);
                                    }
                                }
                            }

                            if (mMatchType.equals("1")) {
                                if (mMatchStatus.equals("8") || mMatchStatus.equals("10") || mMatchStatus.equals("13")) {
                                    mUpdateScoreButton.setEnabled(true);
                                    mUpdateScoreButton.setTextColor(ContextCompat.getColor(CricketGameMatchRequestActivity.this, R.color.white));
                                    mUpdateScoreButton.setText(getResources().getString(R.string.score_update));
                                }
                            }

                            switch (mMatchStatus) {
                                case "5":
                                    mUpdateScoreButton.setEnabled(true);
                                    mUpdateScoreButton.setTextColor(ContextCompat.getColor(CricketGameMatchRequestActivity.this, R.color.white));
                                    mUpdateScoreButton.setText(getResources().getString(R.string.schedule_match));
                                    break;
                                case "1":
                                case "8":
                                case "3":
                                case "6":
                                case "7":
                                case "9":
                                case "10":
                                case "12":
                                case "13":
                                    if (mMatchType.equals("1")) {
                                        if (object.has("set_score")) {
                                            JSONObject scorejson = new JSONObject(object.getString("set_score"));
                                            mTeamsScoreUpdate = scorejson.toString();
                                            mOpponentScoreUpdateStatus = scorejson.getString("opponent_user_score_status");
                                            mMyTeamScoreUpdateStatus = scorejson.getString("login_user_score_status");
                                            if (mOpponentScoreUpdateStatus.equals("2") && mMyTeamScoreUpdateStatus.equals("2")) {
                                                mMatchStatusView.setText("Score updated");
                                            } else if (mOpponentScoreUpdateStatus.equals("1") && mMyTeamScoreUpdateStatus.equals("0")) {
                                                mMatchStatusView.setText("Awaiting score updates from " + mMyTeamName);
                                            } else if (mOpponentScoreUpdateStatus.equals("0") && mMyTeamScoreUpdateStatus.equals("1")) {
                                                mMatchStatusView.setText("Awaiting score updates from " + mOpponentTeamName);
                                            }  else if (mOpponentScoreUpdateStatus.equals("2") && mMyTeamScoreUpdateStatus.equals("0")) {
                                                mMatchStatusView.setText("Awaiting score updates from " + mMyTeamName);
                                            } else if (mOpponentScoreUpdateStatus.equals("0") && mMyTeamScoreUpdateStatus.equals("2")) {
                                                mMatchStatusView.setText("Awaiting score updates from " + mOpponentTeamName);
                                            } else if (mOpponentScoreUpdateStatus.equals("1") && mMyTeamScoreUpdateStatus.equals("2")) {
                                                mMatchStatusView.setText("Score under validation.");
                                            } else if (mOpponentScoreUpdateStatus.equals("2") && mMyTeamScoreUpdateStatus.equals("1")) {
                                                mMatchStatusView.setText("Score under validation.");
                                            } else if (mOpponentScoreUpdateStatus.equals("1") && mMyTeamScoreUpdateStatus.equals("1")) {
                                                mMatchStatusView.setText("Score under validation.");
                                            }
                                        }
                                    }
                                    break;
                                case "2":
                                case "4":
                                    mUpdateScoreButton.setEnabled(true);
                                    mUpdateScoreButton.setTextColor(ContextCompat.getColor(CricketGameMatchRequestActivity.this, R.color.white));
                                    mUpdateScoreButton.setText(getResources().getString(R.string.schedule_match));
                                    break;
                                case "15":
                                    mMatchStatusView.setText("Match is hold");
                                    break;
                                case "20":
                                    mUpdateScoreButton.setText(getResources().getString(R.string.schedule_match));
                                    if (object.has("set_team_form")) {
                                        JSONObject scorejson = new JSONObject(object.getString("set_team_form"));
                                        String opponentTeamFormStatus = scorejson.getString("opponent_user_team_status");
                                        String myTeamFormStatus = scorejson.getString("login_user_team_status");

                                        if (opponentTeamFormStatus.equals("0") && myTeamFormStatus.equals("0")) {
                                            mMatchStatusView.setText(mOpponentTeamName + " team formation is pending");
                                        } else if (opponentTeamFormStatus.equals("0") && myTeamFormStatus.equals("1")) {
                                            mMatchStatusView.setText(mOpponentTeamName + " team form is pending");
                                            mUpdateScoreButton.setEnabled(false);
                                            mUpdateScoreButton.setTextColor(ContextCompat.getColor(CricketGameMatchRequestActivity.this, R.color.white_25));
                                        } else if (opponentTeamFormStatus.equals("1") && myTeamFormStatus.equals("0")) {
                                            mMatchStatusView.setText(mOpponentTeamName + " team formation finished");
                                            mUpdateScoreButton.setEnabled(true);
                                            mUpdateScoreButton.setTextColor(ContextCompat.getColor(CricketGameMatchRequestActivity.this, R.color.white));
                                        } else if (opponentTeamFormStatus.equals("1") && myTeamFormStatus.equals("1")) {
                                            mMatchStatusView.setText(mOpponentTeamName + " team formation finished");
                                        }
                                    }
                                    break;
                                case "11":
                                    mUpdateScoreButton.setEnabled(true);
                                    mUpdateScoreButton.setTextColor(ContextCompat.getColor(CricketGameMatchRequestActivity.this, R.color.white));
                                    mUpdateScoreButton.setText(getResources().getString(R.string.schedule_match));
//                                        mBooking.setVisibility(View.VISIBLE);
                                    break;
                                case "14":
                                    mUpdateScoreButton.setEnabled(false);
                                    mUpdateScoreButton.setTextColor(ContextCompat.getColor(CricketGameMatchRequestActivity.this, R.color.white_25));
                                    mUpdateScoreButton.setText(getResources().getString(R.string.score_update));
//                                    mBooking.setVisibility(View.VISIBLE);
                                    if (json.has("payment_status")) {
                                        if (mLoginUserPaymentStatus.equals("1")) {
//                                            mBooking.setVisibility(View.GONE);
                                        }
                                        if (mOpponentPaymentStatus.equals("0") && mLoginUserPaymentStatus.equals("0")) {
                                            mMatchStatusView.setText(mOpponentTeamName+ " team payment for venue booking is pending");
                                        } else if (mOpponentPaymentStatus.equals("0") && mLoginUserPaymentStatus.equals("1")) {
                                            mMatchStatusView.setText(mOpponentTeamName + " team payment for venue booking is pending");
                                        } else if (mOpponentPaymentStatus.equals("1") && mLoginUserPaymentStatus.equals("0")) {
                                            mMatchStatusView.setText(mOpponentTeamName + " team payment for venue booking is payed");
                                        } else if (mOpponentPaymentStatus.equals("1") && mLoginUserPaymentStatus.equals("1")) {
                                            mMatchStatusView.setText(mOpponentTeamName + " team payment for venue booking is payed");
                                        }
                                    }
                                    break;
                            }
                           /* if (CommonUtils.MatchType(mMatchType).equals("League")) {
                                if (mMatchStatus.equals("8") || mMatchStatus.equals("10")) {
                                    mBooking.setVisibility(View.VISIBLE);
                                    mBooking.setEnabled(true);
                                }
                            } else {
                                mBooking.setVisibility(View.GONE);
                            }*/

                            List<String> tabTitles = new ArrayList<String>();
                            tabTitles.add(mFirstTeamNameView.getText().toString().trim());
                            tabTitles.add(mSecondTeamNameView.getText().toString().trim());
                            setUpViewPagerAdapter(mOpponentSquadInfos, mMySquadInfos, tabTitles);

                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        CommonUtils.ErrorHandleMethod(CricketGameMatchRequestActivity.this, response);
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                    }
                } else {
                    mUpdateScoreButton.setEnabled(false);
                    mUpdateScoreButton.setTextColor(ContextCompat.getColor(CricketGameMatchRequestActivity.this, R.color.white_25));
                    mOpponentChatFabView.setEnabled(false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                CommonUtils.ServerFailureHandleMethod(CricketGameMatchRequestActivity.this, t);
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
        });
    }

    private void setUpViewPagerAdapter(List<SquadInfo> opponentSquadInfos, List<MySquadInfo> mySquadInfos, List<String> tabTitles) {
        MatchRequestPagerAdapter viewPagerAdapter = new MatchRequestPagerAdapter(getSupportFragmentManager(), opponentSquadInfos, mySquadInfos, tabTitles);
        mTeamScoreViewPager.setAdapter(viewPagerAdapter);
        mTeamScoreViewPager.setOffscreenPageLimit(1);
        mTeamsTitleTabLayout.setupWithViewPager(mTeamScoreViewPager);
    }

    private void enableScoreUpdateButton(String matchDate) {
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DefaultText.DATE_MONTH_YEAR);
        String todayDate = dateFormat.format(today);
        if (todayDate.compareTo(matchDate) > 0) {
            mUpdateScoreButton.setEnabled(true);
            mUpdateScoreButton.setTextColor(ContextCompat.getColor(CricketGameMatchRequestActivity.this, R.color.white));
        } else if(todayDate.compareTo(matchDate) < 0) {
            if (mUpdateScoreButton.getText().toString().trim().equals(getResources().getString(R.string.score_update))) {
                mUpdateScoreButton.setEnabled(false);
                mUpdateScoreButton.setTextColor(ContextCompat.getColor(CricketGameMatchRequestActivity.this, R.color.white_25));
            }
        }  else if(todayDate.compareTo(matchDate) == 0) {
            if (mUpdateScoreButton.getText().toString().trim().equals(getResources().getString(R.string.score_update))) {
                mUpdateScoreButton.setEnabled(true);
                mUpdateScoreButton.setTextColor(ContextCompat.getColor(CricketGameMatchRequestActivity.this, R.color.white));
            }  else {
                mUpdateScoreButton.setEnabled(false);
                mUpdateScoreButton.setTextColor(ContextCompat.getColor(CricketGameMatchRequestActivity.this, R.color.white_25));
            }
        }
    }

    public void HashGenerationServer() {
        final PreferenceHelper preferenceHelper = new PreferenceHelper(CricketGameMatchRequestActivity.this, Constants.APP_NAME, 0);
        String auth_token = "Bearer " + preferenceHelper.getString("user_token", "");
        PaymentModel m = new PaymentModel();
        m.setPaymentMatchId(mMatchId);
        m.setPaymentTeamId(mMyTeamId);
        m.setPaymentAmount(mPaymentAmount);
        Call<ResponseBody> call = mApiInterface.PaymentRequest(auth_token, m);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        PayuHashes payuHashes = new PayuHashes();
                        String data = response.body().string();
                        JSONObject json = new JSONObject(data);
                        JSONObject datajson = new JSONObject(json.getString("data"));
                        String amount = datajson.getString("amount");
                        String first_name = datajson.getString("first_name");
                        String email = datajson.getString("email");
                        String phone = datajson.getString("udf1");
                        String matchid = datajson.getString("udf2");
                        String teamid = datajson.getString("udf3");
                        String userid = datajson.getString("udf4");
                        String surl = datajson.getString("surl");
                        String furl = datajson.getString("furl");
                        String productinfo = datajson.getString("productinfo");

                        payuHashes.setPaymentHash(datajson.getString("payment_hash"));
                        payuHashes.setVasForMobileSdkHash(datajson.getString("vas_for_mobile_sdk_hash"));
                        payuHashes.setPaymentRelatedDetailsForMobileSdkHash(datajson.getString("payment_related_details_for_mobile_sdk_hash"));
                        payuHashes.setMerchantIbiboCodesHash(datajson.getString("get_merchant_ibibo_codes_hash"));

                        int environment = PayuConstants.STAGING_ENV;
                        String userCredentials = null; //merchantKey + ":" + "niranjan@ummstudios.com";
                        String merchantKey = "gtKFFx";
                        //TODO Below are mandatory params for hash genetation
                        mPaymentParams = new PaymentParams();
                        mPaymentParams.setKey(merchantKey);
                        mPaymentParams.setProductInfo(productinfo);
                        mPaymentParams.setFirstName(first_name);
                        mPaymentParams.setEmail(email);
                        mPaymentParams.setSurl(surl);   //https://payu.herokuapp.com/success
                        mPaymentParams.setFurl(furl);   //https://payu.herokuapp.com/failure
                        mPaymentParams.setAmount(amount);
                        mPaymentParams.setTxnId(datajson.getString("txnid"));
                        mPaymentParams.setUdf1(phone);
                        mPaymentParams.setUdf2(matchid); //matchid
                        mPaymentParams.setUdf3(teamid); //teamid
                        mPaymentParams.setUdf4(userid);
                        mPaymentParams.setUdf5("");
                        mPaymentParams.setUserCredentials(userCredentials);

                        payuConfig = new PayuConfig();
                        payuConfig.setEnvironment(environment);

                        launchSdkUI(payuHashes);
                    } else {
                        if (response.errorBody() != null) {
                            String error = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(error);
                            String msg = jsonObject.getString("message");
                            String code = jsonObject.getString("code");
                            if (!code.equals("500")) {
                                Toast.makeText(CricketGameMatchRequestActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String error = response.message();
                            Toast.makeText(CricketGameMatchRequestActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //  Log error here since request failed
                try {
                    if (t instanceof SocketTimeoutException) {
                        Toast.makeText(CricketGameMatchRequestActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CricketGameMatchRequestActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == PayuConstants.PAYU_REQUEST_CODE) {
            if (data != null) {
                /**
                 * Here, data.getStringExtra("payu_response") ---> Implicit response sent by PayU
                 * data.getStringExtra("result") ---> Response received from merchant's Surl/Furl
                 *
                 * PayU sends the same response to merchant server and in app. In response check the value of key "status"
                 * for identifying status of transaction. There are two possible status ic_like, success or failure
                 * */
                /*new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme)
                        .setCancelable(false)
                        .setMessage("Payu's Data : " + data.getStringExtra("payu_response") + "\n\n\n Merchant's Data: " + data.getStringExtra("result"))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }).show();*/

                String response = data.getStringExtra("payu_response");
                try {
                    JSONObject json = new JSONObject(response);
                    String successresponse = json.getString("status");
                    if (successresponse.equals("success")) {
                        Intent homeintent = new Intent(CricketGameMatchRequestActivity.this, HomeActivity.class);
                        startActivity(homeintent);
                        finish();
                    } else {
                        finish();
                    }
                    String successresponse2 = json.getString("field9");
                    Log.d("JSON", response + successresponse + "STATUS  " + successresponse2 + "FIELD9 ");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(CricketGameMatchRequestActivity.this, "Payment not successful.", Toast.LENGTH_LONG).show(); // getString(R.string.could_not_receive_data)
            }
        } else if(requestCode == Constants.SCORE_UPDATED){
            if (resultCode == Activity.RESULT_OK){
                finish();
            }
        }
    }

    public void launchSdkUI(PayuHashes payuHashes) {
        String salt = "eCwWELxi";
        Intent intent = new Intent(CricketGameMatchRequestActivity.this, PayUBaseActivity.class);
        intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
        intent.putExtra(PayuConstants.PAYMENT_PARAMS, mPaymentParams);
        intent.putExtra(PayuConstants.PAYU_HASHES, payuHashes);
        intent.putExtra(PayuConstants.SALT, salt);
        startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);
        //Lets fetch all the one click card tokens first
        //fetchMerchantHashes(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
    }

    private class MatchRequestPagerAdapter extends FragmentStatePagerAdapter {
        private List<SquadInfo> mOpponentSquadInfos;
        private List<MySquadInfo> mMySquadInfos;
        private List<String> mTabTitles = new ArrayList<>();

        public MatchRequestPagerAdapter(FragmentManager supportFragmentManager, List<SquadInfo> opponentSquadInfos, List<MySquadInfo> mySquadInfos, List<String> tabTitles) {
            super(supportFragmentManager);
            mOpponentSquadInfos = opponentSquadInfos;
            mMySquadInfos = mySquadInfos;
            mTabTitles = tabTitles;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            switch (position) {
                case 0:
                    fragment = FirstTeamSquadFragment.newInstance(mMySquadInfos);
                    break;
                case 1:
                    fragment = SecondTeamSquadFragment.newInstance(mOpponentSquadInfos);
                    break;
                default:
                    fragment = null;
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return mTabTitles.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitles.get(position);
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }
}
