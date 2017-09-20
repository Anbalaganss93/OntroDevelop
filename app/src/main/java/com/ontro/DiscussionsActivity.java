package com.ontro;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ontro.adapters.DiscussionsAdapter;
import com.ontro.dto.DiscussionCommentInput;
import com.ontro.dto.DiscussionsCommentModel;
import com.ontro.rest.ApiClient;
import com.ontro.rest.ApiInterface;
import com.ontro.utils.CommonUtils;
import com.ontro.utils.PreferenceHelper;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiscussionsActivity extends AppCompatActivity implements View.OnClickListener {
    EditText et_post_comment;
    Typeface typeface_regular;
    private ArrayList<DiscussionsCommentModel> arrayList = new ArrayList<>();
    private DiscussionsAdapter adapter;
    private RecyclerView mlistView;
    private String title = "", name = "", content = "", timeago = "", comment_count = "", image = "", discussionid = "", discussion_flag_status = "0", playerId = "";
    private PreferenceHelper preferenceHelper;
    private ApiInterface apiInterface;
    private TextView listempty;
    private Dialog progress;
    private ImageView send, report_discussion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussions);
        overridePendingTransition(R.anim.slide_in_right, R.anim.pause);
        typeface_regular = Typeface.createFromAsset(getAssets(), "fonts/roboto_regular.ttf");

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        preferenceHelper = new PreferenceHelper(DiscussionsActivity.this, Constants.APP_NAME, 0);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        progress = new Dialog(DiscussionsActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progress.setContentView(R.layout.progressdialog_layout);
        progress.setCancelable(false);

        mlistView = (RecyclerView) findViewById(R.id.activity_discussions_rv);
        CircularImageView discussion_image = (CircularImageView) findViewById(R.id.discussion_image);
        ImageView back = (ImageView) findViewById(R.id.activity_discussions_iv_back);
        report_discussion = (ImageView) findViewById(R.id.report_discussion);
        LinearLayout activity_discussions = (LinearLayout) findViewById(R.id.activity_discussions);
        et_post_comment = (EditText) findViewById(R.id.et_comment);
        TextView discussion_title = (TextView) findViewById(R.id.discussion_title);
        TextView discussion_name = (TextView) findViewById(R.id.discussion_name);
        TextView seen_hours = (TextView) findViewById(R.id.seen_hours);
        send = (ImageView) findViewById(R.id.send);
        TextView discussion_comment = (TextView) findViewById(R.id.discussion_comment);
        TextView total_comment = (TextView) findViewById(R.id.total_comment);
        listempty = (TextView) findViewById(R.id.activity_discussions_tv_empty);
        listempty.setVisibility(View.GONE);
        send.setEnabled(true);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("title")) {
                playerId = intent.getStringExtra("player_id");
                title = StringEscapeUtils.unescapeJava(intent.getStringExtra("title"));
                name = intent.getStringExtra("name");
                timeago = intent.getStringExtra("timeago");
                content =   StringEscapeUtils.unescapeJava(intent.getStringExtra("comment"));
                comment_count = intent.getStringExtra("comment_total");
                image = intent.getStringExtra("image");
                discussionid = intent.getStringExtra("discussion_id");
            }
        }

        discussion_title.setText(StringEscapeUtils.unescapeJava(title));
        discussion_name.setText(name);
        seen_hours.setText(timeago);
        discussion_comment.setText(content);
        String subadd = Integer.parseInt(comment_count) > 1 ? "Comments" : "Comment";
        total_comment.setText(comment_count + " " + subadd);
        if(image != null && !image.isEmpty()) {
            Glide.with(DiscussionsActivity.this).load(image).placeholder(R.drawable.profiledefaultimg).dontAnimate().into(discussion_image);
        } else {
            Glide.with(DiscussionsActivity.this).load(R.drawable.profiledefaultimg).dontAnimate().into(discussion_image);
        }

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(DiscussionsActivity.this);
        mlistView.setLayoutManager(mLayoutManager);
        mlistView.setNestedScrollingEnabled(false);

        RelativeLayout.LayoutParams img_params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        img_params.width = (int) (dm.widthPixels * 0.15);
        img_params.height = ((int) (dm.widthPixels * 0.15));
        discussion_image.setLayoutParams(img_params);

        et_post_comment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        if (CommonUtils.isNetworkAvailable(DiscussionsActivity.this)) {
            DiscussionDetail_servercall();
        } else {
            Toast.makeText(DiscussionsActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }

        setfonts();
        back.setOnClickListener(this);
        send.setOnClickListener(this);
        discussion_image.setOnClickListener(this);
        report_discussion.setOnClickListener(this);
        activity_discussions.setOnClickListener(this);
    }

    public void setfonts() {
        et_post_comment.setTypeface(typeface_regular);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_discussions_iv_back:
                Intent intent = new Intent(DiscussionsActivity.this, HomeActivity.class);
                intent.putExtra("newdiscussion", "true");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
                break;
            case R.id.activity_discussions:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                break;
            case R.id.send:
                if (CommonUtils.isNetworkAvailable(DiscussionsActivity.this)) {
                    if (et_post_comment.getText().toString().trim().length() != 0) {
                        send.setEnabled(false);
                        String comment = et_post_comment.getText().toString().trim();
                        if(!isContainUniCodeChar(comment)) {
                            comment = StringEscapeUtils.escapeJava(comment);
                        }

                        Discussion_PostComment_servercall(comment);
                    } else {
                        et_post_comment.setError("Enter your comment");
                    }
                } else {
                    Toast.makeText(DiscussionsActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.report_discussion:
                String auth_token = "Bearer " + preferenceHelper.getString("user_token", "");
                if (discussion_flag_status.equals("0")) {
                    Call<ResponseBody> call = apiInterface.DiscussionFlag(discussionid, auth_token);
                    Discussion_Flag_servercall(call);
                } else {
                    Call<ResponseBody> call = apiInterface.DiscussionUnFlag(discussionid, auth_token);
                    Discussion_Flag_servercall(call);
                }
                break;
            case R.id.discussion_image :
                Intent profileIntent = new Intent(this, PlayerProfileActivity.class);
                profileIntent.putExtra(Constants.BundleKeys.PLAYER_ID, playerId);
                startActivity(profileIntent);
        }
    }

    private boolean isContainUniCodeChar(String comment) {
        char[] charArray = comment.toCharArray();
        for (char aCharArray : charArray) {
            return aCharArray < 128;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DiscussionsActivity.this, HomeActivity.class);
        intent.putExtra("newdiscussion", "true");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void DiscussionDetail_servercall() {
        progress.show();
        String auth_token = "Bearer " + preferenceHelper.getString("user_token", "");
        Call<ResponseBody> call = apiInterface.DiscussionDetail(discussionid, auth_token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (arrayList.size() != 0) {
                        arrayList.clear();
                    }
                    if (response.code() == 200) {
                        if (response.body() != null) {
                            String data = response.body().string();
                            Log.d("RESPONSE", data);
                            JSONObject json = new JSONObject(data);
                            JSONObject datajson = new JSONObject(json.getString("data"));
                            discussion_flag_status = datajson.getString("is_flaged");
                            if (discussion_flag_status.equals("0")) {
                                report_discussion.setImageResource(R.drawable.report);
                            } else {
                                report_discussion.setImageResource(R.drawable.report_discussion_red);
                            }
                            JSONArray array = new JSONArray(datajson.getString("comments"));
                            if (array.length() != 0) {
                                listempty.setVisibility(View.GONE);
                                for (int i = 0; i < array.length(); i++) {
                                    DiscussionsCommentModel m = new DiscussionsCommentModel();
                                    m.setComment(array.getJSONObject(i).getString("comment_text"));
                                    String countval = array.getJSONObject(i).getString("likes").equals("null") ? "0" : array.getJSONObject(i).getString("likes");
                                    m.setLikecount(countval);
                                    String createdate = array.getJSONObject(i).getString("created_at");
                                    m.setSeen_before(timeconverter(createdate));
                                    m.setLikestatus(array.getJSONObject(i).getString("is_liked"));
                                    m.setFlagstatus(array.getJSONObject(i).getString("is_flaged"));
                                    m.setName(array.getJSONObject(i).getString("name"));
                                    m.setComment_id(array.getJSONObject(i).getString("comment_id"));
                                    arrayList.add(m);
                                }
                            } else {
                                listempty.setVisibility(View.VISIBLE);
                            }
                        } else {
                            String error = response.message();
                            Toast.makeText(DiscussionsActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                        adapter = new DiscussionsAdapter(DiscussionsActivity.this, arrayList, discussionid);
                        mlistView.setAdapter(adapter);
//                        CommonUtils.ErrorHandleMethod(DiscussionsActivity.this,response);
                    } else {
//                        CommonUtils.ErrorHandleMethod(DiscussionsActivity.this,response);
                        String error = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(error);
                        String msg = jsonObject.getString("message");
                        Toast.makeText(DiscussionsActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(DiscussionsActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DiscussionsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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

    public void Discussion_Flag_servercall(Call<ResponseBody> call) {
        progress.show();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.code() == 200) {
                        if (response.body() != null) {
                            String message;
                            String data = response.body().string();
                            Log.d("RESPONSE", data);
                            if (discussion_flag_status.equals("0")) {
                                discussion_flag_status = "1";
                                report_discussion.setImageResource(R.drawable.report_discussion_red);
                                Animation myAnim = AnimationUtils.loadAnimation(DiscussionsActivity.this, R.anim.bounce);
                                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                                myAnim.setInterpolator(interpolator);
                                report_discussion.startAnimation(myAnim);
                            } else {
                                discussion_flag_status = "0";
                                report_discussion.setImageResource(R.drawable.report);
                            }
                        } else {
                            String error = response.message();
                            Toast.makeText(DiscussionsActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String error = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(error);
                        String msg = jsonObject.getString("message");
                        Toast.makeText(DiscussionsActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(DiscussionsActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DiscussionsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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

    public void Discussion_PostComment_servercall(String comment) {
        progress.show();
        String auth_token = "Bearer " + preferenceHelper.getString("user_token", "");
        DiscussionCommentInput m = new DiscussionCommentInput();
        m.setComment(comment);
        m.setDiscussion_id(discussionid);
        Call<ResponseBody> call = apiInterface.DiscussionCommentSent(auth_token, m);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        Toast.makeText(DiscussionsActivity.this, "Posted successfully", Toast.LENGTH_SHORT).show();
                        et_post_comment.setText("");
                        if (CommonUtils.isNetworkAvailable(DiscussionsActivity.this)) {
                            DiscussionDetail_servercall();
                        } else {
                            Toast.makeText(DiscussionsActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (response.errorBody() != null) {
                            String error = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(error);
                            String msg = jsonObject.getString("message");
                            Toast.makeText(DiscussionsActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            String error = response.message();
                            Toast.makeText(DiscussionsActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (progress != null && progress.isShowing()) {
                    progress.dismiss();
                }
                send.setEnabled(true);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //  Log error here since request failed
                try {
                    if (t instanceof SocketTimeoutException) {
                        Toast.makeText(DiscussionsActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DiscussionsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (progress != null && progress.isShowing()) {
                    progress.dismiss();
                }
                send.setEnabled(true);
            }
        });
    }

    public String timeconverter(String createdate) {
        String timeago = "";
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            Date past = format.parse(createdate);
            Date now = new Date();
            long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
            long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
            long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
            long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

            if (seconds < 60) {
                String subadd = seconds > 1 ? "seconds ago" : "second ago";
                timeago = seconds + " " + subadd;
            } else if (minutes < 60) {
                String subadd = minutes > 1 ? "minutes ago" : "minute ago";
                timeago = minutes + " " + subadd;
            } else if (hours < 24) {
                String subadd = hours > 1 ? "hours ago" : "hour ago";
                timeago = hours + " " + subadd;
            } else {
                String subadd = days > 1 ? "days ago" : "day ago";
                timeago = days + " " + subadd;
            }

        } catch (Exception j) {
            j.printStackTrace();
        }
        return timeago;
    }

    private class MyBounceInterpolator implements android.view.animation.Interpolator {
        double mAmplitude = 1;
        double mFrequency = 10;

        MyBounceInterpolator(double amplitude, double frequency) {
            mAmplitude = amplitude;
            mFrequency = frequency;
        }

        public float getInterpolation(float time) {
            return (float) (-1 * Math.pow(Math.E, -time / mAmplitude) *
                    Math.cos(mFrequency * time) + 1);
        }
    }
}
