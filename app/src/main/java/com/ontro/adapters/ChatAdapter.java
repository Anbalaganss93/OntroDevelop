package com.ontro.adapters;

import android.content.Context;
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
import com.ontro.dto.ChatUser;
import com.ontro.utils.CommonUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Android on 20-Feb-17.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<ChatUser> mChatUsers;
    private ChatAdapterListener mChatAdapterListener;
    private Context context;

    public ChatAdapter(FragmentActivity chatsActivity, ChatAdapterListener chatAdapterListener, List<ChatUser> firebaseAuthUsers) {
        this.context = chatsActivity;
        mChatAdapterListener = chatAdapterListener;
        mChatUsers = firebaseAuthUsers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_chat_layout, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ChatUser chatUser = mChatUsers.get(position);
        holder.mChatPersonNameTextView.setText(chatUser.getUserName());
        try {
            String lastChatDate = chatUser.getLastChatTime();
            String dateAndTime[] = lastChatDate.split(" ");
            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DefaultText.YEAR_MONTH_DATE);
            Date date = dateFormat.parse(dateAndTime[0]);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date today = calendar.getTime();
            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(Calendar.HOUR_OF_DAY, 0);
            calendar1.set(Calendar.MINUTE, 0);
            calendar1.set(Calendar.SECOND, 0);
            calendar1.set(Calendar.MILLISECOND, 0);
            calendar1.add(Calendar.DATE, -1);
            Date yesterday = calendar1.getTime();
            if(today.equals(date)) {
                holder.mLastChatTime.setText(Constants.DefaultText.TODAY + CommonUtils.convertTimeFormatIntoAmOrPmFormat(dateAndTime[1]));
            } else if(yesterday.equals(date)) {
                holder.mLastChatTime.setText(Constants.DefaultText.YESTERDAY + CommonUtils.convertTimeFormatIntoAmOrPmFormat(dateAndTime[1]));
            } else {
                DateFormat inputDateFormat = new SimpleDateFormat(Constants.DefaultText.DATE_AND_TIME);
                DateFormat outputDateFormat = new SimpleDateFormat(Constants.DefaultText.LETTER_DATE_AND_TIME);
                String updatedTime = outputDateFormat.format(inputDateFormat.parse(lastChatDate));
                holder.mLastChatTime.setText(updatedTime);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(!chatUser.getImageUrl().equals("")|| chatUser.getImageUrl() != null) {
            Glide.with(context).load(chatUser.getImageUrl()).placeholder(R.drawable.profiledefaultimg).dontAnimate().into(holder.mChatPersonImageView);
        } else {
            Glide.with(context).load(R.drawable.profiledefaultimg).dontAnimate().into(holder.mChatPersonImageView);
        }
        holder.mChatContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              mChatAdapterListener.onItemClicked(view, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mChatUsers != null) {
            return mChatUsers.size();
        }
        return 0;
    }

    public void add(ChatUser chatUser) {
        mChatUsers.add(chatUser);
        notifyItemInserted(mChatUsers.size() - 1);
    }

    public List<ChatUser>  getChatUsers() {
        return mChatUsers;
    }

    public void clear() {
        mChatUsers.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircularImageView mChatPersonImageView;
        private RelativeLayout mChatContainer;
        private TextView mChatPersonNameTextView, mLastChatTime;

        public ViewHolder(View itemView) {
            super(itemView);
            mChatPersonImageView = (CircularImageView) itemView.findViewById(R.id.adapter_chat_layout_civ_player_image);
            mChatPersonNameTextView = (TextView) itemView.findViewById(R.id.adapter_chat_layout_tv_chatter_name);
            mLastChatTime = (TextView) itemView.findViewById(R.id.adapter_chat_layout_tv_chat_time);
            mChatContainer = (RelativeLayout) itemView.findViewById(R.id.chat_container);
        }
    }

    public interface ChatAdapterListener {

        void onItemClicked(View view, int position);
    }
}
