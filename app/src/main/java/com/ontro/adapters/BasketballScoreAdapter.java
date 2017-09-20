package com.ontro.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.ontro.R;
import com.ontro.dto.BasketballScoreUpdateDTO;

import java.util.ArrayList;

public class BasketballScoreAdapter extends RecyclerView.Adapter<BasketballScoreAdapter.ViewHolder> {
    private ArrayList<BasketballScoreUpdateDTO> mBasketballScoreUpdateDTOs = new ArrayList<>();
    private Context context;
    private String scoreUpdateStatus;

    public BasketballScoreAdapter(Activity activity,ArrayList<BasketballScoreUpdateDTO> list, String status) {
        this.context = activity;
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        mBasketballScoreUpdateDTOs = list;
        ArrayList<String> selected_state = new ArrayList<>();
        scoreUpdateStatus = status;

        int count = 0;
        for (BasketballScoreUpdateDTO updateDTO:list) {
            selected_state.add(count,updateDTO.getIs_played());
            count++;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_basketball_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Drawable selected = context.getResources().getDrawable(R.drawable.cb_selected);
        Drawable deseleted = context.getResources().getDrawable(R.drawable.cb_deselected);

        final BasketballScoreUpdateDTO updateDTO = mBasketballScoreUpdateDTOs.get(position);
        final int pos = holder.getAdapterPosition();
        holder.mName.setText(mBasketballScoreUpdateDTOs.get(pos).getName());
        holder.et_point_one.setText(mBasketballScoreUpdateDTOs.get(pos).getPoint_one());
        holder.et_point_two.setText(mBasketballScoreUpdateDTOs.get(pos).getPoint_two());
        holder.et_point_three.setText(mBasketballScoreUpdateDTOs.get(pos).getPoint_three());
        holder.et_point_one.setSelectAllOnFocus(true);
        holder.et_point_two.setSelectAllOnFocus(true);
        holder.et_point_three.setSelectAllOnFocus(true);

        // Player selection
        if (updateDTO.getIs_played().equalsIgnoreCase("0")) {
            holder.cb_name.setCompoundDrawables(deseleted,null,null,null);
        } else {
            holder.cb_name.setCompoundDrawables(selected,null,null,null);
        }

        holder.cb_name.setTag(position);
        holder.et_point_one.setTag(position);
        holder.et_point_two.setTag(position);
        holder.et_point_three.setTag(position);

        holder.et_point_one.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String str = String.valueOf(charSequence);
                if (str.equalsIgnoreCase("") || str.equalsIgnoreCase("0")) {
                    updateDTO.setPoint_one("0");
                }else{
                    updateDTO.setPoint_one(str);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        holder.et_point_two.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String str = String.valueOf(charSequence);
                if (str.equalsIgnoreCase("") || str.equalsIgnoreCase("0")) {
                    updateDTO.setPoint_two("0");
                }else{
                    updateDTO.setPoint_two(str);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        holder.et_point_three.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String str = String.valueOf(charSequence);
                if (str.equalsIgnoreCase("") || str.equalsIgnoreCase("0")) {
                    updateDTO.setPoint_three("0");
                }else{
                    updateDTO.setPoint_three(str);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        holder.cb_name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(mBasketballScoreUpdateDTOs.get(pos).getIs_played().equalsIgnoreCase("0")){
                    // EnableRow(pos);
                    BasketballScoreUpdateDTO updateDTO = new BasketballScoreUpdateDTO(mBasketballScoreUpdateDTOs.get(pos).getId(), mBasketballScoreUpdateDTOs.get(pos).getName(), mBasketballScoreUpdateDTOs.get(pos).getPoint_one(), mBasketballScoreUpdateDTOs.get(pos).getPoint_two(), mBasketballScoreUpdateDTOs.get(pos).getPoint_three(), "1");
                    remove(pos);
                    insert(updateDTO, pos);
                    notifyItemChanged(pos);
                }else{
                    // DisableRow(pos);
                    BasketballScoreUpdateDTO updateDTO = new BasketballScoreUpdateDTO(mBasketballScoreUpdateDTOs.get(pos).getId(), mBasketballScoreUpdateDTOs.get(pos).getName(), mBasketballScoreUpdateDTOs.get(pos).getPoint_one(), mBasketballScoreUpdateDTOs.get(pos).getPoint_two(), mBasketballScoreUpdateDTOs.get(pos).getPoint_three(), "0");
                    remove(pos);
                    insert(updateDTO, pos);
                    notifyItemChanged(pos);
                }
            }
        });

      /*  holder.cb_name.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                BasketballScoreUpdateDTO updateDTO = (BasketballScoreUpdateDTO) cb.getTag();
                updateDTO.setIs_played(cb.isChecked());
               *//* Student contact = (Student) cb.getTag();
                contact.setSelected(cb.isChecked());
                stList.get(pos).setSelected(cb.isChecked());*//*
            }
        });*/

        Object present = updateDTO.getIs_played();
        if (present != null && present.equals("1")) {
            holder.cb_name.setAlpha(1f);
            holder.mName.setTextColor(context.getResources().getColor(R.color.white));
            holder.et_point_one.setEnabled(true);
            holder.et_point_one.setTextColor(context.getResources().getColor(R.color.text_normal));

            holder.et_point_two.setEnabled(true);
            holder.et_point_two.setTextColor(context.getResources().getColor(R.color.text_normal));

            holder.et_point_three.setEnabled(true);
            holder.et_point_three.setTextColor(context.getResources().getColor(R.color.text_normal));
        } else {
            holder.cb_name.setAlpha(0.5f);
            holder.cb_name.setSelected(false);
            holder.mName.setTextColor(context.getResources().getColor(R.color.text_unselected));
            holder.et_point_one.setEnabled(false);
            holder.et_point_one.setTextColor(context.getResources().getColor(R.color.text_unselected));

            holder.et_point_two.setEnabled(false);
            holder.et_point_two.setTextColor(context.getResources().getColor(R.color.text_unselected));

            holder.et_point_three.setEnabled(false);
            holder.et_point_three.setTextColor(context.getResources().getColor(R.color.text_unselected));
        }

        // score already updated. Disable all fields
        if (scoreUpdateStatus.equalsIgnoreCase("1")){
            holder.mName.setTextColor(context.getResources().getColor(R.color.text_unselected));
            holder.et_point_one.setEnabled(false);
            holder.et_point_one.setTextColor(context.getResources().getColor(R.color.text_unselected));

            holder.et_point_two.setEnabled(false);
            holder.et_point_two.setTextColor(context.getResources().getColor(R.color.text_unselected));

            holder.et_point_three.setEnabled(false);
            holder.et_point_three.setTextColor(context.getResources().getColor(R.color.text_unselected));
            holder.cb_name.setClickable(false);
            holder.cb_name.setAlpha(0.5f);
        }
    }

    private void insert(BasketballScoreUpdateDTO basketballScoreUpdateDTO, int position) {
        mBasketballScoreUpdateDTOs.add(position, basketballScoreUpdateDTO);
        notifyItemInserted(position);
    }

    private void remove(int position) {
        mBasketballScoreUpdateDTOs.remove(position);
        notifyItemRemoved(position);
    }


    @Override
    public int getItemCount() {
        return mBasketballScoreUpdateDTOs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private EditText et_point_one, et_point_two, et_point_three;
        private CheckBox cb_name;
        private TextView mName;

        public ViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.tv_player_name);
            et_point_one = (EditText) itemView.findViewById(R.id.et_point_1);
            et_point_two = (EditText) itemView.findViewById(R.id.et_point_2);
            et_point_three = (EditText) itemView.findViewById(R.id.et_point_3);
            cb_name = (CheckBox) itemView.findViewById(R.id.chk_name);
        }
    }

    /*private void DisableRow(int i) {
        holder.cb_name.setAlpha(0.5f);
        holder.mName.setTextColor(context.getResources().getColor(R.color.text_unselected));
        holder.et_point_one.setText("00");
        holder.et_point_one.setEnabled(false);
        holder.et_point_one.setTextColor(context.getResources().getColor(R.color.text_unselected));

        holder.et_point_two.setText("00");
        holder.et_point_two.setEnabled(false);
        holder.et_point_two.setTextColor(context.getResources().getColor(R.color.text_unselected));

        holder.et_point_three.setText("00");
        holder.et_point_three.setEnabled(false);
        holder.et_point_three.setTextColor(context.getResources().getColor(R.color.text_unselected));
    }

    private void EnableRow(int i) {
        holder.cb_name.setAlpha(1f);
        holder.mName.setTextColor(context.getResources().getColor(R.color.white));
        holder.et_point_one.setText("00");
        holder.et_point_one.setEnabled(true);
        holder.et_point_one.setTextColor(context.getResources().getColor(R.color.text_normal));

        holder.et_point_two.setText("00");
        holder.et_point_two.setEnabled(true);
        holder.et_point_two.setTextColor(context.getResources().getColor(R.color.text_normal));

        holder.et_point_three.setText("00");
        holder.et_point_three.setEnabled(true);
        holder.et_point_three.setTextColor(context.getResources().getColor(R.color.text_normal));
    }*/

    public ArrayList<BasketballScoreUpdateDTO> Updatedlist(){
        return mBasketballScoreUpdateDTOs;
    }
}
