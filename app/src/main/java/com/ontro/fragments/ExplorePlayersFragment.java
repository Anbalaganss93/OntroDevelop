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

import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.malinskiy.superrecyclerview.swipe.SwipeDismissRecyclerViewTouchListener;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.ontro.Constants;
import com.ontro.PlayerProfileActivity;
import com.ontro.R;
import com.ontro.RecyclerItemClickListener;
import com.ontro.adapters.ExplorePlayerAdapter;
import com.ontro.dto.ExploreModel;
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

/**
 * Created by Android on 02-May-17.
 */

public class ExplorePlayersFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SwipeDismissRecyclerViewTouchListener.DismissCallbacks {

    private Typeface typeface_regular;
    private ArrayList<ExploreModel> arrayList = new ArrayList<>();
    private PreferenceHelper preferenceHelper;
    private ApiInterface apiInterface;
    private TextView listempty, mPlayerSport;
    private int pagecount = 1;
    private SuperRecyclerView mlistView;
    private ExplorePlayerAdapter adapter;
    private String search_key = "", location = "", type_of_sports = "";
    private EditText location_search;
    private String[] sports = null;
    private MixpanelAPI mMixpanel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_explore_team, container, false);
        mMixpanel = MixpanelAPI.getInstance(getActivity(), getResources().getString(R.string.mixpanel_token));
        typeface_regular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto_regular.ttf");
        preferenceHelper = new PreferenceHelper(getActivity(), Constants.APP_NAME, 0);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Constants.refresh = true;

        if (preferenceHelper.contains("typeofsport")) {
            type_of_sports = preferenceHelper.getString("typeofsport", "");
            location = preferenceHelper.getString("location", "");
        }

        preferenceHelper.remove("Myteam");
        mlistView = (SuperRecyclerView) v.findViewById(R.id.fragment_explore_team_rv);
        location_search = (EditText) getActivity().findViewById(R.id.activity_explore_player_list_et_location_search);
        listempty = (TextView) v.findViewById(R.id.fragment_explore_team_tv_empty);
        mPlayerSport = (TextView) v.findViewById(R.id.favsportonly);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mlistView.setLayoutManager(mLayoutManager);
        mlistView.setRefreshListener(ExplorePlayersFragment.this);
        mlistView.setRefreshingColorResources(android.R.color.holo_orange_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_red_light);
        adapter = new ExplorePlayerAdapter(getActivity(), arrayList);
        mlistView.setAdapter(adapter);
        mlistView.hideProgress();
        mlistView.hideMoreProgress();
        listempty.setVisibility(View.GONE);
        mPlayerSport.setVisibility(View.GONE);

        mlistView.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
                // Fetch more from Api or DB
                mlistView.hideMoreProgress();
                mlistView.showMoreProgress();
                if (CommonUtils.isNetworkAvailable(getActivity())) {
                    pagecount++;
                    ExplorePlayers_servercall();
                }
            }
        }, 10);

        mlistView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                try {
                    JSONObject eventJsonObject = new JSONObject();
                    eventJsonObject.put("UserId", preferenceHelper.getString("user_id", ""));
                    eventJsonObject.put("UserName",  preferenceHelper.getString("user_name", ""));
                    eventJsonObject.put("UserEmail", preferenceHelper.getString("user_email", ""));
                    eventJsonObject.put("PlayerName", arrayList.get(position).getExploreName());
                    eventJsonObject.put("PlayerSport", CommonUtils.sportNameCheck(arrayList.get(position).getExploreSport()));
                    mMixpanel.track("ExplorePlayerProfile", eventJsonObject);
                } catch (JSONException e) {
                    Log.e("Ontro", "Unable to add properties to JSONObject", e);
                }
                Intent intent = new Intent(getActivity(), PlayerProfileActivity.class);
                intent.putExtra(Constants.BundleKeys.PLAYER_ID, arrayList.get(position).getExploreId());
                intent.putExtra(Constants.BundleKeys.SPORT_ID, arrayList.get(position).getExploreSport());
                intent.putExtra(Constants.BundleKeys.PLAYER_NAME, arrayList.get(position).getExploreName());
                startActivity(intent);
            }
        }));
        return v;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible) {
            if (preferenceHelper != null) {
                if (preferenceHelper.getString("Fromfilter", "").equals("true")) {
                    if (sports != null) {
                        if (sports.length > 0) {
                            mPlayerSport.setVisibility(View.VISIBLE);
                            preferenceHelper.save("title", "Showing " + CommonUtils.sportNameCheck(sports[0]) + " only");
                        }
                    }
                }

                if (preferenceHelper.contains("title")) {
                    mPlayerSport.setText(preferenceHelper.getString("title", ""));
                } else {
                    mPlayerSport.setVisibility(View.GONE);
                }
                if (preferenceHelper.contains("typeofsport")) {
                    type_of_sports = preferenceHelper.getString("typeofsport", "");
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LoadPlayerList();
    }

    public void ExplorePlayers_servercall() {
        String auth_token = "Bearer " + preferenceHelper.getString("user_token", "");
        Call<ResponseBody> call = apiInterface.ExplorePlayers(auth_token, search_key, location, type_of_sports, String.valueOf(pagecount));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        Constants.refresh = false;
                        if (pagecount == 1) {
                            adapter.clear();
                        }
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                        JSONObject json = new JSONObject(data);
                        JSONObject datajson = new JSONObject(json.getString("data"));
                        JSONArray array = new JSONArray(datajson.getString("data"));
                        if (array.length() != 0) {
                            listempty.setVisibility(View.GONE);
                            for (int i = 0; i < array.length(); i++) {
                                ExploreModel m = new ExploreModel();
                                m.setExploreId(array.getJSONObject(i).getString("player_id"));
                                m.setExploreName(array.getJSONObject(i).getString("player_name"));
                                m.setExploreImage(array.getJSONObject(i).getString("player_photo"));
                                m.setExploreLocation(array.getJSONObject(i).getString("location"));
                                m.setExploreSport(array.getJSONObject(i).getString("sport"));
                                if (array.getJSONObject(i).has("badge")) {
                                    String badge = !array.getJSONObject(i).getString("badge").equalsIgnoreCase("null") ? array.getJSONObject(i).getString("badge") : "0";
                                    m.setExploreBatch(Integer.parseInt(badge));
                                }
                                m.setIsChecked(0);
                                String userId = preferenceHelper.getString("user_id", "");
                                if (!userId.equals(m.getExploreId())) {
                                    adapter.add(m);
                                }
                            }
                            mlistView.setVisibility(View.VISIBLE);
                        } else {
                            if (pagecount == 1) {
                                listempty.setVisibility(View.VISIBLE);
                                listempty.setText(getResources().getString(R.string.no_players));
                            }
                        }
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
                                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String error = response.message();
                            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mlistView.hideMoreProgress();
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
                mlistView.hideMoreProgress();
            }
        });
    }

    @Override
    public void onRefresh() {
        mlistView.setRefreshListener(this);
        if (CommonUtils.isNetworkAvailable(getActivity())) {
            pagecount = 1;
            ExplorePlayers_servercall();
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

    private void LoadPlayerList(){
        if (CommonUtils.isNetworkAvailable(getActivity())) {
            mlistView.hideProgress();
            mlistView.hideMoreProgress();

            if (preferenceHelper.contains("typeofsport")) {
                mPlayerSport.setVisibility(View.GONE);
                type_of_sports = preferenceHelper.getString("typeofsport", "");
                location = preferenceHelper.getString("location", "");

                if (type_of_sports.equals("")) {
                    mPlayerSport.setVisibility(View.VISIBLE);
                    type_of_sports = preferenceHelper.getString("single_sport", "");
                    if (preferenceHelper.contains("title")) {
                        mPlayerSport.setText(preferenceHelper.getString("title", ""));
                    } else {
                        mPlayerSport.setVisibility(View.GONE);
                    }
                }

            } else {
                mPlayerSport.setVisibility(View.VISIBLE);
                String playerSports = preferenceHelper.getString("player_sports", "");
                if (!playerSports.isEmpty()) {
                    sports = playerSports.split(",");
                    if (sports != null) {
                        if (sports.length > 0) {
                            preferenceHelper.save("single_sport", sports[0]);
                        }
                    }
                }
                if (preferenceHelper.contains("typeofsport")) {
                    type_of_sports = preferenceHelper.getString("typeofsport", "");
                }
                if (preferenceHelper.getString("Fromfilter", "").equals("true")) {
                    if (sports != null) {
                        if (sports.length > 0) {
                            mPlayerSport.setVisibility(View.VISIBLE);
                            preferenceHelper.save("title", "Showing " + CommonUtils.sportNameCheck(sports[0]) + " only");
                            if (preferenceHelper.contains("title")) {
                                mPlayerSport.setText(preferenceHelper.getString("title", ""));
                            } else {
                                mPlayerSport.setVisibility(View.GONE);
                            }
                        }
                    }
                }
                type_of_sports = preferenceHelper.getString("single_sport", "");
            }
            search_key = preferenceHelper.getString("searchkey", "");
            ExplorePlayers_servercall();
        } else {
            Toast.makeText(getActivity(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }
    }
}
