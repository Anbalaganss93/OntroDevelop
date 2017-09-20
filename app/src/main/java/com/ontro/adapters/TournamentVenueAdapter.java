package com.ontro.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ontro.R;
import com.ontro.dto.TournamentVenueAddress;

import java.util.List;

/**
 * Created by IDEOMIND02 on 10-07-2017.
 */

public class TournamentVenueAdapter extends ArrayAdapter<TournamentVenueAddress> {
    private Context mContext;
    private  int layoutResourceId;
    private List<TournamentVenueAddress> mVenueAddresses;
    private AddressToMapNavigationListener mMapNavigationListener;


    public TournamentVenueAdapter(Context context, int resourceId, List<TournamentVenueAddress> venueAddresses, AddressToMapNavigationListener mapNavigationListener) {
        super(context, resourceId, venueAddresses);
        mContext = context;
        layoutResourceId = resourceId;
        mVenueAddresses = venueAddresses;
        mMapNavigationListener = mapNavigationListener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        VenueAddressViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(layoutResourceId, parent, false);
            viewHolder = new VenueAddressViewHolder();
            viewHolder.mGroundNameTextView = (TextView) convertView.findViewById(R.id.inflater_tournament_venue_address_detail_item_tv_ground_name);
            viewHolder.mGroundAddressTextView = (TextView) convertView.findViewById(R.id.inflater_tournament_venue_address_detail_item_tv_ground_address);
            viewHolder.mGroundMapNavigationView = (ImageView) convertView.findViewById(R.id.inflater_tournament_venue_address_detail_item_iv_venue_map);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (VenueAddressViewHolder) convertView.getTag();
        }

        final TournamentVenueAddress tournamentVenueAddress = getItem(position);

        if (tournamentVenueAddress != null) {
            viewHolder.mGroundNameTextView.setText(tournamentVenueAddress.getGroundName());
            viewHolder.mGroundAddressTextView.setText(tournamentVenueAddress.getAddress());
            viewHolder.mGroundMapNavigationView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mMapNavigationListener != null) {
                        mMapNavigationListener.onMapClicked(v, tournamentVenueAddress);
                    }
                }
            });
        }

        return convertView;
    }

    private class VenueAddressViewHolder {
        TextView mGroundNameTextView, mGroundAddressTextView;
        ImageView mGroundMapNavigationView;
    }

    public interface AddressToMapNavigationListener {

        void onMapClicked(View v, TournamentVenueAddress tournamentVenueAddress);
    }
}
