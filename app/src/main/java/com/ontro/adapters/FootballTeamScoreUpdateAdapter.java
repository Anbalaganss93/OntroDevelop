package com.ontro.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ontro.Constants;
import com.ontro.R;
import com.ontro.dto.FootballPlayerScoreUpdate;
import com.ontro.dto.FootballTeamScoreUpdate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IDEOMIND02 on 13-07-2017.
 */

public class FootballTeamScoreUpdateAdapter extends RecyclerView.Adapter<FootballTeamScoreUpdateAdapter.TeamScoreViewHolder> {
    private final String mMyTeamScoreUpdateStatus;
    private List<FootballPlayerScoreUpdate> mPlayerScoreUpdates = new ArrayList<>();
    private List<FootballTeamScoreUpdate> mTeamScoreUpdates = new ArrayList<>();
    private TeamScoreUpdateListener mTeamScoreUpdateListener;
    private Context mContext;

    public FootballTeamScoreUpdateAdapter(Context context, String myTeamScoreUpdateStatus,
                                          List<FootballTeamScoreUpdate> teamScoreUpdates,
                                          TeamScoreUpdateListener teamScoreUpdateListener) {
        mContext = context;
        mMyTeamScoreUpdateStatus = myTeamScoreUpdateStatus;
        mTeamScoreUpdates = teamScoreUpdates;
        mTeamScoreUpdateListener = teamScoreUpdateListener;
    }

    public void add(List<FootballPlayerScoreUpdate> playerScoreUpdates) {
        mPlayerScoreUpdates = playerScoreUpdates;
        notifyDataSetChanged();
    }

    @Override
    public TeamScoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflater_football_team_score_item, parent, false);
        return new TeamScoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TeamScoreViewHolder holder, int position) {
        final FootballTeamScoreUpdate scoreUpdate = mTeamScoreUpdates.get(position);
        holder.mTeamNameTextView.setText(scoreUpdate.getTeamName());
        holder.mTeamGoalView.setText(scoreUpdate.getTeamGoals());
        int totalGoals = 0;
        for (int i = 0; i < mPlayerScoreUpdates.size(); i++) {
            int goals = Integer.valueOf(mPlayerScoreUpdates.get(i).getPlayerGoals());
            totalGoals = totalGoals + goals;
        }

        if (position == 0) {
            if(totalGoals > 0) {
                holder.mGoalAddView.setEnabled(false);
                holder.mGoalSubView.setEnabled(false);
                holder.mTeamGoalView.setText(String.valueOf(totalGoals));
                scoreUpdate.setTeamGoals(String.valueOf(totalGoals));
                if (mTeamScoreUpdateListener != null) {
                    mTeamScoreUpdateListener.onScoreUpdated(mTeamScoreUpdates);
                }
            } else {
                holder.mGoalAddView.setEnabled(true);
                holder.mGoalSubView.setEnabled(true);
            }
        } else {
            if (mMyTeamScoreUpdateStatus.equals(Constants.DefaultText.ONE) || mMyTeamScoreUpdateStatus.equals(Constants.DefaultText.TWO)) {
                holder.mGoalAddView.setEnabled(false);
                holder.mGoalSubView.setEnabled(false);
            } else {
                holder.mGoalAddView.setEnabled(true);
                holder.mGoalSubView.setEnabled(true);
            }
        }
        holder.mGoalAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeGoalScore(holder, true, scoreUpdate, holder.getAdapterPosition());
            }
        });
        holder.mGoalSubView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeGoalScore(holder, false, scoreUpdate, holder.getAdapterPosition());
            }
        });
    }

    private void changeGoalScore(TeamScoreViewHolder holder, boolean canAdd, FootballTeamScoreUpdate scoreUpdate, int adapterPosition) {
        String goal = scoreUpdate.getTeamGoals();
        if (!TextUtils.isEmpty(holder.mTeamGoalView.getText())) {
            String goals = holder.mTeamGoalView.getText().toString();
            if (!goal.equals(goals)) {
                goal = goals;
            }
        }
        int goalScore = Integer.valueOf(goal);
        int goals = goalScore + (canAdd ? 1 : -1);
        if (0 >= goals) {
            goals = 0;
            holder.mGoalSubView.setAlpha((float) 0.5);
        } else {
            holder.mGoalSubView.setAlpha((float) 1.0);
        }
        updateInfo(holder, String.valueOf(goals), scoreUpdate, adapterPosition);
    }

    private void updateInfo(TeamScoreViewHolder holder, String goalScore, FootballTeamScoreUpdate scoreUpdate, int adapterPosition) {
        holder.mTeamNameTextView.setText(scoreUpdate.getTeamName());
        scoreUpdate.setTeamGoals(goalScore);
        holder.mTeamGoalView.setText(goalScore);
        notifyDataSetChanged();
        if (mTeamScoreUpdateListener != null) {
            mTeamScoreUpdateListener.onScoreUpdated(mTeamScoreUpdates);
        }
    }

    public List<FootballTeamScoreUpdate> onUpdatedTeamScore() {
        return mTeamScoreUpdates;
    }

    @Override
    public int getItemCount() {
        return mTeamScoreUpdates.size();
    }

    public class TeamScoreViewHolder extends RecyclerView.ViewHolder {
        private TextView mTeamNameTextView, mTeamGoalView;
        private ImageView mGoalAddView, mGoalSubView;

        public TeamScoreViewHolder(View itemView) {
            super(itemView);
            mTeamNameTextView = (TextView) itemView.findViewById(R.id.inflater_football_team_score_update_item_tv_team_name);
            mTeamGoalView = (TextView) itemView.findViewById(R.id.inflater_football_team_score_update_item_tv_team_goals);
            mGoalAddView = (ImageView) itemView.findViewById(R.id.inflater_football_team_score_update_item_iv_goal_add);
            mGoalSubView = (ImageView) itemView.findViewById(R.id.inflater_football_team_score_update_item_iv_goal_sub);
            Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/roboto_regular.ttf");
            mTeamGoalView.setTypeface(typeface);
            switch (mMyTeamScoreUpdateStatus) {
                case Constants.DefaultText.ZERO:
                    mGoalAddView.setEnabled(true);
                    mGoalSubView.setEnabled(true);
                    break;
                case Constants.DefaultText.ONE:
                case Constants.DefaultText.TWO:
                    mGoalAddView.setEnabled(false);
                    mGoalSubView.setEnabled(false);
                    break;
                case Constants.DefaultText.THREE:
                    mGoalAddView.setEnabled(true);
                    mGoalSubView.setEnabled(true);
                    break;
            }
        }
    }

    public interface TeamScoreUpdateListener {
        void onScoreUpdated(List<FootballTeamScoreUpdate> mTeamScoreUpdates);
    }
}
