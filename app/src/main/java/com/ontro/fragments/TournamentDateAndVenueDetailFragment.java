package com.ontro.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.ontro.Constants;
import com.ontro.R;
import com.ontro.adapters.TournamentVenueAdapter;
import com.ontro.dto.DateAndVenue;
import com.ontro.dto.TournamentVenueAddress;
import com.ontro.utils.CommonUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by IDEOMIND02 on 27-06-2017.
 */

public class TournamentDateAndVenueDetailFragment extends Fragment implements TournamentVenueAdapter.AddressToMapNavigationListener {
    private TextView mTournamentDateView, mTournamentTimeView;
    private ListView mTournamentVenueViews;
    private View mRootView;
    private DateAndVenue mDateAndVenue;

    public static TournamentDateAndVenueDetailFragment newInstance(DateAndVenue dateAndVenue) {
        TournamentDateAndVenueDetailFragment tournamentDateAndVenueDetailFragment = new TournamentDateAndVenueDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BundleKeys.DATE_AND_VENUE, dateAndVenue);
        tournamentDateAndVenueDetailFragment.setArguments(bundle);
        return tournamentDateAndVenueDetailFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_tournament_date_and_venue, container, false);
        initView();
        setValue();
        return mRootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDateAndVenue = (DateAndVenue) getArguments().getSerializable(Constants.BundleKeys.DATE_AND_VENUE);
        }
    }

    private void initView() {
        mTournamentDateView = (TextView) mRootView.findViewById(R.id.fragment_tournament_date_and_venue_tv_date_detail);
        mTournamentTimeView = (TextView) mRootView.findViewById(R.id.fragment_tournament_date_and_venue_tv_time_detail);
        mTournamentVenueViews = (ListView) mRootView.findViewById(R.id.fragment_tournament_date_and_venue_lv_venue_detail);
    }

    private void setValue() {
        if (mDateAndVenue != null) {
            String startDate = CommonUtils.convertDateFormat(mDateAndVenue.getStartDate(), Constants.DefaultText.YEAR_MONTH_DATE, Constants.DefaultText.DATE_MONTH_YEAR);
            String endDate = CommonUtils.convertDateFormat(mDateAndVenue.getEndDate(), Constants.DefaultText.YEAR_MONTH_DATE, Constants.DefaultText.DATE_MONTH_YEAR);
            mTournamentDateView.setText(startDate + " - " + endDate);
            mTournamentTimeView.setText(getTimeFormat(mDateAndVenue.getFromTime()) + " -" + getTimeFormat(mDateAndVenue.getToTime()));
            List<TournamentVenueAddress> venueAddresses = mDateAndVenue.getAddress();
            if(venueAddresses != null) {
                TournamentVenueAdapter tournamentVenueAdapter = new TournamentVenueAdapter(getActivity(), R.layout.inflater_tournament_venue_address_detail_item, venueAddresses, this);
                mTournamentVenueViews.setAdapter(tournamentVenueAdapter);
            }
        }
    }

    private String getTimeFormat(String openingTime) {
        String time = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            Date date = dateFormat.parse(openingTime);
            DateFormat simpleDateFormat = new SimpleDateFormat("h:mma");
            time = simpleDateFormat.format(date).toLowerCase();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    @Override
    public void onMapClicked(View v, TournamentVenueAddress tournamentVenueAddress) {
        String lable =  tournamentVenueAddress.getGroundName();
        String query;
        if(!TextUtils.isEmpty(lable)){
            query = Uri.encode(lable);
        } else {
            query = Constants.Messages.NO_DETAIL;
        }
        String latitude = tournamentVenueAddress.getLatitude();
        String longitude = tournamentVenueAddress.getLongitude();
        Uri gmmIntentUri = Uri.parse("geo:"+latitude+","+longitude+"?q= "+ query +" @"+latitude+","+longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage(Constants.CommonFields.MAP_PACKAGE_NAME);
        startActivity(mapIntent);
    }
}
