package com.ontro.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ontro.Constants;
import com.ontro.R;
import com.ontro.dto.CricketBatsmanScoreModel;
import com.ontro.dto.CricketBowlerScoreModel;
import com.ontro.dto.CricketExtrasScoreModel;
import com.ontro.dto.CricketTeamScoreModel;

import java.util.List;

/**
 * Created by IDEOMIND02 on 17-07-2017.
 */

public class CricketTeamScoreUpdateAdapter extends RecyclerView.Adapter<CricketTeamScoreUpdateAdapter.CricketTeamScoreViewHolder>{
    private final String mMyTeamScoreUpdateStatus;
    private Context mContext;
    private List<CricketTeamScoreModel> mTeamScoreModels;
    private List<CricketBatsmanScoreModel> mBatsmanScoreModels;
    private List<CricketBowlerScoreModel> mBowlerScoreModels;
    private List<CricketExtrasScoreModel> mExtrasScoreModels;
    private CricketTeamScoreUpdateListener mTeamScoreUpdateListener;

    public CricketTeamScoreUpdateAdapter(Context context, List<CricketTeamScoreModel> cricketTeamScoreModels,
                                         String myTeamScoreUpdateStatus, CricketTeamScoreUpdateListener cricketTeamScoreUpdateListener) {
        mContext = context;
        mTeamScoreModels = cricketTeamScoreModels;
        mTeamScoreUpdateListener = cricketTeamScoreUpdateListener;
        mMyTeamScoreUpdateStatus = myTeamScoreUpdateStatus;
    }

