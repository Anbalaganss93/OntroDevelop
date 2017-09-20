package com.ontro.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.ontro.Constants;
import com.ontro.R;
import com.ontro.dto.Chat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by IDEOMIND02 on 16-06-2017.
 */

public class ChatRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_MINE = 1;
    private static final int VIEW_TYPE_OPPONENT = 2;
    private final Context mContext;
    private String mChatType;

    private List<Chat> mChats;

    public ChatRecyclerAdapter(Context context, List<Chat> chats) {
        mContext = context;
        mChats = chats;
    }

    public void add(Chat chat, String chatType) {
        mChats.add(chat);
        mChatType = chatType;
        notifyItemInserted(mChats.size() - 1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case VIEW_TYPE_MINE:
                View viewChatMine = layoutInflater.inflate(R.layout.inflater_chat_item_mine, parent, false);
                viewHolder = new MyChatViewHolder(viewChatMine);
                break;
            case VIEW_TYPE_OPPONENT:
                View viewChatOther = layoutInflater.inflate(R.layout.inflater_chat_item_opponent, parent, false);
                viewHolder = new OpponentChatViewHolder(viewChatOther);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (TextUtils.equals(mChats.get(position).getName(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {
            configureMyChatViewHolder((MyChatViewHolder) holder, position);
        } else {
            configureOtherChatViewHolder((OpponentChatViewHolder) holder, position);
        }
    }

    private void configureMyChatViewHolder(MyChatViewHolder myChatViewHolder, int position) {
        Chat chat = mChats.get(position);
        myChatViewHolder.txtChatMessage.setText(chat.getText());
        myChatViewHolder.txtChatTime.setText(getTimeFormat(chat.getDate()));
    }

    private String getTimeFormat(String date) {
        try {
            DateFormat inputDateFormat = new SimpleDateFormat(Constants.DefaultText.DATE_AND_TIME);
            DateFormat outputDateFormat = new SimpleDateFormat(Constants.DefaultText.TIME);
            return outputDateFormat.format(inputDateFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void configureOtherChatViewHolder(OpponentChatViewHolder otherChatViewHolder, int position) {
        Chat chat = mChats.get(position);
        otherChatViewHolder.txtChatMessage.setText(chat.getText());
        if(chat.getUserColor() != 0) {
            otherChatViewHolder.txtOpponentName.setTextColor(chat.getUserColor());
        } else {
            otherChatViewHolder.txtOpponentName.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        }
        otherChatViewHolder.txtOpponentName.setText(chat.getName());
        if(mChatType.equals(Constants.DefaultText.TEAM)) {
            otherChatViewHolder.txtOpponentName.setVisibility(View.VISIBLE);
        } else {
            otherChatViewHolder.txtOpponentName.setVisibility(View.GONE);
        }
        otherChatViewHolder.txtChatTime.setText(getTimeFormat(chat.getDate()));
    }

    @Override
    public int getItemCount() {
        if (mChats != null) {
            return mChats.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.equals(mChats.get(position).getName(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {
            return VIEW_TYPE_MINE;
        } else {
            return VIEW_TYPE_OPPONENT;
        }
    }

    public void clear() {
        mChats.clear();
        notifyDataSetChanged();
    }

    private static class MyChatViewHolder extends RecyclerView.ViewHolder {
        private TextView txtChatMessage, txtChatTime;

        public MyChatViewHolder(View itemView) {
            super(itemView);
            txtChatMessage = (TextView) itemView.findViewById(R.id.inflater_chat_item_mine_tv_message);
            txtChatTime = (TextView) itemView.findViewById(R.id.inflater_chat_item_mine_tv_time);
        }
    }

    private static class OpponentChatViewHolder extends RecyclerView.ViewHolder {
        private TextView txtChatMessage, txtOpponentName, txtChatTime;

        public OpponentChatViewHolder(View itemView) {
            super(itemView);
            txtChatMessage = (TextView) itemView.findViewById(R.id.inflater_chat_item_opponent_tv_message);
            txtOpponentName = (TextView) itemView.findViewById(R.id.inflater_chat_item_opponent_tv_name);
            txtChatTime = (TextView) itemView.findViewById(R.id.inflater_chat_item_opponent_tv_time);
        }
    }
}

