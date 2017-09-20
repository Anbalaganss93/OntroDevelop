package com.ontro.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ontro.BookVenueDetailsActivity;
import com.ontro.Constants;
import com.ontro.R;
import com.ontro.dto.VenueResponseModel;
import com.ontro.utils.CommonUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Android on 20-Feb-17.
 */

public class BookVenueListAdapter extends RecyclerView.Adapter<BookVenueListAdapter.ViewHolder> {
    private List<VenueResponseModel> mVenueResponseModels;
    private Context context;
    private DisplayMetrics dm;

    public BookVenueListAdapter(FragmentActivity activity, List<VenueResponseModel> bookVenueResponseModels) {
        this.context = activity;
        this.mVenueResponseModels = bookVenueResponseModels;
        dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflater_book_venue_list_item_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final VenueResponseModel venueResponseModel = mVenueResponseModels.get(position);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        param.width = (int) (dm.widthPixels * 0.75);
        holder.mVenueContainerLayout.setLayoutParams(param);
        holder.mVenueImageContainer.setOrientation(LinearLayout.HORIZONTAL);
//        int[] sport_icon = new int[]{R.drawable.image1, R.drawable.image2, R.drawable.image3};
        if(venueResponseModel.getVenueImages() != null) {
            holder.mVenueImageContainer.removeAllViews();
            String[] venueImages = venueResponseModel.getVenueImages().split(",");
            int imageLength;
            if(venueImages.length > 4) {
                imageLength = 4;
            } else {
                imageLength = venueImages.length;
            }
            for (int i = 0; i < imageLength; i++) {
                View view = LayoutInflater.from(context).inflate(R.layout.inflater_venue_image_layout, holder.mVenueImageContainer, false);
                ImageView imageView = (ImageView) view.findViewById(R.id.inflater_book_venue_list_item_iv_venue_image);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.width = 100;
                layoutParams.height = 100;
                layoutParams.setMargins(5, 0, 5, 0);
                imageView.setAdjustViewBounds(true);
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                imageView.setLayoutParams(layoutParams);
                if(venueImages[i] != null || !venueImages[i].isEmpty()) {
                    Glide.with(context).load(venueImages[i]).placeholder(R.drawable.image1).dontAnimate().into(imageView);
                } else {
                    Glide.with(context).load(R.drawable.image1).dontAnimate().into(imageView);
                }
                imageView.setBackgroundResource(R.drawable.image1);
                holder.mVenueImageContainer.addView(view);
            }
        }
        holder.mVenueNameTextView.setText(venueResponseModel.getVenueName());
        holder.mVenueLocationTextView.setText(venueResponseModel.getLocationName());
        String[] sports = venueResponseModel.getSport().split(",");
        StringBuilder sportList = new StringBuilder();
        for (int i = 0; i < sports.length; i++) {
            if (i == sports.length - 1) sportList.append(CommonUtils.sportNameCheck(sports[i]));
            else sportList.append(CommonUtils.sportNameCheck(sports[i])).append(", ");
        }
        holder.mVenueSportsTextView.setText(sportList);
        holder.mVenueAverageCostTextView.setText(Constants.DefaultText.RUPEES_SYMBOL + String.format("%.2f", Double.valueOf(venueResponseModel.getAvgCost())));
        String openingFromTime = getTimeFormat(venueResponseModel.getOpenFrom());
        String openingToTime = getTimeFormat(venueResponseModel.getOpenTo());
        holder.mVenueOpeningHourTextView.setText(openingFromTime + " - " + openingToTime);
        holder.mVenueContainerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BookVenueDetailsActivity.class);
                intent.putExtra(Constants.BundleKeys.VENUE_ID, venueResponseModel.getVenueId());
                context.startActivity(intent);
            }
        });
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
    public int getItemCount() {
        return mVenueResponseModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mVenueContainerLayout, mVenueImageContainer;
        private TextView mVenueNameTextView, mVenueLocationTextView, mVenueSportsTextView,
                mVenueAverageCostTextView, mVenueOpeningHourTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mVenueNameTextView = (TextView) itemView.findViewById(R.id.inflater_book_venue_list_item_tv_venue_name);
            mVenueLocationTextView = (TextView) itemView.findViewById(R.id.inflater_book_venue_list_item_tv_location_name);
            mVenueSportsTextView = (TextView) itemView.findViewById(R.id.inflater_book_venue_list_item_tv_sports_name);
            mVenueAverageCostTextView = (TextView) itemView.findViewById(R.id.inflater_book_venue_list_item_tv_avg_cost_value);
            mVenueOpeningHourTextView = (TextView) itemView.findViewById(R.id.inflater_book_venue_list_item_tv_opening_hours_value);
            mVenueContainerLayout = (LinearLayout) itemView.findViewById(R.id.inflater_book_venue_list_item_ll_discussion_container);
            mVenueImageContainer = (LinearLayout) itemView.findViewById(R.id.inflater_book_venue_list_item_ll_image_container);
        }
    }
}
