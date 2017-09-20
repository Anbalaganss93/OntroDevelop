package com.ontro.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ontro.Constants;
import com.ontro.R;
import com.ontro.dto.BasketballScoreResponse;
import com.ontro.dto.BasketballScoreUpdateDTO;

import java.io.Serializable;
import java.util.List;

/**
 * Created by umm on 20-Jul-17.
 */

public class BasketballStatsFragment extends Fragment {

    private View view1,view2;
    private LinearLayout scoreviewLayout, benchPlayers;
    private RelativeLayout bonusScores;
    private TextView tvBenchPlayers;
    private String benchplayers;
    private CardView cardView;

    public static BasketballStatsFragment newInstance(BasketballScoreResponse scoreResponse) {
        BasketballStatsFragment basketballStatsFragment = new BasketballStatsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BundleKeys.FIRST_TEAM_PLAYER_SCORE, (Serializable) scoreResponse.getPlayerScore());
        bundle.putSerializable(Constants.BundleKeys.BENCH_PLAYER, (Serializable) scoreResponse.getBenchPlayer());
        basketballStatsFragment.setArguments(bundle);
        return basketballStatsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_basketball_stats, null);
        view1 = v.findViewById(R.id.view_divider);
        view2 = v.findViewById(R.id.view_divider2);
        scoreviewLayout = (LinearLayout) v.findViewById(R.id.score_layout);
        benchPlayers = (LinearLayout) v.findViewById(R.id.layout_bench_players);
        bonusScores = (RelativeLayout) v.findViewById(R.id.layout_bonus_scores);
        tvBenchPlayers = (TextView) v.findViewById(R.id.tv_bench_players);
        cardView = (CardView) v.findViewById(R.id.score_cardview);

        if (getArguments() != null) {
            List<BasketballScoreUpdateDTO> list = (List<BasketballScoreUpdateDTO>) getArguments().getSerializable(Constants.BundleKeys.FIRST_TEAM_PLAYER_SCORE);
            List<String> playerlist = (List<String>) getArguments().getSerializable(Constants.BundleKeys.BENCH_PLAYER);
            if (list.size()>0) {
                cardView.setVisibility(View.VISIBLE);
                UpdateScore(list, playerlist);
            }else{
                cardView.setVisibility(View.GONE);
            }
        }
        return v;
    }

    private void UpdateScore(List<BasketballScoreUpdateDTO> list, List<String> playerlist) {
        benchPlayers.setVisibility(View.VISIBLE);
        bonusScores.setVisibility(View.VISIBLE);
        view1.setVisibility(View.VISIBLE);
        view2.setVisibility(View.VISIBLE);
        scoreviewLayout.removeAllViews();
        LayoutInflater vi = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < list.size(); i++) {
            View v = vi.inflate(R.layout.row_basketball_stats, null);
            TextView name = (TextView) v.findViewById(R.id.tv_name);
            TextView point1 = (TextView) v.findViewById(R.id.tv_1p_value);
            TextView point2 = (TextView) v.findViewById(R.id.tv_2p_value);
            TextView point3 = (TextView) v.findViewById(R.id.tv_3p_value);

            name.setText(list.get(i).getName());
            point1.setText(list.get(i).getPoint_one());
            point2.setText(list.get(i).getPoint_two());
            point3.setText(list.get(i).getPoint_three());
            scoreviewLayout.addView(v);
        }

        if (playerlist.size() > 0){
            StringBuilder sb = new StringBuilder();
            for (int i=0;i<playerlist.size();i++) {
                sb.append(playerlist.get(i));
                sb.append(",");
            }
            benchplayers = sb.toString().trim();
            benchplayers = benchplayers.substring(0, benchplayers.length() - 1);
        }

        tvBenchPlayers.setText(benchplayers);
        scoreviewLayout.setPadding(0, 0, 0, 0);
    }
}
