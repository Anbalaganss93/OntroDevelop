package com.ontro;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.ontro.dto.ExploreModel;
import com.ontro.dto.InvitePlayerRequest;
import com.ontro.dto.PlayerInviteCancelRequest;
import com.ontro.dto.PlayerInviteStatus;
import com.ontro.dto.PlayerPersonalSport;
import com.ontro.dto.PlayerProfileData;
import com.ontro.dto.PlayerProfilePersonalModel;
import com.ontro.dto.RecordSportModel;
import com.ontro.fragments.PlayerProfileInfoFragment;
import com.ontro.fragments.PlayerProfileRecordFragment;
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

public class PlayerProfileActivity extends AppCompatActivity implements View.OnClickListener, TabLayout.OnTabSelectedListener {
    private ImageView mFilteredSportImage;
    private ImageButton mBackButton;
    private String mPlayerId, mUserId, mPlayerSportId, mPlayerName;
    private PreferenceHelper preferenceHelper;
    private ApiInterface apiInterface;
    private String authToken;
    private TabLayout mPlayerProfileTab;
    private ViewPager mPlayerProfileViewPager;
    private Dialog mProgressView;
    private TextView mPlayerNameTextView, mPlayerLocationTextView;
    private CircularImageView mPlayerImageView;
    private Button mPlayerInviteButton, mMatchRequestButton;
    private List<String> mTabTitles = new ArrayList<String>();
    private int tabNavigation = 0;
    private String mPlayerInviteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_profile);
        overridePendingTransition(R.anim.slide_in_right, R.anim.pause);
        initView();
        setTypeFace();
        showPlayerProfile();
        setListener();
    }

    private void initView() {
        preferenceHelper = new PreferenceHelper(this, Constants.APP_NAME, 0);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        authToken = "Bearer " + preferenceHelper.getString("user_token", "");
        mPlayerProfileTab = (TabLayout) findViewById(R.id.activity_player_profile_tab);
        mPlayerProfileViewPager = (ViewPager) findViewById(R.id.activity_player_profile_view_pager);
        mFilteredSportImage = (ImageView) findViewById(R.id.activity_player_profile_iv_filtered_sport_image);
        mBackButton = (ImageButton) findViewById(R.id.activity_player_profile_iv_back);
        mPlayerNameTextView = (TextView) findViewById(R.id.activity_player_profile_tv_name);
        mPlayerLocationTextView = (TextView) findViewById(R.id.activity_player_profile_tv_location);
        mPlayerImageView = (CircularImageView) findViewById(R.id.activity_player_profile_civ_player_image);
        mPlayerInviteButton = (Button) findViewById(R.id.activity_player_profile_btn_player_invite);
        mMatchRequestButton = (Button) findViewById(R.id.activity_player_profile_btn_request_match);
        mProgressView = new Dialog(PlayerProfileActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        mProgressView.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressView.setContentView(R.layout.progressdialog_layout);
    }

    private void setTypeFace() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/roboto_regular.ttf");
        ViewGroup vg = (ViewGroup) mPlayerProfileTab.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(typeface);
                    ((TextView) tabViewChild).setAllCaps(false);
                }
            }
        }
    }

    private void setListener() {
        mFilteredSportImage.setOnClickListener(this);
        mBackButton.setOnClickListener(this);
        mPlayerInviteButton.setOnClickListener(this);
        mPlayerProfileTab.addOnTabSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_player_profile_iv_filtered_sport_image:
                if (mPlayerId != null && mUserId != null) {
                    if (mPlayerId.equals(mUserId)) {
                        navigateToProfileCompletionActivity();
                    } else {
                        invitePlayerForTeam();
                    }
                } else if (mPlayerId == null && mUserId != null) {
                    navigateToProfileCompletionActivity();
                }
                break;
            case R.id.activity_player_profile_iv_back:
                finish();
                overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
                break;
            case R.id.activity_player_profile_btn_player_invite:
                if(getIntent().getExtras() != null) {
                    if(getIntent().getExtras().getSerializable(Constants.BundleKeys.PLAYER_TEAM_LIST) != null) {
                        ExploreModel exploreModel = (ExploreModel) getIntent().getExtras().getSerializable(Constants.BundleKeys.PLAYER_TEAM_LIST);
                        String playerId = exploreModel.getExploreId();
                        String playerName = exploreModel.getExploreName();
                        String sportId = exploreModel.getExploreSport();
                        String teamId = (String) getIntent().getExtras().getSerializable(Constants.BundleKeys.TEAM_ID);
                        String teamName = (String) getIntent().getExtras().getSerializable(Constants.BundleKeys.TEAM_NAME);
                       if(mPlayerInviteButton.getText().toString().equals(getResources().getString(R.string.invite_to_team))) {
                           MixpanelAPI mMixpanel = MixpanelAPI.getInstance(PlayerProfileActivity.this, getResources().getString(R.string.mixpanel_token));
                           try {
                               JSONObject eventJsonObject = new JSONObject();
                               eventJsonObject.put("PlayerId", playerId);
                               eventJsonObject.put("PlayerName", playerName);
                               eventJsonObject.put("Sport", CommonUtils.sportNameCheck(sportId));
                               eventJsonObject.put("TeamOwner", preferenceHelper.getString("user_name", ""));
                               eventJsonObject.put("InvitedTeam", teamName);
                               mMixpanel.track("PlayerInvite", eventJsonObject);
                           } catch (JSONException e) {
                               Log.e("Ontro", "Unable to add properties to JSONObject", e);
                           }
                           mProgressView.show();
                           InvitePlayerRequest invitePlayerRequest = new InvitePlayerRequest();
                           invitePlayerRequest.setTeamId(Integer.valueOf(teamId));
                           invitePlayerRequest.setPlayerId(Integer.valueOf(playerId));
                           Call<ResponseBody> call = apiInterface.inviteToTeam(authToken, invitePlayerRequest);
                           call.enqueue(new Callback<ResponseBody>() {
                               @Override
                               public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                   try {
                                       if (response.body() != null && response.code() == 200) {
                                           mProgressView.dismiss();
                                           String data = response.body().string();
                                           Log.d("RESPONSE", data);
                                           JSONObject json = new JSONObject(data);
                                           Toast.makeText(PlayerProfileActivity.this, Constants.Messages.PLAYER_INVITE_SENT_SUCCESSFULLY, Toast.LENGTH_SHORT).show();
                                           mPlayerInviteButton.setText(getResources().getString(R.string.invite_to_team));
                                       } else {
                                           mProgressView.dismiss();
                                           if (response.errorBody() != null) {
                                               try {
                                                   String error = response.errorBody().string();
                                                   JSONObject jsonObject = new JSONObject(error);
                                                   String msg = jsonObject.getString("message");
                                                   String code = jsonObject.getString("code");
                                                   if (!code.equals("500")) {
                                                       Toast.makeText(PlayerProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
                                                   } else {
                                                       Toast.makeText(PlayerProfileActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                                                   }
                                               } catch (JSONException e) {
                                                   e.printStackTrace();
                                                   String error = response.message();
                                                   Toast.makeText(PlayerProfileActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                                               } catch (IOException e) {
                                                   e.printStackTrace();
                                               }
                                           } else {
                                               String error = response.message();
                                               Toast.makeText(PlayerProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                                           }
                                       }
                                   } catch (Exception e) {
                                       e.printStackTrace();
                                   }
                                   if(mProgressView != null && mProgressView.isShowing()) {
                                       mProgressView.dismiss();
                                   }
                               }

                               @Override
                               public void onFailure(Call<ResponseBody> call, Throwable t) {
                                   //  Log error here since request failed
                                   try {
                                       if (t instanceof SocketTimeoutException) {
                                           Toast.makeText(PlayerProfileActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                                       } else {
                                           Toast.makeText(PlayerProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                       }
                                   } catch (Exception e) {
                                       e.printStackTrace();
                                   }
                                   if(mProgressView != null && mProgressView.isShowing()) {
                                       mProgressView.dismiss();
                                   }
                               }
                           });
                       } else if(mPlayerInviteButton.getText().toString().equals(getResources().getString(R.string.cancel_invite_request))) {
                          onCancelPlayerInvite();
                       }
                    } else {
                        invitePlayerForTeam();
                    }
                }
                break;
        }

    }

    private void onCancelPlayerInvite() {
        mProgressView.show();
        PlayerInviteCancelRequest inviteCancelRequest = new PlayerInviteCancelRequest();
        inviteCancelRequest.setInviteId(Integer.valueOf(mPlayerInviteId));
        Call<ResponseBody> call = apiInterface.cancelPlayerInviteRequest(authToken, inviteCancelRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                        mPlayerInviteButton.setText(getResources().getString(R.string.invite_to_team));
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                String error = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(error);
                                String msg = jsonObject.getString("message");
                                String code = jsonObject.getString("code");
                                if (!code.equals("500")) {
                                    Toast.makeText(PlayerProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(PlayerProfileActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                String error = response.message();
                                Toast.makeText(PlayerProfileActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            String error = response.message();
                            Toast.makeText(PlayerProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(mProgressView != null && mProgressView.isShowing()) {
                    mProgressView.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //  Log error here since request failed
                try {
                    if (t instanceof SocketTimeoutException) {
                        Toast.makeText(PlayerProfileActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PlayerProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(mProgressView != null && mProgressView.isShowing()) {
                    mProgressView.dismiss();
                }
            }
        });
    }

    private void invitePlayerForTeam() {
        Intent playerInviteIntent = new Intent(this, PlayerInviteActivity.class);
        playerInviteIntent.putExtra(Constants.BundleKeys.PLAYER_ID, mPlayerId);
        playerInviteIntent.putExtra(Constants.BundleKeys.SPORT_ID, mPlayerSportId);
        playerInviteIntent.putExtra(Constants.BundleKeys.PLAYER_NAME, mPlayerName);
        startActivity(playerInviteIntent);
    }

    private void navigateToProfileCompletionActivity() {
        Intent intent = new Intent(this, ProfileCompletionActivity.class);
        intent.putExtra(Constants.BundleKeys.PROFILE_COMPLETION, Constants.Messages.PLAYER_PROFILE);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if(getIntent() != null) {
            if(tabNavigation == 0) {
                String sportId = getIntent().getStringExtra(Constants.BundleKeys.SPORT_ID);
                if (sportId != null) {
                    for (int position = 0; position < mTabTitles.size(); position++) {
                        if (Integer.valueOf(sportId) == CommonUtils.sportIdCheck(mTabTitles.get(position))) {
                            mPlayerProfileViewPager.setCurrentItem(position);
                            tabNavigation++;
                        }
                    }
                } else {
                    mPlayerProfileViewPager.setCurrentItem(tab.getPosition());
                }
            } else {
                mPlayerProfileViewPager.setCurrentItem(tab.getPosition());
            }
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private void showPlayerProfile() {
        if (getIntent() != null) {
            mUserId = preferenceHelper.getString("user_id", "");
            mPlayerId = getIntent().getStringExtra(Constants.BundleKeys.PLAYER_ID);
            if (mPlayerId != null && !mUserId.equals(mPlayerId)) {
                if(getIntent().getExtras() != null) {
                    if (getIntent().getExtras().getSerializable(Constants.BundleKeys.PLAYER_TEAM_LIST) != null) {
                        ExploreModel exploreModel = (ExploreModel) getIntent().getExtras().getSerializable(Constants.BundleKeys.PLAYER_TEAM_LIST);
                        String playerId = exploreModel.getExploreId();
                        String playerName = exploreModel.getExploreName();
                        String sportId = exploreModel.getExploreSport();
                        String teamId = (String) getIntent().getExtras().getSerializable(Constants.BundleKeys.TEAM_ID);
                        String teamName = (String) getIntent().getExtras().getSerializable(Constants.BundleKeys.TEAM_NAME);
                        if (exploreModel != null) {
                            mPlayerInviteButton.setVisibility(View.VISIBLE);
                            List<PlayerInviteStatus> playerInviteStatuses = exploreModel.getInviteStatuses();
                            int count = 0;
                            for (int i = 0; i < playerInviteStatuses.size(); i++) {
                                PlayerInviteStatus inviteStatus = playerInviteStatuses.get(i);
                                if (teamId.equals(inviteStatus.getTeamId())) {
                                    if (inviteStatus.getInviteStatus().equals(Constants.DefaultText.ONE)) {
                                        mPlayerInviteButton.setVisibility(View.GONE);
                                        break;
                                    } else if (inviteStatus.getInviteStatus().equals(Constants.DefaultText.ZERO)) {
                                        mPlayerInviteButton.setVisibility(View.VISIBLE);
                                        mPlayerInviteButton.setText(getResources().getString(R.string.cancel_invite_request));
                                        mPlayerInviteId = inviteStatus.getInviteId();
                                    }
                                } else {
                                    count++;
                                }
                            }
                            if (count == playerInviteStatuses.size()) {
                                mPlayerInviteButton.setVisibility(View.VISIBLE);
                                mPlayerInviteButton.setText(getResources().getString(R.string.invite_to_team));
                            }

                        } else {
                            mPlayerInviteButton.setVisibility(View.GONE);
                        }
                    } else {
                        mPlayerInviteButton.setVisibility(View.GONE);
                    }
                }
                mPlayerSportId = getIntent().getStringExtra(Constants.BundleKeys.SPORT_ID);
                mPlayerName = getIntent().getStringExtra(Constants.BundleKeys.PLAYER_NAME);
                mFilteredSportImage.setVisibility(View.GONE);
                mPlayerInviteButton.setVisibility(View.VISIBLE);
                mMatchRequestButton.setVisibility(View.GONE);
                if (CommonUtils.isNetworkAvailable(PlayerProfileActivity.this)) {
                    mProgressView.show();
                    getPlayerPersonalInfo(mPlayerId);
                } else {
                    Toast.makeText(PlayerProfileActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                }
            } else {
                mPlayerInviteButton.setVisibility(View.GONE);
                mMatchRequestButton.setVisibility(View.GONE);
                if (CommonUtils.isNetworkAvailable(PlayerProfileActivity.this)) {
                    mProgressView.show();
                    getPlayerPersonalInfo(mPlayerId);
                } else {
                    Toast.makeText(PlayerProfileActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                }
                mFilteredSportImage.setVisibility(View.VISIBLE);
            }
        }
    }

    private void getPlayerPersonalInfo(final String playerId) {
        final ProfileDataBaseHelper profileDataBaseHelper = new ProfileDataBaseHelper(PlayerProfileActivity.this);
        Call<ResponseBody> call;
        if (null != playerId) {
            call = apiInterface.getPlayerPersonalInfo(authToken, playerId);
        } else {
            call = apiInterface.getMyProfilePersonalInfo(authToken);
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                        JSONObject json = new JSONObject(data);
                        JSONObject dataObject = json.getJSONObject("data");
                        PlayerProfilePersonalModel profilePersonalModel = new PlayerProfilePersonalModel();
                        profilePersonalModel.setPlayerId(dataObject.getString("player_id"));
                        profilePersonalModel.setPlayerName(dataObject.getString("player_name"));
                        mPlayerNameTextView.setText(profilePersonalModel.getPlayerName());
                        profilePersonalModel.setPlayerSports(dataObject.getString("player_sports"));
                        profilePersonalModel.setPhone(dataObject.getString("phone"));
                        Log.d("Player Phone no", String.valueOf(dataObject.getString("phone")));
                        profilePersonalModel.setGender(dataObject.getString("gender"));
                        profilePersonalModel.setLocality(dataObject.getString("locality"));
                        profilePersonalModel.setCity(dataObject.getString("city"));
                        profilePersonalModel.setSportType(dataObject.getString("sport_type"));
                        profilePersonalModel.setPlayerDob(dataObject.getString("player_dob"));
                        profilePersonalModel.setProfileImage(dataObject.getString("profile_image"));
                        if (!profilePersonalModel.getProfileImage().equals("null")) {
                            Glide.with(PlayerProfileActivity.this).load(profilePersonalModel.getProfileImage()).placeholder(R.drawable.profiledefaultimg).dontAnimate().into(mPlayerImageView);
                        }
                        profilePersonalModel.setHeight(dataObject.getString("height"));
                        profilePersonalModel.setLocationName(dataObject.getString("location_name"));
                        mPlayerLocationTextView.setText(profilePersonalModel.getLocationName());
                        profilePersonalModel.setCityName(dataObject.getString("city_name"));
                        JSONArray sports = dataObject.getJSONArray("sports");
                        List<PlayerPersonalSport> playerPersonalSports = new ArrayList<>();
                        List<RecordSportModel> sportModels = new ArrayList<>();
                        if (sports.length() > 0) {
                            for (int j = 0; j < sports.length(); j++) {
                                PlayerPersonalSport playerPersonalSport = new PlayerPersonalSport();
                                playerPersonalSport.setSport(sports.getJSONObject(j).getInt("sport"));
                                playerPersonalSport.setHandedness(sports.getJSONObject(j).getString("handedness"));
                                playerPersonalSport.setPosition(sports.getJSONObject(j).getString("position"));
                                if(sports.getJSONObject(j).has("have_team")) {
                                    playerPersonalSport.setHaveTeam(sports.getJSONObject(j).getInt("have_team"));
                                }
                                playerPersonalSports.add(playerPersonalSport);
                                RecordSportModel sportModel = new RecordSportModel();
                                sportModel.setSportid(String.valueOf(sports.getJSONObject(j).getInt("sport")));
                                sportModel.setSportname(CommonUtils.sportNameCheck(String.valueOf(sports.getJSONObject(j).getInt("sport"))));
                                sportModels.add(sportModel);
                            }
                        }
                        profilePersonalModel.setSports(playerPersonalSports);
                        Gson gson = new Gson();
                        String toStoreObject = gson.toJson(profilePersonalModel, PlayerProfilePersonalModel.class);
                        PlayerProfileData profileData = new PlayerProfileData();
                        profileData.setKeyId(1);
                        profileData.setPlayerInfo(toStoreObject);
                        if (playerId == null) {
                            if (profileDataBaseHelper.getProfile().getPlayerInfo() == null) {
                                profileDataBaseHelper.insertPlayerProfile(profileData);
                            } else {
                                profileDataBaseHelper.updateProfile(profileData);
                            }
                        } else {
                            String userId = preferenceHelper.getString("user_id", "");
                            if(userId.equals(playerId)) {
                                if (profileDataBaseHelper.getProfile().getPlayerInfo() == null) {
                                    profileDataBaseHelper.insertPlayerProfile(profileData);
                                } else {
                                    profileDataBaseHelper.updateProfile(profileData);
                                }
                            }
                        }
                        for (int i = 0; i < sportModels.size(); i++) {
                            mTabTitles.add(sportModels.get(i).getSportname());
                        }
                        mTabTitles.add(0, Constants.DefaultText.INFO);

                        setUpViewPagerAdapter(profilePersonalModel, mPlayerId, mTabTitles);
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                String error = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(error);
                                String msg = jsonObject.getString("message");
                                String code = jsonObject.getString("code");
                                if (!code.equals("500")) {
                                    Toast.makeText(PlayerProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(PlayerProfileActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                String error = response.message();
                                Toast.makeText(PlayerProfileActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            String error = response.message();
                            Toast.makeText(PlayerProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mProgressView.isShowing()) mProgressView.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //  Log error here since request failed
                try {
                    if (t instanceof SocketTimeoutException) {
                        Toast.makeText(PlayerProfileActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PlayerProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mProgressView.isShowing()) mProgressView.dismiss();
            }
        });
    }

    private void setUpViewPagerAdapter(PlayerProfilePersonalModel profilePersonalModel, String playerId, List<String> tabTitles) {
        PlayerProfileViewPagerAdapter viewPagerAdapter = new PlayerProfileViewPagerAdapter(this.getSupportFragmentManager(), profilePersonalModel, playerId, tabTitles);
        mPlayerProfileViewPager.setAdapter(viewPagerAdapter);
        mPlayerProfileTab.setupWithViewPager(mPlayerProfileViewPager);
        if(tabTitles.size() > 4) {
            mPlayerProfileTab.setTabMode(TabLayout.MODE_SCROLLABLE);
        } else {
            mPlayerProfileTab.setTabMode(TabLayout.MODE_FIXED);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
    }

    private class PlayerProfileViewPagerAdapter extends FragmentStatePagerAdapter {
        public ArrayList<Fragment.SavedState> mSavedState = new ArrayList<Fragment.SavedState>();
        private PlayerProfilePersonalModel playerProfilePersonalModel;
        private List<String> mTabTitles = new ArrayList<>();
        private String mPlayerId;
        private FragmentTransaction mCurTransaction;
        private FragmentManager mFragmentManager;
        private Fragment mCurrentPrimaryItem = null;

        public PlayerProfileViewPagerAdapter(FragmentManager supportFragmentManager, PlayerProfilePersonalModel profilePersonalModel, String playerId, List<String> tabTitles) {
            super(supportFragmentManager);
            mFragmentManager = supportFragmentManager;
            playerProfilePersonalModel = profilePersonalModel;
            mPlayerId = playerId;
            mTabTitles = tabTitles;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = PlayerProfileInfoFragment.newInstance(playerProfilePersonalModel);
                    break;
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    fragment = PlayerProfileRecordFragment.newInstance(mPlayerId, mTabTitles.get(position));
                    break;
                default:
                    fragment = null;
                    break;
            }
            return fragment;
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
        public Parcelable saveState() {
            return null;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
           /* if (mFragments.size() > position) {
                Fragment f = mFragments.get(position);
                if (f != null) {
                    return f;
                }
            }*/
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
           /* while (mFragments.size() <= position) {
                mFragments.addTeamScore(null);
            }*/
            fragment.setMenuVisibility(false);
           /* mFragments.set(position, fragment);*/
            mCurTransaction.add(container.getId(), fragment);

            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            Fragment fragment = (Fragment) object;

            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction();
            }
            mCurTransaction.remove(fragment);
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
}
