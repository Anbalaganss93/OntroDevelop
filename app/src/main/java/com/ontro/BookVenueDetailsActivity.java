package com.ontro;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker;
import com.ontro.adapters.AmenitiesAdapter;
import com.ontro.adapters.SportFacilitiesAdapter;
import com.ontro.dto.ConfirmBookingInputModel;
import com.ontro.dto.VenueAmenityModel;
import com.ontro.dto.VenueDetailResponseModel;
import com.ontro.dto.VenueImageModel;
import com.ontro.rest.ApiClient;
import com.ontro.rest.ApiInterface;
import com.ontro.utils.CommonUtils;
import com.ontro.utils.PreferenceHelper;
import com.philliphsu.bottomsheetpickers.time.BottomSheetTimePickerDialog;
import com.philliphsu.bottomsheetpickers.time.numberpad.NumberPadTimePickerDialog;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BookVenueDetailsActivity extends AppCompatActivity implements View.OnClickListener, BottomSheetTimePickerDialog.OnTimeSetListener, DatePickerListener {
    public int dateposition = 0;
    private String matchdate = "", match_id = "", myTeamId = "", totime24hour = "", fromtime24hour = "";
    private FrameLayout mViewPagerContainer;
    private ImageView mBackImageView;
    private RecyclerView mSportingFacilitiesRecyclerView, mAmenitiesRecyclerView;
    private LinearLayout mFromTimeContainer, mToTimeContainer, mAmenitiesLayout;
    private TextView mVenueNameTextView, mVenueLocationNameTextView, mOpeningTimeTextView,
            mVenueUtilizationCostTextView, mFromTimeTextView, mToTimeTextView, mVenueImageCountView;
    private ViewPager myVenueImageViewPager;
    private Button mVenueBookingButton;
    private HorizontalPicker mDatePicker;
    private Dialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_venue_details);
        overridePendingTransition(R.anim.slide_in_right, R.anim.pause);
        initView();
        setLayoutParams();
        setTypeFace();
        setListener();
        initializeHorizontalDatePicker();
        initializeDialog();
        if (CommonUtils.isNetworkAvailable(BookVenueDetailsActivity.this)) {
            getVenueDetail();
        } else {
            Toast.makeText(BookVenueDetailsActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeDialog() {
        mProgressDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setContentView(R.layout.progressdialog_layout);
    }

    private void initializeHorizontalDatePicker() {
        Calendar cal = Calendar.getInstance();
        Date currentLocalTime = cal.getTime();
        java.text.DateFormat simpleDateFormat2 = new SimpleDateFormat("HH:mm", Locale.US);
        fromtime24hour = simpleDateFormat2.format(currentLocalTime);

        java.text.DateFormat simpleDateFormat = new SimpleDateFormat("h:mma", Locale.US);
        mFromTimeTextView.setText(simpleDateFormat.format(currentLocalTime));
        cal.add(Calendar.HOUR, 1);

        Date toTime = cal.getTime();
        mToTimeTextView.setText(simpleDateFormat.format(toTime));

        java.text.DateFormat simpleDateFormat4 = new SimpleDateFormat("HH:mm", Locale.US);
        totime24hour = simpleDateFormat4.format(toTime);
        mDatePicker.setListener(this).setOffset(10).init();
        mDatePicker.setDate(new DateTime());
    }

    private void initView() {
        PreferenceHelper preferenceHelper = new PreferenceHelper(BookVenueDetailsActivity.this, Constants.APP_NAME, 0);
        myTeamId = preferenceHelper.getString("myteamid", "");
        match_id = preferenceHelper.getString("match_id", "");

        mViewPagerContainer = (FrameLayout) findViewById(R.id.activity_book_venue_details_fl_view_pager_container);
        mBackImageView = (ImageView) findViewById(R.id.activity_book_venue_details_iv_back);
        mVenueImageCountView = (TextView) findViewById(R.id.activity_book_venue_details_iv_gallery_count);
        mBackImageView.bringToFront();
        mVenueImageCountView.bringToFront();
        mVenueBookingButton = (Button) findViewById(R.id.activity_book_venue_details_btn_booking);
        mSportingFacilitiesRecyclerView = (RecyclerView) findViewById(R.id.activity_book_venue_details_rl_sporting_facilities);
        mAmenitiesRecyclerView = (RecyclerView) findViewById(R.id.activity_book_venue_details_rl_amenities);
        mFromTimeContainer = (LinearLayout) findViewById(R.id.activity_book_venue_details_ll_from_time_container);
        mToTimeContainer = (LinearLayout) findViewById(R.id.activity_book_venue_details_ll_to_time_container);
        mFromTimeTextView = (TextView) findViewById(R.id.activity_book_venue_details_tv_from_time);
        mToTimeTextView = (TextView) findViewById(R.id.activity_book_venue_details_tv_to_time);
        myVenueImageViewPager = (ViewPager) findViewById(R.id.venueviewpager);
        mDatePicker = (HorizontalPicker) findViewById(R.id.activity_book_venue_details_date_picker);
        mVenueNameTextView = (TextView) findViewById(R.id.activity_book_venue_details_tv_venue_name);
        mVenueLocationNameTextView = (TextView) findViewById(R.id.activity_book_venue_details_tv_location_name);
        mOpeningTimeTextView = (TextView) findViewById(R.id.activity_book_venue_details_tv_opening_time);
        mVenueUtilizationCostTextView = (TextView) findViewById(R.id.activity_book_venue_details_tv_venue_price);
        mAmenitiesLayout = (LinearLayout) findViewById(R.id.activity_book_venue_details_ll_amenities_layout);
    }

    private void setLayoutParams() {
        /*Layout params for venue image view pager*/
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = (int) (dm.widthPixels);
        params.height = (int) (dm.heightPixels * 0.4);
        mViewPagerContainer.setLayoutParams(params);
        /*Layout params for sport facilities recycler view*/
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(BookVenueDetailsActivity.this, LinearLayout.HORIZONTAL, false);
        mLayoutManager.setAutoMeasureEnabled(true);
        mSportingFacilitiesRecyclerView.setLayoutManager(mLayoutManager);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSportingFacilitiesRecyclerView.setNestedScrollingEnabled(false);
        }
        /*Layout params for amenities recycler view*/
        LinearLayoutManager mLayoutManager2 = new GridLayoutManager(BookVenueDetailsActivity.this, 3);
        mLayoutManager2.setAutoMeasureEnabled(true);
        mAmenitiesRecyclerView.setLayoutManager(mLayoutManager2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mAmenitiesRecyclerView.setNestedScrollingEnabled(false);
        }
    }

    private void setTypeFace() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/roboto_regular.ttf");
        mVenueBookingButton.setTypeface(typeface);
    }

    private void setListener() {
        mFromTimeContainer.setOnClickListener(this);
        mToTimeContainer.setOnClickListener(this);
        mBackImageView.setOnClickListener(this);
        mVenueBookingButton.setOnClickListener(this);
    }

    private void getVenueDetail() {
        PreferenceHelper preferenceHelper = new PreferenceHelper(this, Constants.APP_NAME, 0);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        String auth_token = "Bearer " + preferenceHelper.getString("user_token", "");
        final List<VenueDetailResponseModel> bookVenueResponseModels = new ArrayList<>();
        Intent intent = getIntent();
        if (intent != null) {
            int venueId = intent.getIntExtra(Constants.BundleKeys.VENUE_ID, 0);
            Call<ResponseBody> call = apiInterface.getVenueDetail(auth_token, venueId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.body() != null && response.code() == 200) {
                            String data = response.body().string();
                            Log.d("RESPONSE", data);
                            JSONObject json = new JSONObject(data);
                            JSONObject jsonObject = json.getJSONObject("data");
                            VenueDetailResponseModel venueDetailResponseModel = new VenueDetailResponseModel();
                            venueDetailResponseModel.setVenueId(jsonObject.getInt("venue_id"));
                            venueDetailResponseModel.setVenueName(jsonObject.getString("venue_name"));
                            mVenueNameTextView.setText(venueDetailResponseModel.getVenueName());
                            venueDetailResponseModel.setLocationName(jsonObject.getString("location_name"));
                            mVenueLocationNameTextView.setText(venueDetailResponseModel.getLocationName());
                            venueDetailResponseModel.setOpenFrom(jsonObject.getString("open_from"));
                            venueDetailResponseModel.setOpenTo(jsonObject.getString("open_to"));
                            mOpeningTimeTextView.setText(Constants.DefaultText.AVAILABLE_ON + getTimeFormat(venueDetailResponseModel.getOpenFrom())
                                    + Constants.DefaultText.TO + getTimeFormat(venueDetailResponseModel.getOpenTo()));
                            venueDetailResponseModel.setAvgCost(jsonObject.getString("avg_cost"));
                            mVenueUtilizationCostTextView.setText(Constants.DefaultText.RUPEES_SYMBOL + String.format("%.2f", Double.valueOf(venueDetailResponseModel.getAvgCost())));
                            List<VenueAmenityModel> venueAmenityModels = new ArrayList<>();
                            JSONArray amenityArrays = jsonObject.getJSONArray("amenities");
                            if (amenityArrays.length() == 0) {
                                mAmenitiesLayout.setVisibility(View.GONE);
                            } else {
                                for (int i = 0; i < amenityArrays.length(); i++) {
                                    VenueAmenityModel venueAmenityModel = new VenueAmenityModel();
                                    venueAmenityModel.setAmenity(amenityArrays.getJSONObject(i).getString("amenitie"));
                                    venueAmenityModels.add(venueAmenityModel);
                                }
                                showVenueAmenities(venueAmenityModels);
                            }
                            venueDetailResponseModel.setSport(jsonObject.getString("sport"));
                            String[] sportArrays = venueDetailResponseModel.getSport().split(",");
                            List<String> sports = Arrays.asList(sportArrays);
                            showSportFacilities(sports);

                            JSONArray venueImageArrays = jsonObject.getJSONArray("images");
                            List<VenueImageModel> venueImageModels = new ArrayList<>();
                            mVenueImageCountView.setText(String.valueOf(venueImageArrays.length()));
                            for (int i = 0; i < venueImageArrays.length(); i++) {
                                VenueImageModel venueImageModel = new VenueImageModel();
                                venueImageModel.setImage(venueImageArrays.getJSONObject(i).getString("image"));
                                venueImageModels.add(venueImageModel);
                            }
                            venueDetailResponseModel.setImages(venueImageModels);
                            showViewPagerImages(venueImageModels);

                        } else {
                            if (response.errorBody() != null) {
                                String error = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(error);
                                String msg = jsonObject.getString("message");
                                String code = jsonObject.getString("code");
                                if (!code.equals("500")) {
                                    Toast.makeText(BookVenueDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                String error = response.message();
                                Toast.makeText(BookVenueDetailsActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(BookVenueDetailsActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BookVenueDetailsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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
    }

    private void getVenueConfimation() {
        PreferenceHelper preferenceHelper = new PreferenceHelper(this, Constants.APP_NAME, 0);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        String auth_token = "Bearer " + preferenceHelper.getString("user_token", "");
        Intent intent = getIntent();
        if (intent != null) {
            int venueId = intent.getIntExtra(Constants.BundleKeys.VENUE_ID, 0);
            String totime = mToTimeTextView.getText().toString().trim();
            String fromtime = mFromTimeTextView.getText().toString().trim();

            ConfirmBookingInputModel input = new ConfirmBookingInputModel();
            input.setVenue_id(String.valueOf(venueId));
            input.setTo_time(totime24hour);
            input.setFrom_time(fromtime24hour);
            input.setMatch_id(match_id);
            input.setMatch_date(matchdate);
            input.setTeam_id(myTeamId);

            Call<ResponseBody> call = apiInterface.ConfirmBooking(auth_token, input);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.body() != null && response.code() == 200) {
                            String data = response.body().string();
                            Log.d("RESPONSE", data);
                            JSONObject json = new JSONObject(data);
                            Toast.makeText(BookVenueDetailsActivity.this, "Booking confirmed", Toast.LENGTH_SHORT).show();
                            Intent homeintent = new Intent(BookVenueDetailsActivity.this, HomeActivity.class);
                            startActivity(homeintent);
                            finish();
                        } else {
                            if (response.errorBody() != null) {
                                try {
                                    String error = response.errorBody().string();
                                    JSONObject jsonObject = new JSONObject(error);
                                    String msg = jsonObject.getString("message");
                                    String code = jsonObject.getString("code");
                                    if (!code.equals("500")) {
                                        Toast.makeText(BookVenueDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(BookVenueDetailsActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    String error = response.message();
                                    Toast.makeText(BookVenueDetailsActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                String error = response.message();
                                Toast.makeText(BookVenueDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    mVenueBookingButton.setEnabled(true);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    //  Log error here since request failed
                    try {
                        if (t instanceof SocketTimeoutException) {
                            Toast.makeText(BookVenueDetailsActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BookVenueDetailsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    mVenueBookingButton.setEnabled(true);
                }
            });
        }
    }

    private String getTimeFormat(String openingTime) {
        String time = null;
        try {
            java.text.DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            Date date = dateFormat.parse(openingTime);
            java.text.DateFormat simpleDateFormat = new SimpleDateFormat("h:mma");
            time = simpleDateFormat.format(date).toLowerCase();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    private void showViewPagerImages(List<VenueImageModel> venueImageModels) {
        MyPageAdapter bannerAdapter = new MyPageAdapter(BookVenueDetailsActivity.this, venueImageModels);
        myVenueImageViewPager.setAdapter(bannerAdapter);
        myVenueImageViewPager.setCurrentItem(0);
    }

    private void showSportFacilities(List<String> sports) {
        SportFacilitiesAdapter adapter = new SportFacilitiesAdapter(BookVenueDetailsActivity.this, sports);
        mSportingFacilitiesRecyclerView.setAdapter(adapter);
    }

    private void showVenueAmenities(List<VenueAmenityModel> venueAmenityModels) {
        AmenitiesAdapter adapter2 = new AmenitiesAdapter(BookVenueDetailsActivity.this, venueAmenityModels);
        mAmenitiesRecyclerView.setAdapter(adapter2);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
    }

    @Override
    public void onTimeSet(ViewGroup viewGroup, int hourOfDay, int minute) {
        switch (dateposition) {
            case 0:
                Log.d("DATE", String.valueOf(hourOfDay));
                Calendar cal = new java.util.GregorianCalendar();
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cal.set(Calendar.MINUTE, minute);
                String doublemin = String.valueOf(minute);
                if (String.valueOf(minute).length() == 1) {
                    doublemin = String.valueOf("0" + minute);
                }
                fromtime24hour = String.valueOf(hourOfDay) + ":" + doublemin;
                mFromTimeTextView.setText(DateFormat.getTimeFormat(this).format(cal.getTime()));
                break;
            case 1:
                Log.d("DATE", String.valueOf(hourOfDay));
                Calendar cal2 = new java.util.GregorianCalendar();
                cal2.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cal2.set(Calendar.MINUTE, minute);
                String doubletomin = String.valueOf(minute);
                if (String.valueOf(minute).length() == 1) {
                    doubletomin = String.valueOf("0" + minute);
                }
                totime24hour = String.valueOf(hourOfDay) + ":" + doubletomin;
                mToTimeTextView.setText(DateFormat.getTimeFormat(this).format(cal2.getTime()));
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_book_venue_details_ll_to_time_container:
                dateposition = 1;
                NumberPadTimePickerDialog pad = new NumberPadTimePickerDialog.Builder(BookVenueDetailsActivity.this, false)
                        .setHeaderColor(ContextCompat.getColor(this, R.color.header_color))
                        .build();
                pad.show(getSupportFragmentManager(), "show");
                break;
            case R.id.activity_book_venue_details_ll_from_time_container:
                dateposition = 0;
                NumberPadTimePickerDialog pad2 = new NumberPadTimePickerDialog.Builder(BookVenueDetailsActivity.this, false)
                        .setHeaderColor(ContextCompat.getColor(this, R.color.header_color))
                        .build();
                pad2.show(getSupportFragmentManager(), "show");
                break;
            case R.id.activity_book_venue_details_iv_back:
                finish();
                overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
                break;
            case R.id.activity_book_venue_details_btn_booking:
                mVenueBookingButton.setEnabled(false);
                if (CommonUtils.isNetworkAvailable(BookVenueDetailsActivity.this)) {
                    getVenueConfimation();
                } else {
                    Toast.makeText(BookVenueDetailsActivity.this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onDateSelected(DateTime dateSelected) {
        Log.d("HorizontalPicker", "Fecha seleccionada=" + dateSelected.toString());
        String[] items = dateSelected.toString().split("T");
        String dateformatold = items[0];
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date date = null;
        try {
            date = fmt.parse(dateformatold);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        matchdate = fmtOut.format(date);
    }

    public class MyPageAdapter extends PagerAdapter {
        Context mContext;
        LayoutInflater mLayoutInflater;
        List<VenueImageModel> venueImageModels;

        public MyPageAdapter(Context context, List<VenueImageModel> venueImageModels) {
            mContext = context;
            this.venueImageModels = venueImageModels;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            if (venueImageModels.size() > 0) {
                return venueImageModels.size();
            }
            return 1;
        }

        public Object instantiateItem(ViewGroup container, int position) {

            View itemView = mLayoutInflater.inflate(R.layout.banner_image_layout, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageview);
            if (venueImageModels.size() > 0) {
                VenueImageModel venueImageModel = (VenueImageModel) venueImageModels.get(position);
                if (!venueImageModel.getImage().equals("") && venueImageModel.getImage() != null) {
                    Glide.with(BookVenueDetailsActivity.this).load(venueImageModel.getImage()).placeholder(R.drawable.match_default).dontAnimate().into(imageView);
                } else {
                    Glide.with(BookVenueDetailsActivity.this).load(R.drawable.match_default).dontAnimate().into(imageView);
                }
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                container.addView(itemView);
            } else {
                container.addView(itemView);
            }
            return itemView;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }
}
