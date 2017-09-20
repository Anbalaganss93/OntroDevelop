package com.ontro;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ontro.adapters.CricketBatsmenScoreUpdateAdapter;
import com.ontro.adapters.CricketBowlerScoreUpdateAdapter;
import com.ontro.adapters.CricketExtrasScoreUpdateAdapter;
import com.ontro.adapters.CricketTeamScoreUpdateAdapter;
import com.ontro.customui.ProfileImageView;
import com.ontro.dto.CricketBatsmanScoreModel;
import com.ontro.dto.CricketBowlerScoreModel;
import com.ontro.dto.CricketExtrasScoreModel;
import com.ontro.dto.CricketExtrasScoreUpdate;
import com.ontro.dto.CricketScoreUpdateRequest;
import com.ontro.dto.CricketTeamScoreModel;
import com.ontro.dto.CricketTeamScoreUpdateModel;
import com.ontro.dto.MySquadInfo;
import com.ontro.fragments.MatchFlagFragment;
import com.ontro.rest.ApiClient;
import com.ontro.rest.ApiInterface;
import com.ontro.utils.CommonUtils;
import com.ontro.utils.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CricketScoreUpdateActivity extends AppCompatActivity implements View.OnClickListener, CricketExtrasScoreUpdateAdapter.ScoreExtrasListener,
        AdapterView.OnItemSelectedListener, CricketTeamScoreUpdateAdapter.CricketTeamScoreUpdateListener {
    private ImageView mBackNavigationView, mBattingPageIndicator, mBowlingPageIndicator, mExtrasPageIndicator;
    private TextView mBattingScoreUpdateView, mBowlingScoreUpdateView, mExtrasScoreUpdateView,
            mTotalScoreUpdateView, mScoreUpdateSkipView;
    private RecyclerView mBatsmanScoreView, mBowlerScoreView, mExtrasScoreView, mTeamScoreView;
    private Button mScoreUpdateNextButton, mScoreUpdateSubmitButton;
    private CricketBatsmenScoreUpdateAdapter mCricketBatsmenScoreUpdateAdapter;
    private CricketBowlerScoreUpdateAdapter mCricketBowlerScoreUpdateAdapter;
    private CricketExtrasScoreUpdateAdapter mCricketExtrasScoreUpdateAdapter;
    private CricketTeamScoreUpdateAdapter mCricketTeamScoreUpdateAdapter;
    private CardView mPlayerScoreCardView, mPlayerExtrasCardView, mTeamScoreCardView;
    private TextView mPlayerRunOrOverView, mPlayerBallOrMaidenView, mPlayerFoursOrRunView, mPlayerSixesOrWicketView,
            mPlayerExtrasView, mPlayerExtrasValueView;
    private TextView mMatchDateView, mMatchVenueView, mMatchStatusView;
    private TextView mFirstTeamNameView, mSecondTeamNameView, mFirstTeamBattingInnView, mSecondTeamBattingInnView, mFirstTeamTotalScoreView, mSecondTeamTotalScoreView,
            mFirstTeamOversView, mSecondTeamOversView;
    private ImageView mFirstTeamUpdateIndicationView, mSecondTeamUpdateIndicationView, mFirstTeamWonIndicationView,
            mSecondTeamWonIndicationView, mMatchFlagImageView;
    private ProfileImageView mFirstTeamImageView, mSecondTeamImageView;
    private AppCompatSpinner mBattingInningsSpinner, mBattingInningsTeamSpinner;
    private LinearLayout mCirclePageIndicator;
    private int tabIndex = 0;
    private List<CricketBatsmanScoreModel> mCricketBatsmanScoreModels = new ArrayList<>();
    private List<CricketBowlerScoreModel> mCricketBowlerScoreModels = new ArrayList<>();
    private List<CricketExtrasScoreUpdate> mCricketExtrasScoreUpdates = new ArrayList<>();
    private List<CricketExtrasScoreModel> mCricketExtrasScoreModels = new ArrayList<>();
    private List<CricketTeamScoreModel> mCricketTeamScoreModels = new ArrayList<>();
    private JSONArray mExtrasScoreArray = new JSONArray();
    private JSONArray mBatsmenScoreArray = new JSONArray();
    private JSONArray mBowlersScoreArray = new JSONArray();
    private String mBatInnings = Constants.DefaultText.FIRST_INNINGS;
    private Dialog mProgressDialog;
    private PreferenceHelper mPreferenceHelper;
    private ApiInterface mApiInterface;
    private String mMyTeamId, mOpponentTeamId, mMatchId, mSportId, mAuthToken, mMyTeamScoreUpdateStatus;
    List<CricketTeamScoreUpdateModel> mUpdatedTeamScoreModels = new ArrayList<>();
    List<CricketBatsmanScoreModel> mBatsmanScoreUpdatedModels = new ArrayList<>();
    List<CricketBowlerScoreModel> mBowlerScoreUpdatedModels = new ArrayList<>();
    List<CricketExtrasScoreModel> mExtrasScoreUpdatedModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cricket_score_update);
        initView();
        setListener();
        getPlayerList();
    }

    private void initView() {
        mBackNavigationView = (ImageView) findViewById(R.id.activity_cricket_score_update_iv_back);
        mBattingScoreUpdateView = (TextView) findViewById(R.id.activity_cricket_score_update_tv_batting);
        mBowlingScoreUpdateView = (TextView) findViewById(R.id.activity_cricket_score_update_tv_bowling);
        mExtrasScoreUpdateView = (TextView) findViewById(R.id.activity_cricket_score_update_tv_extras);
        mTotalScoreUpdateView = (TextView) findViewById(R.id.activity_cricket_score_update_tv_total_score);
        mBatsmanScoreView = (RecyclerView) findViewById(R.id.activity_cricket_score_update_rv_batsman_score);
        mBowlerScoreView = (RecyclerView) findViewById(R.id.activity_cricket_score_update_rv_bowler_score);
        mExtrasScoreView = (RecyclerView) findViewById(R.id.activity_cricket_score_update_rv_extras);
        mTeamScoreView = (RecyclerView) findViewById(R.id.activity_cricket_score_update_rv_team_score);
        mScoreUpdateSkipView = (TextView) findViewById(R.id.activity_cricket_score_update_tv_skip);
        mScoreUpdateNextButton = (Button) findViewById(R.id.activity_cricket_score_update_btn_next);
        mScoreUpdateSubmitButton = (Button) findViewById(R.id.activity_cricket_score_update_btn_submit);
        mPlayerScoreCardView = (CardView) findViewById(R.id.activity_cricket_score_update_cv_player_bat_or_bowl);
        mPlayerExtrasCardView = (CardView) findViewById(R.id.activity_cricket_score_update_cv_extras);
        mTeamScoreCardView = (CardView) findViewById(R.id.activity_cricket_score_update_cv_team_score);
        mPlayerRunOrOverView = (TextView) findViewById(R.id.activity_cricket_score_update_tv_run_or_over);
        mPlayerBallOrMaidenView = (TextView) findViewById(R.id.activity_cricket_score_update_tv_ball_or_maiden);
        mPlayerFoursOrRunView = (TextView) findViewById(R.id.activity_cricket_score_update_tv_fours_or_given_run);
        mPlayerSixesOrWicketView = (TextView) findViewById(R.id.activity_cricket_score_update_tv_sixes_or_maiden);
        mBattingInningsSpinner = (AppCompatSpinner) findViewById(R.id.activity_cricket_score_update_sp_batting_innings);
        mBattingInningsTeamSpinner = (AppCompatSpinner) findViewById(R.id.activity_cricket_score_update_sp_team_batting_innings);
        mPlayerExtrasView = (TextView) findViewById(R.id.activity_cricket_score_update_tv_player_extras);
        mPlayerExtrasValueView = (TextView) findViewById(R.id.activity_cricket_score_update_et_extras);
        mCirclePageIndicator = (LinearLayout) findViewById(R.id.activity_cricket_score_update_page_status);
        mBattingPageIndicator = (ImageView) findViewById(R.id.activity_cricket_score_update_iv_batting_page_indicator);
        mBowlingPageIndicator = (ImageView) findViewById(R.id.activity_cricket_score_update_iv_bowling_page_indicator);
        mExtrasPageIndicator = (ImageView) findViewById(R.id.activity_cricket_score_update_iv_extras_page_indicator);
        mMatchFlagImageView = (ImageView) findViewById(R.id.activity_cricket_score_update_iv_flag_match);

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

        mProgressDialog = new Dialog(CricketScoreUpdateActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setContentView(R.layout.progressdialog_layout);

        mFirstTeamBattingInnView.setVisibility(View.GONE);
        mSecondTeamBattingInnView.setVisibility(View.GONE);
        mSecondTeamBattingInnView.setText(Constants.DefaultText.ZERO);
        mFirstTeamTotalScoreView.setText(Constants.DefaultText.ZERO + Constants.DefaultText.SLASH + Constants.DefaultText.ZERO);
        mSecondTeamTotalScoreView.setText(Constants.DefaultText.ZERO + Constants.DefaultText.SLASH + Constants.DefaultText.ZERO);
        mFirstTeamOversView.setText(Constants.DefaultText.ZERO + Constants.DefaultText.OVERS);
        mSecondTeamOversView.setText(Constants.DefaultText.ZERO + Constants.DefaultText.OVERS);

        mBattingScoreUpdateView.setTextColor(ContextCompat.getColor(this, R.color.dark_red));
        setImportantAsterisk(getResources().getString(R.string.run), mPlayerRunOrOverView, ContextCompat.getColor(this, R.color.color_cement));
        setImportantAsterisk(getResources().getString(R.string.extras), mPlayerExtrasView, ContextCompat.getColor(this, android.R.color.white));
        mBattingPageIndicator.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_cricket_filled_page));
        List<String> battingInnings = Arrays.asList(getResources().getStringArray(R.array.batting_innings));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, battingInnings) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (view instanceof TextView) {
                    ((TextView) view).setTextColor(ContextCompat.getColor(CricketScoreUpdateActivity.this, R.color.light_yellow));
                }
                return view;
            }
        };
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBattingInningsSpinner.setAdapter(arrayAdapter);
        mBattingInningsTeamSpinner.setAdapter(arrayAdapter);

        mPreferenceHelper = new PreferenceHelper(CricketScoreUpdateActivity.this, Constants.APP_NAME, 0);
        mAuthToken = "Bearer " + mPreferenceHelper.getString("user_token", "");
        mSportId = mPreferenceHelper.getString("teamsport", "");
        mApiInterface = ApiClient.getClient().create(ApiInterface.class);
    }

    private void setImportantAsterisk(String text, TextView textView, int color) {
        String important = "*";
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(text);
        int start = builder.length();
        builder.append(important);
        int end = builder.length();
        builder.setSpan(new ForegroundColorSpan(color), start, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(builder);
    }

    private void setListener() {
        mBackNavigationView.setOnClickListener(this);
        mBattingScoreUpdateView.setOnClickListener(this);
        mBowlingScoreUpdateView.setOnClickListener(this);
        mExtrasScoreUpdateView.setOnClickListener(this);
        mTotalScoreUpdateView.setOnClickListener(this);
        mScoreUpdateSkipView.setOnClickListener(this);
        mScoreUpdateNextButton.setOnClickListener(this);
        mScoreUpdateSubmitButton.setOnClickListener(this);
        mMatchFlagImageView.setOnClickListener(this);
        mBattingInningsSpinner.setOnItemSelectedListener(this);
        mBattingInningsTeamSpinner.setOnItemSelectedListener(this);
    }

    private void getPlayerList() {
        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            mMatchDateView.setText(bundle.getString(Constants.BundleKeys.MATCH_DATE));
            mMatchVenueView.setText(bundle.getString(Constants.BundleKeys.MATCH_VENUE));
            mFirstTeamNameView.setText(bundle.getString(Constants.BundleKeys.MY_TEAM_NAME));
            mSecondTeamNameView.setText(bundle.getString(Constants.BundleKeys.OPPONENT_TEAM_NAME));
            mMatchStatusView.setText(bundle.getString(Constants.BundleKeys.SCORE_UPDATE_STATUS));
            mOpponentTeamId = bundle.getString(Constants.BundleKeys.OPPONENT_TEAM_ID);
            mMyTeamId = bundle.getString(Constants.BundleKeys.TEAM_ID);
            mMatchId = bundle.getString(Constants.BundleKeys.MATCH_ID);
            mMyTeamScoreUpdateStatus = bundle.getString(Constants.BundleKeys.LOGIN_USER_SCORE_STATUS);
            String opponentTeamScoreUpdateStatus = bundle.getString(Constants.BundleKeys.OPPONENT_USER_SCORE_STATUS);
            switch (opponentTeamScoreUpdateStatus) {
                case Constants.DefaultText.ZERO:
                    mSecondTeamUpdateIndicationView.setVisibility(View.GONE);
                    break;
                case Constants.DefaultText.ONE:
                case Constants.DefaultText.TWO:
                    mSecondTeamUpdateIndicationView.setVisibility(View.VISIBLE);
                    break;
                case Constants.DefaultText.THREE:
                    mSecondTeamUpdateIndicationView.setVisibility(View.GONE);
                    break;
            }
            switch (mMyTeamScoreUpdateStatus) {
                case Constants.DefaultText.ZERO:
                    mFirstTeamUpdateIndicationView.setVisibility(View.GONE);
                    break;
                case Constants.DefaultText.ONE:
                case Constants.DefaultText.TWO:
                    mFirstTeamUpdateIndicationView.setVisibility(View.VISIBLE);
                    if (CommonUtils.isNetworkAvailable(CricketScoreUpdateActivity.this)) {
                        mProgressDialog.show();
                        getMyTeamScore(mMyTeamId);
                    } else {
                        Toast.makeText(CricketScoreUpdateActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.DefaultText.THREE:
                    mFirstTeamUpdateIndicationView.setVisibility(View.GONE);
                    break;
            }
            String opponentTeamImage = bundle.getString(Constants.BundleKeys.OPPONENT_TEAM_IMAGE);
            if (opponentTeamImage != null) {
                Glide.with(CricketScoreUpdateActivity.this).load(opponentTeamImage).placeholder(R.drawable.match_default).dontAnimate().into(mSecondTeamImageView);
            }
            String myTeamImage = bundle.getString(Constants.BundleKeys.MY_TEAM_IMAGE);
            if (myTeamImage != null) {
                Glide.with(CricketScoreUpdateActivity.this).load(myTeamImage).placeholder(R.drawable.match_default).dontAnimate().into(mFirstTeamImageView);
            }
            ArrayList<MySquadInfo> mySquadPlayers = (ArrayList<MySquadInfo>) bundle.getSerializable(Constants.BundleKeys.MY_TEAM_SQUAD);
            for (MySquadInfo squadInfo : mySquadPlayers) {
                CricketBatsmanScoreModel batsmanScoreModel = new CricketBatsmanScoreModel();
                batsmanScoreModel.setPlayerId(squadInfo.getPlayerId());
                batsmanScoreModel.setPlayerName(squadInfo.getPlayerName());
                batsmanScoreModel.setRuns(Constants.DefaultText.ZERO);
                batsmanScoreModel.setBalls(Constants.DefaultText.ZERO);
                batsmanScoreModel.setFours(Constants.DefaultText.ZERO);
                batsmanScoreModel.setSixs(Constants.DefaultText.ZERO);
                mCricketBatsmanScoreModels.add(batsmanScoreModel);
            }
            for (MySquadInfo squadInfo : mySquadPlayers) {
                CricketBowlerScoreModel bowlerScoreModel = new CricketBowlerScoreModel();
                bowlerScoreModel.setPlayerId(squadInfo.getPlayerId());
                bowlerScoreModel.setPlayerName(squadInfo.getPlayerName());
                bowlerScoreModel.setOver(Constants.DefaultText.ZERO);
                bowlerScoreModel.setMaiden(Constants.DefaultText.ZERO);
                bowlerScoreModel.setBowlingRun(Constants.DefaultText.ZERO);
                bowlerScoreModel.setWickets(Constants.DefaultText.ZERO);
                mCricketBowlerScoreModels.add(bowlerScoreModel);
            }
            List<String> extras = Arrays.asList(getResources().getStringArray(R.array.cricket_extras));
            for (int i = 0; i < extras.size(); i++) {
                CricketExtrasScoreUpdate cricketExtrasScoreUpdate = new CricketExtrasScoreUpdate();
                cricketExtrasScoreUpdate.setExtraName(extras.get(i));
                cricketExtrasScoreUpdate.setExtraValue(Constants.DefaultText.ZERO);
                mCricketExtrasScoreUpdates.add(cricketExtrasScoreUpdate);
            }
            if (mUpdatedTeamScoreModels.size() > 0) {
                for (int i = 0; i < mUpdatedTeamScoreModels.size(); i++) {
                    if (String.valueOf(mUpdatedTeamScoreModels.get(i).getTeamId()).equals(mMyTeamId)) {
                        if (mBatsmanScoreUpdatedModels.size() > 0) {
                            mCricketBatsmanScoreModels = mBatsmanScoreUpdatedModels;
                            mBatsmanScoreView.setEnabled(false);
                        } else {
                            mBatsmanScoreView.setEnabled(true);
                        }
                    }
                }
            }

            mCricketBatsmenScoreUpdateAdapter = new CricketBatsmenScoreUpdateAdapter(CricketScoreUpdateActivity.this, mMyTeamScoreUpdateStatus);
            mBatsmanScoreView.setAdapter(mCricketBatsmenScoreUpdateAdapter);
            mCricketBatsmenScoreUpdateAdapter.add(mCricketBatsmanScoreModels);
        }
    }

    private void getMyTeamScore(final String teamId) {
        Call<ResponseBody> call = mApiInterface.OpponentScoreView(mAuthToken, mMatchId, teamId, mSportId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        try {
                            String data = response.body().string();
                            JSONObject json = new JSONObject(data);
                            JSONObject dataJson = new JSONObject(json.getString("data"));
                            JSONArray teamScoreArray = new JSONArray(dataJson.getString("team_score"));
                            if (teamScoreArray != null) {
                                for (int i = 0; i < teamScoreArray.length(); i++) {
                                    CricketTeamScoreUpdateModel teamScoreUpdateModel = new CricketTeamScoreUpdateModel();
                                    teamScoreUpdateModel.setTeamId(teamScoreArray.getJSONObject(i).getInt("team_id"));
                                    teamScoreUpdateModel.setScore(teamScoreArray.getJSONObject(i).getInt("score"));
                                    teamScoreUpdateModel.setWickets(teamScoreArray.getJSONObject(i).getInt("wickets"));
                                    teamScoreUpdateModel.setOvers(teamScoreArray.getJSONObject(i).getInt("overs"));
                                    teamScoreUpdateModel.setBatInnings(teamScoreArray.getJSONObject(i).getInt("bat_innings"));
                                    teamScoreUpdateModel.setExtras(teamScoreArray.getJSONObject(i).getInt("extras"));
                                    teamScoreUpdateModel.setWide(teamScoreArray.getJSONObject(i).getInt("wide"));
                                    teamScoreUpdateModel.setNoBall(teamScoreArray.getJSONObject(i).getInt("no_ball"));
                                    teamScoreUpdateModel.setLegBye(teamScoreArray.getJSONObject(i).getInt("leg_bye"));
                                    teamScoreUpdateModel.setBye(teamScoreArray.getJSONObject(i).getInt("bye"));
                                    teamScoreUpdateModel.setPenalty(teamScoreArray.getJSONObject(i).getInt("penalty"));
                                    mUpdatedTeamScoreModels.add(teamScoreUpdateModel);
                                }
                            }
                            JSONObject playerScoreObject = new JSONObject(dataJson.getString("player_score"));
                            JSONArray batsmanScore = new JSONArray(playerScoreObject.getString("batting_score"));
                            if (batsmanScore != null) {
                                for (int k = 0; k < batsmanScore.length(); k++) {
                                    CricketBatsmanScoreModel batsmanScoreModel = new CricketBatsmanScoreModel();
                                    batsmanScoreModel.setPlayerName(batsmanScore.getJSONObject(k).getString("player_name"));
                                    batsmanScoreModel.setRuns(String.valueOf(batsmanScore.getJSONObject(k).getInt("run")));
                                    batsmanScoreModel.setBalls(String.valueOf(batsmanScore.getJSONObject(k).getInt("ball")));
                                    batsmanScoreModel.setFours(String.valueOf(batsmanScore.getJSONObject(k).getInt("four")));
                                    batsmanScoreModel.setSixs(String.valueOf(batsmanScore.getJSONObject(k).getInt("six")));
                                    batsmanScoreModel.setStrikeRate(String.valueOf(batsmanScore.getJSONObject(k).getInt("strike_rate")));
                                    mBatsmanScoreUpdatedModels.add(batsmanScoreModel);
                                }
                            }
                            JSONArray bowlerScore = new JSONArray(playerScoreObject.getString("bowling_score"));
                            if (bowlerScore != null) {
                                for (int k = 0; k < bowlerScore.length(); k++) {
                                    CricketBowlerScoreModel bowlerScoreModel = new CricketBowlerScoreModel();
                                    bowlerScoreModel.setPlayerName(bowlerScore.getJSONObject(k).getString("player_name"));
                                    bowlerScoreModel.setOver(String.valueOf(bowlerScore.getJSONObject(k).getInt("over")));
                                    bowlerScoreModel.setWickets(String.valueOf(bowlerScore.getJSONObject(k).getInt("wicket")));
                                    bowlerScoreModel.setBowlingRun(String.valueOf(bowlerScore.getJSONObject(k).getInt("run")));
                                    bowlerScoreModel.setMaiden(String.valueOf(bowlerScore.getJSONObject(k).getInt("maiden")));
                                    bowlerScoreModel.setEconomyRate(String.valueOf(bowlerScore.getJSONObject(k).getInt("economy_rate")));
                                    mBowlerScoreUpdatedModels.add(bowlerScoreModel);
                                }
                            }
                            if (teamId.equals(mMyTeamId)) {
                                if (mUpdatedTeamScoreModels.size() > 0) {
                                    mScoreUpdateSubmitButton.setEnabled(false);
                                } else {
                                    mScoreUpdateSubmitButton.setEnabled(true);
                                }
                            }
                            if (mUpdatedTeamScoreModels.size() > 0) {
                                updatePlayerBattingScore();
                                for (int i = 0; i < mUpdatedTeamScoreModels.size(); i++) {
                                    CricketTeamScoreUpdateModel scoreUpdateModel = mUpdatedTeamScoreModels.get(i);
                                    mFirstTeamBattingInnView.setVisibility(View.VISIBLE);
                                    mSecondTeamBattingInnView.setVisibility(View.VISIBLE);
                                    if (CricketScoreUpdateActivity.this.mOpponentTeamId.equals(String.valueOf(scoreUpdateModel.getTeamId()))) {
                                        mFirstTeamTotalScoreView.setText(scoreUpdateModel.getScore() + Constants.DefaultText.SLASH + scoreUpdateModel.getWickets());
                                        mFirstTeamOversView.setText(scoreUpdateModel.getOvers() + Constants.DefaultText.OVERS);
                                        if (String.valueOf(scoreUpdateModel.getBatInnings()).equals(Constants.DefaultText.ONE)) {
                                            mFirstTeamBattingInnView.setText(Constants.DefaultText.FIRST_INNINGS);
                                            mBatInnings = Constants.DefaultText.FIRST_INNINGS;
                                        } else {
                                            mFirstTeamBattingInnView.setText(Constants.DefaultText.SECOND_INNINGS);
                                            mBatInnings = Constants.DefaultText.SECOND_INNINGS;
                                        }
                                    } else {
                                        mSecondTeamTotalScoreView.setText(scoreUpdateModel.getScore() + Constants.DefaultText.SLASH + scoreUpdateModel.getWickets());
                                        mSecondTeamOversView.setText(scoreUpdateModel.getOvers() + Constants.DefaultText.OVERS);
                                        if (String.valueOf(scoreUpdateModel.getBatInnings()).equals(Constants.DefaultText.ONE)) {
                                            mSecondTeamBattingInnView.setText(Constants.DefaultText.FIRST_INNINGS);
                                            mBatInnings = Constants.DefaultText.FIRST_INNINGS;
                                        } else {
                                            mSecondTeamBattingInnView.setText(Constants.DefaultText.SECOND_INNINGS);
                                            mBatInnings = Constants.DefaultText.SECOND_INNINGS;
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        CommonUtils.ErrorHandleMethod(CricketScoreUpdateActivity.this, response);
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
                CommonUtils.ServerFailureHandleMethod(CricketScoreUpdateActivity.this, t);
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_cricket_score_update_iv_back:
                finish();
                break;
            case R.id.activity_cricket_score_update_tv_batting:
                updatePlayerBattingScore();
                break;
            case R.id.activity_cricket_score_update_tv_bowling:
                updatePlayerBowlingScore();
                break;
            case R.id.activity_cricket_score_update_tv_extras:
                updatePlayerExtrasScore();
                break;
            case R.id.activity_cricket_score_update_tv_total_score:
                updateTeamTotalScore();
                break;
            case R.id.activity_cricket_score_update_tv_skip:
                navigateToSkipUpdate();
                break;
            case R.id.activity_cricket_score_update_btn_next:
                navigateToNextUpdate();
                break;
            case R.id.activity_cricket_score_update_btn_submit:
                if (CommonUtils.isNetworkAvailable(CricketScoreUpdateActivity.this)) {
                    mProgressDialog.show();
                    onUpdateCricketScore();
                } else {
                    Toast.makeText(CricketScoreUpdateActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.activity_cricket_score_update_iv_flag_match:
                MatchFlagFragment matchFlagFragment = MatchFlagFragment.newInstance(mMatchId);
                matchFlagFragment.show(getFragmentManager(), Constants.Messages.FLAG_MATCH);
                break;
        }
    }

    private void onUpdateCricketScore() {
        if (mCricketTeamScoreUpdateAdapter != null) {
            List<CricketTeamScoreModel> cricketTeamScoreModels = mCricketTeamScoreUpdateAdapter.onGetUpdatedTeamScore();
            if (cricketTeamScoreModels != null) {
                JSONArray mTeamScoreArray = new JSONArray();
                int count = 0;
                for (int i = 0; i < cricketTeamScoreModels.size(); i++) {
                    JSONObject teamScoreObject = new JSONObject();
                    try {
                        teamScoreObject.put("team_id", cricketTeamScoreModels.get(i).getTeamId());
                        teamScoreObject.put("score", cricketTeamScoreModels.get(i).getScore());
                        teamScoreObject.put("wickets", cricketTeamScoreModels.get(i).getWickets());
                        teamScoreObject.put("overs", cricketTeamScoreModels.get(i).getOvers());
                        if (mBatInnings.equals(Constants.DefaultText.FIRST_INNINGS)) {
                            if (i == 0) {
                                teamScoreObject.put("bat_innings", Constants.DefaultText.ONE);
                            } else {
                                teamScoreObject.put("bat_innings", Constants.DefaultText.ZERO);
                            }
                        } else {
                            if (i == 0) {
                                teamScoreObject.put("bat_innings", Constants.DefaultText.ZERO);
                            } else {
                                teamScoreObject.put("bat_innings", Constants.DefaultText.ONE);
                            }
                        }
                        if (i == 0) {
                            teamScoreObject.put("extras_score", mExtrasScoreArray);
                        } else {
                            teamScoreObject.put("extras_score", new JSONArray());
                        }
                        if (cricketTeamScoreModels.get(i).getScore().equals(Constants.DefaultText.ZERO)) {
                            count++;
                        }
                        mTeamScoreArray.put(teamScoreObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (count == cricketTeamScoreModels.size()) {
                    Toast.makeText(CricketScoreUpdateActivity.this, Constants.Messages.TEAM_SCORE_CAN_NOT_ZERO, Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                    return;
                }
                PreferenceHelper preferenceHelper = new PreferenceHelper(CricketScoreUpdateActivity.this, Constants.APP_NAME, 0);
                String authToken = "Bearer " + preferenceHelper.getString("user_token", "");
                String sportId = preferenceHelper.getString("teamsport", "");
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                CricketScoreUpdateRequest cricketScoreUpdateRequest = new CricketScoreUpdateRequest();
                cricketScoreUpdateRequest.setMatchId(mMatchId);
                cricketScoreUpdateRequest.setSportType(sportId);
                cricketScoreUpdateRequest.setTeamScore(String.valueOf(mTeamScoreArray));
                cricketScoreUpdateRequest.setBatting(String.valueOf(mBatsmenScoreArray));
                cricketScoreUpdateRequest.setBowling(String.valueOf(mBowlersScoreArray));
                cricketScoreUpdateRequest.setTeamId(mMyTeamId);
                Call<ResponseBody> call = apiInterface.cricketScoreUpdate(authToken, cricketScoreUpdateRequest);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            if (response.body() != null && response.code() == 200) {
                                String data = response.body().string();
                                JSONObject json = new JSONObject(data);
                                Toast.makeText(CricketScoreUpdateActivity.this, json.getString("data"), Toast.LENGTH_SHORT).show();
                                Intent returnIntent = new Intent();
                                setResult(Activity.RESULT_OK, returnIntent);
                                finish();
                            } else {
                                if (response.errorBody() != null) {
                                    String error = response.errorBody().string();
                                    JSONObject jsonObject = new JSONObject(error);
                                    String msg = jsonObject.getString("message");
                                    String code = jsonObject.getString("code");
                                    if (!code.equals("500")) {
                                        Toast.makeText(CricketScoreUpdateActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    String error = response.message();
                                    Toast.makeText(CricketScoreUpdateActivity.this, error, Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(CricketScoreUpdateActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CricketScoreUpdateActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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
        }
    }

    private void navigateToSkipUpdate() {
        if (tabIndex == 0) {
            updatePlayerBowlingScore();
        } else if (tabIndex == 1) {
            updatePlayerExtrasScore();
        } else {
            updateTeamTotalScore();
        }
    }

    private void navigateToNextUpdate() {
        if (tabIndex == 0) {
            if (mUpdatedTeamScoreModels.size() == 0) {
                if (mCricketBatsmenScoreUpdateAdapter != null) {
                    List<CricketBatsmanScoreModel> cricketBatsmanScoreModels = mCricketBatsmenScoreUpdateAdapter.onGetBatsmenScore();
                    if (cricketBatsmanScoreModels != null) {
                        int ballCount = 0;
                        mCricketBatsmanScoreModels = cricketBatsmanScoreModels;
                        mBatsmenScoreArray = new JSONArray();
                        for (int i = 0; i < mCricketBatsmanScoreModels.size(); i++) {
                            JSONObject batsmanObject = new JSONObject();
                            try {
                                batsmanObject.put("player_id", cricketBatsmanScoreModels.get(i).getPlayerId());
                                batsmanObject.put("runs", cricketBatsmanScoreModels.get(i).getRuns());
                                batsmanObject.put("balls", cricketBatsmanScoreModels.get(i).getBalls());
                                batsmanObject.put("fours", cricketBatsmanScoreModels.get(i).getFours());
                                batsmanObject.put("sixs", cricketBatsmanScoreModels.get(i).getSixs());
                                if (cricketBatsmanScoreModels.get(i).getBalls().equals(Constants.DefaultText.ZERO)) {
                                    ballCount++;
                                }
                                mBatsmenScoreArray.put(batsmanObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if (ballCount == cricketBatsmanScoreModels.size()) {
                            Toast.makeText(CricketScoreUpdateActivity.this, Constants.Messages.ENTER_ATLEAST_ONE_BATSMAN_SCORE, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        updatePlayerBowlingScore();
                    }
                }
            } else {
                updatePlayerBowlingScore();
            }
        } else if (tabIndex == 1) {
            if (mUpdatedTeamScoreModels.size() == 0) {
                if (mCricketBowlerScoreUpdateAdapter != null) {
                    if (mBatsmenScoreArray.length() > 0) {
                        List<CricketBowlerScoreModel> cricketBowlerScoreModels = mCricketBowlerScoreUpdateAdapter.onGetBowlersScore();
                        if (cricketBowlerScoreModels != null) {
                            int count = 0;
                            mCricketBowlerScoreModels = cricketBowlerScoreModels;
                            mBowlersScoreArray = new JSONArray();
                            for (int i = 0; i < mCricketBowlerScoreModels.size(); i++) {
                                JSONObject bowlerObject = new JSONObject();
                                try {
                                    bowlerObject.put("player_id", cricketBowlerScoreModels.get(i).getPlayerId());
                                    bowlerObject.put("over", cricketBowlerScoreModels.get(i).getOver());
                                    bowlerObject.put("bowling_run", cricketBowlerScoreModels.get(i).getBowlingRun());
                                    bowlerObject.put("maiden", cricketBowlerScoreModels.get(i).getMaiden());
                                    bowlerObject.put("wickets", cricketBowlerScoreModels.get(i).getWickets());
                                    if (cricketBowlerScoreModels.get(i).getOver().equals(Constants.DefaultText.ZERO)
                                            || (cricketBowlerScoreModels.get(i).getOver().equals(Constants.DefaultText.EMPTY))) {
                                        count++;
                                    }
                                    mBowlersScoreArray.put(bowlerObject);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (count == cricketBowlerScoreModels.size()) {
                                Toast.makeText(CricketScoreUpdateActivity.this, Constants.Messages.ENTER_ATLEAST_ONE_BOWLER_SCORE, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            updatePlayerExtrasScore();
                        }
                    } else {
                        Toast.makeText(CricketScoreUpdateActivity.this, Constants.Messages.ENTER_BATSMAN_SCORE, Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                updatePlayerExtrasScore();
            }
        } else {
            if (mUpdatedTeamScoreModels.size() == 0) {
                if (mCricketExtrasScoreUpdateAdapter != null) {
                    if (mBowlersScoreArray.length() > 0) {
                        List<CricketExtrasScoreUpdate> cricketExtrasScoreUpdates = mCricketExtrasScoreUpdateAdapter.onGetExtrasScore();
                        if (cricketExtrasScoreUpdates != null) {
                            mCricketExtrasScoreUpdates = cricketExtrasScoreUpdates;
                            mCricketExtrasScoreModels = new ArrayList<>();
                            JSONObject extrasObject = new JSONObject();
                            mExtrasScoreArray = new JSONArray();
                            try {
                                extrasObject.put("wide", mCricketExtrasScoreUpdates.get(0).getExtraValue());
                                extrasObject.put("no_ball", mCricketExtrasScoreUpdates.get(1).getExtraValue());
                                extrasObject.put("leg_bye", mCricketExtrasScoreUpdates.get(2).getExtraValue());
                                extrasObject.put("bye", mCricketExtrasScoreUpdates.get(3).getExtraValue());
                                extrasObject.put("penalty", mCricketExtrasScoreUpdates.get(4).getExtraValue());
                                extrasObject.put("extras", mPlayerExtrasValueView.getText().toString());
                                CricketExtrasScoreModel cricketExtrasScoreModel = new CricketExtrasScoreModel();
                                cricketExtrasScoreModel.setWide(mCricketExtrasScoreUpdates.get(0).getExtraValue());
                                cricketExtrasScoreModel.setNoBall(mCricketExtrasScoreUpdates.get(1).getExtraValue());
                                cricketExtrasScoreModel.setLegBye(mCricketExtrasScoreUpdates.get(2).getExtraValue());
                                cricketExtrasScoreModel.setBye(mCricketExtrasScoreUpdates.get(3).getExtraValue());
                                cricketExtrasScoreModel.setPenalty(mCricketExtrasScoreUpdates.get(4).getExtraValue());
                                cricketExtrasScoreModel.setExtras(mPlayerExtrasValueView.getText().toString());
                                mCricketExtrasScoreModels.add(cricketExtrasScoreModel);
                                mExtrasScoreArray.put(extrasObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                          /*  if (mPlayerExtrasValueView.getText().toString().equals(Constants.DefaultText.ZERO)) {
                                Toast.makeText(CricketScoreUpdateActivity.this, Constants.Messages.ENTER_ATLEAST_ONE_BATSMAN_SCORE, Toast.LENGTH_SHORT).show();
                                return;
                            }*/
                            updateTeamTotalScore();
                        }
                    } else {
                        Toast.makeText(CricketScoreUpdateActivity.this, Constants.Messages.ENTER_BOWLER_SCORE, Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                updateTeamTotalScore();
            }
        }

    }

    private void updatePlayerBattingScore() {
        tabIndex = 0;
        mBattingScoreUpdateView.setTextColor(ContextCompat.getColor(this, R.color.dark_red));
        mBowlingScoreUpdateView.setTextColor(ContextCompat.getColor(this, R.color.color_cement));
        mExtrasScoreUpdateView.setTextColor(ContextCompat.getColor(this, R.color.color_cement));
        mTotalScoreUpdateView.setTextColor(ContextCompat.getColor(this, R.color.color_cement));
        mBowlerScoreView.setVisibility(View.GONE);
        mBatsmanScoreView.setVisibility(View.VISIBLE);
        mPlayerScoreCardView.setVisibility(View.VISIBLE);
        mPlayerExtrasCardView.setVisibility(View.GONE);
        mTeamScoreCardView.setVisibility(View.GONE);
        mBattingInningsSpinner.setVisibility(View.VISIBLE);
        String run = getResources().getString(R.string.run);
        String important = Constants.DefaultText.ASTERISK;
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(run);
        int start = builder.length();
        builder.append(important);
        int end = builder.length();
        builder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.color_cement)), start, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mPlayerRunOrOverView.setText(builder);
        mPlayerBallOrMaidenView.setText(getResources().getString(R.string.ball));
        mPlayerFoursOrRunView.setText(getResources().getString(R.string.fours));
        mPlayerSixesOrWicketView.setText(getResources().getString(R.string.sixes));
        mScoreUpdateNextButton.setVisibility(View.VISIBLE);
        mScoreUpdateSkipView.setVisibility(View.VISIBLE);
        mScoreUpdateSubmitButton.setVisibility(View.GONE);
        mCirclePageIndicator.setVisibility(View.VISIBLE);
        List<String> battingInnings = Arrays.asList(getResources().getStringArray(R.array.batting_innings));
        for (int i = 0; i < battingInnings.size(); i++) {
            if (mBatInnings.equals(battingInnings.get(i))) {
                mBattingInningsTeamSpinner.setSelection(i);
                mBattingInningsSpinner.setSelection(i);
            }
        }
        if (mCricketBatsmenScoreUpdateAdapter != null) {
            if (mUpdatedTeamScoreModels.size() > 0) {
                for (int i = 0; i < mUpdatedTeamScoreModels.size(); i++) {
                    if (mMyTeamScoreUpdateStatus.equals("1") || mMyTeamScoreUpdateStatus.equals("2")) {
                        if (mBatsmanScoreUpdatedModels.size() > 0) {
                            mCricketBatsmanScoreModels = mBatsmanScoreUpdatedModels;
                            mBatsmanScoreView.setEnabled(false);
                        } else {
                            mBatsmanScoreView.setEnabled(true);
                        }
                    }
                }
            }
            mCricketBatsmenScoreUpdateAdapter.add(mCricketBatsmanScoreModels);
        }
    }

    private void updatePlayerBowlingScore() {
        tabIndex = 1;
        mBattingScoreUpdateView.setTextColor(ContextCompat.getColor(this, R.color.color_cement));
        mBowlingScoreUpdateView.setTextColor(ContextCompat.getColor(this, R.color.dark_red));
        mExtrasScoreUpdateView.setTextColor(ContextCompat.getColor(this, R.color.color_cement));
        mTotalScoreUpdateView.setTextColor(ContextCompat.getColor(this, R.color.color_cement));
        mBowlerScoreView.setVisibility(View.VISIBLE);
        mBatsmanScoreView.setVisibility(View.GONE);
        mPlayerScoreCardView.setVisibility(View.VISIBLE);
        mPlayerExtrasCardView.setVisibility(View.GONE);
        mTeamScoreCardView.setVisibility(View.GONE);
        mPlayerRunOrOverView.setText(getResources().getString(R.string.over));
        mPlayerBallOrMaidenView.setText(getResources().getString(R.string.maiden));
        mPlayerFoursOrRunView.setText(getResources().getString(R.string.run));
        mPlayerSixesOrWicketView.setText(getResources().getString(R.string.wicket));
        mScoreUpdateNextButton.setVisibility(View.VISIBLE);
        mScoreUpdateSkipView.setVisibility(View.VISIBLE);
        mScoreUpdateSubmitButton.setVisibility(View.GONE);
        mCirclePageIndicator.setVisibility(View.VISIBLE);
        mBattingInningsSpinner.setVisibility(View.INVISIBLE);
        if (mBatsmenScoreArray.length() > 0) {
            mBattingPageIndicator.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_cricket_filled_page));
        } else {
            mBattingPageIndicator.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_cricket_non_filled_page));
        }
        mBowlingPageIndicator.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_cricket_non_filled_page));
        mExtrasPageIndicator.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_cricket_non_filled_page));
        if (mUpdatedTeamScoreModels.size() > 0) {
            for (int i = 0; i < mUpdatedTeamScoreModels.size(); i++) {
                if (mMyTeamScoreUpdateStatus.equals("1") || mMyTeamScoreUpdateStatus.equals("2")) {
                    if (mBowlerScoreUpdatedModels.size() > 0) {
                        mCricketBowlerScoreModels = mBowlerScoreUpdatedModels;
                    }
                }
            }
        }
        if (mCricketBowlerScoreUpdateAdapter == null) {
            mCricketBowlerScoreUpdateAdapter = new CricketBowlerScoreUpdateAdapter(CricketScoreUpdateActivity.this, mMyTeamScoreUpdateStatus);
            mBowlerScoreView.setAdapter(mCricketBowlerScoreUpdateAdapter);
            mCricketBowlerScoreUpdateAdapter.add(mCricketBowlerScoreModels);
        } else {
            mCricketBowlerScoreUpdateAdapter.add(mCricketBowlerScoreModels);
        }
    }

    private void updatePlayerExtrasScore() {
        tabIndex = 2;
        mBattingScoreUpdateView.setTextColor(ContextCompat.getColor(this, R.color.color_cement));
        mBowlingScoreUpdateView.setTextColor(ContextCompat.getColor(this, R.color.color_cement));
        mExtrasScoreUpdateView.setTextColor(ContextCompat.getColor(this, R.color.dark_red));
        mTotalScoreUpdateView.setTextColor(ContextCompat.getColor(this, R.color.color_cement));
        mPlayerScoreCardView.setVisibility(View.GONE);
        mPlayerExtrasCardView.setVisibility(View.VISIBLE);
        mTeamScoreCardView.setVisibility(View.GONE);
        mScoreUpdateNextButton.setVisibility(View.VISIBLE);
        mScoreUpdateSkipView.setVisibility(View.VISIBLE);
        mScoreUpdateSubmitButton.setVisibility(View.GONE);
        mCirclePageIndicator.setVisibility(View.VISIBLE);
        if (mBowlersScoreArray.length() > 0) {
            mBattingPageIndicator.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_cricket_filled_page));
            mBowlingPageIndicator.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_cricket_filled_page));
        } else {
            mBattingPageIndicator.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_cricket_non_filled_page));
            mBowlingPageIndicator.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_cricket_non_filled_page));
        }
        mExtrasPageIndicator.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_cricket_non_filled_page));

        List<CricketExtrasScoreModel> cricketExtrasScoreModels = new ArrayList<>();
        if (mUpdatedTeamScoreModels.size() > 0) {
            CricketTeamScoreUpdateModel teamScoreUpdateModel = mUpdatedTeamScoreModels.get(0);
            CricketExtrasScoreModel cricketExtrasScoreModel = new CricketExtrasScoreModel();
            cricketExtrasScoreModel.setWide(String.valueOf(teamScoreUpdateModel.getWide()));
            cricketExtrasScoreModel.setNoBall(String.valueOf(teamScoreUpdateModel.getNoBall()));
            cricketExtrasScoreModel.setLegBye(String.valueOf(teamScoreUpdateModel.getLegBye()));
            cricketExtrasScoreModel.setBye(String.valueOf(teamScoreUpdateModel.getBye()));
            cricketExtrasScoreModel.setPenalty(String.valueOf(teamScoreUpdateModel.getPenalty()));
            cricketExtrasScoreModel.setExtras(String.valueOf(teamScoreUpdateModel.getExtras()));
            cricketExtrasScoreModels.add(cricketExtrasScoreModel);
        }
        if (mUpdatedTeamScoreModels.size() > 0) {
            if (mMyTeamScoreUpdateStatus.equals("1") || mMyTeamScoreUpdateStatus.equals("2")) {
                CricketTeamScoreUpdateModel teamScoreUpdateModel = mUpdatedTeamScoreModels.get(0);
                mPlayerExtrasValueView.setText(String.valueOf(teamScoreUpdateModel.getExtras()));
                List<String> extras = Arrays.asList(getResources().getStringArray(R.array.cricket_extras));
                String values[] = {String.valueOf(teamScoreUpdateModel.getWide()),
                        String.valueOf(teamScoreUpdateModel.getNoBall()),
                        String.valueOf(teamScoreUpdateModel.getLegBye()),
                        String.valueOf(teamScoreUpdateModel.getBye()),
                        String.valueOf(teamScoreUpdateModel.getPenalty())};
                List<CricketExtrasScoreUpdate> cricketExtrasScoreUpdates = new ArrayList<>();
                for (int k = 0; k < extras.size(); k++) {
                    CricketExtrasScoreUpdate cricketExtrasScoreUpdate = new CricketExtrasScoreUpdate();
                    cricketExtrasScoreUpdate.setExtraName(extras.get(k));
                    cricketExtrasScoreUpdate.setExtraValue(values[k]);
                    cricketExtrasScoreUpdates.add(cricketExtrasScoreUpdate);
                }
                mCricketExtrasScoreUpdates = cricketExtrasScoreUpdates;
                mExtrasScoreUpdatedModels = cricketExtrasScoreModels;
            }
        }
        if (mCricketExtrasScoreUpdateAdapter == null) {
            mCricketExtrasScoreUpdateAdapter = new CricketExtrasScoreUpdateAdapter(CricketScoreUpdateActivity.this, mMyTeamScoreUpdateStatus, this);
            mExtrasScoreView.setAdapter(mCricketExtrasScoreUpdateAdapter);
            mCricketExtrasScoreUpdateAdapter.add(mCricketExtrasScoreUpdates);
        } else {
            mCricketExtrasScoreUpdateAdapter.add(mCricketExtrasScoreUpdates);
        }
    }

    private void updateTeamTotalScore() {
        mBattingScoreUpdateView.setTextColor(ContextCompat.getColor(this, R.color.color_cement));
        mBowlingScoreUpdateView.setTextColor(ContextCompat.getColor(this, R.color.color_cement));
        mExtrasScoreUpdateView.setTextColor(ContextCompat.getColor(this, R.color.color_cement));
        mTotalScoreUpdateView.setTextColor(ContextCompat.getColor(this, R.color.dark_red));
        mPlayerScoreCardView.setVisibility(View.GONE);
        mPlayerExtrasCardView.setVisibility(View.GONE);
        mTeamScoreCardView.setVisibility(View.VISIBLE);
        mScoreUpdateNextButton.setVisibility(View.GONE);
        mScoreUpdateSkipView.setVisibility(View.GONE);
        mScoreUpdateSubmitButton.setVisibility(View.VISIBLE);
        mCirclePageIndicator.setVisibility(View.GONE);
        if (mBowlersScoreArray != null) {
            if (mBowlersScoreArray.length() > 0) {
                mBattingPageIndicator.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_cricket_filled_page));
                mBowlingPageIndicator.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_cricket_filled_page));
                mExtrasPageIndicator.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_cricket_filled_page));
            } else {
                mBattingPageIndicator.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_cricket_non_filled_page));
                mBowlingPageIndicator.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_cricket_non_filled_page));
                mExtrasPageIndicator.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_cricket_non_filled_page));
            }
        }
        List<String> battingInnings = Arrays.asList(getResources().getStringArray(R.array.batting_innings));
        for (int i = 0; i < battingInnings.size(); i++) {
            if (mBatInnings.equals(battingInnings.get(i))) {
                mBattingInningsTeamSpinner.setSelection(i);
                mBattingInningsSpinner.setSelection(i);
            }
        }
        if (mCricketTeamScoreUpdateAdapter == null) {
            CricketTeamScoreModel teamScoreModel1 = new CricketTeamScoreModel();
            teamScoreModel1.setTeamId(mOpponentTeamId);
            teamScoreModel1.setTeamName(mSecondTeamNameView.getText().toString());
            teamScoreModel1.setScore(Constants.DefaultText.ZERO);
            teamScoreModel1.setWickets(Constants.DefaultText.ZERO);
            teamScoreModel1.setOvers(Constants.DefaultText.ZERO);
            teamScoreModel1.setBatInnings(mBatInnings);
            teamScoreModel1.setExtrasScore(new ArrayList<CricketExtrasScoreModel>());
            if (mUpdatedTeamScoreModels.size() > 0) {
                if (mMyTeamScoreUpdateStatus.equals("1") || mMyTeamScoreUpdateStatus.equals("2")) {
                    for (int i = 0; i < mUpdatedTeamScoreModels.size(); i++) {
                        if (String.valueOf(mUpdatedTeamScoreModels.get(i).getTeamId()).equals(mOpponentTeamId)) {
                            CricketTeamScoreUpdateModel scoreUpdateModel = mUpdatedTeamScoreModels.get(i);
                            teamScoreModel1.setScore(String.valueOf(scoreUpdateModel.getScore()));
                            teamScoreModel1.setWickets(String.valueOf(scoreUpdateModel.getWickets()));
                            teamScoreModel1.setOvers(String.valueOf(scoreUpdateModel.getOvers()));
                        }
                    }
                }
            }
            CricketTeamScoreModel teamScoreModel2 = new CricketTeamScoreModel();
            teamScoreModel2.setTeamId(mMyTeamId);
            teamScoreModel2.setTeamName(mFirstTeamNameView.getText().toString());
            teamScoreModel2.setScore(Constants.DefaultText.ZERO);
            teamScoreModel2.setWickets(Constants.DefaultText.ZERO);
            teamScoreModel2.setOvers(Constants.DefaultText.ZERO);
            teamScoreModel2.setExtrasScore(new ArrayList<CricketExtrasScoreModel>());
            if (mUpdatedTeamScoreModels.size() > 0) {
                if (mMyTeamScoreUpdateStatus.equals("1") || mMyTeamScoreUpdateStatus.equals("2")) {
                    for (int i = 0; i < mUpdatedTeamScoreModels.size(); i++) {
                        if (String.valueOf(mUpdatedTeamScoreModels.get(i).getTeamId()).equals(mMyTeamId)) {
                            CricketTeamScoreUpdateModel scoreUpdateModel = mUpdatedTeamScoreModels.get(i);
                            teamScoreModel2.setScore(String.valueOf(scoreUpdateModel.getScore()));
                            teamScoreModel2.setWickets(String.valueOf(scoreUpdateModel.getWickets()));
                            teamScoreModel2.setOvers(String.valueOf(scoreUpdateModel.getOvers()));
                            if (scoreUpdateModel.getBatInnings() == 1) {
                                teamScoreModel2.setBatInnings(Constants.DefaultText.FIRST_INNINGS);
                            }
                        }
                    }
                }
            }
            mCricketTeamScoreModels.add(teamScoreModel2);
            mCricketTeamScoreModels.add(teamScoreModel1);
            mCricketTeamScoreUpdateAdapter = new CricketTeamScoreUpdateAdapter(CricketScoreUpdateActivity.this, mCricketTeamScoreModels, mMyTeamScoreUpdateStatus, this);
            mTeamScoreView.setAdapter(mCricketTeamScoreUpdateAdapter);
            mCricketTeamScoreUpdateAdapter.add(mCricketBatsmanScoreModels, mCricketBowlerScoreModels, mCricketExtrasScoreModels);
        } else {
            mCricketTeamScoreUpdateAdapter.add(mCricketBatsmanScoreModels, mCricketBowlerScoreModels, mCricketExtrasScoreModels);
            mCricketTeamScoreModels = mCricketTeamScoreUpdateAdapter.onGetUpdatedTeamScore();
        }

    }

    @Override
    public void onUpdateExtras(String extras) {
        mPlayerExtrasValueView.setText(extras);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mBatInnings = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onScoreUpdated(List<CricketTeamScoreModel> mTeamScoreUpdates) {
       /* String opponentTeamScore = mTeamScoreUpdates.get(1).getScore();
        String myTeamScore = mTeamScoreUpdates.get(0).getScore();
        String opponentTeamOver = mTeamScoreUpdates.get(1).getOvers();
        String myTeamOver = mTeamScoreUpdates.get(0).getOvers();
        String opponentTeamWicket = mTeamScoreUpdates.get(1).getWickets();
        String myTeamWicket = mTeamScoreUpdates.get(0).getWickets();
        mFirstTeamTotalScoreView.setText(opponentTeamScore + Constants.DefaultText.SLASH + opponentTeamWicket);
        mSecondTeamTotalScoreView.setText(myTeamScore + Constants.DefaultText.SLASH + myTeamWicket);
        mFirstTeamOversView.setText(opponentTeamOver + Constants.DefaultText.OVERS);
        mSecondTeamOversView.setText(myTeamOver + Constants.DefaultText.OVERS);
        if (!TextUtils.isEmpty(opponentTeamScore) && !TextUtils.isEmpty(myTeamScore)) {
            if (Integer.valueOf(opponentTeamScore) > Integer.valueOf(myTeamScore)) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFirstTeamWonIndicationView.setVisibility(View.VISIBLE);
                        mSecondTeamWonIndicationView.setVisibility(View.GONE);
                    }
                }, 300);
            } else if (!opponentTeamScore.equals(myTeamScore)) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFirstTeamWonIndicationView.setVisibility(View.GONE);
                        mSecondTeamWonIndicationView.setVisibility(View.VISIBLE);
                    }
                }, 300);
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFirstTeamWonIndicationView.setVisibility(View.GONE);
                        mSecondTeamWonIndicationView.setVisibility(View.GONE);
                    }
                }, 300);
            }
        }*/
    }
}
