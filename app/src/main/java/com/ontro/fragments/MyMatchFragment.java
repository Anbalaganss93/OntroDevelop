package com.ontro.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.ontro.CommonGameMatchRequestActivity;
import com.ontro.Constants;
import com.ontro.CricketGameMatchRequestActivity;
import com.ontro.CricketScoreOverviewActivity;
import com.ontro.FootballScoreOverviewActivity;
import com.ontro.R;
import com.ontro.ScheduleActivity;
import com.ontro.SetGameScoreOverViewActivity;
import com.ontro.SharedObjects;
import com.ontro.StatsBasketballActivity;
import com.ontro.TeamDetailActivity;
import com.ontro.adapters.MatchRequestSectionAdapter;
import com.ontro.adapters.MyMatchAdapter;
import com.ontro.dto.MatchRequestModel;
import com.ontro.dto.MatchRequestResponseModel;
import com.ontro.rest.ApiClient;
import com.ontro.rest.ApiInterface;
import com.ontro.utils.CommonUtils;
import com.ontro.utils.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Android on 27-Feb-17.
 */

public class MyMatchFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, MyMatchAdapter.MatchRequestListener, MatchRequestSectionAdapter.MatchRequestItemListener {
    private String mAuthToken;
    boolean isAttached = false;
    private int mTabPosition;
    private TextView mNomatches;
    private MyMatchAdapter mMyMatchAdapter;
    private SectionedRecyclerViewAdapter mMatchRequestSectionAdapter;
    private MatchRequestSectionAdapter mMatchRequestSectionAdapter1;
    private SuperRecyclerView mMatchRequestRecyclerView;
    private List<MatchRequestModel> mMatchRequestModels = new ArrayList<>();
    private ApiInterface apiInterface;
    private PreferenceHelper preferenceHelper;
    private Dialog progress;

    public static Fragment newInstance(int position) {
        MyMatchFragment myMatchFragment = new MyMatchFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BundleKeys.MATCH_STATUS_POSITION, position);
        myMatchFragment.setArguments(bundle);
        return myMatchFragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        isAttached = true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mymatch_team_squad_layout, container, false);
        mMatchRequestRecyclerView = (SuperRecyclerView) v.findViewById(R.id.squad_listview);
        mNomatches = (TextView) v.findViewById(R.id.team_squad_tv_norequest);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        preferenceHelper = new PreferenceHelper(getActivity(), Constants.APP_NAME, 0);
        mAuthToken = "Bearer " + preferenceHelper.getString("user_token", "");

        progress = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progress.setContentView(R.layout.progressdialog_layout);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mMatchRequestRecyclerView.setLayoutManager(mLayoutManager);
        mMatchRequestRecyclerView.setRefreshListener(MyMatchFragment.this);
        mMatchRequestRecyclerView.setRefreshingColorResources(android.R.color.holo_orange_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_red_light);
        mMatchRequestRecyclerView.hideMoreProgress();
        mMatchRequestRecyclerView.hideProgress();

        mTabPosition = getArguments().getInt(Constants.BundleKeys.MATCH_STATUS_POSITION);
        mMatchRequestRecyclerView.setScrollBarSize(0);
        mMatchRequestRecyclerView.setVerticalScrollBarEnabled(false);

        mNomatches.setVisibility(View.GONE);
        switch (mTabPosition) {
            case 1:
                mNomatches.setText(R.string.nomatchrequest);
                break;
            case 2:
                mNomatches.setText(R.string.nomatchsheduled);
                break;
            case 3:
                mNomatches.setText(R.string.nomatchcompleted);
                break;
        }

        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return super.getSwipeDirs(recyclerView, viewHolder);
            }
        });
