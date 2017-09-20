package com.ontro.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ontro.Constants;
import com.ontro.R;
import com.ontro.adapters.SecondTeamSquadAdapter;
import com.ontro.dto.SquadInfo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IDEOMIND02 on 23-08-2017.
 */

public class SecondTeamSquadFragment extends Fragment {

    public static Fragment newInstance(List<SquadInfo> squadInfos) {
        SecondTeamSquadFragment firstTeamSquadFragment = new SecondTeamSquadFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BundleKeys.MY_TEAM_SQUAD, (Serializable) squadInfos);
        firstTeamSquadFragment.setArguments(bundle);
        return firstTeamSquadFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second_team_squad, container, false);
        CardView mTeamSquadCardView = (CardView) view.findViewById(R.id.fragment_second_team_squad_card_view);
        RecyclerView mSquadList = (RecyclerView) view.findViewById(R.id.fragment_second_team_squad_rv);
        TextView mSquadEmptyView = (TextView) view.findViewById(R.id.fragment_second_team_squad_tv_empty);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mSquadList.setLayoutManager(gridLayoutManager);
        if(getArguments() != null) {
            List<SquadInfo> squadInfos = (List<SquadInfo>) getArguments().getSerializable(Constants.BundleKeys.MY_TEAM_SQUAD);
            if(squadInfos != null) {
                if(squadInfos.size() > 0) {
                    SecondTeamSquadAdapter secondTeamSquadAdapter = new SecondTeamSquadAdapter(getActivity(), squadInfos);
                    mSquadList.setAdapter(secondTeamSquadAdapter);
                } else {
                    mTeamSquadCardView.setVisibility(View.GONE);
                }
            }
        }
        return view;
    }
}
