package com.ontro.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ontro.R;
import com.ontro.customui.ProfileImageView;
import com.ontro.dto.SquadInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IDEOMIND02 on 23-08-2017.
 */

public class SecondTeamSquadAdapter extends RecyclerView.Adapter<SecondTeamSquadAdapter.MatchSquadViewHolder> {
    private Context mContext;
    private List<SquadInfo> mSquadInfos = new ArrayList<>();

    public SecondTeamSquadAdapter(Context context, List<SquadInfo> squadInfos) {
        mContext = context;
        mSquadInfos = squadInfos;
    }

    @Override
    public MatchSquadViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflater_match_squad_item, parent, false);
        return new MatchSquadViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MatchSquadViewHolder holder, int position) {
        SquadInfo squadInfo = mSquadInfos.get(position);
        holder.mPlayerNameView.setText(squadInfo.getPlayerName());
        if (squadInfo.getPlayerPhoto() != null) {
            Glide.with(mContext).load(squadInfo.getPlayerPhoto()).placeholder(R.drawable.profiledefaultimg).dontAnimate().into(holder.mPlayerImageView);
        } else {
            Glide.with(mContext).load(R.drawable.profiledefaultimg).dontAnimate().into(holder.mPlayerImageView);
        }
    }

    @Override
    public int getItemCount() {
        return mSquadInfos.size();
    }

    public class MatchSquadViewHolder extends RecyclerView.ViewHolder {
        private ProfileImageView mPlayerImageView;
        private TextView mPlayerNameView;

        public MatchSquadViewHolder(View itemView) {
            super(itemView);
            mPlayerImageView = (ProfileImageView) itemView.findViewById(R.id.inflater_match_squad_item_iv_player);
            mPlayerNameView = (TextView) itemView.findViewById(R.id.inflater_match_squad_item_tv_player_name);
        }
    }
}
