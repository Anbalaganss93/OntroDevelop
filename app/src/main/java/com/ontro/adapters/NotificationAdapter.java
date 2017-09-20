package com.ontro.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ontro.R;
import com.ontro.dto.NotificationResponse;

import java.util.List;

/**
 * Created by IDEOMIND02 on 5/5/2017.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private List<NotificationResponse> mNotifications;

    public NotificationAdapter(List<NotificationResponse> notifications) {
        mNotifications = notifications;
    }

    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflater_notification_row_item, parent, false);
        return new NotificationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
        final NotificationResponse notificationResponse = mNotifications.get(position);
        holder.mNotificationTitleTextView.setText(notificationResponse.getTitle());
        holder.mNotificationDescriptionTextView.setText(notificationResponse.getContent());
        holder.mNotificationTimeTextView.setText(notificationResponse.getCreatedAt());
    }

    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView mNotificationTitleTextView, mNotificationDescriptionTextView, mNotificationTimeTextView;

        NotificationViewHolder(View itemView) {
            super(itemView);
            mNotificationTitleTextView = (TextView) itemView.findViewById(R.id.inflater_notification_row_item_title_tv);
            mNotificationDescriptionTextView = (TextView) itemView.findViewById(R.id.inflater_notification_row_item_description_tv);
            mNotificationTimeTextView = (TextView) itemView.findViewById(R.id.inflater_notification_row_item_time_tv);
        }
    }
}
