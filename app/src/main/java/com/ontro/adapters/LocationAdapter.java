package com.ontro.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ontro.R;
import com.ontro.dto.LocationModel;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by intel on 6/15/2016.
 */
public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {
    private Context context;
    private ArrayList<LocationModel> arrayList;
    private ArrayList<LocationModel> searchbeanlist;

    public LocationAdapter(Context mcontext, ArrayList<LocationModel> locationarrayList) {
        this.context = mcontext;
        this.arrayList = locationarrayList;
        this.searchbeanlist=new ArrayList<>();
        this.searchbeanlist.addAll(locationarrayList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_spinnerlayout_personaldetails, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.locationname.setText(arrayList.get(position).getLocationname());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView locationname;

        public ViewHolder(View itemView) {
            super(itemView);
            locationname = (TextView) itemView.findViewById(R.id.gender_text);
        }
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        arrayList.clear();
        if (charText.length() == 0) {
            arrayList.addAll(searchbeanlist);
        } else {
            for (LocationModel Sc : searchbeanlist) {
                if (Sc.getLocationname().toLowerCase(Locale.getDefault()).contains(charText)) {
                    arrayList.add(Sc);

                }
            }
        }
        notifyDataSetChanged();
    }
}
