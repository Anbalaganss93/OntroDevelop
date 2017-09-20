package com.ontro.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ontro.Constants;
import com.ontro.R;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by IDEOMIND02 on 07-08-2017.
 */

public class SetGameTotalScoreOverviewFragment extends Fragment{
    private String sportType;
    private View mRootView;
    private LinearLayout mGameScoreOverViewContainer;
    private TextView mFirstTeamTotalScoreView, mSecondTeamTotalScoreView;

    public static Fragment newInstance(String firstMatchScore, String secondMatchScore, String sportType) {
       SetGameTotalScoreOverviewFragment overviewFragment = new SetGameTotalScoreOverviewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BundleKeys.FIRST_TEAM_MATCH_SCORE, firstMatchScore);
        bundle.putString(Constants.BundleKeys.SECOND_TEAM_MATCH_SCORE, secondMatchScore);
        bundle.putString(Constants.BundleKeys.SPORT_ID, sportType);
        overviewFragment.setArguments(bundle);
        return overviewFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_set_game_total_score_overview, container, false);
        initView();
        if(getArguments() != null) {
            String firstTeamScoreArray = getArguments().getString(Constants.BundleKeys.FIRST_TEAM_MATCH_SCORE);
            String secondTeamScoreArray = getArguments().getString(Constants.BundleKeys.SECOND_TEAM_MATCH_SCORE);
            sportType = getArguments().getString(Constants.BundleKeys.SPORT_ID);
            try {
                JSONArray firstTeamArray = new JSONArray(firstTeamScoreArray);
                JSONArray secondTeamArray = new JSONArray(secondTeamScoreArray);
                scoreView(firstTeamArray, secondTeamArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return mRootView;
    }

    private void initView() {
        mGameScoreOverViewContainer = (LinearLayout) mRootView.findViewById(R.id.fragment_set_game_total_score_overview_ll_container);
        mFirstTeamTotalScoreView = (TextView) getActivity().findViewById(R.id.game_header_team_one_score);
        mSecondTeamTotalScoreView = (TextView) getActivity().findViewById(R.id.game_header_team_two_score);
    }

    private void scoreView(JSONArray firstTeamArray, JSONArray secondTeamArray) {
        int length = firstTeamArray.length();
        int setCount = 0;
        int firstTeamScore = 0;
        int secondTeamScore = 0;
        for (int i = 0; i < length; i++) {
            setCount = setCount + 1;
            View scoreView = LayoutInflater.from(getActivity()).inflate(R.layout.inflater_set_game_total_score_overview_item, null, false);
            TextView setNumber = (TextView) scoreView.findViewById(R.id.game_score_set);
            setNumber.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            TextView teamone_et = (TextView) scoreView.findViewById(R.id.game_score_stat_teamone);
            TextView teamtwo_et = (TextView) scoreView.findViewById(R.id.game_score_stat_teamtwo);
            try {
                teamone_et.setText(firstTeamArray.getJSONObject(i).getString("score"));
                teamtwo_et.setText(secondTeamArray.getJSONObject(i).getString("score"));
                if (i == 0) {
                    firstTeamScore = Integer.parseInt(firstTeamArray.getJSONObject(i).getString("won"));
                    secondTeamScore = Integer.parseInt(secondTeamArray.getJSONObject(i).getString("won"));
                } else {
                    firstTeamScore = firstTeamScore + Integer.parseInt(firstTeamArray.getJSONObject(i).getString("won"));
                    secondTeamScore = secondTeamScore + Integer.parseInt(secondTeamArray.getJSONObject(i).getString("won"));
                }
                mFirstTeamTotalScoreView.setText(String.valueOf(firstTeamScore));
                mSecondTeamTotalScoreView.setText(String.valueOf(secondTeamScore));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            String setnumber = String.valueOf(setCount);
            if (sportType.equalsIgnoreCase("3")){
                setNumber.setText("GAME " + setnumber);
            }else{
                setNumber.setText("SET " + setnumber);
            }

            setNumber.setTag(setCount - 1);
            teamone_et.setTag(setCount - 1);
            teamtwo_et.setTag(setCount - 1);
            mGameScoreOverViewContainer.addView(scoreView, setCount - 1, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        }
    }

}
