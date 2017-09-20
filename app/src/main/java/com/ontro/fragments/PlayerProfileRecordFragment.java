package com.ontro.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.ontro.Constants;
import com.ontro.R;
import com.ontro.adapters.TeamAdapter;
import com.ontro.dto.SportModel;
import com.ontro.rest.ApiClient;
import com.ontro.rest.ApiInterface;
import com.ontro.utils.CommonUtils;
import com.ontro.utils.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by IDEOMIND02 on 28-08-2017.
 */

public class PlayerProfileRecordFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private View mRootView;
    private TableLayout mPlayerRecordTableLayout;
    private ProgressBar mProgressBar;
    private ImageView mBagdeLevel;
    private RecyclerView mPlayerSports;
    private TextView mNoRecordsTextView, mPlayerProgressStage, mTeamHeaderTextView;
    private CircularProgressView mProgressView;
    private RelativeLayout mLevelLayout;
    private LayoutInflater mLayoutInflater;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private PreferenceHelper mPreferenceHelper;
    private ApiInterface mApiInterface;
    private String mAuthToken;

    public static Fragment newInstance(String playerId, String sportTitle) {
        PlayerProfileRecordFragment profileRecordFragment = new PlayerProfileRecordFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BundleKeys.PLAYER_ID, playerId);
        bundle.putString(Constants.BundleKeys.SPORT_NAME, sportTitle);
        profileRecordFragment.setArguments(bundle);
        return profileRecordFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_player_profile_record, container, false);
        initView();
        if(CommonUtils.isNetworkAvailable(getActivity())) {
            mProgressView.setVisibility(View.VISIBLE);
            if(getArguments() != null) {
                String playerId = getArguments().getString(Constants.BundleKeys.PLAYER_ID);
                String sportName  = getArguments().getString(Constants.BundleKeys.SPORT_NAME);
                mPreferenceHelper  = new PreferenceHelper(getActivity(), Constants.APP_NAME, 0);
                mApiInterface = ApiClient.getClient().create(ApiInterface.class);
                mAuthToken = "Bearer " + mPreferenceHelper.getString("user_token", "");
                String userId = mPreferenceHelper.getString("user_id", "");
                if (playerId != null && !userId.equals(playerId)) {
                    getPlayerTeams(playerId, sportName);
                    mTeamHeaderTextView.setVisibility(View.VISIBLE);
                } else {
                    mTeamHeaderTextView.setVisibility(View.GONE);
                }
                getPlayerRecords(playerId, sportName);
            }
        } else {
            Toast.makeText(getActivity(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }
        return mRootView;
    }

    private void initView() {
        mPlayerRecordTableLayout = (TableLayout) mRootView.findViewById(R.id.fragment_player_profile_record_tl);
        mPlayerProgressStage = (TextView) mRootView.findViewById(R.id.fragment_player_profile_record_progress_stage);
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.fragment_player_profile_record_pb_level);
        mBagdeLevel = (ImageView) mRootView.findViewById(R.id.fragment_player_profile_record_badge);
        mNoRecordsTextView = (TextView) mRootView.findViewById(R.id.fragment_player_profile_record_tv_no_record);
        mProgressView = (CircularProgressView) mRootView.findViewById(R.id.fragment_player_profile_record_progress_view);
        mLevelLayout = (RelativeLayout) mRootView.findViewById(R.id.fragment_player_profile_record_rl_level);
        mPlayerSports = (RecyclerView) mRootView.findViewById(R.id.fragment_player_profile_record_rv_sports);
        mTeamHeaderTextView = (TextView) mRootView.findViewById(R.id.fragment_player_profile_record_tv_team);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mPlayerSports.setNestedScrollingEnabled(false);
        mPlayerSports.setLayoutManager(linearLayoutManager);

    }

    private void getPlayerRecords(String playerId, String sportName) {
        String userId = mPreferenceHelper.getString("user_id", "");
        String sportId = String.valueOf(CommonUtils.sportIdCheck(sportName));
        Call<ResponseBody> call;
        if (null != playerId) {
            call = mApiInterface.getPlayerRecordsInfo(mAuthToken, sportId, playerId);
        } else {
            call = mApiInterface.getPlayerRecordsInfo(mAuthToken, sportId, userId);
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        mLevelLayout.setVisibility(View.VISIBLE);
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                        JSONObject json = new JSONObject(data);
                        JSONObject datajson = new JSONObject(json.getString("data"));
                        String badgelevel = datajson.getString("badge");
                        int level = Integer.parseInt(datajson.getString("level"));
                        String progresslevel = datajson.getString("progress");
                        int progress= Integer.parseInt(progresslevel);
                        progress = progress * 20;
                        mProgressBar.setProgress(progress);
                        mPlayerProgressStage.setText("Level " + level);
                        JSONArray jsonArray = datajson.getJSONArray("records");
                        BadgeCalculation(badgelevel);
                        mLayoutInflater = LayoutInflater.from(getActivity());
                        mPlayerRecordTableLayout.removeAllViews();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            View recordView = mLayoutInflater.inflate(R.layout.fragment_record_sport, mPlayerRecordTableLayout, false);
                            TextView name = (TextView) recordView.findViewById(R.id.record_sport_tv_recordname);
                            TextView value = (TextView) recordView.findViewById(R.id.record_sport_tv_recordvalue);
                            View lineview = recordView.findViewById(R.id.record_sport_v_line);
                            lineview.setVisibility(View.VISIBLE);
                            if (i == (jsonArray.length() - 1)) {
                                lineview.setVisibility(View.GONE);
                            }
                            name.setText(jsonArray.getJSONObject(i).getString("name"));
                            value.setText(jsonArray.getJSONObject(i).getString("value"));
                            mPlayerRecordTableLayout.addView(recordView);
                        }
                    } else {
                        CommonUtils.ErrorHandleMethod(getActivity(),response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mProgressView.setVisibility(View.GONE);
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //  Log error here since request failed
                CommonUtils.ServerFailureHandleMethod(getActivity(), t);
                mProgressView.setVisibility(View.GONE);
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void BadgeCalculation(String badgelevel) {
        mBagdeLevel.setVisibility(View.VISIBLE);
        switch (badgelevel) {
            case "0":
                mBagdeLevel.setVisibility(View.GONE);
                break;
            case "1":
                mBagdeLevel.setImageResource(R.drawable.badge);
                break;
            case "2":
                mBagdeLevel.setImageResource(R.drawable.badge2);
                break;
            case "3":
                mBagdeLevel.setImageResource(R.drawable.badge3);
                break;
            case "4":
                mBagdeLevel.setImageResource(R.drawable.badge4);
                break;
            case "5":
                mBagdeLevel.setImageResource(R.drawable.badge5);
                break;
        }
    }

    private void getPlayerTeams(String mPlayerId, String sportName) {
        int playerId = Integer.valueOf(mPlayerId);
        final String sportId = String.valueOf(CommonUtils.sportIdCheck(sportName));
        final ArrayList<SportModel> sportModels = new ArrayList<>();
        Call<ResponseBody> call = mApiInterface.getPlayerTeam(mAuthToken, playerId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                        JSONObject json = new JSONObject(data);
                        JSONArray array = new JSONArray(json.getString("data"));
                        for (int i = 0; i < array.length(); i++) {
                            SportModel sportModel = new SportModel();
                            sportModel.setSportid(array.getJSONObject(i).getString("team_id"));
                            sportModel.setSportname(array.getJSONObject(i).getString("team_name"));
                            String logo = array.getJSONObject(i).getString("team_logo").equals("null") ? "http://euroguide.fourfourtwo.com/quiz-nation/media/theme/badge-wal.png" : array.getJSONObject(i).getString("team_logo");
                            sportModel.setTeamlogo(logo);
                            String id = array.getJSONObject(i).getString("sport");
                            sportModel.setSportimage(CommonUtils.sportCheck(id));
                            sportModel.setLocation(array.getJSONObject(i).getString("team_location"));
                            String percentage = array.getJSONObject(i).getString("progress").equals("null") ? "0" : array.getJSONObject(i).getString("progress");
                            sportModel.setProgress_percent(percentage);
                            sportModel.setBatchimage(CommonUtils.batchCheck(array.getJSONObject(i).getString("badge")));
                            if(sportId.equals(id)) {
                                sportModels.add(sportModel);
                            }
                        }
                        if(sportModels.size() > 0) {
                            mTeamHeaderTextView.setVisibility(View.VISIBLE);
                        } else {
                            mTeamHeaderTextView.setVisibility(View.GONE);
                        }
                        TeamAdapter teamAdapter = new TeamAdapter(getActivity(), sportModels);
                        mPlayerSports.setAdapter(teamAdapter);
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                String error = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(error);
                                String msg = jsonObject.getString("message");
                                String code = jsonObject.getString("code");
                                if (!code.equals("500")) {
                                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), R.string.error500, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                String error = response.message();
                                Toast.makeText(getActivity(), R.string.error500, Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            String error = response.message();
                            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mProgressView.isShown()) mProgressView.setVisibility(View.GONE);
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
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
                if (mProgressView.isShown()) mProgressView.setVisibility(View.GONE);
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }


    @Override
    public void onRefresh() {
        if(CommonUtils.isNetworkAvailable(getActivity())) {
            if(getArguments() != null) {
                String playerId = getArguments().getString(Constants.BundleKeys.PLAYER_ID);
                String sportName  = getArguments().getString(Constants.BundleKeys.SPORT_NAME);
                getPlayerRecords(playerId, sportName);
            }
        } else {
            Toast.makeText(getActivity(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }
    }
}
