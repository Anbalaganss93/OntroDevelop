package com.ontro.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.ontro.Constants;
import com.ontro.HomeActivity;
import com.ontro.OTPActivity;
import com.ontro.PlayerProfileActivity;
import com.ontro.ProfileCompletionActivity;
import com.ontro.ProfileDataBaseHelper;
import com.ontro.R;
import com.ontro.adapters.SportsAdapter;
import com.ontro.dto.PlayerPersonalSport;
import com.ontro.dto.PlayerProfileData;
import com.ontro.dto.PlayerProfilePersonalModel;
import com.ontro.dto.ProfileCompletionModel;
import com.ontro.dto.SportModel;
import com.ontro.rest.ApiClient;
import com.ontro.rest.ApiInterface;
import com.ontro.utils.CommonUtils;
import com.ontro.utils.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SportsFragment extends Fragment implements View.OnClickListener, SportsAdapter.SportsAdapterListener {
    RadioButton r_team, r_individual, r_both;
    private String mSportSelection = "";
    private NestedScrollView scrollView;
    private ArrayList<String> selectedfavort_sport = new ArrayList<>();
    private String[] typeofsport = new String[]{"team", "individual", "both"};
    private int[] sport_icon = new int[]{R.drawable.ic_football_white, R.drawable.ic_cricket_white, R.drawable.ic_tennis_white, R.drawable.ic_basketball_white, R.drawable.ic_badminton_white, R.drawable.ic_volley_white, R.drawable.ic_carrom_white};
    private String[] sportname = new String[]{"Football", "Cricket", "Tennis", "Basketball", "Badminton", "Volleyball", "Carrom"};
    private List<SportModel> arrayList = new ArrayList<>();
    private RadioGroup radiogroup;
    private PreferenceHelper preferenceHelper;
    private ProfileDataBaseHelper mProfileDataBaseHelper;
    private ApiInterface apiInterface;
    private Dialog progress;
    private SportsAdapter mSportsAdapter;
    private FrameLayout profileFrameLayout;
    private JSONArray myarray = new JSONArray();
    private List<String> favsport;
    private MixpanelAPI mMixpanel;

    public static Fragment newInstance(String value) {
        SportsFragment sportsFragment = new SportsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BundleKeys.PROFILE_COMPLETION, value);
        sportsFragment.setArguments(bundle);
        return sportsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_sports, container, false);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        mMixpanel = MixpanelAPI.getInstance(getActivity(), getResources().getString(R.string.mixpanel_token));
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        preferenceHelper = new PreferenceHelper(getActivity(), Constants.APP_NAME, 0);

        progress = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progress.setContentView(R.layout.progressdialog_layout);

        Typeface typeface_regular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto_regular.ttf");
        RecyclerView activity_sports_rv_favourtsport = (RecyclerView) v.findViewById(R.id.activity_sports_rv_favourtsport);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        activity_sports_rv_favourtsport.setLayoutManager(mLayoutManager);
        activity_sports_rv_favourtsport.setNestedScrollingEnabled(false);

        scrollView = (NestedScrollView) v.findViewById(R.id.scrollView);
        Button btn_continue = (Button) v.findViewById(R.id.save);
        radiogroup = (RadioGroup) v.findViewById(R.id.radiogroupie);
        r_team = (RadioButton) v.findViewById(R.id.team);
        r_individual = (RadioButton) v.findViewById(R.id.individual);
        r_both = (RadioButton) v.findViewById(R.id.both);

        profileFrameLayout = (FrameLayout) getActivity().findViewById(R.id.profile_container);
        profileFrameLayout.setVisibility(View.GONE);

        ImageView image1 = (ImageView) getActivity().findViewById(R.id.image1);
        ImageView image2 = (ImageView) getActivity().findViewById(R.id.image2);
        TextView mid_line = (TextView) getActivity().findViewById(R.id.mid_line);

        for (int i = 0; i < 7; i++) {
            try {
                myarray.put(i, "");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        btn_continue.setTypeface(typeface_regular);
        r_team.setTypeface(typeface_regular);
        r_both.setTypeface(typeface_regular);
        r_individual.setTypeface(typeface_regular);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // only for LOLLIPOP and newer versions
            btn_continue.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_bg));
        } else {
            btn_continue.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_bg_normal));
        }
        TextView profile_completion_text = (TextView) getActivity().findViewById(R.id.profile_completion_text);
        profile_completion_text.setText(R.string.pc_title2);

        image1.setImageResource(R.drawable.s3);
        FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params2.width = (int) (dm.widthPixels * 0.1);
        params2.height = ((int) (dm.heightPixels * 0.05));
        params2.gravity = Gravity.CENTER_VERTICAL;
        image1.setLayoutParams(params2);

        mid_line.setBackgroundColor(Color.parseColor("#172934"));
        FrameLayout.LayoutParams mid_line_param = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        mid_line_param.width = (int) (dm.widthPixels * 0.17);
        mid_line_param.height = 3;
        mid_line_param.gravity = Gravity.CENTER_VERTICAL;
        mid_line_param.leftMargin = (int) (dm.widthPixels * 0.10);
        mid_line.setLayoutParams(mid_line_param);

        image2.setImageResource(R.drawable.s2);
        FrameLayout.LayoutParams params3 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params3.width = (int) (dm.widthPixels * 0.2);
        params3.height = ((int) (dm.heightPixels * 0.1));
        params3.gravity = Gravity.CENTER_VERTICAL;
        params3.leftMargin = (int) (dm.widthPixels * 0.24);
        image2.setLayoutParams(params3);
        for (int k = 0; k < 7; k++) {
            selectedfavort_sport.add(k, "");
        }
        Animation anim = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out);
        image2.startAnimation(anim);

        for (int i = 0; i < sportname.length; i++) {
            SportModel m = new SportModel();
            m.setSportimage(sport_icon[i]);
            m.setSportname(sportname[i]);
            m.setSelectedposition(0);
            m.setmPlayerposition(GetSportArraylist(sportname[i]));
            m.setPlayerpositionstatus(GetSportID(i));
            m.setSelected(0);
            arrayList.add(m);
        }
        mSportsAdapter = new SportsAdapter(getActivity(), arrayList, selectedfavort_sport, this);
        activity_sports_rv_favourtsport.setAdapter(mSportsAdapter);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollTo(0, 0);
            }
        });

        if (null != getArguments().getString(Constants.BundleKeys.PROFILE_COMPLETION)) {
            if (getArguments().getString(Constants.BundleKeys.PROFILE_COMPLETION).equalsIgnoreCase(Constants.Messages.PLAYER_PROFILE)) {
                mProfileDataBaseHelper = new ProfileDataBaseHelper(getActivity());
                PlayerProfileData profileData = mProfileDataBaseHelper.getProfile();
                String playerInfo = profileData.getPlayerInfo();
                if (!playerInfo.equals("")) {
                    PlayerProfilePersonalModel profilePersonalModel = new Gson().fromJson(playerInfo, PlayerProfilePersonalModel.class);
                    for (int position = 0; position < typeofsport.length; position++) {
                        if (profilePersonalModel.getSportType().equalsIgnoreCase(typeofsport[position])) {
                            radiogroup.clearCheck();
                            final int finalPosition = position;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ((RadioButton) radiogroup.getChildAt(finalPosition)).setChecked(true);
                                }
                            }, 300);

                            break;
                        }
                    }
                    String selectedSports[] = profilePersonalModel.getPlayerSports().split(",");
                    for (String selectedSport : selectedSports) {
                        for (int j = 0; j < sportname.length; j++) {
                            int sportId = Integer.parseInt(selectedSport);
                            if (sportId == CommonUtils.sportIdCheck(sportname[j])) {
                                SportModel sportModel = new SportModel();
                                sportModel.setSportimage(sport_icon[j]);
                                sportModel.setSportname(sportname[j]);
                                sportModel.setmPlayerposition(GetSportArraylist(sportname[j]));
                                sportModel.setPlayerpositionstatus(GetSportID(j));
                                sportModel.setSelected(1);
                                for (int k = 0; k < profilePersonalModel.getSports().size(); k++) {
                                    PlayerPersonalSport playerPersonalSport = profilePersonalModel.getSports().get(k);
                                    if (playerPersonalSport.getSport() == sportId) {
                                        List<String> playerPersonalInfos = GetSportIndividualList(playerPersonalSport.getSport());
                                        sportModel.setHaveTeam(playerPersonalSport.getHaveTeam());
                                        for (int info = 0; info < playerPersonalInfos.size(); info++) {
                                            if (playerPersonalSport.getHandedness().equalsIgnoreCase(playerPersonalInfos.get(info))) {
                                                sportModel.setSelectedposition(info);
                                            } else if (playerPersonalSport.getPosition().equalsIgnoreCase(playerPersonalInfos.get(info))) {
                                                sportModel.setSelectedposition(info);
                                            }
                                        }
                                    }
                                }
                                arrayList.remove(j);
                                arrayList.add(j, sportModel);

                                break;
                            }
                        }
                    }
                }
            }
        }

        btn_continue.setTypeface(typeface_regular);
        btn_continue.setOnClickListener(this);
        return v;
    }

    private String GetSportID(int pos) {
        String handedness_or_position = "";
        switch (pos) {
            case 0:
                handedness_or_position = "1";
                break;
            case 1:
                handedness_or_position = "0";
                break;
            case 2:
                handedness_or_position = "0";
                break;
            case 3:
                handedness_or_position = "1";
                break;
            case 4:
                handedness_or_position = "0";
                break;
            case 5:
                handedness_or_position = "1";
                break;
            case 6:
                handedness_or_position = "0";
                break;
        }
        return handedness_or_position;
    }

    private List<String> GetSportArraylist(String gamename) {
        List<String> arrayList = null;
        switch (gamename) {
            case "Football":
                arrayList = Arrays.asList(getResources().getStringArray(R.array.football_handedness));
                break;
            case "Cricket":
                arrayList = Arrays.asList(getResources().getStringArray(R.array.position));
                break;
            case "Tennis":
                arrayList = Arrays.asList(getResources().getStringArray(R.array.position));
                break;
            case "Basketball":
                arrayList = Arrays.asList(getResources().getStringArray(R.array.basketball_handedness));
                break;
            case "Badminton":
                arrayList = Arrays.asList(getResources().getStringArray(R.array.position));
                break;
            case "Volleyball":
                arrayList = Arrays.asList(getResources().getStringArray(R.array.volleyball_handedness));
                break;
            case "Carrom":
                arrayList = Arrays.asList(getResources().getStringArray(R.array.position));
                break;
        }
        return arrayList;
    }

    private List<String> GetSportIndividualList(int sportId) {
        List<String> arrayList = null;
        switch (sportId) {
            case 5:
                arrayList = Arrays.asList(getResources().getStringArray(R.array.football_handedness));
                break;
            case 4:
                arrayList = Arrays.asList(getResources().getStringArray(R.array.position));
                break;
            case 6:
                arrayList = Arrays.asList(getResources().getStringArray(R.array.position));
                break;
            case 2:
                arrayList = Arrays.asList(getResources().getStringArray(R.array.basketball_handedness));
                break;
            case 1:
                arrayList = Arrays.asList(getResources().getStringArray(R.array.position));
                break;
            case 7:
                arrayList = Arrays.asList(getResources().getStringArray(R.array.volleyball_handedness));
                break;
            case 3:
                arrayList = Arrays.asList(getResources().getStringArray(R.array.position));
                break;
        }
        return arrayList;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save:
                mSportSelection = preferenceHelper.getString("user_favouriteofsport", "");
                if (validation()) {
                    if (CommonUtils.isNetworkAvailable(getActivity())) {
                        int index = radiogroup.indexOfChild(getActivity().findViewById(radiogroup.getCheckedRadioButtonId()));
                        String sports = "";
                        try {
                            favsport = new ArrayList<>();
                            JSONArray object = new JSONArray(mSportSelection);
                            for (int m = 0; m < object.length(); m++) {
                                favsport.add(object.getJSONObject(m).getString("sport"));
                                if (m == 0) {
                                    sports = object.getJSONObject(m).getString("sport");
                                } else {
                                    sports = sports + "," + object.getJSONObject(m).getString("sport");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        preferenceHelper.save("user_typeofsport", "team");
                        preferenceHelper.save("user_favouriteofsport", mSportSelection);
                        preferenceHelper.save("player_sports", sports);
                        updateprofile_servercall();
                    } else {
                        Toast.makeText(getActivity(), Constants.INTERNET_ERROR, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private void sportMulitSelectArray(JSONArray jsonArr) {
        JSONArray array = new JSONArray();
        if (jsonArr != null) {
            for (int i = 0; i < jsonArr.length(); i++) {
                String element = null;
                try {
                    element = String.valueOf(jsonArr.get(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (!element.isEmpty()) {
                    try {
                        array.put(jsonArr.getJSONObject(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            mSportSelection = array.length() != 0 ? String.valueOf(array) : "";
        }
    }

    private boolean validation() {
        if (mSportSelection.length() == 0) {
            Toast.makeText(getActivity(), "Select any one of your favourite sports", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void updateprofile_servercall() {
        progress.show();
        final ProfileCompletionModel profileCompletionModel = new ProfileCompletionModel();
        profileCompletionModel.setName(preferenceHelper.getString("user_name", ""));
        profileCompletionModel.setPhone(preferenceHelper.getString("user_phone", ""));
        profileCompletionModel.setDob(preferenceHelper.getString("user_dob", ""));
        profileCompletionModel.setGender(preferenceHelper.getString("user_gender", ""));
        profileCompletionModel.setLocality(preferenceHelper.getString("user_location", ""));
        profileCompletionModel.setCity(preferenceHelper.getString("user_city", ""));
        profileCompletionModel.setSport_type(preferenceHelper.getString("user_typeofsport", ""));
        profileCompletionModel.setFav_sport(preferenceHelper.getString("user_favouriteofsport", ""));
        profileCompletionModel.setHeight(preferenceHelper.getString("user_height", ""));
        profileCompletionModel.setProfileimage(ProfileCompletionActivity.img_str);
        try {
            JSONObject eventJsonObject = new JSONObject();
            eventJsonObject.put("UserName",  profileCompletionModel.getName());
            eventJsonObject.put("UserEmail", preferenceHelper.getString("user_email", ""));
            eventJsonObject.put("DOB", profileCompletionModel.getDob());
            eventJsonObject.put("Gender", profileCompletionModel.getGender());
            eventJsonObject.put("City", preferenceHelper.getString("user_city_name", ""));
            eventJsonObject.put("Locality", preferenceHelper.getString("user_location_name", ""));
            JSONArray sportArray = new JSONArray(profileCompletionModel.getFav_sport());
            StringBuilder sports = new StringBuilder();
            if(sportArray != null) {
                for(int i = 0; i < sportArray.length(); i++) {
                    String sport = sportArray.getJSONObject(i).getString("sport");
                    sports.append(CommonUtils.sportNameCheck(sport));
                    if(i != (sportArray.length()-1)) {
                        sports.append(", ");
                    }
                }
            }
            eventJsonObject.put("Sport", sports);
            mMixpanel.track("UserRegistration", eventJsonObject);
        } catch (JSONException e) {
            Log.e("Ontro", "Unable to add properties to JSONObject", e);
        }

        String auth_token = "Bearer " + preferenceHelper.getString("user_token", "");

        Call<ResponseBody> call = apiInterface.Profile_completion(auth_token, profileCompletionModel);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.code() == 200) {
                        if (response.body() != null) {
                            String data = response.body().string();
                            JSONObject json = new JSONObject(data);
                            String message = json.getString("message");
                            preferenceHelper.save("is_profile", "1");
                            preferenceHelper.save("user_location_name", "");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            String profileKey = getArguments().getString(Constants.BundleKeys.PROFILE_COMPLETION);
                            if (null != profileKey && profileKey.equalsIgnoreCase(Constants.Messages.PLAYER_PROFILE)) {
                                Intent intent = new Intent(getActivity(), PlayerProfileActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            } else {
                                Intent intent = new Intent(getActivity(), OTPActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            String error = response.message();
                            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        CommonUtils.ErrorHandleMethod(getActivity(),response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (progress.isShowing()) {
                    progress.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //  Log error here since request failed
                CommonUtils.ServerFailureHandleMethod(getActivity(), t);
                if (progress.isShowing()) {
                    progress.dismiss();
                }
            }
        });
    }

    @Override
    public void showAlertDialog() {
        final Dialog selectionDetailDialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        selectionDetailDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        selectionDetailDialog.setContentView(R.layout.logout_layout);
        CardView card_view = (CardView) selectionDetailDialog.findViewById(R.id.card_view);
        TextView title = (TextView) selectionDetailDialog.findViewById(R.id.title);
        title.setText(Constants.Messages.SPORT_UNSELECTION_INFO);

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = (int) (metrics.widthPixels * 0.8);
        params.gravity = Gravity.CENTER;
        card_view.setLayoutParams(params);
        TextView yes = (TextView) selectionDetailDialog.findViewById(R.id.yes);
        TextView no = (TextView) selectionDetailDialog.findViewById(R.id.no);
        yes.setText(Constants.DefaultText.Ok);
        no.setVisibility(View.GONE);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectionDetailDialog.dismiss();
            }
        });
        selectionDetailDialog.show();

    }
}
