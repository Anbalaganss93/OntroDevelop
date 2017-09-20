package com.ontro;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.ontro.adapters.PlayerInviteAcceptAdapter;
import com.ontro.dto.PlayerInviteApprovalRequest;
import com.ontro.dto.PlayerInviteResponse;
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

public class PlayerInviteApprovalActivity extends AppCompatActivity implements View.OnClickListener,
        PlayerInviteAcceptAdapter.PlayerInviteAcceptListener {
    private ImageView mBackImageView;
    private RecyclerView mTeamListView;
    private TextView mEmptyTeamNotificationTextView;
    private PreferenceHelper mPreferenceHelper;
    private ApiInterface mApiInterface;
    private String mAuthToken;
    private Dialog mProgressView;
    private PlayerInviteAcceptAdapter playerInviteAcceptAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_invite_approval);
        overridePendingTransition(R.anim.slide_in_right, R.anim.pause);
        init();
        initView();
        initProgressView();
        setListener();
    }

    private void init() {
        mPreferenceHelper = new PreferenceHelper(this, Constants.APP_NAME, 0);
        mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        mAuthToken = "Bearer " + mPreferenceHelper.getString("user_token", "");
    }

    private void initView() {
        mBackImageView = (ImageView) findViewById(R.id.activity_player_invite_approval_iv_back);
        mTeamListView = (RecyclerView) findViewById(R.id.activity_player_invite_approval_rv);
        mEmptyTeamNotificationTextView = (TextView) findViewById(R.id.activity_player_invite_approval_tv_no_team);
    }

    private void initProgressView() {
        mProgressView = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        mProgressView.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressView.setContentView(R.layout.progressdialog_layout);
    }

    private void setListener() {
        mBackImageView.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CommonUtils.isNetworkAvailable(this)) {
            mProgressView.show();
            getTeamInvites();
        } else {
            Toast.makeText(this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }
    }

    private void getTeamInvites() {
        String userId = mPreferenceHelper.getString("user_id", "");
        if (!userId.equals("")) {
            Call<ResponseBody> call = mApiInterface.invitePlayerToTeam(mAuthToken, Integer.valueOf(userId));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.body() != null && response.code() == 200) {
                            String data = response.body().string();
                            Log.d("RESPONSE", data);
                            JSONObject json = new JSONObject(data);
                            List<PlayerInviteResponse> mPlayerInviteResponses = new ArrayList<>();
                            JSONArray inviteArray = new JSONArray(json.getString("data"));
                            if (inviteArray != null) {
                                for (int i = 0; i < inviteArray.length(); i++) {
                                    PlayerInviteResponse playerInviteResponse = new PlayerInviteResponse();
                                    playerInviteResponse.setInviteId(inviteArray.getJSONObject(i).getInt("invite_id"));
                                    playerInviteResponse.setTeamName(inviteArray.getJSONObject(i).getString("team_name"));
                                    playerInviteResponse.setTeamId(inviteArray.getJSONObject(i).getInt("team_id"));
                                    playerInviteResponse.setTeamLogo(inviteArray.getJSONObject(i).getString("team_logo"));
                                    playerInviteResponse.setInviteStatus(inviteArray.getJSONObject(i).getInt("invite_status"));
                                    playerInviteResponse.setTeamSport(inviteArray.getJSONObject(i).getInt("team_sport"));
                                    String inviteRequestDate = inviteArray.getJSONObject(i).getString("created_at");
                                    playerInviteResponse.setCreatedAt(CommonUtils.timeconverter(inviteRequestDate));
                                    mPlayerInviteResponses.add(playerInviteResponse);
                                }
                                if(mPlayerInviteResponses.size() == 0) {
                                    mEmptyTeamNotificationTextView.setVisibility(View.VISIBLE);
                                } else {
                                    mEmptyTeamNotificationTextView.setVisibility(View.GONE);
                                }
                                playerInviteAcceptAdapter = new PlayerInviteAcceptAdapter(PlayerInviteApprovalActivity.this, PlayerInviteApprovalActivity.this, mPlayerInviteResponses);
                                mTeamListView.setAdapter(playerInviteAcceptAdapter);
                            }
                        } else {
                            if (response.errorBody() != null) {
                                try {
                                    String error = response.errorBody().string();
                                    JSONObject jsonObject = new JSONObject(error);
                                    String msg = jsonObject.getString("message");
                                    String code = jsonObject.getString("code");
                                    if (!code.equals("500")) {
                                        Toast.makeText(PlayerInviteApprovalActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(PlayerInviteApprovalActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(PlayerInviteApprovalActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                String error = response.message();
                                Toast.makeText(PlayerInviteApprovalActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                    mProgressView.dismiss();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    try {
                        if (t instanceof SocketTimeoutException) {
                            Toast.makeText(PlayerInviteApprovalActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PlayerInviteApprovalActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                        mProgressView.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_player_invite_approval_iv_back:
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
    public void onRequestAcceptClicked(PlayerInviteResponse playerInviteResponse, int position) {
        mProgressView.show();
        onGotInviteResponseFromPlayer(playerInviteResponse.getTeamId(), playerInviteResponse.getInviteId(), Constants.DefaultText.INVITE_ACCEPT_STATUS);
    }

    @Override
    public void onRequestCancelClicked(PlayerInviteResponse playerInviteResponse, int position) {
        mProgressView.show();
        onGotInviteResponseFromPlayer(playerInviteResponse.getTeamId(), playerInviteResponse.getInviteId(), Constants.DefaultText.INVITE_DECLINE_STATUS);
    }

    private void onGotInviteResponseFromPlayer(final int teamId, int inviteId, final int inviteStatus) {
        PlayerInviteApprovalRequest playerInviteApprovalRequest = new PlayerInviteApprovalRequest();
        playerInviteApprovalRequest.setInviteId(inviteId);
        playerInviteApprovalRequest.setInviteStatus(inviteStatus);
        Call<ResponseBody> call = mApiInterface.getInviteApproval(mAuthToken, playerInviteApprovalRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null && response.code() == 200) {
                    try {
                        mProgressView.dismiss();
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                        JSONObject json = new JSONObject(data);
                        String message = json.getString("message");
                        getTeamInvites();
                        if(inviteStatus == Constants.DefaultText.INVITE_ACCEPT_STATUS) {
                            FirebaseMessaging.getInstance().subscribeToTopic(String.valueOf(teamId));
                        }
                        Toast.makeText(PlayerInviteApprovalActivity.this, message, Toast.LENGTH_SHORT).show();
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        mProgressView.dismiss();
                        String error = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(error);
                        String msg = jsonObject.getString("message");
                        Toast.makeText(PlayerInviteApprovalActivity.this, msg, Toast.LENGTH_SHORT).show();

                        if (playerInviteAcceptAdapter != null){
                            playerInviteAcceptAdapter.notifyDataSetChanged();
                        }

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                try {
                    if (t instanceof SocketTimeoutException) {
                        Toast.makeText(PlayerInviteApprovalActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PlayerInviteApprovalActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                    mProgressView.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onItemViewClicked(PlayerInviteResponse playerInviteResponse, int position) {
        Intent intent = new Intent(this, TeamDetailActivity.class);
        SharedObjects.id = String.valueOf(playerInviteResponse.getTeamId());
        intent.putExtra(Constants.BundleKeys.INVITE_ID, String.valueOf(playerInviteResponse.getInviteId()));
        startActivity(intent);
    }
}
