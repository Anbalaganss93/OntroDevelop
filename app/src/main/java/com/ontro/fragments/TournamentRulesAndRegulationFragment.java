package com.ontro.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ontro.Constants;
import com.ontro.R;
import com.ontro.dto.RulesAndRegulations;

/**
 * Created by IDEOMIND02 on 27-06-2017.
 */

public class TournamentRulesAndRegulationFragment extends Fragment {
    private RulesAndRegulations mRulesAndRegulations;
    private TextView mRulesAndRegulationView;
    private CardView mRulesAndRegulationCardView;

    public static TournamentRulesAndRegulationFragment newInstance(RulesAndRegulations rulesAndRegulations) {
        TournamentRulesAndRegulationFragment tournamentRulesAndRegulationFragment = new TournamentRulesAndRegulationFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BundleKeys.RULES_AND_REGULATION, rulesAndRegulations);
        tournamentRulesAndRegulationFragment.setArguments(bundle);
        return tournamentRulesAndRegulationFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            mRulesAndRegulations = (RulesAndRegulations) getArguments().getSerializable(Constants.BundleKeys.RULES_AND_REGULATION);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tournament_rules_and_regulation, container, false);
        mRulesAndRegulationCardView = (CardView) view.findViewById(R.id.fragment_tournament_rules_and_regulation_cv);
        mRulesAndRegulationView = (TextView) view.findViewById(R.id.fragment_tournament_rules_and_regulation_tv);
        if (mRulesAndRegulations != null) {
            mRulesAndRegulationCardView.setVisibility(View.VISIBLE);
            if (mRulesAndRegulations.getTournamentRules().contains("\r\n")) {
                String[] rules = mRulesAndRegulations.getTournamentRules().split("\r\n");
                String line = "";
                for (String rule : rules) {
                    line += "\u2022  " + rule + "\n";
                }
                mRulesAndRegulationView.setText(line);
            } else {
                mRulesAndRegulationView.setText("\u2022  " + mRulesAndRegulations.getTournamentRules() + "\n");
            }
        } else {
            mRulesAndRegulationCardView.setVisibility(View.GONE);
        }
        return view;
    }

}
