package com.ontro.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.ontro.Constants;
import com.ontro.R;
import com.ontro.dto.PlayerInviteResponse;
import com.ontro.utils.PreferenceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IDEOMIND02 on 03-06-2017.
 */

public class PlayerInviteAcceptAdapter extends RecyclerView.Adapter<PlayerInviteAcceptAdapter.PlayerInviteAcceptViewHolder> {
    private final PreferenceHelper preferenceHelper;
    private final MixpanelAPI mMixpanel;
    private PlayerInviteAcceptListener mPlayerInviteAcceptListener;
    private List<PlayerInviteResponse> mPlayerInviteResponses = new ArrayList<>();
    private Context mContext;

    public PlayerInviteAcceptAdapter(Context context, PlayerInviteAcceptListener playerInviteAcceptListener, List<PlayerInviteResponse> playerInviteResponses) {
        mMixpanel = MixpanelAPI.getInstance(context, context.getResources().getString(R.string.mixpanel_token));
        preferenceHelper = new PreferenceHelper(context, Constants.APP_NAME, 0);
        mPlayerInviteAcceptListener = playerInviteAcceptListener;
        mPlayerInviteResponses = playerInviteResponses;
        mContext = context;
    }

    @Override
    public PlayerInviteAcceptViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflater_player_invite_request_approval_row_item, parent, false);
        return new PlayerInviteAcceptViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PlayerInviteAcceptViewHolder holder, int position) {
        final PlayerInviteResponse playerInviteResponse = mPlayerInviteResponses.get(position);
        holder.mTeamNameTextView.setText(playerInviteResponse.getTeamName());
        holder.mInviteRequestTimeView.setText(playerInviteResponse.getCreatedAt());
        holder.mRequestAcceptView.setEnabled(true);
        holder.mRequestCancelView.setEnabled(true);
        Glide.with(mContext).load(playerInviteResponse.getTeamLogo()).placeholder(R.drawable.profiledefaultimg).dontAnimate().into(holder.mTeamImageView);

        holder.mRequestAcceptView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject eventJsonObject = new JSONObject();
                    eventJsonObject.put("UserId", preferenceHelper.getString("user_id", ""));
                    eventJsonObject.put("UserName",  preferenceHelper.getString("user_name", ""));
                    eventJsonObject.put("InvitedTeam", playerInviteResponse.getTeamName());
                    eventJsonObject.put("TeamSport", playerInviteResponse.getTeamSport());
                    eventJsonObject.put("status", "Accepted");
                    mMixpanel.track("PlayerInviteApproval", eventJsonObject);
                } catch (JSONException e) {
                    Log.e("Ontro", "Unable to add properties to JSONObject", e);
                }
                mPlayerInviteAcceptListener.onRequestAcceptClicked(playerInviteResponse, holder.getAdapterPosition());
                holder.mRequestAcceptView.setEnabled(false);
            }
        });

        holder.mRequestCancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject eventJsonObject = new JSONObject();
                    eventJsonObject.put("UserId", preferenceHelper.getString("user_id", ""));
                    eventJsonObject.put("UserName",  preferenceHelper.getString("user_name", ""));
                    eventJsonObject.put("InvitedTeam", playerInviteResponse.getTeamName());
                    eventJsonObject.put("status", "Rejected");
                    mMixpanel.track("PlayerInviteApproval", eventJsonObject);
                } catch (JSONException e) {
                    Log.e("Ontro", "Unable to add properties to JSONObject", e);
                }
                mPlayerInviteAcceptListener.onRequestCancelClicked(playerInviteResponse, holder.getAdapterPosition());
                holder.mRequestCancelView.setEnabled(false);
            }
        });

        holder.mTeamNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayerInviteAcceptListener.onItemViewClicked(playerInviteResponse, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPlayerInviteResponses.size();
    }

    public class PlayerInviteAcceptViewHolder extends RecyclerView.ViewHolder {
        private TextView mTeamNameTextView, mInviteRequestTimeView;
        private ImageView mRequestAcceptView, mRequestCancelView;
        private CircularImageView mTeamImageView;

        public PlayerInviteAcceptViewHolder(View itemView) {
            super(itemView);
            mTeamNameTextView = (TextView) itemView.findViewById(R.id.inflater_player_invite_request_approval_row_item_tv_name);
            mRequestAcceptView = (ImageView) itemView.findViewById(R.id.inflater_player_invite_request_approval_row_item_iv_accept);
            mRequestCancelView = (ImageView) itemView.findViewById(R.id.inflater_player_invite_request_approval_row_item_iv_cancel);
            mTeamImageView = (CircularImageView) itemView.findViewById(R.id.inflater_player_invite_request_approval_row_item_iv_team);
            mInviteRequestTimeView = (TextView) itemView.findViewById(R.id.inflater_player_invite_request_approval_row_item_tv_time);
        }
    }

    public interface PlayerInviteAcceptListener {

        void onRequestAcceptClicked(PlayerInviteResponse playerInviteResponse, int position);

        void onRequestCancelClicked(PlayerInviteResponse playerInviteResponse, int position);

        void onItemViewClicked(PlayerInviteResponse playerInviteResponse, int position);
    }
}
