package com.ontro.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.ontro.R;
import com.ontro.dto.CricketBowlerScoreModel;

import java.util.List;

/**
 * Created by IDEOMIND02 on 26-07-2017.
 */

public class CricketBowlerScoreUpdateAdapter extends RecyclerView.Adapter<CricketBowlerScoreUpdateAdapter.CricketBowlerScoreViewHolder> {
    private Context mContext;
    private List<CricketBowlerScoreModel> mBowlerScoreModels;
    private String mTeamScoreUpdateStatus;

    public CricketBowlerScoreUpdateAdapter(Context context,String teamScoreUpdateStatus) {
        mContext = context;
        mTeamScoreUpdateStatus = teamScoreUpdateStatus;
    }

    public void add(List<CricketBowlerScoreModel> mCricketBowlerScoreModels) {
        mBowlerScoreModels = mCricketBowlerScoreModels;
        notifyDataSetChanged();
    }


    @Override
    public CricketBowlerScoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflater_cricket_player_score_update_item, parent, false);
        return new CricketBowlerScoreViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CricketBowlerScoreViewHolder holder, int position) {
        final CricketBowlerScoreModel bowlerScoreModel = mBowlerScoreModels.get(position);
        holder.mPlayerNameView.setText(bowlerScoreModel.getPlayerName());
        holder.mPlayerOverView.setText(bowlerScoreModel.getOver());
        holder.mPlayerMaidenView.setText(bowlerScoreModel.getMaiden());
        holder.mPlayerRunView.setText(bowlerScoreModel.getBowlingRun());
        holder.mPlayerWicketView.setText(bowlerScoreModel.getWickets());
        if (position == mBowlerScoreModels.size() - 1) {
            holder.mPlayerWicketView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        }
        holder.mPlayerOverView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                bowlerScoreModel.setOver(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        holder.mPlayerMaidenView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                bowlerScoreModel.setMaiden(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        holder.mPlayerRunView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                bowlerScoreModel.setBowlingRun(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        holder.mPlayerWicketView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                bowlerScoreModel.setWickets(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mBowlerScoreModels.size();
    }

    public List<CricketBowlerScoreModel> onGetBowlersScore() {
        return mBowlerScoreModels;
    }

    public class CricketBowlerScoreViewHolder extends RecyclerView.ViewHolder {
        private TextView mPlayerNameView;
        private EditText mPlayerOverView, mPlayerMaidenView, mPlayerRunView, mPlayerWicketView;

        public CricketBowlerScoreViewHolder(View itemView) {
            super(itemView);
            mPlayerNameView = (TextView) itemView.findViewById(R.id.inflater_cricket_player_score_update_item_tv_name);
            mPlayerOverView = (EditText) itemView.findViewById(R.id.inflater_cricket_player_score_update_item_tv_run_or_over);
            mPlayerMaidenView = (EditText) itemView.findViewById(R.id.inflater_cricket_player_score_update_item_tv_ball_or_maiden);
            mPlayerRunView = (EditText) itemView.findViewById(R.id.inflater_cricket_player_score_update_item_tv_four_or_given_run);
            mPlayerWicketView = (EditText) itemView.findViewById(R.id.inflater_cricket_player_score_update_item_tv_six_or_wicket);
            Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/roboto_regular.ttf");
            mPlayerOverView.setTypeface(typeface);
            mPlayerMaidenView.setTypeface(typeface);
            mPlayerRunView.setTypeface(typeface);
            mPlayerWicketView.setTypeface(typeface);
            mPlayerOverView.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
            InputFilter[] filters = new InputFilter[1];
            filters[0] = new InputFilter.LengthFilter(4); //Filter to 10 characters
            mPlayerOverView.setFilters(filters);
            if(mTeamScoreUpdateStatus != null) {
                switch (mTeamScoreUpdateStatus) {
                    case "0":
                        mPlayerOverView.setEnabled(true);
                        mPlayerMaidenView.setEnabled(true);
                        mPlayerRunView.setEnabled(true);
                        mPlayerWicketView.setEnabled(true);
                        mPlayerOverView.setSelectAllOnFocus(true);
                        mPlayerMaidenView.setSelectAllOnFocus(true);
                        mPlayerRunView.setSelectAllOnFocus(true);
                        mPlayerWicketView.setSelectAllOnFocus(true);
                        break;
                    case "1":
                    case "2":
                        mPlayerOverView.setEnabled(false);
                        mPlayerMaidenView.setEnabled(false);
                        mPlayerRunView.setEnabled(false);
                        mPlayerWicketView.setEnabled(false);
                        mPlayerOverView.setSelectAllOnFocus(false);
                        mPlayerMaidenView.setSelectAllOnFocus(false);
                        mPlayerRunView.setSelectAllOnFocus(false);
                        mPlayerWicketView.setSelectAllOnFocus(false);
                        break;
                    case "3":
                        mPlayerOverView.setEnabled(true);
                        mPlayerMaidenView.setEnabled(true);
                        mPlayerRunView.setEnabled(true);
                        mPlayerWicketView.setEnabled(true);
                        mPlayerOverView.setSelectAllOnFocus(true);
                        mPlayerMaidenView.setSelectAllOnFocus(true);
                        mPlayerRunView.setSelectAllOnFocus(true);
                        mPlayerWicketView.setSelectAllOnFocus(true);
                        break;
                }
            }

        }
    }
}
