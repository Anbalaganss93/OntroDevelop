package com.ontro.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ontro.Constants;
import com.ontro.R;
import com.ontro.dto.ExploreModel;
import com.ontro.dto.PlayerInviteStatus;
import com.ontro.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Android on 27-Apr-17.
 */

public class ExplorePlayerListAdapter extends RecyclerView.Adapter<ExplorePlayerListAdapter.ViewHolder> {
    private ArrayList<ExploreModel> arrayList;
    private Context context;
    private DisplayMetrics dm;
    private String mTeamId, mPlayerInviteId;
    private ExplorePlayerListListener mPlayerListListener;

    public ExplorePlayerListAdapter(FragmentActivity activity, ArrayList<ExploreModel> arrayList,
                                    ExplorePlayerListListener playerListListener, String teamId) {
        this.context = activity;
        this.arrayList = arrayList;
        dm = new DisplayMetrics();
        mPlayerListListener = playerListListener;
        mTeamId = teamId;
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflater_explore_player_list_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        RelativeLayout.LayoutParams img_params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        img_params.width = (int) (dm.widthPixels * 0.15);
        img_params.height = ((int) (dm.widthPixels * 0.15));
        holder.mPlayerImageView.setLayoutParams(img_params);

        final ExploreModel exploreModel = arrayList.get(holder.getAdapterPosition());
        String playerImage = exploreModel.getExploreImage();
        holder.mPlayerNameView.setText(exploreModel.getExploreName());
        holder.mPlayerSportTypeView.setText(CommonUtils.sportNameCheck(exploreModel.getExploreSport()));
        holder.mPlayerLocationView.setText(exploreModel.getExploreLocation());
        if (!playerImage.equals("") && playerImage != null) {
            Glide.with(context).load(exploreModel.getExploreImage()).placeholder(R.drawable.profiledefaultimg).dontAnimate().into(holder.mPlayerImageView);
        } else {
            Glide.with(context).load(R.drawable.profiledefaultimg).dontAnimate().into(holder.mPlayerImageView);
        }
        List<PlayerInviteStatus> playerInviteStatuses = exploreModel.getInviteStatuses();
        int count  = 0;
        label:
        for (int i = 0; i < playerInviteStatuses.size(); i++) {
            PlayerInviteStatus inviteStatus = playerInviteStatuses.get(i);
            if (mTeamId.equals(inviteStatus.getTeamId())) {
                switch (inviteStatus.getInviteStatus()) {
                    case Constants.DefaultText.ONE:
                        holder.mPlayerExistView.setVisibility(View.VISIBLE);
                        holder.mPlayerInviteOrCancelView.setVisibility(View.GONE);
                        break label;
                    case Constants.DefaultText.ZERO:
                        holder.mPlayerExistView.setVisibility(View.GONE);
                        holder.mPlayerInviteOrCancelView.setVisibility(View.VISIBLE);
                        holder.mPlayerInviteOrCancelView.setText(context.getResources().getString(R.string.cancel));
                        holder.mPlayerInviteOrCancelView.setTextColor(ContextCompat.getColor(context, R.color.player_invite_cancel_color));
                        holder.mPlayerInviteOrCancelView.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_player_invite_cancel));
                        mPlayerInviteId = inviteStatus.getInviteId();
                        break label;
                    default:
                        holder.mPlayerExistView.setVisibility(View.GONE);
                        holder.mPlayerInviteOrCancelView.setVisibility(View.VISIBLE);
                        holder.mPlayerInviteOrCancelView.setText(context.getResources().getString(R.string.invite));
                        holder.mPlayerInviteOrCancelView.setTextColor(ContextCompat.getColor(context, R.color.white));
                        holder.mPlayerInviteOrCancelView.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_player_invite));
                        break;
                }
            } else {
                count++;
            }
        }

        if(count ==  playerInviteStatuses.size()) {
            holder.mPlayerExistView.setVisibility(View.GONE);
            holder.mPlayerInviteOrCancelView.setVisibility(View.VISIBLE);
            holder.mPlayerInviteOrCancelView.setText(context.getResources().getString(R.string.invite));
            holder.mPlayerInviteOrCancelView.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.mPlayerInviteOrCancelView.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_player_invite));
        }

        holder.mPlayerInviteOrCancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.mPlayerInviteOrCancelView.getText().equals(context.getResources().getString(R.string.invite))) {
                    if(mPlayerListListener != null) {
                        mPlayerListListener.invitePlayerToTeam(exploreModel, mTeamId);
                    }
                } else {
                    if(mPlayerListListener != null) {
                        mPlayerListListener.cancelPlayerInviteRequest(mPlayerInviteId);
                    }
                }
            }
        });

        holder.mPlayerListItemContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPlayerListListener != null) {
                    mPlayerListListener.showPlayerQuickViewDialog(exploreModel, mTeamId);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void add(ExploreModel category) {
        insert(category, arrayList.size());
    }

    private void insert(ExploreModel category, int count) {
        arrayList.add(count, category);
        notifyItemInserted(count);
    }

    public ArrayList<ExploreModel> getlist() {
        return arrayList;
    }

    public void remove(int position) {
        arrayList.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        int size = arrayList.size();
        arrayList.clear();
        notifyItemRangeRemoved(0, size);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mPlayerNameView, mPlayerLocationView, mPlayerSportTypeView, mPlayerInviteOrCancelView;
        private CircularImageView mPlayerImageView;
        private ImageView mPlayerExistView;
        private LinearLayout mPlayerListItemContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            mPlayerImageView = (CircularImageView) itemView.findViewById(R.id.inflater_explore_player_list_item_iv_player);
            mPlayerNameView = (TextView) itemView.findViewById(R.id.inflater_explore_player_list_item_tv_name);
            mPlayerLocationView = (TextView) itemView.findViewById(R.id.inflater_explore_player_list_item_tv_location);
            mPlayerInviteOrCancelView = (TextView) itemView.findViewById(R.id.inflater_explore_player_list_item_tv_player_invite_or_cancel);
            mPlayerSportTypeView = (TextView) itemView.findViewById(R.id.inflater_explore_player_list_item_tv_sport);
            mPlayerListItemContainer = (LinearLayout) itemView.findViewById(R.id.inflater_explore_player_list_item_ll_container);
            mPlayerExistView = (ImageView) itemView.findViewById(R.id.inflater_explore_player_list_item_iv_player_exist);
        }
    }

    public interface ExplorePlayerListListener {

        void invitePlayerToTeam(ExploreModel exploreModel, String teamId);

        void cancelPlayerInviteRequest(String inviteId);

        void showPlayerQuickViewDialog(ExploreModel exploreModel, String teamId);
    }
}


