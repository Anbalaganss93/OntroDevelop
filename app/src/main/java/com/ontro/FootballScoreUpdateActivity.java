package com.ontro;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ontro.adapters.FootballPlayerScoreUpdateAdapter;
import com.ontro.adapters.FootballTeamScoreUpdateAdapter;
import com.ontro.customui.ProfileImageView;
import com.ontro.dto.FootballPlayerScoreUpdate;
import com.ontro.dto.FootballScoreUpdateRequest;
import com.ontro.dto.FootballTeamScoreUpdate;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FootballScoreUpdateActivity extends AppCompatActivity implements View.OnClickListener, FootballTeamScoreUpdateAdapter.TeamScoreUpdateListener {
    private ImageView mBackNavigationView;
    private TextView mPlayerStatisticsView, mTotalScoreView, mPlayerStatisticsSkipView;
    private RecyclerView mPlayerScoreView, mTeamScoreView;
    private RelativeLayout mStatisticsNextLayout, mScoreSubmitLayout;
    private Button mStatisticsNextButton, mScoreSubmitButton;
    private FootballPlayerScoreUpdateAdapter mFootballPlayerScoreUpdateAdapter;
    private FootballTeamScoreUpdateAdapter mFootballTeamScoreUpdateAdapter;
    private CardView mPlayerStatisticsCardView, mTeamScoreCardView;
    private TextView mMatchDateView, mMatchVenueView, mMatchStatusView;
    private TextView mFirstTeamNameView, mSecondTeamNameView, mFirstTeamTotalScoreView, mSecondTeamTotalScoreView;
    private ImageView mSportImageView, mFirstTeamUpdateIndicationView, mSecondTeamUpdateIndicationView,
            mFirstTeamWonIndicationView, mSecondTeamWonIndicationView, mMatchFlagImageView;
    private ProfileImageView mFirstTeamImageView, mSecondTeamImageView;
    private Dialog mProgressDialog;
    private List<FootballPlayerScoreUpdate> mFootballPlayerScoreUpdates = new ArrayList<>();
    private List<FootballTeamScoreUpdate> mFootballTeamScoreUpdates = new ArrayList<>();
    private String mMyTeamId, mOpponentTeamId, mMatchId;
    private String mMyTeamScoreUpdateStatus, mOpponentTeamScoreUpdateStatus;
    private String mAuthToken, mSportId;
    private ApiInterface mApiInterface;
    List<FootballPlayerScoreUpdate> mFootballPlayerScoreUpdatedModels = new ArrayList<>();
    private List<FootballTeamScoreUpdate> mFootballTeamScoreUpdatedModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_football_score_update);
        overridePendingTransition(R.anim.slide_in_right, R.anim.pause);
        initView();
        getPlayerList();
        setListener();
    }

    private void initView() {
        mBackNavigationView = (ImageView) findViewById(R.id.activity_football_score_update_iv_back);
        mPlayerStatisticsView = (TextView) findViewById(R.id.activity_football_score_update_tv_player_statistics);
        mTotalScoreView = (TextView) findViewById(R.id.activity_football_score_update_tv_total_score);
        mPlayerScoreView = (RecyclerView) findViewById(R.id.activity_football_score_update_rv_player_statistics);
        mTeamScoreView = (RecyclerView) findViewById(R.id.activity_football_score_update_rv_team_score);
        mStatisticsNextLayout = (RelativeLayout) findViewById(R.id.activity_football_score_update_rl_player_statistics);
        mScoreSubmitLayout = (RelativeLayout) findViewById(R.id.activity_football_score_update_rl_total_score);
        mPlayerStatisticsSkipView = (TextView) findViewById(R.id.activity_football_score_update_tv_skip);
        mStatisticsNextButton = (Button) findViewById(R.id.activity_football_score_update_btn_next);
        mScoreSubmitButton = (Button) findViewById(R.id.activity_football_score_update_btn_submit);
        mPlayerStatisticsCardView = (CardView) findViewById(R.id.activity_football_score_update_cv_player_statistics);
        mTeamScoreCardView = (CardView) findViewById(R.id.activity_football_score_update_cv_team_score);
        mMatchFlagImageView = (ImageView) findViewById(R.id.activity_football_score_update_iv_flag_match);
        mMatchDateView = (TextView) findViewById(R.id.game_header_date);
        mMatchVenueView = (TextView) findViewById(R.id.game_header_location);
        mFirstTeamNameView = (TextView) findViewById(R.id.game_header_team_one_name);
        mSecondTeamNameView = (TextView) findViewById(R.id.game_header_team_two_name);
        mFirstTeamTotalScoreView = (TextView) findViewById(R.id.game_header_team_one_score);
        mSecondTeamTotalScoreView = (TextView) findViewById(R.id.game_header_team_two_score);
        mFirstTeamUpdateIndicationView = (ImageView) findViewById(R.id.team_one_winner_indicator);
        mSecondTeamUpdateIndicationView = (ImageView) findViewById(R.id.team_two_winner_indicator);
        mFirstTeamWonIndicationView = (ImageView) findViewById(R.id.winner_teamone);
        mSecondTeamWonIndicationView = (ImageView) findViewById(R.id.winner_teamtwo);
        mFirstTeamImageView = (ProfileImageView) findViewById(R.id.game_header_team);
        mSecondTeamImageView = (ProfileImageView) findViewById(R.id.game_header_team2);
        mMatchStatusView = (TextView) findViewById(R.id.game_header_team_score_update_status);
        mSportImageView = (ImageView) findViewById(R.id.game_header_sportimage);

        mProgressDialog = new Dialog(FootballScoreUpdateActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setContentView(R.layout.progressdialog_layout);
        mPlayerStatisticsView.setTextColor(ContextCompat.getColor(this, R.color.dark_red));

        PreferenceHelper mPreferenceHelper = new PreferenceHelper(FootballScoreUpdateActivity.this, Constants.APP_NAME, 0);
        mAuthToken = "Bearer " + mPreferenceHelper.getString("user_token", "");
        mSportId = mPreferenceHelper.getString("teamsport", "");
        mSportImageView.setImageResource(CommonUtils.scoreUpdateSport(mSportId));
        mApiInterface = ApiClient.getClient().create(ApiInterface.class);
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
            mOpponentTeamScoreUpdateStatus = bundle.getString(Constants.BundleKeys.OPPONENT_USER_SCORE_STATUS);
            switch (mOpponentTeamScoreUpdateStatus) {
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
                    if (CommonUtils.isNetworkAvailable(FootballScoreUpdateActivity.this)) {
                        mProgressDialog.show();
                        getMyTeamScore(mMyTeamId);
                    } else {
                        Toast.makeText(FootballScoreUpdateActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.DefaultText.THREE:
                    mFirstTeamUpdateIndicationView.setVisibility(View.GONE);
                    break;
            }
            String opponentTeamImage = bundle.getString(Constants.BundleKeys.OPPONENT_TEAM_IMAGE);
            if (opponentTeamImage != null) {
                Glide.with(FootballScoreUpdateActivity.this).load(opponentTeamImage).placeholder(R.drawable.match_default).dontAnimate().into(mSecondTeamImageView);
            }
            String myTeamImage = bundle.getString(Constants.BundleKeys.MY_TEAM_IMAGE);
            if (myTeamImage != null) {
                Glide.with(FootballScoreUpdateActivity.this).load(myTeamImage).placeholder(R.drawable.match_default).dontAnimate().into(mFirstTeamImageView);
            }

            ArrayList<MySquadInfo> mySquadPlayers = (ArrayList<MySquadInfo>) bundle.getSerializable(Constants.BundleKeys.MY_TEAM_SQUAD);
            for (MySquadInfo squadInfo : mySquadPlayers) {
                FootballPlayerScoreUpdate update = new FootballPlayerScoreUpdate();
                update.setPlayerId(squadInfo.getPlayerId());
                update.setPlayerName(squadInfo.getPlayerName());
                update.setPlayerGoals(Constants.DefaultText.ZERO);
                update.setPlayerAssists(Constants.DefaultText.ZERO);
                update.setIsGolfKeeper(Constants.DefaultText.ZERO);
                mFootballPlayerScoreUpdates.add(update);
            }

            mFootballPlayerScoreUpdateAdapter = new FootballPlayerScoreUpdateAdapter(this, mMyTeamScoreUpdateStatus);
            mPlayerScoreView.setAdapter(mFootballPlayerScoreUpdateAdapter);
            mFootballPlayerScoreUpdateAdapter.add(mFootballPlayerScoreUpdates);
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
                                    FootballTeamScoreUpdate teamScoreUpdateModel = new FootballTeamScoreUpdate();
                                    teamScoreUpdateModel.setTeamId(String.valueOf(teamScoreArray.getJSONObject(i).getInt("team_id")));
                                    teamScoreUpdateModel.setTeamGoals(String.valueOf(teamScoreArray.getJSONObject(i).getInt("score")));
                                    mFootballTeamScoreUpdatedModels.add(teamScoreUpdateModel);
                                }
                            }
                            JSONArray playerScore = new JSONArray(dataJson.getString("player_score"));
                            if (playerScore != null) {
                                for (int k = 0; k < playerScore.length(); k++) {
                                    FootballPlayerScoreUpdate footballPlayerScoreUpdate = new FootballPlayerScoreUpdate();
                                    footballPlayerScoreUpdate.setPlayerName(playerScore.getJSONObject(k).getString("player_name"));
                                    footballPlayerScoreUpdate.setPlayerGoals(String.valueOf(playerScore.getJSONObject(k).getInt("no_of_goal")));
                                    footballPlayerScoreUpdate.setPlayerAssists(String.valueOf(playerScore.getJSONObject(k).getInt("assists")));
                                    footballPlayerScoreUpdate.setIsGolfKeeper(String.valueOf(playerScore.getJSONObject(k).getString("goal_keeper")));
                                    mFootballPlayerScoreUpdatedModels.add(footballPlayerScoreUpdate);
                                }
                            }
                            if (teamId.equals(mMyTeamId)) {
                                if (mFootballTeamScoreUpdatedModels.size() > 0) {
                                    mScoreSubmitButton.setEnabled(false);
                                    mScoreSubmitButton.setTextColor(ContextCompat.getColor(FootballScoreUpdateActivity.this, R.color.white_50));
                                    mStatisticsNextButton.setEnabled(false);
                                    mStatisticsNextButton.setTextColor(ContextCompat.getColor(FootballScoreUpdateActivity.this, R.color.white_50));
                                } else {
                                    mScoreSubmitButton.setEnabled(true);
                                    mScoreSubmitButton.setTextColor(ContextCompat.getColor(FootballScoreUpdateActivity.this, R.color.white));
                                    mStatisticsNextButton.setEnabled(true);
                                    mStatisticsNextButton.setTextColor(ContextCompat.getColor(FootballScoreUpdateActivity.this, R.color.white));
                                }
                            }
                            if (mFootballTeamScoreUpdatedModels.size() > 0) {
                                updatePlayerStatisticsUi();
                                for (int i = 0; i < mFootballTeamScoreUpdatedModels.size(); i++) {
                                    FootballTeamScoreUpdate scoreUpdateModel = mFootballTeamScoreUpdatedModels.get(i);
                                    if (mOpponentTeamId.equals(String.valueOf(scoreUpdateModel.getTeamId()))) {
                                        mSecondTeamTotalScoreView.setText(scoreUpdateModel.getTeamGoals());
                                    } else {
                                        mFirstTeamTotalScoreView.setText(scoreUpdateModel.getTeamGoals());
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        CommonUtils.ErrorHandleMethod(FootballScoreUpdateActivity.this, response);
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
                CommonUtils.ServerFailureHandleMethod(FootballScoreUpdateActivity.this, t);
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
        });
    }

    private void setListener() {
        mBackNavigationView.setOnClickListener(this);
        mStatisticsNextButton.setOnClickListener(this);
        mScoreSubmitButton.setOnClickListener(this);
        mPlayerStatisticsSkipView.setOnClickListener(this);
        mPlayerStatisticsView.setOnClickListener(this);
        mTotalScoreView.setOnClickListener(this);
        mMatchFlagImageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_football_score_update_tv_player_statistics:
                updatePlayerStatisticsUi();
                break;
            case R.id.activity_football_score_update_tv_total_score:
                updateTeamScoreUi();
                break;
            case R.id.activity_football_score_update_btn_next:
                onNextClicked();
                break;
            case R.id.activity_football_score_update_btn_submit:
                if (CommonUtils.isNetworkAvailable(FootballScoreUpdateActivity.this)) {
                    mProgressDialog.show();
                    onUpdateFootballScore();
                } else {
                    Toast.makeText(FootballScoreUpdateActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.activity_football_score_update_tv_skip:
                updateTeamScoreUi();
                break;
            case R.id.activity_football_score_update_iv_back:
                finish();
                overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
                break;
            case R.id.activity_football_score_update_iv_flag_match:
                MatchFlagFragment matchFlagFragment = MatchFlagFragment.newInstance(mMatchId);
                matchFlagFragment.show(getFragmentManager(), Constants.Messages.FLAG_MATCH);
                break;
        }
    }

    private void onUpdateFootballScore() {
        if (mFootballPlayerScoreUpdateAdapter != null && mFootballTeamScoreUpdateAdapter != null) {
            List<FootballPlayerScoreUpdate> playerScoreUpdates = mFootballPlayerScoreUpdateAdapter.getUpdatedScore();
            JSONArray mPlayerScoreArray = new JSONArray();
            JSONArray mTeamScoreArray = new JSONArray();
            if (playerScoreUpdates != null) {
                for (int i = 0; i < playerScoreUpdates.size(); i++) {
                    JSONObject playerScoreObject = new JSONObject();
                    try {
                        playerScoreObject.put("player_id", playerScoreUpdates.get(i).getPlayerId());
                        playerScoreObject.put("no_of_goal", playerScoreUpdates.get(i).getPlayerGoals());
                        playerScoreObject.put("assists", playerScoreUpdates.get(i).getPlayerAssists());
                        playerScoreObject.put("goal_keeper", playerScoreUpdates.get(i).getIsGolfKeeper());
                        mPlayerScoreArray.put(playerScoreObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            List<FootballTeamScoreUpdate> teamScoreUpdates = mFootballTeamScoreUpdateAdapter.onUpdatedTeamScore();
            if (teamScoreUpdates != null) {
                int count = 0;
                for (int i = 0; i < teamScoreUpdates.size(); i++) {
                    JSONObject teamScoreObject = new JSONObject();
                    try {
                        teamScoreObject.put("team_id", teamScoreUpdates.get(i).getTeamId());
                        teamScoreObject.put("score", teamScoreUpdates.get(i).getTeamGoals());
                        if (teamScoreUpdates.get(i).getTeamGoals().equals(Constants.DefaultText.ZERO)) {
                            count++;
                        }
                        mTeamScoreArray.put(teamScoreObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (count == teamScoreUpdates.size()) {
                    Toast.makeText(FootballScoreUpdateActivity.this, Constants.Messages.TEAM_SCORE_CAN_NOT_ZERO, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            PreferenceHelper preferenceHelper = new PreferenceHelper(FootballScoreUpdateActivity.this, Constants.APP_NAME, 0);
            String authToken = "Bearer " + preferenceHelper.getString("user_token", "");
            String sportId = preferenceHelper.getString("teamsport", "");
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            FootballScoreUpdateRequest footballScoreUpdateRequest = new FootballScoreUpdateRequest();
            footballScoreUpdateRequest.setMatchId(mMatchId);
            footballScoreUpdateRequest.setSportType(sportId);
            footballScoreUpdateRequest.setTeamScore(String.valueOf(mTeamScoreArray));
            footballScoreUpdateRequest.setPlayerScore(String.valueOf(mPlayerScoreArray));
            footballScoreUpdateRequest.setTeamId(mMyTeamId);
            Call<ResponseBody> call = apiInterface.footballScoreUpdate(authToken, footballScoreUpdateRequest);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.body() != null && response.code() == 200) {
                            String data = response.body().string();
                            JSONObject json = new JSONObject(data);
                            Toast.makeText(FootballScoreUpdateActivity.this, json.getString("data"), Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(FootballScoreUpdateActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                String error = response.message();
                                Toast.makeText(FootballScoreUpdateActivity.this, error, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(FootballScoreUpdateActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(FootballScoreUpdateActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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

    private void updatePlayerStatisticsUi() {
        mPlayerStatisticsView.setTextColor(ContextCompat.getColor(this, R.color.dark_red));
        mTotalScoreView.setTextColor(ContextCompat.getColor(this, R.color.color_cement));
        mStatisticsNextLayout.setVisibility(View.VISIBLE);
        mScoreSubmitLayout.setVisibility(View.GONE);
        mPlayerStatisticsCardView.setVisibility(View.VISIBLE);
        mTeamScoreCardView.setVisibility(View.GONE);
        if (mFootballPlayerScoreUpdateAdapter != null) {
            if (mFootballPlayerScoreUpdatedModels.size() > 0) {
                if (mMyTeamScoreUpdateStatus.equals("1") || mMyTeamScoreUpdateStatus.equals("2")) {
                    if (mFootballPlayerScoreUpdatedModels.size() > 0) {
                        mFootballPlayerScoreUpdates = mFootballPlayerScoreUpdatedModels;
                    }
                }
            }
            mFootballPlayerScoreUpdateAdapter.add(mFootballPlayerScoreUpdates);
        }
    }

    private void onNextClicked() {
        if (mFootballPlayerScoreUpdateAdapter != null) {
            int count = 0;
            List<FootballPlayerScoreUpdate> playerScoreUpdates = mFootballPlayerScoreUpdateAdapter.getUpdatedScore();
            if (playerScoreUpdates != null) {
                int totalGoals = 0;
                int totalAssists = 0;
                for (int k = 0; k < playerScoreUpdates.size(); k++) {
                    FootballPlayerScoreUpdate scoreUpdate = playerScoreUpdates.get(k);
                    totalGoals = totalGoals + Integer.valueOf(scoreUpdate.getPlayerGoals());
                    totalAssists = totalAssists + Integer.valueOf(scoreUpdate.getPlayerAssists());
                    if (scoreUpdate.getIsGolfKeeper().equals(Constants.DefaultText.ZERO)) {
                        count++;
                    }
                }
                if (count == playerScoreUpdates.size()) {
                    Toast.makeText(FootballScoreUpdateActivity.this, Constants.Messages.SELECT_GOLF_KEEPER, Toast.LENGTH_LONG).show();
                    return;
                } else if (totalAssists > totalGoals) {
                    Toast.makeText(FootballScoreUpdateActivity.this, Constants.Messages.GOALS_AND_ASSIST_MISMATCH, Toast.LENGTH_LONG).show();
                    return;
                }
                int remainAssist = totalAssists;
                for (int i = 0; i < playerScoreUpdates.size(); i++) {
                    FootballPlayerScoreUpdate scoreUpdate = playerScoreUpdates.get(i);
                    int playerAssist = Integer.valueOf(scoreUpdate.getPlayerAssists());
                    int playerGoals = totalGoals - Integer.valueOf(scoreUpdate.getPlayerGoals());
                    if (playerAssist <= playerGoals) {
                        remainAssist = remainAssist - playerAssist;
                    }
                }
                if (remainAssist != 0) {
                    Toast.makeText(FootballScoreUpdateActivity.this, Constants.Messages.GOALS_AND_ASSIST_MISMATCH, Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
        updateTeamScoreUi();
    }

    private void updateTeamScoreUi() {
        mPlayerStatisticsView.setTextColor(ContextCompat.getColor(this, R.color.color_cement));
        mTotalScoreView.setTextColor(ContextCompat.getColor(this, R.color.dark_red));
        mStatisticsNextLayout.setVisibility(View.GONE);
        mScoreSubmitLayout.setVisibility(View.VISIBLE);
        mPlayerStatisticsCardView.setVisibility(View.GONE);
        mTeamScoreCardView.setVisibility(View.VISIBLE);
        if (mFootballTeamScoreUpdateAdapter == null) {
            FootballTeamScoreUpdate update1 = new FootballTeamScoreUpdate();
            update1.setTeamId(mMyTeamId);
            update1.setTeamName(mFirstTeamNameView.getText().toString());
            update1.setTeamGoals(Constants.DefaultText.ZERO);
            if (mFootballTeamScoreUpdatedModels.size() > 0) {
                if (mMyTeamScoreUpdateStatus.equals("1") || mMyTeamScoreUpdateStatus.equals("2")) {
                    for (int i = 0; i < mFootballTeamScoreUpdatedModels.size(); i++) {
                        FootballTeamScoreUpdate scoreUpdateModel = mFootballTeamScoreUpdatedModels.get(i);
                        if (String.valueOf(scoreUpdateModel.getTeamId()).equals(mMyTeamId)) {
                            update1.setTeamGoals(scoreUpdateModel.getTeamGoals());
                            break;
                        }
                    }
                }
            }
            mFootballTeamScoreUpdates.add(update1);
            FootballTeamScoreUpdate update2 = new FootballTeamScoreUpdate();
            update2.setTeamId(mOpponentTeamId);
            update2.setTeamName(mSecondTeamNameView.getText().toString());
            update2.setTeamGoals(Constants.DefaultText.ZERO);
            if (mFootballTeamScoreUpdatedModels.size() > 0) {
                if (mMyTeamScoreUpdateStatus.equals("1") || mMyTeamScoreUpdateStatus.equals("2")) {
                    for (int i = 0; i < mFootballTeamScoreUpdatedModels.size(); i++) {
                        FootballTeamScoreUpdate scoreUpdateModel = mFootballTeamScoreUpdatedModels.get(i);
                        if (String.valueOf(scoreUpdateModel.getTeamId()).equals(mOpponentTeamId)) {
                            update2.setTeamGoals(scoreUpdateModel.getTeamGoals());
                            break;
                        }
                    }
                }
            }
            mFootballTeamScoreUpdates.add(update2);
            List<FootballPlayerScoreUpdate> playerScoreUpdates = mFootballPlayerScoreUpdateAdapter.getUpdatedScore();
            if (playerScoreUpdates != null) {
                mFootballPlayerScoreUpdates = playerScoreUpdates;
                mFootballTeamScoreUpdateAdapter = new FootballTeamScoreUpdateAdapter(FootballScoreUpdateActivity.this, mMyTeamScoreUpdateStatus, mFootballTeamScoreUpdates, this);
                mTeamScoreView.setAdapter(mFootballTeamScoreUpdateAdapter);
                mFootballTeamScoreUpdateAdapter.add(playerScoreUpdates);
            }
        } else {
            List<FootballPlayerScoreUpdate> playerScoreUpdates = mFootballPlayerScoreUpdateAdapter.getUpdatedScore();
            mFootballTeamScoreUpdateAdapter.add(playerScoreUpdates);
            mFootballTeamScoreUpdates = mFootballTeamScoreUpdateAdapter.onUpdatedTeamScore();
        }
    }

    @Override
    public void onScoreUpdated(List<FootballTeamScoreUpdate> mTeamScoreUpdates) {
        String opponentTeamScore = mTeamScoreUpdates.get(1).getTeamGoals();
        String myTeamScore = mTeamScoreUpdates.get(0).getTeamGoals();
        mFirstTeamTotalScoreView.setText(myTeamScore);
        mSecondTeamTotalScoreView.setText(opponentTeamScore);
       /* if (Integer.valueOf(opponentTeamScore) > Integer.valueOf(myTeamScore)) {
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
        }*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
    }
}
