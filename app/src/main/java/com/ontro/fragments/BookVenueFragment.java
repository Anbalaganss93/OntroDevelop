package com.ontro.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ontro.Constants;
import com.ontro.R;
import com.ontro.adapters.BookVenueListAdapter;
import com.ontro.dto.BookVenueRequestModel;
import com.ontro.dto.VenueResponseModel;
import com.ontro.rest.ApiClient;
import com.ontro.rest.ApiInterface;
import com.ontro.utils.CommonUtils;
import com.ontro.utils.PreferenceHelper;
import com.philliphsu.bottomsheetpickers.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookVenueFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener, View.OnFocusChangeListener, View.OnTouchListener {
    private View mRootView;
    private TextView mMatchTypeTextView, mMatchLocationTextView, mMatchDateTextView;
    private Button mGetVenueButton;
    private RecyclerView mVenueListRecyclerView;
    private String matchType = "";
    private Dialog mProgressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_book_venue_details, container, false);
        initViews();
        initializeDialog();
        setScheduleBaseLayout();
        setButtonBackGround();
        setTypeFace();
        setLayoutManager();
        setListener();
        return mRootView;
    }

    private void initViews() {
        mMatchTypeTextView = (TextView) mRootView.findViewById(R.id.fragment_book_venue_details_tv_match_type);
        mMatchLocationTextView = (TextView) mRootView.findViewById(R.id.fragment_book_venue_details_tv_match_location);
        mMatchDateTextView = (TextView) mRootView.findViewById(R.id.fragment_book_venue_details_tv_match_date);
        mVenueListRecyclerView = (RecyclerView) mRootView.findViewById(R.id.fragment_book_venue_details_rv);
        mGetVenueButton = (Button) mRootView.findViewById(R.id.fragment_book_venue_details_btn_get_venue);
        CommonUtils.locationid = "";
    }

    private void initializeDialog() {
        mProgressDialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setContentView(R.layout.progressdialog_layout);
    }

    private void setTypeFace() {
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto_regular.ttf");
        mGetVenueButton.setTypeface(typeface);
    }

    private void setLayoutManager() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayout.HORIZONTAL, false);
        layoutManager.setAutoMeasureEnabled(true);
        mVenueListRecyclerView.setLayoutManager(layoutManager);
        mVenueListRecyclerView.setNestedScrollingEnabled(false);
    }

    private void setButtonBackGround() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // only for LOLLIPOP and newer versions
            mGetVenueButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_bg));
        } else {
            mGetVenueButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_bg_normal));
        }
    }

    private void setListener() {
        mMatchTypeTextView.setOnClickListener(this);
        mMatchLocationTextView.setOnClickListener(this);
        mMatchDateTextView.setOnClickListener(this);
        mGetVenueButton.setOnClickListener(this);

        mMatchTypeTextView.setOnFocusChangeListener(this);
        mMatchLocationTextView.setOnFocusChangeListener(this);
        mMatchDateTextView.setOnFocusChangeListener(this);

        mMatchTypeTextView.setOnTouchListener(this);
        mMatchLocationTextView.setOnTouchListener(this);
        mMatchDateTextView.setOnTouchListener(this);
    }

    private void setScheduleBaseLayout() {
        ImageView image1 = (ImageView) getActivity().findViewById(R.id.image1);
        ImageView image2 = (ImageView) getActivity().findViewById(R.id.image2);
        TextView mid_line = (TextView) getActivity().findViewById(R.id.mid_line);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        image1.setImageResource(R.drawable.s3);
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params2.width = (int) (dm.widthPixels * 0.1);
        params2.height = ((int) (dm.widthPixels * 0.1));
        params2.addRule(RelativeLayout.CENTER_VERTICAL);
        image1.setLayoutParams(params2);

        mid_line.setBackgroundColor(Color.parseColor("#32921C"));
        RelativeLayout.LayoutParams mid_line_param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mid_line_param.width = (int) (dm.widthPixels * 0.15);
        mid_line_param.height = 1;
        mid_line_param.addRule(RelativeLayout.RIGHT_OF, R.id.image1);
        mid_line_param.addRule(RelativeLayout.CENTER_VERTICAL);
        mid_line.setLayoutParams(mid_line_param);

        image2.setImageResource(R.drawable.s2);
        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params3.width = (int) (dm.widthPixels * 0.15);
        params3.height = ((int) (dm.widthPixels * 0.15));
        params3.addRule(RelativeLayout.CENTER_VERTICAL);
        params3.addRule(RelativeLayout.RIGHT_OF, R.id.mid_line);
        image2.setLayoutParams(params3);

        Animation anim = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out);
        image2.startAnimation(anim);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment_book_venue_details_tv_match_type:
                pickMatchTypeFromDialog();
                break;
            case R.id.fragment_book_venue_details_tv_match_location:
                CommonUtils.locationdialog(getActivity(), mMatchLocationTextView, 3);
                break;
            case R.id.fragment_book_venue_details_tv_match_date:
                mMatchDateTextView.setFocusable(true);
                mMatchDateTextView.setFocusableInTouchMode(true);
                mMatchDateTextView.requestFocus();
                Calendar now = Calendar.getInstance();
                DatePickerDialog date = DatePickerDialog.newInstance(
                        BookVenueFragment.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH));
                date.setBackgroundColor(Color.parseColor("#E9E9E9"));
                date.setHeaderColor(Color.parseColor("#18212A"));
                date.setHeaderTextColorSelected(Color.parseColor("#FFFFFF"));
                date.setHeaderTextColorUnselected(Color.parseColor("#E2E2E2"));
                date.setHeaderTextDark(true);
                date.setAccentColor(Color.parseColor("#18212A"));
                date.setMinDate(now);
                date.show(getFragmentManager(), "show");
                break;
            case R.id.fragment_book_venue_details_btn_get_venue:
                if (CommonUtils.isNetworkAvailable(getActivity())) {
                    if (isValid()) {
                        mProgressDialog.show();
                        getVenueDetail();
                    }
                } else {
                    Toast.makeText(getActivity(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private boolean isValid() {
        mMatchTypeTextView.setError(null);
        mMatchLocationTextView.setError(null);
        mMatchDateTextView.setError(null);

        if (mMatchTypeTextView.getText().toString().length() == 0) {
            mMatchTypeTextView.setError("Choose any one of the match type");
            mMatchTypeTextView.requestFocus();
            hideKeyboard(mMatchTypeTextView);
            return false;
        } else if (CommonUtils.locationid.length() == 0) {
            mMatchLocationTextView.setError("Provide location");
            mMatchLocationTextView.requestFocus();
            hideKeyboard(mMatchLocationTextView);
            return false;
        } else if (mMatchDateTextView.getText().toString().length() == 0) {
            mMatchDateTextView.setError("Provide date");
            mMatchDateTextView.requestFocus();
            hideKeyboard(mMatchDateTextView);
            return false;
        }
        return true;
    }

    private void pickMatchTypeFromDialog() {
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
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
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto_regular.ttf");
        rb_type1.setTypeface(typeface);
        rb_type2.setTypeface(typeface);
        rb_type3.setTypeface(typeface);
        switch (matchType) {
            case "1":
                rb_type1.setChecked(true);
                break;
            case "2":
                rb_type2.setChecked(true);
                break;
            case "3":
                rb_type3.setChecked(true);
                break;
        }

        rb_type1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                matchType = "1";
                mMatchTypeTextView.setText(R.string.street);
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        rb_type2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                matchType = "2";
                mMatchTypeTextView.setText(R.string.leak);
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        rb_type3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                matchType = "3";
                mMatchTypeTextView.setText(R.string.tournament);
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
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
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.fragment_book_venue_details_tv_match_type:
                if (hasFocus) {
                    hideKeyboard(v);
                    mMatchTypeTextView.setError("Choose any one of the match type");
                }
                break;
            case R.id.fragment_book_venue_details_tv_match_location:
                if (hasFocus) {
                    hideKeyboard(v);
                    mMatchLocationTextView.setError("Provide location");
                }
                break;
            case R.id.fragment_book_venue_details_tv_match_date:
                if (!hasFocus) {
                    mMatchDateTextView.requestFocus();
                    mMatchDateTextView.setError("Provide date");
                    hideKeyboard(v);
                }
        }
    }

    private void getVenueDetail() {
        PreferenceHelper preferenceHelper = new PreferenceHelper(getActivity(), Constants.APP_NAME, 0);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        String auth_token = "Bearer " + preferenceHelper.getString("user_token", "");
        final BookVenueRequestModel bookVenueRequestModel = new BookVenueRequestModel();
        bookVenueRequestModel.setMatchType(matchType);
        int matchTypeId = Integer.valueOf(matchType);
        int locationId = Integer.valueOf(CommonUtils.locationid);
        bookVenueRequestModel.setMatchLocation(CommonUtils.locationid);
        String matchDate = mMatchDateTextView.getText().toString().trim();
        bookVenueRequestModel.setMatchDate(matchDate);
        final List<VenueResponseModel> venueResponseModels = new ArrayList<>();
        Call<ResponseBody> call = apiInterface.getVenue(auth_token, matchTypeId, locationId, matchDate);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                        JSONObject json = new JSONObject(data);
                        JSONObject datajson = new JSONObject(json.getString("data"));
                        JSONArray jsonArray = new JSONArray(datajson.getString("data"));
                        if (venueResponseModels.size() != 0) {
                            venueResponseModels.clear();
                        }
                        if (jsonArray.length() != 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                VenueResponseModel venueResponseModel = new VenueResponseModel();
                                venueResponseModel.setVenueId(jsonArray.getJSONObject(i).getInt("venue_id"));
                                venueResponseModel.setVenueName(jsonArray.getJSONObject(i).getString("venue_name"));
                                venueResponseModel.setLocationName(jsonArray.getJSONObject(i).getString("location_name"));
                                venueResponseModel.setOpenFrom(jsonArray.getJSONObject(i).getString("open_from"));
                                venueResponseModel.setOpenTo(jsonArray.getJSONObject(i).getString("open_to"));
                                venueResponseModel.setAvgCost(jsonArray.getJSONObject(i).getString("avg_cost"));
                                venueResponseModel.setSport(jsonArray.getJSONObject(i).getString("sport"));
                                venueResponseModel.setVenueImages(jsonArray.getJSONObject(i).getString("Venue_images"));
                                venueResponseModels.add(venueResponseModel);
                            }
                            showVenueDetail(venueResponseModels);
                        } else {
                            Toast.makeText(getActivity(), "Venue not available", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                String error = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(error);
                                String msg = jsonObject.getString("message");
                                String code = jsonObject.getString("code");
                                if (!code.equals("500")) {
                                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), R.string.error500, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                String error = response.message();
                                Toast.makeText(getActivity(), R.string.error500, Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            String error = response.message();
                            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
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

    private void showVenueDetail(List<VenueResponseModel> bookVenueResponseModels) {
        BookVenueListAdapter adapter = new BookVenueListAdapter(getActivity(), bookVenueResponseModels);
        mVenueListRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.fragment_book_venue_details_tv_match_type:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mMatchTypeTextView.setFocusable(false);
                }
                break;
            case R.id.fragment_book_venue_details_tv_match_location:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mMatchLocationTextView.setFocusable(false);
                }
                break;
            case R.id.fragment_book_venue_details_tv_match_date:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mMatchDateTextView.setFocusable(false);
                }
                break;
        }
        return false;
    }
}
