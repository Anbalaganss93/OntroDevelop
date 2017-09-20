package com.ontro;

import android.app.Dialog;
import android.content.Intent;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ontro.customui.ProfileImageView;
import com.ontro.dto.CricketBatsmanScoreModel;
import com.ontro.dto.CricketBowlerScoreModel;
import com.ontro.dto.CricketScoreResponse;
import com.ontro.dto.FootballPlayerScoreUpdate;
import com.ontro.dto.FootballScoreResponse;
import com.ontro.dto.MatchRequestModel;
import com.ontro.fragments.CricketFirstTeamScoreFragment;
import com.ontro.fragments.CricketSecondTeamScoreFragment;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CricketScoreOverviewActivity extends AppCompatActivity implements View.OnClickListener, TabLayout.OnTabSelectedListener {
    private ImageView mBackNavigationView;
    private TabLayout mTeamsTitleTabLayout;
    private ViewPager mTeamScoreViewPager;
    private TextView mMatchDateView, mMatchVenueView, mMatchStatusView;
    private TextView mFirstTeamNameView, mSecondTeamNameView, mFirstTeamBattingInnView, mSecondTeamBattingInnView, mFirstTeamTotalScoreView, mSecondTeamTotalScoreView,
            mFirstTeamOversView, mSecondTeamOversView;
    private ImageView mFirstTeamUpdateIndicationView, mSecondTeamUpdateIndicationView, mFirstTeamWonIndicationView,
            mSecondTeamWonIndicationView;
    private ProfileImageView mFirstTeamImageView, mSecondTeamImageView;
    private List<String> mPlayers = new ArrayList<>();
    private Dialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cricket_score_overview);
        overridePendingTransition(R.anim.slide_in_right, R.anim.pause);
        initView();
        if (CommonUtils.isNetworkAvailable(CricketScoreOverviewActivity.this)) {
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
            Toast.makeText(CricketScoreOverviewActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }
        setListener();
    }

    private void initView() {
        mBackNavigationView = (ImageView) findViewById(R.id.activity_cricket_score_overview_iv_back);
        mTeamsTitleTabLayout = (TabLayout) findViewById(R.id.activity_cricket_score_overview_tl);
        mTeamScoreViewPager = (ViewPager) findViewById(R.id.activity_cricket_score_overview_pager);

        mMatchDateView = (TextView) findViewById(R.id.cricket_score_header_tv_match_date);
        mMatchVenueView = (TextView) findViewById(R.id.cricket_score_header_tv_match_location);
        mMatchStatusView = (TextView) findViewById(R.id.cricket_score_header_tv_team_score_update_status);
        mFirstTeamNameView = (TextView) findViewById(R.id.cricket_score_header_tv_first_team_name);
        mSecondTeamNameView = (TextView) findViewById(R.id.cricket_score_header_tv_second_team_name);
        mFirstTeamBattingInnView = (TextView) findViewById(R.id.cricket_score_header_tv_first_team_batting_innings);
        mSecondTeamBattingInnView = (TextView) findViewById(R.id.cricket_score_header_tv_second_team_batting_innings);
        mFirstTeamTotalScoreView = (TextView) findViewById(R.id.cricket_score_header_tv_first_team_total_score);
        mSecondTeamTotalScoreView = (TextView) findViewById(R.id.cricket_score_header_tv_second_team_total_score);
        mFirstTeamOversView = (TextView) findViewById(R.id.cricket_score_header_tv_first_team_total_overs);
        mSecondTeamOversView = (TextView) findViewById(R.id.cricket_score_header_tv_second_team_total_overs);
        mFirstTeamUpdateIndicationView = (ImageView) findViewById(R.id.cricket_score_header_iv_first_team_score_update_status);
        mSecondTeamUpdateIndicationView = (ImageView) findViewById(R.id.cricket_score_header_iv_second_team_score_update_status);
        mFirstTeamWonIndicationView = (ImageView) findViewById(R.id.cricket_score_header_iv_first_team_won_indicator);
        mSecondTeamWonIndicationView = (ImageView) findViewById(R.id.cricket_score_header_iv_second_team_won_indicator);
        mFirstTeamImageView = (ProfileImageView) findViewById(R.id.cricket_score_header_iv_first_team_logo);
        mSecondTeamImageView = (ProfileImageView) findViewById(R.id.cricket_score_header_iv_second_team_logo);

        mProgressDialog = new Dialog(CricketScoreOverviewActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setContentView(R.layout.progressdialog_layout);

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
            Glide.with(CricketScoreOverviewActivity.this).load(opponentTeamLogo).placeholder(R.drawable.profiledefaultimg).dontAnimate().into(mSecondTeamImageView);
        } else {
            Glide.with(CricketScoreOverviewActivity.this).load(R.drawable.profiledefaultimg).dontAnimate().into(mSecondTeamImageView);
        }
        if (myTeamLogo != null) {
            Glide.with(CricketScoreOverviewActivity.this).load(myTeamLogo).placeholder(R.drawable.profiledefaultimg).dontAnimate().into(mFirstTeamImageView);
        } else {
            Glide.with(CricketScoreOverviewActivity.this).load(R.drawable.profiledefaultimg).dontAnimate().into(mFirstTeamImageView);
        }
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        PreferenceHelper preferenceHelper = new PreferenceHelper(CricketScoreOverviewActivity.this, Constants.APP_NAME, 0);
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
                        String venueName = "", venueBookingFromTime = "", venueBookingToTime = "";
                        if (object.has("venue_name")) {
                            venueName = object.getString("venue_name");
                        }
                        if (object.has("venue_booking_date")) {
                            if (!venueDate.equals("null")) {
                                matchDate = object.getString("venue_booking_date");
                            }
                        }
                        if (object.has("venue_booking_from_time")) {
                            venueBookingFromTime = CommonUtils.convertTimeFormatIntoAmOrPmFormat(object.getString("venue_booking_from_time"));
                        }
                        if (object.has("venue_booking_to_time")) {
                            venueBookingToTime = CommonUtils.convertTimeFormatIntoAmOrPmFormat(object.getString("venue_booking_to_time"));
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
                        final List<CricketScoreResponse> cricketScoreResponses = new ArrayList<CricketScoreResponse>();
                        try {
                            for (int i = 0; i < matchScoreArray.length(); i++) {
                                CricketScoreResponse scoreResponse = new CricketScoreResponse();
                                scoreResponse.setTeamId(matchScoreArray.getJSONObject(i).getInt("team_id"));
                                scoreResponse.setTeamName(matchScoreArray.getJSONObject(i).getString("team_name"));
                                scoreResponse.setTeamLogo(matchScoreArray.getJSONObject(i).getString("team_logo"));
                                scoreResponse.setTeamLocation(matchScoreArray.getJSONObject(i).getString("team_location"));

                                scoreResponse.setScore(matchScoreArray.getJSONObject(i).getInt("score"));
                                scoreResponse.setWickets(matchScoreArray.getJSONObject(i).getInt("wickets"));
                                scoreResponse.setOvers(matchScoreArray.getJSONObject(i).getDouble("overs"));
                                scoreResponse.setExtras(matchScoreArray.getJSONObject(i).getInt("extras"));
                                scoreResponse.setWide(matchScoreArray.getJSONObject(i).getInt("wide"));
                                scoreResponse.setNoBall(matchScoreArray.getJSONObject(i).getInt("no_ball"));
                                scoreResponse.setLegBye(matchScoreArray.getJSONObject(i).getInt("leg_bye"));
                                scoreResponse.setBye(matchScoreArray.getJSONObject(i).getInt("bye"));
                                scoreResponse.setPenalty(matchScoreArray.getJSONObject(i).getInt("penalty"));
                                scoreResponse.setBatInnings(matchScoreArray.getJSONObject(i).getInt("bat_innings"));

                               List<CricketBatsmanScoreModel> batsmanScoreModels = new ArrayList<CricketBatsmanScoreModel>();
                                JSONArray batsmanScore = new JSONArray(matchScoreArray.getJSONObject(i).getString("batting_score"));
                                if (batsmanScore != null) {
                                    for (int k = 0; k < batsmanScore.length(); k++) {
                                        CricketBatsmanScoreModel batsmanScoreModel = new CricketBatsmanScoreModel();
                                        batsmanScoreModel.setPlayerName(batsmanScore.getJSONObject(k).getString("player_name"));
                                        batsmanScoreModel.setRuns(String.valueOf(batsmanScore.getJSONObject(k).getInt("run")));
                                        batsmanScoreModel.setBalls(String.valueOf(batsmanScore.getJSONObject(k).getInt("ball")));
                                        batsmanScoreModel.setFours(String.valueOf(batsmanScore.getJSONObject(k).getInt("four")));
                                        batsmanScoreModel.setSixs(String.valueOf(batsmanScore.getJSONObject(k).getInt("six")));
                                        batsmanScoreModel.setStrikeRate(String.valueOf(batsmanScore.getJSONObject(k).getInt("strike_rate")));
                                        batsmanScoreModels.add(batsmanScoreModel);
                                    }
                                    scoreResponse.setBattingScore(batsmanScoreModels);
                                }

                                List<CricketBowlerScoreModel> bowlerScoreModels = new ArrayList<CricketBowlerScoreModel>();
                                JSONArray bowlerScore = new JSONArray(matchScoreArray.getJSONObject(i).getString("bowling_score"));
                                if (bowlerScore != null) {
                                    for (int k = 0; k < bowlerScore.length(); k++) {
                                        CricketBowlerScoreModel bowlerScoreModel = new CricketBowlerScoreModel();
                                        bowlerScoreModel.setPlayerName(bowlerScore.getJSONObject(k).getString("player_name"));
                                        bowlerScoreModel.setOver(String.valueOf(bowlerScore.getJSONObject(k).getInt("over")));
                                        bowlerScoreModel.setWickets(String.valueOf(bowlerScore.getJSONObject(k).getInt("wicket")));
                                        bowlerScoreModel.setBowlingRun(String.valueOf(bowlerScore.getJSONObject(k).getInt("run")));
                                        bowlerScoreModel.setMaiden(String.valueOf(bowlerScore.getJSONObject(k).getInt("maiden")));
                                        bowlerScoreModel.setEconomyRate(String.valueOf(bowlerScore.getJSONObject(k).getInt("economy_rate")));
                                        bowlerScoreModels.add(bowlerScoreModel);
                                    }
                                    scoreResponse.setBowlingScore(bowlerScoreModels);
                                }
                                cricketScoreResponses.add(scoreResponse);
                            }


                            if (winnerId.equals(myTeamId)) {
                                mFirstTeamWonIndicationView.setVisibility(View.VISIBLE);
                                mSecondTeamWonIndicationView.setVisibility(View.GONE);
                            } else {
                                mFirstTeamWonIndicationView.setVisibility(View.GONE);
                                mSecondTeamWonIndicationView.setVisibility(View.VISIBLE);
                            }
                            CricketScoreResponse myTeamScoreResponse = new CricketScoreResponse();
                            CricketScoreResponse opponentTeamScoreResponse = new CricketScoreResponse();
                            if(myTeamId.equals(String.valueOf(cricketScoreResponses.get(0).getTeamId()))) {
                                mFirstTeamTotalScoreView.setText(cricketScoreResponses.get(0).getScore() + Constants.DefaultText.SLASH + cricketScoreResponses.get(0).getWickets());
                                mSecondTeamTotalScoreView.setText(cricketScoreResponses.get(1).getScore() + Constants.DefaultText.SLASH + cricketScoreResponses.get(1).getWickets());
                                mFirstTeamOversView.setText(cricketScoreResponses.get(0).getOvers() + Constants.DefaultText.OVERS);
                                mSecondTeamOversView.setText(cricketScoreResponses.get(1).getOvers() + Constants.DefaultText.OVERS);
                                String battingInnings = String.valueOf(cricketScoreResponses.get(0).getBatInnings());
                                if(battingInnings.equals(Constants.DefaultText.ONE)) {
                                    mFirstTeamBattingInnView.setText(Constants.DefaultText.FIRST_INNINGS);
                                    mSecondTeamBattingInnView.setText(Constants.DefaultText.SECOND_INNINGS);
                                } else {
                                    mFirstTeamBattingInnView.setText(Constants.DefaultText.SECOND_INNINGS);
                                    mSecondTeamBattingInnView.setText(Constants.DefaultText.FIRST_INNINGS);
                                }
                                myTeamScoreResponse = cricketScoreResponses.get(0);
                                opponentTeamScoreResponse = cricketScoreResponses.get(1);
                            } else {
                                mFirstTeamTotalScoreView.setText(cricketScoreResponses.get(1).getScore() + Constants.DefaultText.SLASH + cricketScoreResponses.get(1).getWickets());
                                mSecondTeamTotalScoreView.setText(cricketScoreResponses.get(0).getScore() + Constants.DefaultText.SLASH + cricketScoreResponses.get(0).getWickets());
                                mFirstTeamOversView.setText(cricketScoreResponses.get(1).getOvers() + Constants.DefaultText.OVERS);
                                mSecondTeamOversView.setText(cricketScoreResponses.get(0).getOvers() + Constants.DefaultText.OVERS);
                                String battingInnings = String.valueOf(cricketScoreResponses.get(1).getBatInnings());
                                if(battingInnings.equals(Constants.DefaultText.ONE)) {
                                    mSecondTeamBattingInnView.setText(Constants.DefaultText.FIRST_INNINGS);
                                    mFirstTeamBattingInnView.setText(Constants.DefaultText.SECOND_INNINGS);
                                } else {
                                    mFirstTeamBattingInnView.setText(Constants.DefaultText.FIRST_INNINGS);
                                    mSecondTeamBattingInnView.setText(Constants.DefaultText.SECOND_INNINGS);
                                }
                                myTeamScoreResponse = cricketScoreResponses.get(1);
                                opponentTeamScoreResponse = cricketScoreResponses.get(0);
                            }
                            List<String> tabTitles = new ArrayList<String>();
                            tabTitles.add(myTeamName);
                            tabTitles.add(opponentTeamName);
                            setUpViewPagerAdapter(myTeamScoreResponse, opponentTeamScoreResponse, tabTitles);
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

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //  Log error here since request failed
                try {
                    if (t instanceof SocketTimeoutException) {
                        Toast.makeText(CricketScoreOverviewActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CricketScoreOverviewActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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

    private void setUpViewPagerAdapter(CricketScoreResponse myTeamScoreResponse, CricketScoreResponse opponentTeamScoreResponse, List<String> tabTitles) {
        CricketScoreViewPagerAdapter viewPagerAdapter = new CricketScoreViewPagerAdapter(getSupportFragmentManager(), myTeamScoreResponse, opponentTeamScoreResponse, tabTitles);
        mTeamScoreViewPager.setAdapter(viewPagerAdapter);
        mTeamScoreViewPager.setOffscreenPageLimit(1);
        mTeamsTitleTabLayout.setupWithViewPager(mTeamScoreViewPager);
    }

    private void setListener() {
        mBackNavigationView.setOnClickListener(this);
        mTeamsTitleTabLayout.addOnTabSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_cricket_score_overview_iv_back:
                finish();
                overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
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
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
    }

    private class CricketScoreViewPagerAdapter extends FragmentStatePagerAdapter {
        private CricketScoreResponse mMyTeamScoreResponse;
        private CricketScoreResponse mOpponentTeamScoreResponse;
        private List<String> mTabTitles = new ArrayList<>();

        CricketScoreViewPagerAdapter(FragmentManager fm, CricketScoreResponse myTeamScoreResponse, CricketScoreResponse opponentScoreResponse, List<String> tabTitles) {
            super(fm);
            mMyTeamScoreResponse = myTeamScoreResponse;
            mOpponentTeamScoreResponse = opponentScoreResponse;
            mTabTitles = tabTitles;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            switch (position) {
                case 0:
                    fragment = CricketFirstTeamScoreFragment.newInstance(mMyTeamScoreResponse);
                    break;
                case 1:
                    fragment = CricketSecondTeamScoreFragment.newInstance(mOpponentTeamScoreResponse);
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