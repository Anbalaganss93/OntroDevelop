package com.ontro.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.ontro.Constants;
import com.ontro.PlayerProfileActivity;
import com.ontro.R;
import com.ontro.dto.BadgeLevel;
import com.ontro.dto.ExploreModel;
import com.ontro.dto.InvitePlayerRequest;
import com.ontro.dto.MatchStats;
import com.ontro.dto.PlayerInviteCancelRequest;
import com.ontro.dto.PlayerInviteStatus;
import com.ontro.dto.PlayerQuickViewResponse;
import com.ontro.dto.RemovePlayerFromTeam;
import com.ontro.rest.ApiClient;
import com.ontro.rest.ApiInterface;
import com.ontro.utils.CommonUtils;
import com.ontro.utils.PreferenceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import cheekiat.slideview.SlideView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by IDEOMIND02 on 5/5/2017.
 */

public class PlayerQuickViewFragment extends DialogFragment {
    private PreferenceHelper preferenceHelper;
    private String authToken;
    private ApiInterface apiInterface;
    private Dialog mProgressDialog;

    public static PlayerQuickViewFragment newInstance(String playerId, String sportId, String teamId, String isOwner) {
        PlayerQuickViewFragment fragment = new PlayerQuickViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BundleKeys.PLAYER_ID, playerId);
        bundle.putString(Constants.BundleKeys.SPORT_ID, sportId);
        bundle.putString(Constants.BundleKeys.TEAM_ID, teamId);
        bundle.putString(Constants.BundleKeys.IS_OWNER, isOwner);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static PlayerQuickViewFragment newInstance(ExploreModel exploreModel, String teamId, String teamName) {
        PlayerQuickViewFragment fragment = new PlayerQuickViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BundleKeys.PLAYER_ID, exploreModel.getExploreId());
        bundle.putString(Constants.BundleKeys.SPORT_ID, exploreModel.getExploreSport());
        bundle.putString(Constants.BundleKeys.TEAM_ID, teamId);
        bundle.putString(Constants.BundleKeys.TEAM_NAME, teamName);
        bundle.putSerializable(Constants.BundleKeys.EXPLORE_MODEL, exploreModel);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mProgressDialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setContentView(R.layout.progressdialog_layout);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View playerQuickView = getActivity().getLayoutInflater().inflate(R.layout.fragment_player_quick_view, null);
        ImageView mBackImageView = (ImageView) playerQuickView.findViewById(R.id.fragment_player_quick_view_iv_back);
        mBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        if (CommonUtils.isNetworkAvailable(getActivity())) {
            mProgressDialog.show();
            getPlayerQuickView(playerQuickView);
        } else {
            Toast.makeText(getActivity(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }
        builder.setView(playerQuickView);
        AlertDialog mPlayerQuickViewDialog = builder.create();
        mPlayerQuickViewDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mPlayerQuickViewDialog.setCanceledOnTouchOutside(true);
        mPlayerQuickViewDialog.show();
        return mPlayerQuickViewDialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void getPlayerQuickView(final View playerQuickView) {
        preferenceHelper = new PreferenceHelper(getActivity(), Constants.APP_NAME, 0);
        authToken = "Bearer " + preferenceHelper.getString("user_token", "");
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        if (getArguments() != null) {
            String playerId = getArguments().getString(Constants.BundleKeys.PLAYER_ID);
            String sportId = getArguments().getString(Constants.BundleKeys.SPORT_ID);
            Call<ResponseBody> call = apiInterface.getPlayerQuickViewInfo(authToken, Integer.valueOf(playerId), sportId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.body() != null && response.code() == 200) {
                        try {
                            mProgressDialog.dismiss();
                            String data = response.body().string();
                            Log.d("RESPONSE", data);
                            JSONObject json = new JSONObject(data);
                            JSONObject dataJson = new JSONObject(json.getString("data"));
                            PlayerQuickViewResponse playerQuickViewResponse = new PlayerQuickViewResponse();
                            playerQuickViewResponse.setPlayerId(dataJson.getInt("player_id"));
                            playerQuickViewResponse.setPlayerName(dataJson.getString("player_name"));
                            playerQuickViewResponse.setProfileImage(dataJson.getString("profile_image"));
                            playerQuickViewResponse.setHeight(dataJson.getInt("height"));
                            playerQuickViewResponse.setProgress(dataJson.getInt("progress"));
                            playerQuickViewResponse.setLocationName(dataJson.getString("location_name"));
                            playerQuickViewResponse.setCityName(dataJson.getString("city_name"));
                            JSONObject badgeLevelObject = new JSONObject(dataJson.getString("badge_level"));
                            BadgeLevel badgeLevel = new BadgeLevel();
                            badgeLevel.setSport(badgeLevelObject.getInt("over_all_points"));
                            badgeLevel.setBadge(badgeLevelObject.getInt("badge"));
                            badgeLevel.setLevel(badgeLevelObject.getInt("level"));
                            playerQuickViewResponse.setBadgeLevel(badgeLevel);
                            JSONObject matchStatistics = new JSONObject(dataJson.getString("match_stats"));
                            MatchStats matchStats = new MatchStats();
                            matchStats.setTotalMatch(matchStatistics.getInt("total_match"));
                            matchStats.setWon(matchStatistics.getInt("won"));
                            matchStats.setLost(matchStatistics.getInt("lost"));
                            playerQuickViewResponse.setMatchStats(matchStats);
                            showPlayerQuickView(playerQuickView, playerQuickViewResponse);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        mProgressDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    try {
                        mProgressDialog.dismiss();
                        if (t instanceof SocketTimeoutException) {
                            Toast.makeText(getActivity(), R.string.timeout, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void showPlayerQuickView(View playerQuickView, PlayerQuickViewResponse playerQuickViewResponse) {
        final CircularImageView mPlayerImageView = (CircularImageView) playerQuickView.findViewById(R.id.fragment_player_quick_view_civ_player_image);
        LinearLayout mTournamentLayout = (LinearLayout) playerQuickView.findViewById(R.id.fragment_player_quick_view_ll_tournaments);
        LinearLayout mPlayerSpecificationLayout = (LinearLayout) playerQuickView.findViewById(R.id.fragment_player_quick_view_ll_specification);
        Button mViewProfileButton = (Button) playerQuickView.findViewById(R.id.fragment_player_quick_view_btn_view_profile);
        final TextView mPlayerNameView = (TextView) playerQuickView.findViewById(R.id.fragment_player_quick_view_tv_player_name);
        final TextView mPlayerLocationView = (TextView) playerQuickView.findViewById(R.id.fragment_player_quick_view_tv_player_ranking);
        final TextView mPlayerWonMatchesView = (TextView) playerQuickView.findViewById(R.id.fragment_player_quick_view_tv_won_status_value);
        final TextView mPlayerNoOfMatchesView = (TextView) playerQuickView.findViewById(R.id.fragment_player_quick_view_tv_no_of_match_value);
        final TextView mPlayerLostMatchesView = (TextView) playerQuickView.findViewById(R.id.fragment_player_quick_view_tv_lost_status_value);
        TextView mPlayerLevelView = (TextView) playerQuickView.findViewById(R.id.fragment_player_quick_view_tv_level);
        TextView mTournamentWinnerStatusView = (TextView) playerQuickView.findViewById(R.id.fragment_player_quick_view_tv_tournament_won_status_value);
        TextView mPlayerNoOfTournamentView = (TextView) playerQuickView.findViewById(R.id.fragment_player_quick_view_tv_no_of_tournament_value);
        TextView mTournamentRunnerStatusView = (TextView) playerQuickView.findViewById(R.id.fragment_player_quick_view_tv_runner_status_value);
        TextView mPlayerAverageView = (TextView) playerQuickView.findViewById(R.id.fragment_player_quick_view_tv_average_value);
        TextView mPlayerCenturyView = (TextView) playerQuickView.findViewById(R.id.fragment_player_quick_view_tv_century_value);
        TextView mPlayerHalfCenturyView = (TextView) playerQuickView.findViewById(R.id.fragment_player_quick_view_tv_half_century_value);
        TextView mPlayerMomView = (TextView) playerQuickView.findViewById(R.id.fragment_player_quick_view_tv_mom_value);
        ProgressBar mProgressBar = (ProgressBar) playerQuickView.findViewById(R.id.fragment_player_quick_view_pb_level);
        final SlideView mSlideView = (SlideView) playerQuickView.findViewById(R.id.fragment_player_quick_view_slide_view);
        final TextView mRemovePlayerFromTeam = (TextView) playerQuickView.findViewById(R.id.fragment_player_quick_view_iv_remove_player);
        mRemovePlayerFromTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removePlayerFromTeam(mRemovePlayerFromTeam);
            }
        });
        String playerInviteId = "";
        if (getArguments().getString(Constants.BundleKeys.IS_OWNER) != null) {
            if (getArguments().getString(Constants.BundleKeys.IS_OWNER).equals(Constants.DefaultText.ONE)) {
                String playerId = getArguments().getString(Constants.BundleKeys.PLAYER_ID);
                String userId = preferenceHelper.getString("user_id", "");
                if (!playerId.equals(userId)) {
                    mRemovePlayerFromTeam.setVisibility(View.VISIBLE);
                } else {
                    mRemovePlayerFromTeam.setVisibility(View.GONE);
                }
            } else {
                mRemovePlayerFromTeam.setVisibility(View.GONE);
            }
        } else {
            mRemovePlayerFromTeam.setVisibility(View.GONE);
        }

        if (getArguments().getSerializable(Constants.BundleKeys.EXPLORE_MODEL) != null) {
            mSlideView.setVisibility(View.VISIBLE);
            ExploreModel exploreModel = (ExploreModel) getArguments().getSerializable(Constants.BundleKeys.EXPLORE_MODEL);
            List<PlayerInviteStatus> playerInviteStatuses = exploreModel.getInviteStatuses();
            int count = 0;
            for (int i = 0; i < playerInviteStatuses.size(); i++) {
                PlayerInviteStatus inviteStatus = playerInviteStatuses.get(i);
                if (getArguments().getString(Constants.BundleKeys.TEAM_ID).equals(inviteStatus.getTeamId())) {
                    if (inviteStatus.getInviteStatus().equals(Constants.DefaultText.ONE)) {
                        mSlideView.setVisibility(View.GONE);
                        break;
                    } else if (inviteStatus.getInviteStatus().equals(Constants.DefaultText.ZERO)) {
                        mSlideView.setText(Constants.DefaultText.SLIDE_TO_CANCEL_REQUEST);
                        playerInviteId = inviteStatus.getInviteId();
                    }
                } else {
                    count++;
                }
            }
            if (count == playerInviteStatuses.size()) {
                mSlideView.setText(Constants.DefaultText.SLIDE_TO_INVITE_TO_TEAM);
            }

        } else {
            mSlideView.setVisibility(View.GONE);
        }

        final String finalPlayerInviteId = playerInviteId;
        mSlideView.setOnFinishListener(new SlideView.OnFinishListener() {
            @Override
            public void onFinish() {
                if (mSlideView.getText().equals(Constants.DefaultText.SLIDE_TO_INVITE_TO_TEAM)) {
                    invitePlayerToTeam(mSlideView);
                } else if ((mSlideView.getText().equals(Constants.DefaultText.SLIDE_TO_INVITE_TO_TEAM))) {
                    onCancelPlayerInvite(finalPlayerInviteId, mSlideView);
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mProgressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
        } else {
            mProgressBar.getProgressDrawable().setColorFilter(
                    Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
        }

        if (playerQuickViewResponse != null) {
            mPlayerNameView.setText(playerQuickViewResponse.getPlayerName());
            mPlayerLocationView.setText(playerQuickViewResponse.getLocationName());
            if (getActivity() != null) {
                if (playerQuickViewResponse.getProfileImage() != null) {
                    Glide.with(getActivity()).load(playerQuickViewResponse.getProfileImage()).placeholder(R.drawable.profiledefaultimg).dontAnimate().into(mPlayerImageView);
                } else {
                    Glide.with(getActivity()).load(Constants.PLAYER_DEFAULT_IMAGE_URL).placeholder(R.drawable.profiledefaultimg).dontAnimate().into(mPlayerImageView);
                }
            }

            MatchStats matchStats = playerQuickViewResponse.getMatchStats();
            mPlayerWonMatchesView.setText(String.valueOf(matchStats.getWon()));
            mPlayerNoOfMatchesView.setText(String.valueOf(matchStats.getTotalMatch()));
            mPlayerLostMatchesView.setText(String.valueOf(matchStats.getLost()));
            BadgeLevel badgeLevel = playerQuickViewResponse.getBadgeLevel();
            int progress = playerQuickViewResponse.getProgress() * 20;
            mProgressBar.setProgress(progress);
            mPlayerLevelView.setText("Level " + badgeLevel.getLevel());
        }

        if (getArguments() != null) {
            mViewProfileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateToPlayerProfile();
                    dismiss();
                }
            });
        }

    }

    private void invitePlayerToTeam(final SlideView mSlideView) {
        ExploreModel exploreModel = (ExploreModel) getArguments().getSerializable(Constants.BundleKeys.EXPLORE_MODEL);
        String playerId = exploreModel.getExploreId();
        String playerName = exploreModel.getExploreName();
        String sportId = exploreModel.getExploreSport();
        MixpanelAPI mMixpanel = MixpanelAPI.getInstance(getActivity(), getResources().getString(R.string.mixpanel_token));
        try {
            JSONObject eventJsonObject = new JSONObject();
            eventJsonObject.put("PlayerId", playerId);
            eventJsonObject.put("PlayerName", playerName);
            eventJsonObject.put("Sport", CommonUtils.sportNameCheck(sportId));
            eventJsonObject.put("TeamOwner", preferenceHelper.getString("user_name", ""));
            eventJsonObject.put("InvitedTeam", getArguments().getString(Constants.BundleKeys.TEAM_NAME));
            mMixpanel.track("PlayerInvite", eventJsonObject);
        } catch (JSONException e) {
            Log.e("Ontro", "Unable to add properties to JSONObject", e);
        }

        InvitePlayerRequest invitePlayerRequest = new InvitePlayerRequest();
        invitePlayerRequest.setTeamId(Integer.valueOf(getArguments().getString(Constants.BundleKeys.TEAM_ID)));
        invitePlayerRequest.setPlayerId(Integer.valueOf(playerId));
        Call<ResponseBody> call = apiInterface.inviteToTeam(authToken, invitePlayerRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                        JSONObject json = new JSONObject(data);
                        Toast.makeText(getActivity(), Constants.Messages.PLAYER_INVITE_SENT_SUCCESSFULLY, Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mSlideView.reset();
                            }
                        }, 300);
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
            }
        });
    }

    private void onCancelPlayerInvite(String finalPlayerInviteId, final SlideView mSlideView) {
        PlayerInviteCancelRequest inviteCancelRequest = new PlayerInviteCancelRequest();
        inviteCancelRequest.setInviteId(Integer.valueOf(finalPlayerInviteId));
        Call<ResponseBody> call = apiInterface.cancelPlayerInviteRequest(authToken, inviteCancelRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                        JSONObject json = new JSONObject(data);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mSlideView.reset();
                            }
                        }, 300);
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
            }
        });
    }

    private void removePlayerFromTeam(final TextView mRemovePlayerFromTeam) {
        mProgressDialog.show();
        int playerId = Integer.valueOf(getArguments().getString(Constants.BundleKeys.PLAYER_ID));
        int teamId = Integer.valueOf(getArguments().getString(Constants.BundleKeys.TEAM_ID));
        RemovePlayerFromTeam removePlayerFromTeam = new RemovePlayerFromTeam();
        removePlayerFromTeam.setTeamId(teamId);
        removePlayerFromTeam.setPlayerId(playerId);
        Call<ResponseBody> call = apiInterface.removePlayerFromTeam(authToken, removePlayerFromTeam);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                        JSONObject json = new JSONObject(data);
                        Toast.makeText(getActivity(), "Player removed", Toast.LENGTH_SHORT).show();
                        dismiss();
                        mRemovePlayerFromTeam.setVisibility(View.GONE);
                        getActivity().finish();
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                String error = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(error);
                                String msg = null;
                                msg = jsonObject.getString("message");
                                String code = jsonObject.getString("code");
                                if (!code.equals("500")) {
                                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), R.string.error500, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getActivity(), R.string.error500, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String error = response.message();
                            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mProgressDialog.dismiss();
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
                mProgressDialog.dismiss();
            }
        });

    }

    private void navigateToPlayerProfile() {
        Intent intent = new Intent(getActivity(), PlayerProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BundleKeys.PLAYER_TEAM_LIST, getArguments().getSerializable(Constants.BundleKeys.EXPLORE_MODEL));
        bundle.putString(Constants.BundleKeys.TEAM_ID, getArguments().getString(Constants.BundleKeys.TEAM_ID));
        bundle.putString(Constants.BundleKeys.TEAM_NAME, getArguments().getString(Constants.BundleKeys.TEAM_NAME));
        intent.putExtra(Constants.BundleKeys.PLAYER_ID, getArguments().getString(Constants.BundleKeys.PLAYER_ID));
        intent.putExtra(Constants.BundleKeys.SPORT_ID, getArguments().getString(Constants.BundleKeys.SPORT_ID));
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
