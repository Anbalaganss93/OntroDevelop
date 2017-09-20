package com.ontro.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ontro.R;
import com.ontro.dto.VenueAmenityModel;

import java.util.List;

/**
 * Created by Android on 20-Feb-17.
 */

public class AmenitiesAdapter extends RecyclerView.Adapter<AmenitiesAdapter.ViewHolder> {
    private List<VenueAmenityModel> venueAmenityModels;
    private Context context;

    public AmenitiesAdapter(FragmentActivity activity, List<VenueAmenityModel> venueAmenityModels) {
        this.context = activity;
        this.venueAmenityModels = venueAmenityModels;
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.amenities_adapter_layout, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        VenueAmenityModel m = venueAmenityModels.get(position);
        holder.amenities.setText(m.getAmenity());
    }

    @Override
    public int getItemCount() {
        return venueAmenityModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView amenities;

        public ViewHolder(View itemView) {
            super(itemView);
            amenities = (TextView) itemView.findViewById(R.id.amenities);
        }
    }
}
