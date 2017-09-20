package com.ontro.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ontro.R;
import com.ontro.adapters.SetGameTeamScoreAdapter;

/**
 * Created by IDEOMIND02 on 07-08-2017.
 */

public class SetGameFirstTeamScoreFragment extends Fragment{
    private RecyclerView mTeamScoreOverView;
    public static Fragment newInstance() {
        return new SetGameFirstTeamScoreFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_set_game_team_score_overview, container, false);
        mTeamScoreOverView = (RecyclerView) rootView.findViewById(R.id.fragment_set_game_team_score_overview_rv);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mTeamScoreOverView.setLayoutManager(gridLayoutManager);
        SetGameTeamScoreAdapter setGameTeamScoreAdapter = new SetGameTeamScoreAdapter(getActivity());
        mTeamScoreOverView.setAdapter(setGameTeamScoreAdapter);
        return rootView;
    }
}
