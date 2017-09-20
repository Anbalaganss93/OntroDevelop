package com.ontro.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ontro.R;
import com.ontro.dto.CricketExtrasScoreUpdate;

import java.util.List;

/**
 * Created by IDEOMIND02 on 17-07-2017.
 */

public class CricketExtrasScoreUpdateAdapter extends RecyclerView.Adapter<CricketExtrasScoreUpdateAdapter.CricketExtrasViewHolder> {
    private Context mContext;
    private List<CricketExtrasScoreUpdate> mCricketExtrasScoreModels;
    private ScoreExtrasListener mScoreExtrasListener;
    private String mTeamScoreUpdateStatus;
    private int mTotalExtras = 0;

    public CricketExtrasScoreUpdateAdapter(Context context,  String myTeamScoreUpdateStatus, ScoreExtrasListener scoreExtrasListener) {
        mContext = context;
        mScoreExtrasListener = scoreExtrasListener;
        mTeamScoreUpdateStatus = myTeamScoreUpdateStatus;
    }

    public void add(List<CricketExtrasScoreUpdate> cricketExtrasScoreModels) {
        mCricketExtrasScoreModels = cricketExtrasScoreModels;
        mTotalExtras = 0;
        for(int i = 0; i < mCricketExtrasScoreModels.size(); i++) {
            mTotalExtras = mTotalExtras + Integer.valueOf(mCricketExtrasScoreModels.get(i).getExtraValue());
        }
        notifyDataSetChanged();
    }

    @Override
    public CricketExtrasViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflater_cricket_extras_score_update_item, parent, false);
        return new CricketExtrasViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CricketExtrasViewHolder holder, int position) {
        final CricketExtrasScoreUpdate cricketExtrasScoreModel = mCricketExtrasScoreModels.get(position);
        holder.mExtrasNameTextView.setText(cricketExtrasScoreModel.getExtraName());
        holder.mTeamExtrasView.setText(cricketExtrasScoreModel.getExtraValue());

        holder.mExtrasAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeExtrasScore(holder, true, cricketExtrasScoreModel);
            }
        });
        holder.mExtrasSubView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeExtrasScore(holder, false, cricketExtrasScoreModel);
            }
        });
    }

    private void changeExtrasScore(CricketExtrasViewHolder holder, boolean canAdd, CricketExtrasScoreUpdate cricketExtrasScoreModel) {
        String extrasValue = cricketExtrasScoreModel.getExtraValue();

        int extrasScore  = Integer.valueOf(extrasValue);
        int extras =  extrasScore + (canAdd ? 1 : -1);
        if (0 > extras) {
            extras = 0;
            holder.mExtrasSubView.setAlpha((float) 0.5);
        } else {
            mTotalExtras = mTotalExtras + (canAdd ? 1 : -1);
            holder.mExtrasSubView.setAlpha((float) 1.0);
        }

        if (999 <= extras) {
            extras = 999;
            holder.mExtrasAddView.setAlpha((float) 0.5);
        } else {
            holder.mExtrasAddView.setAlpha((float) 1.0);
        }
        updateInfo(holder, String.valueOf(extras), cricketExtrasScoreModel);
    }

    private void updateInfo(CricketExtrasViewHolder holder, String extras, CricketExtrasScoreUpdate cricketExtrasScoreModel) {
        holder.mTeamExtrasView.setText(extras);
        cricketExtrasScoreModel.setExtraValue(extras);
        notifyDataSetChanged();
        if(mScoreExtrasListener != null) {
            mScoreExtrasListener.onUpdateExtras(String.valueOf(mTotalExtras));
        }
    }

    @Override
    public int getItemCount() {
        return mCricketExtrasScoreModels.size();
    }

    public List<CricketExtrasScoreUpdate> onGetExtrasScore() {
        return mCricketExtrasScoreModels;
    }

    public class CricketExtrasViewHolder extends RecyclerView.ViewHolder {
        private TextView mExtrasNameTextView, mTeamExtrasView;
        private ImageView mExtrasAddView, mExtrasSubView;

        public CricketExtrasViewHolder(View itemView) {
            super(itemView);
            mExtrasNameTextView = (TextView) itemView.findViewById(R.id.inflater_cricket_extras_score_update_item_tv_team_name);
            mTeamExtrasView = (TextView) itemView.findViewById(R.id.inflater_cricket_extras_score_update_item_tv_team_goals);
            mExtrasAddView = (ImageView) itemView.findViewById(R.id.inflater_cricket_extras_score_update_item_iv_extras_add);
            mExtrasSubView = (ImageView) itemView.findViewById(R.id.inflater_cricket_extras_score_update_item_iv_extras_sub);
            Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/roboto_regular.ttf");
            mTeamExtrasView.setTypeface(typeface);
            if(mTeamScoreUpdateStatus != null) {
                switch (mTeamScoreUpdateStatus) {
                    case "0":
                        mExtrasAddView.setEnabled(true);
                        mExtrasSubView.setEnabled(true);
                        break;
                    case "1":
                    case "2":
                        mExtrasAddView.setEnabled(false);
                        mExtrasSubView.setEnabled(false);
                        break;
                    case "3":
                        mExtrasAddView.setEnabled(true);
                        mExtrasSubView.setEnabled(true);
                        break;
                }
            }

        }
    }

    public interface ScoreExtrasListener {

        void onUpdateExtras(String extras);
    }
}
