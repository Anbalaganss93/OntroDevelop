package com.ontro.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.ontro.ChatDetailActivity;
import com.ontro.Constants;
import com.ontro.CricketScoreUpdateActivity;
import com.ontro.FootballScoreUpdateActivity;
import com.ontro.HomeActivity;
import com.ontro.MyTeamFormationActivity;
import com.ontro.R;
import com.ontro.ScheduleActivity;
import com.ontro.ScoreUpdateBasketballActivity;
import com.ontro.SetGameScoreUpdateActivity;
import com.ontro.customui.ProfileImageView;
import com.ontro.dto.ChatUser;
import com.ontro.dto.MatchRequestResponseModel;
import com.ontro.dto.MySquadInfo;
import com.ontro.dto.PaymentModel;
import com.ontro.dto.ScheduleConfirmInput;
import com.ontro.dto.SquadInfo;
import com.ontro.rest.ApiClient;
import com.ontro.rest.ApiInterface;
import com.ontro.rest.TeamDetailResponse;
import com.ontro.utils.CommonUtils;
import com.ontro.utils.PreferenceHelper;
import com.payu.india.Extras.PayUSdkDetails;
import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Payu.Payu;
import com.payu.india.Payu.PayuConstants;
import com.payu.payuui.Activity.PayUBaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Android on 27-Feb-17.
 */

