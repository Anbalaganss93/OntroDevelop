package com.ontro.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.ontro.Constants;
import com.ontro.R;
import com.ontro.dto.CricketBatsmanScoreModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IDEOMIND02 on 17-07-2017.
 */

public class CricketBatsmenScoreUpdateAdapter extends RecyclerView.Adapter<CricketBatsmenScoreUpdateAdapter.CricketBatsmanScoreViewHolder> {
    private Context mContext;
    private List<CricketBatsmanScoreModel> mBatsmanScoreModels = new ArrayList<>();
    private String mTeamScoreUpdateStatus;

    public CricketBatsmenScoreUpdateAdapter(Context context, String myTeamScoreUpdateStatus) {
        mContext = context;
        mTeamScoreUpdateStatus = myTeamScoreUpdateStatus;
    }

    public void add(List<CricketBatsmanScoreModel> cricketBatsmanScoreModels) {
        mBatsmanScoreModels = cricketBatsmanScoreModels;
        notifyDataSetChanged();
    }

    @Override
    public CricketBatsmanScoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflater_cricket_player_score_update_item, parent, false);
        return new CricketBatsmanScoreViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CricketBatsmanScoreViewHolder holder, int position) {
        final CricketBatsmanScoreModel batsmanScoreModel = mBatsmanScoreModels.get(position);
        holder.mPlayerNameView.setText(batsmanScoreModel.getPlayerName());
        holder.mPlayerRunView.setText(batsmanScoreModel.getRuns());
        holder.mPlayerBallView.setText(batsmanScoreModel.getBalls());
        holder.mPlayerFoursView.setText(batsmanScoreModel.getFours());
        holder.mPlayerSixesView.setText(batsmanScoreModel.getSixs());
        if (position == mBatsmanScoreModels.size() - 1) {
            holder.mPlayerSixesView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        }
        holder.mPlayerRunView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                batsmanScoreModel.setRuns(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        holder.mPlayerBallView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                batsmanScoreModel.setBalls(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        holder.mPlayerFoursView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                batsmanScoreModel.setFours(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        holder.mPlayerSixesView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                batsmanScoreModel.setSixs(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mBatsmanScoreModels.size();
    }

    public List<CricketBatsmanScoreModel> onGetBatsmenScore() {
        return mBatsmanScoreModels;
    }

    public class CricketBatsmanScoreViewHolder extends RecyclerView.ViewHolder {
        private TextView mPlayerNameView;
        private EditText mPlayerRunView, mPlayerBallView, mPlayerFoursView, mPlayerSixesView;

        public CricketBatsmanScoreViewHolder(View itemView) {
            super(itemView);
            mPlayerNameView = (TextView) itemView.findViewById(R.id.inflater_cricket_player_score_update_item_tv_name);
            mPlayerRunView = (EditText) itemView.findViewById(R.id.inflater_cricket_player_score_update_item_tv_run_or_over);
            mPlayerBallView = (EditText) itemView.findViewById(R.id.inflater_cricket_player_score_update_item_tv_ball_or_maiden);
            mPlayerFoursView = (EditText) itemView.findViewById(R.id.inflater_cricket_player_score_update_item_tv_four_or_given_run);
            mPlayerSixesView = (EditText) itemView.findViewById(R.id.inflater_cricket_player_score_update_item_tv_six_or_wicket);
            Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/roboto_regular.ttf");
            mPlayerRunView.setTypeface(typeface);
            mPlayerBallView.setTypeface(typeface);
            mPlayerFoursView.setTypeface(typeface);
            mPlayerSixesView.setTypeface(typeface);
            if(mTeamScoreUpdateStatus != null) {
                switch (mTeamScoreUpdateStatus) {
                    case Constants.DefaultText.ZERO:
                        mPlayerRunView.setEnabled(true);
                        mPlayerBallView.setEnabled(true);
                        mPlayerFoursView.setEnabled(true);
                        mPlayerSixesView.setEnabled(true);
                        mPlayerRunView.setSelectAllOnFocus(true);
                        mPlayerBallView.setSelectAllOnFocus(true);
                        mPlayerFoursView.setSelectAllOnFocus(true);
                        mPlayerSixesView.setSelectAllOnFocus(true);
                        break;
                    case Constants.DefaultText.ONE:
                    case Constants.DefaultText.TWO:
                        mPlayerRunView.setEnabled(false);
                        mPlayerBallView.setEnabled(false);
                        mPlayerFoursView.setEnabled(false);
                        mPlayerSixesView.setEnabled(false);
                        mPlayerRunView.setSelectAllOnFocus(false);
                        mPlayerBallView.setSelectAllOnFocus(false);
                        mPlayerFoursView.setSelectAllOnFocus(false);
                        mPlayerSixesView.setSelectAllOnFocus(false);
                        break;
                    case Constants.DefaultText.THREE:
                        mPlayerRunView.setEnabled(true);
                        mPlayerBallView.setEnabled(true);
                        mPlayerFoursView.setEnabled(true);
                        mPlayerSixesView.setEnabled(true);
                        mPlayerRunView.setSelectAllOnFocus(true);
                        mPlayerBallView.setSelectAllOnFocus(true);
                        mPlayerFoursView.setSelectAllOnFocus(true);
                        mPlayerSixesView.setSelectAllOnFocus(true);
                        break;
                }
            }
        }
    }
}
