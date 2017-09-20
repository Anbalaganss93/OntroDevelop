package com.ontro;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ontro.adapters.BasketballScoreAdapter;
import com.ontro.customui.ProfileImageView;
import com.ontro.dto.BasketballScoreDTO;
import com.ontro.dto.BasketballScoreUpdateDTO;
import com.ontro.dto.MySquadInfo;
import com.ontro.fragments.MatchFlagFragment;
import com.ontro.rest.ApiClient;
import com.ontro.rest.ApiInterface;
import com.ontro.utils.CommonUtils;
import com.ontro.utils.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScoreUpdateBasketballActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout quarterLayout, totalscorelayout, detaillayout;
    private ImageButton add, reset, remove;
    private LayoutInflater mLayoutInflater;
    private View scoreview;
    private int setcount = 0;
    private Button submit;
    private TextView tv_set_quarter_dropdown, tv_detail_team_one, tv_detail_team_two;
    private RecyclerView playerRecyclerView;
    private BasketballScoreAdapter adapter;
    private View view;
    private TextView setNumber;
    private EditText et_team_one, et_team_two;
    private NestedScrollView nestedScrollView;
    private TextView mMatchDateView, mMatchVenueView, mMatchStatusView;
    private TextView mFirstTeamNameView, mSecondTeamNameView, mFirstTeamTotalScoreView, mSecondTeamTotalScoreView;
    private ImageView mSportImageView, mFirstTeamUpdateIndicationView, mSecondTeamUpdateIndicationView, mFirstTeamWonIndicationView,
            mSecondTeamWonIndicationView, mMatchFlagImageView;
    private String mMyTeamId, mOpponentTeamId, mMatchId, mTeamUpdateStatus;
    private ProfileImageView mFirstTeamImageView, mSecondTeamImageView;
    ArrayList<BasketballScoreUpdateDTO> mBasketballScoreUpdateDTOs = new ArrayList<>();
    private String matchId, opponentteamid, myteamid;
    private PreferenceHelper preferenceHelper;
    private ApiInterface apiInterface;
    private String totalsetcount;
    private int totalcount = 0, selectedPosition = 0;
    private HashMap<Integer, ArrayList<BasketballScoreUpdateDTO>> playerlist;
    private ArrayList<MySquadInfo> mySquadPlayers = new ArrayList<>();
    private String myTeamUpdateStatus;
    private Dialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_update_basketball);
        overridePendingTransition(R.anim.slide_in_right, R.anim.pause);
        Init();
        GetMatchDetails();
    }

    private void GetMatchDetails() {
        matchId = preferenceHelper.getString("match_id", "");
        opponentteamid = preferenceHelper.getString("teamid", "");
        myteamid = preferenceHelper.getString("myteamid", "");

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            mMatchDateView.setText(bundle.getString(Constants.BundleKeys.MATCH_DATE));
            mMatchVenueView.setText(bundle.getString(Constants.BundleKeys.MATCH_VENUE));
            mFirstTeamNameView.setText(bundle.getString(Constants.BundleKeys.MY_TEAM_NAME));
            mSecondTeamNameView.setText(bundle.getString(Constants.BundleKeys.OPPONENT_TEAM_NAME));
            mMatchStatusView.setText(bundle.getString(Constants.BundleKeys.SCORE_UPDATE_STATUS));
            mMyTeamId = bundle.getString(Constants.BundleKeys.OPPONENT_TEAM_ID);
            mOpponentTeamId = bundle.getString(Constants.BundleKeys.TEAM_ID);
            mMatchId = bundle.getString(Constants.BundleKeys.MATCH_ID);
            mTeamUpdateStatus = bundle.getString(Constants.BundleKeys.TEAM_UPDATE_STATUS);
            String firstTeamImage = bundle.getString(Constants.BundleKeys.OPPONENT_TEAM_IMAGE);

            mySquadPlayers = (ArrayList<MySquadInfo>) bundle.getSerializable(Constants.BundleKeys.MY_TEAM_SQUAD);
            Addsquad(mySquadPlayers);

            TeamScoreUpdateStatus(mTeamUpdateStatus);

            if (firstTeamImage != null) {
                Glide.with(ScoreUpdateBasketballActivity.this).load(firstTeamImage).placeholder(R.drawable.match_default).dontAnimate().into(mSecondTeamImageView);
            }
            String secondTeamImage = bundle.getString(Constants.BundleKeys.MY_TEAM_IMAGE);
            if (secondTeamImage != null) {
                Glide.with(ScoreUpdateBasketballActivity.this).load(secondTeamImage).placeholder(R.drawable.match_default).dontAnimate().into(mFirstTeamImageView);
            }
        }
    }

    private void Addsquad(ArrayList<MySquadInfo> mySquadPlayers) {
        for (MySquadInfo squadInfo : mySquadPlayers) {
            BasketballScoreUpdateDTO update = new BasketballScoreUpdateDTO(squadInfo.getPlayerId(), squadInfo.getPlayerName(), "0", "0", "0", "1");
            mBasketballScoreUpdateDTOs.add(update);
        }
    }

    private void TeamScoreUpdateStatus(String teams_score_update) {
        if (teams_score_update != null) {
            if (teams_score_update.length() != 0) {
                JSONObject scorejson = null;
                try {
                    scorejson = new JSONObject(teams_score_update);
                    String opponentTeamUpdateStatus = scorejson.getString("opponent_user_score_status");
                    myTeamUpdateStatus = scorejson.getString("login_user_score_status");
                    totalsetcount = scorejson.getString("total_set");

                    if (!totalsetcount.equals("0") && !totalsetcount.equals("null") && !totalsetcount.isEmpty()) {
                        totalcount = Integer.parseInt(totalsetcount);
                    } else {
                        totalcount = 1;
                    }
                    mFirstTeamUpdateIndicationView.setVisibility(View.GONE);
                    mSecondTeamUpdateIndicationView.setVisibility(View.GONE);
                    quarterLayout.setVisibility(View.VISIBLE);
                    submit.setVisibility(View.VISIBLE);

                    switch (opponentTeamUpdateStatus) {
                        case "0":
                            mSecondTeamUpdateIndicationView.setVisibility(View.GONE);
                            break;
                        case "1":
                        case "2":
                            mSecondTeamUpdateIndicationView.setVisibility(View.VISIBLE);
                            break;
                    }

                    switch (myTeamUpdateStatus) {
                        case "0":
                            scoreViewAdd();
                            mFirstTeamUpdateIndicationView.setVisibility(View.GONE);
                            break;
                        case "1":
                        case "2":
                            scoreViewAdd();
                            GetScores();
                            mFirstTeamUpdateIndicationView.setVisibility(View.VISIBLE);
                            submit.setVisibility(View.GONE);
                            reset.setVisibility(View.INVISIBLE);
                            remove.setVisibility(View.INVISIBLE);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void GetScores() {
        String sportid = preferenceHelper.getString("teamsport", "");
        String auth_token = "Bearer " + preferenceHelper.getString("user_token", "");

        Call<ResponseBody> call = apiInterface.OpponentScoreView(auth_token, matchId, myteamid, sportid);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        JSONObject jsonObject = new JSONObject(data);
                        JSONObject dataJsonObject1 = jsonObject.getJSONObject("data");
                        JSONArray team_oneJsonArray = dataJsonObject1.getJSONArray("team_two");
                        JSONArray team_twoJsonArray = dataJsonObject1.getJSONArray("team_one");
                        JSONArray playerJsonArray = dataJsonObject1.getJSONArray("player_score");
                        ShowTeamScore(team_oneJsonArray, team_twoJsonArray, playerJsonArray);
                    } else {
                        CommonUtils.ErrorHandleMethod(ScoreUpdateBasketballActivity.this, response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //  Log error here since request failed
                CommonUtils.ServerFailureHandleMethod(ScoreUpdateBasketballActivity.this, t);
            }
        });
    }

    private void ShowTeamScore(JSONArray team_oneJsonArray, JSONArray team_twoJsonArray, JSONArray playerJsonArray) {
        nestedScrollView.setPadding(0, 0, 0, 0);
        final int childcount = quarterLayout.getChildCount();
        try {
            if (childcount > 0) {
                for (int i = 0; i < childcount; i++) {
                    view = quarterLayout.getChildAt(i);
                    EditText et_quarter_two = (EditText) view.findViewById(R.id.tv_q2);
                    et_quarter_two.setText(team_oneJsonArray.getJSONObject(i).getString("score"));
                    et_quarter_two.setEnabled(false);
                }

                for (int i = 0; i < childcount; i++) {
                    view = quarterLayout.getChildAt(i);
                    EditText et_quarter_one = (EditText) view.findViewById(R.id.tv_q1);
                    et_quarter_one.setText(team_twoJsonArray.getJSONObject(i).getString("score"));
                    et_quarter_one.setEnabled(false);
                }

                if (playerJsonArray.length() > 0) {
                    ArrayList<BasketballScoreUpdateDTO> list;
                    playerlist = new HashMap<>();
                    for (int j = 0; j < playerJsonArray.length(); j++) {
                        list = new ArrayList<>();
                        JSONArray array = playerJsonArray.getJSONObject(j).getJSONArray("players");
                        for (int k=0;k<array.length();k++){
                            String id = array.getJSONObject(k).getString("player_id");
                            String name = array.getJSONObject(k).getString("player_name");
                            String first_point = array.getJSONObject(k).getString("one_point");
                            String second_point = array.getJSONObject(k).getString("two_point");
                            String third_point = array.getJSONObject(k).getString("third_point");
                            BasketballScoreUpdateDTO updateDTO = new BasketballScoreUpdateDTO(id,name, first_point, second_point, third_point, "1");
                            list.add(updateDTO);
                        }
                        playerlist.put(j, list);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                scoreViewAdd();
                break;

            case R.id.tv_update_quarter_dropdown:
                // Score updated by team one
                if (myTeamUpdateStatus.equalsIgnoreCase("1")) {
                    totalscorelayout.setVisibility(View.VISIBLE);
                    detaillayout.setVisibility(View.GONE);
                } else {
                    totalscorelayout.setVisibility(View.VISIBLE);
                    submit.setVisibility(View.VISIBLE);
                    detaillayout.setVisibility(View.GONE);
                    nestedScrollView.setPadding(0, 0, 0, 120);
                }

                if (!myTeamUpdateStatus.equalsIgnoreCase("1")) {
                    ArrayList<BasketballScoreUpdateDTO> scoreUpdateDTOArrayList = adapter.Updatedlist();
                    playerlist.put(selectedPosition, scoreUpdateDTOArrayList);
                    UpdateTotalScore(scoreUpdateDTOArrayList);
                }
                break;

            case R.id.btn_submit:
                UpdateScore();
                break;
            case R.id.score_update_iv_flag_match:
                MatchFlagFragment matchFlagFragment = MatchFlagFragment.newInstance(mMatchId);
                matchFlagFragment.show(getFragmentManager(), Constants.Messages.FLAG_MATCH);
                break;
        }
    }

    private void UpdateTotalScore(ArrayList<BasketballScoreUpdateDTO> scoreUpdateDTOArrayList) {
        int sumOne = 0, sumTwo = 0, sumThree = 0;
        for (int k = 0; k < scoreUpdateDTOArrayList.size(); k++) {
            sumOne += (Integer.parseInt(scoreUpdateDTOArrayList.get(k).getPoint_one()));
            sumTwo += 2 * (Integer.parseInt(scoreUpdateDTOArrayList.get(k).getPoint_two()));
            sumThree += 3 * (Integer.parseInt(scoreUpdateDTOArrayList.get(k).getPoint_three()));
        }

        String total = String.valueOf(sumOne + sumTwo + sumThree);
        View scoreview = quarterLayout.getChildAt(selectedPosition);
        EditText et_quarter_one = (EditText) scoreview.findViewById(R.id.tv_q1);
        et_quarter_one.setText(total);
        et_quarter_one.setEnabled(false);
        // et_quarter_two.setEnabled(false);
    }

    private void UpdateScore() {
        final int childcount = quarterLayout.getChildCount();
        if (childcount > 0) {
            JSONArray array = new JSONArray();
            for (int i = 0; i < childcount; i++) {
                view = quarterLayout.getChildAt(i);
                EditText et_quarter_one = (EditText) view.findViewById(R.id.tv_q1);
                String score1 = et_quarter_one.getText().toString().trim().length() != 0 ? et_quarter_one.getText().toString().trim() : "0";
                JSONObject object = new JSONObject();
                try {
                    object.put("set_no", i + 1);
                    object.put("score", score1);
                    object.put("team_id", myteamid);
                    array.put(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            JSONArray array2 = new JSONArray();
            for (int i = 0; i < childcount; i++) {
                view = quarterLayout.getChildAt(i);
                EditText et_quarter_two = (EditText) view.findViewById(R.id.tv_q2);
                String score2 = et_quarter_two.getText().toString().trim().length() != 0 ? et_quarter_two.getText().toString().trim() : "0";
                JSONObject object = new JSONObject();
                try {
                    object.put("set_no", i + 1);
                    object.put("score", score2);
                    object.put("team_id", opponentteamid);
                    array2.put(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            JSONArray playerJsonArray = new JSONArray();
            if (playerlist.size() > 0) {
                for (int i = 0; i < childcount; i++) {
                    JSONObject object = new JSONObject();
                    JSONArray player = new JSONArray();
                    ArrayList<BasketballScoreUpdateDTO> squad = playerlist.get(i);
                    for (int j = 0; j < squad.size(); j++) {
                        JSONObject object1 = new JSONObject();
                        try {
                            object1.put("player_id", squad.get(j).getId());
                            object1.put("point_one", squad.get(j).getPoint_one());
                            object1.put("point_two", squad.get(j).getPoint_two());
                            object1.put("point_third", squad.get(j).getPoint_three());
                            if (squad.get(j).getIs_played().equalsIgnoreCase("1")) {
                                player.put(object1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        object.put("set_no", i + 1);
                        object.put("players", player);
                        playerJsonArray.put(object);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            BasketballScoreDTO basketballScoreDTO = new BasketballScoreDTO();
            basketballScoreDTO.setMatchId(matchId);
            basketballScoreDTO.setTeamid(myteamid);
            basketballScoreDTO.setPlayer_score(String.valueOf(playerJsonArray));
            basketballScoreDTO.setSportType(preferenceHelper.getString("teamsport", ""));
            basketballScoreDTO.setTeam_one_score(String.valueOf(array));
            basketballScoreDTO.setTeam_two_score(String.valueOf(array2));

            if (CommonUtils.isNetworkAvailable(this)) {
                mProgressDialog.show();
                scoreupdate(basketballScoreDTO);
            } else {
                Toast.makeText(ScoreUpdateBasketballActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
        }
        return super.onOptionsItemSelected(item);
    }

    public void scoreViewAdd() {
        if (totalsetcount.equalsIgnoreCase("0")) {
            for (int i = 0; i < 1; i++) {
                setcount = setcount + 1;
                mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                scoreview = mLayoutInflater.inflate(R.layout.row_basketball_score, null, false);
                setNumber = (TextView) scoreview.findViewById(R.id.tv_update_quarter_one);

                reset.setVisibility(View.GONE);
                remove.setVisibility(View.GONE);

                et_team_one = (EditText) scoreview.findViewById(R.id.tv_q1);
                et_team_two = (EditText) scoreview.findViewById(R.id.tv_q2);
                // et_team_one.setSelection(TextUtils.getTrimmedLength(et_team_one.getText()));
                // et_team_two.setSelectAllOnFocus(true);
                // et_team_one.setSelectAllOnFocus(true);

                setNumber.setText(setcount + "Q");
                setNumber.setTag(setcount - 1);
                et_team_one.setTag(setcount - 1);
                et_team_two.setTag(setcount - 1);
                reset.setTag(setcount - 1);
                remove.setTag(setcount - 1);
                quarterLayout.addView(scoreview, setcount - 1, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                EnableSetAdd(setcount);
                mBasketballScoreUpdateDTOs = new ArrayList<>();
                Addsquad(mySquadPlayers);
                playerlist.put(setcount - 1, mBasketballScoreUpdateDTOs);

                if (setcount > 1) {
                    submit.setEnabled(true);
                    submit.setClickable(true);
                } else {
                    submit.setEnabled(false);
                    submit.setClickable(false);
                }

                setNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Integer pos = (Integer) view.getTag();
                        int set = pos + 1;

                        ArrayList<BasketballScoreUpdateDTO> list = playerlist.get(pos);
                        selectedPosition = pos;
                        adapter = new BasketballScoreAdapter(ScoreUpdateBasketballActivity.this, list, myTeamUpdateStatus);
                        playerRecyclerView.setAdapter(adapter);

                        totalscorelayout.setVisibility(View.GONE);
                        submit.setVisibility(View.GONE);
                        nestedScrollView.setPadding(0, 0, 0, 0);
                        detaillayout.setVisibility(View.VISIBLE);

                        // set score
                        View setView1 = quarterLayout.getChildAt(pos);
                        EditText et_quarter_one = (EditText) setView1.findViewById(R.id.tv_q1);
                        EditText et_quarter_two = (EditText) setView1.findViewById(R.id.tv_q2);

                        tv_detail_team_two.setText(et_quarter_two.getText().toString());
                        tv_detail_team_one.setText(et_quarter_one.getText().toString());

                        tv_set_quarter_dropdown.setText(set + "Q");
                        tv_set_quarter_dropdown.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.up_arr, 0);
                    }
                });

                setNumber.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        int totalCount = quarterLayout.getChildCount();
                        final Integer pos = (Integer) view.getTag();
                        int set = pos + 1;

                        if (totalCount != 0 && totalCount == set && set != 1) {
                            reset.setVisibility(View.VISIBLE);
                            remove.setVisibility(View.VISIBLE);
                        }

                        remove.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int totalCount = quarterLayout.getChildCount();
                                Integer pos = (Integer) view.getTag();
                                int set = pos + 1;

                                if (totalCount != 0 && totalCount == set) {
                                    quarterLayout.removeViewAt(pos);
                                    setcount = setcount - 1;
                                    EnableSetAdd(setcount);
                                }
                            }
                        });

                        reset.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int totalCount = quarterLayout.getChildCount();
                                Integer pos = (Integer) view.getTag();
                                int set = pos + 1;

                                if (totalCount != 0 && totalCount == set) {
                                    quarterLayout.removeViewAt(pos);
                                    setcount = pos;
                                    scoreViewAdd();
                                }
                            }
                        });

                        return true;
                    }
                });
            }
        } else {
            for (int i = 0; i < totalcount; i++) {
                setcount = setcount + 1;
                mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                scoreview = mLayoutInflater.inflate(R.layout.row_basketball_score, null, false);
                setNumber = (TextView) scoreview.findViewById(R.id.tv_update_quarter_one);

                reset.setVisibility(View.GONE);
                remove.setVisibility(View.GONE);

                et_team_one = (EditText) scoreview.findViewById(R.id.tv_q1);
                et_team_two = (EditText) scoreview.findViewById(R.id.tv_q2);
                // et_team_one.setSelection(TextUtils.getTrimmedLength(et_team_one.getText()));
                // et_team_two.setSelectAllOnFocus(true);

                setNumber.setText(setcount + "Q");
                setNumber.setTag(setcount - 1);
                et_team_one.setTag(setcount - 1);
                et_team_two.setTag(setcount - 1);
                reset.setTag(setcount - 1);
                remove.setTag(setcount - 1);
                quarterLayout.addView(scoreview, setcount - 1, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                EnableSetAdd(setcount);
                mBasketballScoreUpdateDTOs = new ArrayList<>();
                Addsquad(mySquadPlayers);
                playerlist.put(setcount - 1, mBasketballScoreUpdateDTOs);

                if (setcount > 1) {
                    submit.setEnabled(true);
                    submit.setClickable(true);
                } else {
                    submit.setEnabled(false);
                    submit.setClickable(false);
                }

                setNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Integer pos = (Integer) view.getTag();
                        int set = pos + 1;

                        ArrayList<BasketballScoreUpdateDTO> list = playerlist.get(pos);
                        selectedPosition = pos;
                        adapter = new BasketballScoreAdapter(ScoreUpdateBasketballActivity.this, list,myTeamUpdateStatus);
                        playerRecyclerView.setAdapter(adapter);

                        totalscorelayout.setVisibility(View.GONE);
                        submit.setVisibility(View.GONE);
                        nestedScrollView.setPadding(0, 0, 0, 0);
                        detaillayout.setVisibility(View.VISIBLE);

                        // set score
                        View setView1 = quarterLayout.getChildAt(pos);
                        EditText et_quarter_one = (EditText) setView1.findViewById(R.id.tv_q1);
                        EditText et_quarter_two = (EditText) setView1.findViewById(R.id.tv_q2);

                        tv_detail_team_two.setText(et_quarter_two.getText().toString());
                        tv_detail_team_one.setText(et_quarter_one.getText().toString());

                        tv_set_quarter_dropdown.setText(set + "Q");
                        tv_set_quarter_dropdown.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.up_arr, 0);
                    }
                });

                setNumber.setLongClickable(false);
                add.setEnabled(false);
                add.setClickable(false);
                add.setAlpha(0.4f);
            }
        }

        nestedScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                nestedScrollView.fullScroll(View.FOCUS_DOWN);
            }
        }, 100);
    }

    private void EnableSetAdd(int count) {
        reset.setVisibility(View.GONE);
        remove.setVisibility(View.GONE);
        reset.setTag(setcount - 1);
        remove.setTag(setcount - 1);
        if (count == 11) {
            add.setEnabled(false);
            add.setAlpha(0.5f);
        } else {
            add.setEnabled(true);
            add.setAlpha(1f);
        }
    }

    public void CloseScoreUpdateActivity(View view) {
        ScoreUpdateBasketballActivity.this.finish();
    }

    private void Init() {
        playerlist = new HashMap<>();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        preferenceHelper = new PreferenceHelper(ScoreUpdateBasketballActivity.this, Constants.APP_NAME, 0);
        mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        nestedScrollView = (NestedScrollView) findViewById(R.id.parent_scrollview);
        scoreview = mLayoutInflater.inflate(R.layout.row_basketball_score, null);
        quarterLayout = (LinearLayout) findViewById(R.id.quarter_layout);
        totalscorelayout = (LinearLayout) findViewById(R.id.total_score_layout);
        detaillayout = (LinearLayout) findViewById(R.id.detail_score_layout);
        playerRecyclerView = (RecyclerView) findViewById(R.id.list_players);

        mMatchDateView = (TextView) findViewById(R.id.game_header_date);
        mMatchVenueView = (TextView) findViewById(R.id.game_header_location);
        mFirstTeamNameView = (TextView) findViewById(R.id.game_header_team_one_name);
        mSecondTeamNameView = (TextView) findViewById(R.id.game_header_team_two_name);
        mFirstTeamTotalScoreView = (TextView) findViewById(R.id.game_header_team_one_score);
        mSecondTeamTotalScoreView = (TextView) findViewById(R.id.game_header_team_two_score);
        mFirstTeamUpdateIndicationView = (ImageView) findViewById(R.id.team_one_winner_indicator);
        mSecondTeamUpdateIndicationView = (ImageView) findViewById(R.id.team_two_winner_indicator);
        mFirstTeamWonIndicationView = (ImageView) findViewById(R.id.winner_teamone);
        mSecondTeamWonIndicationView = (ImageView) findViewById(R.id.winner_teamtwo);
        mFirstTeamImageView = (ProfileImageView) findViewById(R.id.game_header_team);
        mSecondTeamImageView = (ProfileImageView) findViewById(R.id.game_header_team2);
        mMatchStatusView = (TextView) findViewById(R.id.game_header_team_score_update_status);
        mSportImageView = (ImageView) findViewById(R.id.game_header_sportimage);
        mMatchFlagImageView = (ImageView) findViewById(R.id.score_update_iv_flag_match);
        mSportImageView.setImageResource(R.drawable.ic_basketball_white);
        mFirstTeamUpdateIndicationView.setVisibility(View.GONE);
        mSecondTeamUpdateIndicationView.setVisibility(View.GONE);

        reset = (ImageButton) findViewById(R.id.btn_set_reset);
        remove = (ImageButton) findViewById(R.id.btn_set_delete);

        tv_set_quarter_dropdown = (TextView) findViewById(R.id.tv_update_quarter_dropdown);
        tv_detail_team_one = (TextView) findViewById(R.id.tv_q1);
        tv_detail_team_two = (TextView) findViewById(R.id.tv_q2);
        tv_set_quarter_dropdown.setOnClickListener(this);

        mProgressDialog = new Dialog(ScoreUpdateBasketballActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setContentView(R.layout.progressdialog_layout);

        add = (ImageButton) findViewById(R.id.btn_add);
        submit = (Button) findViewById(R.id.btn_submit);
        submit.setOnClickListener(this);
        submit.setVisibility(View.VISIBLE);
        add.setOnClickListener(this);
        mMatchFlagImageView.setOnClickListener(this);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    public void scoreupdate(BasketballScoreDTO basketballScoreDTO) {
        String auth_token = "Bearer " + preferenceHelper.getString("user_token", "");
        Call<ResponseBody> call;
        call = apiInterface.BasketballScoreUpdate(auth_token, basketballScoreDTO);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        JSONObject json = new JSONObject(data);
                        Toast.makeText(ScoreUpdateBasketballActivity.this, "Score updated", Toast.LENGTH_SHORT).show();
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    } else {
                        CommonUtils.ErrorHandleMethod(ScoreUpdateBasketballActivity.this, response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //  Log error here since request failed
                CommonUtils.ServerFailureHandleMethod(ScoreUpdateBasketballActivity.this, t);
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
        });
    }
}
