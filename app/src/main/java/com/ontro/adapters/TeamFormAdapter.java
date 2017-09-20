package com.ontro.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ontro.R;
import com.ontro.dto.ScheduleTeamFormModel;
import com.ontro.fragments.ScheduleTeamFormFragment;

import java.util.ArrayList;

/**
 * Created by Android on 20-Feb-17.
 */

public class TeamFormAdapter extends RecyclerView.Adapter<TeamFormAdapter.ViewHolder> {
    private ArrayList<ScheduleTeamFormModel> arrayList;
    private Context context;
    private DisplayMetrics dm;

    public TeamFormAdapter(FragmentActivity activity, ArrayList<ScheduleTeamFormModel> arrayList) {
        this.context = activity;
        this.arrayList = arrayList;
        dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_teamform_layout, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ScheduleTeamFormModel m = ScheduleTeamFormFragment.arrayList.get(holder.getAdapterPosition());
        RelativeLayout.LayoutParams img_params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        img_params.width = (int) (dm.widthPixels * 0.15);
        img_params.height = ((int) (dm.widthPixels * 0.15));
        holder.mSquadimage.setLayoutParams(img_params);

        holder.mSquadimage.setBorderWidth(0);
        holder.mSquadimage.setBorderColor(Color.parseColor("#0000FFFF"));

        Glide.with(context).load(m.getPlayer_photo()).placeholder(R.drawable.profiledefaultimg).dontAnimate().into(holder.mSquadimage);
        holder.mSquadname.setText(m.getPlayer_name());
        holder.mSquadlocation.setText(m.getPlayer_location());

        if (m.ischecked()) {
            holder.mCheckbox.setChecked(true);
        } else {
            holder.mCheckbox.setChecked(false);
        }

        holder.mCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (m.ischecked()) {
                    m.setIschecked(false);
                } else {
                    m.setIschecked(true);
                }
                notifyDataSetChanged();
            }

        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircularImageView mSquadimage;
        private CheckBox mCheckbox;
        private TextView mSquadname, mSquadlocation;

        public ViewHolder(View itemView) {
            super(itemView);
            mSquadimage = (CircularImageView) itemView.findViewById(R.id.adapter_teamform_tv_squadimage);
            mCheckbox = (CheckBox) itemView.findViewById(R.id.adapter_teamform_cb_mCheckbox);
            mSquadname = (TextView) itemView.findViewById(R.id.adapter_teamform_tv_squadname);
            mSquadlocation = (TextView) itemView.findViewById(R.id.adapter_teamform_tv_squadstatus);
        }
    }
}
