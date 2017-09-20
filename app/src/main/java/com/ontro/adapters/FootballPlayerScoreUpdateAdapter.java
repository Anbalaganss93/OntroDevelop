package com.ontro.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ontro.Constants;
import com.ontro.R;
import com.ontro.dto.FootballPlayerScoreUpdate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IDEOMIND02 on 12-07-2017.
 */

public class FootballPlayerScoreUpdateAdapter extends RecyclerView.Adapter<FootballPlayerScoreUpdateAdapter.FootballScoreViewHolder> {
    private List<FootballPlayerScoreUpdate> mFootballPlayerScoreUpdates = new ArrayList<>();
    private Context mContext;
    private int selectedPosition = -1;
    private String mMyTeamScoreUpdateStatus;

    public FootballPlayerScoreUpdateAdapter(Context context, String myTeamScoreUpdateStatus) {
        mContext = context;
        mMyTeamScoreUpdateStatus = myTeamScoreUpdateStatus;
    }

    public void add(List<FootballPlayerScoreUpdate> footballPlayerScoreUpdates) {
        mFootballPlayerScoreUpdates = footballPlayerScoreUpdates;
        notifyDataSetChanged();
    }

    @Override
    public FootballScoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflater_football_score_update_view_item, parent, false);
        return new FootballScoreViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FootballScoreViewHolder holder, int position) {
        final FootballPlayerScoreUpdate scoreUpdate = mFootballPlayerScoreUpdates.get(position);
        holder.mPlayerNameTextView.setText(scoreUpdate.getPlayerName());
        holder.mPlayerGoalView.setText(scoreUpdate.getPlayerGoals());
        holder.mPlayerAssistsView.setText(scoreUpdate.getPlayerAssists());
        holder.mGoalKeeperCheckBox.setOnCheckedChangeListener(null);
        if(selectedPosition == -1) {
            if (scoreUpdate.getIsGolfKeeper().equals(Constants.DefaultText.ONE)) {
                holder.mGoalKeeperCheckBox.setChecked(true);
            } else {
                holder.mGoalKeeperCheckBox.setChecked(false);
            }
        } else {
            holder.mGoalKeeperCheckBox.setChecked(selectedPosition == position);
        }
        if(selectedPosition == position) {
            scoreUpdate.setIsGolfKeeper(Constants.DefaultText.ONE);
        } else {
            scoreUpdate.setIsGolfKeeper(Constants.DefaultText.ZERO);
        }
        holder.mGoalKeeperCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        holder.mGoalKeeperCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selectedPosition = holder.getAdapterPosition();
                updateInfo(holder, scoreUpdate.getPlayerGoals(), scoreUpdate.getPlayerAssists(), scoreUpdate);
            }
        });

        holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mGoalKeeperCheckBox.setChecked(!holder.mGoalKeeperCheckBox.isChecked());
            }
        });

        holder.mGoalAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeGoalScore(holder, true, scoreUpdate);
            }
        });
        holder.mGoalSubView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeGoalScore(holder, false, scoreUpdate);
            }
        });
        holder.mAssistAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeAssistScore(holder, true, scoreUpdate);
            }
        });
        holder.mAssistSubView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeAssistScore(holder, false, scoreUpdate);
            }
        });
    }

    private void changeAssistScore(FootballScoreViewHolder holder, boolean canAdd, FootballPlayerScoreUpdate scoreUpdate) {
        String goals = scoreUpdate.getPlayerGoals();
        if(!TextUtils.isEmpty(holder.mPlayerGoalView.getText())) {
            if(!goals.equals(holder.mPlayerGoalView.getText().toString())) {
                goals = holder.mPlayerGoalView.getText().toString();
            }
        }
        String playerAssists  = scoreUpdate.getPlayerAssists();
        if(!TextUtils.isEmpty(holder.mPlayerAssistsView.getText())) {
            String assists = holder.mPlayerAssistsView.getText().toString();
            if(!playerAssists.equals(assists)) {
                playerAssists = assists;
            }
        }
        int assistScore = Integer.valueOf(playerAssists);
        int assists =  assistScore + (canAdd ? 1 : -1);
        if (0 >= assists) {
            assists = 0;
            holder.mAssistSubView.setAlpha((float) 0.5);
        } else {
            int goalValue = Integer.valueOf(goals);
            /*if(goalValue < assists) {
                goalValue = goalValue + (canAdd ? 1 : -1);
            }*/
            goals = String.valueOf(goalValue);
            holder.mAssistSubView.setAlpha((float) 1.0);
        }
        updateInfo(holder, goals, String.valueOf(assists), scoreUpdate);
    }

    private void changeGoalScore(FootballScoreViewHolder holder, boolean canAdd, FootballPlayerScoreUpdate scoreUpdate) {
        String goal  = scoreUpdate.getPlayerGoals();
        if(!TextUtils.isEmpty(holder.mPlayerGoalView.getText())) {
          String goals = holder.mPlayerGoalView.getText().toString();
            if(!goal.equals(goals)) {
                goal = goals;
            }
        }
        String playerAssists  = scoreUpdate.getPlayerAssists();
        if(!TextUtils.isEmpty(holder.mPlayerAssistsView.getText())) {
            playerAssists = holder.mPlayerAssistsView.getText().toString();
        }
        int goalScore = Integer.valueOf(goal);
        int goals =  goalScore + (canAdd ? 1 : -1);
        if (0 >= goals) {
            goals = 0;
            holder.mGoalSubView.setAlpha((float) 0.5);
        } else {
            holder.mGoalSubView.setAlpha((float) 1.0);
        }
        int assists = Integer.valueOf(playerAssists);
       /* if(goals < assists) {
            assists = assists + (canAdd ? 1 : -1);
        }*/
        playerAssists = String.valueOf(assists);
        updateInfo(holder, String.valueOf(goals), playerAssists, scoreUpdate);
    }

    private void updateInfo(FootballScoreViewHolder holder, String goalScore, String assistScore, FootballPlayerScoreUpdate scoreUpdate) {
        holder.mGoalKeeperCheckBox.setChecked(selectedPosition == holder.getAdapterPosition());
        holder.mPlayerNameTextView.setText(scoreUpdate.getPlayerName());
        if(selectedPosition == holder.getAdapterPosition()) {
            scoreUpdate.setIsGolfKeeper(Constants.DefaultText.ONE);
        } else {
            scoreUpdate.setIsGolfKeeper(Constants.DefaultText.ZERO);
        }
        scoreUpdate.setPlayerGoals(goalScore);
        scoreUpdate.setPlayerAssists(assistScore);
        holder.mPlayerGoalView.setText(goalScore);
        holder.mPlayerAssistsView.setText(assistScore);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mFootballPlayerScoreUpdates.size();
    }

    public List<FootballPlayerScoreUpdate> getUpdatedScore() {
        return mFootballPlayerScoreUpdates;
    }

    public class FootballScoreViewHolder extends RecyclerView.ViewHolder {
        private TextView mPlayerNameTextView, mPlayerGoalView, mPlayerAssistsView;
        private ImageView mGoalAddView, mGoalSubView, mAssistAddView, mAssistSubView;
        private AppCompatCheckBox mGoalKeeperCheckBox;
        private LinearLayout mLinearLayout;

        public FootballScoreViewHolder(View itemView) {
            super(itemView);
            mPlayerNameTextView = (TextView) itemView.findViewById(R.id.inflater_football_score_update_view_item_tv_player_name);
            mPlayerGoalView = (TextView) itemView.findViewById(R.id.inflater_football_score_update_view_item_tv_player_goals);
            mPlayerAssistsView = (TextView) itemView.findViewById(R.id.inflater_football_score_update_view_item_tv_player_assists);
            mGoalAddView = (ImageView) itemView.findViewById(R.id.inflater_football_score_update_view_item_iv_goal_add);
            mGoalSubView = (ImageView) itemView.findViewById(R.id.inflater_football_score_update_view_item_iv_goal_sub);
            mAssistAddView = (ImageView) itemView.findViewById(R.id.inflater_football_score_update_view_item_iv_assist_add);
            mAssistSubView = (ImageView) itemView.findViewById(R.id.inflater_football_score_update_view_item_iv_assist_sub);
            mGoalKeeperCheckBox = (AppCompatCheckBox) itemView.findViewById(R.id.inflater_football_score_update_view_item_cb_goal_keeper);
            mLinearLayout = (LinearLayout) itemView.findViewById(R.id.activity_football_score_update_ll);
            Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/roboto_regular.ttf");
            mPlayerGoalView.setTypeface(typeface);
            mPlayerAssistsView.setTypeface(typeface);
            switch (mMyTeamScoreUpdateStatus) {
                case Constants.DefaultText.ZERO:
                    mGoalAddView.setEnabled(true);
                    mGoalSubView.setEnabled(true);
                    mAssistAddView.setEnabled(true);
                    mAssistSubView.setEnabled(true);
                    mGoalKeeperCheckBox.setEnabled(true);
                    mLinearLayout.setEnabled(true);
                    break;
                case Constants.DefaultText.ONE:
                case Constants.DefaultText.TWO:
                    mGoalAddView.setEnabled(false);
                    mGoalSubView.setEnabled(false);
                    mAssistAddView.setEnabled(false);
                    mAssistSubView.setEnabled(false);
                    mGoalKeeperCheckBox.setEnabled(false);
                    mLinearLayout.setEnabled(false);
                    break;
                case Constants.DefaultText.THREE:
                    mGoalAddView.setEnabled(true);
                    mGoalSubView.setEnabled(true);
                    mAssistAddView.setEnabled(true);
                    mAssistSubView.setEnabled(true);
                    mGoalKeeperCheckBox.setEnabled(true);
                    mLinearLayout.setEnabled(true);
                    break;
            }
        }
    }
}
