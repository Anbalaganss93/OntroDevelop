package com.ontro.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ontro.Constants;
import com.ontro.R;
import com.ontro.dto.BasketballScoreResponse;
import com.ontro.dto.BasketballSetDTO;

import java.io.Serializable;
import java.util.List;

/**
 * Created by umm on 20-Jul-17.
 */

public class BasketballOverviewFragment extends Fragment {

    private LinearLayout scoreviewLayout;
    private String[] quarter = {"1ST QUARTER", "2ND QUARTER", "3RD QUARTER", "4TH QUARTER", "5TH QUARTER", "6TH QUARTER", "7TH QUARTER",
    "8TH QUARTER", "9TH QUARTER", "10TH QUARTER"};

    public static BasketballOverviewFragment newInstance(List<BasketballScoreResponse> responses) {
        BasketballOverviewFragment basketballOverviewFragment = new BasketballOverviewFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BundleKeys.QUARTER_SQUARE, (Serializable) responses);
        basketballOverviewFragment.setArguments(bundle);
        return basketballOverviewFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_basketball_overview, container, false);
        scoreviewLayout = (LinearLayout)view.findViewById(R.id.score_layout);
        if (getArguments() != null) {
            List<BasketballScoreResponse> list = (List<BasketballScoreResponse>) getArguments().getSerializable(Constants.BundleKeys.QUARTER_SQUARE);
            UpdateOverview(list);
        }
        return view;
    }

    private void UpdateOverview(List<BasketballScoreResponse> list) {
        scoreviewLayout.removeAllViews();
        LayoutInflater vi = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        List<BasketballSetDTO> teamOne = list.get(1).getQuarterScore();
        List<BasketballSetDTO> teamTwo = list.get(0).getQuarterScore();

        for (int i = 0; i < teamOne.size(); i++) {
            View v = vi.inflate(R.layout.row_basketball_overview, null);
            TextView name = (TextView) v.findViewById(R.id.tv_quarter);
            TextView point1 = (TextView) v.findViewById(R.id.tv_score_one);
            TextView point2 = (TextView) v.findViewById(R.id.tv_score_two);

            name.setText(quarter[i]);
            point1.setText(teamTwo.get(i).getScore());
            point2.setText(teamOne.get(i).getScore());
            scoreviewLayout.addView(v);
        }
        scoreviewLayout.setPadding(15, 25, 15, 25);
    }
}
