package com.ontro.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ontro.R;
import com.ontro.dto.PersonalDetailsSpinnerModel;

import java.util.ArrayList;

/**
 * Created by intel on 6/15/2016.
 */
public class SpinnneradapterPersonalDetailsAdapter extends ArrayAdapter<PersonalDetailsSpinnerModel> {
    private Context contexts;
    private ArrayList<PersonalDetailsSpinnerModel> arrayList;
    private LayoutInflater inflater;

    public SpinnneradapterPersonalDetailsAdapter(FragmentActivity personalDetailsActivity, ArrayList<PersonalDetailsSpinnerModel> arrayList) {
        super(personalDetailsActivity, R.layout.activity_spinnerlayout_personaldetails, arrayList);
        this.contexts = personalDetailsActivity;
        inflater = (LayoutInflater) personalDetailsActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.arrayList = arrayList;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = getView(position, convertView, parent);
        if(view instanceof TextView) {
            ((TextView) view).setText(arrayList.get(position).getGender());
        }
        return view;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder mViewHolder;
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.activity_spinnerlayout_personaldetails, parent, false);
            mViewHolder.gender = (TextView) convertView.findViewById(R.id.gender_text);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.gender.setText(arrayList.get(position).getGender());
        return convertView;
    }

    public class ViewHolder {
        private TextView gender;
    }
}
