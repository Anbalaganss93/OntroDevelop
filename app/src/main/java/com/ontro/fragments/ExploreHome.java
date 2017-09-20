package com.ontro.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.ontro.Constants;
import com.ontro.HomeActivity;
import com.ontro.R;
import com.ontro.utils.CommonUtils;
import com.ontro.utils.PreferenceHelper;

public class ExploreHome extends Fragment implements View.OnFocusChangeListener {
    public static String searchkey = "", progress_show = "1";
    View v = null;
    private PreferenceHelper preferenceHelper;
    private ImageView search;
    private EditText searchbox;
    private int search_status = 0;
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_explore_home, container, false);

        preferenceHelper = new PreferenceHelper(getActivity(), Constants.APP_NAME, 0);
        search = (ImageView) getActivity().findViewById(R.id.activity_explore_player_list_iv_search);
        searchbox = (EditText) getActivity().findViewById(R.id.searchbox);

        searchbox.setOnFocusChangeListener(this);
        search.setVisibility(View.GONE);
        searchbox.setVisibility(View.GONE);
        closeSearchBox(searchbox);
        progress_show = "1";

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity activity = getActivity();
                if (activity != null && isAdded()) {
                    if (search_status == 0) {
                        openSearchBox();
                    } else {
                        closeSearchBox(view);
                    }
                }
            }
        });

        searchbox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() > 0) {
                    progress_show = "2";
                    preferenceHelper.save("searchkey", String.valueOf(charSequence));
                    SetupTeamandPlayer();
                } else {
                    if (isAdded()) {
                        progress_show = "1";
                        preferenceHelper.save("searchkey", "");
                        SetupTeamandPlayer();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        search.setVisibility(View.VISIBLE);

        return v;
    }

    public void openSearchBox() {
        try {
            search_status = 1;

            searchbox.post(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {
                    int cx = searchbox.getWidth();
                    int cy = searchbox.getHeight() / 2;
                    // get the final radius for the clipping circle
                    float finalRadius = (float) Math.hypot(cx, cy);
                    // create the animator for this view (the start radius is zero)
                    Animator anim;
                    anim = ViewAnimationUtils.createCircularReveal(searchbox, cx, cy, 0, finalRadius);
                    anim.setDuration((long) 220);
                    // make the view visible and start the animation
                    searchbox.setVisibility(View.VISIBLE);
                    searchbox.setFocusableInTouchMode(true);
                    searchbox.setFocusable(true);
                    searchbox.requestFocus();
                    if (getActivity() != null) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(searchbox, InputMethodManager.SHOW_IMPLICIT);
                    }
                    anim.start();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("SEARCH", String.valueOf(e.toString()));
        }
    }

    public void closeSearchBox(View view) {
        try {
            searchbox.setText("");
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            search_status = 0;
            int cx = searchbox.getWidth();
            int cy = searchbox.getHeight() / 2;
            // get the final radius for the clipping circle
            float finalRadius = (float) Math.hypot(cx, cy);
            // create the animator for this view (the start radius is zero)
            Animator anim;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                anim = ViewAnimationUtils.createCircularReveal(searchbox, cx, cy, finalRadius, 0);
                anim.setInterpolator(new AccelerateDecelerateInterpolator());
                anim.setDuration((long) 250);
                anim.start();
                // make the view visible and start the animation
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        searchbox.setFocusableInTouchMode(false);
                        searchbox.setFocusable(false);
                        searchbox.clearFocus();
                        searchbox.setVisibility(View.GONE);
                        if (getActivity() != null) {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(searchbox.getWindowToken(), 0);
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("SEARCH ic_close_grey", String.valueOf(e.toString()));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        search.setVisibility(View.VISIBLE);

        if (preferenceHelper.getString("searchvisible", "").equals("true")) {
            searchbox.setVisibility(View.VISIBLE);
        } else if (preferenceHelper.getString("searchvisible", "").equals("false")) {
            searchbox.setVisibility(View.GONE);
            filterhelper();
        }
        SetupTeamandPlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void SetupTeamandPlayer() {
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);

        Activity activity = getActivity();
        if (activity != null && isAdded()) {
            viewPager.setAdapter(new SampleFragmentPagerAdapter(getChildFragmentManager(),
                    getActivity()));
            TabLayout tabLayout = (TabLayout) v.findViewById(R.id.sliding_tabs);
            tabLayout.setupWithViewPager(viewPager);
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    HomeActivity.Exploretabposition = 0;
                } else {
                    HomeActivity.Exploretabposition = 1;
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    HomeActivity.Exploretabposition = 0;
                } else {
                    HomeActivity.Exploretabposition = 1;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(HomeActivity.Exploretabposition);

    }

    @Override
    public void onStop() {
        search.setVisibility(View.GONE);
        searchbox.setVisibility(View.GONE);
        closeSearchBox(searchbox);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Activity activity = getActivity();
        if (activity != null && isAdded()) {
            if (viewPager != null) viewPager.setAdapter(null);
            filterhelper();
        }
        search.setVisibility(View.GONE);
        super.onDestroyView();
    }

    public void filterhelper() {
        if (searchbox.getVisibility() == View.VISIBLE) {
            searchbox.setVisibility(View.GONE);
            try {
                closeSearchBox(searchbox);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        searchbox.setText("");
        if (preferenceHelper.contains("typeofsport")) {
            preferenceHelper.remove("typeofsport");
            preferenceHelper.remove("location");
            preferenceHelper.remove("user_location_name");
            CommonUtils.locationid = "";
        }
        preferenceHelper.save("searchkey", "");
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } else {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private class SampleFragmentPagerAdapter extends FragmentStatePagerAdapter {
        final int PAGE_COUNT = 2;
        private String tabTitles[] = new String[]{"Teams", "Players"};
        private Context context;

        private SampleFragmentPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment mfrag = null;
            switch (position) {
                case 0:
                    mfrag = new ExploreTeamsFragment();
                    break;
                case 1:

                    mfrag = new ExplorePlayersFragment();
                    break;
            }
            return mfrag;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }
    }
}