package com.ontro.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ontro.Constants;
import com.ontro.R;
import com.ontro.SharedObjects;
import com.ontro.TeamDetailActivity;
import com.ontro.dto.SportModel;
import com.ontro.utils.PreferenceHelper;

import java.util.ArrayList;

/**
 * Created by Android on 20-Feb-17.
 */

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.ViewHolder> {
    private ArrayList<SportModel> arrayList;
    private Context context;
    private DisplayMetrics dm;
    private PreferenceHelper preferenceHelper;

    public TeamAdapter(FragmentActivity activity, ArrayList<SportModel> arrayList) {
        this.context = activity;
        this.arrayList = arrayList;
        dm = new DisplayMetrics();
        preferenceHelper = new PreferenceHelper(activity, Constants.APP_NAME, 0);
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_team_layout, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final SportModel sportModel = arrayList.get(position);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params2.width = (int) ((dm.widthPixels * 0.42));
        params2.setMargins(10, 5, 10, 10);
        holder.sportcontainer.setLayoutParams(params2);

        LinearLayout.LayoutParams textparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textparam.setMargins((int) (dm.widthPixels * 0.05), (int) (dm.heightPixels * 0.02), (int) (dm.widthPixels * 0.05), 0);
        textparam.gravity = Gravity.CENTER;
        holder.sport_name.setLayoutParams(textparam);

        FrameLayout.LayoutParams img_params2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        img_params2.width = (int) (dm.widthPixels * 0.17);
        img_params2.height = ((int) (dm.widthPixels * 0.17));
        img_params2.setMargins(0, (int) (dm.heightPixels * 0.01), 0, 0);
        holder.circleimage.setLayoutParams(img_params2);
        holder.circleimage.bringToFront();

        FrameLayout.LayoutParams img_params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        img_params.width = (int) (dm.widthPixels * 0.16);
        img_params.height = ((int) (dm.widthPixels * 0.16));
        img_params.setMargins(0, (int) (dm.heightPixels * 0.005), 0, 0);
        holder.sport_image.setLayoutParams(img_params);
        holder.sport_image.setBorderColor(Color.parseColor("#1E2730"));
        holder.sport_image.setBorderWidth(5);

        RelativeLayout.LayoutParams left_params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        left_params.width = (int) (dm.widthPixels * 0.13);
        left_params.height = ((int) (dm.widthPixels * 0.13));
        left_params.setMargins(10, 0, 0, 0);
        left_params.addRule(RelativeLayout.ALIGN_PARENT_LEFT | RelativeLayout.CENTER_VERTICAL);
        holder.left_side.setLayoutParams(left_params);

        RelativeLayout.LayoutParams right_side_params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        right_side_params.width = (int) (dm.widthPixels * 0.13);
        right_side_params.height = ((int) (dm.widthPixels * 0.13));
        right_side_params.setMargins(0, 0, 10, 0);
        right_side_params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        holder.right_side.setLayoutParams(right_side_params);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.progress_bar.setProgressTintList(ColorStateList.valueOf(Color.RED));
        } else {
            holder.progress_bar.getProgressDrawable().setColorFilter(
                    Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
        }

        holder.right_side.setImageResource(R.drawable.badge1);

        if(sportModel.getSportimage() != 0) {
            Glide.with(context).load(sportModel.getSportimage()).dontAnimate().into(holder.left_side);
        }
        if(!sportModel.getTeamlogo().isEmpty()) {
            Glide.with(context).load(sportModel.getTeamlogo()).placeholder(R.drawable.profiledefaultimg).dontAnimate().into(holder.sport_image);
        } else {
            Glide.with(context).load(R.drawable.profiledefaultimg).dontAnimate().into(holder.sport_image);
        }
        holder.sport_name.setText(sportModel.getSportname());
        String progressLevel = sportModel.getProgress_percent().equals("null") ? "0" : sportModel.getProgress_percent();
        int progressPercentage = Integer.parseInt(progressLevel) * 20 ;
        holder.progress_bar.setProgress(progressPercentage);

        holder.sportcontainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedObjects.id = arrayList.get(holder.getAdapterPosition()).getSportid();
                preferenceHelper.save("Myteam", "0");
                Intent intent = new Intent(context, TeamDetailActivity.class);
                intent.putExtra(Constants.BundleKeys.IS_OWNER, String.valueOf(sportModel.getIswoner()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView sport_name;
        private ImageView left_side, right_side;
        private CircularImageView sport_image,circleimage;
        private RelativeLayout sportcontainer;
        private ProgressBar progress_bar;

        public ViewHolder(View itemView) {
            super(itemView);
            sportcontainer = (RelativeLayout) itemView.findViewById(R.id.sportcontainer);
            sport_image = (CircularImageView) itemView.findViewById(R.id.sport_image);
            circleimage = (CircularImageView) itemView.findViewById(R.id.circleimage);
            left_side = (ImageView) itemView.findViewById(R.id.left_side);
            right_side = (ImageView) itemView.findViewById(R.id.right_side);
            sport_name = (TextView) itemView.findViewById(R.id.sport_name);
            progress_bar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
        }
    }
}
