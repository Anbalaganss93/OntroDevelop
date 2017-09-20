package com.ontro.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.ontro.Constants;
import com.ontro.HomeActivity;
import com.ontro.R;
import com.ontro.adapters.TeamFormAdapter;
import com.ontro.dto.ScheduleTeamFormModel;
import com.ontro.dto.SquadInfo;
import com.ontro.dto.TeamFormInputModel;
import com.ontro.rest.ApiClient;
import com.ontro.rest.ApiInterface;
import com.ontro.utils.CommonUtils;
import com.ontro.utils.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Android on 27-Feb-17.
 */

public class ScheduleTeamFormFragment extends Fragment implements View.OnClickListener {
    public static ArrayList<ScheduleTeamFormModel> arrayList = new ArrayList<>();
    LinearLayoutManager mLayoutManager;
    private Dialog progress;
    private PreferenceHelper preferenceHelper;
    private ApiInterface apiInterface;
    private SuperRecyclerView mlistView;
    private String match_id, team_id, myTeamId, mMatchType;
    private String playerid = "";
    private String opppnentid = "";
    private Button next;

    public static Fragment newInstance(String matchType, List<SquadInfo> squadInfos) {
        ScheduleTeamFormFragment teamFormFragment = new ScheduleTeamFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BundleKeys.MATCH_TYPE, matchType);
        bundle.putSerializable(Constants.BundleKeys.OPPONENT_SQUADS, (Serializable) squadInfos);
        teamFormFragment.setArguments(bundle);
        return teamFormFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_team_form_layout, container, false);

        Typeface typeface_regular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto_regular.ttf");
        preferenceHelper = new PreferenceHelper(getActivity(), Constants.APP_NAME, 0);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        match_id = preferenceHelper.getString("match_id", "");
        team_id = preferenceHelper.getString("teamid", "");
        myTeamId = preferenceHelper.getString("myteamid", "");

        progress = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progress.setContentView(R.layout.progressdialog_layout);

        mlistView = (SuperRecyclerView) v.findViewById(R.id.team_listview);
        next = (Button) v.findViewById(R.id.next);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        ImageView image1 = (ImageView) getActivity().findViewById(R.id.image1);
        ImageView image2 = (ImageView) getActivity().findViewById(R.id.image2);
        TextView mid_line = (TextView) getActivity().findViewById(R.id.mid_line);

        image1.setImageResource(R.drawable.step_team);
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params2.width = (int) (dm.widthPixels * 0.15);
        params2.height = ((int) (dm.widthPixels * 0.15));
        params2.addRule(RelativeLayout.CENTER_VERTICAL);
        image1.setLayoutParams(params2);

        mid_line.setBackgroundColor(Color.parseColor("#C6C9CD"));
        RelativeLayout.LayoutParams mid_line_param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mid_line_param.width = (int) (dm.widthPixels * 0.15);
        mid_line_param.height = 1;
        mid_line_param.addRule(RelativeLayout.RIGHT_OF, R.id.image1);
        mid_line_param.addRule(RelativeLayout.CENTER_VERTICAL);
        mid_line.setLayoutParams(mid_line_param);

        image2.setImageResource(R.drawable.s4);
        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params3.width = (int) (dm.widthPixels * 0.1);
        params3.height = ((int) (dm.widthPixels * 0.1));
        params3.addRule(RelativeLayout.CENTER_VERTICAL);
        params3.addRule(RelativeLayout.RIGHT_OF, R.id.mid_line);
        image2.setLayoutParams(params3);

        if (getArguments() != null) {
            mMatchType = getArguments().getString(Constants.BundleKeys.MATCH_TYPE);
            if (CommonUtils.MatchType(mMatchType).equals("League")) {
                next.setText("Next");
            } else {
                next.setText("Submit");
            }
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // only for LOLLIPOP and newer versions
            next.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_bg));
        } else {
            next.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_bg_normal));
        }
        mLayoutManager = new LinearLayoutManager(getActivity());
        mlistView.setLayoutManager(mLayoutManager);

        mlistView.hideProgress();
        mlistView.hideMoreProgress();

        if (CommonUtils.isNetworkAvailable(getActivity())) {
            progress.show();
            GetTeamPlayer();
        } else {
            Toast.makeText(getActivity(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }

        next.setTypeface(typeface_regular);

        next.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.next:
                getSelectedPlayers();
                if (CommonUtils.isNetworkAvailable(getActivity())) {
                    if (playerid.length() != 0) {
                        List<SquadInfo> squadInfos = (List<SquadInfo>) getArguments().getSerializable(Constants.BundleKeys.OPPONENT_SQUADS);
                        if (squadInfos != null) {
                            int opponentTeamSize = squadInfos.size();
                            int count = 0;
                            for (ScheduleTeamFormModel dto : arrayList) {
                                if (dto.ischecked()) {
                                    count++;
                                }
                            }
                            if (count == opponentTeamSize) {
                                int isUniqueUserExist = checkSelectedPlayerIsUnique(squadInfos);
                                if (isUniqueUserExist == 0) {
                                    progress.show();
                                    GetTeamFormation();
                                } else {
                                    Toast.makeText(getActivity(), Constants.Messages.SELECTED_PLAYER_EXIST, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), Constants.Messages.SQUAD_MISMATCH, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progress.show();
                            GetTeamFormation();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Select players", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                }
        }
    }

    private int checkSelectedPlayerIsUnique(List<SquadInfo> squadInfos) {
        int count = 0;
        for (ScheduleTeamFormModel scheduleTeamFormModel : arrayList) {
            if (scheduleTeamFormModel.ischecked()) {
                for (SquadInfo squadInfo : squadInfos) {
                    if (scheduleTeamFormModel.getPlayerid().equals(squadInfo.getPlayerId())) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private void getSelectedPlayers() {
        playerid = "";
        StringBuilder sb = new StringBuilder();
        for (ScheduleTeamFormModel dto : arrayList) {
            if (dto.ischecked()) {
                sb.append(dto.getPlayerid());
                sb.append(",");
            }
        }
        playerid = sb.toString().trim();
        if (!TextUtils.isEmpty(playerid)) {
            playerid = playerid.substring(0, playerid.length() - 1);
        }
    }

    public void GetTeamPlayer() {
        progress.show();
        String auth_token = "Bearer " + preferenceHelper.getString("user_token", "");
        Call<ResponseBody> call = apiInterface.TeamPlayerlist(auth_token, myTeamId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (arrayList.size() != 0) {
                        arrayList.clear();
                    }
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                        JSONObject json = new JSONObject(data);
                        JSONArray array = new JSONArray(json.getString("data"));
                        if (array.length() != 0) {
                            for (int i = 0; i < array.length(); i++) {
                                ScheduleTeamFormModel m = new ScheduleTeamFormModel();
                                m.setPlayerid(array.getJSONObject(i).getString("player_id"));
                                String userId = preferenceHelper.getString("user_id", "");
                                if (!userId.equals(m.getPlayerid())) {
                                    m.setPlayer_name(array.getJSONObject(i).getString("player_name"));
                                } else {
                                    m.setPlayer_name(array.getJSONObject(i).getString("player_name") + " (you)");
                                }
                                m.setPlayer_photo(array.getJSONObject(i).getString("player_photo"));
                                if (array.getJSONObject(i).getString("player_location") != null) {
                                    m.setPlayer_location(array.getJSONObject(i).getString("player_location"));
                                }
                                m.setIschecked(false);
                                arrayList.add(m);
                            }
                        }
                        TeamFormAdapter adapter = new TeamFormAdapter(getActivity(), arrayList);
                        mlistView.setAdapter(adapter);
                    } else {
                        CommonUtils.ErrorHandleMethod(getActivity(), response);
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
                CommonUtils.ServerFailureHandleMethod(getActivity(), t);
                if (progress != null && progress.isShowing()) {
                    progress.dismiss();
                }
            }
        });
    }

    public void GetTeamFormation() {
        progress.show();
        String auth_token = "Bearer " + preferenceHelper.getString("user_token", "");
        TeamFormInputModel input = new TeamFormInputModel();
        input.setMatchid(match_id);
        input.setPlayerid(playerid);
        input.setOpponentid(myTeamId);
        Call<ResponseBody> call = apiInterface.FormTeam(auth_token, input);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        Log.d("RESPONSE", data);

                        JSONObject json = new JSONObject(data);

                        if (!mMatchType.isEmpty()) {
                            if (mMatchType.equalsIgnoreCase("League")) {
                                next.setText("NEXT");
                                try {
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.add(R.id.schedule_container, new BookVenueFragment(), "bookvenue");
                                    fragmentTransaction.hide(ScheduleTeamFormFragment.this);
                                    fragmentTransaction.addToBackStack(ScheduleTeamFormFragment.class.getName());
                                    fragmentTransaction.commit();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Intent intent = new Intent(getActivity(), HomeActivity.class);
                                intent.putExtra(Constants.BundleKeys.MY_MATCH, "true");
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        }
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                String error = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(error);
                                String msg = jsonObject.getString("message");
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

                                if (msg.equals("your team already team formed for  this match")) {
                                    if (!TextUtils.isEmpty(mMatchType)) {
                                        if (mMatchType.equalsIgnoreCase("League")) {
                                            next.setText("NEXT");
                                            try {
                                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                fragmentTransaction.add(R.id.schedule_container, new BookVenueFragment(), "bookvenue");
                                                fragmentTransaction.hide(ScheduleTeamFormFragment.this);
                                                fragmentTransaction.addToBackStack(ScheduleTeamFormFragment.class.getName());
                                                fragmentTransaction.commit();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            getActivity().finish();
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                String error = response.message();
                                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            CommonUtils.ErrorHandleMethod(getActivity(), response);
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
                CommonUtils.ServerFailureHandleMethod(getActivity(), t);
                if (progress != null && progress.isShowing()) {
                    progress.dismiss();
                }
            }
        });
    }

}
