package com.ontro.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daimajia.swipe.SwipeLayout;
import com.ontro.Constants;
import com.ontro.R;
import com.ontro.customui.ProfileImageView;
import com.ontro.dto.MatchRequestModel;
import com.ontro.utils.CommonUtils;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * Created by IDEOMIND02 on 08-08-2017.
 */

public class MatchRequestSectionAdapter extends StatelessSection {
    private String mHeaderTitle;
    private List<MatchRequestModel> mMatchRequestModels;
    private int mOpenItem;
    private Context mContext;
    private MatchRequestItemListener mMatchRequestItemListener;

    public MatchRequestSectionAdapter(String headerTitle, List<MatchRequestModel> matchRequestModels, Context context, MatchRequestItemListener matchRequestItemListener) {
        super(new SectionParameters.Builder(R.layout.inflater_match_request_item)
                .headerResourceId(R.layout.inflater_match_request_header)
                .build());
        mHeaderTitle = headerTitle;
        mMatchRequestModels = matchRequestModels;
        mContext = context;
        mMatchRequestItemListener = matchRequestItemListener;
        mOpenItem = -1;
    }

    @Override
    public int getContentItemsTotal() {
        return mMatchRequestModels.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new MatchRequestItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
        MatchRequestItemViewHolder itemViewHolder = (MatchRequestItemViewHolder) holder;
        final MatchRequestModel matchRequestModel = mMatchRequestModels.get(position);
        itemViewHolder.mMatchSportTypeImageView.setImageResource(CommonUtils.scoreUpdateSport(matchRequestModel.getTeamSport()));
        itemViewHolder.mSwipeLayout.setTag(position);
        itemViewHolder.mSwipeLayout.addDrag(SwipeLayout.DragEdge.Left, itemViewHolder.mSwipeRejectLinearLayout);
        itemViewHolder.mSwipeLayout.addDrag(SwipeLayout.DragEdge.Right, itemViewHolder.mSwipeAcceptLinearLayout);
        itemViewHolder.mMatchRequestDateView.setText(matchRequestModel.getRequestDate());

        if (matchRequestModel.getBookingDate() != null) {
            if(!matchRequestModel.getBookingDate().isEmpty()) {
                itemViewHolder.mMatchTimeView.setVisibility(View.VISIBLE);
                itemViewHolder.mMatchDateView.setText(matchRequestModel.getBookingDate());
                itemViewHolder.mMatchTimeView.setText(CommonUtils.convertTimeFormatIntoAmOrPmFormat(matchRequestModel.getFromTime()) + Constants.DefaultText.HYPHEN
                        + CommonUtils.convertTimeFormatIntoAmOrPmFormat(matchRequestModel.getToTime()));
            } else {
                if(matchRequestModel.getMatchLocation() != null) {
                    itemViewHolder.mMatchTimeView.setVisibility(View.VISIBLE);
                    itemViewHolder.mMatchTimeView.setText(matchRequestModel.getMatchLocation());
                } else {

                    itemViewHolder.mMatchTimeView.setVisibility(View.GONE);
                }
                itemViewHolder.mMatchDateView.setText(matchRequestModel.getMatchDate());
            }
        } else {
            if(matchRequestModel.getMatchLocation() != null) {
                itemViewHolder.mMatchTimeView.setVisibility(View.VISIBLE);
                itemViewHolder.mMatchTimeView.setText(matchRequestModel.getMatchLocation());
            } else {
                itemViewHolder.mMatchTimeView.setVisibility(View.GONE);
            }
            itemViewHolder.mMatchDateView.setText(matchRequestModel.getMatchDate());
        }
        itemViewHolder.mMyTeamNameView.setText(matchRequestModel.getMyTeamName());
        String myTeamLogo = matchRequestModel.getMyTeamLogo();
        if (myTeamLogo != null) {
            Glide.with(mContext).load(myTeamLogo).placeholder(R.drawable.profiledefaultimg).dontAnimate().into(itemViewHolder.mMyTeamImageView);
        } else {
            Glide.with(mContext).load(R.drawable.profiledefaultimg).dontAnimate().into(itemViewHolder.mMyTeamImageView);
        }
        itemViewHolder.mOpponentTeamNameView.setText(matchRequestModel.getOpponentTeamName());
        String opponentTeamLogo = matchRequestModel.getOpponentTeamLogo();
        if (opponentTeamLogo != null) {
            Glide.with(mContext).load(opponentTeamLogo).placeholder(R.drawable.profiledefaultimg).dontAnimate().into(itemViewHolder.mOpponentTeamImageView);
        } else {
            Glide.with(mContext).load(R.drawable.profiledefaultimg).dontAnimate().into(itemViewHolder.mOpponentTeamImageView);
        }

        if (position != mOpenItem) {
            itemViewHolder.mSwipeLayout.close(true);
        }

        if (matchRequestModel.getStatus().equals("1")|| matchRequestModel.getStatus().equals("2")) {
            itemViewHolder.mSwipeLayout.setSwipeEnabled(false);
        } else {
            itemViewHolder.mSwipeLayout.setSwipeEnabled(true);
            itemViewHolder.mMatchRequestContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMatchRequestItemListener != null) {
                        mMatchRequestItemListener.showMatchInfoDialog(matchRequestModel);
                    }
                }
            });
        }

        itemViewHolder.mSwipeAcceptLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMatchRequestItemListener != null) {
                    mMatchRequestItemListener.onRequestAccept(matchRequestModel);
                }
            }
        });

        itemViewHolder.mSwipeRejectLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMatchRequestItemListener != null) {
                    mMatchRequestItemListener.onRequestReject(matchRequestModel);
                }
            }
        });

        itemViewHolder.mSwipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {

                if (mOpenItem != (int) layout.getTag()) {

                    mOpenItem = (int) layout.getTag();
                    for (int i = 0; mMatchRequestModels.size() > i; i++) {

                        if (i != mOpenItem) {
                            mMatchRequestItemListener.notifyItemChange(i);
                        }
                    }
                }
            }

            @Override
            public void onOpen(SwipeLayout layout) {

            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onClose(SwipeLayout layout) {

            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

            }
        });
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new MatchRequestHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        MatchRequestHeaderViewHolder headerViewHolder = (MatchRequestHeaderViewHolder) holder;
        headerViewHolder.mHeaderTitleView.setText(mHeaderTitle);
    }

    public void closeAllItems() {
        mOpenItem = -1;
        if(mMatchRequestItemListener != null) {
            mMatchRequestItemListener.notifyDataSetChanged();
        }
    }

    private class MatchRequestHeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView mHeaderTitleView;

        public MatchRequestHeaderViewHolder(View view) {
            super(view);
            mHeaderTitleView = (TextView) view.findViewById(R.id.inflater_match_request_header_tv_title);
        }
    }

    private class MatchRequestItemViewHolder extends RecyclerView.ViewHolder {
        private SwipeLayout mSwipeLayout;
        private LinearLayout mSwipeAcceptLinearLayout, mSwipeRejectLinearLayout, mMatchRequestContainer, mOpponentTeamDetailContainer, mMyTeamDetailContainer;
        private ImageView mMatchSportTypeImageView;
        private ProfileImageView mMyTeamImageView, mOpponentTeamImageView;
        private TextView mMatchAcceptView, mMatchRejectView, mMyTeamNameView, mOpponentTeamNameView, mMatchDateView, mMatchTimeView, mMatchRequestDateView;

        public MatchRequestItemViewHolder(View itemView) {
            super(itemView);
            mSwipeLayout = (SwipeLayout) itemView.findViewById(R.id.inflater_match_request_item_swipe_layout);
            mSwipeAcceptLinearLayout = (LinearLayout) itemView.findViewById(R.id.inflater_match_request_item_ll_accept);
            mMatchAcceptView = (TextView) itemView.findViewById(R.id.inflater_match_request_item_tv_accept);
            mSwipeRejectLinearLayout = (LinearLayout) itemView.findViewById(R.id.inflater_match_request_item_ll_reject);
            mMatchRejectView = (TextView) itemView.findViewById(R.id.inflater_match_request_item_tv_reject);
            mMatchSportTypeImageView = (ImageView) itemView.findViewById(R.id.inflater_match_request_item_iv_match_sport_type);
            mMyTeamImageView = (ProfileImageView) itemView.findViewById(R.id.inflater_match_request_item_iv_my_team);
            mOpponentTeamImageView = (ProfileImageView) itemView.findViewById(R.id.inflater_match_request_item_iv_opponent_team);
            mMyTeamNameView = (TextView) itemView.findViewById(R.id.inflater_match_request_item_tv_my_team_name);
            mOpponentTeamNameView = (TextView) itemView.findViewById(R.id.inflater_match_request_item_tv_opponent_team_name);
            mMatchDateView = (TextView) itemView.findViewById(R.id.inflater_match_request_item_tv_match_date);
            mMatchTimeView = (TextView) itemView.findViewById(R.id.inflater_match_request_item_tv_match_time);
            mMatchRequestContainer = (LinearLayout) itemView.findViewById(R.id.inflater_match_request_ll_container);
            mMatchRequestDateView = (TextView) itemView.findViewById(R.id.inflater_match_request_tv_request_time);
            mOpponentTeamDetailContainer = (LinearLayout) itemView.findViewById(R.id.inflater_match_request_ll_opponent_team);
            mMyTeamDetailContainer = (LinearLayout) itemView.findViewById(R.id.inflater_match_request_ll_my_team);
        }
    }

    public interface MatchRequestItemListener {

        void onRequestAccept(MatchRequestModel matchRequestModel);

        void onRequestReject(MatchRequestModel matchRequestModel);

        void notifyItemChange(int position);

        void notifyDataSetChanged();

        void showMatchInfoDialog(MatchRequestModel matchRequestModel);
    }
}
