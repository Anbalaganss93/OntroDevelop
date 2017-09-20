package com.ontro.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ontro.Constants;
import com.ontro.R;

public class MyMatchesHome extends Fragment implements View.OnClickListener {
    private View mRootView;
    private TextView mMatchRequestedView, mMatchScheduledView, mMatchCompletedView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_my_match_home, container, false);
        initView();
        if(getArguments() != null) {
            int matchStatusPosition = getArguments().getInt(Constants.BundleKeys.MATCH_STATUS_POSITION);
            if(matchStatusPosition == 1) {
                Fragment fragment = MyMatchFragment.newInstance(matchStatusPosition);
                mMatchRequestedView.setTextColor(ContextCompat.getColor(getContext(), R.color.button_bg_color));
                mMatchScheduledView.setTextColor(ContextCompat.getColor(getContext(), R.color.gray_100));
                mMatchCompletedView.setTextColor(ContextCompat.getColor(getContext(), R.color.gray_100));
                navigateToFragment(fragment);
            }else if(matchStatusPosition == 2){
                Fragment fragment = MyMatchFragment.newInstance(matchStatusPosition);
                mMatchRequestedView.setTextColor(ContextCompat.getColor(getContext(), R.color.gray_100));
                mMatchScheduledView.setTextColor(ContextCompat.getColor(getContext(), R.color.button_bg_color));
                mMatchCompletedView.setTextColor(ContextCompat.getColor(getContext(), R.color.gray_100));
                navigateToFragment(fragment);
            }
        } else {
            Fragment fragment = MyMatchFragment.newInstance(1);
            mMatchRequestedView.setTextColor(ContextCompat.getColor(getContext(), R.color.button_bg_color));
            mMatchScheduledView.setTextColor(ContextCompat.getColor(getContext(), R.color.gray_100));
            mMatchCompletedView.setTextColor(ContextCompat.getColor(getContext(), R.color.gray_100));
            navigateToFragment(fragment);
        }
        setListener();
        return mRootView;
    }

    private void setListener() {
        mMatchRequestedView.setOnClickListener(this);
        mMatchScheduledView.setOnClickListener(this);
        mMatchCompletedView.setOnClickListener(this);
    }

    private void initView() {
        mMatchRequestedView = (TextView) mRootView.findViewById(R.id.fragment_my_match_home_tv_requested);
        mMatchScheduledView = (TextView) mRootView.findViewById(R.id.fragment_my_match_home_tv_scheduled);
        mMatchCompletedView = (TextView) mRootView.findViewById(R.id.fragment_my_match_home_tv_completed);
    }

    @Override
    public void onClick(View v) {
        Fragment fragment;
        switch (v.getId()) {
            case R.id.fragment_my_match_home_tv_requested :
                fragment = MyMatchFragment.newInstance(1);
                mMatchRequestedView.setTextColor(ContextCompat.getColor(getContext(), R.color.button_bg_color));
                mMatchScheduledView.setTextColor(ContextCompat.getColor(getContext(), R.color.gray_100));
                mMatchCompletedView.setTextColor(ContextCompat.getColor(getContext(), R.color.gray_100));
                navigateToFragment(fragment);
                break;
            case R.id.fragment_my_match_home_tv_scheduled :
                fragment = MyMatchFragment.newInstance(2);
                mMatchRequestedView.setTextColor(ContextCompat.getColor(getContext(), R.color.gray_100));
                mMatchScheduledView.setTextColor(ContextCompat.getColor(getContext(), R.color.button_bg_color));
                mMatchCompletedView.setTextColor(ContextCompat.getColor(getContext(), R.color.gray_100));
                navigateToFragment(fragment);
                break;
            case R.id.fragment_my_match_home_tv_completed :
                fragment = MyMatchFragment.newInstance(3);
                mMatchRequestedView.setTextColor(ContextCompat.getColor(getContext(), R.color.gray_100));
                mMatchScheduledView.setTextColor(ContextCompat.getColor(getContext(), R.color.gray_100));
                mMatchCompletedView.setTextColor(ContextCompat.getColor(getContext(), R.color.button_bg_color));
                navigateToFragment(fragment);
                break;
        }
    }

    private void navigateToFragment(Fragment fragment) {
        if (fragment != null) {
            getFragmentManager().beginTransaction().replace(R.id.fragment_my_match_home_fl, fragment).commit();
        }
    }
}
