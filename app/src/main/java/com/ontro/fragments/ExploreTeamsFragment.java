package com.ontro.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.malinskiy.superrecyclerview.swipe.SwipeDismissRecyclerViewTouchListener;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.ontro.Constants;
import com.ontro.R;
import com.ontro.RecyclerItemClickListener;
import com.ontro.SharedObjects;
import com.ontro.TeamDetailActivity;
import com.ontro.adapters.ExploreTeamAdapter;
import com.ontro.dto.ExploreModel;
import com.ontro.rest.ApiClient;
import com.ontro.rest.ApiInterface;
import com.ontro.utils.CommonUtils;
import com.ontro.utils.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Android on 02-May-17.
 */

public class ExploreTeamsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SwipeDismissRecyclerViewTouchListener.DismissCallbacks {

    public String search_key = "", location = "", type_of_sports = "";
    Typeface typeface_regular;
    private ArrayList<ExploreModel> arrayList = new ArrayList<>();
    private PreferenceHelper preferenceHelper;
    private ApiInterface apiInterface;
    private CircularProgressView progress;
    private TextView listempty, favsportonly;
    private int pagecount = 1;
    private ExploreTeamAdapter adapter;
    private SuperRecyclerView mlistView;
    private EditText location_search;
    private String[] sports=null;
    private MixpanelAPI mMixpanel;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_explore_team, container, false);
        mMixpanel = MixpanelAPI.getInstance(getActivity(), getResources().getString(R.string.mixpanel_token));
        typeface_regular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto_regular.ttf");
        preferenceHelper = new PreferenceHelper(getActivity(), Constants.APP_NAME, 0);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        preferenceHelper.remove("Myteam");
        if (preferenceHelper.contains("typeofsport")) {
            type_of_sports = preferenceHelper.getString("typeofsport", "");
            location = preferenceHelper.getString("location", "");
        }

        mlistView = (SuperRecyclerView) v.findViewById(R.id.fragment_explore_team_rv);
        location_search = (EditText) getActivity().findViewById(R.id.fragment_explore_home_et_location_search);
        favsportonly = (TextView) v.findViewById(R.id.favsportonly);
        progress = (CircularProgressView) v.findViewById(R.id.fragment_explore_team_progress_view);
        listempty = (TextView) v.findViewById(R.id.fragment_explore_team_tv_empty);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mlistView.setLayoutManager(mLayoutManager);
        mlistView.setRefreshListener(ExploreTeamsFragment.this);
        mlistView.setRefreshingColorResources(android.R.color.holo_orange_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_red_light);
        adapter = new ExploreTeamAdapter(getActivity(), arrayList);
        mlistView.setAdapter(adapter);
        mlistView.hideProgress();
        mlistView.hideMoreProgress();
        listempty.setVisibility(View.GONE);
        favsportonly.setVisibility(View.GONE);
        mlistView.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
                // Fetch more from Api or DB
                progress.setVisibility(View.GONE);
                mlistView.hideProgress();
                mlistView.hideMoreProgress();
                mlistView.showMoreProgress();
                if (CommonUtils.isNetworkAvailable(getActivity())) {
                    pagecount++;
                    ExploreTeam_servercall();
                }
            }
        }, 10);

        mlistView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                try {
                    JSONObject eventJsonObject = new JSONObject();
                    eventJsonObject.put("UserId", preferenceHelper.getString("user_id", ""));
                    eventJsonObject.put("UserEmail", preferenceHelper.getString("user_email", ""));
                    eventJsonObject.put("UserName",  preferenceHelper.getString("user_name", ""));
                    eventJsonObject.put("TeamName", arrayList.get(position).getExploreName());
                    eventJsonObject.put("TeamSport", CommonUtils.sportNameCheck(arrayList.get(position).getExploreSport()));
                    mMixpanel.track("ExploreTeamClick", eventJsonObject);
                } catch (JSONException e) {
                    Log.e("Ontro", "Unable to add properties to JSONObject", e);
                }
                SharedObjects.id = arrayList.get(position).getExploreId();
                Intent intent = new Intent(getActivity(), TeamDetailActivity.class);
                intent.putExtra(Constants.BundleKeys.INVITE_TO_MATCH, Constants.DefaultText.ONE);
                intent.putExtra(Constants.BundleKeys.IS_OWNER, arrayList.get(position).getIsOwner());
                intent.putExtra(Constants.BundleKeys.TEAM_ID, arrayList.get(position).getExploreId());
                intent.putExtra(Constants.BundleKeys.SPORT_ID, arrayList.get(position).getExploreSport());
                intent.putExtra(Constants.BundleKeys.TEAM_NAME, arrayList.get(position).getExploreName());
                startActivity(intent);
            }
        }));

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (CommonUtils.isNetworkAvailable(getActivity())) {
            progress.setVisibility(View.VISIBLE);
            mlistView.hideProgress();
            mlistView.hideMoreProgress();


            if (preferenceHelper.contains("typeofsport")) {
                favsportonly.setVisibility(View.GONE);
                type_of_sports = preferenceHelper.getString("typeofsport", "");
                location = preferenceHelper.getString("location", "");
                if (type_of_sports.equals("")) {
                    favsportonly.setVisibility(View.VISIBLE);
                    type_of_sports = preferenceHelper.getString("single_sport", "");
                    if (preferenceHelper.contains("title")) {
                        favsportonly.setText(preferenceHelper.getString("title", ""));
                    } else {
                        favsportonly.setVisibility(View.GONE);
                    }
                }
            } else {
                favsportonly.setVisibility(View.VISIBLE);
                String playerSports = preferenceHelper.getString("player_sports", "");
                if (!playerSports.isEmpty()) {
                    sports = playerSports.split(",");
                    if(sports!=null) {
                        if (sports.length > 0) {
                            preferenceHelper.save("single_sport", sports[0]);
                        }
                    }
                }
                if (preferenceHelper.contains("typeofsport")) {
                    type_of_sports = preferenceHelper.getString("typeofsport", "");
                }
                if (preferenceHelper.getString("Fromfilter", "").equals("true")) {
                    if(sports!=null) {
                        if (sports.length > 0) {
                            favsportonly.setVisibility(View.VISIBLE);
                            preferenceHelper.save("title", "Showing " + CommonUtils.sportNameCheck(sports[0]) + " only");
                            if (preferenceHelper.contains("title")) {
                                favsportonly.setText(preferenceHelper.getString("title", ""));
                            } else {
                                favsportonly.setVisibility(View.GONE);
                            }
                        }
                    }
                }
                type_of_sports = preferenceHelper.getString("single_sport", "");
            }
            search_key = preferenceHelper.getString("searchkey", "");

            if (ExploreHome.progress_show.equals("2")) {
                progress.setVisibility(View.GONE);
            }
            ExploreTeam_servercall();
        } else {
            Toast.makeText(getActivity(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }
    }

    public void ExploreTeam_servercall() {
        String auth_token = "Bearer " + preferenceHelper.getString("user_token", "");
        Call<ResponseBody> call = apiInterface.ExploreTeams(auth_token, search_key, location, type_of_sports, String.valueOf(pagecount));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (pagecount == 1) {
                        adapter.clear();
                    }
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                        JSONObject json = new JSONObject(data);
                        JSONObject datajson = new JSONObject(json.getString("data"));
                        JSONArray array = new JSONArray(datajson.getString("data"));
                        if (array.length() != 0) {
                            listempty.setVisibility(View.GONE);
                            for (int i = 0; i < array.length(); i++) {
                                ExploreModel m = new ExploreModel();
                                m.setExploreId(array.getJSONObject(i).getString("team_id"));
                                m.setExploreName(array.getJSONObject(i).getString("team_name"));
                                m.setExploreImage(array.getJSONObject(i).getString("team_logo"));
                                m.setExploreSport(array.getJSONObject(i).getString("teams_sport"));
                                m.setExploreLocation(array.getJSONObject(i).getString("team_location"));
                                m.setExploreSport(array.getJSONObject(i).getString("teams_sport"));
                                favsportonly.setText("Showing " + CommonUtils.sportNameCheck(m.getExploreSport()) + " only");
                                if (preferenceHelper.getString("Fromfilter", "").equals("true")) {
                                    preferenceHelper.save("title", "Showing " + CommonUtils.sportNameCheck(m.getExploreSport()) + " only");
                                }
                                m.setIsChecked(0);
                                m.setIsOwner("0");
                                if (array.getJSONObject(i).has("is_owner")) {
                                    m.setIsOwner(array.getJSONObject(i).getString("is_owner"));
                                }
                                if (!m.getIsOwner().equals("1")) {
                                    adapter.add(m);
                                }
                            }
                            mlistView.setVisibility(View.VISIBLE);
                        } else {
                            if (pagecount == 1) {
                                listempty.setVisibility(View.VISIBLE);
                                listempty.setText(getResources().getString(R.string.no_team_found));
                            }
                        }
                    } else {
                        CommonUtils.ErrorHandleMethod(getActivity(),response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mlistView.hideProgress();
                mlistView.hideMoreProgress();
                progress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //  Log error here since request failed
                CommonUtils.ServerFailureHandleMethod(getActivity(), t);
                mlistView.hideMoreProgress();
                progress.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onRefresh() {
        mlistView.setRefreshListener(this);
        if (CommonUtils.isNetworkAvailable(getActivity())) {
            mlistView.hideProgress();
            mlistView.hideMoreProgress();
            pagecount = 1;
            progress.setVisibility(View.GONE);
            ExploreTeam_servercall();
        } else {
            Toast.makeText(getActivity(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean canDismiss(int position) {
        return false;
    }

    @Override
    public void onDismiss(RecyclerView recyclerView, int[] reverseSortedPositions) {

    }
}
