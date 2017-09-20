package com.ontro;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ontro.customui.ProfileImageView;
import com.ontro.dto.MatchRequestModel;
import com.ontro.dto.MySquadInfo;
import com.ontro.dto.SquadInfo;
import com.ontro.fragments.FirstTeamSquadFragment;
import com.ontro.fragments.SecondTeamSquadFragment;
import com.ontro.fragments.SetGameTotalScoreOverviewFragment;
import com.ontro.rest.ApiClient;
import com.ontro.rest.ApiInterface;
import com.ontro.utils.CommonUtils;
import com.ontro.utils.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetGameScoreOverViewActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, AppBarLayout.OnOffsetChangedListener {
    private TabLayout mTeamsTitleTabLayout;
    private ViewPager mTeamScoreViewPager;
    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private TextView mMatchDateView, mMatchVenueView, mMatchStatusView;
    private TextView mFirstTeamNameView, mSecondTeamNameView, mFirstTeamTotalScoreView, mSecondTeamTotalScoreView;
    private ImageView mSportImageView, mFirstTeamWonIndicationView, mSecondTeamWonIndicationView;
    private ProfileImageView mFirstTeamImageView, mSecondTeamImageView;
    private Dialog mProgressDialog;
    private Toolbar mToolbar;
    private String mSportType;
    private List<MySquadInfo> mFirstTeamSquads = new ArrayList<>();
    private List<SquadInfo> mSecondTeamSquads = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_game_score_over_view);
        overridePendingTransition(R.anim.slide_in_right, R.anim.pause);
        initView();
        if (CommonUtils.isNetworkAvailable(SetGameScoreOverViewActivity.this)) {
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
            Toast.makeText(SetGameScoreOverViewActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }
        setListener();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.activity_set_game_score_overview_toolbar);
        mTeamsTitleTabLayout = (TabLayout) findViewById(R.id.activity_set_game_score_overview_tl);
        mTeamScoreViewPager = (ViewPager) findViewById(R.id.activity_set_game_score_overview_pager);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.activity_set_game_score_overview_appbar_layout);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.activity_set_game_score_overview_collapsing_toolbar);

        mMatchDateView = (TextView) findViewById(R.id.game_header_date);
        mMatchVenueView = (TextView) findViewById(R.id.game_header_location);
        mFirstTeamNameView = (TextView) findViewById(R.id.game_header_team_one_name);
        mSecondTeamNameView = (TextView) findViewById(R.id.game_header_team_two_name);

        mFirstTeamWonIndicationView = (ImageView) findViewById(R.id.winner_teamone);
        mSecondTeamWonIndicationView = (ImageView) findViewById(R.id.winner_teamtwo);
        mFirstTeamImageView = (ProfileImageView) findViewById(R.id.game_header_team);
        mSecondTeamImageView = (ProfileImageView) findViewById(R.id.game_header_team2);
        mMatchStatusView = (TextView) findViewById(R.id.game_header_team_score_update_status);
        mSportImageView = (ImageView) findViewById(R.id.game_header_sportimage);
        mSportImageView.setImageResource(R.drawable.ic_football_white);
        mProgressDialog = new Dialog(SetGameScoreOverViewActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setContentView(R.layout.progressdialog_layout);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            mToolbar.setTitle("");
            mToolbar.setTitleTextColor(ContextCompat.getColor(SetGameScoreOverViewActivity.this, android.R.color.transparent));
            mToolbar.bringToFront();
        }
    }

    private void setListener() {
        mTeamsTitleTabLayout.addOnTabSelectedListener(this);
        mAppBarLayout.addOnOffsetChangedListener(this);
    }


    public void getMatchDetails(MatchRequestModel matchRequestModel) {
        String matchId = matchRequestModel.getMatchId();
        final String myTeamId = matchRequestModel.getMyTeamId();
        String opponentTeamId = matchRequestModel.getOpponentTeamId();
        mFirstTeamNameView.setText(matchRequestModel.getMyTeamName());
        mSecondTeamNameView.setText(matchRequestModel.getOpponentTeamName());
        final String myTeamName = matchRequestModel.getMyTeamName();
        final String opponentTeamName = matchRequestModel.getOpponentTeamName();
        String opponentTeamLogo = matchRequestModel.getOpponentTeamLogo();
        String myTeamLogo = matchRequestModel.getMyTeamLogo();
        if (opponentTeamLogo != null) {
            Glide.with(SetGameScoreOverViewActivity.this).load(opponentTeamLogo).placeholder(R.drawable.profiledefaultimg).dontAnimate().into(mSecondTeamImageView);
        } else {
            Glide.with(SetGameScoreOverViewActivity.this).load(R.drawable.profiledefaultimg).dontAnimate().into(mSecondTeamImageView);
        }
        if (myTeamLogo != null) {
            Glide.with(SetGameScoreOverViewActivity.this).load(myTeamLogo).placeholder(R.drawable.profiledefaultimg).dontAnimate().into(mFirstTeamImageView);
        } else {
            Glide.with(SetGameScoreOverViewActivity.this).load(R.drawable.profiledefaultimg).dontAnimate().into(mFirstTeamImageView);
        }

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        final PreferenceHelper preferenceHelper = new PreferenceHelper(SetGameScoreOverViewActivity.this, Constants.APP_NAME, 0);
        String authToken = "Bearer " + preferenceHelper.getString("user_token", "");
        Call<ResponseBody> call = apiInterface.ScoreCompleted(authToken, matchId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        if (data.length() > 0) {
                            preferenceHelper.save("match_score", data);
                        } else {
                            preferenceHelper.save("match_score", "");
                        }
                        JSONObject json = new JSONObject(data);
                        JSONObject object = new JSONObject(json.getString("data"));
                        String mMatchId, mWinnerId, mMatchDate, mVenueDate, mVenuename, mLocation, mStatusOfMatch;
                        mMatchId = object.getString("match_id");
                        String mMatchtype = object.getString("match_type");
                        mSportType = object.getString("sport_type");
                        mSportImageView.setImageResource(CommonUtils.scoreUpdateSport(mSportType));
                        mWinnerId = object.getString("winner_team_id ");
                        mLocation = object.getString("match_location");
                        mMatchDate = object.getString("match_date");
                        mVenueDate = object.getString("venue_booking_date");
                        mStatusOfMatch = object.getString("status_of_match");
                        String mVenueAddress = object.getString("venue_address");

                        String venueName = "", venueBookingDate = "", venueBookingFromTime = "", venueBookingToTime = "";
                        if (object.has("venue_name")) {
                            venueName = object.getString("venue_name");
                        }
                        if (object.has("venue_booking_date")) {
                            if (mVenueDate.equals("null")) {
                                venueBookingDate = object.getString("venue_booking_date");
                            } else {
                                venueBookingDate = mMatchDate;
                            }
                        }
                        if (object.has("venue_booking_from_time")) {
                            venueBookingFromTime = getTimeFormat(object.getString("venue_booking_from_time"));
                        }
                        if (object.has("venue_booking_to_time")) {
                            venueBookingToTime = getTimeFormat(object.getString("venue_booking_to_time"));
                        }

                        JSONObject myTeamObject = new JSONObject(object.getString("my_team"));
                        myTeamObject.getString("team_name");

                        String mTeamId = "", mTeamLogo, mTeamName = "", mTeamLocation, mTeamTotalScore = "", mTeamWickets = "", mTeamTotalOvers = "";

                        JSONArray matchScoreArray = new JSONArray(object.getString("match_score"));
                        String mTeamOneId = matchScoreArray.getJSONObject(0).getString("team_id");
                        String mTeamOneLogo = matchScoreArray.getJSONObject(0).getString("team_logo");
                        String mTeamOneName = matchScoreArray.getJSONObject(0).getString("team_name");
                        String mTeamOneLocation = matchScoreArray.getJSONObject(0).getString("team_location");

                        String mTeamTwoId = matchScoreArray.getJSONObject(1).getString("team_id");
                        String mTeamTwoLogo = matchScoreArray.getJSONObject(1).getString("team_logo");
                        String mTeamTwoName = matchScoreArray.getJSONObject(1).getString("team_name");
                        String mTeamTwoLocation = matchScoreArray.getJSONObject(1).getString("team_location");

                        if (mWinnerId.equals(myTeamId)) {
                            mFirstTeamWonIndicationView.setVisibility(View.VISIBLE);
                            mSecondTeamWonIndicationView.setVisibility(View.GONE);
                        } else {
                            mFirstTeamWonIndicationView.setVisibility(View.GONE);
                            mSecondTeamWonIndicationView.setVisibility(View.VISIBLE);
                        }

                        mMatchDateView.setText(CommonUtils.convertDateFormat(mVenueDate, Constants.DefaultText.YEAR_MONTH_DATE, Constants.DefaultText.DATE_MONTH_LETTER) + "," + mLocation);
                        mMatchStatusView.setText(mStatusOfMatch);

                        if (!venueBookingDate.equals("") && !venueBookingDate.equals("null")) {
                            mMatchDateView.setText(venueBookingDate + " " + venueBookingFromTime + "-" + venueBookingToTime);
                        } else {
                            mMatchDateView.setText(CommonUtils.convertDateFormat(mMatchDate, Constants.DefaultText.YEAR_MONTH_DATE, Constants.DefaultText.DATE_MONTH_YEAR));
                        }

                        if (mVenueAddress.length() != 0 && !mVenueAddress.equals("null")) {
                            mMatchVenueView.setText(mVenueAddress);
                        } else if (!venueName.equals("") && !venueName.equals("null")) {
                            mMatchVenueView.setText(venueName + "\n" + mLocation);
                        } else {
                            mMatchVenueView.setText(mLocation);
                        }
                        List<String> tabTitles = new ArrayList<String>();
                        tabTitles.add(getResources().getString(R.string.overview));
                        tabTitles.add(myTeamName);
                        tabTitles.add(opponentTeamName);
                        JSONArray firstTeamMatchPlayerArray = new JSONArray(matchScoreArray.getJSONObject(0).getString("match_player"));
                        JSONArray secondTeamMatchPlayerArray = new JSONArray(matchScoreArray.getJSONObject(1).getString("match_player"));

                        JSONArray firstTeamArray;
                        JSONArray secondTeamArray;
                        if(myTeamId.equals(mTeamOneId)) {
                            firstTeamArray = new JSONArray(matchScoreArray.getJSONObject(0).getString("set_score"));
                            secondTeamArray = new JSONArray(matchScoreArray.getJSONObject(1).getString("set_score"));
                        } else {
                            firstTeamArray = new JSONArray(matchScoreArray.getJSONObject(1).getString("set_score"));
                            secondTeamArray = new JSONArray(matchScoreArray.getJSONObject(0).getString("set_score"));
                        }

                        if (myTeamId.equals(mTeamOneId)) {
                            if (firstTeamMatchPlayerArray.length() > 0) {
                                for (int i = 0; i < firstTeamMatchPlayerArray.length(); i++) {
                                    MySquadInfo squadInfo = new MySquadInfo();
                                    squadInfo.setPlayerId(firstTeamMatchPlayerArray.getJSONObject(i).getString("player_id"));
                                    squadInfo.setPlayerName(firstTeamMatchPlayerArray.getJSONObject(i).getString("player_name"));
                                    squadInfo.setPlayerImage(firstTeamMatchPlayerArray.getJSONObject(i).getString("profile_image"));
                                    mFirstTeamSquads.add(squadInfo);
                                }
                            }
                            if (secondTeamMatchPlayerArray.length() > 0) {
                                for (int i = 0; i < secondTeamMatchPlayerArray.length(); i++) {
                                    SquadInfo squadInfo = new SquadInfo();
                                    squadInfo.setPlayerId(secondTeamMatchPlayerArray.getJSONObject(i).getString("player_id"));
                                    squadInfo.setPlayerName(secondTeamMatchPlayerArray.getJSONObject(i).getString("player_name"));
                                    squadInfo.setPlayerPhoto(secondTeamMatchPlayerArray.getJSONObject(i).getString("profile_image"));
                                    mSecondTeamSquads.add(squadInfo);
                                }
                            }
                        } else {
                            if (secondTeamMatchPlayerArray.length() > 0) {
                                for (int i = 0; i < secondTeamMatchPlayerArray.length(); i++) {
                                    MySquadInfo squadInfo = new MySquadInfo();
                                    squadInfo.setPlayerId(secondTeamMatchPlayerArray.getJSONObject(i).getString("player_id"));
                                    squadInfo.setPlayerName(secondTeamMatchPlayerArray.getJSONObject(i).getString("player_name"));
                                    squadInfo.setPlayerImage(secondTeamMatchPlayerArray.getJSONObject(i).getString("profile_image"));
                                    mFirstTeamSquads.add(squadInfo);
                                }
                            }
                            if (firstTeamMatchPlayerArray.length() > 0) {
                                for (int i = 0; i < firstTeamMatchPlayerArray.length(); i++) {
                                    SquadInfo squadInfo = new SquadInfo();
                                    squadInfo.setPlayerId(firstTeamMatchPlayerArray.getJSONObject(i).getString("player_id"));
                                    squadInfo.setPlayerName(firstTeamMatchPlayerArray.getJSONObject(i).getString("player_name"));
                                    squadInfo.setPlayerPhoto(firstTeamMatchPlayerArray.getJSONObject(i).getString("profile_image"));
                                    mSecondTeamSquads.add(squadInfo);
                                }
                            }
                        }
                        setUpViewPagerAdapter(firstTeamArray.toString(), secondTeamArray.toString(), tabTitles);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
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
                try {
                    if (t instanceof SocketTimeoutException) {
                        Toast.makeText(SetGameScoreOverViewActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SetGameScoreOverViewActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
        });
    }

    private void setUpViewPagerAdapter(String firstTeamScore, String secondTeamScore, List<String> tabTitles) {
        SetGameScoreViewPagerAdapter viewPagerAdapter = new SetGameScoreViewPagerAdapter(getSupportFragmentManager(), firstTeamScore, secondTeamScore, tabTitles);
        mTeamScoreViewPager.setAdapter(viewPagerAdapter);
        mTeamScoreViewPager.setOffscreenPageLimit(1);
        mTeamsTitleTabLayout.setupWithViewPager(mTeamScoreViewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
            mCollapsingToolbarLayout.setTitle("");
        } else if (verticalOffset == 0) {
            mCollapsingToolbarLayout.setTitle("");
        } else {
            mCollapsingToolbarLayout.setTitle("");
        }
    }

    private class SetGameScoreViewPagerAdapter extends FragmentStatePagerAdapter {
        private List<String> mTabTitles = new ArrayList<>();
        private String mFirstTeamScore;
        private String mSecondTeamScore;

        public SetGameScoreViewPagerAdapter(FragmentManager fm, String firstTeamScore, String secondTeamScore, List<String> tabTitles) {
            super(fm);
            mTabTitles = tabTitles;
            mFirstTeamScore = firstTeamScore;
            mSecondTeamScore = secondTeamScore;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = SetGameTotalScoreOverviewFragment.newInstance(mFirstTeamScore, mSecondTeamScore, mSportType);
                    break;
                case 1:
                    fragment = FirstTeamSquadFragment.newInstance(mFirstTeamSquads);
                    break;
                case 2:
                    fragment = SecondTeamSquadFragment.newInstance(mSecondTeamSquads);
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
