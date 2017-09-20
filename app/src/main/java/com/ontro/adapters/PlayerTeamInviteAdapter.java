package com.ontro.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ontro.Constants;
import com.ontro.R;
import com.ontro.dto.SportModel;
import com.ontro.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IDEOMIND02 on 03-06-2017.
 */

public class PlayerTeamInviteAdapter extends RecyclerView.Adapter<PlayerTeamInviteAdapter.PlayerInviteViewHolder> {
    private Context mContext;
    private List<SportModel> mSportModels = new ArrayList<>();
    private PlayerInviteListener mPlayerInviteListener;

    public PlayerTeamInviteAdapter(Context context, PlayerInviteListener playerInviteListener, List<SportModel> sportModels) {
        mContext = context;
        mPlayerInviteListener = playerInviteListener;
        mSportModels = sportModels;
    }

    @Override
    public PlayerTeamInviteAdapter.PlayerInviteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflater_player_invite_team_row_item, parent, false);
        return new PlayerInviteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PlayerTeamInviteAdapter.PlayerInviteViewHolder holder, final int position) {
        final SportModel sportModel = mSportModels.get(position);
        holder.mTeamNameTextView.setText(sportModel.getSportname());
        holder.mBaseRelativeLayout.setTag(sportModel.getSportid());
        holder.mTeamLocationTextView.setText(sportModel.getLocation());
        Glide.with(mContext).load(sportModel.getTeamlogo()).placeholder(R.drawable.profiledefaultimg).dontAnimate().into(holder.mTeamImageView);

        holder.mBaseRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int isOwner = sportModel.getIswoner();
                if(mPlayerInviteListener != null) {
                    if (isOwner == 1) {
                        if (CommonUtils.isNetworkAvailable(mContext)) {
                            String teamId = (String) v.getTag();
                            mPlayerInviteListener.inviteToTeam(teamId, sportModel.getSportname());
                        } else {
                            Toast.makeText(mContext, Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mContext, "You are not owner of this team", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mSportModels.size();
    }

    public class PlayerInviteViewHolder extends RecyclerView.ViewHolder  {
        private TextView mTeamNameTextView, mTeamLocationTextView;
        private CircularImageView mTeamImageView;
        private RelativeLayout mBaseRelativeLayout;

        public PlayerInviteViewHolder(View itemView) {
            super(itemView);
            mTeamNameTextView = (TextView) itemView.findViewById(R.id.inflater_player_invite_team_row_item_tv_name);
            mTeamLocationTextView = (TextView) itemView.findViewById(R.id.inflater_player_invite_team_row_item_tv_location);
            mTeamImageView = (CircularImageView) itemView.findViewById(R.id.inflater_player_invite_team_row_item_iv_team);
            mBaseRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.inflater_player_invite_team_row_item_rv);
        }
    }

    public interface PlayerInviteListener {

        void inviteToTeam(String teamId, String teamName);
    }
}
