package com.ontro;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.ontro.dto.CreateTeamInput;
import com.ontro.dto.TeamNameRequest;
import com.ontro.rest.ApiClient;
import com.ontro.rest.ApiInterface;
import com.ontro.utils.CommonUtils;
import com.ontro.utils.PreferenceHelper;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ontro.R.id.activity_create_team_rl_explore_team;

public class CreateTeamActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {
    public static Uri selectedImageUri;
    public static String encodedImage;
    private PreferenceHelper preferenceHelper;
    private TextView mTeamSportTextView, mTeamLocationTextView, mTeamLogoTextView;
    private EditText mTeamNameView, mTeamInfoView;
    private int sportid;
    private Dialog mySportDialog;
    private ArrayList<String> favsports;
    private ImageView mCreateTeamBackNavigationView;
    private RelativeLayout mCreateTeamButtonLayout, mTeamInfoLayout;
    private Dialog mProgressDialog;
    private ApiInterface apiInterface;
    private String authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);
        overridePendingTransition(R.anim.slide_in_right, R.anim.pause);
        initView();
        setListener();
    }

    private void initView() {
        preferenceHelper = new PreferenceHelper(CreateTeamActivity.this, Constants.APP_NAME, 0);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        authToken = "Bearer " + preferenceHelper.getString("user_token", "");
        mCreateTeamBackNavigationView = (ImageView) findViewById(R.id.activity_create_team_iv_back);
        mTeamLocationTextView = (TextView) findViewById(R.id.activity_create_team_tv_location);
        mTeamNameView = (EditText) findViewById(R.id.activity_create_team_et_team_name);
        mTeamSportTextView = (TextView) findViewById(R.id.activity_create_team_tv_sport);
        mTeamInfoView = (EditText) findViewById(R.id.activity_create_team_et_team_info);
        mTeamLogoTextView = (TextView) findViewById(R.id.activity_create_team_tv_upload_image);
        mCreateTeamButtonLayout = (RelativeLayout) findViewById(R.id.activity_create_team_rl_explore_team);
        mTeamInfoLayout = (RelativeLayout) findViewById(R.id.activity_create_team_rl_team_info);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/roboto_regular.ttf");
        mTeamNameView.setTypeface(typeface);
        mTeamInfoView.setTypeface(typeface);

        mProgressDialog = new Dialog(CreateTeamActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setContentView(R.layout.progressdialog_layout);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // only for LOLLIPOP and newer versions
            mCreateTeamButtonLayout.setBackground(ContextCompat.getDrawable(CreateTeamActivity.this, R.drawable.button_bg));
        } else {
            mCreateTeamButtonLayout.setBackground(ContextCompat.getDrawable(CreateTeamActivity.this, R.drawable.button_bg_normal));
        }
    }

    private void setListener() {
        mCreateTeamBackNavigationView.setOnClickListener(this);
        mCreateTeamButtonLayout.setOnClickListener(this);
        mTeamSportTextView.setOnClickListener(this);
        mTeamLogoTextView.setOnClickListener(this);
        mTeamLocationTextView.setOnClickListener(this);
        mTeamNameView.setOnFocusChangeListener(this);
        mTeamInfoView.setOnFocusChangeListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_create_team_iv_back:
                finish();
                overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
                break;
            case activity_create_team_rl_explore_team:
                if (mTeamNameView.getText().toString().trim().length() == 0) {
                    Toast.makeText(CreateTeamActivity.this, "Provide team name", Toast.LENGTH_SHORT).show();
                } else {
                    checkTeamAvailable();
                }
                break;
            case R.id.activity_create_team_tv_sport:
                mTeamNameView.clearFocus();
                mTeamInfoView.clearFocus();
                mTeamSportTextView.setBackground(ContextCompat.getDrawable(CreateTeamActivity.this, R.drawable.bg_create_team_selection_outline));
                mTeamLogoTextView.setBackground(ContextCompat.getDrawable(CreateTeamActivity.this, R.drawable.login_edittext_bg));
                mTeamLocationTextView.setBackground(ContextCompat.getDrawable(CreateTeamActivity.this, R.drawable.login_edittext_bg));
                mTeamInfoLayout.setBackground(ContextCompat.getDrawable(CreateTeamActivity.this, R.drawable.login_edittext_bg));
                mySportDialog = new Dialog(CreateTeamActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
                Window window = mySportDialog.getWindow();
                assert window != null;
                window.setGravity(Gravity.BOTTOM);
                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                mySportDialog.setTitle(null);
                mySportDialog.setCanceledOnTouchOutside(true);
                mySportDialog.setContentView(R.layout.adapter_sport_position_layout);
                RadioGroup rg = (RadioGroup) mySportDialog.findViewById(R.id.radiogroup);
                rg.setPadding(15, 10, 10, 10);
                final RadioButton[] rb = new RadioButton[favsports.size()];
                for (int i = 0; i < favsports.size(); i++) {
                    rb[i] = new RadioButton(CreateTeamActivity.this);
                    rb[i].setCompoundDrawablesWithIntrinsicBounds(R.drawable.discussion_dialog_radio_drawable, 0, 0, 0);
                    rb[i].setButtonDrawable(null);
                    rb[i].setPadding(15, 10, 10, 10);
                    rb[i].setCompoundDrawablePadding(10);
                    rg.addView(rb[i]);
                    rb[i].setText(CommonUtils.sportNameCheck(favsports.get(i)));
                    if (String.valueOf(sportid).equals(favsports.get(i))) {
                        rb[i].setChecked(true);
                    } else {
                        rb[i].setChecked(false);
                    }
                }

                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        View radioButton = radioGroup.findViewById(i);
                        int index = radioGroup.indexOfChild(radioButton);
                        sportid = Integer.valueOf(favsports.get(index));
                        mTeamSportTextView.setText(CommonUtils.sportNameCheck(favsports.get(index)));
                        mySportDialog.dismiss();
                    }
                });

                mySportDialog.show();
                break;
            case R.id.activity_create_team_tv_location:
                mTeamLocationTextView.setBackground(ContextCompat.getDrawable(CreateTeamActivity.this, R.drawable.bg_create_team_selection_outline));
                mTeamSportTextView.setBackground(ContextCompat.getDrawable(CreateTeamActivity.this, R.drawable.login_edittext_bg));
                mTeamLogoTextView.setBackground(ContextCompat.getDrawable(CreateTeamActivity.this, R.drawable.login_edittext_bg));
                mTeamInfoLayout.setBackground(ContextCompat.getDrawable(CreateTeamActivity.this, R.drawable.login_edittext_bg));
                mTeamNameView.clearFocus();
                mTeamInfoView.clearFocus();
                CommonUtils.locationdialog(CreateTeamActivity.this, mTeamLocationTextView, 3);
                break;
            case R.id.activity_create_team_tv_upload_image:
                mTeamNameView.clearFocus();
                mTeamInfoView.clearFocus();
                mTeamLocationTextView.setBackground(ContextCompat.getDrawable(CreateTeamActivity.this, R.drawable.login_edittext_bg));
                mTeamSportTextView.setBackground(ContextCompat.getDrawable(CreateTeamActivity.this, R.drawable.login_edittext_bg));
                mTeamLogoTextView.setBackground(ContextCompat.getDrawable(CreateTeamActivity.this, R.drawable.bg_create_team_selection_outline));
                mTeamInfoLayout.setBackground(ContextCompat.getDrawable(CreateTeamActivity.this, R.drawable.login_edittext_bg));
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(CreateTeamActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(CreateTeamActivity.this, new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.CommonKeys.MY_PERMISSIONS_REQUEST);
                    } else {
                        uploadpicture();
                    }
                } else {
                    uploadpicture();
                }
                break;
        }
    }

    private void checkTeamAvailable() {
        TeamNameRequest teamNameRequest = new TeamNameRequest();
        teamNameRequest.setTeamId(Constants.DefaultText.ZERO);
        teamNameRequest.setTeamName(mTeamNameView.getText().toString().trim());
        Call<ResponseBody> call = apiInterface.checkTeamNameAvailable(authToken, teamNameRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                        JSONObject json = new JSONObject(data);
                        String message = json.getString("message");
                        if(message.equals(Constants.DefaultText.TEAM_NAME_ALREADY_TAKEN)) {
                            Toast.makeText(CreateTeamActivity.this, Constants.Messages.TEAM_NAME_EXIST, Toast.LENGTH_LONG).show();
                            mTeamNameView.requestFocus();
                        } else {
                            String locationId = preferenceHelper.getString("user_location", "");
                            if (validation(locationId)) {
                                if(CommonUtils.isNetworkAvailable(CreateTeamActivity.this)) {
                                    createTeam(locationId, String.valueOf(sportid));
                                } else {
                                    Toast.makeText(CreateTeamActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                        return;
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                String error = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(error);
                                String msg = jsonObject.getString("message");
                                Toast.makeText(CreateTeamActivity.this, msg, Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                String error = response.message();
                                Toast.makeText(CreateTeamActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String error = response.message();
                            Toast.makeText(CreateTeamActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
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
                try {
                    if (t instanceof SocketTimeoutException) {
                        Toast.makeText(CreateTeamActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CreateTeamActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
        });
    }

    private void createTeam(final String locationId, final String sportId) {
        MixpanelAPI mMixpanel = MixpanelAPI.getInstance(this, getResources().getString(R.string.mixpanel_token));
        try {
            JSONObject eventJsonObject = new JSONObject();
            eventJsonObject.put("UserName", preferenceHelper.getString("user_name", ""));
            eventJsonObject.put("UserId", preferenceHelper.getString("user_id", ""));
            eventJsonObject.put("TeamName", mTeamNameView.getText().toString().trim());
            eventJsonObject.put("TeamLocation", mTeamLocationTextView.getText().toString().trim());
            eventJsonObject.put("TeamSport", mTeamSportTextView.getText().toString().trim());
            mMixpanel.track("TeamCreated", eventJsonObject);
        } catch (JSONException e) {
            Log.e("Ontro", "Unable to add properties to JSONObject", e);
        }
        mProgressDialog.show();
        CreateTeamInput input = new CreateTeamInput();
        input.setTeam_name(mTeamNameView.getText().toString().trim());
        input.setSport(sportId);
        input.setTeam_location(locationId);
        input.setTeam_logo(encodedImage);
        input.setTeam_about(mTeamInfoView.getText().toString().trim());
        Call<ResponseBody> call = apiInterface.CreateTeam(authToken, input);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                        JSONObject json = new JSONObject(data);
                        String teamId  = json.getString("data");

                        CreateTeamActivity.this.finish();
                        Intent intent = new Intent(CreateTeamActivity.this, ExplorePlayerListActivity.class);
                        intent.putExtra(Constants.BundleKeys.TEAM_ID, teamId);
                        intent.putExtra(Constants.BundleKeys.SPORT_ID, sportId);
                        intent.putExtra(Constants.BundleKeys.TEAM_NAME, mTeamNameView.getText().toString().trim());
                        startActivity(intent);
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                String error = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(error);
                                String msg = jsonObject.getString("message");
                                Toast.makeText(CreateTeamActivity.this, msg, Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                String error = response.message();
                                Toast.makeText(CreateTeamActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String error = response.message();
                            Toast.makeText(CreateTeamActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
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
                try {
                    if (t instanceof SocketTimeoutException) {
                        Toast.makeText(CreateTeamActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CreateTeamActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.CommonKeys.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            galleryimage(data);
        }
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            String path = getRealPathFromURI(resultUri);//uri.getLastPathSegment();
            String filename = path.substring(path.lastIndexOf("/") + 1);
            mTeamLogoTextView.setText(filename);
//            destinationImageuri = UCrop.getOutput(data);
            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), resultUri);
                converimgaetostring(bm);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            Toast.makeText(getApplicationContext(), String.valueOf(cropError), Toast.LENGTH_SHORT).show();
        }
    }

    public void galleryimage(Intent data) {
        Uri uri = data.getData();
        selectedImageUri = uri;
        /*
        String path = getRealPathFromURI(resultUri);//uri.getLastPathSegment();
        String filename = path.substring(path.lastIndexOf("/") + 1);
        mTeamLogoTextView.setText(filename);*/
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            converimgaetostring(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File f = new File(String.valueOf(destination));
        Uri yourUri = Uri.fromFile(f);
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setMaxScaleMultiplier(5);
        options.setFreeStyleCropEnabled(false);
        options.setCropGridColor(ContextCompat.getColor(this, R.color.colorAccent));
        options.setToolbarColor(ContextCompat.getColor(this, R.color.header_color));
        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.white));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        UCrop.of(yourUri, Uri.fromFile(destination))
                .withAspectRatio(3, 2)
                .withOptions(options)
                .start(this);
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            try {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
                if (result == null) {
                    result = contentURI.getPath();
                }
                cursor.close();
            } catch (Exception e) {
                result = contentURI.getPath();
                e.printStackTrace();
            }
        }
        return result;
    }

    public void converimgaetostring(Bitmap bitmap) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();
            encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
            preferenceHelper.save("base64", encodedImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String sports = preferenceHelper.getString("player_sports", "");
        favsports = new ArrayList<>();
        String[] myfavsportid = sports.split(",");
        for (int m = 0; m < myfavsportid.length; m++) {
            favsports.add(myfavsportid[m]);
        }
    }

    public void uploadpicture() {
        Intent image_intent = new Intent();
        image_intent.setType("image/*");
        image_intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(image_intent, Constants.CommonKeys.PICK_IMAGE_REQUEST);
    }

    public Boolean validation(String locationId) {
        if (mTeamSportTextView.getText().toString().trim().length() == 0) {
            Toast.makeText(CreateTeamActivity.this, "Provide team sport", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mTeamNameView.getText().toString().trim().length() == 0) {
            Toast.makeText(CreateTeamActivity.this, "Provide team name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (locationId.length() == 0 || mTeamLocationTextView.getText().length() == 0) {
            Toast.makeText(CreateTeamActivity.this, "Select Location", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mTeamInfoView.getText().toString().trim().length() == 0) {
            Toast.makeText(CreateTeamActivity.this, "Provide team info", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.CommonKeys.MY_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We can now safely use the API we requested access to
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    // Permission granted
                    uploadpicture();
                }
            } else {
                // Permission was denied or request was cancelled
                Toast.makeText(getApplicationContext(), "Permission was denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            switch (v.getId()) {
                case R.id.activity_create_team_et_team_name:
                    mTeamLocationTextView.setBackground(ContextCompat.getDrawable(CreateTeamActivity.this, R.drawable.login_edittext_bg));
                    mTeamSportTextView.setBackground(ContextCompat.getDrawable(CreateTeamActivity.this, R.drawable.login_edittext_bg));
                    mTeamLogoTextView.setBackground(ContextCompat.getDrawable(CreateTeamActivity.this, R.drawable.login_edittext_bg));
                    mTeamInfoLayout.setBackground(ContextCompat.getDrawable(CreateTeamActivity.this, R.drawable.login_edittext_bg));
                    break;
                case R.id.activity_create_team_et_team_info:
                    mTeamInfoView.requestFocus();
                    mTeamLocationTextView.setBackground(ContextCompat.getDrawable(CreateTeamActivity.this, R.drawable.login_edittext_bg));
                    mTeamSportTextView.setBackground(ContextCompat.getDrawable(CreateTeamActivity.this, R.drawable.login_edittext_bg));
                    mTeamLogoTextView.setBackground(ContextCompat.getDrawable(CreateTeamActivity.this, R.drawable.login_edittext_bg));
                    mTeamInfoLayout.setBackground(ContextCompat.getDrawable(CreateTeamActivity.this, R.drawable.bg_create_team_selection_outline));
                    break;
            }
        }
    }
}