public class MatchRequestInfoFragment extends Fragment implements View.OnClickListener,
        AdapterView.OnItemSelectedListener, View.OnTouchListener, SwipeRefreshLayout.OnRefreshListener {

    String matchstatus = "";
    int environment;
    private ApiInterface apiInterface;
    private PreferenceHelper preferenceHelper;
    private Dialog progress;
    private String matchId = "", approvalStatus, teamId = "", myteamid = "", venueBookingId = "", wonTeamId, mPayAmount = "0",
            myTeamUpdateStatus, opponentTeamUpdateStatus, opponentTeamName, opponentTeamId;
    private TextView mMatchTypeTextView, mOpponentTeamNameView, mMatchDateTextView, mVenueDetailTextView,
            mMatchStatusTextView, mAmountTitle, mVenueAmount;
    private Gson gson;
    private Button request_status, mBooking, Accept_request_status, scoreupdate;
    private LinearLayout mMatchInfoLayout;
    private ProfileImageView mTeamImage;
    private LayoutInflater mLayoutInflater;
    private int formcount = 0;
    private JSONArray array, mTeamScoreArray, mPlayerScoreArray, mBatManArray, mBowlerArray;
    private Spinner winningteam2;
    private View mainview;
    private Typeface typeface_regular;
    private String resultapprovestatus;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private TextView mPlayerLocationTextView;
    private ArrayList<SquadInfo> mSquadInfos;
    private ArrayList<MySquadInfo> mySquadPlayers;
    private String team_name;
    private GestureDetector gestureDetector;
    private SwipeRefreshLayout swipeLayout;
    private String merchantKey = "gtKFFx", userCredentials;
    private PaymentParams mPaymentParams;
    private PayuConfig payuConfig;
    private String mOpponentPayStatus = "", mUserPayStatus = "";
    private String salt = "eCwWELxi";
    private String mMatchType;
    private ChatUser chatUser;
    private String mSportid;
    private String mTeamsScoreUpdate;
    private String opponent_team_logo;
    private String current_user_logo;
    private JSONObject scorejson;
    private ImageView mTeamSportTypeImageView;
    private int mMatchStatusPosition;

    public static MatchRequestInfoFragment newInstance(int matchStatusPosition) {
        MatchRequestInfoFragment matchRequestInfoFragment = new MatchRequestInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BundleKeys.MATCH_STATUS_POSITION, matchStatusPosition);
        matchRequestInfoFragment.setArguments(bundle);
        return new MatchRequestInfoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainview = inflater.inflate(R.layout.fragment_match_request_info_layout, container, false);

        gson = new Gson();
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        Payu.setInstance(getActivity());
        PayUSdkDetails payUSdkDetails = new PayUSdkDetails();

        typeface_regular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto_regular.ttf");
        mMatchInfoLayout = (LinearLayout) mainview.findViewById(R.id.match_detail_ll_container);
        mMatchTypeTextView = (TextView) mainview.findViewById(R.id.match_detail_info_tv_matchtype);
        mVenueDetailTextView = (TextView) mainview.findViewById(R.id.match_detail_info_tv_venuedetail);
        mMatchDateTextView = (TextView) mainview.findViewById(R.id.match_detail_info_tv_date);
        mMatchStatusTextView = (TextView) mainview.findViewById(R.id.match_detail_info_tv_status);
        mOpponentTeamNameView = (TextView) mainview.findViewById(R.id.match_detail_info_tv_opponentteam);
        mAmountTitle = (TextView) mainview.findViewById(R.id.match_detail_info_tv_amounttitle);
        mVenueAmount = (TextView) mainview.findViewById(R.id.match_detail_info_tv_venueamount);
        scoreupdate = (Button) mainview.findViewById(R.id.match_request_scoreupdate);
        mBooking = (Button) mainview.findViewById(R.id.request_booking);
        scoreupdate.setVisibility(View.GONE);
        mVenueAmount.setVisibility(View.GONE);
        mAmountTitle.setVisibility(View.GONE);
        mBooking.setVisibility(View.GONE);
        mMatchInfoLayout.setMinimumHeight((int) (metrics.heightPixels * 0.85));
        swipeLayout = (SwipeRefreshLayout) mainview.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        return mainview;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NestedScrollView mscroll = (NestedScrollView) view.findViewById(R.id.mScrollView);
        request_status = (Button) view.findViewById(R.id.request_status);
        Accept_request_status = (Button) view.findViewById(R.id.Accept_request_status);
        Button friends_chat = (Button) view.findViewById(R.id.friends_chat);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) getActivity().findViewById(R.id.activity_match_request_collapsing_toolbar);
        mPlayerLocationTextView = (TextView) getActivity().findViewById(R.id.activity_match_request_tv_player_or_team_location);
        mTeamImage = (ProfileImageView) getActivity().findViewById(R.id.activity_match_request_iv_player_or_team);
        mTeamSportTypeImageView = (ImageView) getActivity().findViewById(R.id.activity_match_request_iv_sport_type);

        /*MaterialViewPagerHelper.registerScrollView(getActivity(), mscroll);*/
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Typeface typeface_regular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto_regular.ttf");
        preferenceHelper = new PreferenceHelper(getActivity(), Constants.APP_NAME, 0);

        matchId = preferenceHelper.getString("match_id", "");
        teamId = preferenceHelper.getString("teamid", "");
        myteamid = preferenceHelper.getString("myteamid", "");

        progress = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progress.setContentView(R.layout.progressdialog_layout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // only for LOLLIPOP and newer versions
            request_status.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_bg));
        } else {
            request_status.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_bg_normal));
        }

        mLayoutInflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        request_status.setVisibility(View.GONE);

        if (CommonUtils.isNetworkAvailable(getActivity())) {
            progress.show();
            getMatchDetails();
        } else {
            Toast.makeText(getActivity(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }

        friends_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChatDetailActivity.class);
                intent.putExtra(Constants.BundleKeys.FCM_UID, chatUser);
                startActivity(intent);
                getActivity().finish();
            }
        });

        Accept_request_status.setOnClickListener(this);

        if (getArguments() != null) {
            mMatchStatusPosition = getArguments().getInt(Constants.BundleKeys.MATCH_STATUS_POSITION);
            switch (mMatchStatusPosition) {
                case 1:
                    approvalStatus = "1";
                    Accept_request_status.setVisibility(View.GONE);
                    request_status.setText(getResources().getString(R.string.accept_request));
                    break;
                case 2:
                    approvalStatus = "2";
                    Accept_request_status.setVisibility(View.GONE);
                    request_status.setText(getResources().getString(R.string.cancel_request));
                    break;
            }
        }

        request_status.setTypeface(typeface_regular);
        friends_chat.setTypeface(typeface_regular);
        Accept_request_status.setTypeface(typeface_regular);
        scoreupdate.setTypeface(typeface_regular);
        request_status.setOnClickListener(this);
        scoreupdate.setOnClickListener(this);
        mBooking.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (CommonUtils.isNetworkAvailable(getActivity())) {
            progress.show();
            getMatchDetails();
        } else {
            Toast.makeText(getActivity(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }
    }

    public void getMatchDetails() {
        String auth_token = "Bearer " + preferenceHelper.getString("user_token", "");
        Call<ResponseBody> call = apiInterface.MatchDetail(auth_token, matchId, teamId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        mMatchInfoLayout.setVisibility(View.VISIBLE);
                        Log.d("MATCHDETAILS", data);
                        JSONObject alldata = new JSONObject(data);
                        JSONObject json = new JSONObject(alldata.getString("data"));
                        String matchid = json.getString("match_id");
                        int flagType = json.getInt("flag_type");
                        if(flagType > 0) {
                            scoreupdate.setVisibility(View.VISIBLE);
                        } else {
                            scoreupdate.setVisibility(View.GONE);
                        }
                        mMatchType = json.getString("match_type");
                        if (json.has("pay_amount")) {
                            mPayAmount = json.getString("pay_amount");
                            mVenueAmount.setText("Rs." + mPayAmount);
                            mVenueAmount.setVisibility(View.VISIBLE);
                            mAmountTitle.setVisibility(View.VISIBLE);
                        }
                        String matchlocation = json.getString("match_location");
                        String teamlocation = json.getString("team_location");
                        opponentTeamName = json.getString("opponent_team_name");
                        opponentTeamId = json.getString("opponent_team_id");
                        teamId = json.getString("team_id");
                        preferenceHelper.save("teamId", "");
                        mOpponentTeamNameView.setText(opponentTeamName);
                        if (json.has("sport")) {
                            mSportid = json.getString("sport");
                        }
                        if (json.has("venue_booking_id")) {
                            venueBookingId = json.getString("venue_booking_id");
                        }
                        String venueName = "", venueBookingDate = "", venueBookingFromTime = "", venueBookingToTime = "";
                        if (json.has("venue_name")) {
                            if (!json.getString("venue_name").equals("null") && json.getString("venue_name").length() != 0) {
                                venueName = json.getString("venue_name");
                            }
                        }

                        if (json.has("venue_booking_date")) {
                            venueBookingDate = json.getString("venue_booking_date");
                        }

                        if (json.has("venue_booking_from_time")) {
                            venueBookingFromTime = getTimeFormat(json.getString("venue_booking_from_time"));
                        }
                        if (json.has("venue_booking_to_time")) {
                            venueBookingToTime = getTimeFormat(json.getString("venue_booking_to_time"));
                        }
                        opponent_team_logo = json.getString("team_logo");
                        current_user_logo = json.getString("opponent_team_logo");
                        if (!opponent_team_logo.isEmpty()) {
                            Glide.with(getActivity()).load(opponent_team_logo).placeholder(R.drawable.profiledefaultimg).dontAnimate().into(mTeamImage);
                        } else {
                            Glide.with(getActivity()).load(R.drawable.profiledefaultimg).dontAnimate().into(mTeamImage);
                        }

                        team_name = json.getString("team_name");
                        String matchdate = json.getString("match_date");
                        String level = json.getString("level");

                        chatUser = new ChatUser();
                        chatUser.setUniqueId(json.getString("player_fcm_uid"));
                        chatUser.setUserName(json.getString("player_name"));
                        chatUser.setImageUrl(json.getString("profile_image"));
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                        String status = "";
                        if (json.has("status_of_match")) {
                            status = json.getString("status_of_match");
                        }

                        if (json.has("matchstatus")) {
                            matchstatus = json.getString("matchstatus");
                        }

                        if (json.has("payment_status")) {
                            JSONObject payjson = new JSONObject(json.getString("payment_status"));
                            mOpponentPayStatus = payjson.getString("opponent_user_pay_status");
                            mUserPayStatus = payjson.getString("login_user_pay_status");
                        }

                        JSONArray squadArray = new JSONArray(json.getString("players"));

                        ArrayList<SquadInfo> arrayList = new ArrayList<>();
                        if (json.has("my_players")) {
                            JSONArray myPlayerArray = new JSONArray(json.getString("my_players"));
                            mySquadPlayers = new ArrayList<>();
                            if (myPlayerArray.length() > 0) {
                                for (int i = 0; i < myPlayerArray.length(); i++) {
                                    String s_name = myPlayerArray.getJSONObject(i).getString("player_name");
                                    String s_position = myPlayerArray.getJSONObject(i).getString("location_name");
                                    String s_logo = myPlayerArray.getJSONObject(i).getString("profile_image");
                                    String s_id = myPlayerArray.getJSONObject(i).getString("player_id");
                                    MySquadInfo mysquadInfo = new MySquadInfo(s_id, s_name, s_position, s_logo);
                                    mySquadPlayers.add(mysquadInfo);
                                }
                            }
                        }

                        if (squadArray.length() > 0) {
                            for (int i = 0; i < squadArray.length(); i++) {
                                SquadInfo squadInfo = new SquadInfo();
                                squadInfo.setPlayerName(squadArray.getJSONObject(i).getString("player_name"));
                                squadInfo.setPlayerLocation(squadArray.getJSONObject(i).getString("player_location"));
                                squadInfo.setPlayerPhoto(squadArray.getJSONObject(i).getString("player_photo"));
                                squadInfo.setPlayerId(squadArray.getJSONObject(i).getString("player_id"));
                                arrayList.add(squadInfo);
                            }
                        } else {
                            preferenceHelper.save("info", "");
                        }

//                        TeamDetailResponse teamDetailResponse = new TeamDetailResponse(matchid, mMatchType, matchlocation, matchdate, venueBookingId, status, level, opponent_team_logo, mSportid, arrayList);
                        mSquadInfos = arrayList;
//                        String squadsjson = gson.toJson(teamDetailResponse);
//                        preferenceHelper.save("info", squadsjson);

                        if (mMatchType.equals("1")) {
                            if (matchstatus.equals("8") || matchstatus.equals("10") || matchstatus.equals("13")) {
                                formcount = 0;
//                              sportformcontainer.setVisibility(View.VISIBLE);
                                scoreupdate.setVisibility(View.VISIBLE);
                                if (json.has("set_score")) {
                                    mTeamsScoreUpdate = json.getString("set_score");
                                } else {
                                    mTeamsScoreUpdate = "";
                                }
                                if (json.has("set_score")) {
                                    scorejson = new JSONObject(json.getString("set_score"));
                                    opponentTeamUpdateStatus = scorejson.getString("opponent_user_score_status");
                                    myTeamUpdateStatus = scorejson.getString("login_user_score_status");
                                }
                            }
                        }

                        mMatchStatusTextView.setText(status);
                        switch (matchstatus) {
                            case "5":
                                Accept_request_status.setVisibility(View.GONE);
                                request_status.setVisibility(View.VISIBLE);
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
                                Accept_request_status.setVisibility(View.GONE);
                                request_status.setVisibility(View.GONE);
                                if (mMatchType.equals("1")) {
                                    if (json.has("set_score")) {
                                        if (opponentTeamUpdateStatus.equals("2") && myTeamUpdateStatus.equals("2")) {
                                            scoreupdate.setVisibility(View.GONE);
                                            mMatchStatusTextView.setText("Score updated");
                                        } else if (opponentTeamUpdateStatus.equals("1") && myTeamUpdateStatus.equals("0")) {
                                            mMatchStatusTextView.setText("Awaiting score updates from " + opponentTeamName);
                                        } else if (opponentTeamUpdateStatus.equals("0") && myTeamUpdateStatus.equals("1")) {
                                            mMatchStatusTextView.setText("Awaiting score updates from " + team_name);
                                        } else if (opponentTeamUpdateStatus.equals("3") && myTeamUpdateStatus.equals("2")) {
                                            mMatchStatusTextView.setText("You cancelled " + team_name + " team score details.\n\n" + "Your team score approved by " + team_name + ".");
                                        } else if (opponentTeamUpdateStatus.equals("2") && myTeamUpdateStatus.equals("3")) {
                                            mMatchStatusTextView.setText("Your score details cancelled by " + team_name + ".Please re update.\n\n" + "You approved " + team_name + " score details.");
                                        } else if (opponentTeamUpdateStatus.equals("3") && myTeamUpdateStatus.equals("3")) {
                                            mMatchStatusTextView.setText("You cancelled " + team_name + " team score details.Please re update.\n\n" + opponentTeamName + " cancelled your team score details. Please re update.");
                                        } else if (opponentTeamUpdateStatus.equals("3") && myTeamUpdateStatus.equals("1")) {
                                            mMatchStatusTextView.setText("You cancelled " + team_name + " team score details.Please re update.\n\n" + opponentTeamName + " team score updated");
                                        } else if (opponentTeamUpdateStatus.equals("1") && myTeamUpdateStatus.equals("3")) {
                                            mMatchStatusTextView.setText(team_name + " team score updated.\n\n" + "Your score details cancelled by " + team_name + ".Please re update.");
                                        } else if (opponentTeamUpdateStatus.equals("2") && myTeamUpdateStatus.equals("0")) {
//                                            mMatchStatusTextView.setText(opponentTeamName + " team score approved by you.\n\n" + "Waiting for your team score update");
                                            mMatchStatusTextView.setText("Awaiting score updates from " + opponentTeamName);
                                        } else if (opponentTeamUpdateStatus.equals("0") && myTeamUpdateStatus.equals("2")) {
                                            mMatchStatusTextView.setText("Awaiting score updates from " + team_name);
//                                            mMatchStatusTextView.setText("Your team score approved by " + team_name + "\n\n" + "Waiting for " + team_name + " team score update");
                                        } else if (opponentTeamUpdateStatus.equals("1") && myTeamUpdateStatus.equals("2")) {
//                                            mMatchStatusTextView.setText("Your team score approved by " + team_name + "\n\n" + team_name + " Waiting for your approval.");
//                                            scoreupdate.setVisibility(View.GONE);
                                            mMatchStatusTextView.setText("Score under validation.");
                                        } else if (opponentTeamUpdateStatus.equals("2") && myTeamUpdateStatus.equals("1")) {
//                                            mMatchStatusTextView.setText("Your team waiting for approval.\n\n" + team_name + " team score approved by you");
//                                            scoreupdate.setVisibility(View.GONE);
                                            mMatchStatusTextView.setText("Score under validation.");
                                        } else if (opponentTeamUpdateStatus.equals("3") && myTeamUpdateStatus.equals("0")) {
                                            mMatchStatusTextView.setText("You cancelled " + team_name + " team score details.\n\n" + "Waiting for your team score update.");
                                        } else if (opponentTeamUpdateStatus.equals("0") && myTeamUpdateStatus.equals("3")) {
                                            mMatchStatusTextView.setText("Your team score details cancelled by " + team_name + ".\n\n" + "Waiting for " + team_name + " team score update.");
                                        } else if (opponentTeamUpdateStatus.equals("1") && myTeamUpdateStatus.equals("1")) {
//                                            scoreupdate.setVisibility(View.GONE);
                                            mMatchStatusTextView.setText("Score under validation.");
                                        }
                                    }
                                }
                                break;
                            case "2":
                            case "4":
                                Accept_request_status.setVisibility(View.GONE);
                                request_status.setVisibility(View.VISIBLE);
                                request_status.setText("Schedule");
                                break;
                            case "15":
                                mMatchStatusTextView.setText("Match is hold");
                                break;
                            case "20":
                                Accept_request_status.setVisibility(View.GONE);
                                request_status.setVisibility(View.VISIBLE);
                                request_status.setText("Schedule");
                                if (json.has("set_team_form")) {
                                    JSONObject scorejson = new JSONObject(json.getString("set_team_form"));
                                    String opponentTeamFormStatus = scorejson.getString("opponent_user_team_status");
                                    String myTeamFormStatus = scorejson.getString("login_user_team_status");

                                    if (myTeamFormStatus.equals("1")) {
                                        request_status.setVisibility(View.GONE);
                                    }

                                    if (opponentTeamFormStatus.equals("0") && myTeamFormStatus.equals("0")) {
                                        mMatchStatusTextView.setText(team_name + " team formation is pending.\n\nYour team formation is pending.");
                                    } else if (opponentTeamFormStatus.equals("0") && myTeamFormStatus.equals("1")) {
                                        mMatchStatusTextView.setText(team_name + " team form is pending.\n\nYour team formation finished.");
                                    } else if (opponentTeamFormStatus.equals("1") && myTeamFormStatus.equals("0")) {
                                        mMatchStatusTextView.setText(team_name + " team formation finished.\n\nYour team formation is pending.");
                                    } else if (opponentTeamFormStatus.equals("1") && myTeamFormStatus.equals("1")) {
                                        mMatchStatusTextView.setText(team_name + " team formation finished.\n\nYour team formation finished.");
                                    }
                                }
                                break;
                            case "11":
                                if (mMatchStatusPosition == 2) {
                                    Accept_request_status.setVisibility(View.VISIBLE);
                                    request_status.setVisibility(View.VISIBLE);
                                    mBooking.setVisibility(View.VISIBLE);
                                } else {
                                    Accept_request_status.setVisibility(View.GONE);
                                    request_status.setVisibility(View.GONE);
                                    mMatchStatusTextView.setText("Match scheduled by opponent.");
                                   /* Accept_request_status.setVisibility(View.GONE);
                                    request_status.setVisibility(View.VISIBLE);
                                    request_status.setText("Form team");*/
                                }
                                break;
                            case "14":
                                Accept_request_status.setVisibility(View.GONE);
                                request_status.setVisibility(View.GONE);
                                mBooking.setVisibility(View.VISIBLE);
                                if (json.has("payment_status")) {
                                    if (mUserPayStatus.equals("1")) {
                                        mBooking.setVisibility(View.GONE);
                                    }
                                    if (mOpponentPayStatus.equals("0") && mUserPayStatus.equals("0")) {
                                        mMatchStatusTextView.setText(team_name + " team payment for venue booking is pending.\n\nYour team payment for venue booking is pending.");
                                    } else if (mOpponentPayStatus.equals("0") && mUserPayStatus.equals("1")) {
                                        mMatchStatusTextView.setText(team_name + " team payment for venue booking is pending.\n\nYour team payment for venue booking is payed.");
                                    } else if (mOpponentPayStatus.equals("1") && mUserPayStatus.equals("0")) {
                                        mMatchStatusTextView.setText(team_name + " team payment for venue booking is payed.\n\nYour team payment for venue booking is pending.");
                                    } else if (mOpponentPayStatus.equals("1") && mUserPayStatus.equals("1")) {
                                        mMatchStatusTextView.setText(team_name + " team payment for venue booking is payed.\n\nYour team payment for venue booking is payed.");
                                    }
                                }
                                break;
                        }

                        mMatchTypeTextView.setText(CommonUtils.MatchType(mMatchType));
                        if (CommonUtils.MatchType(mMatchType).equals("League")) {
                            if (matchstatus.equals("8") || matchstatus.equals("10")) {
                                mBooking.setVisibility(View.VISIBLE);
                                mBooking.setEnabled(true);
                            }
                        } else {
                            mBooking.setVisibility(View.GONE);
                        }
                        if (!venueName.equals("")) {
                            mVenueDetailTextView.setText(venueName + "\n" + matchlocation);
                        } else {
                            mVenueDetailTextView.setText(matchlocation);
                        }

                        mCollapsingToolbarLayout.setVisibility(View.VISIBLE);
                        mCollapsingToolbarLayout.setTitle(team_name);
                        mPlayerLocationTextView.setText(teamlocation);
                        mTeamSportTypeImageView.setImageResource(CommonUtils.sportCheck(mSportid));
                        if (!venueBookingDate.equals("") && !venueBookingDate.equals("null")) {
                            String bookingDate = CommonUtils.convertDateFormat(venueBookingDate, Constants.DefaultText.YEAR_MONTH_DATE, Constants.DefaultText.DATE_MONTH_YEAR);
                            mMatchDateTextView.setText(bookingDate + " " + venueBookingFromTime + "-" + venueBookingToTime);
                            enableScoreUpdateButton(bookingDate);
                        } else {
                            String matchDate = CommonUtils.convertDateFormat(matchdate, Constants.DefaultText.YEAR_MONTH_DATE, Constants.DefaultText.DATE_MONTH_YEAR);
                            mMatchDateTextView.setText(matchDate);
                            enableScoreUpdateButton(matchDate);
                        }
                    } else {
                        CommonUtils.ErrorHandleMethod(getActivity(), response);
                        if (progress != null && progress.isShowing()) {
                            progress.dismiss();
                        }
                    }
                } catch (Exception e) {
                    mMatchInfoLayout.setVisibility(View.GONE);
                    e.printStackTrace();
                }
                if (swipeLayout.isRefreshing()) {
                    swipeLayout.setRefreshing(false);
                }
                if (progress != null && progress.isShowing()) {
                    progress.dismiss();
                }
            }

            private String getTimeFormat(String openingTime) {
                String time = null;
                try {
                    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                    Date date = dateFormat.parse(openingTime);
                    DateFormat simpleDateFormat = new SimpleDateFormat("h:mma");
                    time = simpleDateFormat.format(date).toLowerCase();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return time;
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //  Log error here since request failed
                CommonUtils.ServerFailureHandleMethod(getActivity(), t);
                if (progress != null && progress.isShowing()) {
                    progress.dismiss();
                }
            }
        });
    }

    private void enableScoreUpdateButton(String matchDate) {
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DefaultText.DATE_MONTH_YEAR);
        String todayDate = dateFormat.format(today);
        if (todayDate.equals(matchDate)) {
            scoreupdate.setEnabled(true);
        } else {
            scoreupdate.setEnabled(false);
        }
    }

    public void getMatchAccept() {
        String auth_token = "Bearer " + preferenceHelper.getString("user_token", "");
        MatchRequestResponseModel input = new MatchRequestResponseModel();
        input.setMatchId(matchId);
        input.setMatchStatus(approvalStatus);
        Call<ResponseBody> call = apiInterface.MatchConfirmationStatus(auth_token, input);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        JSONObject json = new JSONObject(data);
                        String message = json.getString("message");
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

                        if (CommonUtils.isNetworkAvailable(getActivity())) {
                            progress.show();
                            getMatchDetails();
                        } else {
                            Toast.makeText(getActivity(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (response.errorBody() != null) {
                            String error = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(error);
                            String msg = jsonObject.getString("message");
                            String code = jsonObject.getString("code");
                            if (!code.equals("500")) {
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String error = response.message();
                            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (progress != null && progress.isShowing()) {
                    progress.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //  Log error here since request failed
                try {
                    if (t instanceof SocketTimeoutException) {
                        Toast.makeText(getActivity(), R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (progress != null && progress.isShowing()) {
                    progress.dismiss();
                }
            }
        });
    }

    public void getMatchScheduledAccept() {
        String auth_token = "Bearer " + preferenceHelper.getString("user_token", "");
        ScheduleConfirmInput input = new ScheduleConfirmInput();
        input.setVenues_booking_id(venueBookingId);
        input.setVenue_status(approvalStatus);
        Call<ResponseBody> call = apiInterface.ScheduleConfirm(auth_token, input);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        JSONObject json = new JSONObject(data);
                        String message = json.getString("message");
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

                        if (CommonUtils.isNetworkAvailable(getActivity())) {
                            progress.show();
                            getMatchDetails();
                        } else {
                            Toast.makeText(getActivity(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (response.errorBody() != null) {
                            String error = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(error);
                            String msg = jsonObject.getString("message");
                            String code = jsonObject.getString("code");
                            if (!code.equals("500")) {
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String error = response.message();
                            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (progress != null && progress.isShowing()) {
                    progress.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //  Log error here since request failed
                try {
                    if (t instanceof SocketTimeoutException) {
                        Toast.makeText(getActivity(), R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (progress != null && progress.isShowing()) {
                    progress.dismiss();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.request_status:
                if (request_status.getText().toString().trim().equals("Schedule")) {
                    Intent intent = new Intent(getActivity(), ScheduleActivity.class);
                    intent.putExtra(Constants.BundleKeys.MATCH_TYPE, CommonUtils.MatchType(mMatchType));
                    startActivity(intent);

                } else if (request_status.getText().toString().trim().equals("Form team")) {
                    Intent intent = new Intent(getActivity(), MyTeamFormationActivity.class);
                    startActivity(intent);
                } else {
                    if (CommonUtils.isNetworkAvailable(getActivity())) {
                        progress.show();
                        getMatchAccept();
                    } else {
                        Toast.makeText(getActivity(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.Accept_request_status:
                approvalStatus = "1";
                if (CommonUtils.isNetworkAvailable(getActivity())) {
                    progress.show();
                    if (matchstatus.equals("11")) {
                        Intent intent = new Intent(getActivity(), MyTeamFormationActivity.class);
                        intent.putExtra("Venueid", venueBookingId);
                        startActivity(intent);
                    } else {
                        getMatchScheduledAccept();
                    }
                } else {
                    Toast.makeText(getActivity(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.match_request_scoreupdate:
                String sportId = preferenceHelper.getString("teamsport", "");
                switch (sportId) {
                    case "1":
                    case "3":
                    case "6":
                    case "7":
                        if (CommonUtils.isNetworkAvailable(getActivity())) {
                            Intent intent = new Intent(getActivity(), SetGameScoreUpdateActivity.class);
                            intent.putExtra("team_name", team_name);
                            intent.putExtra("opponent_team_name", opponentTeamName);
                            intent.putExtra("team_update_status", mMatchStatusTextView.getText().toString().trim());
                            intent.putExtra("date", mMatchDateTextView.getText().toString().trim());
                            intent.putExtra("location", mVenueDetailTextView.getText().toString().trim());
                            intent.putExtra("sportid", mSportid);
                            intent.putExtra("teams_score_update", mTeamsScoreUpdate);
                            intent.putExtra("opponentimage", opponent_team_logo);
                            intent.putExtra("userimage", current_user_logo);
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            Toast.makeText(getActivity(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "2":
                        Intent scoreupdateIntent = new Intent(getActivity(), ScoreUpdateBasketballActivity.class);
                        Bundle bundle1 = new Bundle();
                        bundle1.putString(Constants.BundleKeys.MY_TEAM_NAME, opponentTeamName);
                        bundle1.putString(Constants.BundleKeys.OPPONENT_TEAM_NAME, team_name);
                        bundle1.putString(Constants.BundleKeys.MY_TEAM_IMAGE, current_user_logo);
                        bundle1.putString(Constants.BundleKeys.OPPONENT_TEAM_IMAGE, opponent_team_logo);
                        bundle1.putString(Constants.BundleKeys.SCORE_UPDATE_STATUS, mMatchStatusTextView.getText().toString().trim());
                        bundle1.putString(Constants.BundleKeys.MATCH_DATE, mMatchDateTextView.getText().toString().trim());
                        bundle1.putString(Constants.BundleKeys.MATCH_VENUE, mVenueDetailTextView.getText().toString().trim());
                        bundle1.putString(Constants.BundleKeys.SPORT_ID, mSportid);
                        bundle1.putString(Constants.BundleKeys.TEAM_UPDATE_STATUS, scorejson.toString());
                        bundle1.putString(Constants.BundleKeys.TEAM_ID, teamId);
                        bundle1.putString(Constants.BundleKeys.OPPONENT_TEAM_ID, opponentTeamId);
                        bundle1.putString(Constants.BundleKeys.MATCH_ID, matchId);
                        bundle1.putSerializable(Constants.BundleKeys.MY_TEAM_SQUAD, mySquadPlayers);
                        scoreupdateIntent.putExtras(bundle1);
                        startActivity(scoreupdateIntent);
                        getActivity().finish();
                        break;
                    case "4":
                        Intent cricketScoreUpdateIntent = new Intent(getActivity(), CricketScoreUpdateActivity.class);
                        Bundle cricketScoreUpdateBundle = new Bundle();
                        cricketScoreUpdateBundle.putString(Constants.BundleKeys.MY_TEAM_NAME, team_name);
                        cricketScoreUpdateBundle.putString(Constants.BundleKeys.OPPONENT_TEAM_NAME, opponentTeamName);
                        cricketScoreUpdateBundle.putString(Constants.BundleKeys.MY_TEAM_IMAGE, current_user_logo);
                        cricketScoreUpdateBundle.putString(Constants.BundleKeys.OPPONENT_TEAM_IMAGE, opponent_team_logo);
                        cricketScoreUpdateBundle.putString(Constants.BundleKeys.SCORE_UPDATE_STATUS, mMatchStatusTextView.getText().toString().trim());
                        cricketScoreUpdateBundle.putString(Constants.BundleKeys.MATCH_DATE, mMatchDateTextView.getText().toString().trim());
                        cricketScoreUpdateBundle.putString(Constants.BundleKeys.MATCH_VENUE, mVenueDetailTextView.getText().toString().trim());
                        cricketScoreUpdateBundle.putString(Constants.BundleKeys.SPORT_ID, mSportid);
                        cricketScoreUpdateBundle.putString(Constants.BundleKeys.TEAM_ID, teamId);
                        cricketScoreUpdateBundle.putString(Constants.BundleKeys.OPPONENT_TEAM_ID, opponentTeamId);
                        cricketScoreUpdateBundle.putString(Constants.BundleKeys.MATCH_ID, matchId);
                        cricketScoreUpdateBundle.putString(Constants.BundleKeys.OPPONENT_USER_SCORE_STATUS, opponentTeamUpdateStatus);
                        cricketScoreUpdateBundle.putString(Constants.BundleKeys.LOGIN_USER_SCORE_STATUS, myTeamUpdateStatus);
                        cricketScoreUpdateBundle.putSerializable(Constants.BundleKeys.MY_TEAM_SQUAD, mySquadPlayers);
                        cricketScoreUpdateIntent.putExtras(cricketScoreUpdateBundle);
                        startActivity(cricketScoreUpdateIntent);
                        getActivity().finish();
                        break;
                    case "5":
                        Intent footballScoreUpdateIntent = new Intent(getActivity(), FootballScoreUpdateActivity.class);
                        Bundle footballScoreUpdateBundle = new Bundle();
                        footballScoreUpdateBundle.putString(Constants.BundleKeys.MY_TEAM_NAME, team_name);
                        footballScoreUpdateBundle.putString(Constants.BundleKeys.OPPONENT_TEAM_NAME, opponentTeamName);
                        footballScoreUpdateBundle.putString(Constants.BundleKeys.MY_TEAM_IMAGE, opponent_team_logo);
                        footballScoreUpdateBundle.putString(Constants.BundleKeys.OPPONENT_TEAM_IMAGE, current_user_logo);
                        footballScoreUpdateBundle.putString(Constants.BundleKeys.SCORE_UPDATE_STATUS, mMatchStatusTextView.getText().toString().trim());
                        footballScoreUpdateBundle.putString(Constants.BundleKeys.MATCH_DATE, mMatchDateTextView.getText().toString().trim());
                        footballScoreUpdateBundle.putString(Constants.BundleKeys.MATCH_VENUE, mVenueDetailTextView.getText().toString().trim());
                        footballScoreUpdateBundle.putString(Constants.BundleKeys.SPORT_ID, mSportid);
                        footballScoreUpdateBundle.putString(Constants.BundleKeys.TEAM_ID, teamId);
                        footballScoreUpdateBundle.putString(Constants.BundleKeys.OPPONENT_TEAM_ID, opponentTeamId);
                        footballScoreUpdateBundle.putString(Constants.BundleKeys.MATCH_ID, matchId);
                        footballScoreUpdateBundle.putString(Constants.BundleKeys.OPPONENT_USER_SCORE_STATUS, opponentTeamUpdateStatus);
                        footballScoreUpdateBundle.putString(Constants.BundleKeys.LOGIN_USER_SCORE_STATUS, myTeamUpdateStatus);
                        footballScoreUpdateBundle.putSerializable(Constants.BundleKeys.MY_TEAM_SQUAD, mySquadPlayers);
                        footballScoreUpdateIntent.putExtras(footballScoreUpdateBundle);
                        startActivity(footballScoreUpdateIntent);
                        getActivity().finish();
                        break;
                }
                break;
            case R.id.request_booking:
                HashGenerationServer();
                break;
        }
    }

    public int status(String status) {
        int won = 1;
        switch (status) {
            case "won":
                won = 1;
                break;
            case "lose":
                won = 0;
                break;
        }
        return won;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            Toast.makeText(getActivity(), "Item not selected", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void HashGenerationServer() {
        mBooking.setEnabled(false);
        final PreferenceHelper preferenceHelper = new PreferenceHelper(getActivity(), Constants.APP_NAME, 0);
        String auth_token = "Bearer " + preferenceHelper.getString("user_token", "");
        PaymentModel m = new PaymentModel();
        m.setPaymentMatchId(matchId);
        m.setPaymentTeamId(myteamid);
        m.setPaymentAmount(mPayAmount);
        Call<ResponseBody> call = apiInterface.PaymentRequest(auth_token, m);
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

                        environment = PayuConstants.STAGING_ENV;
                        userCredentials = null; //merchantKey + ":" + "niranjan@ummstudios.com";
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
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String error = response.message();
                            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mBooking.setEnabled(true);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mBooking.setEnabled(true);
                //  Log error here since request failed
                try {
                    if (t instanceof SocketTimeoutException) {
                        Toast.makeText(getActivity(), R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
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
                        Intent homeintent = new Intent(getActivity(), HomeActivity.class);
                        startActivity(homeintent);
                        getActivity().finish();
                    } else {
                        getActivity().finish();
                    }
                    String successresponse2 = json.getString("field9");
                    mBooking.setVisibility(View.GONE);
                    Log.d("JSON", response + successresponse + "STATUS  " + successresponse2 + "FIELD9 ");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getActivity(), "Payment not successful.", Toast.LENGTH_LONG).show(); // getString(R.string.could_not_receive_data)
            }
        }
    }

    public void launchSdkUI(PayuHashes payuHashes) {
        Intent intent = new Intent(getActivity(), PayUBaseActivity.class);
        intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
        intent.putExtra(PayuConstants.PAYMENT_PARAMS, mPaymentParams);
        intent.putExtra(PayuConstants.PAYU_HASHES, payuHashes);
        intent.putExtra(PayuConstants.SALT, salt);
        startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);
        //Lets fetch all the one click card tokens first
        //fetchMerchantHashes(intent);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!(v instanceof EditText)) {
            hideKeyboard(v);
            return true;
        }
        return false;
    }

    private void hideKeyboard(View hideView) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(hideView.getWindowToken(), 0);
    }

    @Override
    public void onRefresh() {
        if (CommonUtils.isNetworkAvailable(getActivity())) {
            if (progress != null && progress.isShowing()) {
                progress.dismiss();
            }
            getMatchDetails();
        } else {
            Toast.makeText(getActivity(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }
    }
}
