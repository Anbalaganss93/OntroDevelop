package com.ontro;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.ontro.adapters.PlayerTeamInviteAdapter;
import com.ontro.dto.InvitePlayerRequest;
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
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayerInviteActivity extends AppCompatActivity implements View.OnClickListener, PlayerTeamInviteAdapter.PlayerInviteListener {
    private ImageView mBackImageView;
    private RecyclerView mTeamListView;
    private TextView mEmptyTeamNotificationTextView;
    private PlayerTeamInviteAdapter mPlayerTeamInviteAdapter;
    private List<SportModel> mSportModels;
    private PreferenceHelper mPreferenceHelper;
    private ApiInterface mApiInterface;
    private String mAuthToken;
    private Dialog mProgressView;
    private MixpanelAPI mMixpanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_invite);
        overridePendingTransition(R.anim.slide_in_right, R.anim.pause);
        init();
        initView();
        initProgressView();
        if (CommonUtils.isNetworkAvailable(this)) {
            mProgressView.show();
            getMyTeams();
        } else {
            Toast.makeText(this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }
        setListener();
    }

    private void init() {
        mPreferenceHelper = new PreferenceHelper(this, Constants.APP_NAME, 0);
        mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        mAuthToken = "Bearer " + mPreferenceHelper.getString("user_token", "");
        mMixpanel = MixpanelAPI.getInstance(this, getResources().getString(R.string.mixpanel_token));
    }

    private void initView() {
        mBackImageView = (ImageView) findViewById(R.id.activity_player_invite_iv_back);
        mTeamListView = (RecyclerView) findViewById(R.id.activity_player_invite_rv_team_list);
        mEmptyTeamNotificationTextView = (TextView) findViewById(R.id.activity_player_invite_tv_no_team);
    }

    private void initProgressView() {
        mProgressView = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        mProgressView.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressView.setContentView(R.layout.progressdialog_layout);
    }

    private void setListener() {
        mBackImageView.setOnClickListener(this);
    }

    private void getMyTeams() {
        Call<ResponseBody> call = mApiInterface.MyTeam(mAuthToken);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                        JSONObject json = new JSONObject(data);
                        if (mSportModels != null && mSportModels.size() > 0) {
                            mSportModels.clear();
                        }
                        if (!json.getString("data").equals("0")) {
                            JSONArray array = new JSONArray(json.getString("data"));
                            mSportModels = new ArrayList<SportModel>();
                            for (int i = 0; i < array.length(); i++) {
                                String sportId = array.getJSONObject(i).getString("sport");
                                SportModel sportModel = new SportModel();
                                sportModel.setSportid(array.getJSONObject(i).getString("team_id"));
                                sportModel.setSportname(array.getJSONObject(i).getString("team_name"));
                                sportModel.setIswoner(array.getJSONObject(i).getInt("is_owner"));
                                sportModel.setTeamlogo(array.getJSONObject(i).getString("team_logo"));
                                sportModel.setSportimage(CommonUtils.sportCheck(sportId));
                                sportModel.setLocation(array.getJSONObject(i).getString("team_location"));
                                String percentage = array.getJSONObject(i).getString("progress").equals("null") ? "0" : array.getJSONObject(i).getString("progress");
                                sportModel.setProgress_percent(percentage);
                                sportModel.setBatchimage(CommonUtils.batchCheck(array.getJSONObject(i).getString("badge")));
                                sportModel.setTeamAbout(array.getJSONObject(i).getString("team_about"));
                                if(String.valueOf(sportModel.getIswoner()).equals(Constants.DefaultText.ONE)) {
                                    if (getIntent()!= null) {
                                        String playerSportId = getIntent().getStringExtra(Constants.BundleKeys.SPORT_ID);
                                        if(playerSportId.equals(sportId)) {
                                            mSportModels.add(sportModel);
                                        }
                                    }
                                }
                            }
                            mPlayerTeamInviteAdapter = new PlayerTeamInviteAdapter(PlayerInviteActivity.this, PlayerInviteActivity.this, mSportModels);
                            mTeamListView.setAdapter(mPlayerTeamInviteAdapter);
                        } else {
                            mEmptyTeamNotificationTextView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                String error = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(error);
                                String msg = jsonObject.getString("message");
                                String code = jsonObject.getString("code");
                                if (!code.equals("500")) {
                                    Toast.makeText(PlayerInviteActivity.this, msg, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(PlayerInviteActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                String error = response.message();
                                Toast.makeText(PlayerInviteActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            String error = response.message();
                            Toast.makeText(PlayerInviteActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mProgressView.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //  Log error here since request failed
                try {
                    if (t instanceof SocketTimeoutException) {
                        Toast.makeText(PlayerInviteActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PlayerInviteActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                    mProgressView.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_player_invite_iv_back:
                finish();
                overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
    }

    @Override
    public void inviteToTeam(String teamId, String teamName) {
        sendInvitePlayerRequest(teamId, teamName);
    }

    private void sendInvitePlayerRequest(String teamId, String teamName) {
        String playerId = "", playerName, sportId;
        if (getIntent().getStringExtra(Constants.BundleKeys.PLAYER_ID) != null) {
            playerId = getIntent().getStringExtra(Constants.BundleKeys.PLAYER_ID);
            playerName = getIntent().getStringExtra(Constants.BundleKeys.PLAYER_NAME);
            sportId = getIntent().getStringExtra(Constants.BundleKeys.SPORT_ID);
            try {
                JSONObject eventJsonObject = new JSONObject();
                eventJsonObject.put("PlayerId", playerId);
                eventJsonObject.put("PlayerName",  playerName);
                eventJsonObject.put("Sport", CommonUtils.sportNameCheck(sportId));
                eventJsonObject.put("TeamOwner", mPreferenceHelper.getString("user_name", ""));
                eventJsonObject.put("InvitedTeam", teamName);
                mMixpanel.track("PlayerInvite", eventJsonObject);
            } catch (JSONException e) {
                Log.e("Ontro", "Unable to add properties to JSONObject", e);
            }
        }
        InvitePlayerRequest invitePlayerRequest = new InvitePlayerRequest();
        invitePlayerRequest.setTeamId(Integer.valueOf(teamId));
        invitePlayerRequest.setPlayerId(Integer.valueOf(playerId));
        Call<ResponseBody> call = mApiInterface.inviteToTeam(mAuthToken, invitePlayerRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                        JSONObject json = new JSONObject(data);
                        Toast.makeText(PlayerInviteActivity.this, "Invite send", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                String error = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(error);
                                String msg = jsonObject.getString("message");
                                String code = jsonObject.getString("code");
                                if (!code.equals("500")) {
                                    Toast.makeText(PlayerInviteActivity.this, msg, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(PlayerInviteActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                String error = response.message();
                                Toast.makeText(PlayerInviteActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            String error = response.message();
                            Toast.makeText(PlayerInviteActivity.this, error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(PlayerInviteActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PlayerInviteActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
