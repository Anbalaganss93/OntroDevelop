package com.ontro.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ontro.Constants;
import com.ontro.R;
import com.ontro.dto.SquadInfo;
import com.ontro.utils.PreferenceHelper;

import java.util.List;

/**
 * Created by Android on 20-Feb-17.
 */

public class SquadsAdapter extends RecyclerView.Adapter<SquadsAdapter.ViewHolder> {
    private RecyclerViewItemClickListener itemClickListener;
    private List<SquadInfo> arrayList;
    private Context context;
    private DisplayMetrics dm;
    private String mTeamOwner;

    public SquadsAdapter(FragmentActivity activity, RecyclerViewItemClickListener itemClickListener, List<SquadInfo> arrayList, String teamOwner) {
        this.context = activity;
        this.arrayList = arrayList;
        this.itemClickListener = itemClickListener;
        mTeamOwner = teamOwner;
        dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflater_team_squad_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override

    public void onBindViewHolder(final ViewHolder holder, final int position) {
        RelativeLayout.LayoutParams img_params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        img_params.width = (int) (dm.widthPixels * 0.15);
        img_params.height = ((int) (dm.widthPixels * 0.15));
        holder.mPlayerProfileImage.setLayoutParams(img_params);
        holder.mPlayerProfileImage.setBorderWidth(0);
        holder.mPlayerProfileImage.setBorderColor(Color.parseColor("#0000FFFF"));
        PreferenceHelper preferenceHelper = new PreferenceHelper(context, Constants.APP_NAME, 0);
        final SquadInfo squadInfo = arrayList.get(position);
        String myId = preferenceHelper.getString("user_id", "");
        if (squadInfo.getPlayerId().equals(myId)) {
            holder.mPlayerNameTextView.setText(Constants.DefaultText.YOU);
        } else {
            holder.mPlayerNameTextView.setText(squadInfo.getPlayerName());
        }
        if (squadInfo.getPlayerId().equals(mTeamOwner)) {
            holder.mPlayerRoleTextView.setVisibility(View.VISIBLE);
            holder.mPlayerRoleTextView.setText(Constants.DefaultText.OWNER);
        } else {
            holder.mPlayerRoleTextView.setVisibility(View.GONE);
        }

        holder.mPlayerLocationTextView.setText(squadInfo.getPlayerLocation());
        if(squadInfo.getPlayerPhoto() != null) {
            Glide.with(context).load(squadInfo.getPlayerPhoto()).placeholder(R.drawable.profiledefaultimg).dontAnimate().into(holder.mPlayerProfileImage);
        } else {
            Glide.with(context).load(R.drawable.profiledefaultimg).into(holder.mPlayerProfileImage);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClickListIem(squadInfo, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public interface RecyclerViewItemClickListener {

        void onClickListIem(SquadInfo squadInfo, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircularImageView mPlayerProfileImage;
        private TextView mPlayerNameTextView, mPlayerLocationTextView, mPlayerRoleTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mPlayerProfileImage = (CircularImageView) itemView.findViewById(R.id.inflater_team_squad_item_iv_player);
            mPlayerNameTextView = (TextView) itemView.findViewById(R.id.inflater_team_squad_item_tv_player_name);
            mPlayerLocationTextView = (TextView) itemView.findViewById(R.id.inflater_team_squad_item_tv_player_location);
            mPlayerRoleTextView = (TextView) itemView.findViewById(R.id.inflater_team_squad_item_tv_player_role);

        }
    }
}
