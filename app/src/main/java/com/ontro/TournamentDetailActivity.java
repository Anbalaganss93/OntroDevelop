package com.ontro;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.ontro.dto.DateAndVenue;
import com.ontro.dto.Overview;
import com.ontro.dto.RulesAndRegulations;
import com.ontro.dto.TournamentDetailResponse;
import com.ontro.dto.TournamentPrice;
import com.ontro.dto.TournamentVenueAddress;
import com.ontro.fragments.TournamentDateAndVenueDetailFragment;
import com.ontro.fragments.TournamentEntryFeesDetailFragment;
import com.ontro.fragments.TournamentOverViewFragment;
import com.ontro.fragments.TournamentRulesAndRegulationFragment;
import com.ontro.rest.ApiClient;
import com.ontro.rest.ApiInterface;
import com.ontro.utils.CommonUtils;
import com.ontro.utils.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TournamentDetailActivity extends AppCompatActivity implements View.OnClickListener, AppBarLayout.OnOffsetChangedListener, TabLayout.OnTabSelectedListener {
    private Button mJoinButton;
    private ImageView mTournamentDetailImageView, mContactCallImageView, mSportTypeImageView;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private AppBarLayout mAppBarLayout;
    private Dialog mProgressBar;
    private int mFormId, mTournamentId;
    private ViewPager mTournamentViewPager;
    private TabLayout mTournamentDetailTabs;
    private TournamentDetailResponse tournamentDetailResponse;
    private PreferenceHelper preferenceHelper;
    private String mTournamentName;
    private String mMobileNumber, mPersonPhoneNumber1 = "", mPersonPhoneNumber2 = "";
    private String tournamentAdPicture = "";
    private RelativeLayout mCallFooterLayout;
    private LinearLayout mCallLayout, mRegisterFooterLayout;
    private TextView mCallTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_detail);
        overridePendingTransition(R.anim.slide_in_right, R.anim.pause);
        initView();
        initializeProgressBar();
        setButtonBackground();
        setToolbar();
        setTypeFace();
        setValues();
        setListener();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.activity_tournament_detail_toolbar);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.activity_tournament_detail_layout_collapsing_toolbar);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.activity_tournament_detail_layout_app_bar);
        mTournamentDetailImageView = (ImageView) findViewById(R.id.activity_tournament_detail_iv_logo);
        mTournamentDetailTabs = (TabLayout) findViewById(R.id.activity_tournament_detail_tl);
        mTournamentViewPager = (ViewPager) findViewById(R.id.activity_tournament_detail_view_pager);
        mContactCallImageView = (ImageView) findViewById(R.id.activity_tournament_detail_iv_call);
        mSportTypeImageView = (ImageView) findViewById(R.id.activity_tournament_detail_iv_sport_logo);
        mJoinButton = (Button) findViewById(R.id.activity_tournament_detail_btn_join);
        mRegisterFooterLayout = (LinearLayout) findViewById(R.id.activity_tournament_detail_ll_footer);
        mCallFooterLayout = (RelativeLayout) findViewById(R.id.activity_tournament_detail_rl_call_footer);
        mCallLayout = (LinearLayout) findViewById(R.id.activity_tournament_detail_ll_call);
        mCallTextView = (TextView) findViewById(R.id.activity_tournament_detail_btn_call);
    }

    private void initializeProgressBar() {
        mProgressBar = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        mProgressBar.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressBar.setContentView(R.layout.progressdialog_layout);
    }

    private void setButtonBackground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // only for LOLLIPOP and newer versions
            mJoinButton.setBackground(ContextCompat.getDrawable(this, R.drawable.button_bg));
            mCallLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.button_bg));
        } else {
            mJoinButton.setBackground(ContextCompat.getDrawable(this, R.drawable.button_bg_normal));
            mCallLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.button_bg_normal));
        }
    }

    private void setToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            mToolbar.setTitle("");
            mToolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        }
    }

    private void setTypeFace() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/roboto_regular.ttf");
        mJoinButton.setTypeface(typeface);
        ViewGroup vg = (ViewGroup) mTournamentDetailTabs.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(typeface);
                }
            }
        }
    }

    private void setUpViewPagerAdapter(TournamentDetailResponse tournamentDetailResponse) {
        try {
            TournamentDetailViewPagerAdapter viewPagerAdapter = new TournamentDetailViewPagerAdapter(this.getSupportFragmentManager(), tournamentDetailResponse);
            mTournamentViewPager.setAdapter(viewPagerAdapter);
            mTournamentDetailTabs.setupWithViewPager(mTournamentViewPager);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void setValues() {
        Intent intent = getIntent();
        if (intent != null) {
            int tournamentId = intent.getIntExtra(Constants.BundleKeys.TOURNAMENT_ID, 0);
            if (CommonUtils.isNetworkAvailable(this)) {
                mProgressBar.show();
                if (tournamentId != 0) {
                    mTournamentId = tournamentId;
                    getTournamentsDetail(tournamentId);
                }
            } else {
                Toast.makeText(this, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getTournamentsDetail(final int tournamentId) {
        preferenceHelper = new PreferenceHelper(this, Constants.APP_NAME, 0);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        String authToken = "Bearer " + preferenceHelper.getString("user_token", "");
        Call<ResponseBody> call = apiInterface.getTournamentDetail(authToken, tournamentId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                        JSONObject jsonObject = new JSONObject(data);
                        if (!jsonObject.getString("data").equals("null")) {
                            JSONObject json = new JSONObject(jsonObject.getString("data"));
                            tournamentDetailResponse = new TournamentDetailResponse();
                            tournamentDetailResponse.setTournamentName(json.getString("tournament_name"));
                            mTournamentName = tournamentDetailResponse.getTournamentName();
                            tournamentDetailResponse.setSportsType(json.getString("sports_type"));
                            setSportTypeIcon(tournamentDetailResponse.getSportsType());
                            tournamentDetailResponse.setLogo(json.getString("logo"));
                            tournamentAdPicture = json.getString("advert_pic");
                            tournamentDetailResponse.setAdvertPic(tournamentAdPicture);
                            if (tournamentAdPicture != null && !tournamentAdPicture.isEmpty()) {
                                Glide.with(getApplicationContext()).load(tournamentAdPicture).placeholder(R.drawable.match_default).dontAnimate().into(mTournamentDetailImageView);
                            } else {
                                Glide.with(getApplicationContext()).load(R.drawable.match_default).dontAnimate().into(mTournamentDetailImageView);
                            }
                            mFormId = json.getInt("form_id");
                            tournamentDetailResponse.setFormId(mFormId);
                            tournamentDetailResponse.setPerson1(json.getString("person1"));
                            mPersonPhoneNumber1 = tournamentDetailResponse.getPerson1();
                            tournamentDetailResponse.setPerson2(json.getString("person2"));
                            mPersonPhoneNumber2 = tournamentDetailResponse.getPerson2();
                            int paymentStatus = json.getInt("payment_type");
                            if(paymentStatus != 0) {
                                if(paymentStatus == 3) {
                                    mCallFooterLayout.setVisibility(View.VISIBLE);
                                    mRegisterFooterLayout.setVisibility(View.GONE);
                                } else {
                                    mCallFooterLayout.setVisibility(View.GONE);
                                    mRegisterFooterLayout.setVisibility(View.VISIBLE);
                                }
                            }

                            JSONObject overViewObject = new JSONObject(json.getString("overview"));
                            Overview overview = new Overview();
                            overview.setOrganizationName(overViewObject.getString("organization_name"));
                            overview.setDescription(overViewObject.getString("description"));
                            if(overViewObject.has("fb_link")) {
                                overview.setFbLink(overViewObject.getString("fb_link"));
                            }
                            if(overViewObject.has("twitter_link")) {
                                overview.setTwitterLink(overViewObject.getString("twitter_link"));
                            }
                            if(overViewObject.has("web_url")) {
                                overview.setWebUrl(overViewObject.getString("web_url"));
                            }
                            if(overViewObject.has("fixtures")) {
                                overview.setFixtures(overViewObject.getString("fixtures"));
                            }
                            tournamentDetailResponse.setOverview(overview);

                            JSONObject rulesObject = new JSONObject(json.getString("rulesAndRegulations"));
                            RulesAndRegulations rulesAndRegulations = new RulesAndRegulations();
                            rulesAndRegulations.setTournamentRules(rulesObject.getString("tournament_rules"));
                            tournamentDetailResponse.setRulesAndRegulations(rulesAndRegulations);

                            JSONObject dateAndVenueObject = new JSONObject(json.getString("dateAndVenue"));
                            DateAndVenue dateAndVenue = new DateAndVenue();
                            dateAndVenue.setStartDate(dateAndVenueObject.getString("start_date"));
                            dateAndVenue.setEndDate(dateAndVenueObject.getString("end_date"));
                            dateAndVenue.setFromTime(dateAndVenueObject.getString("from_time"));
                            dateAndVenue.setToTime(dateAndVenueObject.getString("to_time"));
                            JSONArray addressArray = new JSONArray(dateAndVenueObject.getString("address"));
                            List<TournamentVenueAddress> tournamentVenueAddresses = new ArrayList<TournamentVenueAddress>();
                            for (int i = 0; i < addressArray.length(); i++) {
                                TournamentVenueAddress tournamentVenueAddress = new TournamentVenueAddress();
                                tournamentVenueAddress.setGroundName(addressArray.getJSONObject(i).getString("ground_name"));
                                tournamentVenueAddress.setAddress(addressArray.getJSONObject(i).getString("address"));
                                tournamentVenueAddress.setLatitude(addressArray.getJSONObject(i).getString("latitude"));
                                tournamentVenueAddress.setLongitude(addressArray.getJSONObject(i).getString("longitude"));
                                tournamentVenueAddresses.add(tournamentVenueAddress);
                            }
                            dateAndVenue.setAddress(tournamentVenueAddresses);
                            tournamentDetailResponse.setDateAndVenue(dateAndVenue);

                            JSONArray priceArray = new JSONArray(json.getString("price"));
                            List<TournamentPrice> tournamentPrices = new ArrayList<TournamentPrice>();
                            for (int i = 0; i < priceArray.length(); i++) {
                                TournamentPrice tournamentPrice = new TournamentPrice();
                                tournamentPrice.setCategoryName(priceArray.getJSONObject(i).getString("category_name"));
                                tournamentPrice.setPrice(priceArray.getJSONObject(i).getString("price"));
                                tournamentPrices.add(tournamentPrice);
                            }
                            tournamentDetailResponse.setPrice(tournamentPrices);
                            setUpViewPagerAdapter(tournamentDetailResponse);
                        } else {
                            mJoinButton.setVisibility(View.GONE);
                        }
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                String error = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(error);
                                String msg = jsonObject.getString("message");
                                String code = jsonObject.getString("code");
                                if (!code.equals("500")) {
                                    Toast.makeText(TournamentDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(TournamentDetailActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                String error = response.message();
                                Toast.makeText(TournamentDetailActivity.this, R.string.error500, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String error = response.message();
                            Toast.makeText(TournamentDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                if (mProgressBar.isShowing()) {
                    mProgressBar.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (mProgressBar.isShowing()) {
                    mProgressBar.dismiss();
                }
                try {
                    if (t instanceof SocketTimeoutException) {
                        Toast.makeText(TournamentDetailActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(TournamentDetailActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void setSportTypeIcon(String sportsType) {
        mSportTypeImageView.bringToFront();
        switch (sportsType) {
            case "1":
                mSportTypeImageView.setBackgroundResource(R.drawable.ic_badminton_white);
                break;
            case "2":
                mSportTypeImageView.setBackgroundResource(R.drawable.ic_basketball_white);
                break;
            case "3":
                mSportTypeImageView.setBackgroundResource(R.drawable.ic_carrom_white);
                break;
            case "4":
                mSportTypeImageView.setBackgroundResource(R.drawable.ic_cricket_white);
                break;
            case "5":
                mSportTypeImageView.setBackgroundResource(R.drawable.ic_football_white);
                break;
            case "6":
                mSportTypeImageView.setBackgroundResource(R.drawable.ic_tennis_white);
                break;
            case "7":
                mSportTypeImageView.setBackgroundResource(R.drawable.ic_volley_white);
                break;
            default:
                break;
        }

    }

    private void setListener() {
        mCallLayout.setOnClickListener(this);
        mJoinButton.setOnClickListener(this);
        mTournamentDetailTabs.addOnTabSelectedListener(this);
        mAppBarLayout.addOnOffsetChangedListener(this);
        mContactCallImageView.setOnClickListener(this);
        mTournamentDetailImageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_tournament_detail_btn_join:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // only for LOLLIPOP and newer versions
                    mJoinButton.setBackground(ContextCompat.getDrawable(this, R.drawable.button_bg));
                } else {
                    mJoinButton.setBackground(ContextCompat.getDrawable(this, R.drawable.button_bg_normal));
                }
                if (mTournamentId != 0) {
                    Intent webViewIntent = new Intent(TournamentDetailActivity.this, WebViewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.BundleKeys.TOURNAMENT_ID, mTournamentId);
                    bundle.putInt(Constants.BundleKeys.FORM_ID, mFormId);
                    bundle.putString(Constants.BundleKeys.TOURNAMENT_NAME, mTournamentName);
                    webViewIntent.putExtras(bundle);
                    startActivity(webViewIntent);
                }
                break;
            case R.id.activity_tournament_detail_ll_call :
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // only for LOLLIPOP and newer versions
                    mCallLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.button_bg));
                } else {
                    mCallLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.button_bg_normal));
                }
                showCallDetailDialog();
                break;
            case R.id.activity_tournament_detail_iv_call:
                showCallDetailDialog();
                break;
            case R.id.activity_tournament_detail_iv_logo:
                Intent intent = new Intent(TournamentDetailActivity.this, VenueImageView.class);
                intent.putExtra("image", tournamentAdPicture);
                startActivity(intent);
                break;
        }
    }

    private void showCallDetailDialog() {
        if ((null != mPersonPhoneNumber1 && !TextUtils.isEmpty(mPersonPhoneNumber1)) && (null != mPersonPhoneNumber2 && !TextUtils.isEmpty(mPersonPhoneNumber2))) {
            final MixpanelAPI mMixpanel = MixpanelAPI.getInstance(TournamentDetailActivity.this, getResources().getString(R.string.mixpanel_token));
            final AlertDialog.Builder builder = new AlertDialog.Builder(TournamentDetailActivity.this);
            View callView = TournamentDetailActivity.this.getLayoutInflater().inflate(R.layout.dialog_call_layout, null);
            builder.setView(callView);
            final AlertDialog alertDialog = builder.create();
            LinearLayout call_container = (LinearLayout) callView.findViewById(R.id.call_container);
            TextView mNumber1 = (TextView) callView.findViewById(R.id.number1);
            TextView mNumber2 = (TextView) callView.findViewById(R.id.number2);
            if (null != mPersonPhoneNumber1 && !TextUtils.isEmpty(mPersonPhoneNumber1)) {
                mNumber1.setText(mPersonPhoneNumber1);
            } else {
                mNumber1.setVisibility(View.GONE);
            }
            if (null != mPersonPhoneNumber2 && !TextUtils.isEmpty(mPersonPhoneNumber2)) {
                mNumber2.setText(mPersonPhoneNumber2);
            } else {
                mNumber2.setVisibility(View.GONE);
            }
            mNumber1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        JSONObject eventJsonObject = new JSONObject();
                        eventJsonObject.put("UserId", preferenceHelper.getString("user_id", ""));
                        eventJsonObject.put("UserName",  preferenceHelper.getString("user_name", ""));
                        eventJsonObject.put("UserEmail", preferenceHelper.getString("user_email", ""));
                        mMixpanel.track("TournamentCall", eventJsonObject);
                    } catch (JSONException e) {
                        Log.e("Ontro", "Unable to add properties to JSONObject", e);
                    }
                    makeCall(mPersonPhoneNumber1);
                    alertDialog.dismiss();
                }
            });
            mNumber2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        JSONObject eventJsonObject = new JSONObject();
                        eventJsonObject.put("UserId", preferenceHelper.getString("user_id", ""));
                        eventJsonObject.put("UserName",  preferenceHelper.getString("user_name", ""));
                        eventJsonObject.put("UserEmail", preferenceHelper.getString("user_email", ""));
                        mMixpanel.track("TournamentCall", eventJsonObject);
                    } catch (JSONException e) {
                        Log.e("Ontro", "Unable to add properties to JSONObject", e);
                    }
                    makeCall(mPersonPhoneNumber2);
                    alertDialog.dismiss();
                }
            });

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.width = (int) (metrics.widthPixels * 0.8);
            params.gravity = Gravity.CENTER;
            call_container.setLayoutParams(params);
            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(true);
            alertDialog.show();
        } else {
            Toast.makeText(TournamentDetailActivity.this, Constants.Messages.NO_NUMBER_FOUND, Toast.LENGTH_SHORT).show();
        }
    }

    private void makeCall(String phoneNumber) {
        if (phoneNumber.contains("-")) {
            mMobileNumber = phoneNumber.substring(phoneNumber.indexOf("-") + 1).trim();
        } else {
            mMobileNumber = phoneNumber;
        }

        askPermission();
    }

    private void askPermission() {
        if (ContextCompat.checkSelfPermission(TournamentDetailActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TournamentDetailActivity.this, new String[]{android.Manifest.permission.CALL_PHONE}, Constants.CommonKeys.REQUEST_CODE_CALL_PHONE);
        } else {
            String string = !mMobileNumber.matches("[7-9][0-9]{9}") ? Intent.ACTION_DIAL : Intent.ACTION_CALL;
            startActivity(new Intent(string, Uri.parse(Constants.DefaultText.TEL_KEYWORD + mMobileNumber)));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            String string = !mMobileNumber.matches("[7-9][0-9]{9}") ? Intent.ACTION_DIAL : Intent.ACTION_CALL;
            startActivity(new Intent(string, Uri.parse(Constants.DefaultText.TEL_KEYWORD + mMobileNumber)));
        } else {
            Toast.makeText(TournamentDetailActivity.this, Constants.CommonFields.CALL_PERMISSION_DENIED, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
            mCollapsingToolbarLayout.setTitle(mTournamentName);
        } else if (verticalOffset == 0) {
            mCollapsingToolbarLayout.setTitle(mTournamentName);
        } else {
            mCollapsingToolbarLayout.setTitle(mTournamentName);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mTournamentViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
    }

    private class TournamentDetailViewPagerAdapter extends FragmentPagerAdapter {

        public ArrayList<Fragment.SavedState> mSavedState = new ArrayList<Fragment.SavedState>();
        private String[] mTournamentDetailTabTitles = getResources().getStringArray(R.array.tournament_tabs);
        private TournamentDetailResponse mTournamentDetailResponse;
        private FragmentTransaction mCurTransaction;
        private FragmentManager mFragmentManager;
        private Fragment mCurrentPrimaryItem = null;

        public TournamentDetailViewPagerAdapter(FragmentManager supportFragmentManager, TournamentDetailResponse tournamentDetailResponse) {
            super(supportFragmentManager);
          /*  this.mFragments = fragments;*/
            mFragmentManager = supportFragmentManager;
            mTournamentDetailResponse = tournamentDetailResponse;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = TournamentOverViewFragment.newInstance(mTournamentDetailResponse.getOverview());
                    return fragment;
                case 1:
                    fragment = TournamentDateAndVenueDetailFragment.newInstance(mTournamentDetailResponse.getDateAndVenue());
                    return fragment;
                case 2:
                    fragment = TournamentEntryFeesDetailFragment.newInstance(mTournamentDetailResponse.getPrice());
                    return fragment;
                case 3:
                    fragment = TournamentRulesAndRegulationFragment.newInstance(mTournamentDetailResponse.getRulesAndRegulations());
                    return fragment;
                default:
                    fragment = null;
                    return fragment;
            }
        }

        @Override
        public int getCount() {
            return mTournamentDetailTabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTournamentDetailTabTitles[position];
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
           /* if (mFragments.size() > position) {
                Fragment f = mFragments.get(position);
                if (f != null) {
                    return f;
                }
            }*/
            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction();
            }
            Fragment fragment = getItem(position);
            if (mSavedState.size() > position) {
                Fragment.SavedState fss = mSavedState.get(position);
                if (fss != null) {
                    try {
                        fragment.setInitialSavedState(fss);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
           /* while (mFragments.size() <= position) {
                mFragments.addTeamScore(null);
            }*/
            fragment.setMenuVisibility(false);
           /* mFragments.set(position, fragment);*/
            mCurTransaction.add(container.getId(), fragment);

            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            Fragment fragment = (Fragment)object;

            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction();
            }
            mCurTransaction.remove(fragment);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            if (mCurTransaction != null) {
                mCurTransaction.commitAllowingStateLoss();
                mCurTransaction = null;
                mFragmentManager.executePendingTransactions();
            }
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            Fragment fragment = (Fragment) object;
            if (fragment != mCurrentPrimaryItem) {
                if (mCurrentPrimaryItem != null) {
                    mCurrentPrimaryItem.setMenuVisibility(false);
                }
                if (fragment != null) {
                    fragment.setMenuVisibility(true);
                }
                mCurrentPrimaryItem = fragment;
            }
        }


    }
}


