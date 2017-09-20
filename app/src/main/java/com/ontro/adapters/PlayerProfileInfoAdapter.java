package com.ontro.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ontro.R;
import com.ontro.dto.PlayerPersonalSport;
import com.ontro.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IDEOMIND02 on 28-08-2017.
 */

public class PlayerProfileInfoAdapter extends RecyclerView.Adapter<PlayerProfileInfoAdapter.PlayerSportViewHolder>{
    private List<PlayerPersonalSport> mPlayerPersonalSports = new ArrayList<>();

    public PlayerProfileInfoAdapter(Context context, List<PlayerPersonalSport> personalSports) {
        mPlayerPersonalSports = personalSports;
        Context mContext = context;
    }

    @Override
    public PlayerSportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflater_player_profile_sport_info_item, parent, false);
        return new PlayerSportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlayerSportViewHolder holder, int position) {
        PlayerPersonalSport playerPersonalSport = mPlayerPersonalSports.get(position);
        String handedness = playerPersonalSport.getHandedness();
        String playerPosition = playerPersonalSport.getPosition();
        if (handedness.equals("null") || handedness.equals("")) {
            holder.mPlayerHandnessOrPositionView.setText(playerPosition);
        } else if (playerPosition.equals("null") || playerPosition.equals("")) {
            holder.mPlayerHandnessOrPositionView.setText(handedness);
        }
        int selectedSports = mPlayerPersonalSports.size();
        if(((selectedSports - 1) == position) || ((selectedSports - 2) == position )) {
            holder.mPlayerSportFooterView.setVisibility(View.GONE);
        } else {
            holder.mPlayerSportFooterView.setVisibility(View.VISIBLE);
        }
        holder.mPlayerSportImageView.setImageResource(CommonUtils.scoreUpdateSport(String.valueOf(playerPersonalSport.getSport())));
    }

    @Override
    public int getItemCount() {
        return mPlayerPersonalSports.size();
    }

    class PlayerSportViewHolder extends RecyclerView.ViewHolder {
        private TextView mPlayerHandnessOrPositionView;
        private ImageView mPlayerSportImageView;
        private View mPlayerSportFooterView;

        PlayerSportViewHolder(View itemView) {
            super(itemView);
            mPlayerHandnessOrPositionView = (TextView) itemView.findViewById(R.id.inflater_player_profile_sport_info_item_tv);
            mPlayerSportImageView = (ImageView) itemView.findViewById(R.id.inflater_player_profile_sport_info_item_iv);
            mPlayerSportFooterView = itemView.findViewById(R.id.inflater_player_profile_sport_info_item_view);
        }
    }
}
