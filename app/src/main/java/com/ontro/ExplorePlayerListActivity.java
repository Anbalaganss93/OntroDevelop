package com.ontro;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.malinskiy.superrecyclerview.swipe.SwipeDismissRecyclerViewTouchListener;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.ontro.adapters.ExplorePlayerListAdapter;
import com.ontro.dto.ExploreModel;
import com.ontro.dto.InvitePlayerRequest;
import com.ontro.dto.PlayerInviteCancelRequest;
import com.ontro.dto.PlayerInviteStatus;
import com.ontro.fragments.PlayerQuickViewFragment;
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

public class ExplorePlayerListActivity extends AppCompatActivity implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener, SwipeDismissRecyclerViewTouchListener.DismissCallbacks,
        View.OnFocusChangeListener, TextView.OnEditorActionListener, ExplorePlayerListAdapter.ExplorePlayerListListener {
    private TextView mPlayerListEmptyView, mToolbarTitle;
    private Dialog mProgressDialog;
    private PreferenceHelper preferenceHelper;
    private ApiInterface apiInterface;
    private SuperRecyclerView mExplorePlayerListView;
    private ExplorePlayerListAdapter adapter;
    private Button mExplorePlayerDoneButton;
    private int pagecount = 1;
    private ImageView mPlayerSearchView;
    private EditText mPlayerSearchEditText;
    private int search_status = 0;
    private String search_key = "";
    private MixpanelAPI mMixpanel;
    private ImageView mBackNavigationView;
    private String authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_player_list);
        overridePendingTransition(R.anim.slide_in_right, R.anim.pause);
        mMixpanel = MixpanelAPI.getInstance(this, getResources().getString(R.string.mixpanel_token));
        Typeface typeface_regular = Typeface.createFromAsset(getAssets(), "fonts/roboto_regular.ttf");
        preferenceHelper = new PreferenceHelper(ExplorePlayerListActivity.this, Constants.APP_NAME, 0);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        authToken = "Bearer " + preferenceHelper.getString("user_token", "");
        initView();
        setListener();
        mPlayerSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() > 0) {
                    if (CommonUtils.isNetworkAvailable(ExplorePlayerListActivity.this)) {
                        search_key = mPlayerSearchEditText.getText().toString().trim();
                        pagecount = 1;
                        ExplorePlayer_servercall();
                    } else {
                        Toast.makeText(ExplorePlayerListActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (CommonUtils.isNetworkAvailable(ExplorePlayerListActivity.this)) {
                        search_key = "";
                        pagecount = 1;
                        ExplorePlayer_servercall();
                    } else {
                        Toast.makeText(ExplorePlayerListActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void setListener() {
        mBackNavigationView.setOnClickListener(this);
        mPlayerSearchView.setOnClickListener(this);
        mExplorePlayerDoneButton.setOnClickListener(this);
        mPlayerSearchEditText.setOnFocusChangeListener(this);
        mPlayerSearchEditText.setOnEditorActionListener(this);
    }

    private void initView() {
        mProgressDialog = new Dialog(ExplorePlayerListActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setContentView(R.layout.progressdialog_layout);
        mBackNavigationView = (ImageView) findViewById(R.id.activity_explore_player_list_iv_back);
        mToolbarTitle = (TextView) findViewById(R.id.activity_explore_player_list_tv_toolbar_title);
        mPlayerListEmptyView = (TextView) findViewById(R.id.activity_explore_player_list_tv_empty);
        mExplorePlayerDoneButton = (Button) findViewById(R.id.activity_explore_player_list_btn_done);
        mPlayerSearchView = (ImageView) findViewById(R.id.activity_explore_player_list_iv_search);
        mPlayerSearchEditText = (EditText) findViewById(R.id.activity_explore_player_list_et_location_search);
        mExplorePlayerListView = (SuperRecyclerView) findViewById(R.id.activity_explore_player_list_rv);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(ExplorePlayerListActivity.this);
        mExplorePlayerListView.setLayoutManager(mLayoutManager);
        search_key = "";

        if (Constants.arrayList.size() != 0) {
            Constants.arrayList.clear();
        }
        mPlayerListEmptyView.setVisibility(View.GONE);
        mExplorePlayerDoneButton.setEnabled(true);
        Intent intent = getIntent();
        if (intent != null) {
            String teamId = intent.getStringExtra(Constants.BundleKeys.TEAM_ID);
            adapter = new ExplorePlayerListAdapter(ExplorePlayerListActivity.this, Constants.arrayList, this, teamId);
            mExplorePlayerListView.setAdapter(adapter);

            mExplorePlayerListView.hideProgress();
            mExplorePlayerListView.hideMoreProgress();
            mPlayerListEmptyView.setVisibility(View.GONE);

            mExplorePlayerListView.setupMoreListener(new OnMoreListener() {
                @Override
                public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
                    // Fetch more from Api or DB
                    if (CommonUtils.isNetworkAvailable(ExplorePlayerListActivity.this)) {
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                        pagecount++;
                        ExplorePlayer_servercall();
                    }
                }
            }, 3);

            if (CommonUtils.isNetworkAvailable(this)) {
                mProgressDialog.show();
                ExplorePlayer_servercall();
            } else {
                Toast.makeText(ExplorePlayerListActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
    }

    public void openSearchBox() {
        try {
            search_status = 1;

            mPlayerSearchEditText.post(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {
                    int cx = mPlayerSearchEditText.getWidth();
                    int cy = mPlayerSearchEditText.getHeight() / 2;
                    // get the final radius for the clipping circle
                    float finalRadius = (float) Math.hypot(cx, cy);
                    // create the animator for this view (the start radius is zero)
                    Animator anim;
                    anim = ViewAnimationUtils.createCircularReveal(mPlayerSearchEditText, cx, cy, 0, finalRadius);
                    anim.setDuration((long) 220);
                    // make the view visible and start the animation
                    mPlayerSearchEditText.setVisibility(View.VISIBLE);
                    mPlayerSearchEditText.setFocusableInTouchMode(true);
                    mPlayerSearchEditText.setFocusable(true);
                    mPlayerSearchEditText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(mPlayerSearchEditText, InputMethodManager.SHOW_IMPLICIT);
                    anim.start();
                }
            });
          /*  mPlayerSearchEditText.setFocusableInTouchMode(true);
            mPlayerSearchEditText.setFocusable(true);
            mPlayerSearchEditText.requestFocus();
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);*/
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("SEARCH", String.valueOf(e.toString()));
        }
    }

    public void closeSearchBox(View view) {
        try {
            mPlayerSearchEditText.setText("");
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            search_status = 0;
            int cx = mPlayerSearchEditText.getWidth();
            int cy = mPlayerSearchEditText.getHeight() / 2;
            // get the final radius for the clipping circle
            float finalRadius = (float) Math.hypot(cx, cy);
            // create the animator for this view (the start radius is zero)
            Animator anim;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                anim = ViewAnimationUtils.createCircularReveal(mPlayerSearchEditText, cx, cy, finalRadius, 0);
                anim.setInterpolator(new AccelerateDecelerateInterpolator());
                anim.setDuration((long) 250);
                anim.start();
                // make the view visible and start the animation
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mPlayerSearchEditText.setFocusableInTouchMode(false);
                        mPlayerSearchEditText.setFocusable(false);
                        mPlayerSearchEditText.clearFocus();
                        mPlayerSearchEditText.setVisibility(View.GONE);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("SEARCH ic_close_grey", String.valueOf(e.toString()));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_explore_player_list_iv_back:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                finish();
                overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
                break;
            case R.id.activity_explore_player_list_iv_search:
                if (search_status == 0) {
                    pagecount = 1;
                    mToolbarTitle.setVisibility(View.GONE);
                    openSearchBox();
                } else {
                    mToolbarTitle.setVisibility(View.VISIBLE);
                    closeSearchBox(view);
                }
                break;
            case R.id.activity_explore_player_list_btn_done:
                Intent intent = new Intent(ExplorePlayerListActivity.this, HomeActivity.class);
                intent.putExtra("FromCreateteam", "true");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
        }
    }

    public void ExplorePlayer_servercall() {
        Intent intent = getIntent();
        if (intent != null) {
            String teamId = intent.getStringExtra(Constants.BundleKeys.TEAM_ID);
            String sportId = intent.getStringExtra(Constants.BundleKeys.SPORT_ID);
            String location = "";
            Call<ResponseBody> call;
            call = apiInterface.ExplorePlayers(authToken, search_key, location, sportId, String.valueOf(pagecount));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.body() != null && response.code() == 200) {
                            if (pagecount == 1) {
                                adapter.clear();
                            }
                            String data = response.body().string();
                            Log.d("RESPONSE", data);
                            JSONObject json = new JSONObject(data);
                            JSONObject datajson = new JSONObject(json.getString("data"));
                            JSONArray array = new JSONArray(datajson.getString("data"));
                            if (array.length() != 0) {
                                mPlayerListEmptyView.setVisibility(View.GONE);
                                for (int i = 0; i < array.length(); i++) {
                                    ExploreModel exploreModel = new ExploreModel();
                                    exploreModel.setExploreId(array.getJSONObject(i).getString("player_id"));
                                    String userId = preferenceHelper.getString("user_id", "");
                                    exploreModel.setExploreName(array.getJSONObject(i).getString("player_name"));
                                    exploreModel.setExploreImage(array.getJSONObject(i).getString("player_photo"));
                                    exploreModel.setExploreLocation(array.getJSONObject(i).getString("location"));
                                    exploreModel.setExploreSport(array.getJSONObject(i).getString("sport"));
//                                m.setExploreBatch(Integer.parseInt(array.getJSONObject(i).getString("badge")));
                                    exploreModel.setIsChecked(0);
                                    List<PlayerInviteStatus> playerInviteStatusList = new ArrayList<>();
                                    JSONArray jsonArray = new JSONArray(array.getJSONObject(i).getString("player_teams"));
                                    if (jsonArray.length() > 0) {
                                        for (int k = 0; k < jsonArray.length(); k++) {
                                            PlayerInviteStatus playerInviteStatus = new PlayerInviteStatus();
                                            playerInviteStatus.setTeamId(jsonArray.getJSONObject(k).getString("team_id"));
                                            playerInviteStatus.setInviteStatus(jsonArray.getJSONObject(k).getString("invite_status"));
                                            playerInviteStatus.setInviteId(jsonArray.getJSONObject(k).getString("invite_id"));
                                            playerInviteStatusList.add(playerInviteStatus);
                                        }
                                    }
                                    exploreModel.setInviteStatuses(playerInviteStatusList);
                                    if (!exploreModel.getExploreId().equals(userId)) {
                                        adapter.add(exploreModel);
                                    } else {
                                        if (array.length() == 1) {
                                            mPlayerListEmptyView.setVisibility(View.VISIBLE);
                                            mExplorePlayerDoneButton.setVisibility(View.GONE);
                                        } else {
                                            mExplorePlayerDoneButton.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            } else {
                                if (pagecount == 1) {
                                    mPlayerListEmptyView.setVisibility(View.VISIBLE);
                                    mExplorePlayerDoneButton.setVisibility(View.GONE);
                                } else {
                                    mExplorePlayerDoneButton.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            if (response.errorBody() != null) {
                                try {
                                    String error = response.errorBody().string();
                                    JSONObject jsonObject = new JSONObject(error);
                                    String msg = jsonObject.getString("message");
                                    Toast.makeText(ExplorePlayerListActivity.this, msg, Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    if (adapter != null) adapter.clear();
                                    String error = response.message();
                                    Toast.makeText(ExplorePlayerListActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                String error = response.message();
                                Toast.makeText(ExplorePlayerListActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mExplorePlayerListView.hideMoreProgress();
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    //  Log error here since request failed
                    try {
                        if (t instanceof SocketTimeoutException) {
                            Toast.makeText(ExplorePlayerListActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ExplorePlayerListActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mExplorePlayerListView.hideMoreProgress();
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                }
            });
        }
    }

    @Override
    public void onRefresh() {
        if (CommonUtils.isNetworkAvailable(ExplorePlayerListActivity.this)) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            pagecount = 1;
            ExplorePlayer_servercall();
        } else {
            Toast.makeText(ExplorePlayerListActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean canDismiss(int position) {
        return false;
    }

    @Override
    public void onDismiss(RecyclerView recyclerView, int[] reverseSortedPositions) {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            InputMethodManager imm = (InputMethodManager) ExplorePlayerListActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            InputMethodManager imm = (InputMethodManager) ExplorePlayerListActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        return false;
    }

    @Override
    public void invitePlayerToTeam(ExploreModel exploreModel, String teamId) {
        String playerId = exploreModel.getExploreId();
        String playerName = exploreModel.getExploreName();
        String sportId = exploreModel.getExploreSport();
        String teamName = "";
        if (getIntent() != null) {
            teamName = getIntent().getStringExtra(Constants.BundleKeys.TEAM_NAME);
        }
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

        InvitePlayerRequest invitePlayerRequest = new InvitePlayerRequest();
        invitePlayerRequest.setTeamId(Integer.valueOf(teamId));
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
                        Toast.makeText(ExplorePlayerListActivity.this, Constants.Messages.PLAYER_INVITE_SENT_SUCCESSFULLY, Toast.LENGTH_SHORT).show();
                        if (CommonUtils.isNetworkAvailable(ExplorePlayerListActivity.this)) {
                            mProgressDialog.show();
                            pagecount = 1;
                            ExplorePlayer_servercall();
                        } else {
                            Toast.makeText(ExplorePlayerListActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                String error = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(error);
                                String msg = jsonObject.getString("message");
                                String code = jsonObject.getString("code");
                                if (!code.equals("500")) {
                                    Toast.makeText(ExplorePlayerListActivity.this, msg, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ExplorePlayerListActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                String error = response.message();
                                Toast.makeText(ExplorePlayerListActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            String error = response.message();
                            Toast.makeText(ExplorePlayerListActivity.this, error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ExplorePlayerListActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ExplorePlayerListActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void cancelPlayerInviteRequest(String inviteId) {
        PlayerInviteCancelRequest inviteCancelRequest = new PlayerInviteCancelRequest();
        inviteCancelRequest.setInviteId(Integer.valueOf(inviteId));
        Call<ResponseBody> call = apiInterface.cancelPlayerInviteRequest(authToken, inviteCancelRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                        JSONObject json = new JSONObject(data);
                        if (CommonUtils.isNetworkAvailable(ExplorePlayerListActivity.this)) {
                            mProgressDialog.show();
                            pagecount = 1;
                            ExplorePlayer_servercall();
                        } else {
                            Toast.makeText(ExplorePlayerListActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                String error = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(error);
                                String msg = jsonObject.getString("message");
                                String code = jsonObject.getString("code");
                                if (!code.equals("500")) {
                                    Toast.makeText(ExplorePlayerListActivity.this, msg, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ExplorePlayerListActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                String error = response.message();
                                Toast.makeText(ExplorePlayerListActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            String error = response.message();
                            Toast.makeText(ExplorePlayerListActivity.this, error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ExplorePlayerListActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ExplorePlayerListActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void showPlayerQuickViewDialog(ExploreModel exploreModel, String teamId) {
        String teamName = "";
        if (getIntent() != null) {
            teamName = getIntent().getStringExtra(Constants.BundleKeys.TEAM_NAME);
        }
        PlayerQuickViewFragment playerQuickViewFragment
                = PlayerQuickViewFragment.newInstance(exploreModel, teamId, teamName);
        playerQuickViewFragment.show(getFragmentManager(), Constants.Messages.PLAYER_QUICK_VIEW);
    }
}
