package com.ontro;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ontro.customui.ProfileImageView;
import com.ontro.dto.MatchRequestModel;
import com.ontro.dto.MatchRequestResponseModel;
import com.ontro.dto.SquadInfo;
import com.ontro.dto.TeamEditRequestModel;
import com.ontro.dto.TeamNameRequest;
import com.ontro.dto.TeamRecord;
import com.ontro.fragments.TeamInfoFragment;
import com.ontro.fragments.TeamSquadFragment;
import com.ontro.rest.ApiClient;
import com.ontro.rest.ApiInterface;
import com.ontro.rest.TeamDetailResponse;
import com.ontro.utils.CommonUtils;
import com.ontro.utils.PreferenceHelper;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeamDetailActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener,
        AppBarLayout.OnOffsetChangedListener, View.OnClickListener {
    public static int info_squad_state = 1;
    private Toolbar mToolbar;
    private TextView mPlayerOrTeamLocation;
    private TabLayout mPlayerOrTeamDetailTab;
    private ViewPager mPlayerOrTeamViewPager;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private AppBarLayout mAppBarLayout;
    private Button mMatchOrPlayerInviteButton, mJoinTeamButton;
    private PreferenceHelper preferenceHelper;
    private ApiInterface mApiInterface;
    private ProfileImageView mTeamImageView, mEditTeamImageView;
    private String teamLogo;
    private String mAuthToken;
    private Dialog mProgressDialog;
    private ImageView mTeamSportImageView;
    private TeamDetailResponse teamDetailResponse;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_detail);
        overridePendingTransition(R.anim.slide_in_right, R.anim.pause);
        initView();
        setUpViewPagerAdapter();
        integrateViewPagerWithTabLayout();
        setListener();
        setTypeFace();
    }

    private void initView() {
        preferenceHelper = new PreferenceHelper(TeamDetailActivity.this, Constants.APP_NAME, 0);
        mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        mAuthToken = "Bearer " + preferenceHelper.getString("user_token", "");
        mUserId = preferenceHelper.getString("user_id", "");
        mToolbar = (Toolbar) findViewById(R.id.activity_team_detail_toolbar);
        mPlayerOrTeamLocation = (TextView) findViewById(R.id.activity_team_detail_tv_player_or_team_location);
        mPlayerOrTeamDetailTab = (TabLayout) findViewById(R.id.activity_team_detail_tl);
        mMatchOrPlayerInviteButton = (Button) findViewById(R.id.activity_team_detail_btn_invite_accept_or_request_match);
        mJoinTeamButton = (Button) findViewById(R.id.activity_team_detail_btn_join_team_or_cancel_request);
        mPlayerOrTeamViewPager = (ViewPager) findViewById(R.id.activity_team_detail_view_pager);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.activity_team_detail_collapsing_toolbar);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.activity_team_detail_appbar_layout);
        mTeamSportImageView = (ImageView) findViewById(R.id.activity_team_detail_iv_sport_type);
        mTeamImageView = (ProfileImageView) findViewById(R.id.activity_team_detail_iv_player_or_team);
        mProgressDialog = new Dialog(TeamDetailActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setContentView(R.layout.progressdialog_layout);
        Typeface typeface = Typeface.createFromAsset(TeamDetailActivity.this.getAssets(), "fonts/roboto_regular.ttf");
        mMatchOrPlayerInviteButton.setTypeface(typeface);
        mJoinTeamButton.setTypeface(typeface);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            mToolbar.setTitle("");
            mToolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        }
        Intent intent = getIntent();
        if (intent.getStringExtra(Constants.BundleKeys.INVITE_TO_MATCH) != null) {
            mMatchOrPlayerInviteButton.setText(getResources().getString(R.string.invite_to_match));
            mMatchOrPlayerInviteButton.setVisibility(View.VISIBLE);
        } else if (intent.getExtras().getSerializable(Constants.BundleKeys.MY_MATCH) != null) {
            mMatchOrPlayerInviteButton.setText(getResources().getString(R.string.accept_schedule_match));
            mMatchOrPlayerInviteButton.setVisibility(View.VISIBLE);
        } else {
            mMatchOrPlayerInviteButton.setVisibility(View.GONE);
        }

    }

    private void setUpViewPagerAdapter() {
        try {
            Intent intent = getIntent();
            String playerInviteId = "";
            if (intent.getStringExtra(Constants.BundleKeys.INVITE_ID) != null) {
                playerInviteId = intent.getStringExtra(Constants.BundleKeys.INVITE_ID);
            }
            String userCanRequestMatch = "";
            if (intent.getStringExtra(Constants.BundleKeys.INVITE_TO_MATCH) != null) {
                userCanRequestMatch = intent.getStringExtra(Constants.BundleKeys.INVITE_TO_MATCH);
            }
            if (CommonUtils.isNetworkAvailable(TeamDetailActivity.this)) {
                GetTeamDetails(playerInviteId, userCanRequestMatch);
            } else {
                Toast.makeText(TeamDetailActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
            }

        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void GetTeamDetails(final String playerInviteId, final String userCanRequestMatch) {
        mProgressDialog.show();
        Call<ResponseBody> call = mApiInterface.FetchTeamInfo(mAuthToken, SharedObjects.id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call1, Response<ResponseBody> response) {

                if (response.body() != null && response.code() == 200) {
                    try {
                        String user = response.body().string();
                        JSONObject jsonObject = new JSONObject(user);
                        JSONObject dataObject = jsonObject.getJSONObject("data");

                        teamDetailResponse = new TeamDetailResponse();
                        teamDetailResponse.setTeamName(dataObject.getString("team_name"));
                        mCollapsingToolbarLayout.setTitle(teamDetailResponse.getTeamName());
                        teamDetailResponse.setTeamOwner(dataObject.getString("team_owner"));
                        teamDetailResponse.setTeamId(dataObject.getString("team_id"));
                        teamDetailResponse.setSport(dataObject.getString("sport"));
                        mTeamSportImageView.setImageResource(getSportIcon(teamDetailResponse.getSport()));
                        teamDetailResponse.setIsOwner(dataObject.getString("is_woner"));
                        preferenceHelper.save("is_owner", teamDetailResponse.getIsOwner());
                        teamDetailResponse.setLoginUserAllowInvites(dataObject.getString("login_user_allow_invites"));
                        teamDetailResponse.setTeamAbout(dataObject.getString("team_about"));
                        teamDetailResponse.setTeamLogo(dataObject.getString("team_logo"));
                        if (teamDetailResponse.getTeamLogo() != null && !teamDetailResponse.getTeamLogo().isEmpty()) {
                            Glide.with(TeamDetailActivity.this).load(teamDetailResponse.getTeamLogo()).placeholder(R.drawable.profiledefaultimg).dontAnimate().into(mTeamImageView);
                        } else {
                            Glide.with(TeamDetailActivity.this).load(R.drawable.profiledefaultimg).dontAnimate().into(mTeamImageView);
                        }
                        teamDetailResponse.setTeamLocation(dataObject.getString("team_location"));
                        mPlayerOrTeamLocation.setText(teamDetailResponse.getTeamLocation());
                        teamDetailResponse.setLocationId(dataObject.getString("location_id"));
                        teamDetailResponse.setLevel(dataObject.getString("level"));
                        teamDetailResponse.setBadge(dataObject.getString("badge"));
                        teamDetailResponse.setProgress(dataObject.getString("progress"));
                        if (dataObject.has("team_record")) {
                            JSONObject teamRecordJsonObject = new JSONObject(dataObject.getString("team_record"));
                            TeamRecord teamRecord = new TeamRecord();
                            teamRecord.setTotalMatch(teamRecordJsonObject.getString("total_match"));
                            teamRecord.setWon(teamRecordJsonObject.getString("won"));
                            teamRecord.setLost(teamRecordJsonObject.getString("lost"));
                            teamDetailResponse.setTeamRecord(teamRecord);
                        }
                        ArrayList<SquadInfo> arrayList = new ArrayList<>();
                        JSONArray squadArray = dataObject.getJSONArray("team_players");
                        if (squadArray != null) {
                            for (int i = 0; i < squadArray.length(); i++) {
                                FirebaseMessaging.getInstance().subscribeToTopic(teamDetailResponse.getTeamId());
                                SquadInfo squadInfo = new SquadInfo();
                                squadInfo.setPlayerName(squadArray.getJSONObject(i).getString("player_name"));
                                squadInfo.setPlayerLocation(squadArray.getJSONObject(i).getString("player_location"));
                                squadInfo.setPlayerPhoto(squadArray.getJSONObject(i).getString("player_photo"));
                                squadInfo.setPlayerId(squadArray.getJSONObject(i).getString("player_id"));
                                arrayList.add(squadInfo);
                            }
                            teamDetailResponse.setSquadInfos(arrayList);
                        }
                        ownerInviteCheck(userCanRequestMatch, teamDetailResponse.getLoginUserAllowInvites());
                        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), playerInviteId, teamDetailResponse);
                        mPlayerOrTeamViewPager.setAdapter(viewPagerAdapter);

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    CommonUtils.ErrorHandleMethod(TeamDetailActivity.this, response);
                }
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call1, Throwable t) {
                // Log error here since request failed
                CommonUtils.ServerFailureHandleMethod(TeamDetailActivity.this, t);
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
        });
    }

    private int getSportIcon(String sportId) {
        int sportImage = 0;
        switch (sportId) {
            case "1":
                sportImage = R.drawable.ic_badminton_white_96;
                break;
            case "2":
                sportImage = R.drawable.ic_basket_ball_white_96;
                break;
            case "3":
                sportImage = R.drawable.ic_carrom_white_96;
                break;
            case "4":
                sportImage = R.drawable.ic_cricket_white_96;
                break;
            case "5":
                sportImage = R.drawable.ic_football_white_96;
                break;
            case "6":
                sportImage = R.drawable.ic_tennis_white_96;
                break;
            case "7":
                sportImage = R.drawable.ic_volleyball_white_96;
                break;
        }
        return sportImage;
    }

    private void ownerInviteCheck(String userCanRequestMatch, String loginUserAllowInvites) {
        if (!userCanRequestMatch.isEmpty()) {
            if (preferenceHelper.contains("Myteam")) {
                if (mMatchOrPlayerInviteButton.getText().toString().equals(getResources().getString(R.string.invite_to_match))) {
                    mMatchOrPlayerInviteButton.setVisibility(View.GONE);
                }
            } else {
                if (loginUserAllowInvites.equals(Constants.DefaultText.ONE)) {
                    if (mMatchOrPlayerInviteButton.getText().toString().equals(getResources().getString(R.string.invite_to_match))) {
                        mMatchOrPlayerInviteButton.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (mMatchOrPlayerInviteButton.getText().toString().equals(getResources().getString(R.string.invite_to_match))) {
                        mMatchOrPlayerInviteButton.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    private void setListener() {
        mPlayerOrTeamDetailTab.addOnTabSelectedListener(this);
        mAppBarLayout.addOnOffsetChangedListener(this);
        mMatchOrPlayerInviteButton.setOnClickListener(this);
        mJoinTeamButton.setOnClickListener(this);
    }

    private void integrateViewPagerWithTabLayout() {
        mPlayerOrTeamDetailTab.setupWithViewPager(mPlayerOrTeamViewPager);
        mPlayerOrTeamViewPager.setOffscreenPageLimit(1);
    }

    private void setTypeFace() {
        Typeface typefaceRegular = Typeface.createFromAsset(getAssets(), "fonts/roboto_regular.ttf");
        mCollapsingToolbarLayout.setCollapsedTitleTypeface(typefaceRegular);
        Typeface typefaceBold = Typeface.createFromAsset(getAssets(), "fonts/roboto_bold.ttf");
        mCollapsingToolbarLayout.setExpandedTitleTypeface(typefaceBold);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mPlayerOrTeamViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
            mPlayerOrTeamLocation.setVisibility(View.GONE);
        } else if (verticalOffset == 0) {
            mPlayerOrTeamLocation.setVisibility(View.VISIBLE);
        } else {
            mPlayerOrTeamLocation.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = getIntent();
                if (intent != null) {
                    getMenuInflater().inflate(R.menu.menu_team_detail, menu);
                    final String isOwner = intent.getStringExtra(Constants.BundleKeys.IS_OWNER);
                    if (isOwner != null) {
                        if (teamDetailResponse != null) {
                            if (isOwner.equals(Constants.DefaultText.ONE)) {
                                menu.findItem(R.id.edit_team).setVisible(true);
                                menu.findItem(R.id.add_member).setVisible(true);
                                menu.findItem(R.id.delete_team).setVisible(true);
                                menu.findItem(R.id.exit_team).setVisible(false);
                            } else {
                                String userCanRequestMatch = "";
                                if (intent.getStringExtra(Constants.BundleKeys.INVITE_TO_MATCH) != null) {
                                    userCanRequestMatch = intent.getStringExtra(Constants.BundleKeys.INVITE_TO_MATCH);
                                }
                                if (!userCanRequestMatch.isEmpty()) {
                                    if (teamDetailResponse.getLoginUserAllowInvites().equals(Constants.DefaultText.ONE)) {
                                        menu.findItem(R.id.edit_team).setVisible(false);
                                        menu.findItem(R.id.add_member).setVisible(false);
                                        menu.findItem(R.id.delete_team).setVisible(false);
                                        menu.findItem(R.id.exit_team).setVisible(false);
                                        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                                        layoutParams.gravity = Gravity.END;
                                        layoutParams.setMargins(0, 30, 30, 0);
                                        mTeamSportImageView.setLayoutParams(layoutParams);
                                    }
                                } else {
                                    // Check current user is in this squad
                                    if(CheckSquad(teamDetailResponse.getSquadInfos())){
                                        menu.findItem(R.id.edit_team).setVisible(false);
                                        menu.findItem(R.id.add_member).setVisible(false);
                                        menu.findItem(R.id.delete_team).setVisible(false);
                                        menu.findItem(R.id.exit_team).setVisible(true);
                                    }else{
                                        menu.findItem(R.id.edit_team).setVisible(false);
                                        menu.findItem(R.id.add_member).setVisible(false);
                                        menu.findItem(R.id.delete_team).setVisible(false);
                                        menu.findItem(R.id.exit_team).setVisible(false);
                                    }
                                }
                            }
                        } else {
                            String userCanRequestMatch = "";
                            if (intent.getStringExtra(Constants.BundleKeys.INVITE_TO_MATCH) != null) {
                                userCanRequestMatch = intent.getStringExtra(Constants.BundleKeys.INVITE_TO_MATCH);
                            }
                            if (userCanRequestMatch.isEmpty()) {
                                // Check current user is in this squad
                                if(CheckSquad(teamDetailResponse.getSquadInfos())){
                                    menu.findItem(R.id.edit_team).setVisible(false);
                                    menu.findItem(R.id.add_member).setVisible(false);
                                    menu.findItem(R.id.delete_team).setVisible(false);
                                    menu.findItem(R.id.exit_team).setVisible(true);
                                }else{
                                    menu.findItem(R.id.edit_team).setVisible(false);
                                    menu.findItem(R.id.add_member).setVisible(false);
                                    menu.findItem(R.id.delete_team).setVisible(false);
                                    menu.findItem(R.id.exit_team).setVisible(false);
                                }
                            }
                        }
                    }
                }
            }
        }, 2000);
        return true;
    }

    private boolean CheckSquad(List<SquadInfo> squadInfos) {
        boolean status = false;
        for (int i=0;i<squadInfos.size();i++){
            String playerId = squadInfos.get(i).getPlayerId();
           if (mUserId.equals(playerId)){
               status = true;
           }
        }
        return status;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_team:
                showTeamEditDialog();
                return true;
            case R.id.add_member:
                navigateToPlayerInviteActivity();
                return true;
            case R.id.delete_team:
                deleteTeam();
                return true;
            case R.id.exit_team:
                exitFromThisTeam();
                return true;
            case android.R.id.home:
                overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteTeam() {
        mProgressDialog.show();
        String auth_token = "Bearer " + preferenceHelper.getString("user_token", "");
        Call<ResponseBody> call = mApiInterface.TeamDelete(auth_token, Integer.valueOf(teamDetailResponse.getTeamId()));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                        JSONObject json = new JSONObject(data);
                        Toast.makeText(TeamDetailActivity.this, "Team removed", Toast.LENGTH_SHORT).show();
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(teamDetailResponse.getTeamId());
                        if (CommonUtils.isNetworkAvailable(TeamDetailActivity.this)) {
                            Intent intent = new Intent(TeamDetailActivity.this, HomeActivity.class);
                            intent.putExtra("FromCreateteam", "true");
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(TeamDetailActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                String error = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(error);
                                String msg = null;
                                msg = jsonObject.getString("message");
                                String code = jsonObject.getString("code");
                                if (!code.equals("500")) {
                                    Toast.makeText(TeamDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(TeamDetailActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(TeamDetailActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String error = response.message();
                            Toast.makeText(TeamDetailActivity.this, error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(TeamDetailActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(TeamDetailActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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

    private void exitFromThisTeam() {
        mProgressDialog.show();
        int userId = Integer.parseInt(preferenceHelper.getString("user_id", ""));
        Call<ResponseBody> call = mApiInterface.TeamExit(mAuthToken, userId, Integer.valueOf(teamDetailResponse.getTeamId()));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                        JSONObject json = new JSONObject(data);
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(teamDetailResponse.getTeamId());
                        if (CommonUtils.isNetworkAvailable(TeamDetailActivity.this)) {
                            Intent intent = new Intent(TeamDetailActivity.this, HomeActivity.class);
                            intent.putExtra("FromCreateteam", "true");
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(TeamDetailActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                String error = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(error);
                                String msg = null;
                                msg = jsonObject.getString("message");
                                String code = jsonObject.getString("code");
                                if (!code.equals("500")) {
                                    Toast.makeText(TeamDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(TeamDetailActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(TeamDetailActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String error = response.message();
                            Toast.makeText(TeamDetailActivity.this, error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(TeamDetailActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(TeamDetailActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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

    private void navigateToPlayerInviteActivity() {
        if (teamDetailResponse != null) {
            preferenceHelper.save("teaminfo_squade_add", "true");
            Intent inviteIntent = new Intent(TeamDetailActivity.this, ExplorePlayerListActivity.class);
            inviteIntent.putExtra(Constants.BundleKeys.SPORT_ID, teamDetailResponse.getSport());
            inviteIntent.putExtra(Constants.BundleKeys.TEAM_ID, teamDetailResponse.getTeamId());
            inviteIntent.putExtra(Constants.BundleKeys.TEAM_NAME, teamDetailResponse.getTeamName());
            startActivity(inviteIntent);
        }
    }

    private void showTeamEditDialog() {
        preferenceHelper.save("user_location", "");
        final Dialog teamInfoEditDialog = new Dialog(TeamDetailActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        teamInfoEditDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        teamInfoEditDialog.setContentView(R.layout.dialog_team_info_edit);
        teamInfoEditDialog.getWindow().setDimAmount(0.7f);
        teamInfoEditDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); // This flag is required to set otherwise the setDimAmount method will not show any effect

        CardView card_view = (CardView) teamInfoEditDialog.findViewById(R.id.dialog_team_info_edit_card_view);
        ImageView back = (ImageView) teamInfoEditDialog.findViewById(R.id.dialog_team_info_edit_iv_dismiss);
        Button mSubmit = (Button) teamInfoEditDialog.findViewById(R.id.dialog_team_info_edit_btn_save);
        mEditTeamImageView = (ProfileImageView) teamInfoEditDialog.findViewById(R.id.dialog_team_info_edit_iv);
        final RelativeLayout teamInfoEditLayout = (RelativeLayout) teamInfoEditDialog.findViewById(R.id.dialog_team_info_rl_edit);
        final EditText teamName = (EditText) teamInfoEditDialog.findViewById(R.id.dialog_team_info_edit_et_name);
        final TextView teamLocation = (TextView) teamInfoEditDialog.findViewById(R.id.dialog_team_info_edit_et_location);
        final TextView teamOverview = (TextView) teamInfoEditDialog.findViewById(R.id.dialog_team_info_edit_et_overview);

        teamName.setText(teamDetailResponse.getTeamName());
        teamLocation.setText(teamDetailResponse.getTeamLocation());
        teamOverview.setText(teamDetailResponse.getTeamAbout());

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = (int) (metrics.widthPixels * 0.8);
        params.gravity = Gravity.CENTER;
        card_view.setLayoutParams(params);

        teamLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                teamLocation.setBackground(ContextCompat.getDrawable(TeamDetailActivity.this, R.drawable.bg_create_team_selection_outline));
//                teamName.setFocusable(false);
                teamInfoEditLayout.setBackground(ContextCompat.getDrawable(TeamDetailActivity.this, R.drawable.login_edittext_bg));
                CommonUtils.locationdialog(TeamDetailActivity.this, teamLocation, 3);
            }
        });

        teamName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    teamLocation.setBackground(ContextCompat.getDrawable(TeamDetailActivity.this, R.drawable.login_edittext_bg));
                    teamInfoEditLayout.setBackground(ContextCompat.getDrawable(TeamDetailActivity.this, R.drawable.login_edittext_bg));
                }
            }
        });

        teamOverview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    teamLocation.setBackground(ContextCompat.getDrawable(TeamDetailActivity.this, R.drawable.login_edittext_bg));
                    teamInfoEditLayout.setBackground(ContextCompat.getDrawable(TeamDetailActivity.this, R.drawable.bg_create_team_selection_outline));
                }
            }
        });


        mEditTeamImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    // Do something for lollipop and above versions
                    if (ContextCompat.checkSelfPermission(TeamDetailActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(TeamDetailActivity.this, new String[]{
                                android.Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.CommonKeys.MY_PERMISSIONS_REQUEST);
                    } else {
                        selectImage();
                    }
                } else {
                    selectImage();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                teamInfoEditDialog.dismiss();
            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (teamName.getText().toString().trim().length() == 0) {
                    Toast.makeText(TeamDetailActivity.this, "Provide team name", Toast.LENGTH_SHORT).show();
                } else if (!teamName.getText().toString().equals(teamDetailResponse.getTeamName())) {
                    TeamNameRequest teamNameRequest = new TeamNameRequest();
                    teamNameRequest.setTeamId(Constants.DefaultText.ZERO);
                    teamNameRequest.setTeamName(teamName.getText().toString().trim());
                    Call<ResponseBody> call = mApiInterface.checkTeamNameAvailable(mAuthToken, teamNameRequest);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                if (response.body() != null && response.code() == 200) {
                                    String data = response.body().string();
                                    Log.d("RESPONSE", data);
                                    JSONObject json = new JSONObject(data);
                                    String message = json.getString("message");
                                    if (message.equals(Constants.DefaultText.TEAM_NAME_ALREADY_TAKEN)) {
                                        Toast.makeText(TeamDetailActivity.this, Constants.Messages.TEAM_NAME_EXIST, Toast.LENGTH_LONG).show();
                                        teamName.requestFocus();
                                    } else {
                                        String locationId = preferenceHelper.getString("user_location", "");
                                        if (locationId.equals("")) {
                                            locationId = teamDetailResponse.getLocationId();
                                        }
                                        onChangeTeamInfo(teamDetailResponse.getTeamId(), teamName.getText().toString().trim(), locationId, teamLogo, teamOverview.getText().toString().trim());
                                        teamInfoEditDialog.dismiss();
                                    }
                                } else {
                                    if (response.errorBody() != null) {
                                        try {
                                            String error = response.errorBody().string();
                                            JSONObject jsonObject = new JSONObject(error);
                                            String msg = jsonObject.getString("message");
                                            Toast.makeText(TeamDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            String error = response.message();
                                            Toast.makeText(TeamDetailActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        String error = response.message();
                                        Toast.makeText(TeamDetailActivity.this, error, Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(TeamDetailActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(TeamDetailActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                            }
                        }
                    });
                } else {
                    String locationId = preferenceHelper.getString("user_location", "");
                    if (locationId.equals("")) {
                        locationId = teamDetailResponse.getLocationId();
                    }
                    onChangeTeamInfo(teamDetailResponse.getTeamId(), teamName.getText().toString().trim(), locationId, teamLogo, teamOverview.getText().toString().trim());
                    teamInfoEditDialog.dismiss();
                }
            }
        });
        teamInfoEditDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_team_detail_btn_join_team_or_cancel_request:
                break;
            case R.id.activity_team_detail_btn_invite_accept_or_request_match:
                Intent intent = getIntent();
                if (intent.getStringExtra(Constants.BundleKeys.INVITE_TO_MATCH) != null) {
                    Intent matchInviteIntent = new Intent(TeamDetailActivity.this, MatchInviteDetailsActivity.class);
                    matchInviteIntent.putExtra(Constants.BundleKeys.TEAM_ID, intent.getStringExtra(Constants.BundleKeys.TEAM_ID));
                    matchInviteIntent.putExtra(Constants.BundleKeys.TEAM_NAME, intent.getStringExtra(Constants.BundleKeys.TEAM_NAME));
                    matchInviteIntent.putExtra(Constants.BundleKeys.SPORT_ID, intent.getStringExtra(Constants.BundleKeys.SPORT_ID));
                    startActivity(matchInviteIntent);
                } else if (intent.getExtras().getSerializable(Constants.BundleKeys.MY_MATCH) != null) {
                    final MatchRequestModel matchRequestModel = (MatchRequestModel) intent.getExtras().getSerializable(Constants.BundleKeys.MY_MATCH);
                    preferenceHelper.save("match_id", matchRequestModel.getMatchId());
                    preferenceHelper.save("teamid", matchRequestModel.getOpponentTeamId());
                    preferenceHelper.save("myteamid", matchRequestModel.getMyTeamId());
                    preferenceHelper.save("teamsport", matchRequestModel.getTeamSport());
                    MatchRequestResponseModel matchRequestResponseModel = new MatchRequestResponseModel();
                    matchRequestResponseModel.setMatchId(matchRequestModel.getMatchId());
                    matchRequestResponseModel.setMatchStatus(Constants.DefaultText.ONE);
                    mProgressDialog.show();
                    Call<ResponseBody> call = mApiInterface.MatchConfirmationStatus(mAuthToken, matchRequestResponseModel);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                if (response.body() != null && response.code() == 200) {
                                    String data = response.body().string();
                                    JSONObject json = new JSONObject(data);
                                    String message = json.getString("message");
                                    Toast.makeText(TeamDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(TeamDetailActivity.this, ScheduleActivity.class);
                                    intent.putExtra(Constants.BundleKeys.MATCH_TYPE, CommonUtils.MatchType(matchRequestModel.getMatchType()));
                                    startActivity(intent);
                                } else {
                                    if (response.errorBody() != null) {
                                        String error = response.errorBody().string();
                                        JSONObject jsonObject = new JSONObject(error);
                                        String msg = jsonObject.getString("message");
                                        String code = jsonObject.getString("code");
                                        if (!code.equals("500")) {
                                            Toast.makeText(TeamDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        String error = response.message();
                                        Toast.makeText(TeamDetailActivity.this, error, Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(TeamDetailActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(TeamDetailActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Camera", "Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(TeamDetailActivity.this);
        builder.setTitle("Choose Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Camera")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, Constants.CommonKeys.REQUEST_CAMERA);
                } else if (items[item].equals("Gallery")) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);//
                    startActivityForResult(Intent.createChooser(intent, "Select File"), Constants.CommonKeys.SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.CommonKeys.SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == Constants.CommonKeys.REQUEST_CAMERA)
                onCaptureImageResult(data);
        }

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), resultUri);
                Glide.with(this).load(resultUri).placeholder(R.drawable.profiledefaultimg).dontAnimate().into(mEditTeamImageView);
                mEditTeamImageView.setImageBitmap(bitmap);
                base64(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            Toast.makeText(getApplicationContext(), String.valueOf(cropError), Toast.LENGTH_SHORT).show();
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File f = new File(String.valueOf(destination));
        Uri yourUri = Uri.fromFile(f);
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setMaxScaleMultiplier(5);
        options.setCropGridColor(ContextCompat.getColor(this, R.color.colorAccent));
        options.setToolbarColor(ContextCompat.getColor(this, R.color.white));
        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.black));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        UCrop.of(yourUri, Uri.fromFile(destination))
                .withOptions(options)
                .start(this);
        base64(bitmap);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setMaxScaleMultiplier(5);
        options.setCropGridColor(ContextCompat.getColor(this, R.color.colorAccent));
        options.setToolbarColor(ContextCompat.getColor(this, R.color.white));
        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.black));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        UCrop.of(selectedImageUri, Uri.fromFile(destination))
                .withOptions(options)
                .start(this);
    }

    public String base64(Bitmap bm) {
        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.images);
        if (bm != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 90, stream);
            byte[] image = stream.toByteArray();
            System.out.println("byte array:" + image);
            teamLogo = Base64.encodeToString(image, 0);
            Log.d("IMG", teamLogo);
            return teamLogo;
        } else {
            Toast.makeText(getApplicationContext(), "Unable to get convert image", Toast.LENGTH_SHORT).show();
        }
        return "";
    }

    private void onChangeTeamInfo(String teamId, String teamName, String teamLocationId, String teamLogo, String teamAbout) {
        TeamEditRequestModel teamEditRequestModel = new TeamEditRequestModel();
        teamEditRequestModel.setTeamId(teamId);
        teamEditRequestModel.setTeamName(teamName);
        teamEditRequestModel.setTeamLocation(teamLocationId);
        teamEditRequestModel.setTeamLogo(teamLogo);
        teamEditRequestModel.setTeamAbout(teamAbout);
        Call<ResponseBody> call = mApiInterface.onUpdateTeamInfo(mAuthToken, teamEditRequestModel);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        try {
                            String data = response.body().string();
                            JSONObject json = new JSONObject(data);
                            String message = json.getString("message");
                            Toast.makeText(TeamDetailActivity.this, message, Toast.LENGTH_LONG).show();
                            setUpViewPagerAdapter();
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private String[] tabTitles = new String[]{"Info", "Squad"};
        private String mPlayerInviteId;
        private String canUserRequestMatch;
        private TeamDetailResponse mTeamDetailResponse;

        ViewPagerAdapter(FragmentManager fragmentManager, String playerInviteId, TeamDetailResponse teamDetailResponse) {
            super(fragmentManager);
            mPlayerInviteId = playerInviteId;
            mTeamDetailResponse = teamDetailResponse;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = TeamInfoFragment.newInstance(mPlayerInviteId, mTeamDetailResponse);
                    break;
                case 1:
                    fragment = TeamSquadFragment.newInstance(mTeamDetailResponse);
                    break;
                default:
                    fragment = null;
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }
}