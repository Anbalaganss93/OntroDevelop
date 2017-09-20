package com.ontro.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ontro.Constants;
import com.ontro.R;
import com.ontro.dto.MatchFlagRequestModel;
import com.ontro.rest.ApiClient;
import com.ontro.rest.ApiInterface;
import com.ontro.utils.PreferenceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by IDEOMIND02 on 09-08-2017.
 */

public class MatchFlagFragment extends DialogFragment implements View.OnClickListener {
    private View mMatchFlagView;
    private ImageView mFlagDescriptionView;
    private TextView mFlagDescriptionTitleView;
    private TextView mFlagDescriptionTextView;
    private LinearLayout mFlagMatchContentLayout;
    private RelativeLayout mFlagMatchOthersLayout;
    private TextView mFalseScoreSubmitView;
    private TextView mTeamOrPlayerIssueView;
    private TextView mOffensiveTextView;
    private TextView mOthersTextView;
    private EditText mReasonEditText;
    private Button mFlagButton;
    private LinearLayout mFlagMatchBottomLayout;
    private ImageView mFlagDescriptionBackView;
    private ImageView mFlagDescriptionCloseView;

    public static MatchFlagFragment newInstance(String matchId) {
        MatchFlagFragment matchFlagFragment = new MatchFlagFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BundleKeys.MATCH_ID, matchId);
        matchFlagFragment.setArguments(bundle);
        return matchFlagFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mMatchFlagView = inflater.inflate(R.layout.fragment_match_flag, container, false);
        initView();
        setListener();
        return mMatchFlagView;
    }

    private void initView() {
        mFlagDescriptionView = (ImageView) mMatchFlagView.findViewById(R.id.fragment_match_flag_iv);
        mFlagDescriptionBackView = (ImageView) mMatchFlagView.findViewById(R.id.fragment_match_flag_iv_back);
        mFlagDescriptionCloseView = (ImageView) mMatchFlagView.findViewById(R.id.fragment_match_flag_iv_close);
        mFlagDescriptionTitleView = (TextView) mMatchFlagView.findViewById(R.id.fragment_match_flag_tv_dialog_title);
        mFlagDescriptionTextView = (TextView) mMatchFlagView.findViewById(R.id.fragment_match_flag_tv_dialog_description);
        mFlagMatchBottomLayout = (LinearLayout) mMatchFlagView.findViewById(R.id.fragment_match_flag_ll_bottom);
        mFlagMatchContentLayout = (LinearLayout) mMatchFlagView.findViewById(R.id.fragment_match_flag_ll_content);
        mFlagMatchOthersLayout = (RelativeLayout) mMatchFlagView.findViewById(R.id.fragment_match_flag_ll_reason);
        mFalseScoreSubmitView = (TextView) mMatchFlagView.findViewById(R.id.fragment_match_flag_tv_false_score);
        mTeamOrPlayerIssueView = (TextView) mMatchFlagView.findViewById(R.id.fragment_match_flag_tv_not_turn_up);
        mOffensiveTextView = (TextView) mMatchFlagView.findViewById(R.id.fragment_match_flag_tv_offensive);
        mOthersTextView = (TextView) mMatchFlagView.findViewById(R.id.fragment_match_flag_tv_others);
        mReasonEditText = (EditText) mMatchFlagView.findViewById(R.id.fragment_match_flag_et_reason);
        mFlagButton = (Button) mMatchFlagView.findViewById(R.id.fragment_match_flag_btn_flag);
        Typeface typefaceRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto_regular.ttf");
        mReasonEditText.setTypeface(typefaceRegular);
        Typeface typefaceBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto_bold.ttf");
        mFlagButton.setTypeface(typefaceBold);
    }

    private void setListener() {
        mFlagDescriptionCloseView.setOnClickListener(this);
        mFlagDescriptionBackView.setOnClickListener(this);
        mFalseScoreSubmitView.setOnClickListener(this);
        mTeamOrPlayerIssueView.setOnClickListener(this);
        mOffensiveTextView.setOnClickListener(this);
        mOthersTextView.setOnClickListener(this);
        mFlagButton.setOnClickListener(this);
    }

    private void flagMatch(String mMatchId, int flagType, String reason) {
        PreferenceHelper preferenceHelper = new PreferenceHelper(getActivity(), Constants.APP_NAME, 0);
        String authToken = "Bearer " + preferenceHelper.getString("user_token", "");
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        MatchFlagRequestModel matchFlagRequestModel = new MatchFlagRequestModel();
        matchFlagRequestModel.setMatchId(mMatchId);
        matchFlagRequestModel.setFlagType(flagType);
        matchFlagRequestModel.setReason(reason);
        Call<ResponseBody> call = apiInterface.setFlagMatch(authToken, matchFlagRequestModel);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                       /* try {
                            String data = response.body().string();
                            Log.d("RESPONSE", data);
                            JSONObject json = new JSONObject(data);
                            showSubmissionDialog();
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }*/
                        showSubmissionDialog();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                try {
                    if (t instanceof SocketTimeoutException) {
                        Toast.makeText(getActivity(), R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showSubmissionDialog() {
        mFlagDescriptionView.setImageResource(R.drawable.ic_flag_confirmation);
        mFlagDescriptionTextView.setText(getResources().getString(R.string.required_activities_will_be_carried_out));
        mFlagDescriptionTitleView.setText(getResources().getString(R.string.appreciate_your_inputs));
        mFlagMatchBottomLayout.setVisibility(View.GONE);
        mFlagDescriptionBackView.setVisibility(View.GONE);
        mFlagDescriptionCloseView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onClick(View v) {
        if (getArguments() != null) {
            String matchId = getArguments().getString(Constants.BundleKeys.MATCH_ID);
            switch (v.getId()) {
                case R.id.fragment_match_flag_tv_false_score:
                    flagMatch(matchId, 1, Constants.DefaultText.EMPTY);
                    break;
                case R.id.fragment_match_flag_tv_not_turn_up:
                    flagMatch(matchId, 2, Constants.DefaultText.EMPTY);
                    break;
                case R.id.fragment_match_flag_tv_offensive:
                    flagMatch(matchId, 3, Constants.DefaultText.EMPTY);
                    break;
                case R.id.fragment_match_flag_tv_others:
                    mFlagMatchContentLayout.setVisibility(View.GONE);
                    mFlagMatchOthersLayout.setVisibility(View.VISIBLE);
                    mFlagDescriptionBackView.setVisibility(View.VISIBLE);
                    break;
                case R.id.fragment_match_flag_btn_flag:
                    String reason = mReasonEditText.getText().toString().trim();
                    flagMatch(matchId, 4, reason);
                    break;
                case R.id.fragment_match_flag_iv_back:
                    mFlagMatchContentLayout.setVisibility(View.VISIBLE);
                    mFlagMatchOthersLayout.setVisibility(View.GONE);
                    mFlagDescriptionBackView.setVisibility(View.GONE);
                    break;
                case R.id.fragment_match_flag_iv_close:
                    dismiss();
                    getActivity().finish();
                    break;


            }
        }
    }
}
