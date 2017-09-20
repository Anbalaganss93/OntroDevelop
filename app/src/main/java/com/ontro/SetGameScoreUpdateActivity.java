package com.ontro;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ontro.dto.ScoreUpdate;
import com.ontro.fragments.MatchFlagFragment;
import com.ontro.rest.ApiClient;
import com.ontro.rest.ApiInterface;
import com.ontro.utils.CommonUtils;
import com.ontro.utils.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetGameScoreUpdateActivity extends AppCompatActivity implements View.OnClickListener {

    private LayoutInflater mLayoutInflater;
    private LinearLayout mScoreContainer, mAddContainer;
    private ImageView mAddScore, back, mMatchFlagImageView;
    private int setcount = 0;
    private View scoreview;
    private Button submit;
    private ImageView team_one_winner_indicator;
    private ImageView team_two_winner_indicator;
    private View v;
    private ApiInterface apiInterface;
    private PreferenceHelper preferenceHelper;
    private Call<ResponseBody> call;
    private String auth_token;
    private int score_update_length = 1;
    private Typeface regular_typeface;
    private Typeface bold_typeface;
    private TextView teamone;
    private TextView teamtwo;
    private TextView score_update_status;
    private TextView mDate;
    private TextView mLocation;
    private ImageView mSportImage;
    private ImageView team_one_userProfile;
    private ImageView team_two_userProfile;
    private String totalsetcount = "0";
    private int DR_click = 0;
    private ImageView reseticon,deleteicon,reset,delete;
    private Dialog progress;
    private String mTeamCommonId,mSportId, mOpponentTeamId, mMyTeamId, mMatchId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_game_score_update);
        overridePendingTransition(R.anim.slide_in_right, R.anim.pause);
        DisplayMetrics merics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(merics);

        progress = new Dialog(SetGameScoreUpdateActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progress.setContentView(R.layout.progressdialog_layout);

        mScoreContainer = (LinearLayout) findViewById(R.id.game_score_update_ll_container);
        mAddContainer = (LinearLayout) findViewById(R.id.game_score_add_container);
        mAddScore = (ImageView) findViewById(R.id.add_score_view);
        reset = (ImageView) findViewById(R.id.reset);
        delete = (ImageView) findViewById(R.id.delete);
        team_one_userProfile = (ImageView) findViewById(R.id.game_header_team);
        team_two_userProfile = (ImageView) findViewById(R.id.game_header_team2);
        mSportImage = (ImageView) findViewById(R.id.game_header_sportimage);
        mDate = (TextView) findViewById(R.id.game_header_date);
        mLocation = (TextView) findViewById(R.id.game_header_location);
        teamone = (TextView) findViewById(R.id.game_header_team_one_name);
        teamtwo = (TextView) findViewById(R.id.game_header_team_two_name);
        score_update_status = (TextView) findViewById(R.id.game_header_team_score_update_status);
        submit = (Button) findViewById(R.id.activity_explore_player_list_btn_done);
        team_one_winner_indicator = (ImageView) findViewById(R.id.team_one_winner_indicator);
        team_two_winner_indicator = (ImageView) findViewById(R.id.team_two_winner_indicator);
        mMatchFlagImageView = (ImageView) findViewById(R.id.flag_match);
        team_two_winner_indicator.setVisibility(View.GONE);
        team_one_winner_indicator.setVisibility(View.GONE);

        Bundle bundle = getIntent().getExtras();
        mDate.setText(bundle.getString(Constants.BundleKeys.MATCH_DATE));
        mLocation.setText(bundle.getString(Constants.BundleKeys.MATCH_VENUE));
        teamone.setText(bundle.getString(Constants.BundleKeys.MY_TEAM_NAME));
        teamtwo.setText(bundle.getString(Constants.BundleKeys.OPPONENT_TEAM_NAME));
        score_update_status.setText(bundle.getString(Constants.BundleKeys.SCORE_UPDATE_STATUS));
        mSportId = bundle.getString(Constants.BundleKeys.SPORT_ID);
        mSportImage.setImageResource(CommonUtils.scoreUpdateSport(mSportId));
        mOpponentTeamId = bundle.getString(Constants.BundleKeys.OPPONENT_TEAM_ID);
        mMyTeamId = bundle.getString(Constants.BundleKeys.TEAM_ID);
        mMatchId = bundle.getString(Constants.BundleKeys.MATCH_ID);

        String opponentTeamImage = bundle.getString(Constants.BundleKeys.OPPONENT_TEAM_IMAGE);
        if (opponentTeamImage != null) {
            Glide.with(SetGameScoreUpdateActivity.this).load(opponentTeamImage).placeholder(R.drawable.match_default).dontAnimate().into(team_two_userProfile);
        }
        String myTeamImage = bundle.getString(Constants.BundleKeys.MY_TEAM_IMAGE);
        if (myTeamImage != null) {
            Glide.with(SetGameScoreUpdateActivity.this).load(myTeamImage).placeholder(R.drawable.match_default).dontAnimate().into(team_one_userProfile);
        }

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        regular_typeface = Typeface.createFromAsset(getAssets(), "fonts/roboto_regular.ttf");
        bold_typeface = Typeface.createFromAsset(getAssets(), "fonts/roboto_bold.ttf");
        preferenceHelper = new PreferenceHelper(SetGameScoreUpdateActivity.this, Constants.APP_NAME, 0);


        auth_token = "Bearer " + preferenceHelper.getString("user_token", "");

        TeamScoreUpdateStatus(bundle.getString(Constants.BundleKeys.TEAM_UPDATE_STATUS));
        RelativeLayout.LayoutParams sport_image_param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        sport_image_param.width = (int) (merics.widthPixels * 0.12);
        sport_image_param.height = (int) (merics.widthPixels * 0.12);
        sport_image_param.addRule(RelativeLayout.ALIGN_PARENT_RIGHT | RelativeLayout.CENTER_VERTICAL);
//        mSportImage.setLayoutParams(sport_image_param);
        submit.setTypeface(bold_typeface);
        back = (ImageView) findViewById(R.id.activity_explore_player_list_iv_back);
        submitAccess();
        setListener();
    }

    private void TeamScoreUpdateStatus(String teams_score_update) {
        if (teams_score_update != null) {
            if (teams_score_update.length() != 0) {
                JSONObject scorejson = null;
                try {
                    scorejson = new JSONObject(teams_score_update);
                    String opponentTeamUpdateStatus = scorejson.getString("opponent_user_score_status");
                    String myTeamUpdateStatus = scorejson.getString("login_user_score_status");
                    totalsetcount = scorejson.getString("total_set");
                    Log.d("TotalCount", String.valueOf(totalsetcount));
                    if (!totalsetcount.equals("0") && !totalsetcount.equals("null") && !totalsetcount.isEmpty()) {
                        score_update_length = Integer.parseInt(totalsetcount);
                    } else {
                        score_update_length = 1;
                    }
                    team_two_winner_indicator.setVisibility(View.GONE);
                    team_one_winner_indicator.setVisibility(View.GONE);
                    mScoreContainer.setVisibility(View.VISIBLE);
                    submit.setVisibility(View.VISIBLE);
                    switch (opponentTeamUpdateStatus) {
                        case "0":
                            team_two_winner_indicator.setVisibility(View.GONE);
                            break;
                        case "1":
                            mTeamCommonId = mOpponentTeamId;
                        case "2":
//                            getScoreView();
                            team_two_winner_indicator.setVisibility(View.VISIBLE);
                            break;
                        case "3":
                            team_two_winner_indicator.setVisibility(View.GONE);
                            break;
                    }

                    switch (myTeamUpdateStatus) {
                        case "0":
                            scoreViewAdd();
                            team_one_winner_indicator.setVisibility(View.GONE);
                            break;
                        case "1":
                        case "2":
                            mTeamCommonId = mMyTeamId;
                            team_one_winner_indicator.setVisibility(View.VISIBLE);
                            mScoreContainer.setVisibility(View.VISIBLE);
                            getScoreView();
                            mAddContainer.setVisibility(View.INVISIBLE);
                            submit.setVisibility(View.GONE);
                            reset.setVisibility(View.INVISIBLE);
                            delete.setVisibility(View.INVISIBLE);
                            break;
                        case "3":
                            scoreViewAdd();
                            team_one_winner_indicator.setVisibility(View.GONE);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            mScoreContainer.setVisibility(View.GONE);
            mAddContainer.setVisibility(View.INVISIBLE);
            submit.setVisibility(View.GONE);
            reset.setVisibility(View.INVISIBLE);
            delete.setVisibility(View.INVISIBLE);
        }
    }

    private void setListener() {
        mAddScore.setOnClickListener(this);
        back.setOnClickListener(this);
        submit.setOnClickListener(this);
        mMatchFlagImageView.setOnClickListener(this);
    }

    public void scoreViewAdd() {
        for (int i = 0; i < score_update_length; i++) {
            setcount = setcount + 1;
            mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            scoreview = mLayoutInflater.inflate(R.layout.game_score_update_view_layout, null, false);
            TextView setNumber = (TextView) scoreview.findViewById(R.id.game_score_set);
            setNumber.setTextColor(ContextCompat.getColor(SetGameScoreUpdateActivity.this, R.color.white));
            final EditText teamone_et = (EditText) scoreview.findViewById(R.id.game_score_update_teamone);
            final EditText teamtwo_et = (EditText) scoreview.findViewById(R.id.game_score_update_teamtwo);
            reseticon = (ImageView) scoreview.findViewById(R.id.reseticon);
            deleteicon = (ImageView) scoreview.findViewById(R.id.deleteicon);
            View bottomline = (View) scoreview.findViewById(R.id.bottomline);
            View line = (View) scoreview.findViewById(R.id.line);
            teamone_et.setSelection(TextUtils.getTrimmedLength(teamone_et.getText()));
            teamone_et.setSelectAllOnFocus(true);

            // Sport carrom
            if (mSportId.equalsIgnoreCase("3")){
                setNumber.setText("GAME " + setcount);
            }else{
                setNumber.setText("SET " + setcount);
            }

            setNumber.setTag(setcount - 1);
            teamone_et.setTag(setcount - 1);
            teamtwo_et.setTag(setcount - 1);
            reseticon.setTag(setcount - 1);
            deleteicon.setTag(setcount - 1);
            mAddScore.setTag(setcount - 1);
            reset.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            reset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reset.setVisibility(View.GONE);
                    delete.setVisibility(View.GONE);
                    int currentview_count = setcount - 1;
                    mScoreContainer.removeViewAt(currentview_count);
                    setcount = currentview_count;
                    scoreViewAdd();
                    submitAccess();
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reset.setVisibility(View.GONE);
                    delete.setVisibility(View.GONE);
                    mScoreContainer.removeViewAt(setcount - 1);
                    setcount = setcount - 1;
                    DR_click = 0;
                    if (setcount == 11) {
                        mAddContainer.setVisibility(View.INVISIBLE);
                    } else if (setcount < 11) {
                        mAddContainer.setVisibility(View.VISIBLE);
                    }
                    reseticon.setTag(setcount - 1);
                    deleteicon.setTag(setcount - 1);
                    submitAccess();
                }
            });

           /* deleteicon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ic_reset_grey.setVisibility(View.GONE);
                    delete.setVisibility(View.GONE);
                    mScoreContainer.removeViewAt(setcount - 1);
                    setcount = setcount - 1;
                    DR_click = 0;
                    if (setcount == 1) {
                        mAddContainer.setVisibility(View.VISIBLE);
                    } else if (setcount < 11) {
                        mAddContainer.setVisibility(View.VISIBLE);
                    }
                    reseticon.setTag(setcount - 1);
                    deleteicon.setTag(setcount - 1);
                    submitAccess();
                }
            });*/

            setNumber.setFocusable(true);
            submit.setEnabled(false);
            reseticon.setOnClickListener(this);
//          deleteicon.setOnClickListener(this);
            final int finalI = mScoreContainer.getChildCount() + 1;
            setNumber.setLongClickable(true);

            setNumber.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View view) {
                    Integer io = (Integer) view.getTag();
                    Log.d("IO", String.valueOf(io));

                    mScoreContainer.invalidate();
                    if (setcount != 1 && setcount == finalI) {
                        if (DR_click == 1) {
                            DR_click = 0;
                           /* reseticon.findViewWithTag(view.getTag()).setVisibility(View.INVISIBLE);
                            deleteicon.findViewWithTag(view.getTag()).setVisibility(View.INVISIBLE);*/
                            reset.setVisibility(View.GONE);
                            delete.setVisibility(View.GONE);
                        } else if (DR_click == 0) {
                            DR_click = 1;
                            /*reseticon.findViewWithTag(view.getTag()).setVisibility(View.VISIBLE);
                            deleteicon.findViewWithTag(view.getTag()).setVisibility(View.VISIBLE);*/
                            reset.setVisibility(View.VISIBLE);
                            delete.setVisibility(View.VISIBLE);
                        }
                    }
                    return true;
                }
            });

            teamone_et.setTypeface(regular_typeface);
            teamtwo_et.setTypeface(regular_typeface);

            mScoreContainer.addView(scoreview, setcount - 1, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            submitAccess();
            if (!totalsetcount.equals("0")) {
                mAddContainer.setVisibility(View.INVISIBLE);
                if (totalsetcount.equals(String.valueOf(setcount))) {
                    bottomline.setVisibility(View.INVISIBLE);
                }
            } else {
                if (setcount == 11) {
                    mAddContainer.setVisibility(View.INVISIBLE);
                    bottomline.setVisibility(View.INVISIBLE);
                } else {
                    mAddContainer.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void scoreViewDetail(JSONArray team1, JSONArray team2) {
        for (int i = 0; i < score_update_length; i++) {
            setcount = setcount + 1;
            mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            scoreview = mLayoutInflater.inflate(R.layout.game_score_update_view_layout, null, false);
            TextView setNumber = (TextView) scoreview.findViewById(R.id.game_score_set);
            setNumber.setTextColor(ContextCompat.getColor(SetGameScoreUpdateActivity.this, R.color.white));
            final EditText teamone_et = (EditText) scoreview.findViewById(R.id.game_score_update_teamone);
            final EditText teamtwo_et = (EditText) scoreview.findViewById(R.id.game_score_update_teamtwo);
            try {
                String score = team1.getJSONObject(i).getString("score");
                teamone_et.setText(score);
                String score2 = team2.getJSONObject(i).getString("score");
                teamtwo_et.setText(score2);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            teamone_et.setEnabled(false);
            teamtwo_et.setEnabled(false);
            teamtwo_et.setFocusable(false);
            teamtwo_et.setFocusable(false);
            teamtwo_et.setFocusableInTouchMode(false);
            teamtwo_et.setFocusableInTouchMode(false);

            reseticon = (ImageView) scoreview.findViewById(R.id.reseticon);
            deleteicon = (ImageView) scoreview.findViewById(R.id.deleteicon);
            View bottomline = (View) scoreview.findViewById(R.id.bottomline);
            View line = (View) scoreview.findViewById(R.id.line);
            teamone_et.setSelection(TextUtils.getTrimmedLength(teamone_et.getText()));
            teamone_et.setSelectAllOnFocus(true);
            // Sport carrom
            if (mSportId.equalsIgnoreCase("3")){
                setNumber.setText("GAME " + setcount);
            }else{
                setNumber.setText("SET " + setcount);
            }
            setNumber.setTag(setcount - 1);
            teamone_et.setTag(setcount - 1);
            teamtwo_et.setTag(setcount - 1);
            reseticon.setTag(setcount - 1);
            deleteicon.setTag(setcount - 1);
            mAddScore.setTag(setcount - 1);
            reset.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);

            teamone_et.setTypeface(regular_typeface);
            teamtwo_et.setTypeface(regular_typeface);

            mScoreContainer.addView(scoreview, setcount - 1, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            submitAccess();
            if (!totalsetcount.equals("0")) {
                mAddContainer.setVisibility(View.INVISIBLE);
                if (totalsetcount.equals(String.valueOf(setcount))) {
                    bottomline.setVisibility(View.INVISIBLE);
                }
            } else {
                if (setcount == 11) {
                    mAddContainer.setVisibility(View.INVISIBLE);
                    bottomline.setVisibility(View.INVISIBLE);
                } else {
                    mAddContainer.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void deleteadd() {
        for (int i = 0; i < score_update_length; i++) {
            setcount = setcount + 1;
            mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            scoreview = mLayoutInflater.inflate(R.layout.game_score_update_view_layout, null, false);
            TextView setNumber = (TextView) scoreview.findViewById(R.id.game_score_set);
            setNumber.setTextColor(ContextCompat.getColor(SetGameScoreUpdateActivity.this, R.color.white));
            final EditText teamone_et = (EditText) scoreview.findViewById(R.id.game_score_update_teamone);
            final EditText teamtwo_et = (EditText) scoreview.findViewById(R.id.game_score_update_teamtwo);
            reseticon = (ImageView) scoreview.findViewById(R.id.reseticon);
            deleteicon = (ImageView) scoreview.findViewById(R.id.deleteicon);
            View bottomline = (View) scoreview.findViewById(R.id.bottomline);
            View line = (View) scoreview.findViewById(R.id.line);
            teamone_et.setSelection(TextUtils.getTrimmedLength(teamone_et.getText()));
            teamone_et.setSelectAllOnFocus(true);
            // Sport carrom
            if (mSportId.equalsIgnoreCase("3")){
                setNumber.setText("GAME " + setcount);
            }else{
                setNumber.setText("SET " + setcount);
            }
            setNumber.setTag(setcount - 1);
            teamone_et.setTag(setcount - 1);
            teamtwo_et.setTag(setcount - 1);
            reseticon.setTag(setcount - 1);
            deleteicon.setTag(setcount - 1);
            mAddScore.setTag(setcount - 1);
            mScoreContainer.addView(scoreview, setcount - 1, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_score_view:
                reset.setVisibility(View.GONE);
                reset.setVisibility(View.GONE);
                try {
                    reseticon.findViewWithTag(view.getTag()).setVisibility(View.INVISIBLE);
                    deleteicon.findViewWithTag(view.getTag()).setVisibility(View.INVISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                DR_click = 0;
                scoreViewAdd();
                break;
            case R.id.activity_explore_player_list_iv_back:
                finish();
                overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
                break;
            case R.id.activity_explore_player_list_btn_done:
                final int childcount = mScoreContainer.getChildCount();
                JSONArray array = new JSONArray();
                JSONObject arrayjson = new JSONObject();

                for (int i = 0; i < childcount; i++) {
                    v = mScoreContainer.getChildAt(i);
                    EditText teamone_et = (EditText) v.findViewById(R.id.game_score_update_teamone);
                    String score1 = teamone_et.getText().toString().trim().length() != 0 ? teamone_et.getText().toString().trim() : "0";
                    JSONObject object = new JSONObject();
                    try {
                        object.put("set_no", i + 1);
                        object.put("score", score1);
                        object.put("team_id", mMyTeamId);
                        array.put(object);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                JSONArray array2 = new JSONArray();
                for (int i = 0; i < childcount; i++) {
                    v = mScoreContainer.getChildAt(i);
                    EditText teamtwo_et = (EditText) v.findViewById(R.id.game_score_update_teamtwo);
                    String score2 = teamtwo_et.getText().toString().trim().length() != 0 ? teamtwo_et.getText().toString().trim() : "0";
                    JSONObject object = new JSONObject();
                    try {
                        object.put("set_no", i + 1);
                        object.put("score", score2);
                        object.put("team_id", mOpponentTeamId);
                        array2.put(object);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                ScoreUpdate input = new ScoreUpdate();
                input.setMatchId(mMatchId);
                input.setTeamid(mMyTeamId);
                input.setSporttype(preferenceHelper.getString("teamsport", ""));
                input.setTeam_one_score(String.valueOf(array));
                input.setTeam_two_score(String.valueOf(array2));
                progress.show();
                scoreupdate(input);
                Log.d("ARRAY", String.valueOf(arrayjson));
                break;
            /*case R.id.deleteicon:
                mScoreContainer.removeViewAt(setcount - 1);
                setcount = setcount - 1;
                if (setcount == 1) {
                    mAddContainer.setVisibility(View.VISIBLE);
                } else if (setcount < 11) {
                    mAddContainer.setVisibility(View.VISIBLE);
                }
                reseticon.setTag(setcount);
                deleteicon.setTag(setcount);
                submitAccess();
                break;*/
            case R.id.reseticon:
                int currentview_count = setcount - 1;
                mScoreContainer.removeViewAt(currentview_count);
                setcount = currentview_count;
                scoreViewAdd();
                submitAccess();
                break;
            case R.id.flag_match:
                MatchFlagFragment matchFlagFragment = MatchFlagFragment.newInstance(mMatchId);
                matchFlagFragment.show(getFragmentManager(), Constants.Messages.FLAG_MATCH);
                break;
        }
    }

    private void submitAccess() {
        if (setcount > 1) {
            submit.setBackgroundResource(R.drawable.score_update_btn_background);
            submit.setEnabled(true);
        } else {
            submit.setBackgroundResource(R.drawable.score_update_btn_diable_background);
            submit.setEnabled(false);
        }
    }

    public void scoreupdate(ScoreUpdate input) {
        String auth_token = "Bearer " + preferenceHelper.getString("user_token", "");
        int sportId = CommonUtils.sportIdCheck(preferenceHelper.getString("teamsport", ""));
        Call<ResponseBody> call = null;
        call = apiInterface.ScoreUpdate(auth_token, input);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        JSONObject json = new JSONObject(data);
                        Toast.makeText(SetGameScoreUpdateActivity.this, "Score updated", Toast.LENGTH_SHORT).show();
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    } else {
                        CommonUtils.ErrorHandleMethod(SetGameScoreUpdateActivity.this, response);
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
                CommonUtils.ServerFailureHandleMethod(SetGameScoreUpdateActivity.this, t);
                if (progress != null && progress.isShowing()) {
                    progress.dismiss();
                }
            }
        });
    }

    public void getScoreView() {
        progress.show();
        String sportid = preferenceHelper.getString("teamsport", "");
        String auth_token = "Bearer " + preferenceHelper.getString("user_token", "");
        Call<ResponseBody> call = apiInterface.OpponentScoreView(auth_token, mMatchId, mTeamCommonId, sportid);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String json = response.body().string();
                        JSONObject datajson = new JSONObject(json);
                        JSONObject object =new JSONObject( datajson.getString("data"));
                        JSONArray team1 = new JSONArray(object.getString("team_one"));
                        /*for (int i = 0; i < team1.length(); i++) {
                            String setno = team1.getJSONObject(i).getString("set_no");
                            String score = team1.getJSONObject(i).getString("score");
                        }*/
                        JSONArray team2 = new JSONArray(object.getString("team_two"));
                       /* for (int i = 0; i < team2.length(); i++) {
                            String team2_setno = team1.getJSONObject(i).getString("set_no");
                            String team2_score = team1.getJSONObject(i).getString("score");
                        }*/
                        if (mTeamCommonId.equals(mOpponentTeamId)) {
                            scoreViewDetail(team2, team1);
                        }else{
                            scoreViewDetail(team1, team2);
                        }

                    } else {
                        CommonUtils.ErrorHandleMethod(SetGameScoreUpdateActivity.this, response);
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
                CommonUtils.ServerFailureHandleMethod(SetGameScoreUpdateActivity.this, t);
                if (progress != null && progress.isShowing()) {
                    progress.dismiss();
                }
            }
        });
    }
}
