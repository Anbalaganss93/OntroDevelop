package com.ontro.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.ontro.Constants;
import com.ontro.R;
import com.ontro.dto.TournamentPrice;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IDEOMIND02 on 27-06-2017.
 */

public class TournamentEntryFeesDetailFragment extends Fragment {
    private List<TournamentPrice> mTournamentPrices;
    private TableLayout mEntryFeesView;
    private CardView mEntryFeesCardView;

    public static TournamentEntryFeesDetailFragment newInstance(List<TournamentPrice> price) {
        TournamentEntryFeesDetailFragment  tournamentEntryFeesDetailFragment = new TournamentEntryFeesDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BundleKeys.ENTRY_FEES, (Serializable) price);
        tournamentEntryFeesDetailFragment.setArguments(bundle);
        return tournamentEntryFeesDetailFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            mTournamentPrices = (List<TournamentPrice>) getArguments().getSerializable(Constants.BundleKeys.ENTRY_FEES);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tournament_entry_fees, container, false);
        mEntryFeesCardView = (CardView) view.findViewById(R.id.fragment_tournament_entry_fees_cv);
         mEntryFeesView = (TableLayout) view.findViewById(R.id.fragment_tournament_entry_fees_tl);
        if(mTournamentPrices != null) {
            mEntryFeesCardView.setVisibility(View.VISIBLE);
            mEntryFeesView.removeAllViews();
            for(int i = 0; i < mTournamentPrices.size(); i++) {
                LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View venueAddressView = layoutInflater.inflate(R.layout.inflater_tournament_entry_fees_detail_item, null);
                TextView venueAddressTextView = (TextView) venueAddressView.findViewById(R.id.inflater_tournament_entry_fees_detail_item_tv);
                TournamentPrice tournamentPrice = mTournamentPrices.get(i);
                venueAddressTextView.setText(tournamentPrice.getCategoryName() + "\n"+ Constants.DefaultText.RUPEES_SYMBOL + tournamentPrice.getPrice());
                mEntryFeesView.addView(venueAddressView);
            }
        } else {
            mEntryFeesCardView.setVisibility(View.GONE);
        }
        return view;
    }
}
