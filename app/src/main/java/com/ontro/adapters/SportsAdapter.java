package com.ontro.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ontro.Constants;
import com.ontro.R;
import com.ontro.dto.SportModel;
import com.ontro.utils.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by UMMWDC001 on 5/13/2016.
 */

public class SportsAdapter extends RecyclerView.Adapter<SportsAdapter.ViewHolder> {
    private List<SportModel> arrayList;
    private String mHandednessContent = "", mPositionContent = "";
    private Context mContext;
    private List<String> selectedfavort_sport;
    private JSONArray myarray = new JSONArray();
    private PreferenceHelper preferenceHelper;
    private SportsAdapterListener mSportsAdapterListener;

    public SportsAdapter(FragmentActivity activity, List<SportModel> arrayList, List<String> selectedfavort_sport, SportsAdapterListener sportsAdapterListener) {
        this.mContext = activity;
        this.arrayList = arrayList;
        mSportsAdapterListener = sportsAdapterListener;
        preferenceHelper = new PreferenceHelper(activity, Constants.APP_NAME, 0);
        this.selectedfavort_sport = selectedfavort_sport;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflater_sport_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final SportModel sportModel = arrayList.get(position);
        if (sportModel.getSelected() == 1) {
            holder.mSportItemContainer.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_sport_selection_outline));
            holder.mSportSpecializationTextView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_sport_position_selection));
            holder.mSportSpecializationTextView.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            holder.mSportSpecializationTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down_arrow_dark, 0);
        } else {
            holder.mSportItemContainer.setBackgroundColor(Color.parseColor("#10171F"));
            holder.mSportSpecializationTextView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_sport_position_unselection));
            holder.mSportSpecializationTextView.setTextColor(ContextCompat.getColor(mContext, R.color.sport_unselection_arrow_color));
            holder.mSportSpecializationTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down_arrow_light, 0);
        }
        holder.mSportNameTextView.setText(sportModel.getSportname());
        Glide.with(mContext).load(sportModel.getSportimage()).dontAnimate().into(holder.mSportImageView);
        final List<String> arrayList = sportModel.getmPlayerposition();
        holder.mSportSpecializationTextView.setText(arrayList.get(sportModel.getSelectedposition()));
        holder.mSportSpecializationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sportDialog(sportModel.getmPlayerposition(), holder, sportModel);
            }
        });
        if (sportModel.getSelected() == 1) {
            if (sportModel.getPlayerpositionstatus().equals("1")) {
                mHandednessContent = arrayList.get(sportModel.getSelectedposition());
                mPositionContent = "";
            } else {
                mHandednessContent = "";
                mPositionContent = arrayList.get(sportModel.getSelectedposition());
            }
            JSONObject json = new JSONObject();
            try {
                json.put("sport", GetSportID(holder.getAdapterPosition()));
                json.put("handedness", mHandednessContent);
                json.put("position", mPositionContent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            addToPos(holder.getAdapterPosition(), json, myarray);
            selectedfavort_sport.add(holder.getAdapterPosition(), GetSportID(holder.getAdapterPosition()));
        }
        holder.mSportItemContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sportModel.getSelected() == 0) {
                    sportModel.setSelected(1);
                    if (sportModel.getPlayerpositionstatus().equals("0")) {
                        mHandednessContent = arrayList.get(sportModel.getSelectedposition());
                        mPositionContent = "";
                    } else {
                        mHandednessContent = "";
                        mPositionContent = arrayList.get(sportModel.getSelectedposition());
                    }
                    JSONObject json = new JSONObject();
                    try {
                        json.put("sport", GetSportID(holder.getAdapterPosition()));
                        json.put("handedness", mHandednessContent);
                        json.put("position", mPositionContent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    addToPos(holder.getAdapterPosition(), json, myarray);
                    selectedfavort_sport.add(holder.getAdapterPosition(), GetSportID(holder.getAdapterPosition()));
                } else if (sportModel.getSelected() == 1) {
                    if(sportModel.getHaveTeam() == 0) {
                        removeToPos(holder.getAdapterPosition(), myarray);
                        notifyDataSetChanged();
                        sportModel.setSelected(0);
                    } else {
                        if(mSportsAdapterListener != null) {
                            mSportsAdapterListener.showAlertDialog();
                        }
                    }
                }
                notifyDataSetChanged();
            }
        });
    }

    private void removeToPos(int pos, JSONArray jsonArr) {
        try {
            jsonArr.put(pos, "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sportMulitSelectArray(jsonArr);
    }

    private void addToPos(int pos, JSONObject jsonObj, JSONArray jsonArr) {
        try {
            jsonArr.put(pos, jsonObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sportMulitSelectArray(jsonArr);
    }

    private void sportMulitSelectArray(JSONArray jsonArr) {
        JSONArray array = new JSONArray();
        if (jsonArr != null) {
            String element = "";
            for (int i = 0; i < jsonArr.length(); i++) {
                try {
                    if (jsonArr.get(i) != null) {
                        try {
                            element = String.valueOf(jsonArr.get(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (element.length() != 0) {
                    try {
                        array.put(jsonArr.getJSONObject(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            String mSportSelection = array.length() != 0 ? String.valueOf(array) : "";
            preferenceHelper.save("user_favouriteofsport", mSportSelection);
        }
    }

    private String GetSportID(int pos) {
        String sportid = "";
        switch (pos) {
            case 0:
                sportid = "5";
                break;
            case 1:
                sportid = "4";
                break;
            case 2:
                sportid = "6";
                break;
            case 3:
                sportid = "2";
                break;
            case 4:
                sportid = "1";
                break;
            case 5:
                sportid = "7";
                break;
            case 6:
                sportid = "3";
                break;
        }
        return sportid;
    }

    private void sportDialog(final List<String> arrayList, ViewHolder holder, final SportModel m) {
        final Dialog dialog = new Dialog(mContext, android.R.style.Theme_Translucent_NoTitleBar);
        Window window = dialog.getWindow();
        assert window != null;
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.adapter_sport_position_layout);
        final RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.radiogroup);
        rg.setPadding(15, 10, 10, 10);
        final RadioButton[] rb = new RadioButton[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            rb[i] = new RadioButton(mContext);
            rb[i].setCompoundDrawablesWithIntrinsicBounds(R.drawable.discussion_dialog_radio_drawable, 0, 0, 0);
            rb[i].setButtonDrawable(null);
            rb[i].setPadding(15, 10, 10, 10);
            rb[i].setCompoundDrawablePadding(10);
            rg.addView(rb[i]);
            rb[i].setText(arrayList.get(i));
        }

        if (m.getSelected() == 1) {
            if (m.getPlayerpositionstatus().equals("0")) {
                if (m.getSelectedposition() >= 0) {
                    ((RadioButton) rg.getChildAt(m.getSelectedposition())).setChecked(true);
                }
            } else {
                if (m.getSelectedposition() >= 0) {
                    ((RadioButton) rg.getChildAt(m.getSelectedposition())).setChecked(true);
                }
            }
        }

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View radioButton = radioGroup.findViewById(i);
                int index = radioGroup.indexOfChild(radioButton);
                m.setSelectedposition(index);
                if (m.getPlayerpositionstatus().equals("0")) {
                    mHandednessContent = arrayList.get(index);
                    mPositionContent = "";
                } else {
                    mHandednessContent = "";
                    mPositionContent = arrayList.get(index);
                }
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                notifyDataSetChanged();
            }
        });
        dialog.show();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mSportImageView;
        private TextView mSportNameTextView, mSportSpecializationTextView;
        private FrameLayout mSportBackground;
        private RelativeLayout mSportItemContainer;

        public ViewHolder(View convertView) {
            super(convertView);
            mSportBackground = (FrameLayout) convertView.findViewById(R.id.inflater_sport_item_fl);
            mSportItemContainer = (RelativeLayout) convertView.findViewById(R.id.inflater_sport_item_rl_container);
            mSportImageView = (ImageView) convertView.findViewById(R.id.inflater_sport_item_iv);
            mSportNameTextView = (TextView) convertView.findViewById(R.id.inflater_sport_item_tv_name);
            mSportSpecializationTextView = (TextView) convertView.findViewById(R.id.inflater_sport_item_tv_specialization);
        }
    }

    public interface SportsAdapterListener {

        void showAlertDialog();
    }
}