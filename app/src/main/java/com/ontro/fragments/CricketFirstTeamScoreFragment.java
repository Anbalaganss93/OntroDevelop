package com.ontro.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.ontro.Constants;
import com.ontro.R;
import com.ontro.dto.CricketBatsmanScoreModel;
import com.ontro.dto.CricketBowlerScoreModel;
import com.ontro.dto.CricketScoreResponse;

import java.util.List;

/**
 * Created by IDEOMIND02 on 20-07-2017.
 */

public class CricketFirstTeamScoreFragment extends Fragment implements View.OnClickListener {
    private View mRootView;
    private TextView mTotalExtrasView, mIndividualExtrasView, mShowMorePlayerScoreView, mShowLessPlayerScoreView;
    private TableLayout mPlayerScoreView, mBowlerScoreView;
    private CricketScoreResponse mCricketScoreResponse;

    public static Fragment newInstance(CricketScoreResponse cricketScoreResponse) {
        CricketFirstTeamScoreFragment scoreFragment = new CricketFirstTeamScoreFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BundleKeys.FIRST_TEAM_SCORE, cricketScoreResponse);
        scoreFragment.setArguments(bundle);
        return scoreFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_cricket_team_score, container, false);
        initView();
        if (getArguments() != null) {
            mCricketScoreResponse = (CricketScoreResponse) getArguments().getSerializable(Constants.BundleKeys.FIRST_TEAM_SCORE);
            setValue(mCricketScoreResponse);
        }
        setListener();
        return mRootView;
    }

    private void initView() {
        mPlayerScoreView = (TableLayout) mRootView.findViewById(R.id.fragment_cricket_team_score_tl_players);
        mBowlerScoreView = (TableLayout) mRootView.findViewById(R.id.fragment_cricket_team_score_tl_bowlers);
        mShowMorePlayerScoreView = (TextView) mRootView.findViewById(R.id.fragment_cricket_team_score_tv_more);
        mShowLessPlayerScoreView = (TextView) mRootView.findViewById(R.id.fragment_cricket_team_score_tv_less);
        mTotalExtrasView = (TextView) mRootView.findViewById(R.id.fragment_cricket_team_score_tv_extras);
        mIndividualExtrasView = (TextView) mRootView.findViewById(R.id.fragment_cricket_team_score_tv_extras_individual);
    }

    private void setListener() {
        mShowMorePlayerScoreView.setOnClickListener(this);
        mShowLessPlayerScoreView.setOnClickListener(this);
    }

    private void setValue(CricketScoreResponse mCricketScoreResponse) {
        if (mCricketScoreResponse != null) {
            mTotalExtrasView.setText(String.valueOf(mCricketScoreResponse.getExtras()));
            mIndividualExtrasView.setText("("
                    + "b " + mCricketScoreResponse.getBye()
                    + ", lb " + mCricketScoreResponse.getLegBye()
                    + ", w " + mCricketScoreResponse.getWide()
                    + ", nb " + mCricketScoreResponse.getNoBall()
                    + ", p " + mCricketScoreResponse.getPenalty()
                    + ")");
            showBowlerView(mCricketScoreResponse.getBowlingScore());
            if (mCricketScoreResponse.getBattingScore().size() > 2) {
                showPlayerScoreCards(mCricketScoreResponse.getBattingScore(), 2);
            } else {
                showPlayerScoreCards(mCricketScoreResponse.getBattingScore(), mCricketScoreResponse.getBattingScore().size());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_cricket_team_score_tv_more:
                if (mCricketScoreResponse.getBattingScore() != null) {
                    mShowMorePlayerScoreView.setVisibility(View.GONE);
                    mShowLessPlayerScoreView.setVisibility(View.VISIBLE);
                    showPlayerScoreCards(mCricketScoreResponse.getBattingScore(), mCricketScoreResponse.getBattingScore().size());
                }
                break;
            case R.id.fragment_cricket_team_score_tv_less:
                if (mCricketScoreResponse.getBattingScore() != null) {
                    mShowMorePlayerScoreView.setVisibility(View.VISIBLE);
                    mShowLessPlayerScoreView.setVisibility(View.GONE);
                    if (mCricketScoreResponse.getBattingScore().size() > 2) {
                        showPlayerScoreCards(mCricketScoreResponse.getBattingScore(), 2);
                    } else {
                        showPlayerScoreCards(mCricketScoreResponse.getBattingScore(), mCricketScoreResponse.getBattingScore().size());
                    }
                }
                break;
        }
    }

    private void showPlayerScoreCards(List<CricketBatsmanScoreModel> batsmanScoreModels, int noOfPlayers) {
        mPlayerScoreView.removeAllViews();
        for (int i = 0; i < noOfPlayers; i++) {
            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View batsManScoreView = layoutInflater.inflate(R.layout.inflater_cricket_bat_or_bowl_score_view_item, mPlayerScoreView, false);
            TextView batsmanName = (TextView) batsManScoreView.findViewById(R.id.inflater_cricket_bat_or_bowl_score_view_item_tv_player_name);
            TextView batsmanRun = (TextView) batsManScoreView.findViewById(R.id.inflater_cricket_bat_or_bowl_score_view_item_tv_run_or_over);
            TextView batsmanBall = (TextView) batsManScoreView.findViewById(R.id.inflater_cricket_bat_or_bowl_score_view_item_tv_ball_or_maiden);
            TextView batsmanFours = (TextView) batsManScoreView.findViewById(R.id.inflater_cricket_bat_or_bowl_score_view_item_tv_four_or_given_run);
            TextView batsmanSixes = (TextView) batsManScoreView.findViewById(R.id.inflater_cricket_bat_or_bowl_score_view_item_tv_six_or_wicket);
            TextView batsmanStrikeRate = (TextView) batsManScoreView.findViewById(R.id.inflater_cricket_bat_or_bowl_score_view_item_tv_sr_or_eco);

            CricketBatsmanScoreModel batsmanScoreModel = batsmanScoreModels.get(i);
            batsmanName.setText(batsmanScoreModel.getPlayerName());
            batsmanRun.setText(batsmanScoreModel.getRuns());
            batsmanBall.setText(batsmanScoreModel.getBalls());
            batsmanFours.setText(batsmanScoreModel.getFours());
            batsmanSixes.setText(batsmanScoreModel.getSixs());
            batsmanStrikeRate.setText(String.format("%.2f", Double.valueOf(batsmanScoreModel.getStrikeRate())));

            if (batsmanBall.getText().toString().equals(Constants.DefaultText.ZERO)) {
                batsmanName.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_cement));
                batsmanRun.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_cement));
                batsmanBall.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_cement));
                batsmanFours.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_cement));
                batsmanSixes.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_cement));
                batsmanStrikeRate.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_cement));
                batsmanRun.setText(Constants.DefaultText.NO_SCORE);
                batsmanBall.setText(Constants.DefaultText.NO_SCORE);
                batsmanFours.setText(Constants.DefaultText.NO_SCORE);
                batsmanSixes.setText(Constants.DefaultText.NO_SCORE);
                batsmanStrikeRate.setText(Constants.DefaultText.NO_SCORE);
            }
            mPlayerScoreView.addView(batsManScoreView);
        }
    }

    private void showBowlerView(List<CricketBowlerScoreModel> mPlayers) {
        if (mPlayers != null) {
            mBowlerScoreView.removeAllViews();
            for (int i = 0; i < mPlayers.size(); i++) {
                LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View bowlerScoreView = layoutInflater.inflate(R.layout.inflater_cricket_bat_or_bowl_score_view_item, mBowlerScoreView, false);
                TextView bowlerName = (TextView) bowlerScoreView.findViewById(R.id.inflater_cricket_bat_or_bowl_score_view_item_tv_player_name);
                TextView bowlerOver = (TextView) bowlerScoreView.findViewById(R.id.inflater_cricket_bat_or_bowl_score_view_item_tv_run_or_over);
                TextView bowlerMaiden = (TextView) bowlerScoreView.findViewById(R.id.inflater_cricket_bat_or_bowl_score_view_item_tv_ball_or_maiden);
                TextView bowlerGivenRun = (TextView) bowlerScoreView.findViewById(R.id.inflater_cricket_bat_or_bowl_score_view_item_tv_four_or_given_run);
                TextView bowlerWickets = (TextView) bowlerScoreView.findViewById(R.id.inflater_cricket_bat_or_bowl_score_view_item_tv_six_or_wicket);
                TextView bowlerEconomy = (TextView) bowlerScoreView.findViewById(R.id.inflater_cricket_bat_or_bowl_score_view_item_tv_sr_or_eco);

                CricketBowlerScoreModel bowlerScoreModel = mPlayers.get(i);
                bowlerName.setText(bowlerScoreModel.getPlayerName());
                bowlerOver.setText(bowlerScoreModel.getOver());
                bowlerMaiden.setText(bowlerScoreModel.getMaiden());
                bowlerGivenRun.setText(bowlerScoreModel.getBowlingRun());
                bowlerWickets.setText(bowlerScoreModel.getWickets());
                bowlerEconomy.setText(String.format("%.2f", Double.valueOf(bowlerScoreModel.getEconomyRate())));
                mBowlerScoreView.addView(bowlerScoreView);
            }

        }
    }
}