//        touchHelper.attachToRecyclerView(mOrderRecyclerView);

        return v;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if(menuVisible) {
            if (CommonUtils.isNetworkAvailable(getActivity())) {
                progress.show();
                mNomatches.setVisibility(View.GONE);
                GetMatchesList();
            } else {
                Toast.makeText(getActivity(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (CommonUtils.isNetworkAvailable(getActivity())) {
            Fragment now = getActivity().getSupportFragmentManager().findFragmentByTag("mymatches");
            if (now != null && now.isVisible()) {
                progress.show();
            } else {
                if (progress != null) {
                    progress.dismiss();
                }
            }
            mNomatches.setVisibility(View.GONE);
            GetMatchesList();
        } else {
            Toast.makeText(getActivity(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }
    }

    public void GetMatchesList() {
        Call<ResponseBody> call = apiInterface.MyMatch(mAuthToken, String.valueOf(mTabPosition));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (mMatchRequestModels.size() != 0) {
                    mMatchRequestModels.clear();
                }
                mNomatches.setVisibility(View.GONE);
                try {
                    if (response.body() != null && response.code() == 200) {
                        if (progress != null && progress.isShowing()) {
                            progress.dismiss();
                        }
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                        JSONObject json = new JSONObject(data);
                        List<MatchRequestModel> receivedMatchRequests = new ArrayList<>();
                        List<MatchRequestModel> requestedMatchRequests = new ArrayList<>();

                        JSONArray array = new JSONArray(json.getString("data"));
                        if (array.length() != 0) {
                            for (int i = 0; i < array.length(); i++) {
                                MatchRequestModel matchRequestModel = new MatchRequestModel();
                                matchRequestModel.setMatchId(array.getJSONObject(i).getString("match_id"));
                                matchRequestModel.setMatchStatus(array.getJSONObject(i).getString("match_status"));
                                matchRequestModel.setMatchType(array.getJSONObject(i).getString("match_type"));
                                String matchDate = array.getJSONObject(i).getString("match_date");
                                matchRequestModel.setMatchDate(CommonUtils.convertDateFormat(matchDate, Constants.DefaultText.YEAR_MONTH_DATE, Constants.DefaultText.DATE_MONTH_YEAR));
                                matchRequestModel.setOpponentTeamId(array.getJSONObject(i).getString("team_id"));
                                matchRequestModel.setOpponentTeamName(array.getJSONObject(i).getString("team_name"));
                                matchRequestModel.setOpponentTeamLogo(array.getJSONObject(i).getString("team_logo"));
                                matchRequestModel.setMyTeamId(array.getJSONObject(i).getString("your_team_id"));
                                matchRequestModel.setMyTeamName(array.getJSONObject(i).getString("your_team_name"));
                                matchRequestModel.setMyTeamLogo(array.getJSONObject(i).getString("your_team_logo"));
                                matchRequestModel.setTeamSport(array.getJSONObject(i).getString("team_sport"));
                                String requestDate = array.getJSONObject(i).getString("request_date");
                                matchRequestModel.setRequestDate(CommonUtils.timeconverter(requestDate));
                                if(array.getJSONObject(i).has("booking_date")) {
                                    String bookingDate = array.getJSONObject(i).getString("booking_date");
                                    matchRequestModel.setBookingDate(CommonUtils.convertDateFormat(bookingDate, Constants.DefaultText.YEAR_MONTH_DATE, Constants.DefaultText.DATE_MONTH_YEAR));
                                }
                                if(array.getJSONObject(i).has("from_time")) {
                                    matchRequestModel.setFromTime(array.getJSONObject(i).getString("from_time"));
                                }
                                if(array.getJSONObject(i).has("to_time")) {
                                    matchRequestModel.setToTime(array.getJSONObject(i).getString("to_time"));
                                }
                                if(array.getJSONObject(i).has("location_name")) {
                                    matchRequestModel.setMatchLocation(array.getJSONObject(i).getString("location_name"));
                                }
                                if(array.getJSONObject(i).has("invite_status")) {
                                    JSONObject statusObject = new JSONObject(array.getJSONObject(i).getString("invite_status"));
                                    matchRequestModel.setStatusMessage(statusObject.getString("status_message"));
                                    matchRequestModel.setStatus(statusObject.getString("status"));
                                }
                                if(mTabPosition == 1) {
                                    if(matchRequestModel.getStatusMessage().equals(Constants.DefaultText.WAITING_FOR_OPPONENT_CONFIRMATION)
                                            || matchRequestModel.getStatusMessage().equals(Constants.DefaultText.MATCH_CONFIRMED_BY_OPPONENT)) {
                                        requestedMatchRequests.add(matchRequestModel);
                                    } else {
                                        receivedMatchRequests.add(matchRequestModel);
                                    }
                                } else {
                                    mMatchRequestModels.add(matchRequestModel);
                                }
                            }
                        } else {
                            mNomatches.setVisibility(View.VISIBLE);
                        }

                        if(mTabPosition == 1) {
                            setAdapterWithHeader(requestedMatchRequests, receivedMatchRequests);
                        } else {
                            setAdapter(mMatchRequestModels);
                        }
                    } else {
                        if (progress != null && progress.isShowing()) {
                            progress.dismiss();
                        }
                        CommonUtils.ErrorHandleMethod(getActivity(), response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //  Log error here since request failed
                CommonUtils.ServerFailureHandleMethod(getActivity(), t);
                if (progress != null && progress.isShowing()) {
                    progress.dismiss();
                }
            }
        });
    }

    private void setAdapterWithHeader(List<MatchRequestModel> requestedMatchRequests, List<MatchRequestModel> receivedMatchRequests) {
        mMatchRequestSectionAdapter = new SectionedRecyclerViewAdapter();
        if(receivedMatchRequests.size() > 0) {
            mMatchRequestSectionAdapter1 = new MatchRequestSectionAdapter(getResources().getString(R.string.received), receivedMatchRequests, getActivity(), this);
            mMatchRequestSectionAdapter.addSection(mMatchRequestSectionAdapter1);
        }
        if(requestedMatchRequests.size() > 0){
            MatchRequestSectionAdapter matchRequestSectionAdapter2 = new MatchRequestSectionAdapter(getResources().getString(R.string.requested), requestedMatchRequests, getActivity(), this);
            mMatchRequestSectionAdapter.addSection(matchRequestSectionAdapter2);
        }
        mMatchRequestRecyclerView.setAdapter(mMatchRequestSectionAdapter);
    }

    private void setAdapter(List<MatchRequestModel> mMatchRequestModels) {
        mMyMatchAdapter = new MyMatchAdapter(getActivity(), mMatchRequestModels, mTabPosition, this);
        mMatchRequestRecyclerView.setAdapter(mMyMatchAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mMyMatchAdapter!=null) mMyMatchAdapter.closeAllItems();
        if(mMatchRequestSectionAdapter1 !=null) mMatchRequestSectionAdapter1.closeAllItems();
    }


    @Override
    public void onRefresh() {
        if (CommonUtils.isNetworkAvailable(getActivity())) {
            if (progress != null && progress.isShowing()) {
                progress.dismiss();
            }
            GetMatchesList();
        } else {
            Toast.makeText(getActivity(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestItemClicked(MatchRequestModel matchRequestModel) {
        preferenceHelper.save("match_id", matchRequestModel.getMatchId());
        preferenceHelper.save("teamid", matchRequestModel.getOpponentTeamId());
        preferenceHelper.save("myteamid", matchRequestModel.getMyTeamId());
        preferenceHelper.save("teamsport", matchRequestModel.getTeamSport());

        if (mTabPosition != 3) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.BundleKeys.MATCH_STATUS_POSITION, mTabPosition);
            bundle.putSerializable(Constants.BundleKeys.MY_MATCH, matchRequestModel);
            switch (matchRequestModel.getTeamSport()) {
                case "1":
                case "2":
                case "3":
                case "5":
                case "6":
                case "7":
                    Intent intent = new Intent(getActivity(), CommonGameMatchRequestActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case "4":
                    Intent cricketIntent = new Intent(getActivity(), CricketGameMatchRequestActivity.class);
                    cricketIntent.putExtras(bundle);
                    startActivity(cricketIntent);
                    break;
            }
        } else {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.BundleKeys.MY_MATCH, matchRequestModel);
            switch (matchRequestModel.getTeamSport()) {
                case "1":
                case "3":
                case "6":
                case "7":
                    Intent intent = new Intent(getActivity(), SetGameScoreOverViewActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case "2":
                    Intent basketballIntent1 = new Intent(getActivity(), StatsBasketballActivity.class);
                    basketballIntent1.putExtras(bundle);
                    startActivity(basketballIntent1);
                    break;
                case "4":
                    Intent cricketIntent = new Intent(getActivity(), CricketScoreOverviewActivity.class);
                    cricketIntent.putExtras(bundle);
                    startActivity(cricketIntent);
                    break;
                case "5":
                    Intent footballIntent = new Intent(getActivity(), FootballScoreOverviewActivity.class);
                    footballIntent.putExtras(bundle);
                    startActivity(footballIntent);
                    break;
            }
        }
    }

    @Override
    public void onRequestAccept(MatchRequestModel matchRequestModel) {
        if (CommonUtils.isNetworkAvailable(getActivity())) {
            progress.show();
            onGotRequestResponseFromPlayer(matchRequestModel, Constants.DefaultText.ONE);
        } else {
            Toast.makeText(getActivity(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestReject(MatchRequestModel matchRequestModel) {
        if (CommonUtils.isNetworkAvailable(getActivity())) {
            progress.show();
            onGotRequestResponseFromPlayer(matchRequestModel, Constants.DefaultText.ZERO);
        } else {
            Toast.makeText(getActivity(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void notifyItemChange(int position) {
        mMatchRequestSectionAdapter.notifyItemChangedInSection(mMatchRequestSectionAdapter1, position);
    }

    @Override
    public void notifyDataSetChanged() {
        mMatchRequestSectionAdapter.notifyDataSetChanged();
    }

    @Override
    public void showMatchInfoDialog(final MatchRequestModel matchRequestModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View matchDialogView = LayoutInflater.from(getActivity()).inflate(R.layout.inflater_match_request_info, null);
        TextView mOpponentTeamNameView = (TextView) matchDialogView.findViewById(R.id.inflater_match_request_info_tv_opponent_team_name);
        TextView mMyTeamNameView = (TextView) matchDialogView.findViewById(R.id.inflater_match_request_info_tv_my_team_name);
        RelativeLayout mProfileViewLayout = (RelativeLayout) matchDialogView.findViewById(R.id.inflater_match_request_info_rl_view_profile);
        RelativeLayout mRequestAcceptLayout = (RelativeLayout) matchDialogView.findViewById(R.id.inflater_match_request_info_rl_request_accept);
        RelativeLayout mRequestRejectLayout = (RelativeLayout) matchDialogView.findViewById(R.id.inflater_match_request_info_rl_request_reject);
        builder.setView(matchDialogView);
        mOpponentTeamNameView.setText(matchRequestModel.getOpponentTeamName() + " ");
        mMyTeamNameView.setText(Constants.DefaultText.VS + " " +matchRequestModel.getMyTeamName());
        final AlertDialog dialog = builder.create();
        mProfileViewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TeamDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.BundleKeys.MY_MATCH, matchRequestModel);
                SharedObjects.id = String.valueOf(matchRequestModel.getOpponentTeamId());
                intent.putExtras(bundle);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        mRequestAcceptLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtils.isNetworkAvailable(getActivity())) {
                    progress.show();
                    dialog.dismiss();
                    onGotRequestResponseFromPlayer(matchRequestModel, Constants.DefaultText.ONE);
                } else {
                    Toast.makeText(getActivity(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
        mRequestRejectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtils.isNetworkAvailable(getActivity())) {
                    progress.show();
                    dialog.dismiss();
                    onGotRequestResponseFromPlayer(matchRequestModel, Constants.DefaultText.ZERO);
                } else {
                    Toast.makeText(getActivity(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    private void onGotRequestResponseFromPlayer(final MatchRequestModel matchRequestModel, final String requestStatus) {
        preferenceHelper.save("match_id", matchRequestModel.getMatchId());
        preferenceHelper.save("teamid", matchRequestModel.getOpponentTeamId());
        preferenceHelper.save("myteamid", matchRequestModel.getMyTeamId());
        preferenceHelper.save("teamsport", matchRequestModel.getTeamSport());
        MatchRequestResponseModel matchRequestResponseModel = new MatchRequestResponseModel();
        matchRequestResponseModel.setMatchId(matchRequestModel.getMatchId());
        matchRequestResponseModel.setMatchStatus(requestStatus);
        Call<ResponseBody> call = apiInterface.MatchConfirmationStatus(mAuthToken, matchRequestResponseModel);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        JSONObject json = new JSONObject(data);
                        String message = json.getString("message");
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        if(requestStatus.equals(Constants.DefaultText.ONE)) {
                            Intent intent = new Intent(getActivity(), ScheduleActivity.class);
                            intent.putExtra(Constants.BundleKeys.MATCH_TYPE,  CommonUtils.MatchType(matchRequestModel.getMatchType()));
                            startActivity(intent);
                        } else {
                            if (CommonUtils.isNetworkAvailable(getActivity())) {
                                progress.show();
                                GetMatchesList();
                            } else {
                                Toast.makeText(getActivity(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        if (response.errorBody() != null) {
                            String error = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(error);
                            String msg = jsonObject.getString("message");
                            String code = jsonObject.getString("code");
                            if (!code.equals("500")) {
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String error = response.message();
                            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (progress != null && progress.isShowing()) {
                    progress.dismiss();
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
                if (progress != null && progress.isShowing()) {
                    progress.dismiss();
                }
            }
        });
    }
}
