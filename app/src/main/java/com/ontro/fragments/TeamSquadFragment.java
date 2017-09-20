package com.ontro.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;

import com.google.gson.Gson;
import com.ontro.Constants;
import com.ontro.ExplorePlayerListActivity;
import com.ontro.R;
import com.ontro.TeamDetailActivity;
import com.ontro.adapters.SquadsAdapter;
import com.ontro.dto.SquadInfo;
import com.ontro.rest.TeamDetailResponse;
import com.ontro.utils.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Android on 27-Feb-17.
 */

public class TeamSquadFragment extends Fragment implements SquadsAdapter.RecyclerViewItemClickListener {
    private TeamDetailResponse mTeamDetailResponse;
    private List<SquadInfo> mSquadInfos;

    public static Fragment newInstance(TeamDetailResponse teamDetailResponse) {
        TeamSquadFragment teamSquadFragment = new TeamSquadFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BundleKeys.TEAM_DETAIL_RESPONSE, teamDetailResponse);
        teamSquadFragment.setArguments(bundle);
        return teamSquadFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_team_squad_layout, container, false);
        Button mAddNewMemberButton = (Button) v.findViewById(R.id.fragment_team_squad_btn_add_new_member);
        RecyclerView mlistView = (RecyclerView) v.findViewById(R.id.fragment_team_squad_rv);
       /* mlistView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int scrollDy = 0;
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                scrollDy += dy;
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                AppBarLayout appBarLayout;
                if (getActivity() instanceof TeamDetailActivity) {
                    appBarLayout = ((AppBarLayout) getActivity().findViewById(R.id.activity_team_detail_appbar_layout));
                } else {
                    appBarLayout = ((AppBarLayout) getActivity().findViewById(R.id.activity_match_request_appbar_layout));
                }
                if (scrollDy == 0 && (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE)) {
                    if (null != mSquadInfos && mSquadInfos.size() > 5)
                        appBarLayout.setExpanded(true);
                }
            }
        });*/
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto_regular.ttf");
        mAddNewMemberButton.setTypeface(typeface);

        if(getArguments() != null) {
            mTeamDetailResponse = (TeamDetailResponse) getArguments().getSerializable(Constants.BundleKeys.TEAM_DETAIL_RESPONSE);
            if (mTeamDetailResponse != null) {
                if(mTeamDetailResponse.getIsOwner().equals(Constants.DefaultText.ONE)) {
                    mAddNewMemberButton.setVisibility(View.VISIBLE);
                } else {
                    mAddNewMemberButton.setVisibility(View.GONE);
                }

                mSquadInfos = mTeamDetailResponse.getSquadInfos();
                String teamOwner = mTeamDetailResponse.getTeamOwner();

                // Squad list
                if (mSquadInfos != null) {
//            mScrollView.setVisibility(View.GONE);
                    SquadsAdapter adapter = new SquadsAdapter(getActivity(), this, mSquadInfos, teamOwner);
                    mlistView.setAdapter(adapter);
                }
            }


        }

        mAddNewMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTeamDetailResponse != null) {
                    PreferenceHelper preferenceHelper = new PreferenceHelper(getActivity(), Constants.APP_NAME, 0);
                    preferenceHelper.save("teaminfo_squade_add", "true");
                    Intent inviteIntent = new Intent(getActivity(), ExplorePlayerListActivity.class);
                    inviteIntent.putExtra(Constants.BundleKeys.SPORT_ID, mTeamDetailResponse.getSport());
                    inviteIntent.putExtra(Constants.BundleKeys.TEAM_ID, mTeamDetailResponse.getTeamId());
                    inviteIntent.putExtra(Constants.BundleKeys.TEAM_NAME, mTeamDetailResponse.getTeamName());
                    startActivity(inviteIntent);
                }
            }
        });

        return v;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible && getActivity() != null) {
            TeamDetailActivity.info_squad_state = 2;
          }
    }


    @Override
    public void onClickListIem(SquadInfo squadInfo, int position) {
        PlayerQuickViewFragment playerQuickViewFragment
                = PlayerQuickViewFragment.newInstance(mSquadInfos.get(position).getPlayerId(),
                mTeamDetailResponse.getSport(), mTeamDetailResponse.getTeamId(), mTeamDetailResponse.getIsOwner());
        playerQuickViewFragment.show(getActivity().getFragmentManager(), Constants.Messages.PLAYER_QUICK_VIEW);
    }
}
