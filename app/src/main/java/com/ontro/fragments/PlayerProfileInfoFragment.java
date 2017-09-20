package com.ontro.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ontro.Constants;
import com.ontro.R;
import com.ontro.adapters.PlayerProfileInfoAdapter;
import com.ontro.dto.PlayerPersonalSport;
import com.ontro.dto.PlayerProfilePersonalModel;
import com.ontro.utils.CommonUtils;

import java.util.Calendar;
import java.util.List;

/**
 * Created by IDEOMIND02 on 28-08-2017.
 */

public class PlayerProfileInfoFragment extends Fragment {
    private View mRootView;
    private RecyclerView mPlayerSportView;
    private TextView mPlayerAgeView;
    private TextView mPlayerHeightView;

    public static Fragment newInstance(PlayerProfilePersonalModel playerProfilePersonalModel) {
        PlayerProfileInfoFragment profileInfoFragment = new PlayerProfileInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BundleKeys.PLAYER_PROFILE_INFO, playerProfilePersonalModel);
        profileInfoFragment.setArguments(bundle);
        return profileInfoFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_player_profile_info, container, false);
        mPlayerSportView = (RecyclerView) mRootView.findViewById(R.id.fragment_player_profile_info_rv);
        mPlayerAgeView = (TextView) mRootView.findViewById(R.id.fragment_player_profile_info_tv_age);
        mPlayerHeightView = (TextView) mRootView.findViewById(R.id.fragment_player_profile_info_tv_height);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mPlayerSportView.setLayoutManager(gridLayoutManager);
        if (getArguments() != null) {
            PlayerProfilePersonalModel profilePersonalModel
                    = (PlayerProfilePersonalModel) getArguments().getSerializable(Constants.BundleKeys.PLAYER_PROFILE_INFO);
            if (profilePersonalModel != null) {
                calculateAgeFromDob(CommonUtils.convertDateFormat(profilePersonalModel.getPlayerDob(), "yyyy-mm-dd", "dd-mm-yyyy"));
                mPlayerHeightView.setText(profilePersonalModel.getHeight() + Constants.DefaultText.CENTIMETER);
                List<PlayerPersonalSport> personalSports = profilePersonalModel.getSports();
                if (personalSports != null) {
                    PlayerProfileInfoAdapter profileInfoAdapter = new PlayerProfileInfoAdapter(getActivity(), personalSports);
                    mPlayerSportView.setAdapter(profileInfoAdapter);
                }
            }
        }
        return mRootView;
    }

    private void calculateAgeFromDob(String playerDob) {
        String dateOfBirth = playerDob.replace("-", "");
        int year = Integer.valueOf(dateOfBirth.substring(4, 8));
        int month = Integer.valueOf(dateOfBirth.substring(2, 4));
        int day = Integer.valueOf(dateOfBirth.substring(0, 2));
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob.set(year, month, day);
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        mPlayerAgeView.setText(age + Constants.DefaultText.YEARS);
    }
}
