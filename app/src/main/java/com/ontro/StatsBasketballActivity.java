package com.ontro;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ontro.customui.ProfileImageView;
import com.ontro.dto.BasketballScoreResponse;
import com.ontro.dto.BasketballScoreUpdateDTO;
import com.ontro.dto.BasketballSetDTO;
import com.ontro.fragments.BasketballOverviewFragment;
import com.ontro.fragments.BasketballStatsFragment;
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

public class StatsBasketballActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener,AppBarLayout.OnOffsetChangedListener {
    private TabLayout overviewTabLayout;
    private ViewPager mStatisticsViewPager;
    private TextView mMatchDateView, mMatchVenueView, mMatchStatusView;
    private TextView mFirstTeamNameView, mSecondTeamNameView, mFirstTeamTotalScoreView, mSecondTeamTotalScoreView;
    private ImageView mSportImageView, mFirstTeamWonIndicationView,
            mSecondTeamWonIndicationView;
    private ProfileImageView mFirstTeamImageView, mSecondTeamImageView;
    private Dialog mProgressDialog;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout mAppBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats_basketball);
        overridePendingTransition(R.anim.slide_in_right, R.anim.pause);
        Init();
        if (CommonUtils.isNetworkAvailable(StatsBasketballActivity.this)) {
            mProgressDialog.show();
            getMatchDetails();
        } else {
            Toast.makeText(StatsBasketballActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }
        setListener();
    }

    private void Init() {
        overviewTabLayout = (TabLayout)findViewById(R.id.basketball_overview_tablayout);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.basketball_score_overview_appbar_layout);
        mStatisticsViewPager = (ViewPager) findViewById(R.id.basketball_view_pager);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.basketball_score_overview_collapsing_toolbar);
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
        mSportImageView.setImageResource(R.drawable.ic_basketball_white);
        mProgressDialog = new Dialog(StatsBasketballActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setContentView(R.layout.progressdialog_layout);
        mToolbar = (Toolbar) findViewById(R.id.basketball_score_overview_toolbar);
        overviewTabLayout.addOnTabSelectedListener(this);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            mToolbar.setTitle("");
            mToolbar.setTitleTextColor(ContextCompat.getColor(StatsBasketballActivity.this, android.R.color.transparent));
            mToolbar.bringToFront();
        }
    }

    private void setUpViewPagerAdapter(List<BasketballScoreResponse> sc, List<String> titles) {
        try {
            StatsBasketballActivity.OverViewPagerAdapter viewPagerAdapter = new StatsBasketballActivity.OverViewPagerAdapter(this.getSupportFragmentManager(),sc,titles);
            mStatisticsViewPager.setAdapter(viewPagerAdapter);
            overviewTabLayout.setupWithViewPager(mStatisticsViewPager);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void getMatchDetails() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        PreferenceHelper preferenceHelper = new PreferenceHelper(StatsBasketballActivity.this, Constants.APP_NAME, 0);
        String matchId = preferenceHelper.getString("match_id", "");
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
                            if (venueDate.equals("null")) {
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

                        BasketballScoreResponse basketballScoreResponse = new BasketballScoreResponse();
                        JSONArray matchScoreArray = new JSONArray(object.getString("match_score"));
                        final List<BasketballScoreResponse> scoreResponses = new ArrayList<>();
                        try {
                            for (int i = 0; i < matchScoreArray.length(); i++) {
                                basketballScoreResponse = new BasketballScoreResponse();
                                basketballScoreResponse.setTeamId(matchScoreArray.getJSONObject(i).getInt("team_id"));
                                basketballScoreResponse.setTeamName(matchScoreArray.getJSONObject(i).getString("team_name"));
                                basketballScoreResponse.setTeamLogo(matchScoreArray.getJSONObject(i).getString("team_logo"));
                                basketballScoreResponse.setTeamLocation(matchScoreArray.getJSONObject(i).getString("team_location"));
                                List<BasketballSetDTO> quarterScoreUpdates = new ArrayList<>();

                                JSONArray overallScore = new JSONArray(matchScoreArray.getJSONObject(i).getString("quarter_score"));
                                for (int k = 0; k < overallScore.length(); k++) {
                                    BasketballSetDTO basketballSetDTO = new BasketballSetDTO();
                                    basketballSetDTO.setSetnumber(overallScore.getJSONObject(k).getString("set_no"));
                                    basketballSetDTO.setScore(overallScore.getJSONObject(k).getString("score"));
                                    basketballSetDTO.setWon(overallScore.getJSONObject(k).getString("won"));
                                    quarterScoreUpdates.add(basketballSetDTO);
                                }
                                basketballScoreResponse.setQuarterScore(quarterScoreUpdates);

                                List<BasketballScoreUpdateDTO> playerScoreUpdates = new ArrayList<>();
                                List<String> benchplayerScoreUpdates = new ArrayList<>();
                                JSONArray playerScore = new JSONArray(matchScoreArray.getJSONObject(i).getString("player_score"));

                                if (playerScore != null) {
                                    for (int k = 0; k < playerScore.length(); k++) {
                                        String id =  playerScore.getJSONObject(k).getString("player_id");
                                        String name = playerScore.getJSONObject(k).getString("player_name");
                                        String pointOne = playerScore.getJSONObject(k).getString("firstPoint");
                                        String pointTwo = playerScore.getJSONObject(k).getString("secondPoint");
                                        String pointThree = playerScore.getJSONObject(k).getString("thirdPoint");

                                        BasketballScoreUpdateDTO updateDTO = new BasketballScoreUpdateDTO(id,name,pointOne,pointTwo,pointThree,"");
                                        playerScoreUpdates.add(updateDTO);
                                    }
                                    basketballScoreResponse.setPlayerScore(playerScoreUpdates);
                                }

                                JSONArray benchPlayers = new JSONArray(matchScoreArray.getJSONObject(i).getString("bench_players"));
                                if (benchPlayers.length() > 0){
                                    for (int l=0;l<benchPlayers.length();l++){
                                        String name = benchPlayers.getJSONObject(l).getString("player_name");
                                        benchplayerScoreUpdates.add(name);
                                    }
                                    basketballScoreResponse.setBenchPlayer(benchplayerScoreUpdates);
                                }
                                scoreResponses.add(basketballScoreResponse);
                            }

                            mFirstTeamNameView.setText(scoreResponses.get(1).getTeamName());
                            mSecondTeamNameView.setText(scoreResponses.get(0).getTeamName());
                            String firstTeamImage = scoreResponses.get(1).getTeamLogo();
                            if (firstTeamImage != null) {
                                Glide.with(StatsBasketballActivity.this).load(firstTeamImage).placeholder(R.drawable.match_default).dontAnimate().into(mFirstTeamImageView);
                            }
                            String secondTeamImage = scoreResponses.get(0).getTeamLogo();
                            if (secondTeamImage != null) {
                                Glide.with(StatsBasketballActivity.this).load(secondTeamImage).placeholder(R.drawable.match_default).dontAnimate().into(mSecondTeamImageView);
                            }
                            if (winnerId.equals(String.valueOf(scoreResponses.get(0).getTeamId()))) {
                                mFirstTeamWonIndicationView.setVisibility(View.GONE);
                                mSecondTeamWonIndicationView.setVisibility(View.VISIBLE);
                            } else {
                                mFirstTeamWonIndicationView.setVisibility(View.VISIBLE);
                                mSecondTeamWonIndicationView.setVisibility(View.GONE);
                            }

                            // Update total sets
                            UpdateTotalSet(scoreResponses);

                            List<String> tabTitles = new ArrayList<>();
                            tabTitles.add(scoreResponses.get(1).getTeamName());
                            tabTitles.add(scoreResponses.get(0).getTeamName());
                            setUpViewPagerAdapter(scoreResponses, tabTitles);
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
                        Toast.makeText(StatsBasketballActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(StatsBasketballActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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

    private void UpdateTotalSet(List<BasketballScoreResponse> scoreResponseList) {
        List<BasketballSetDTO> team_one_score = scoreResponseList.get(1).getQuarterScore();
        List<BasketballSetDTO> team_two_score = scoreResponseList.get(0).getQuarterScore();
        int team_one_total_set = 0,team_two_total_set = 0,count = 0;

        for(int i=0 ;i<team_one_score.size();i++){
            String won = team_one_score.get(i).getWon();
            if (won.equalsIgnoreCase("1")){
                team_one_total_set += count + 1;
            }
        }

        for(int i=0 ;i<team_two_score.size();i++){
            String won = team_two_score.get(i).getWon();
            if (won.equalsIgnoreCase("1")){
                team_two_total_set += count + 1;
            }
        }

        mFirstTeamTotalScoreView.setText(String.valueOf(team_two_total_set));
        mSecondTeamTotalScoreView.setText(String.valueOf(team_one_total_set));
    }


    public void CloseScoreOverviewActivity(View view) {
        StatsBasketballActivity.this.finish();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mStatisticsViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private void setListener() {
        overviewTabLayout.addOnTabSelectedListener(this);
        mAppBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
            collapsingToolbarLayout.setTitle("");
        } else if (verticalOffset == 0) {
            collapsingToolbarLayout.setTitle("");
        } else {
            collapsingToolbarLayout.setTitle("");
        }
    }

    private class OverViewPagerAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment.SavedState> mSavedState = new ArrayList<Fragment.SavedState>();
        private List<String> mTabTitles = new ArrayList<>();
        private List<BasketballScoreResponse> basketballScoreResponse;
        private FragmentTransaction mCurTransaction;
        private FragmentManager mFragmentManager;
        private Fragment mCurrentPrimaryItem = null;

        OverViewPagerAdapter(FragmentManager supportFragmentManager, List<BasketballScoreResponse> scoreResponse, List<String> tabnames) {
            super(supportFragmentManager);
            mFragmentManager = supportFragmentManager;
            mTabTitles = tabnames;
            mTabTitles.add(0,"OVERVIEW");
            basketballScoreResponse = scoreResponse;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = BasketballOverviewFragment.newInstance(basketballScoreResponse);
                    return fragment;
                case 1:
                    fragment = BasketballStatsFragment.newInstance(basketballScoreResponse.get(1));
                    return fragment;
                case 2:
                    fragment = BasketballStatsFragment.newInstance(basketballScoreResponse.get(0));
                    return fragment;
                default:
                    fragment = null;
                    return fragment;
            }
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
        public Object instantiateItem(ViewGroup container, int position) {
            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction();
            }
            Fragment fragment = getItem(position);
            if (mSavedState.size() > position) {
                Fragment.SavedState fss = mSavedState.get(position);
                if (fss != null) {
                    try {
                        fragment.setInitialSavedState(fss);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            fragment.setMenuVisibility(false);
            mCurTransaction.add(container.getId(), fragment);
            return fragment;
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            if (mCurTransaction != null) {
                mCurTransaction.commitAllowingStateLoss();
                mCurTransaction = null;
                mFragmentManager.executePendingTransactions();
            }
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            Fragment fragment = (Fragment) object;
            if (fragment != mCurrentPrimaryItem) {
                if (mCurrentPrimaryItem != null) {
                    mCurrentPrimaryItem.setMenuVisibility(false);
                }
                if (fragment != null) {
                    fragment.setMenuVisibility(true);
                }
                mCurrentPrimaryItem = fragment;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
