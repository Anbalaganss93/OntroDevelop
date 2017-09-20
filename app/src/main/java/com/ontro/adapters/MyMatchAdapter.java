package com.ontro.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Created by Android on 20-Feb-17.
 */

public class MyMatchAdapter extends RecyclerView.Adapter<MyMatchAdapter.ViewHolder> {
    private List<MatchRequestModel> mMatchRequestModels;
    private Context mContext;
    private int mOpenItem;
    private MatchRequestListener mMatchRequestListener;
    private int mTabPosition;

    public MyMatchAdapter(Context context, List<MatchRequestModel> matchRequestModels, int tabPosition, MatchRequestListener matchRequestListener) {
        mContext = context;
        mMatchRequestModels = matchRequestModels;
        mOpenItem = -1;
        mTabPosition = tabPosition;
        mMatchRequestListener = matchRequestListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflater_match_request_item, parent, false);
        return new ViewHolder(view);
    }

    public void closeAllItems() {
        mOpenItem = -1;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final MatchRequestModel matchRequestModel = mMatchRequestModels.get(position);
        holder.mMatchSportTypeImageView.setImageResource(CommonUtils.scoreUpdateSport(matchRequestModel.getTeamSport()));
        holder.mSwipeLayout.setTag(position);
        holder.mMatchRequestDateView.setText(matchRequestModel.getRequestDate());
        if(matchRequestModel.getBookingDate() != null) {
            if(!matchRequestModel.getBookingDate().isEmpty()) {
                holder.mMatchTimeView.setVisibility(View.VISIBLE);
                holder.mMatchDateView.setText(matchRequestModel.getBookingDate());
                if (matchRequestModel.getFromTime() != null && matchRequestModel.getTeamSport() != null) {
                    holder.mMatchTimeView.setText(CommonUtils.convertTimeFormatIntoAmOrPmFormat(matchRequestModel.getFromTime()) + Constants.DefaultText.HYPHEN
                            + CommonUtils.convertTimeFormatIntoAmOrPmFormat(matchRequestModel.getToTime()));
                }
            } else {
                holder.mMatchTimeView.setVisibility(View.GONE);
                holder.mMatchDateView.setText(matchRequestModel.getMatchDate());
            }
        } else {
            holder.mMatchTimeView.setVisibility(View.GONE);
            holder.mMatchDateView.setText(matchRequestModel.getMatchDate());
        }
        holder.mMyTeamNameView.setText(matchRequestModel.getMyTeamName());
        String myTeamLogo = matchRequestModel.getMyTeamLogo();
        if (myTeamLogo != null) {
            Glide.with(mContext).load(myTeamLogo).placeholder(R.drawable.profiledefaultimg).dontAnimate().into(holder.mMyTeamImageView);
        } else {
            Glide.with(mContext).load(R.drawable.profiledefaultimg).dontAnimate().into(holder.mMyTeamImageView);
        }
        holder.mOpponentTeamNameView.setText(matchRequestModel.getOpponentTeamName());
        String opponentTeamLogo = matchRequestModel.getOpponentTeamLogo();
        if (opponentTeamLogo != null) {
            Glide.with(mContext).load(opponentTeamLogo).placeholder(R.drawable.profiledefaultimg).dontAnimate().into(holder.mOpponentTeamImageView);
        } else {
            Glide.with(mContext).load(R.drawable.profiledefaultimg).dontAnimate().into(holder.mOpponentTeamImageView);
        }

        if (position != mOpenItem) {
            holder.mSwipeLayout.close(true);
        }
        holder.mSwipeLayout.setSwipeEnabled(false);

        /*if(mTabPosition == 1) {
            if(matchRequestModel.getStatusMessage().equals("waiting for opponent confirmation")) {
                holder.mSwipeLayout.setSwipeEnabled(false);
            } else {
                holder.mSwipeLayout.setSwipeEnabled(true);
            }
        } else {
            holder.mSwipeLayout.setSwipeEnabled(false);
        }

        if(mTabPosition == 1) {
            holder.mOpponentTeamDetailContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMatchRequestListener != null) {
                        mMatchRequestListener.navigateToTeamDetailActivity(matchRequestModel.getOpponentTeamId());
                    }
                }
            });

            holder.mMyTeamDetailContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMatchRequestListener != null) {
                        mMatchRequestListener.navigateToTeamDetailActivity(matchRequestModel.getMyTeamId());
                    }
                }
            });
        }

        holder.mSwipeAcceptLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMatchRequestListener != null) {
                    mMatchRequestListener.onRequestAccept(matchRequestModel);
                }
            }
        });

        holder.mSwipeRejectLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMatchRequestListener != null) {
                    mMatchRequestListener.onRequestReject(matchRequestModel);
                }
            }
        });*/

        if(mTabPosition != 1) {
            holder.mMatchRequestContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMatchRequestListener != null) {
                        mMatchRequestListener.onRequestItemClicked(matchRequestModel);
                    }
                }
            });
        }

      /*  holder.mSwipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {

                if (mOpenItem != (int) layout.getTag()) {

                    mOpenItem = (int) layout.getTag();
                    for (int i = 0; mMatchRequestModels.size() > i; i++) {

                        if (i != mOpenItem) {

                            notifyItemChanged(i);
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
        });*/
    }

    @Override
    public int getItemCount() {
        return mMatchRequestModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private SwipeLayout mSwipeLayout;
        private LinearLayout mSwipeAcceptLinearLayout, mSwipeRejectLinearLayout, mMatchRequestContainer, mOpponentTeamDetailContainer, mMyTeamDetailContainer;
        private ImageView mMatchSportTypeImageView;
        private ProfileImageView mMyTeamImageView, mOpponentTeamImageView;
        private TextView mMatchAcceptView, mMatchRejectView, mMyTeamNameView, mOpponentTeamNameView, mMatchDateView, mMatchTimeView, mMatchRequestDateView;

        public ViewHolder(View itemView) {
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

    public interface MatchRequestListener {

        void onRequestItemClicked(MatchRequestModel matchRequestModel);

    }
}