    @Override
    public CricketTeamScoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflater_cricket_team_score_update_item, parent, false);
        return new CricketTeamScoreViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CricketTeamScoreViewHolder holder, int position) {
        final CricketTeamScoreModel cricketTeamScoreModel = mTeamScoreModels.get(position);
        holder.mTeamNameView.setText(cricketTeamScoreModel.getTeamName());
        holder.mTeamScoreView.setText(cricketTeamScoreModel.getScore());
        holder.mTeamWicketView.setText(cricketTeamScoreModel.getWickets());
        holder.mTeamOverView.setText(cricketTeamScoreModel.getOvers());

        if(mMyTeamScoreUpdateStatus.equals("0") || mMyTeamScoreUpdateStatus.equals("3")) {
            int totalScore = 0;
            for (int i = 0; i < mBatsmanScoreModels.size(); i++) {
                totalScore = totalScore + Integer.valueOf(mBatsmanScoreModels.get(i).getRuns());
            }
            int totalOver = 0, totalWickets = 0, ballCount = 0;
            for (int i = 0; i < mBowlerScoreModels.size(); i++) {
                String over = mBowlerScoreModels.get(i).getOver();
                if (over.contains(Constants.DefaultText.DOT)) {
                    String overs[] = over.split("\\.");
                    int overCount = Integer.valueOf(overs[0]) * 6;
                    ballCount = Integer.valueOf(overs[1]);
                    totalOver += overCount;
                } else {
                    totalOver += (Integer.valueOf(over) * 6);
                }
                totalWickets = totalWickets + Integer.valueOf(mBowlerScoreModels.get(i).getWickets());
            }
            if (mExtrasScoreModels.size() > 0) {
                int extras = Integer.valueOf(mExtrasScoreModels.get(0).getExtras());
                totalScore = totalScore + extras;
            }
            int over = totalOver / 6;
            if (totalScore != 0 && totalOver != 0) {
                if (position == 0) {
                    holder.mTeamScoreView.setEnabled(false);
                    holder.mTeamWicketView.setEnabled(true);
                    holder.mTeamOverView.setEnabled(true);
                    cricketTeamScoreModel.setScore(String.valueOf(totalScore));
                    holder.mTeamScoreView.setText(cricketTeamScoreModel.getScore());
                    holder.mTeamScoreView.setSelectAllOnFocus(false);
                    holder.mTeamWicketView.setSelectAllOnFocus(false);
                    holder.mTeamOverView.setSelectAllOnFocus(false);
                    if (mTeamScoreUpdateListener != null) {
                        mTeamScoreUpdateListener.onScoreUpdated(mTeamScoreModels);
                    }
                } else {
                    if (mMyTeamScoreUpdateStatus.equals("1") || mMyTeamScoreUpdateStatus.equals("2")) {
                        holder.mTeamScoreView.setEnabled(false);
                        holder.mTeamWicketView.setEnabled(false);
                        holder.mTeamOverView.setEnabled(false);
                        cricketTeamScoreModel.setOvers(over + Constants.DefaultText.DOT + ballCount);
                        cricketTeamScoreModel.setWickets(String.valueOf(totalWickets));
                        holder.mTeamWicketView.setText(cricketTeamScoreModel.getWickets());
                        holder.mTeamOverView.setText(cricketTeamScoreModel.getOvers());
                    } else {
                        holder.mTeamScoreView.setEnabled(true);
                        holder.mTeamWicketView.setEnabled(false);
                        holder.mTeamOverView.setEnabled(false);
                        cricketTeamScoreModel.setOvers(over + Constants.DefaultText.DOT + ballCount);
                        cricketTeamScoreModel.setWickets(String.valueOf(totalWickets));
                        holder.mTeamWicketView.setText(cricketTeamScoreModel.getWickets());
                        holder.mTeamOverView.setText(cricketTeamScoreModel.getOvers());
                    }
                }
            }

            holder.mTeamScoreView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    cricketTeamScoreModel.setScore(s.toString());
                    if (mTeamScoreUpdateListener != null) {
                        mTeamScoreUpdateListener.onScoreUpdated(mTeamScoreModels);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            holder.mTeamWicketView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    cricketTeamScoreModel.setWickets(s.toString());
                    if (mTeamScoreUpdateListener != null) {
                        mTeamScoreUpdateListener.onScoreUpdated(mTeamScoreModels);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            holder.mTeamOverView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    cricketTeamScoreModel.setOvers(s.toString());
                    if (mTeamScoreUpdateListener != null) {
                        mTeamScoreUpdateListener.onScoreUpdated(mTeamScoreModels);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        } else {
            holder.mTeamScoreView.setEnabled(false);
            holder.mTeamWicketView.setEnabled(false);
            holder.mTeamOverView.setEnabled(false);
        }

    }

    @Override
    public int getItemCount() {
        return mTeamScoreModels.size();
    }

    public List<CricketTeamScoreModel> onGetUpdatedTeamScore() {
        return mTeamScoreModels;
    }

    public void add(List<CricketBatsmanScoreModel> mCricketBatsmanScoreModels, List<CricketBowlerScoreModel> mCricketBowlerScoreModels, List<CricketExtrasScoreModel> mCricketExtrasScoreModels) {
        mBatsmanScoreModels = mCricketBatsmanScoreModels;
        mBowlerScoreModels = mCricketBowlerScoreModels;
        mExtrasScoreModels = mCricketExtrasScoreModels;
        notifyItemRangeChanged(0, mTeamScoreModels.size());
    }

    public class CricketTeamScoreViewHolder extends RecyclerView.ViewHolder {
        private EditText mTeamScoreView, mTeamWicketView, mTeamOverView;
        private TextView mTeamNameView;

        public CricketTeamScoreViewHolder(View itemView) {
            super(itemView);
            mTeamNameView = (TextView) itemView.findViewById(R.id.inflater_cricket_team_score_update_item_tv_team_name);
            mTeamScoreView = (EditText) itemView.findViewById(R.id.inflater_cricket_team_score_update_item_tv_team_score);
            mTeamWicketView = (EditText) itemView.findViewById(R.id.inflater_cricket_team_score_update_item_tv_team_wicket);
            mTeamOverView = (EditText) itemView.findViewById(R.id.inflater_cricket_team_score_update_item_tv_team_overs);
            Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/roboto_regular.ttf");
            mTeamScoreView.setTypeface(typeface);
            mTeamWicketView.setTypeface(typeface);
            mTeamOverView.setTypeface(typeface);
            switch (mMyTeamScoreUpdateStatus) {
                case "0":
                    mTeamScoreView.setEnabled(true);
                    mTeamWicketView.setEnabled(true);
                    mTeamOverView.setEnabled(true);
                    mTeamScoreView.setSelectAllOnFocus(true);
                    mTeamWicketView.setSelectAllOnFocus(true);
                    mTeamOverView.setSelectAllOnFocus(true);
                    break;
                case "1":
                case "2":
                    mTeamScoreView.setEnabled(false);
                    mTeamWicketView.setEnabled(false);
                    mTeamOverView.setEnabled(false);
                    mTeamScoreView.setSelectAllOnFocus(false);
                    mTeamWicketView.setSelectAllOnFocus(false);
                    mTeamOverView.setSelectAllOnFocus(false);
                    break;
                case "3":
                    mTeamScoreView.setEnabled(true);
                    mTeamWicketView.setEnabled(true);
                    mTeamOverView.setEnabled(true);
                    mTeamScoreView.setSelectAllOnFocus(true);
                    mTeamWicketView.setSelectAllOnFocus(true);
                    mTeamOverView.setSelectAllOnFocus(true);
                    break;
            }
        }
    }

    public interface CricketTeamScoreUpdateListener {
        void onScoreUpdated(List<CricketTeamScoreModel> mTeamScoreUpdates);
    }
}
