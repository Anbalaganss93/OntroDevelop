package com.ontro;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsFeedDetailActivity extends AppCompatActivity {
    Toolbar mToolbar;
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    AppBarLayout mAppBarLayout;
    ImageView mNewsFeedDetailImageView;
    TextView mNewsFeedTitleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed_detail);
        overridePendingTransition(R.anim.slide_in_right, R.anim.pause);
        initView();
        setToolbar();
        if(getIntent() != null) {
            mNewsFeedTitleView.setText(getIntent().getStringExtra("content"));
            mNewsFeedDetailImageView.setVisibility(View.GONE);
            mAppBarLayout.setExpanded(false);
        }
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.activity_news_feed_detail_toolbar);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.activity_news_feed_detail_layout_collapsing_toolbar);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.activity_news_feed_detail_layout_app_bar);
        mNewsFeedDetailImageView = (ImageView) findViewById(R.id.activity_news_feed_detail_iv_logo);
        mNewsFeedTitleView = (TextView) findViewById(R.id.activity_news_feed_detail_tv_title);
    }

    private void setToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
