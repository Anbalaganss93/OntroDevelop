package com.ontro;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.ontro.adapters.FilterSportsAdapter;
import com.ontro.dto.SportModel;
import com.ontro.utils.CommonUtils;
import com.ontro.utils.MyGridView;
import com.ontro.utils.PreferenceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FilterActivity extends AppCompatActivity implements View.OnClickListener {
    public ArrayList<SportModel> arrayList = new ArrayList<>();
    String temp = "", locationid = "";
    private ScrollView scrollView;
    private FilterSportsAdapter adapter;
    private TextView location_search;
    private RadioButton sortby_batch5, sortby_batch4, sortby_batch3, sortby_batch2, sortby_batch1;
    private int[] sport_icon = new int[]{R.drawable.ic_football_white, R.drawable.ic_cricket_white, R.drawable.ic_tennis_white, R.drawable.ic_basketball_white, R.drawable.ic_badminton_white, R.drawable.ic_volley_white, R.drawable.ic_carrom_white};
    private String[] sportName = new String[]{"Football", "Cricket", "Tennis", "Basketball", "Badminton", "Volleyball", "Carrom"};
    private String[] sportId = new String[]{"5", "4", "6", "2", "1", "7", "3"};
    private PreferenceHelper preferenceHelper;
    private MixpanelAPI mMixpanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        overridePendingTransition(R.anim.slide_in_right, R.anim.pause);
        mMixpanel = MixpanelAPI.getInstance(this, getResources().getString(R.string.mixpanel_token));
        Typeface typeface_regular = Typeface.createFromAsset(getAssets(), "fonts/roboto_regular.ttf");
        preferenceHelper = new PreferenceHelper(FilterActivity.this, Constants.APP_NAME, 0);

        preferenceHelper.save("Fromfilter", "false");
        preferenceHelper.save("searchvisible", "true");
        MyGridView sport_gridview = (MyGridView) findViewById(R.id.sport_gridview);
        LinearLayout outtouch = (LinearLayout) findViewById(R.id.outtouch);
        location_search = (TextView) findViewById(R.id.activity_explore_player_list_et_location_search);
        sortby_batch5 = (RadioButton) findViewById(R.id.sortby_batch5);
        sortby_batch4 = (RadioButton) findViewById(R.id.sortby_batch4);
        sortby_batch3 = (RadioButton) findViewById(R.id.sortby_batch3);
        sortby_batch2 = (RadioButton) findViewById(R.id.sortby_batch2);
        sortby_batch1 = (RadioButton) findViewById(R.id.sortby_batch1);
        ImageView back = (ImageView) findViewById(R.id.activity_explore_player_list_iv_back);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        Button apply = (Button) findViewById(R.id.apply);
        TextView reset = (TextView) findViewById(R.id.reset);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // only for LOLLIPOP and newer versions
            apply.setBackground(ContextCompat.getDrawable(this, R.drawable.button_bg));
        } else {
            apply.setBackground(ContextCompat.getDrawable(this, R.drawable.button_bg_normal));
        }

        if (preferenceHelper.contains("user_location_name")) {
            String locationname = preferenceHelper.getString("user_location_name", "");
            location_search.setText(locationname);
        }

        String sportids = preferenceHelper.getString("typeofsport", "");
        String[] id = new String[0];

        if (sportids.length() != 0) {
            id = sportids.split(",");
        }

        for (int i = 0; i < sportName.length; i++) {
            SportModel m = new SportModel();
            m.setSportimage(sport_icon[i]);
            m.setSportname(sportName[i]);
            m.setSportid(sportId[i]);
            m.setSelected(0);
            arrayList.add(m);
        }

        if (sportids.length() != 0) {
            for (String anId : id) {
                for (int k = 0; k < sportName.length; k++) {
                    SportModel model = (SportModel) arrayList.get(k);
                    if (anId.equals(model.getSportid())) {
                        model.setSelected(1);
                    }
                }
            }
        }

        adapter = new FilterSportsAdapter(FilterActivity.this, arrayList);
        sport_gridview.setAdapter(adapter);

        sport_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SportModel m = (SportModel) arrayList.get(i);
                if (m.getSelected() == 0) {
                    m.setSelected(1);
                } else {
                    m.setSelected(0);
                }
                adapter.notifyDataSetChanged();
            }
        });

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollTo(0, 0);
            }
        });

        apply.setTypeface(typeface_regular);
        back.setOnClickListener(this);
        reset.setOnClickListener(this);
        outtouch.setOnClickListener(this);
        location_search.setOnClickListener(this);
        apply.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_explore_player_list_iv_back:
                if (temp.length() == 0) {
                    preferenceHelper.save("Fromfilter", "true");
                } else {
                    preferenceHelper.save("Fromfilter", "false");
                }
                finish();
                overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
                break;
            case R.id.apply:
                filterby();
                if (validation()) {
                    try {
                        JSONObject eventJsonObject = new JSONObject();
                        eventJsonObject.put("UserId", preferenceHelper.getString("user_id", ""));
                        eventJsonObject.put("UserName",  preferenceHelper.getString("user_name", ""));
                        eventJsonObject.put("UserEmail", preferenceHelper.getString("user_email", ""));
                        mMixpanel.track("ExploreFilterOption", eventJsonObject);
                    } catch (JSONException e) {
                        Log.e("Ontro", "Unable to add properties to JSONObject", e);
                    }
                    finish();
                } else {
                    Toast.makeText(FilterActivity.this, "Select any one of the filter type", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.reset:
                reset_alert();
                break;
            case R.id.outtouch:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                break;
            case R.id.activity_explore_player_list_et_location_search:
                CommonUtils.locationdialog(FilterActivity.this, location_search, 3);
                break;
        }
    }

    public void filterby() {
        temp = "";
        for (int i = 0; i < arrayList.size(); i++) {
            SportModel m = (SportModel) arrayList.get(i);
            if (m.getSelected() == 1) {
                if (temp.length() == 0) {
                    temp = m.getSportid();
                } else {
                    temp = temp + "," + m.getSportid();
                }
            }
        }

        locationid = CommonUtils.locationid;
        preferenceHelper.save("typeofsport", temp);
        preferenceHelper.save("location", CommonUtils.locationid);
        preferenceHelper.save("user_location_name", location_search.getText().toString().trim());
    }

    private boolean validation() {
        if (temp.length() == 0 && locationid.length() == 0) {
            return false;
        }
        return true;
    }

    public void reset_filter() {
        preferenceHelper.save("typeofsport", "");
        preferenceHelper.save("location", "");
        preferenceHelper.save("user_location_name", "");
        preferenceHelper.save("Fromfilter", "true");
        CommonUtils.locationid = "";
        location_search.setText("");
        for (int i = 0; i < sportName.length; i++) {
            SportModel m = (SportModel) arrayList.get(i);
            m.setSelected(0);
            adapter.notifyDataSetChanged();
        }
        sortby_batch5.setChecked(false);
        sortby_batch4.setChecked(false);
        sortby_batch3.setChecked(false);
        sortby_batch2.setChecked(false);
        sortby_batch1.setChecked(false);
    }

    private void filter_alert() {
        final Dialog filter_apply = new Dialog(FilterActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        filter_apply.requestWindowFeature(Window.FEATURE_NO_TITLE);
        filter_apply.setContentView(R.layout.logout_layout);
        LinearLayout logout_container = (LinearLayout) filter_apply.findViewById(R.id.logout_container);
        TextView title = (TextView) filter_apply.findViewById(R.id.title);
        title.setText(R.string.filteralert);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = (int) (metrics.widthPixels * 0.8);
        params.gravity = Gravity.CENTER;
        logout_container.setLayoutParams(params);

        TextView yes = (TextView) filter_apply.findViewById(R.id.yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterby();
                finish();
            }
        });
        TextView no = (TextView) filter_apply.findViewById(R.id.no);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filter_apply.isShowing()) {
                    filter_apply.dismiss();
                }
                finish();
            }
        });
        filter_apply.show();
    }

    private void reset_alert() {
        final Dialog filter_apply = new Dialog(FilterActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        filter_apply.requestWindowFeature(Window.FEATURE_NO_TITLE);
        filter_apply.setContentView(R.layout.logout_layout);
        LinearLayout logout_container = (LinearLayout) filter_apply.findViewById(R.id.logout_container);
        CardView card_view = (CardView) filter_apply.findViewById(R.id.card_view);
        TextView title = (TextView) filter_apply.findViewById(R.id.title);
        title.setText(R.string.resetalert);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = (int) (metrics.widthPixels * 0.8);
        params.gravity = Gravity.CENTER;
        card_view.setLayoutParams(params);

        TextView yes = (TextView) filter_apply.findViewById(R.id.yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset_filter();
                finish();
            }
        });

        TextView no = (TextView) filter_apply.findViewById(R.id.no);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filter_apply.isShowing()) {
                    filter_apply.dismiss();
                }
//                finish();
            }
        });

        filter_apply.show();
    }

    @Override
    public void onBackPressed() {
        if (temp.length() == 0) {
            preferenceHelper.save("Fromfilter", "true");
        } else {
            preferenceHelper.save("Fromfilter", "false");
        }
        finish();
        overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
    }
}
