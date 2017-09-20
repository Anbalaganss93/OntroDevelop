package com.ontro.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ontro.R;
import com.ontro.dto.SportModel;

import java.util.ArrayList;

/**
 * Created by UMMWDC001 on 5/13/2016.
 */
public class FilterSportsAdapter extends BaseAdapter {
    private ArrayList<SportModel> arrayList;
    private DisplayMetrics dm;
    private Context mcontext;
    private LayoutInflater inflater;

    public FilterSportsAdapter(FragmentActivity activity, ArrayList<SportModel> arrayList) {
        this.mcontext = activity;
        this.arrayList = arrayList;
        inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.filter_adapter_sport_layout, parent,false);
            holder.sportcontainer = (LinearLayout) convertView.findViewById(R.id.sportcontainer);
            holder.sportbackground = (FrameLayout) convertView.findViewById(R.id.sportbackground);
            holder.sport_image = (ImageView) convertView.findViewById(R.id.sport_image);
            holder.sport_name = (TextView) convertView.findViewById(R.id.sport_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SportModel m = arrayList.get(position);
        if (m.getSelected() == 1) {
            holder.sportbackground.setBackground(ContextCompat.getDrawable(mcontext, R.drawable.bg_filter_sport_selection_outline));
        } else {
            holder.sportbackground.setBackground(null);
        }
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params2.width = (int) (dm.widthPixels * 0.27);
        params2.setMargins(10, 10, 10, 10);
        params2.height = ((int) (dm.widthPixels * 0.27));
        holder.sportcontainer.setLayoutParams(params2);
        holder.sportbackground.setLayoutParams(params2);

        Glide.with(mcontext).load(m.getSportimage()).dontAnimate().into(holder.sport_image);
        holder.sport_name.setText(m.getSportname());
        return convertView;
    }


    public class ViewHolder {
        ImageView sport_image;
        TextView sport_name;
        LinearLayout sportcontainer;
        FrameLayout sportbackground;
    }
}