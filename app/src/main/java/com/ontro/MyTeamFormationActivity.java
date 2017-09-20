package com.ontro;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.ontro.adapters.MyTeamFormAdapter;
import com.ontro.dto.ScheduleConfirmInput;
import com.ontro.dto.ScheduleTeamFormModel;
import com.ontro.dto.TeamFormInputModel;
import com.ontro.rest.ApiClient;
import com.ontro.rest.ApiInterface;
import com.ontro.utils.CommonUtils;
import com.ontro.utils.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Android on 27-Feb-17.
 */

public class MyTeamFormationActivity extends AppCompatActivity implements View.OnClickListener {
    public List<ScheduleTeamFormModel> arrayList = new ArrayList<>();
    LinearLayoutManager mLayoutManager;
    private Dialog progress;
    private PreferenceHelper preferenceHelper;
    private ApiInterface apiInterface;
    private SuperRecyclerView mlistView;
    private String match_id, myteamid;
    private String playerid = "";
    private String venueBookingId = "";
    private Intent intent;
    private ImageView back;
//    private ScheduleConfirmationListener mScheduleConfirmationListener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myteam_form_layout);
        overridePendingTransition(R.anim.slide_in_right, R.anim.pause);
        Typeface typeface_regular = Typeface.createFromAsset(MyTeamFormationActivity.this.getAssets(), "fonts/roboto_regular.ttf");
        preferenceHelper = new PreferenceHelper(MyTeamFormationActivity.this, Constants.APP_NAME, 0);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        match_id = preferenceHelper.getString("match_id", "");
        myteamid = preferenceHelper.getString("myteamid", "");
        intent = getIntent();
        if (intent != null) {
            venueBookingId = intent.getStringExtra("Venueid");
        }
        progress = new Dialog(MyTeamFormationActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progress.setContentView(R.layout.progressdialog_layout);

        mlistView = (SuperRecyclerView) findViewById(R.id.team_listview);
        Button accept = (Button) findViewById(R.id.accept);
        back = (ImageView) findViewById(R.id.activity_explore_player_list_iv_back);

        DisplayMetrics dm = new DisplayMetrics();
        MyTeamFormationActivity.this.getWindowManager().getDefaultDisplay().getMetrics(dm);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // only for LOLLIPOP and newer versions
            accept.setBackground(ContextCompat.getDrawable(MyTeamFormationActivity.this, R.drawable.button_bg));
        } else {
            accept.setBackground(ContextCompat.getDrawable(MyTeamFormationActivity.this, R.drawable.button_bg_normal));
        }
        mLayoutManager = new LinearLayoutManager(MyTeamFormationActivity.this);
        mlistView.setLayoutManager(mLayoutManager);

        mlistView.hideProgress();
        mlistView.hideMoreProgress();

        if (CommonUtils.isNetworkAvailable(MyTeamFormationActivity.this)) {
            progress.show();
            GetTeamPlayer();
        } else {
            Toast.makeText(MyTeamFormationActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }

        accept.setTypeface(typeface_regular);
        accept.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.accept:
                getSelectedPlayers();
                if (CommonUtils.isNetworkAvailable(MyTeamFormationActivity.this)) {
                    if (playerid.length() != 0) {
                        progress.show();
                        GetTeamFormation();
                    } else {
                        Toast.makeText(MyTeamFormationActivity.this, "Select players", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MyTeamFormationActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.activity_explore_player_list_iv_back:
                finish();
                overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
                break;
        }
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
        Call<ResponseBody> call = apiInterface.TeamPlayerlist(auth_token, myteamid);
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
                                if (array.getJSONObject(i).getString("player_photo") != null || !array.getJSONObject(i).getString("player_photo").equals("null")) {
                                    m.setPlayer_photo(array.getJSONObject(i).getString("player_photo"));
                                } else {
                                    m.setPlayer_photo(CommonUtils.default_image);
                                }
                                if (array.getJSONObject(i).getString("player_location") != null) {
                                    m.setPlayer_location(array.getJSONObject(i).getString("player_location"));
                                }
                                m.setIschecked(false);

                                arrayList.add(m);

                            }
                        }
                        MyTeamFormAdapter adapter = new MyTeamFormAdapter(MyTeamFormationActivity.this, arrayList);
                        mlistView.setAdapter(adapter);
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                String error = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(error);
                                String msg = jsonObject.getString("message");
                                Toast.makeText(MyTeamFormationActivity.this, msg, Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                String error = response.message();
                                Toast.makeText(MyTeamFormationActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String error = response.message();
                            Toast.makeText(MyTeamFormationActivity.this, error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MyTeamFormationActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MyTeamFormationActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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

    public void getMatchScheduledAccept() {
        String auth_token = "Bearer " + preferenceHelper.getString("user_token", "");
        ScheduleConfirmInput input = new ScheduleConfirmInput();
        input.setVenues_booking_id(venueBookingId);
        input.setVenue_status("1");
        Call<ResponseBody> call = apiInterface.ScheduleConfirm(auth_token, input);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        JSONObject json = new JSONObject(data);
                        String message = json.getString("message");
                        finish();
                        Toast.makeText(MyTeamFormationActivity.this, message, Toast.LENGTH_SHORT).show();
                    } else {
                        if (response.errorBody() != null) {
                            String error = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(error);
                            String msg = jsonObject.getString("message");
                            Toast.makeText(MyTeamFormationActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            String error = response.message();
                            Toast.makeText(MyTeamFormationActivity.this, error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MyTeamFormationActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MyTeamFormationActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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

    public void GetTeamFormation() {
        progress.show();
        String auth_token = "Bearer " + preferenceHelper.getString("user_token", "");
        TeamFormInputModel input = new TeamFormInputModel();
        input.setMatchid(match_id);
        input.setPlayerid(playerid);
        input.setOpponentid(myteamid);
        Call<ResponseBody> call = apiInterface.FormTeam(auth_token, input);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                        JSONObject json = new JSONObject(data);
                        Toast.makeText(MyTeamFormationActivity.this, "Team formed successfully.", Toast.LENGTH_SHORT).show();
                        if (CommonUtils.isNetworkAvailable(MyTeamFormationActivity.this)) {
                            getMatchScheduledAccept();
                        } else {
                            Toast.makeText(MyTeamFormationActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        if (response.errorBody() != null) {
                            try {
                                String error = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(error);
                                String msg = jsonObject.getString("message");
                                if (msg.equals("your team already team formed for  this match")) {
                                    if (CommonUtils.isNetworkAvailable(MyTeamFormationActivity.this)) {
                                        getMatchScheduledAccept();
                                    } else {
                                        Toast.makeText(MyTeamFormationActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                                    }
                                }
                                Toast.makeText(MyTeamFormationActivity.this, msg, Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                String error = response.message();
                                Toast.makeText(MyTeamFormationActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String error = response.message();
                            Toast.makeText(MyTeamFormationActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                    finish();
                    if (progress != null && progress.isShowing()) {
                        progress.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //  Log error here since request failed
                try {
                    if (progress != null && progress.isShowing()) {
                        progress.dismiss();
                    }
                    if (t instanceof SocketTimeoutException) {
                        Toast.makeText(MyTeamFormationActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MyTeamFormationActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
    }
}
