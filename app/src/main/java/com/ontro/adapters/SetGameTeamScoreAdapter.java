package com.ontro.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ontro.R;
import com.ontro.SetGameScoreOverViewActivity;

/**
 * Created by IDEOMIND02 on 07-08-2017.
 */

public class SetGameTeamScoreAdapter extends RecyclerView.Adapter<SetGameTeamScoreAdapter.TeamScoreViewHolder>{
    private Context mContext;

    public SetGameTeamScoreAdapter(Context context) {
        mContext = context;
    }

    @Override
    public TeamScoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflater_set_game_team_score_overview_item, parent, false);
        return new TeamScoreViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TeamScoreViewHolder holder, int position) {
       /* if (mTeamOneLogo != null) {
            Glide.with(SetGameScoreOverViewActivity.this).load(mTeamOneLogo).placeholder(R.drawable.match_default).dontAnimate().into(mFirstTeamImageView);
        }
        if (mTeamTwoLogo != null) {
            Glide.with(SetGameScoreOverViewActivity.this).load(mTeamTwoLogo).placeholder(R.drawable.match_default).dontAnimate().into(mSecondTeamImageView);
        }*/
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class TeamScoreViewHolder extends RecyclerView.ViewHolder {
        private ImageView mPlayerImageView;
        private TextView mPlayerNameView;

        private TeamScoreViewHolder(View itemView) {
            super(itemView);
            mPlayerImageView = (ImageView) itemView.findViewById(R.id.inflater_set_game_team_score_overview_item_iv);
            mPlayerNameView = (TextView) itemView.findViewById(R.id.inflater_set_game_team_score_overview_item_tv);
        }
    }
}
