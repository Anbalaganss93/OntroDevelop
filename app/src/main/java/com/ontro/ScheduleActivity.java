package com.ontro;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ontro.dto.SquadInfo;
import com.ontro.fragments.BookVenueFragment;
import com.ontro.fragments.ScheduleTeamFormFragment;

import java.util.List;

public class ScheduleActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView back, image1, image2;
    private TextView mid_line, toolbar_text;
    private DisplayMetrics dm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        overridePendingTransition(R.anim.slide_in_right, R.anim.pause);
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        image1 = (ImageView) findViewById(R.id.image1);
        image2 = (ImageView) findViewById(R.id.image2);
        back = (ImageView) findViewById(R.id.activity_schedule_iv_back);
        mid_line = (TextView) findViewById(R.id.mid_line);
        toolbar_text = (TextView) findViewById(R.id.toolbar_text);

        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params2.width = (int) (dm.widthPixels * 0.15);
        params2.height = ((int) (dm.widthPixels * 0.15));
        params2.addRule(RelativeLayout.CENTER_VERTICAL);
        image1.setLayoutParams(params2);

        RelativeLayout.LayoutParams mid_line_param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mid_line_param.width = (int) (dm.widthPixels * 0.15);
        mid_line_param.height = ((int) (1));
        mid_line_param.addRule(RelativeLayout.RIGHT_OF, R.id.image1);
        mid_line_param.addRule(RelativeLayout.CENTER_VERTICAL);
        mid_line.setLayoutParams(mid_line_param);

        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params3.width = (int) (dm.widthPixels * 0.1);
        params3.height = ((int) (dm.widthPixels * 0.1));
        params3.addRule(RelativeLayout.CENTER_VERTICAL);
        params3.addRule(RelativeLayout.RIGHT_OF, R.id.mid_line);
        image2.setLayoutParams(params3);

        try {
            if (getIntent() != null) {
                Bundle bundle = getIntent().getExtras();
                String matchType = bundle.getString(Constants.BundleKeys.MATCH_TYPE);
                List<SquadInfo> squadInfos = (List<SquadInfo>) bundle.getSerializable(Constants.BundleKeys.OPPONENT_SQUADS);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.schedule_container, ScheduleTeamFormFragment.newInstance(matchType, squadInfos), "teamform");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_schedule_iv_back:
                ScheduleTeamFormFragment fragment = (ScheduleTeamFormFragment) getSupportFragmentManager().findFragmentByTag("teamform");
                if (fragment != null && fragment.isVisible()) {
                    finish();
                    overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
                }
                BookVenueFragment venue_fragment = (BookVenueFragment) getSupportFragmentManager().findFragmentByTag("bookvenue");
                if (venue_fragment != null && venue_fragment.isVisible()) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.schedule_container, new ScheduleTeamFormFragment(), "teamform");
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        ScheduleTeamFormFragment fragment = (ScheduleTeamFormFragment) getSupportFragmentManager().findFragmentByTag("teamform");
        if (fragment != null && fragment.isVisible()) {
//            Intent home_intent = new Intent(ScheduleActivity.this, TeamDetailActivity.class);
//            startActivity(home_intent);
            finish();
        }

        BookVenueFragment venue_fragment = (BookVenueFragment) getSupportFragmentManager().findFragmentByTag("bookvenue");
        if (venue_fragment != null && venue_fragment.isVisible()) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.schedule_container, new ScheduleTeamFormFragment(), "teamform");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
}