package com.ontro.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.ontro.Constants;
import com.ontro.CreateTeamActivity;
import com.ontro.MyTeamDataBaseHelper;
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

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyTeamFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private View mRootView;
    private RecyclerView mTeamRecyclerView;
    private RelativeLayout mTeamEmptyView;
    private ArrayList<SportModel> arrayList = new ArrayList<>();
    private TeamAdapter adapter;
    private PreferenceHelper preferenceHelper;
    private ApiInterface apiInterface;
    private CircularProgressView mProgressView;
    private MyTeamDataBaseHelper myTeamDataBaseHelper;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private MixpanelAPI mMixpanel;
    private FloatingActionButton mCreateTeamFabView;
    private Button mCreateTeamButton;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_my_team, container, false);
        mMixpanel = MixpanelAPI.getInstance(getActivity(), getResources().getString(R.string.mixpanel_token));
        preferenceHelper = new PreferenceHelper(getActivity(), Constants.APP_NAME, 0);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        myTeamDataBaseHelper = new MyTeamDataBaseHelper(getActivity());
        initView();
        setListener();
        return mRootView;
    }

    private void initView() {
        mTeamRecyclerView = (RecyclerView) mRootView.findViewById(R.id.fragment_my_team_rv);
        mProgressView = (CircularProgressView) mRootView.findViewById(R.id.fragment_my_team_progress_view);
        mTeamEmptyView = (RelativeLayout) mRootView.findViewById(R.id.fragment_my_team_rl_empty_team);
        mCreateTeamButton = (Button) mRootView.findViewById(R.id.fragment_my_team_btn_create_team);
        mCreateTeamFabView = (FloatingActionButton) mRootView.findViewById(R.id.fragment_my_team_fab_create_team);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.fragment_my_team_swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimary, R.color.colorPrimary, R.color.colorPrimary);
        LinearLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mTeamRecyclerView.setLayoutManager(mLayoutManager);
        mTeamRecyclerView.setLongClickable(true);
        Typeface typeface_regular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto_regular.ttf");
        mCreateTeamButton.setTypeface(typeface_regular);
    }

    private void setListener() {
        mCreateTeamButton.setOnClickListener(this);
        mCreateTeamFabView.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (CommonUtils.isNetworkAvailable(getActivity())) {
            mProgressView.setVisibility(View.VISIBLE);
            GetTeams();
        } else {
            Toast.makeText(getActivity(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }
    }

    public void GetTeams() {
        String auth_token = "Bearer " + preferenceHelper.getString("user_token", "");
        Call<ResponseBody> call = apiInterface.MyTeam(auth_token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        if (arrayList.size() != 0) {
                            arrayList.clear();
                        }
                        myTeamDataBaseHelper.deletealldata();
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                        JSONObject json = new JSONObject(data);

                        if (!json.getString("data").equals("0")) {
                            mCreateTeamFabView.setVisibility(View.VISIBLE);
                            JSONArray array = new JSONArray(json.getString("data"));
                            mTeamEmptyView.setVisibility(View.GONE);
                            for (int i = 0; i < array.length(); i++) {
                                SportModel m = new SportModel();
                                m.setSportid(array.getJSONObject(i).getString("team_id"));
                                FirebaseMessaging.getInstance().subscribeToTopic(m.getSportid());
                                m.setSportname(array.getJSONObject(i).getString("team_name"));
                                m.setIswoner(array.getJSONObject(i).getInt("is_owner"));
                                m.setPlayerid(array.getJSONObject(i).getInt("owner"));
                                String logo = array.getJSONObject(i).getString("team_logo").equals("null") ? "http://euroguide.fourfourtwo.com/quiz-nation/media/theme/badge-wal.png" : array.getJSONObject(i).getString("team_logo");
                                m.setTeamlogo(logo);
                                String id = array.getJSONObject(i).getString("sport");
                                m.setSportimage(CommonUtils.sportCheck(id));
                                m.setLocation(array.getJSONObject(i).getString("team_location"));
                                String percentage = array.getJSONObject(i).getString("progress").equals("null") ? "0" : array.getJSONObject(i).getString("progress");
                                m.setProgress_percent(percentage);
                                m.setBatchimage(CommonUtils.batchCheck(array.getJSONObject(i).getString("badge")));
                                m.setTeamAbout(array.getJSONObject(i).getString("team_about"));
                                arrayList.add(m);
                                if (array.getJSONObject(i).getInt("is_owner") == 1) {  //1 is owner 0- is not woner of the team
                                    myTeamDataBaseHelper.insertContact(array.getJSONObject(i).getString("team_name"), array.getJSONObject(i).getString("team_id"), array.getJSONObject(i).getString("sport"));
                                }
                            }
                        } else {
                            mCreateTeamFabView.setVisibility(View.GONE);
                            mTeamEmptyView.setVisibility(View.VISIBLE);
                        }
                        adapter = new TeamAdapter(getActivity(), arrayList);
                        mTeamRecyclerView.setAdapter(adapter);
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
                            }
                        } else {
                            String error = response.message();
                            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                        }
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
                try {
                    if (t instanceof SocketTimeoutException) {
                        Toast.makeText(getActivity(), R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mProgressView.setVisibility(View.GONE);
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment_my_team_btn_create_team:
            case R.id.fragment_my_team_fab_create_team:
                preferenceHelper.save("user_location", "");
                try {
                    JSONObject eventJsonObject = new JSONObject();
                    eventJsonObject.put("UserId", preferenceHelper.getString("user_id", ""));
                    eventJsonObject.put("UserName", preferenceHelper.getString("user_name", ""));
                    eventJsonObject.put("UserEmail", preferenceHelper.getString("user_email", ""));
                    mMixpanel.track("CreateTeam", eventJsonObject);
                } catch (JSONException e) {
                    Log.e("Ontro", "Unable to add properties to JSONObject", e);
                }
                Intent intent = new Intent(getActivity(), CreateTeamActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onRefresh() {
        if (CommonUtils.isNetworkAvailable(getActivity())) {
            GetTeams();
        } else {
            Toast.makeText(getActivity(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }
    }
}
