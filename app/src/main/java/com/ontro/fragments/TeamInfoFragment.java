package com.ontro.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.ontro.Constants;
import com.ontro.R;
import com.ontro.SharedObjects;
import com.ontro.TeamDetailActivity;
import com.ontro.customui.ProfileImageView;
import com.ontro.dto.PlayerInviteApprovalRequest;
import com.ontro.dto.SquadInfo;
import com.ontro.dto.TeamRecord;
import com.ontro.rest.ApiClient;
import com.ontro.rest.ApiInterface;
import com.ontro.rest.TeamDetailResponse;
import com.ontro.utils.CommonUtils;
import com.ontro.utils.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Android
 */

public class TeamInfoFragment extends Fragment implements View.OnClickListener {
    private NestedScrollView mScrollView;
    private ProgressBar progress_bar;
    private TeamDetailResponse teamDetailResponse;
    private ImageView level_image;
    private PreferenceHelper preferenceHelper;
    private TextView tv_level, tv_description, tv_matches, tv_won, tv_lost;
    private ApiInterface apiInterface;
    private Dialog progress;
    private LinearLayout mLastGameLayout, mTeamRecordLayout, mInviteAcceptLayout;
    private ImageView mInviteAcceptImageView, mInviteDeclineImageView, mTeamSportTypeImageView;
    private DisplayMetrics dm;
    private View view;
    private MixpanelAPI mMixpanel;
    private String matchInvite;
    private CircularProgressBar mCircularProgressBar;

