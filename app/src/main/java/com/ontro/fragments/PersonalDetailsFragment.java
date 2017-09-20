package com.ontro.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.ontro.Constants;
import com.ontro.ProfileDataBaseHelper;
import com.ontro.R;
import com.ontro.RoundRectCornerImageView;
import com.ontro.adapters.SpinnneradapterPersonalDetailsAdapter;
import com.ontro.dto.PersonalDetailsSpinnerModel;
import com.ontro.dto.PlayerProfileData;
import com.ontro.dto.PlayerProfilePersonalModel;
import com.ontro.utils.CommonUtils;
import com.ontro.utils.PreferenceHelper;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PersonalDetailsFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TextView.OnEditorActionListener {
    private Spinner genderspinner;
    private RelativeLayout gender_container;
    private ArrayList<PersonalDetailsSpinnerModel> arrayList = new ArrayList<>();
    private String[] genders = new String[]{"Gender", "Male", "Female", "Transgender"};
    private TextView mDobView;
    private ImageView image1;
    private ImageView image2;
    private TextView mLocationNameView, mCityNameView;
    private String gender_id = "0", locationid = "";
    private PreferenceHelper preferenceHelper;
    private RoundRectCornerImageView profile_upload;
    private EditText mUserNameView, mPhoneNumberView, mPlayerHeightView;
    private FrameLayout profileFrameLayout;
    private String playerHeight = "";


    public static Fragment newInstance(String value) {
        PersonalDetailsFragment personalDetailsFragment = new PersonalDetailsFragment();
        Bundle args = new Bundle();
        args.putString(Constants.BundleKeys.PROFILE_COMPLETION, value);
        personalDetailsFragment.setArguments(args);
        return personalDetailsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_personal_details, container, false);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        Typeface typeface_regular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto_regular.ttf");
        preferenceHelper = new PreferenceHelper(getActivity(), Constants.APP_NAME, 0);

        genderspinner = (Spinner) v.findViewById(R.id.pd_gender);
        mDobView = (TextView) v.findViewById(R.id.pd_dob);
        LinearLayout outtouch = (LinearLayout) v.findViewById(R.id.outtouch);
        Button next = (Button) v.findViewById(R.id.next);
        mLocationNameView = (TextView) v.findViewById(R.id.location_edit);
        mCityNameView = (TextView) v.findViewById(R.id.city_edit);
        mUserNameView = (EditText) v.findViewById(R.id.pd_username);
        mPlayerHeightView = (EditText) v.findViewById(R.id.pd_height);
        mPhoneNumberView = (EditText) v.findViewById(R.id.pd_phonenumber);
        image1 = (ImageView) getActivity().findViewById(R.id.image1);
        image2 = (ImageView) getActivity().findViewById(R.id.image2);
        profile_upload = (RoundRectCornerImageView) getActivity().findViewById(R.id.profile_upload);
        profileFrameLayout = (FrameLayout) getActivity().findViewById(R.id.profile_container);
        profileFrameLayout.setVisibility(View.VISIBLE);

        TextView mid_line = (TextView) getActivity().findViewById(R.id.mid_line);
        gender_container = (RelativeLayout) v.findViewById(R.id.gender_container);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // only for LOLLIPOP and newer versions
            next.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_bg));
        } else {
            next.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_bg_normal));
        }

        TextView profile_completion_text = (TextView) getActivity().findViewById(R.id.profile_completion_text);
        profile_completion_text.setText(R.string.pc_title1);


