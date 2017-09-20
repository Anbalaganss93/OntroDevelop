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
import com.ontro.dto.FootballPlayerScoreUpdate;
import com.ontro.dto.FootballScoreResponse;
import com.ontro.dto.MatchRequestModel;
import com.ontro.fragments.FootballFirstTeamScoreFragment;
import com.ontro.fragments.FootballSecondTeamScoreFragment;
import com.ontro.rest.ApiClient;
import com.ontro.rest.ApiInterface;
import com.ontro.utils.CommonUtils;
import com.ontro.utils.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;
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

public class FootballScoreOverviewActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, AppBarLayout.OnOffsetChangedListener {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_football_score_overview);
        overridePendingTransition(R.anim.slide_in_right, R.anim.pause);
        initView();
        if (CommonUtils.isNetworkAvailable(FootballScoreOverviewActivity.this)) {
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
            Toast.makeText(FootballScoreOverviewActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }
        setListener();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.activity_football_score_overview_toolbar);
        mTeamsTitleTabLayout = (TabLayout) findViewById(R.id.activity_football_score_overview_tl);
        mTeamScoreViewPager = (ViewPager) findViewById(R.id.activity_football_score_overview_pager);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.activity_football_score_overview_appbar_layout);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.activity_football_score_overview_collapsing_toolbar);

        mMatchDateView = (TextView) findViewById(R.id.game_header_date);
        mMatchVenueView = (TextView) findViewById(R.id.game_header_location);
        mFirstTeamNameView = (TextView) findViewById(R.id.game_header_team_one_name);
        mSecondTeamNameView = (TextView) findViewById(R.id.game_header_team_two_name);
        mFirstTeamTotalScoreView = (TextView) findViewById(R.id.game_header_team_one_score);
        mSecondTeamTotalScoreView = (TextView) findViewById(R.id.game_header_team_two_score);
        mFirstTeamWonIndicationView = (ImageView) findViewById(R.id.winner_teamone);
        mSecondTeamWonIndicationView = (ImageView) findViewById(R.id.winner_teamtwo);
        mFirstTeamImageView = (ProfileImageView) findViewById(R.id.game_header_team);
        mSecondTeamImageView = (ProfileImageView) findViewById(R.id.game_header_team2);
        mMatchStatusView = (TextView) findViewById(R.id.game_header_team_score_update_status);
        mSportImageView = (ImageView) findViewById(R.id.game_header_sportimage);
        mSportImageView.setImageResource(R.drawable.ic_football_white);
        mProgressDialog = new Dialog(FootballScoreOverviewActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setContentView(R.layout.progressdialog_layout);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            mToolbar.setTitle("");
            mToolbar.setTitleTextColor(ContextCompat.getColor(FootballScoreOverviewActivity.this, android.R.color.transparent));
            mToolbar.bringToFront();
        }
    }

    private void getMatchDetails(MatchRequestModel matchRequestModel) {
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
            Glide.with(FootballScoreOverviewActivity.this).load(opponentTeamLogo).placeholder(R.drawable.profiledefaultimg).dontAnimate().into(mSecondTeamImageView);
        } else {
            Glide.with(FootballScoreOverviewActivity.this).load(R.drawable.profiledefaultimg).dontAnimate().into(mSecondTeamImageView);
        }
        if (myTeamLogo != null) {
            Glide.with(FootballScoreOverviewActivity.this).load(myTeamLogo).placeholder(R.drawable.profiledefaultimg).dontAnimate().into(mFirstTeamImageView);
        } else {
            Glide.with(FootballScoreOverviewActivity.this).load(R.drawable.profiledefaultimg).dontAnimate().into(mFirstTeamImageView);
        }
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        PreferenceHelper preferenceHelper = new PreferenceHelper(FootballScoreOverviewActivity.this, Constants.APP_NAME, 0);
        String auth_token = "Bearer " + preferenceHelper.getString("user_token", "");
        Call<ResponseBody> call = apiInterface.ScoreCompleted(auth_token, matchId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        JSONObject json = new JSONObject(data);
                        JSONObject object = new JSONObject(json.getString("data"));
                        String Matchtype = object.getString("match_type");
                        String sportType = object.getString("sport_type");
                        final String winnerId = object.getString("winner_team_id ");
                        String location = object.getString("match_location");
                        String matchDate = object.getString("match_date");
                        String venueDate = object.getString("venue_booking_date");
                        String statusOfMatch = object.getString("status_of_match");
                        String mVenueAddress = object.getString("venue_address");
                        String venueName = "", venueBookingDate = "", venueBookingFromTime = "", venueBookingToTime = "";
                        if (object.has("venue_name")) {
                            venueName = object.getString("venue_name");
                        }
                        if (object.has("venue_booking_date")) {
                            if (!venueDate.equals("null")) {
                                venueBookingDate = object.getString("venue_booking_date");
                            } else {
                                venueBookingDate = matchDate;
                            }
                        }
                        if (object.has("venue_booking_from_time")) {
                            venueBookingFromTime = getTimeFormat(object.getString("venue_booking_from_time"));
                        }
                        if (object.has("venue_booking_to_time")) {
                            venueBookingToTime = getTimeFormat(object.getString("venue_booking_to_time"));
                        }

                        mMatchDateView.setText(CommonUtils.convertDateFormat(matchDate, Constants.DefaultText.YEAR_MONTH_DATE, Constants.DefaultText.DATE_MONTH_LETTER));
                        mMatchStatusView.setText(statusOfMatch);

                        if (mVenueAddress.length() != 0 && !mVenueAddress.equals("null")) {
                            mMatchVenueView.setText(mVenueAddress);
                        } else if (!venueName.equals("") && !venueName.equals("null")) {
                            mMatchVenueView.setText(venueName + " ," + location);
                        } else {
                            mMatchVenueView.setText(location);
                        }

                        JSONArray matchScoreArray = new JSONArray(object.getString("match_score"));
                        final List<FootballScoreResponse> footballScoreResponses = new ArrayList<FootballScoreResponse>();
                        try {
                            for (int i = 0; i < matchScoreArray.length(); i++) {
                                FootballScoreResponse scoreResponse = new FootballScoreResponse();
                                scoreResponse.setTeamId(matchScoreArray.getJSONObject(i).getInt("team_id"));
                                scoreResponse.setTeamName(matchScoreArray.getJSONObject(i).getString("team_name"));
                                scoreResponse.setTeamLogo(matchScoreArray.getJSONObject(i).getString("team_logo"));
                                scoreResponse.setTeamLocation(matchScoreArray.getJSONObject(i).getString("team_location"));
                                scoreResponse.setTotalGoal(matchScoreArray.getJSONObject(i).getInt("total_goal"));
                                List<FootballPlayerScoreUpdate> playerScoreUpdates = new ArrayList<FootballPlayerScoreUpdate>();
                                JSONArray playerScore = new JSONArray(matchScoreArray.getJSONObject(i).getString("player_score"));
                                if (playerScore != null) {
                                    for (int k = 0; k < playerScore.length(); k++) {
                                        FootballPlayerScoreUpdate footballPlayerScoreUpdate = new FootballPlayerScoreUpdate();
                                        footballPlayerScoreUpdate.setPlayerName(playerScore.getJSONObject(k).getString("player_name"));
                                        footballPlayerScoreUpdate.setPlayerGoals(String.valueOf(playerScore.getJSONObject(k).getInt("no_of_goal")));
                                        footballPlayerScoreUpdate.setPlayerAssists(String.valueOf(playerScore.getJSONObject(k).getInt("assists")));
                                        footballPlayerScoreUpdate.setIsGolfKeeper(String.valueOf(playerScore.getJSONObject(k).getString("goal_keeper")));
                                        playerScoreUpdates.add(footballPlayerScoreUpdate);
                                    }
                                    scoreResponse.setPlayerScore(playerScoreUpdates);
                                }
                                footballScoreResponses.add(scoreResponse);
                            }

                            if (winnerId.equals(myTeamId)) {
                                mFirstTeamWonIndicationView.setVisibility(View.VISIBLE);
                                mSecondTeamWonIndicationView.setVisibility(View.GONE);
                            } else {
                                mFirstTeamWonIndicationView.setVisibility(View.GONE);
                                mSecondTeamWonIndicationView.setVisibility(View.VISIBLE);
                            }
                            FootballScoreResponse myTeamFootballResponse = new FootballScoreResponse();
                            FootballScoreResponse opponentTeamFootballResponse = new FootballScoreResponse();
                            if(myTeamId.equals(String.valueOf(footballScoreResponses.get(0).getTeamId()))) {
                                mFirstTeamTotalScoreView.setText(String.valueOf(footballScoreResponses.get(0).getTotalGoal()));
                                mSecondTeamTotalScoreView.setText(String.valueOf(footballScoreResponses.get(1).getTotalGoal()));
                                myTeamFootballResponse = footballScoreResponses.get(0);
                                opponentTeamFootballResponse = footballScoreResponses.get(1);
                            } else {
                                mFirstTeamTotalScoreView.setText(String.valueOf(footballScoreResponses.get(1).getTotalGoal()));
                                mSecondTeamTotalScoreView.setText(String.valueOf(footballScoreResponses.get(0).getTotalGoal()));
                                myTeamFootballResponse = footballScoreResponses.get(1);
                                opponentTeamFootballResponse = footballScoreResponses.get(0);
                            }
                            List<String> tabTitles = new ArrayList<String>();
                            tabTitles.add(myTeamName);
                            tabTitles.add(opponentTeamName);
                            setUpViewPagerAdapter(myTeamFootballResponse, opponentTeamFootballResponse, tabTitles);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                        Toast.makeText(FootballScoreOverviewActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(FootballScoreOverviewActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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

    private void setUpViewPagerAdapter(FootballScoreResponse myTeamFootballResponse, FootballScoreResponse opponentTeamFootballResponse, List<String> tabTitles) {
        FootballScoreViewPagerAdapter viewPagerAdapter = new FootballScoreViewPagerAdapter(getSupportFragmentManager(), myTeamFootballResponse, opponentTeamFootballResponse, tabTitles);
        mTeamScoreViewPager.setAdapter(viewPagerAdapter);
        mTeamScoreViewPager.setOffscreenPageLimit(1);
        mTeamsTitleTabLayout.setupWithViewPager(mTeamScoreViewPager);
    }

    private void setListener() {
        mTeamsTitleTabLayout.addOnTabSelectedListener(this);
        mAppBarLayout.addOnOffsetChangedListener(this);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
    }

    private class FootballScoreViewPagerAdapter extends FragmentStatePagerAdapter {
        private FootballScoreResponse mMyFootballScoreResponses;
        private FootballScoreResponse mOpponentFootballScoreResponse;
        private List<String> mTabTitles = new ArrayList<>();

        public FootballScoreViewPagerAdapter(FragmentManager fm, FootballScoreResponse myTeamFootballScoreResponses,
                                             FootballScoreResponse opponentTeamFootballScoreResponses, List<String> tabTitles) {
            super(fm);
            mMyFootballScoreResponses = myTeamFootballScoreResponses;
            mOpponentFootballScoreResponse = opponentTeamFootballScoreResponses;
            mTabTitles = tabTitles;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = FootballFirstTeamScoreFragment.newInstance(mMyFootballScoreResponses);
                    break;
                case 1:
                    fragment = FootballSecondTeamScoreFragment.newInstance(mOpponentFootballScoreResponse);
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
