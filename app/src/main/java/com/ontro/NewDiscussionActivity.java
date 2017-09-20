package com.ontro;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.ontro.dto.StartDiscussionInput;
import com.ontro.rest.ApiClient;
import com.ontro.rest.ApiInterface;
import com.ontro.utils.CommonUtils;
import com.ontro.utils.PreferenceHelper;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

import java.net.SocketTimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewDiscussionActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText discussion_name, discussion_content;
    private Typeface regular;
    private Button post;
    private ImageView back;
    private PreferenceHelper preferenceHelper;
    private ApiInterface apiInterface;
    private CircularProgressView progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_discussion);
        overridePendingTransition(R.anim.slide_in_right, R.anim.pause);
        regular = Typeface.createFromAsset(getAssets(), "fonts/roboto_regular.ttf");
        preferenceHelper = new PreferenceHelper(NewDiscussionActivity.this, Constants.APP_NAME, 0);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        discussion_name = (EditText) findViewById(R.id.discussion_name);
        discussion_content = (EditText) findViewById(R.id.discussion_content);
        progress = (CircularProgressView) findViewById(R.id.activity_explore_player_list_progress_view);
        progress.setVisibility(View.GONE);
        post = (Button) findViewById(R.id.post);
        back = (ImageView) findViewById(R.id.activity_explore_player_list_iv_back);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // only for LOLLIPOP and newer versions
            post.setBackground(ContextCompat.getDrawable(NewDiscussionActivity.this, R.drawable.button_bg));
        } else {
            post.setBackground(ContextCompat.getDrawable(NewDiscussionActivity.this, R.drawable.button_bg_normal));
        }

        discussion_name.setTypeface(regular);
        discussion_content.setTypeface(regular);
        post.setTypeface(regular);

        back.setOnClickListener(this);
        post.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_explore_player_list_iv_back:
                finish();
                overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
                break;
            case R.id.post:
                if (CommonUtils.isNetworkAvailable(NewDiscussionActivity.this)) {
                    if (validation()) {
                        String title = discussion_name.getText().toString().trim();
                        if(!isContainUniCodeChar(title)) {
                            title = StringEscapeUtils.escapeJava(title);
                        }
                        String content = discussion_content.getText().toString().trim();
                        if(!isContainUniCodeChar(content)) {
                            content = StringEscapeUtils.escapeJava(content);
                        }
                        Create_Discussion_servercall(title, content);
                    }
                } else {
                    Toast.makeText(NewDiscussionActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public boolean isContainUniCodeChar(String input) {
        char[] charArray = input.toCharArray();
        for (char aCharArray : charArray) {
            return aCharArray < 128;
        }
        return false;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(NewDiscussionActivity.this, HomeActivity.class);
        intent.putExtra("newdiscussion", "true");
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
    }

    public Boolean validation() {
        if (discussion_name.getText().toString().length() == 0) {
            discussion_name.setError("Provide title");
            return false;
        } else if (discussion_content.getText().toString().length() == 0) {
            discussion_content.setError("Provide description");
            return false;
        }
        return true;
    }

    public void Create_Discussion_servercall(String title, String content) {
        progress.setVisibility(View.VISIBLE);
        String auth_token = "Bearer " + preferenceHelper.getString("user_token", "");
        StartDiscussionInput m = new StartDiscussionInput();
        m.setTitle(title);
        m.setContent(content);
        Call<ResponseBody> call = apiInterface.NewDiscussion(auth_token, m);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                        Intent intent = new Intent(NewDiscussionActivity.this, HomeActivity.class);
                        intent.putExtra("newdiscussion", "true");
                        startActivity(intent);
                        finish();
                        Toast.makeText(NewDiscussionActivity.this, "Discussion created successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        if (response.errorBody() != null) {
                            String error = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(error);
                            String msg = jsonObject.getString("message");
                            Toast.makeText(NewDiscussionActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            String error = response.message();
                            Toast.makeText(NewDiscussionActivity.this, error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(NewDiscussionActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NewDiscussionActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progress.setVisibility(View.GONE);
            }
        });
    }
}
