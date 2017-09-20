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

import com.ontro.adapters.NotificationAdapter;
import com.ontro.dto.NotificationResponse;
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

public class NotificationActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mBackImageView;
    private RecyclerView mNotificationRecyclerView;
    private TextView mNoNotificationView;
    private Dialog mProgressBar;
    private PreferenceHelper mPreferenceHelper;
    private ApiInterface mApiInterface;
    private String mAuthToken;
    private NotificationAdapter mNotificationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        overridePendingTransition(R.anim.slide_in_right, R.anim.pause);
        initView();
        initializeProgressBar();
        if (CommonUtils.isNetworkAvailable(this)) {
            mProgressBar.show();
            getNotificationHistory();
        } else {
            Toast.makeText(this, Constants.INTERNET_ERROR, Toast.LENGTH_LONG).show();
        }
        mBackImageView.setOnClickListener(this);
    }

    private void initView() {
        mBackImageView = (ImageView) findViewById(R.id.activity_notification_back_iv);
        mNotificationRecyclerView = (RecyclerView) findViewById(R.id.activity_notification_rv);
        mNoNotificationView = (TextView) findViewById(R.id.activity_notification_tv_nil);
    }

    private void initializeProgressBar() {
        mProgressBar = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        mProgressBar.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressBar.setContentView(R.layout.progressdialog_layout);
    }

    private void getNotificationHistory() {
        mPreferenceHelper = new PreferenceHelper(this, Constants.APP_NAME, 0);
        mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        mAuthToken = "Bearer " + mPreferenceHelper.getString("user_token", "");
        String userId = mPreferenceHelper.getString("user_id", "");
        if (!userId.equals("")) {
            Call<ResponseBody> call = mApiInterface.getNotificationHistory(mAuthToken, userId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.code() == 200 && response.body() != null) {
                            String data = response.body().string();
                            Log.d("RESPONSE", data);
                            JSONObject jsonObject = new JSONObject(data);
                            JSONArray dataArray = new JSONArray(jsonObject.getString("data"));
                            List<NotificationResponse> mNotifications = new ArrayList<>();
                            if (dataArray.length() > 0) {
                                for (int i = 0; i < dataArray.length(); i++) {
                                    NotificationResponse notificationResponse = new NotificationResponse();
                                    notificationResponse.setTitle(dataArray.getJSONObject(i).getString("title"));
                                    notificationResponse.setContent(dataArray.getJSONObject(i).getString("content"));
                                    notificationResponse.setClickAction(dataArray.getJSONObject(i).getString("click_action"));
                                    String notificationDate = dataArray.getJSONObject(i).getString("created_at");
                                    notificationResponse.setCreatedAt(CommonUtils.timeconverter(notificationDate));
                                    mNotifications.add(notificationResponse);
                                }
                                setAdapter(mNotifications);
                            } else {
                                mNoNotificationView.setVisibility(View.VISIBLE);
                            }
                        } else {
                            if (response.errorBody() != null) {
                                String error = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(error);
                                String msg = jsonObject.getString("message");
                                Toast.makeText(NotificationActivity.this, msg, Toast.LENGTH_SHORT).show();
                            } else {
                                String error = response.message();
                                Toast.makeText(NotificationActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                    if (mProgressBar.isShowing()) {
                        mProgressBar.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    try {
                        if (t instanceof SocketTimeoutException) {
                            Toast.makeText(NotificationActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(NotificationActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (mProgressBar.isShowing()) {
                        mProgressBar.dismiss();
                    }
                }
            });
        }
    }

    private void setAdapter(List<NotificationResponse> mNotifications) {
        mNotificationAdapter = new NotificationAdapter(mNotifications);
        mNotificationRecyclerView.setAdapter(mNotificationAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_notification_back_iv:
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

 /*   @Override
    public void onRequestAcceptClicked(NotificationResponse notificationResponse, int position) {
        mProgressBar.show();
        onGotInviteResponseFromPlayer(notificationResponse.getInviteId(), Constants.DefaultText.INVITE_ACCEPT_STATUS);
    }

    @Override
    public void onRequestCancelClicked(NotificationResponse notificationResponse, int position) {
        mProgressBar.show();
        onGotInviteResponseFromPlayer(notificationResponse.getInviteId(), Constants.DefaultText.INVITE_DECLINE_STATUS);
    }

    private void onGotInviteResponseFromPlayer(Integer inviteId, Integer inviteStatus) {
        PlayerInviteApprovalRequest playerInviteApprovalRequest = new PlayerInviteApprovalRequest();
        playerInviteApprovalRequest.setInviteId(inviteId);
        playerInviteApprovalRequest.setInviteStatus(inviteStatus);
        Call<ResponseBody> call = mApiInterface.getInviteApproval(mAuthToken, playerInviteApprovalRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null && response.code() == 200) {
                    try {
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                        JSONObject json = new JSONObject(data);
                        String message = json.getString("message");
                        getNotificationHistory();
                        Toast.makeText(NotificationActivity.this, message, Toast.LENGTH_SHORT).show();
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        mProgressBar.dismiss();
                        String error = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(error);
                        String msg = jsonObject.getString("message");
                        Toast.makeText(NotificationActivity.this, msg, Toast.LENGTH_SHORT).show();

                        if (mNotificationAdapter != null){
                            mNotificationAdapter.notifyDataSetChanged();
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
                        Toast.makeText(NotificationActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NotificationActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                    mProgressBar.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onItemViewClicked(NotificationResponse notificationResponse, int position) {
        Intent intent = new Intent(this, TeamDetailActivity.class);
        SharedObjects.id = String.valueOf(notificationResponse.getTeamId());
        intent.putExtra(Constants.BundleKeys.INVITE_ID, notificationResponse.getInviteId().toString());
        startActivity(intent);
    }*/
}
