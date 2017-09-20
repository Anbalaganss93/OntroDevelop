package com.ontro;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ontro.dto.InviteModel;
import com.ontro.dto.MyTeamDataBaseModel;
import com.ontro.dto.SportModel;
import com.ontro.rest.ApiClient;
import com.ontro.rest.ApiInterface;
import com.ontro.utils.CommonUtils;
import com.ontro.utils.PreferenceHelper;
import com.philliphsu.bottomsheetpickers.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MatchInviteDetailsActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    private TextView mTeamIdTextView, mOpponentTeamIdTextView, mMatchTypeTextView, mMatchLocationTextView, mMatchDateTextView;
    private ImageView mBackImageView;
    private Button mInviteSendButton;
    private Typeface typeface_regular;
    private String opponent_id = "", myteamid = "", matchtype = "", opponent_team_name = "", sport = "";
    private MyTeamDataBaseHelper myTeamDataBaseHelper;
    private ArrayList<MyTeamDataBaseModel> mtdatabase = new ArrayList<>();
    private PreferenceHelper preferenceHelper;
    private Dialog progress;
    private ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_details);
        overridePendingTransition(R.anim.slide_in_right, R.anim.pause);
        typeface_regular = Typeface.createFromAsset(getAssets(), "fonts/roboto_regular.ttf");
        preferenceHelper = new PreferenceHelper(MatchInviteDetailsActivity.this, Constants.APP_NAME, 0);
        myTeamDataBaseHelper = new MyTeamDataBaseHelper(MatchInviteDetailsActivity.this);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Intent intent = getIntent();
        if (intent != null) {
            opponent_id = intent.getStringExtra(Constants.BundleKeys.TEAM_ID);
            opponent_team_name = intent.getStringExtra(Constants.BundleKeys.TEAM_NAME);
            sport = intent.getStringExtra(Constants.BundleKeys.SPORT_ID);
        }

        progress = new Dialog(MatchInviteDetailsActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progress.setContentView(R.layout.progressdialog_layout);

        mBackImageView = (ImageView) findViewById(R.id.activity_invite_detail_iv_back);
        mTeamIdTextView = (TextView) findViewById(R.id.activity_invite_detail_tv_teamid);
        mOpponentTeamIdTextView = (TextView) findViewById(R.id.activity_invite_detail_tv_opponentteamid);
//        mMatchTypeTextView = (TextView) findViewById(R.id.activity_invite_detail_tv_match_type);
        mMatchLocationTextView = (TextView) findViewById(R.id.activity_invite_detail_tv_location);
        mMatchDateTextView = (TextView) findViewById(R.id.activity_invite_detail_tv_match_date);
        mInviteSendButton = (Button) findViewById(R.id.activity_invite_detail_bt_invitesend);

        mOpponentTeamIdTextView.setText(opponent_team_name);
        mInviteSendButton.setEnabled(true);

        mTeamIdTextView.setOnClickListener(this);
        mOpponentTeamIdTextView.setOnClickListener(this);
//        mMatchTypeTextView.setOnClickListener(this);
        mMatchLocationTextView.setOnClickListener(this);
        mMatchDateTextView.setOnClickListener(this);
        mBackImageView.setOnClickListener(this);
        mInviteSendButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_invite_detail_tv_location:
                CommonUtils.locationdialog(MatchInviteDetailsActivity.this, mMatchLocationTextView, 3);
                break;
            case R.id.activity_invite_detail_iv_back:
                CommonUtils.locationid = "";
                finish();
                overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
                break;
            case R.id.activity_invite_detail_tv_match_date:
                Calendar now = Calendar.getInstance();
                DatePickerDialog date = DatePickerDialog.newInstance(
                        MatchInviteDetailsActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH));
                date.setBackgroundColor(Color.parseColor("#E9E9E9"));
                date.setHeaderColor(Color.parseColor("#18212A"));
                date.setHeaderTextColorSelected(Color.parseColor("#FFFFFF"));
                date.setHeaderTextColorUnselected(Color.parseColor("#E2E2E2"));
                date.setMinDate(now);
                date.setHeaderTextDark(true);
                date.setAccentColor(Color.parseColor("#18212A"));
                date.show(getSupportFragmentManager(), "show");
                break;
           /* case R.id.activity_invite_detail_tv_matchtype:
                final Dialog dialog = new Dialog(MatchInviteDetailsActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
                Window window = dialog.getWindow();
                assert window != null;
                window.setGravity(Gravity.BOTTOM);
                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                dialog.setTitle(null);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setContentView(R.layout.dialog_invite_matchtype);
                RadioButton rb_type1 = (RadioButton) dialog.findViewById(R.id.rb_type1);
                RadioButton rb_type2 = (RadioButton) dialog.findViewById(R.id.rb_type2);
                RadioButton rb_type3 = (RadioButton) dialog.findViewById(R.id.rb_type3);
                rb_type1.setTypeface(typeface_regular);
                rb_type2.setTypeface(typeface_regular);
                rb_type3.setTypeface(typeface_regular);

                rb_type1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        matchtype = "1";
                        mMatchTypeTextView.setText(R.string.street);
                        mMatchTypeTextView.setFocusable(false);
                        mMatchTypeTextView.setError(null);
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });

                rb_type2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        matchtype = "2";
                        mMatchTypeTextView.setText(R.string.leak);
                        mMatchTypeTextView.setFocusable(false);
                        mMatchTypeTextView.setError(null);
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });

                rb_type3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        matchtype = "3";
                        mMatchTypeTextView.setText(R.string.tournament);
                        mMatchTypeTextView.setFocusable(false);
                        mMatchTypeTextView.setError(null);
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
                break;*/
            case R.id.activity_invite_detail_tv_teamid:
                try {
                    ArrayList<MyTeamDataBaseModel> baseModels = myTeamDataBaseHelper.getAllTeams();
                    if (baseModels != null) {
                        mtdatabase = new ArrayList<>();
                        for (int i = 0; i < baseModels.size(); i++) {
                            MyTeamDataBaseModel myTeamDataBaseModel = baseModels.get(i);
                            if (sport.equals(myTeamDataBaseModel.getSportid())) {
                                mtdatabase.add(myTeamDataBaseModel);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (mtdatabase.size() == 0) {
                    if (CommonUtils.isNetworkAvailable(MatchInviteDetailsActivity.this)) {
                        GetTeams();
                    } else {
                        Toast.makeText(MatchInviteDetailsActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    MyTeamListDialog(mtdatabase);
                }
                break;
            case R.id.activity_invite_detail_bt_invitesend:
                if (CommonUtils.isNetworkAvailable(MatchInviteDetailsActivity.this)) {
                    if (validation()) {
                        mInviteSendButton.setEnabled(false);
                        progress.show();
                        GetInvite();
                    }
                } else {
                    Toast.makeText(MatchInviteDetailsActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public Boolean validation() {
        if (myteamid.length() == 0) {
            Toast.makeText(MatchInviteDetailsActivity.this, "Choose your team", Toast.LENGTH_SHORT).show();
            return false;
        } else if (opponent_id.length() == 0) {
            mOpponentTeamIdTextView.setError("No opponent team");
            return false;
        } /*else if (mMatchTypeTextView.getText().toString().length() == 0) {
            Toast.makeText(MatchInviteDetailsActivity.this, "Choose any one of the match type", Toast.LENGTH_SHORT).show();
            return false;
        }*/ else if (CommonUtils.locationid.length() == 0 || mMatchLocationTextView.getText().toString().length() == 0) {
            Toast.makeText(MatchInviteDetailsActivity.this, "Provide location", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mMatchDateTextView.getText().toString().length() == 0) {
            Toast.makeText(MatchInviteDetailsActivity.this, "Provide date", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void GetTeams() {
        progress.show();
        String auth_token = "Bearer " + preferenceHelper.getString("user_token", "");
        Call<ResponseBody> call = apiInterface.MyTeam(auth_token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        myTeamDataBaseHelper.deletealldata();
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                        JSONObject json = new JSONObject(data);
                        if (!json.getString("data").equals("0")) {
                            JSONArray array = new JSONArray(json.getString("data"));
                            for (int i = 0; i < array.length(); i++) {
                                SportModel sportModel = new SportModel();
                                sportModel.setSportid(array.getJSONObject(i).getString("team_id"));
                                sportModel.setSportname(array.getJSONObject(i).getString("team_name"));
                                String logo = array.getJSONObject(i).getString("team_logo").equals("null") ? "http://euroguide.fourfourtwo.com/quiz-nation/media/theme/badge-wal.png" : array.getJSONObject(i).getString("team_logo");
                                sportModel.setTeamlogo(logo);
                                String id = array.getJSONObject(i).getString("sport");
                                sportModel.setIswoner(array.getJSONObject(i).getInt("is_owner"));
                                sportModel.setSportimage(CommonUtils.sportCheck(id));
                                sportModel.setLocation(array.getJSONObject(i).getString("team_location"));
                                String percentage = array.getJSONObject(i).getString("progress").equals("null") ? "0" : array.getJSONObject(i).getString("progress");
                                sportModel.setProgress_percent(percentage);
                                sportModel.setBatchimage(CommonUtils.batchCheck(array.getJSONObject(i).getString("badge")));
                                if (sportModel.getIswoner() == 1) {
                                    myTeamDataBaseHelper.insertContact(array.getJSONObject(i).getString("team_name"), array.getJSONObject(i).getString("team_id"), array.getJSONObject(i).getString("sport"));
                                }
                            }
                        }
                        try {
                            ArrayList<MyTeamDataBaseModel> baseModels = myTeamDataBaseHelper.getAllTeams();
                            if (baseModels != null) {
                                mtdatabase = new ArrayList<>();
                                for (int i = 0; i < baseModels.size(); i++) {
                                    MyTeamDataBaseModel myTeamDataBaseModel = baseModels.get(i);
                                    if (sport.equals(myTeamDataBaseModel.getSportid())) {
                                        mtdatabase.add(myTeamDataBaseModel);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (mtdatabase.size() != 0) {
                            MyTeamListDialog(mtdatabase);
                        } else {
                            Toast.makeText(MatchInviteDetailsActivity.this, "Create a team", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (response.errorBody() != null) {
                            String error = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(error);
                            String msg = jsonObject.getString("message");
                            Toast.makeText(MatchInviteDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            String error = response.message();
                            Toast.makeText(MatchInviteDetailsActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MatchInviteDetailsActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MatchInviteDetailsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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

    public void GetInvite() {
        String auth_token = "Bearer " + preferenceHelper.getString("user_token", "");
        InviteModel m = new InviteModel();
        m.setTeam_id(myteamid);
        m.setOpponent_team_id(opponent_id);
        m.setMatch_type(Constants.DefaultText.ONE);
        m.setSport_type(sport);
        m.setGametype(Constants.DefaultText.TWO);
        m.setLocation(CommonUtils.locationid);
        m.setMatch_date(mMatchDateTextView.getText().toString().trim());
        Call<ResponseBody> call = apiInterface.Invite(auth_token, m);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                        Toast.makeText(MatchInviteDetailsActivity.this, Constants.Messages.MATCH_INVITE_SENT_SUCCESSFULLY, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MatchInviteDetailsActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        CommonUtils.ErrorHandleMethod(MatchInviteDetailsActivity.this, response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (progress != null && progress.isShowing()) {
                    progress.dismiss();
                }
                mInviteSendButton.setEnabled(true);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //  Log error here since request failed
                CommonUtils.ServerFailureHandleMethod(MatchInviteDetailsActivity.this, t);
                if (progress != null && progress.isShowing()) {
                    progress.dismiss();
                }
                mInviteSendButton.setEnabled(true);
            }
        });
    }

    private void MyTeamListDialog(final ArrayList<MyTeamDataBaseModel> arrayList) {
        final Dialog dialog = new Dialog(MatchInviteDetailsActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        Window window = dialog.getWindow();
        assert window != null;
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.adapter_sport_position_layout);
        RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.radiogroup);
        rg.setPadding(15, 10, 10, 10);
        final RadioButton[] rb = new RadioButton[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            rb[i] = new RadioButton(MatchInviteDetailsActivity.this);
            rb[i].setCompoundDrawablesWithIntrinsicBounds(R.drawable.discussion_dialog_radio_drawable, 0, 0, 0);
            rb[i].setButtonDrawable(null);
            rb[i].setPadding(15, 10, 10, 10);
            rb[i].setCompoundDrawablePadding(10);
            rg.addView(rb[i]);
            rb[i].setText(arrayList.get(i).getTeamname());
        }

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View radioButton = radioGroup.findViewById(i);
                int index = radioGroup.indexOfChild(radioButton);
                mTeamIdTextView.setText(arrayList.get(index).getTeamname());
                myteamid = arrayList.get(index).getTeamid();
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                    mTeamIdTextView.setFocusable(false);
                    mTeamIdTextView.setError(null);
                }
            }
        });

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CommonUtils.locationid = "";
        finish();
        overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
    }

    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        Calendar cal = new java.util.GregorianCalendar();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        Date date = cal.getTime();
        String datetime = format.format(date);
        mMatchDateTextView.setText(datetime);
        mMatchDateTextView.setFocusable(false);
        mMatchDateTextView.setError(null);
    }

}
