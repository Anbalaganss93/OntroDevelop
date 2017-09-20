package com.ontro;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ontro.fragments.MatchRequestInfoFragment;
import com.ontro.fragments.TeamSquadFragment;

public class MatchRequestActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, AppBarLayout.OnOffsetChangedListener {
    private Toolbar mToolbar;
    private TextView mPlayerOrTeamLocation;
    private TabLayout mPlayerOrTeamDetailTab;
    private ViewPager mPlayerOrTeamViewPager;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private AppBarLayout mAppBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_request);
        overridePendingTransition(R.anim.slide_in_right, R.anim.pause);
        initView();
        addBackNavigation();
        setUpViewPagerAdapter();
        integrateViewPagerWithTabLayout();
        setListener();
        setTypeFace();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.activity_match_request_toolbar);
        mPlayerOrTeamLocation = (TextView) findViewById(R.id.activity_match_request_tv_player_or_team_location);
        mPlayerOrTeamDetailTab = (TabLayout) findViewById(R.id.activity_match_request_tl);
        mPlayerOrTeamViewPager = (ViewPager) findViewById(R.id.activity_match_request_view_pager);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.activity_match_request_collapsing_toolbar);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.activity_match_request_appbar_layout);
    }

    private void addBackNavigation() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            mToolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        }
    }

    private void setUpViewPagerAdapter() {
        try {
            if(getIntent() != null) {
                int tabPosition =  getIntent().getExtras().getInt(Constants.BundleKeys.MATCH_STATUS_POSITION);
                MatchRequestViewAdapter viewPagerAdapter = new MatchRequestViewAdapter(getSupportFragmentManager(), tabPosition);
                mPlayerOrTeamViewPager.setAdapter(viewPagerAdapter);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void setListener() {
        mPlayerOrTeamDetailTab.addOnTabSelectedListener(this);
        mAppBarLayout.addOnOffsetChangedListener(this);
    }

    private void integrateViewPagerWithTabLayout() {
        mPlayerOrTeamDetailTab.setupWithViewPager(mPlayerOrTeamViewPager);
    }

    private void setTypeFace() {
        Typeface typefaceRegular = Typeface.createFromAsset(getAssets(), "fonts/roboto_regular.ttf");
        mCollapsingToolbarLayout.setCollapsedTitleTypeface(typefaceRegular);
        Typeface typefaceBold = Typeface.createFromAsset(getAssets(), "fonts/roboto_bold.ttf");
        mCollapsingToolbarLayout.setExpandedTitleTypeface(typefaceBold);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mPlayerOrTeamViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
            mPlayerOrTeamLocation.setVisibility(View.GONE);
        } else if (verticalOffset == 0) {
            mPlayerOrTeamLocation.setVisibility(View.VISIBLE);
        } else {
            mPlayerOrTeamLocation.setVisibility(View.GONE);
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
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
    }

    private class MatchRequestViewAdapter extends FragmentStatePagerAdapter {
        private String[] matchDetailTabTitles = new String[]{"Info", "Squad"};
        private String[] matchCompletedTabTitles = new String[]{"overview", "stats"};
        private int mMatchStatusPosition;

        public MatchRequestViewAdapter(FragmentManager supportFragmentManager, int tabPosition) {
            super(supportFragmentManager);
            mMatchStatusPosition = tabPosition;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    if (mMatchStatusPosition != 3) {
                        fragment = MatchRequestInfoFragment.newInstance(mMatchStatusPosition);
                    }
                    break;
                case 1:
                    if (mMatchStatusPosition != 3) {
                        fragment = TeamSquadFragment.newInstance(null);
                    }
                    break;
                default:
                    fragment = null;
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            CharSequence pageTitle = "";
            switch (position) {
                case 0:
                    if(mMatchStatusPosition != 3) {
                        pageTitle = matchDetailTabTitles[0];
                    } else {
                        pageTitle = matchCompletedTabTitles[0];
                    }
                    break;
                case 1:
                    if(mMatchStatusPosition != 3) {
                        pageTitle = matchDetailTabTitles[1];
                    } else {
                        pageTitle = matchCompletedTabTitles[1];
                    }
                    break;
            }
            return pageTitle;
        }
    }

}