    public static Fragment newInstance(String playerInviteId, TeamDetailResponse teamDetailResponse) {
        TeamInfoFragment teamInfoFragment = new TeamInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BundleKeys.PLAYER_INVITE_ID, playerInviteId);
        bundle.putSerializable(Constants.BundleKeys.TEAM_DETAIL_RESPONSE, teamDetailResponse);
        teamInfoFragment.setArguments(bundle);
        return teamInfoFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_team_info_layout, container, false);
        mMixpanel = MixpanelAPI.getInstance(getActivity(), getResources().getString(R.string.mixpanel_token));
        preferenceHelper = new PreferenceHelper(getActivity(), Constants.APP_NAME, 0);
        initView();
        if(getArguments() != null) {
            String playerInviteId = getArguments().getString(Constants.BundleKeys.PLAYER_INVITE_ID);
            if (playerInviteId.isEmpty()) {
                mInviteAcceptLayout.setVisibility(View.GONE);
            } else {
                mInviteAcceptLayout.setVisibility(View.VISIBLE);
            }
            teamDetailResponse = (TeamDetailResponse) getArguments().getSerializable(Constants.BundleKeys.TEAM_DETAIL_RESPONSE);
            if(teamDetailResponse != null) {
                showTeamDetails(teamDetailResponse);
            }
        }

        // Fetch team info
        mInviteAcceptImageView.setOnClickListener(this);
        mInviteDeclineImageView.setOnClickListener(this);
        return view;
    }

    private void showTeamDetails(TeamDetailResponse teamDetailResponse) {
        tv_description.setText(teamDetailResponse.getTeamAbout());
        TeamRecord teamRecord = teamDetailResponse.getTeamRecord();
        tv_matches.setText(teamRecord.getTotalMatch());
        tv_won.setText(teamRecord.getWon());
        tv_lost.setText(teamRecord.getLost());
        tv_level.setText("Level " + teamDetailResponse.getLevel());
        int progressPercentage = Integer.valueOf(teamDetailResponse.getProgress());
        progressPercentage = progressPercentage * 20;
        progress_bar.setProgress(progressPercentage);

        mCircularProgressBar.setProgressWithAnimation(progressPercentage, SharedObjects.animationDuration);
        level_image.setImageResource(SharedObjects.GetBadge(Integer.parseInt(teamDetailResponse.getLevel())));
    }

    private void initView() {
        mScrollView = (NestedScrollView) view.findViewById(R.id.mScrollView);
        progress_bar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mInviteAcceptLayout = (LinearLayout) view.findViewById(R.id.fragment_team_info_ll_invite_accept);
        mInviteAcceptImageView = (ImageView) view.findViewById(R.id.fragment_team_info_iv_invite_accept);
        mInviteDeclineImageView = (ImageView) view.findViewById(R.id.fragment_team_info_iv_invite_decline);
        mCircularProgressBar = (CircularProgressBar) getActivity().findViewById(R.id.activity_team_detail_circular_pb);
        tv_level = (TextView) view.findViewById(R.id.txt_level);
        level_image = (ImageView) view.findViewById(R.id.level_badge);
        tv_description = (TextView) view.findViewById(R.id.txt_description);
        tv_matches = (TextView) view.findViewById(R.id.txt_matches);
        tv_won = (TextView) view.findViewById(R.id.txt_won);
        tv_lost = (TextView) view.findViewById(R.id.txt_lost);
        mLastGameLayout = (LinearLayout) view.findViewById(R.id.fragment_team_info_ll_last_game);
        mTeamRecordLayout = (LinearLayout) view.findViewById(R.id.fragment_team_info_ll_team_record);
        progress = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progress.setContentView(R.layout.progressdialog_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progress_bar.setProgressTintList(ColorStateList.valueOf(Color.RED));
        } else {
            progress_bar.getProgressDrawable().setColorFilter(
                    Color.RED, PorterDuff.Mode.SRC_IN);
        }
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible) {
            TeamDetailActivity.info_squad_state = 1;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_team_info_iv_invite_accept:
                progress.show();
                try {
                    JSONObject eventJsonObject = new JSONObject();
                    eventJsonObject.put("UserId", preferenceHelper.getString("user_id", ""));
                    eventJsonObject.put("UserName", preferenceHelper.getString("user_name", ""));
                    eventJsonObject.put("InvitedTeam", teamDetailResponse.getTeamName());
                    eventJsonObject.put("TeamSport", CommonUtils.sportNameCheck(teamDetailResponse.getSport()));
                    eventJsonObject.put("status", "Accepted");
                    mMixpanel.track("PlayerInviteApproval", eventJsonObject);
                } catch (JSONException e) {
                    Log.e("Ontro", "Unable to add properties to JSONObject", e);
                }
                onGotInviteResponseFromPlayer(Constants.DefaultText.INVITE_ACCEPT_STATUS);
                break;
            case R.id.fragment_team_info_iv_invite_decline:
                try {
                    JSONObject eventJsonObject = new JSONObject();
                    eventJsonObject.put("UserId", preferenceHelper.getString("user_id", ""));
                    eventJsonObject.put("UserName", preferenceHelper.getString("user_name", ""));
                    eventJsonObject.put("InvitedTeam", teamDetailResponse.getTeamName());
                    eventJsonObject.put("Sport", CommonUtils.sportNameCheck(teamDetailResponse.getSport()));
                    eventJsonObject.put("status", "Rejected");
                    mMixpanel.track("PlayerInviteApproval", eventJsonObject);
                } catch (JSONException e) {
                    Log.e("Ontro", "Unable to add properties to JSONObject", e);
                }
                onGotInviteResponseFromPlayer(Constants.DefaultText.INVITE_DECLINE_STATUS);
                break;
        }
    }

    private void onGotInviteResponseFromPlayer(final int inviteStatus) {

        if(getArguments() != null) {
            String playerInviteId = getArguments().getString(Constants.BundleKeys.PLAYER_INVITE_ID);
            if (!playerInviteId.isEmpty()) {
                apiInterface = ApiClient.getClient().create(ApiInterface.class);
                String authToken = "Bearer " + preferenceHelper.getString("user_token", "");
                PlayerInviteApprovalRequest playerInviteApprovalRequest = new PlayerInviteApprovalRequest();
                playerInviteApprovalRequest.setInviteId(Integer.valueOf(playerInviteId));
                playerInviteApprovalRequest.setInviteStatus(inviteStatus);
                Call<ResponseBody> call = apiInterface.getInviteApproval(authToken, playerInviteApprovalRequest);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.body() != null && response.code() == 200) {
                            try {
                                String data = response.body().string();
                                Log.d("RESPONSE", data);
                                JSONObject json = new JSONObject(data);
                                String message = json.getString("message");
                                mInviteAcceptLayout.setVisibility(View.GONE);
                                if(inviteStatus == Constants.DefaultText.INVITE_ACCEPT_STATUS) {
                                    FirebaseMessaging.getInstance().subscribeToTopic(teamDetailResponse.getTeamId());
                                }
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            CommonUtils.ErrorHandleMethod(getActivity(), response);
                        }
                        if (progress != null && progress.isShowing()) {
                            progress.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        CommonUtils.ServerFailureHandleMethod(getActivity(), t);
                        if (progress != null && progress.isShowing()) {
                            progress.dismiss();
                        }
                    }
                });

            }
        }



    }
}
