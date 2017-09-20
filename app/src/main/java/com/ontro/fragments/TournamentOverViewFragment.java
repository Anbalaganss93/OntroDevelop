package com.ontro.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ontro.Constants;
import com.ontro.PdfViewActivity;
import com.ontro.R;
import com.ontro.dto.Overview;

/**
 * Created by IDEOMIND02 on 27-06-2017.
 */

public class TournamentOverViewFragment extends Fragment implements View.OnClickListener {
    private View mRootView;
    private TextView mTournamentOrganizedByView, mTournamentDescriptionView;
    private ImageView mTournamentFacebookPageView, mTournamentTwitterPageView, mTournamentWebPageView, mFixturesPdfView;
    private CardView mFixturesCardView;
    private Overview mOverView;

    public static TournamentOverViewFragment newInstance(Overview overview) {
        TournamentOverViewFragment tournamentOverViewFragment = new TournamentOverViewFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BundleKeys.OVER_VIEW, overview);
        tournamentOverViewFragment.setArguments(bundle);
        return tournamentOverViewFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mOverView = (Overview) getArguments().getSerializable(Constants.BundleKeys.OVER_VIEW);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_tournament_overview, container, false);
        initView();
        setListener();
        setValue();
        return mRootView;
    }

    private void initView() {
        mTournamentOrganizedByView = (TextView) mRootView.findViewById(R.id.fragment_overview_tournament_tv_organized_by);
        mTournamentDescriptionView = (TextView) mRootView.findViewById(R.id.fragment_overview_tournament_tv_description);
        mTournamentFacebookPageView = (ImageView) mRootView.findViewById(R.id.fragment_overview_tournament_iv_facebook_logo);
        mTournamentTwitterPageView = (ImageView) mRootView.findViewById(R.id.fragment_overview_tournament_iv_twitter_logo);
        mTournamentWebPageView = (ImageView) mRootView.findViewById(R.id.fragment_overview_tournament_iv_web_logo);
        mFixturesCardView = (CardView) mRootView.findViewById(R.id.fragment_overview_tournament_cv_fixtures);
        mFixturesPdfView = (ImageView) mRootView.findViewById(R.id.fragment_overview_tournament_iv_fixtures_pdf);
    }

    private void setListener() {
        mTournamentFacebookPageView.setOnClickListener(this);
        mTournamentTwitterPageView.setOnClickListener(this);
        mTournamentWebPageView.setOnClickListener(this);
        mFixturesPdfView.setOnClickListener(this);
    }

    private void setValue() {
        if (mOverView != null) {
            mTournamentOrganizedByView.setText(mOverView.getOrganizationName());
            mTournamentDescriptionView.setText(mOverView.getDescription());
            if(mOverView.getFbLink() != null) {
                if(!TextUtils.isEmpty(mOverView.getFbLink())) {
                    mTournamentFacebookPageView.setVisibility(View.VISIBLE);
                } else {
                    mTournamentFacebookPageView.setVisibility(View.GONE);
                }
            } else {
                mTournamentFacebookPageView.setVisibility(View.GONE);
            }
            if(mOverView.getTwitterLink() != null) {
                if(!TextUtils.isEmpty(mOverView.getTwitterLink())) {
                    mTournamentTwitterPageView.setVisibility(View.VISIBLE);
                } else {
                    mTournamentTwitterPageView.setVisibility(View.GONE);
                }
            } else {
                mTournamentTwitterPageView.setVisibility(View.GONE);
            }
            if(mOverView.getWebUrl() != null) {
                if(!TextUtils.isEmpty(mOverView.getWebUrl())) {
                    mTournamentWebPageView.setVisibility(View.VISIBLE);
                } else {
                    mTournamentWebPageView.setVisibility(View.GONE);
                }
            } else {
                mTournamentWebPageView.setVisibility(View.GONE);
            }
            if(mOverView.getFixtures() != null) {
                if(TextUtils.isEmpty(mOverView.getFixtures())) {
                    mFixturesCardView.setVisibility(View.GONE);
                } else {
                    mFixturesCardView.setVisibility(View.VISIBLE);
                }
            } else {
                mFixturesCardView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_overview_tournament_iv_facebook_logo:
                if (mOverView != null) NavigateToFacebookPage(mOverView.getFbLink());
                break;
            case R.id.fragment_overview_tournament_iv_twitter_logo:
                if (mOverView != null)
                    NavigateToTwitterPage(getActivity(), mOverView.getTwitterLink());
                break;
            case R.id.fragment_overview_tournament_iv_web_logo:
                if (mOverView != null)
                    NavigateToWebPage(mOverView.getWebUrl());
                break;
            case R.id.fragment_overview_tournament_iv_fixtures_pdf :
                Intent intent = new Intent(getActivity(), PdfViewActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void NavigateToWebPage(String webUrl) {
        if (!webUrl.isEmpty()) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
            startActivity(webIntent);
        }
    }

    private void NavigateToFacebookPage(String fbLink) {
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        String facebookUrl = getFacebookPageURL(getActivity(), fbLink);
        facebookIntent.setData(Uri.parse(facebookUrl));
        startActivity(facebookIntent);
    }

    private String getFacebookPageURL(Context context, String fbLink) {
        String facebookUrl = fbLink;
        String pageId;
        if (!facebookUrl.isEmpty()) {
            String pageName[] = facebookUrl.split("/");
            if(pageName != null) {
                int length = pageName.length;
                pageId = pageName[length - 1];
                PackageManager packageManager = context.getPackageManager();
                try {
                    int versionCode = packageManager.getPackageInfo(Constants.CommonFields.FACEBOOK_NAVIGATION_PACKAGE, 0).versionCode;
                    if (versionCode >= 3002850) { //newer versions of fb app
                        facebookUrl = Constants.CommonFields.FACEBOOK_APP_NAVIGATION_URL + facebookUrl;
                    } else { //older versions of fb app
                        if (pageId != null)
                            facebookUrl = Constants.CommonFields.FACEBOOK_PAGE_ID_NAVIGATION_URL + pageId;
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    return facebookUrl; //normal web url
                }
            }
        }
        return facebookUrl;
    }

    private void NavigateToTwitterPage(Context context, String twitterUrl) {
        if (twitterUrl.length() != 0) {
            Intent twitterIntent;
            try {
                // get the Twitter app if possible
                String twitterPage[] = twitterUrl.split("/");
                if(twitterPage != null) {
                    int length = twitterPage.length;
                    String pageName = twitterPage[length-1];
                    context.getPackageManager().getPackageInfo(Constants.CommonFields.TWITTER_NAVIGATION_PACKAGE, 0);
                    twitterIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.CommonFields.TWITTER_PAGE_NAVIGATION_URL + pageName));
                    startActivity(twitterIntent);
                }
            } catch (Exception e) {
                // no Twitter app, revert to browser
                twitterIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterUrl));
                startActivity(twitterIntent);
            }

        }
    }


}