//        image1.bringToFront();
        image1.setImageResource(R.drawable.s1);
        image2.setImageResource(R.drawable.s4);


        FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params2.width = (int) (dm.widthPixels * 0.2);
        params2.height = ((int) (dm.heightPixels * 0.1));
        params2.gravity = Gravity.CENTER_VERTICAL;
        image1.setLayoutParams(params2);

        mid_line.setBackgroundColor(Color.parseColor("#283138"));
        FrameLayout.LayoutParams mid_line_param = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        mid_line_param.width = (int) (dm.widthPixels * 0.17);
        mid_line_param.height = 3;
        mid_line_param.gravity = Gravity.CENTER_VERTICAL;
        mid_line_param.leftMargin = (int) (dm.widthPixels * 0.18);
        mid_line.setLayoutParams(mid_line_param);

        FrameLayout.LayoutParams params3 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params3.width = (int) (dm.widthPixels * 0.1);
        params3.height = ((int) (dm.heightPixels * 0.05));
        params3.gravity = Gravity.CENTER_VERTICAL;
        params3.leftMargin = (int) (dm.widthPixels * 0.35);
        image2.setLayoutParams(params3);

        for (String gender : genders) {
            PersonalDetailsSpinnerModel m = new PersonalDetailsSpinnerModel();
            m.setGender(gender);
            arrayList.add(m);
        }

        ArrayAdapter<PersonalDetailsSpinnerModel> arrayAdapter = new SpinnneradapterPersonalDetailsAdapter(getActivity(), arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderspinner.setAdapter(arrayAdapter);

        if (preferenceHelper.contains("user_name")) {
            mUserNameView.setText(preferenceHelper.getString("user_name", ""));
        }
        if (preferenceHelper.contains("user_profilepic")) {
            String pic = preferenceHelper.getString("user_profilepic", "");
            if (!pic.equals("")) {
                Glide.with(getActivity()).load(preferenceHelper.getString("user_profilepic", "")).placeholder(R.drawable.profiledefaultimg).dontAnimate().into(profile_upload);
            } else {
                Glide.with(getActivity()).load(R.drawable.profiledefaultimg).dontAnimate().into(profile_upload);
            }
        }
        if (preferenceHelper.contains("user_phone")) {
            mPhoneNumberView.setText(preferenceHelper.getString("user_phone", ""));
        }
        if (preferenceHelper.contains("user_birthday")) {
            mDobView.setText(preferenceHelper.getString("user_birthday", ""));
        }
        if (preferenceHelper.contains("gender")) {
            String gender = preferenceHelper.getString("gender", "");
            for (int i = 0; i < genders.length; i++) {
                if (gender != null && gender.equalsIgnoreCase(genders[i])) {
                    genderspinner.setSelection(i);
                    break;
                }
            }
        }

        if (null != getArguments()) {
            if (null != getArguments().getString(Constants.BundleKeys.PROFILE_COMPLETION)) {
                if (getArguments().getString(Constants.BundleKeys.PROFILE_COMPLETION).equalsIgnoreCase(Constants.Messages.PLAYER_PROFILE)) {
                    ProfileDataBaseHelper profileDataBaseHelper = new ProfileDataBaseHelper(getActivity());
                    PlayerProfileData profileData = profileDataBaseHelper.getProfile();
                    String playerInfo = profileData.getPlayerInfo();
                    if (playerInfo != null) {
                        PlayerProfilePersonalModel profilePersonalModel = new Gson().fromJson(playerInfo, PlayerProfilePersonalModel.class);
                        mUserNameView.setText(profilePersonalModel.getPlayerName());
                        mPhoneNumberView.setText(String.valueOf(profilePersonalModel.getPhone()));
                        mDobView.setText(CommonUtils.convertDateFormat(profilePersonalModel.getPlayerDob(), "yyyy-mm-dd", "dd-mm-yyyy"));
                        mLocationNameView.setText(profilePersonalModel.getLocationName());
                        mCityNameView.setText(profilePersonalModel.getCityName());
                        mPlayerHeightView.setText(String.valueOf(profilePersonalModel.getHeight()));
                        preferenceHelper.save("user_location", profilePersonalModel.getLocality());
                        Glide.with(getActivity()).load(profilePersonalModel.getProfileImage()).placeholder(R.drawable.profiledefaultimg).dontAnimate().into(profile_upload);
                        CommonUtils.locationid = profilePersonalModel.getLocality();
                        CommonUtils.cityid = profilePersonalModel.getCity();
                        String gender = profilePersonalModel.getGender();
                        for (int i = 0; i < arrayList.size(); i++) {
                            if (gender != null && gender.equalsIgnoreCase(arrayList.get(i).getGender())) {
                                genderspinner.setSelection(i);
                                break;
                            }
                        }
                    }
                }
            }
        }

        genderspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender_container.setFocusable(true);
                gender_container.setClickable(true);
                genderspinner.setSelection(position);
                if (position == 1) {
                    gender_id = "male";
                } else if (position == 2) {
                    gender_id = "female";
                } else if (position == 3) {
                    gender_id = "transgender";
                } else {
                    gender_id = "0";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mUserNameView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        mPhoneNumberView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        next.setTypeface(typeface_regular);

        mDobView.setOnClickListener(this);
        next.setOnClickListener(this);
        mLocationNameView.setOnClickListener(this);
        mCityNameView.setOnClickListener(this);
        mPlayerHeightView.setOnEditorActionListener(this);
        outtouch.setOnClickListener(this);
        return v;
    }


    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onResume() {
        super.onResume();
//        image1.bringToFront();
//        image1.setImageResource(R.drawable.s1);
//        image2.setImageResource(R.drawable.s4);
        if (preferenceHelper.contains("user_location")) {
            locationid = preferenceHelper.getString("user_location", "");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pd_dob:
                mDobView.setFocusable(true);
                mDobView.setFocusableInTouchMode(true);
                mDobView.requestFocus();
                mDobView.setError(null);

                Calendar now = Calendar.getInstance();
                DatePickerDialog date = DatePickerDialog.newInstance(
                        PersonalDetailsFragment.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                date.setMaxDate(now);
                date.show(getActivity().getFragmentManager(), "Datepickerdialog");

                /*Calendar now = Calendar.getInstance();

                DatePickerDialog date = DatePickerDialog.newInstance(
                        PersonalDetailsFragment.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH));
*/
               /* date.setBackgroundColor(Color.parseColor("#E9E9E9"));
                date.setHeaderColor(Color.parseColor("#18212A"));
                date.setHeaderTextColorSelected(Color.parseColor("#FFFFFF"));
                date.setHeaderTextColorUnselected(Color.parseColor("#E2E2E2"));
                date.setHeaderTextDark(true);
                date.setAccentColor(Color.parseColor("#18212A"));
                date.setMaxDate(now);
                date.show(getFragmentManager(), "show");*/
                break;
            case R.id.next:
                locationid = preferenceHelper.getString("user_location", "");
                navigateToSportFragment();
                break;
            case R.id.location_edit:
                if (CommonUtils.cityid.length() != 0) {
                    CommonUtils.locationdialog(getActivity(), mLocationNameView, 2);
                } else {
                    Toast.makeText(getActivity(), "Please select city", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.city_edit:
                CommonUtils.locationdialog(getActivity(), mCityNameView, 1);
                break;
            case R.id.outtouch:
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                break;
        }
    }

    private void navigateToSportFragment() {
        playerHeight = mPlayerHeightView.getText().toString().trim();
        if (mUserNameView.getText().toString().trim().length() == 0) {
            Toast.makeText(getActivity(), Constants.Messages.ENTER_NAME, Toast.LENGTH_SHORT).show();
        } else if (mPhoneNumberView.getText().toString().trim().length() == 0) {
            Toast.makeText(getActivity(), Constants.Messages.ENTER_MOBILE_NUMBER, Toast.LENGTH_SHORT).show();
        } else if (mPhoneNumberView.getText().toString().trim().length() != 0 && mPhoneNumberView.getText().toString().trim().length() < 10) {
            Toast.makeText(getActivity(), Constants.Messages.ENTER_VALID_MOBILE_NO, Toast.LENGTH_SHORT).show();
        } else if (mDobView.getText().toString().trim().length() == 0) {
            Toast.makeText(getActivity(), Constants.Messages.SELECT_BIRTH_DATE, Toast.LENGTH_SHORT).show();
        } else if (invalidDob(mDobView.getText().toString().trim())) {
            Toast.makeText(getActivity(), Constants.Messages.INVALID_BIRTH_DATE, Toast.LENGTH_SHORT).show();
        } else if (gender_id.equals("0")) {
            Toast.makeText(getActivity(), Constants.Messages.SELECT_GENDER, Toast.LENGTH_SHORT).show();
        } else if (CommonUtils.cityid.length() == 0 || mCityNameView.getText().toString().trim().length() == 0) {
            Toast.makeText(getActivity(), Constants.Messages.SELECT_CITY, Toast.LENGTH_SHORT).show();
        } else if (CommonUtils.locationid.length() == 0 || mLocationNameView.getText().toString().trim().length() == 0) {
            Toast.makeText(getActivity(), Constants.Messages.SELECT_LOCATION, Toast.LENGTH_SHORT).show();
        } else if (!CommonUtils.validate(playerHeight)) {
            Toast.makeText(getActivity(), Constants.Messages.INVALID_HEIGHT, Toast.LENGTH_SHORT).show();
        } else {
            preferenceHelper.save("user_name", mUserNameView.getText().toString().trim());
            preferenceHelper.save("user_height", mPlayerHeightView.getText().toString().trim());
            preferenceHelper.save("user_phone", mPhoneNumberView.getText().toString().trim());
            preferenceHelper.save("user_dob", mDobView.getText().toString().trim());
            preferenceHelper.save("user_gender", gender_id);
            preferenceHelper.save("user_city", CommonUtils.cityid);
            preferenceHelper.save("user_city_name", mCityNameView.getText().toString().trim());
            preferenceHelper.save("user_location", CommonUtils.locationid);
            preferenceHelper.save("user_location_name", mLocationNameView.getText().toString().trim());
            try {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment sportsFragment = SportsFragment.newInstance(getArguments().getString(Constants.BundleKeys.PROFILE_COMPLETION));
                fragmentTransaction.add(R.id.profiledata, sportsFragment, "sports");
                fragmentTransaction.hide(PersonalDetailsFragment.this);
                fragmentTransaction.addToBackStack(PersonalDetailsFragment.class.getName());
                fragmentTransaction.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void nextpagecall() {
        preferenceHelper.save("user_name", mUserNameView.getText().toString().trim());
        preferenceHelper.save("user_height", mPlayerHeightView.getText().toString().trim());
        preferenceHelper.save("user_phone", mPhoneNumberView.getText().toString().trim());
        preferenceHelper.save("user_dob", mDobView.getText().toString().trim());
        preferenceHelper.save("user_gender", gender_id);
        preferenceHelper.save("user_city", CommonUtils.cityid);
        preferenceHelper.save("user_location", CommonUtils.locationid);
        preferenceHelper.save("user_location_name", mLocationNameView.getText().toString().trim());
        try {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment sportsFragment = SportsFragment.newInstance(getArguments().getString(Constants.BundleKeys.PROFILE_COMPLETION));
            fragmentTransaction.add(R.id.profiledata, sportsFragment, "sports");
            fragmentTransaction.hide(PersonalDetailsFragment.this);
            fragmentTransaction.addToBackStack(PersonalDetailsFragment.class.getName());
            fragmentTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean invalidDob(String playerDob) {
        String dateOfBirth = playerDob.replace("-", "");
        int year = Integer.valueOf(dateOfBirth.substring(4, 8));
        int month = Integer.valueOf(dateOfBirth.substring(2, 4));
        int day = Integer.valueOf(dateOfBirth.substring(0, 2));
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob.set(year, month, day);
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        return age < 10;
    }

    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        Calendar cal = new java.util.GregorianCalendar();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        Date date = cal.getTime();
        String datetime = format.format(date);
        mDobView.setText(datetime);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            navigateToSportFragment();
            return true;
        }
        return false;
    }
}