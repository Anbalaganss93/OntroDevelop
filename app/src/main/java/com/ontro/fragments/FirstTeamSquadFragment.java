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
import com.ontro.adapters.FirstTeamSquadAdapter;
import com.ontro.dto.MySquadInfo;
import com.ontro.dto.SquadInfo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IDEOMIND02 on 23-08-2017.
 */

public class FirstTeamSquadFragment extends Fragment {

    public static Fragment newInstance(List<MySquadInfo> squadInfos) {
        FirstTeamSquadFragment firstTeamSquadFragment = new FirstTeamSquadFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BundleKeys.OPPONENT_SQUADS, (Serializable) squadInfos);
        firstTeamSquadFragment.setArguments(bundle);
        return firstTeamSquadFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first_team_squad, container, false);
        CardView mTeamSquadCardView = (CardView) view.findViewById(R.id.fragment_first_team_squad_card_view);
        RecyclerView mSquadList = (RecyclerView) view.findViewById(R.id.fragment_first_team_squad_rv);
        TextView mSquadEmptyView = (TextView) view.findViewById(R.id.fragment_first_team_squad_tv_empty);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mSquadList.setLayoutManager(gridLayoutManager);
        if(getArguments() != null) {
            List<MySquadInfo> squadInfos = (List<MySquadInfo>) getArguments().getSerializable(Constants.BundleKeys.OPPONENT_SQUADS);
            if(squadInfos != null) {
                if(squadInfos.size() > 0) {
                    FirstTeamSquadAdapter firstTeamSquadAdapter = new FirstTeamSquadAdapter(getActivity(), squadInfos);
                    mSquadList.setAdapter(firstTeamSquadAdapter);
                } else {
                    mTeamSquadCardView.setVisibility(View.GONE);
                }
            }
        }
        return view;
    }
}
