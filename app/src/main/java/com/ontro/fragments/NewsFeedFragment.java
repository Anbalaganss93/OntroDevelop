package com.ontro.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.ontro.Constants;
import com.ontro.R;
import com.ontro.adapters.NewsFeedAdapter;
import com.ontro.dto.NewsFeedModel;
import com.ontro.rest.ApiClient;
import com.ontro.rest.ApiInterface;
import com.ontro.utils.CommonUtils;
import com.ontro.utils.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by umm on 22-Feb-17.
 */

public class NewsFeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    LinearLayoutManager mLayoutManager;
    private NewsFeedAdapter adapter;
    private SuperRecyclerView mlistView;
    private CircularProgressView progress;
    private TextView listempty;
    private ArrayList<NewsFeedModel> arrayList = new ArrayList<>();
    private PreferenceHelper preferenceHelper;
    private ApiInterface apiInterface;
    private SwipeRefreshLayout swipeLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_newsfeed_layout, container, false);
        mlistView = (SuperRecyclerView) v.findViewById(R.id.discussion_listview);
        progress = (CircularProgressView) v.findViewById(R.id.activity_explore_player_list_progress_view);
        listempty = (TextView) v.findViewById(R.id.activity_explore_player_list_tv_empty);
        progress.setVisibility(View.GONE);
        listempty.setVisibility(View.GONE);

        preferenceHelper = new PreferenceHelper(getActivity(), Constants.APP_NAME, 0);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mlistView.setLayoutManager(mLayoutManager);
        mlistView.hideMoreProgress();
        mlistView.hideProgress();

        if (CommonUtils.isNetworkAvailable(getActivity())) {
            progress.setVisibility(View.VISIBLE);
            getNewsFeeds();
        } else {
            progress.setVisibility(View.GONE);
            Toast.makeText(getActivity(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }

        swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        return v;
    }

    public void getNewsFeeds() {
        String auth_token = "Bearer " + preferenceHelper.getString("user_token", "");
        Call<ResponseBody> call = apiInterface.getNewsFeeds(auth_token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progress.setVisibility(View.GONE);
                if (swipeLayout.isRefreshing()) {
                    swipeLayout.setRefreshing(false);
                }
                try {
                    if (response.body() != null && response.code() == 200) {
                        if (arrayList.size() != 0) {
                            arrayList.clear();
                        }
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                        JSONObject json = new JSONObject(data);
                        JSONArray array = new JSONArray(json.getString("data"));
                        if (array.length() != 0) {
                            listempty.setVisibility(View.GONE);
                            for (int i = 0; i < array.length(); i++) {
                                NewsFeedModel newsFeedModel = new NewsFeedModel();
                                newsFeedModel.setType(Integer.parseInt(array.getJSONObject(i).getString("type")));
                                newsFeedModel.setLikes(array.getJSONObject(i).getString("likes"));
                                String image = array.getJSONObject(i).getString("image");
                                newsFeedModel.setImage(image);
                                newsFeedModel.setDescription(array.getJSONObject(i).getString("content"));
                                newsFeedModel.setNewsfeed_id(array.getJSONObject(i).getString("news_id"));
                                newsFeedModel.setIs_liked(array.getJSONObject(i).getString("is_liked"));
                                String createdate = array.getJSONObject(i).getString("created_at");
                                newsFeedModel.setHours_ago(CommonUtils.timeconverter(createdate));
                                newsFeedModel.setTournamentId(array.getJSONObject(i).getInt("tournaments_id"));
                                arrayList.add(newsFeedModel);
                            }
                        } else {
                            listempty.setVisibility(View.VISIBLE);
                        }
                        adapter = new NewsFeedAdapter(getActivity(), arrayList);
                        mlistView.setAdapter(adapter);
                    } else {
                        CommonUtils.ErrorHandleMethod(getActivity(),response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //  Log error here since request failed
                CommonUtils.ServerFailureHandleMethod(getActivity(), t);
                progress.setVisibility(View.GONE);
                if (swipeLayout.isRefreshing()) {
                    swipeLayout.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        if (CommonUtils.isNetworkAvailable(getActivity())) {
            progress.setVisibility(View.GONE);
            getNewsFeeds();
        } else {
            progress.setVisibility(View.GONE);
            Toast.makeText(getActivity(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }
    }
}
