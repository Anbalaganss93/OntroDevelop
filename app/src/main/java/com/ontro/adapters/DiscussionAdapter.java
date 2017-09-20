package com.ontro.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ontro.Constants;
import com.ontro.DiscussionsActivity;
import com.ontro.PlayerProfileActivity;
import com.ontro.R;
import com.ontro.dto.DiscussionModel;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;

/**
 * Created by umm on 20-Feb-17.
 */

public class DiscussionAdapter extends RecyclerView.Adapter<DiscussionAdapter.ViewHolder> {
    private ArrayList<DiscussionModel> arrayList;
    private Context context;
    private DisplayMetrics dm;

    public DiscussionAdapter(FragmentActivity activity, ArrayList<DiscussionModel> arrayList) {
        this.context = activity;
        this.arrayList = arrayList;
        dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_discussion_layout, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        RelativeLayout.LayoutParams img_params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        img_params.width = (int) (dm.widthPixels * 0.12);
        img_params.height = ((int) (dm.widthPixels * 0.12));
        img_params.addRule(RelativeLayout.ALIGN_PARENT_LEFT | RelativeLayout.CENTER_VERTICAL);
        holder.discussion_image.setLayoutParams(img_params);

        final DiscussionModel discussionModel = arrayList.get(holder.getAdapterPosition());
        if (discussionModel.getUser_image() != null && !discussionModel.getUser_image().isEmpty()) {
            Glide.with(context).load(discussionModel.getUser_image()).placeholder(R.drawable.profiledefaultimg).dontAnimate().into(holder.discussion_image);
        } else {
            Glide.with(context).load(R.drawable.profiledefaultimg).dontAnimate().into(holder.discussion_image);
        }

        holder.name.setText(discussionModel.getUser_name());
        String messageTitle = discussionModel.getUser_question();
        holder.seen_hours.setText(discussionModel.getUser_seen_hours());
        String userComment = discussionModel.getUser_comment();
        try {
            holder.title.setText(StringEscapeUtils.unescapeJava(messageTitle));
            holder.user_comment.setText(StringEscapeUtils.unescapeJava(userComment));
        } catch (Exception e) {
            e.printStackTrace();
        }

        int commentcount = Integer.parseInt(discussionModel.getComment_count());
        String subadd = commentcount > 1 ? "Comments" : "Comment";
        holder.total_comment.setText(discussionModel.getComment_count() + " " + subadd);
        if (discussionModel.getIscommented().equals("0")) {
            holder.total_comment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.message, 0, 0, 0);
        } else {
            holder.total_comment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.message_red, 0, 0, 0);
        }

        holder.discussion_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DiscussionModel discussionModel = arrayList.get(holder.getAdapterPosition());
                Intent intent = new Intent(context, DiscussionsActivity.class);
                intent.putExtra("player_id", discussionModel.getPlayer_id());
                intent.putExtra("title", discussionModel.getUser_question());
                intent.putExtra("name", discussionModel.getUser_name());
                intent.putExtra("timeago", discussionModel.getUser_seen_hours());
                intent.putExtra("comment", discussionModel.getUser_comment());
                intent.putExtra("comment_total", discussionModel.getComment_count());
                intent.putExtra("image", discussionModel.getUser_image());
                intent.putExtra("discussion_id", discussionModel.getDiscussionid());
                context.startActivity(intent);
                ((Activity) context).finish();
            }
        });

        holder.discussion_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PlayerProfileActivity.class);
                intent.putExtra(Constants.BundleKeys.PLAYER_ID, discussionModel.getPlayer_id());
                context.startActivity(intent);
            }
        });
    }

    public String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title, name, seen_hours, user_comment, total_comment;
        private CircularImageView discussion_image;
        private LinearLayout discussion_container;

        public ViewHolder(View itemView) {
            super(itemView);
            discussion_container = (LinearLayout) itemView.findViewById(R.id.discussion_container);
            discussion_image = (CircularImageView) itemView.findViewById(R.id.discussion_image);
            title = (TextView) itemView.findViewById(R.id.title);
            name = (TextView) itemView.findViewById(R.id.name);
            seen_hours = (TextView) itemView.findViewById(R.id.seen_hours);
            user_comment = (TextView) itemView.findViewById(R.id.user_comment);
            total_comment = (TextView) itemView.findViewById(R.id.total_comment);
        }
    }
}
