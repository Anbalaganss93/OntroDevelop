package com.ontro.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ontro.R;
import com.ontro.dto.ExploreModel;
import com.ontro.utils.CommonUtils;

import java.util.ArrayList;

/**
 * Created by Android on 20-Feb-17.
 */

public class ExplorePlayerAdapter extends RecyclerView.Adapter<ExplorePlayerAdapter.ViewHolder> {
    private ArrayList<ExploreModel> arrayList;
    private Context context;
    private DisplayMetrics dm;

    public ExplorePlayerAdapter(FragmentActivity exploreActivity, ArrayList<ExploreModel> arrayList) {
        this.context = exploreActivity;
        this.arrayList = arrayList;
        dm = new DisplayMetrics();
        exploreActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_explore_layout, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
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


    @Override
    public void onBindViewHolder(final ViewHolder holder,int position) {

        RelativeLayout.LayoutParams img_params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        img_params.width = (int) (dm.widthPixels * 0.15);
        img_params.height = ((int) (dm.widthPixels * 0.15));
        holder.explore_image.setLayoutParams(img_params);

        RelativeLayout.LayoutParams explore_batch_params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        explore_batch_params.width = (int) (dm.widthPixels * 0.12);
        explore_batch_params.height = ((int) (dm.widthPixels * 0.12));
        explore_batch_params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        explore_batch_params.addRule(RelativeLayout.CENTER_VERTICAL);
        holder.explore_batch.setLayoutParams(explore_batch_params);

        ExploreModel m = arrayList.get(position);
        if(m.getExploreImage() != null && !m.getExploreImage().isEmpty()) {
            Glide.with(context).load(m.getExploreImage()).placeholder(R.drawable.profiledefaultimg).dontAnimate().into(holder.explore_image);
        } else {
            Glide.with(context).load(R.drawable.profiledefaultimg).dontAnimate().into(holder.explore_image);
        }

        holder.explore_name.setText(m.getExploreName());
        holder.explore_sport.setText(CommonUtils.sportNameCheck(m.getExploreSport()));
        holder.explore_location.setText(m.getExploreLocation());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView explore_name, explore_location,explore_sport;
        private ImageView explore_batch;
        private CircularImageView explore_image;
        private RelativeLayout explore_container;

        public ViewHolder(View itemView) {
            super(itemView);
            explore_image = (CircularImageView) itemView.findViewById(R.id.explore_image);
            explore_batch = (ImageView) itemView.findViewById(R.id.explore_batch);
            explore_name = (TextView) itemView.findViewById(R.id.explore_name);
            explore_sport = (TextView) itemView.findViewById(R.id.explore_sport);
            explore_location = (TextView) itemView.findViewById(R.id.explore_location);
            explore_container = (RelativeLayout) itemView.findViewById(R.id.explore_container);
        }
    }
}
