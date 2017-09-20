package com.ontro.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ontro.BookVenueDetailsActivity;
import com.ontro.R;
import com.ontro.utils.CommonUtils;

import java.util.List;

/**
 * Created by Android on 20-Feb-17.
 */

public class SportFacilitiesAdapter extends RecyclerView.Adapter<SportFacilitiesAdapter.ViewHolder> {
    private List<String> sports;
    private Context context;
    private DisplayMetrics dm;

    public SportFacilitiesAdapter(BookVenueDetailsActivity bookVenueDetailsActivity, List<String> sports) {
        this.context = bookVenueDetailsActivity;
        this.sports = sports;
        dm = new DisplayMetrics();
        bookVenueDetailsActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sport_facility_adapter_layout, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        String sportId = sports.get(position);
        LinearLayout.LayoutParams img_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        img_params.width = (int) (dm.widthPixels * 0.12);
        img_params.height = ((int) (dm.widthPixels * 0.12));
        holder.sportFacility.setLayoutParams(img_params);
        holder.sportFacility.setBackgroundResource(CommonUtils.sportCheck(sportId));
    }


    @Override
    public int getItemCount() {
        return sports.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView sportFacility;

        public ViewHolder(View itemView) {
            super(itemView);
            sportFacility = (ImageView) itemView.findViewById(R.id.sportfacility);
        }
    }
}
