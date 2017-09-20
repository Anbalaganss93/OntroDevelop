package com.ontro.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.ontro.Constants;
import com.ontro.R;
import com.ontro.dto.FootballPlayerScoreUpdate;
import com.ontro.dto.FootballScoreResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IDEOMIND02 on 21-07-2017.
 */

public class FootballFirstTeamScoreFragment extends Fragment implements View.OnClickListener {
    private View mRootView;
    private TextView mShowMorePlayerScoreView, mShowLessPlayerScoreView;
    private TableLayout mPlayerScoreView;
    private List<FootballPlayerScoreUpdate> mPlayerScores = new ArrayList<>();

    public static Fragment newInstance(FootballScoreResponse footballScoreResponse) {
        FootballFirstTeamScoreFragment teamScoreFragment = new FootballFirstTeamScoreFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BundleKeys.FIRST_TEAM_PLAYER_SCORE, (Serializable) footballScoreResponse.getPlayerScore());
        teamScoreFragment.setArguments(bundle);
        return teamScoreFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_football_first_team_score_overview, container, false);
        initView();
        if (getArguments() != null) {
            mPlayerScores = (List<FootballPlayerScoreUpdate>) getArguments().getSerializable(Constants.BundleKeys.FIRST_TEAM_PLAYER_SCORE);
            setValue(mPlayerScores);
        }
        setListener();
        return mRootView;
    }

    private void initView() {
        mPlayerScoreView = (TableLayout) mRootView.findViewById(R.id.fragment_football_first_team_score_overview_tl_players);
        mShowMorePlayerScoreView = (TextView) mRootView.findViewById(R.id.fragment_football_first_team_score_overview_tv_more);
        mShowLessPlayerScoreView = (TextView) mRootView.findViewById(R.id.fragment_football_first_team_score_overview_tv_less);
    }

    private void setListener() {
        mShowMorePlayerScoreView.setOnClickListener(this);
        mShowLessPlayerScoreView.setOnClickListener(this);
    }

    private void setValue(List<FootballPlayerScoreUpdate> mPlayerScores) {
        if (mPlayerScores != null) {
            showMorePlayer(mPlayerScores);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_football_first_team_score_overview_tv_more:
                showLessPlayer(mPlayerScores);
                break;
            case R.id.fragment_football_first_team_score_overview_tv_less:
                showMorePlayer(mPlayerScores);
                break;
        }

    }

    private void showMorePlayer(List<FootballPlayerScoreUpdate> mPlayerScores) {
        mShowMorePlayerScoreView.setVisibility(View.VISIBLE);
        mShowLessPlayerScoreView.setVisibility(View.GONE);
        if (mPlayerScores != null) {
            if (mPlayerScores.size() > 2) {
                showPlayerScoreCards(mPlayerScores, 2);
            } else {
                showPlayerScoreCards(mPlayerScores, mPlayerScores.size());
            }
        }

    }

    private void showLessPlayer(List<FootballPlayerScoreUpdate> mPlayerScores) {
        mShowMorePlayerScoreView.setVisibility(View.GONE);
        mShowLessPlayerScoreView.setVisibility(View.VISIBLE);
        showPlayerScoreCards(mPlayerScores, mPlayerScores.size());
    }


    private void showPlayerScoreCards(List<FootballPlayerScoreUpdate> mPlayerScores, int noOfPlayers) {
        mPlayerScoreView.removeAllViews();
        for (int i = 0; i < noOfPlayers; i++) {
            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View playerScoreView = layoutInflater.inflate(R.layout.inflater_football_score_view_item, null);
            TextView playerName = (TextView) playerScoreView.findViewById(R.id.inflater_football_score_view_item_tv_player_name);
            TextView playerGoals = (TextView) playerScoreView.findViewById(R.id.inflater_football_score_view_item_tv_player_goals);
            TextView playerAssists = (TextView) playerScoreView.findViewById(R.id.inflater_football_score_view_item_tv_player_assists);
            ImageView golfImage = (ImageView) playerScoreView.findViewById(R.id.inflater_football_score_view_item_iv_goal_keeper);
            FootballPlayerScoreUpdate footballPlayerScoreUpdate = mPlayerScores.get(i);
            String goals = footballPlayerScoreUpdate.getPlayerGoals();
            String assists = footballPlayerScoreUpdate.getPlayerAssists();
            playerName.setText(footballPlayerScoreUpdate.getPlayerName());
            playerGoals.setText(goals);
            playerAssists.setText(assists);
            if(footballPlayerScoreUpdate.getIsGolfKeeper().equals("1")) {
                golfImage.setVisibility(View.VISIBLE);
            } else {
                golfImage.setVisibility(View.INVISIBLE);
            }
            if (goals.equals(Constants.DefaultText.ZERO)
                    && assists.equals(Constants.DefaultText.ZERO)) {
                playerName.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_cement));
                playerGoals.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_cement));
                playerAssists.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_cement));
                playerGoals.setText(Constants.DefaultText.NO_SCORE);
                playerAssists.setText(Constants.DefaultText.NO_SCORE);
            } else if (goals.equals(Constants.DefaultText.ZERO)) {
                playerGoals.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_cement));
                playerGoals.setText(Constants.DefaultText.NO_SCORE);
            } else if (assists.equals(Constants.DefaultText.ZERO)) {
                playerAssists.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_cement));
                playerAssists.setText(Constants.DefaultText.NO_SCORE);
            }
            mPlayerScoreView.addView(playerScoreView);
        }
    }
}
