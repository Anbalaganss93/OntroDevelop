package com.ontro.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.ontro.Constants;
import com.ontro.NewDiscussionActivity;
import com.ontro.R;
import com.ontro.adapters.DiscussionAdapter;
import com.ontro.dto.DiscussionListInput;
import com.ontro.dto.DiscussionModel;
import com.ontro.rest.ApiClient;
import com.ontro.rest.ApiInterface;
import com.ontro.utils.CommonUtils;
import com.ontro.utils.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Android on 20-Feb-17.
 */

public class DiscussionFragment extends Fragment implements View.OnClickListener {
    private LinearLayoutManager mLayoutManager;
    private Typeface typeface_regular;
    private DiscussionAdapter adapter;
    private RecyclerView mlistView;
    private Handler handler;
    private Button startdiscussion;
    private Dialog dialog;
    private String discussion_show = "1";
    private PreferenceHelper preferenceHelper;
    private ApiInterface apiInterface;
    private TextView show_discussion_state, listempty;
    private CircularProgressView progress;
    private ArrayList<DiscussionModel> arrayList = new ArrayList<>();
    private RadioButton latest, trending;
    private int buzzstate = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_discussion_layout, container, false);

        typeface_regular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto_regular.ttf");
        preferenceHelper = new PreferenceHelper(getActivity(), Constants.APP_NAME, 0);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        mlistView = (RecyclerView) v.findViewById(R.id.discussion_listview);
        show_discussion_state = (TextView) v.findViewById(R.id.show_discussion_state);
        listempty = (TextView) v.findViewById(R.id.activity_explore_player_list_tv_empty);
        startdiscussion = (Button) v.findViewById(R.id.startdiscussion);

        progress = (CircularProgressView) v.findViewById(R.id.activity_explore_player_list_progress_view);
        progress.setVisibility(View.GONE);
        listempty.setVisibility(View.GONE);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setAutoMeasureEnabled(true);
        mlistView.setLayoutManager(mLayoutManager);
        mlistView.setNestedScrollingEnabled(false);
        handler = new Handler();

        show_discussion_state.setOnClickListener(this);
        startdiscussion.setOnClickListener(this);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (CommonUtils.isNetworkAvailable(getActivity())) {
            Discussion_servercall();
        } else {
            Toast.makeText(getActivity(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }
    }

    public void Discussion_servercall() {
        progress.setVisibility(View.VISIBLE);
        String auth_token = "Bearer " + preferenceHelper.getString("user_token", "");
        DiscussionListInput m=new DiscussionListInput();
        m.setShow(discussion_show);
        Call<ResponseBody> call = apiInterface.Discusionlist(auth_token, m);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        if (arrayList.size() != 0) {
                            arrayList.clear();
                        }
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                        JSONObject json = new JSONObject(data);
                        if(json.getString("data").length()!=0) {
                            JSONArray array = new JSONArray(json.getString("data"));
                            if (array.length() != 0) {
                                listempty.setVisibility(View.GONE);
                                for (int i = 0; i < array.length(); i++) {
                                    DiscussionModel m = new DiscussionModel();
                                    m.setPlayer_id(array.getJSONObject(i).getString("player_id"));
                                    m.setUser_comment(array.getJSONObject(i).getString("content"));
                                    String createdate = array.getJSONObject(i).getString("created_at");
                                    m.setUser_seen_hours(CommonUtils.timeconverter(createdate));
                                    m.setUser_question(array.getJSONObject(i).getString("title"));
                                    m.setUser_name(array.getJSONObject(i).getString("name"));
                                    m.setUser_image(array.getJSONObject(i).getString("profile_image"));
                                    m.setComment_count(array.getJSONObject(i).getString("commentsCount"));
                                    m.setDiscussionid(array.getJSONObject(i).getString("discussion_id"));
                                    m.setIscommented(array.getJSONObject(i).getString("is_commented"));
                                    arrayList.add(m);
                                }
                            } else {
                                listempty.setVisibility(View.VISIBLE);
                            }
                        }else{
                            listempty.setVisibility(View.VISIBLE);
                        }
                        adapter = new DiscussionAdapter(getActivity(), arrayList);
                        mlistView.setAdapter(adapter);
                    } else {
                        if (response.errorBody() != null) {
                            String error = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(error);
                            String msg = jsonObject.getString("message");
                            String code = jsonObject.getString("code");
                            if(!code.equals("500")) {
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getActivity(),R.string.error500, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String error = response.message();
                            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progress.setVisibility(View.GONE);
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
                progress.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.show_discussion_state:
                dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
                Window window = dialog.getWindow();
                assert window != null;
                window.setGravity(Gravity.BOTTOM);
                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                dialog.setTitle(null);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setContentView(R.layout.discussion_dialog);
                latest = (RadioButton) dialog.findViewById(R.id.latest);
                trending = (RadioButton) dialog.findViewById(R.id.trending);
                latest.setTypeface(typeface_regular);
                trending.setTypeface(typeface_regular);

                if (buzzstate == 0) {
                    latest.setChecked(true);
                    trending.setChecked(false);
                } else {
                    latest.setChecked(false);
                    trending.setChecked(true);
                }

                latest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        show_discussion_state.setText(getResources().getString(R.string.latest));
                        trending.setChecked(false);
                        buzzstate = 0;
                        discussion_show = "1";
                        latest.setChecked(true);
                        if (CommonUtils.isNetworkAvailable(getActivity())) {
                            Discussion_servercall();
                        } else {
                            Toast.makeText(getActivity(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                        }

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                            }
                        }, 100);

                    }
                });

                trending.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        latest.setChecked(false);
                        trending.setChecked(true);
                        buzzstate = 1;
                        discussion_show = "2";
                        show_discussion_state.setText(getResources().getString(R.string.trending));
                        if (CommonUtils.isNetworkAvailable(getActivity())) {
                            Discussion_servercall();
                        } else {
                            Toast.makeText(getActivity(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                        }
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                            }
                        }, 100);
                    }
                });
                dialog.show();
                break;

            case R.id.startdiscussion:
                Intent intent = new Intent(getActivity(), NewDiscussionActivity.class);
                startActivity(intent);
                break;
        }
    }
}
