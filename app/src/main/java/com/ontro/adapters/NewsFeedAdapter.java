package com.ontro.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.ontro.Constants;
import com.ontro.R;
import com.ontro.TournamentDetailActivity;
import com.ontro.dto.NewsFeedModel;
import com.ontro.rest.ApiClient;
import com.ontro.rest.ApiInterface;
import com.ontro.utils.PreferenceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Android on 20-Feb-17.
 */

public class NewsFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<NewsFeedModel> mNewsFeedModels;
    private Context context;
    private DisplayMetrics dm;
    private PreferenceHelper preferenceHelper;
    private ApiInterface apiInterface;
    private Dialog progress;
    private String auth_token, newsfeedid;
    private int selectedposition = -1, like_updated_value;
    private int likecount = 0;
    private MixpanelAPI mMixpanel;

    public NewsFeedAdapter(FragmentActivity activity, ArrayList<NewsFeedModel> newsFeedModels) {
        this.context = activity;
        this.mNewsFeedModels = newsFeedModels;
        dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        mMixpanel = MixpanelAPI.getInstance(activity, activity.getResources().getString(R.string.mixpanel_token));
        preferenceHelper = new PreferenceHelper(activity, Constants.APP_NAME, 0);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        progress = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progress.setContentView(R.layout.progressdialog_layout);

        auth_token = "Bearer " + preferenceHelper.getString("user_token", "");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        switch (viewType) {
            case 1:
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_newsfeed_text_layout, parent, false);
                return new ViewHolder2(view2);
            case 2:
            case 5:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_newsfeed_text_image_layout, parent, false);
                return new ViewHolder(view);
            case 3:
                View view3 = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_newsfeed_achievement_won_layout, parent, false);
                return new ViewHolder3(view3);
            case 4:
                View view4 = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_newsfeed_match_winnings_layout, parent, false);
                return new ViewHolder4(view4);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        NewsFeedModel m = mNewsFeedModels.get(position);
        return m.getType();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final NewsFeedModel newsFeedModel = mNewsFeedModels.get(position);
        switch (holder.getItemViewType()) {
            case 1:
                final ViewHolder2 holder2 = (ViewHolder2) holder;
                final NewsFeedModel m2 = mNewsFeedModels.get(position);

                holder2.layout2_content.setText(m2.getDescription());
                holder2.layout2_timeago.setText(m2.getHours_ago());
                holder2.likecount.setText(m2.getLikes());

                holder2.like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        newsfeedid = newsFeedModel.getNewsfeed_id();
                        likecount = Integer.parseInt(newsFeedModel.getLikes());
                        selectedposition = holder2.getAdapterPosition();
                        if (newsFeedModel.getIs_liked().equals("0")) {
                            like_updated_value = likecount + 1;
                            String likestatus = newsFeedModel.getIs_liked().equals("0") ? "1" : "0";
                            newsFeedModel.setIs_liked(likestatus);
                            newsFeedModel.setLikes(String.valueOf(like_updated_value));
                            holder2.likecount.setText(String.valueOf(like_updated_value));
                            holder2.like.setImageResource(R.drawable.ic_like_filled);
                            try {
                                JSONObject eventJsonObject = new JSONObject();
                                eventJsonObject.put("NewsId", newsFeedModel.getNewsfeed_id() );
                                eventJsonObject.put("UserName",  preferenceHelper.getString("user_name", ""));
                                eventJsonObject.put("UserEmail", preferenceHelper.getString("user_email", ""));
                                eventJsonObject.put("NewsContent", newsFeedModel.getDescription());
                                mMixpanel.track("NewsLikes", eventJsonObject);
                            } catch (JSONException e) {
                                Log.e("Ontro", "Unable to add properties to JSONObject", e);
                            }
                            Call<ResponseBody> call = apiInterface.NewsLike(newsfeedid, auth_token);
                            Discussion_LFULUF_servercall(call, null, holder2);
                        } else {
                            if (likecount > 0) {
                                like_updated_value = likecount - 1;
                                String likestatus = newsFeedModel.getIs_liked().equals("0") ? "1" : "0";
                                newsFeedModel.setIs_liked(likestatus);
                                newsFeedModel.setLikes(String.valueOf(like_updated_value));
                                holder2.likecount.setText(String.valueOf(like_updated_value));
                                holder2.like.setImageResource(R.drawable.ic_like);

                                Call<ResponseBody> call = apiInterface.NewsUnLike(newsfeedid, auth_token);
                                Discussion_LFULUF_servercall(call, null, holder2);
                            } else {
                                newsFeedModel.setLikes("0");
                                holder2.likecount.setText(String.valueOf(0));
                            }
                        }
                    }
                });

                if (m2.getIs_liked().equals("0")) {
                    holder2.like.setImageResource(R.drawable.ic_like);
                } else {
                    holder2.like.setImageResource(R.drawable.ic_like_filled);
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            JSONObject eventJsonObject = new JSONObject();
                            eventJsonObject.put("NewsId", newsFeedModel.getNewsfeed_id() );
                            eventJsonObject.put("UserName",  preferenceHelper.getString("user_name", ""));
                            eventJsonObject.put("UserEmail", preferenceHelper.getString("user_email", ""));
                            eventJsonObject.put("NewsContent", newsFeedModel.getDescription());
                            mMixpanel.track("NewsView", eventJsonObject);
                        } catch (JSONException e) {
                            Log.e("Ontro", "Unable to add properties to JSONObject", e);
                        }
                       /* Intent intent = new Intent(context, NewsFeedDetailActivity.class);
                        intent.putExtra("content", m2.getDescription());
                        context.startActivity(intent);*/
                    }
                });
                break;
            case 2:
                final ViewHolder holder1 = (ViewHolder) holder;
                LinearLayout.LayoutParams firstparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                firstparams.height = ((int) (dm.heightPixels * 0.3));
                holder1.newsfeed_container1.setLayoutParams(firstparams);
                holder1.content.setText(newsFeedModel.getDescription());
                holder1.likecount.setText(newsFeedModel.getLikes());
                holder1.timeago.setText(newsFeedModel.getHours_ago());
                Glide.with(context).load(newsFeedModel.getImage()).placeholder(R.drawable.match_default).dontAnimate().into(holder1.newsfeed_image);
                holder1.like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NewsFeedModel m = mNewsFeedModels.get(holder1.getAdapterPosition());
                        newsfeedid = m.getNewsfeed_id();
                        likecount = Integer.parseInt(m.getLikes());
                        selectedposition = holder1.getAdapterPosition();
                        if (m.getIs_liked().equals("0")) {
                            like_updated_value = likecount + 1;
                            String likestatus = m.getIs_liked().equals("0") ? "1" : "0";
                            m.setIs_liked(likestatus);
                            m.setLikes(String.valueOf(like_updated_value));
                            holder1.likecount.setText(String.valueOf(like_updated_value));
                            holder1.like.setImageResource(R.drawable.ic_like_filled);
                            try {
                                JSONObject eventJsonObject = new JSONObject();
                                eventJsonObject.put("NewsId", newsFeedModel.getNewsfeed_id() );
                                eventJsonObject.put("UserName",  preferenceHelper.getString("user_name", ""));
                                eventJsonObject.put("UserEmail", preferenceHelper.getString("user_email", ""));
                                eventJsonObject.put("NewsContent", newsFeedModel.getDescription());
                                mMixpanel.track("NewsLikes", eventJsonObject);
                            } catch (JSONException e) {
                                Log.e("Ontro", "Unable to add properties to JSONObject", e);
                            }
                            Call<ResponseBody> call = apiInterface.NewsLike(newsfeedid, auth_token);
                            Discussion_LFULUF_servercall(call, holder1, null);

                        } else {
                            if (likecount > 0) {
                                like_updated_value = likecount - 1;
                                String likestatus = m.getIs_liked().equals("0") ? "1" : "0";
                                m.setIs_liked(likestatus);
                                m.setLikes(String.valueOf(like_updated_value));
                                holder1.likecount.setText(String.valueOf(like_updated_value));
                                holder1.like.setImageResource(R.drawable.ic_like);

                                Call<ResponseBody> call = apiInterface.NewsUnLike(newsfeedid, auth_token);
                                Discussion_LFULUF_servercall(call, holder1, null);
                            } else {
                                m.setLikes("0");
                                holder1.likecount.setText(String.valueOf(0));
                            }
                        }
                    }
                });

                if (newsFeedModel.getIs_liked().equals("0")) {
                    holder1.like.setImageResource(R.drawable.ic_like);
                } else {
                    holder1.like.setImageResource(R.drawable.ic_like_filled);
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            JSONObject eventJsonObject = new JSONObject();
                            eventJsonObject.put("NewsId", newsFeedModel.getNewsfeed_id() );
                            eventJsonObject.put("UserName",  preferenceHelper.getString("user_name", ""));
                            eventJsonObject.put("UserEmail", preferenceHelper.getString("user_email", ""));
                            eventJsonObject.put("NewsContent", newsFeedModel.getDescription());
                            mMixpanel.track("NewsView", eventJsonObject);
                        } catch (JSONException e) {
                            Log.e("Ontro", "Unable to add properties to JSONObject", e);
                        }
                       /* Intent intent = new Intent(context, NewsFeedDetailActivity.class);
                        intent.putExtra("content", m2.getDescription());
                        context.startActivity(intent);*/
                    }
                });

                break;
            case 3:
                ViewHolder3 holder3 = (ViewHolder3) holder;
                LayoutInflater inflater3 = LayoutInflater.from(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.width = (int) (dm.widthPixels * 0.13);
                params.height = (int) (dm.widthPixels * 0.13);
                params.gravity = Gravity.CENTER_VERTICAL;

                holder3.batch_layout.removeAllViews();
                for (int i = 0; i < 1; i++) {
                    View to_add = inflater3.inflate(R.layout.adapter_won_batch_layout, holder3.batch_layout, false);
                    ImageView batch = (ImageView) to_add.findViewById(R.id.batch);
                    batch.setLayoutParams(params);
                    if (i < 3) {
                        batch.setImageResource(R.drawable.badge1);
                    } else {
                        batch.setImageResource(R.drawable.badge1);
                    }
                    holder3.batch_layout.addView(to_add);
                }
                break;
            case 4:
                ViewHolder4 holder4 = (ViewHolder4) holder;
                if (position % 2 == 0) {
                    holder4.left_team_container.setAlpha((float) 0.3);
                    holder4.right_team_container.setAlpha(1);
                    holder4.game_image.setImageResource(R.drawable.ic_cricket_white);
                } else {
                    holder4.left_team_container.setAlpha(1);
                    holder4.right_team_container.setAlpha((float) 0.3);
                    holder4.game_image.setImageResource(R.drawable.ic_cricket_white);
                }

                LayoutInflater inflater2 = LayoutInflater.from(context);
                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                params2.width = (int) (dm.widthPixels * 0.1);
                params2.height = (int) (dm.widthPixels * 0.1);
                params2.setMargins(0, 5, 0, 5);
                params2.gravity = Gravity.CENTER_VERTICAL;

                LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params3.width = (int) (dm.widthPixels * 0.1);
                params3.height = (int) (dm.widthPixels * 0.1);
                params3.setMargins(0, 5, 0, 5);
                params3.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT | Gravity.END;

                if (position % 2 == 0) {
                    holder4.left_overs.setVisibility(View.VISIBLE);
                    holder4.right_overs.setVisibility(View.VISIBLE);
                    holder4.left_overs.setText("19 overs");
                    holder4.right_overs.setText("19 overs");
                    holder4.left_scores.setText("255/5");
                    holder4.right_scores.setText("455/5");
                } else {
                    holder4.left_overs.setVisibility(View.GONE);
                    holder4.right_overs.setVisibility(View.GONE);
                    holder4.left_scores.setText("3");
                    holder4.right_scores.setText("2");
                }

                holder4.left_batch_container.removeAllViews();
                for (int i = 0; i < 1; i++) {
                    View to_add = inflater2.inflate(R.layout.adapter_won_batch_layout, holder4.left_batch_container, false);
                    ImageView batch = (ImageView) to_add.findViewById(R.id.batch);
                    batch.setLayoutParams(params2);
                    if (i < 3) {
                        batch.setImageResource(R.drawable.badge1);
                    } else {
                        batch.setImageResource(R.drawable.badge1);
                    }
                    holder4.left_batch_container.addView(to_add);
                }

                holder4.right_batch_container.removeAllViews();
                for (int i = 0; i < 1; i++) {
                    View to_add = inflater2.inflate(R.layout.adapter_won_batch_layout, holder4.right_batch_container, false);
                    ImageView batch = (ImageView) to_add.findViewById(R.id.batch);
                    batch.setLayoutParams(params3);
                    if (i < 3) {
                        batch.setImageResource(R.drawable.badge1);
                    } else {
                        batch.setImageResource(R.drawable.badge1);
                    }
                    holder4.right_batch_container.addView(to_add);
                }
                break;
            case 5:
                final ViewHolder holder5 = (ViewHolder) holder;
                LinearLayout.LayoutParams fifthtparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                fifthtparams.height = ((int) (dm.heightPixels * 0.3));
                holder5.newsfeed_container1.setLayoutParams(fifthtparams);

                holder5.content.setText(newsFeedModel.getDescription());
                holder5.likecount.setText(newsFeedModel.getLikes());
                holder5.timeago.setText(newsFeedModel.getHours_ago());
                Glide.with(context).load(newsFeedModel.getImage()).placeholder(R.drawable.match_default).dontAnimate().into(holder5.newsfeed_image);
                holder5.like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NewsFeedModel newsFeedModel1 = mNewsFeedModels.get(holder5.getAdapterPosition());
                        newsfeedid = newsFeedModel1.getNewsfeed_id();
                        likecount = Integer.parseInt(newsFeedModel1.getLikes());
                        selectedposition = holder5.getAdapterPosition();
                        if (newsFeedModel1.getIs_liked().equals("0")) {
                            like_updated_value = likecount + 1;
                            String likestatus = newsFeedModel1.getIs_liked().equals("0") ? "1" : "0";
                            newsFeedModel1.setIs_liked(likestatus);
                            newsFeedModel1.setLikes(String.valueOf(like_updated_value));
                            holder5.likecount.setText(String.valueOf(like_updated_value));
                            holder5.like.setImageResource(R.drawable.ic_like_filled);
                            try {
                                JSONObject eventJsonObject = new JSONObject();
                                eventJsonObject.put("TournamentId", newsFeedModel1.getTournamentId() );
                                eventJsonObject.put("UserName",  preferenceHelper.getString("user_name", ""));
                                eventJsonObject.put("UserEmail", preferenceHelper.getString("user_email", ""));
                                eventJsonObject.put("TournamentName", newsFeedModel1.getDescription());
                                mMixpanel.track("TournamentLikes", eventJsonObject);
                            } catch (JSONException e) {
                                Log.e("Ontro", "Unable to add properties to JSONObject", e);
                            }
                            notifyItemChanged(selectedposition);

                            Call<ResponseBody> call = apiInterface.NewsLike(newsfeedid, auth_token);
                            Discussion_LFULUF_servercall(call, holder5, null);
                        } else {
                            if (likecount > 0) {
                                like_updated_value = likecount - 1;
                                String likestatus = newsFeedModel1.getIs_liked().equals("0") ? "1" : "0";
                                newsFeedModel1.setIs_liked(likestatus);
                                newsFeedModel1.setLikes(String.valueOf(like_updated_value));
                                holder5.likecount.setText(String.valueOf(like_updated_value));
                                holder5.like.setImageResource(R.drawable.ic_like);
                                notifyItemChanged(selectedposition);

                                Call<ResponseBody> call = apiInterface.NewsUnLike(newsfeedid, auth_token);
                                Discussion_LFULUF_servercall(call, holder5, null);
                            } else {
                                newsFeedModel1.setLikes("0");
                                holder5.likecount.setText(String.valueOf(0));
                            }
                        }
                    }
                });

                if (newsFeedModel.getIs_liked().equals("0")) {
                    holder5.like.setImageResource(R.drawable.ic_like);
                } else {
                    holder5.like.setImageResource(R.drawable.ic_like_filled);
                }

                holder5.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int tournamentId = newsFeedModel.getTournamentId();
                        if (tournamentId > 0) {
                            try {
                                JSONObject eventJsonObject = new JSONObject();
                                eventJsonObject.put("TournamentId", newsFeedModel.getTournamentId() );
                                eventJsonObject.put("UserName",  preferenceHelper.getString("user_name", ""));
                                eventJsonObject.put("UserEmail", preferenceHelper.getString("user_email", ""));
                                eventJsonObject.put("TournamentName", newsFeedModel.getDescription());
                                mMixpanel.track("TournamentViews", eventJsonObject);
                            } catch (JSONException e) {
                                Log.e("Ontro", "Unable to add properties to JSONObject", e);
                            }
                            Intent intent = new Intent(context, TournamentDetailActivity.class);
                            intent.putExtra(Constants.BundleKeys.TOURNAMENT_ID, tournamentId);
                            Activity activity = (Activity) context;
                            activity.startActivity(intent);
                        }
                    }
                });

                holder5.share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
                        sendIntent.setType("text/plain");
                        context.startActivity(sendIntent);
                    }
                });

                break;
        }
    }

    @Override
    public int getItemCount() {
        return mNewsFeedModels.size();
    }

    private void Discussion_LFULUF_servercall(Call<ResponseBody> call, final ViewHolder holder, final ViewHolder2 holder2) {
//        progress.show();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    NewsFeedModel m = mNewsFeedModels.get(selectedposition);
                    if (response.code() != 200) {
                        if (response.body() != null) {
                            String error = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(error);
                            String msg = jsonObject.getString("message");
                            switch (m.getType()) {
                                case 1:
                                    if (m.getIs_liked().equals("1")) {
                                        like_updated_value = likecount - 1;
                                        holder.likecount.setText(String.valueOf(like_updated_value));
                                        holder.like.setImageResource(R.drawable.ic_like);
                                    } else {
                                        like_updated_value = likecount + 1;
                                        holder.likecount.setText(String.valueOf(like_updated_value));
                                        holder.like.setImageResource(R.drawable.ic_like_filled);
                                    }
                                    break;
                                case 2:
                                    if (m.getIs_liked().equals("1")) {
                                        like_updated_value = likecount - 1;
                                        holder2.likecount.setText(String.valueOf(like_updated_value));
                                        holder2.like.setImageResource(R.drawable.ic_like);

                                    } else {
                                        like_updated_value = likecount + 1;
                                        holder2.likecount.setText(String.valueOf(like_updated_value));
                                        holder2.like.setImageResource(R.drawable.ic_like_filled);
                                    }
                                    break;
                            }
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            String error = response.message();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                    Handler mHandler = new Handler();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            notifyItemChanged(selectedposition);
                        }
                    }, 10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //  Log error here since request failed
                try {
                    if (t instanceof SocketTimeoutException) {
                        Toast.makeText(context, R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (progress != null & progress.isShowing()) {
                    progress.dismiss();
                }
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout newsfeed_container1;
        private RoundedImageView newsfeed_image;
        private ImageView like, share;
        private TextView content, likecount, timeago;

        public ViewHolder(View itemView) {
            super(itemView);
            newsfeed_container1 = (RelativeLayout) itemView.findViewById(R.id.newsfeed_container1);
            newsfeed_image = (RoundedImageView) itemView.findViewById(R.id.newsfeed_image);
            content = (TextView) itemView.findViewById(R.id.content);
            likecount = (TextView) itemView.findViewById(R.id.likecount);
            timeago = (TextView) itemView.findViewById(R.id.timeago);
            like = (ImageView) itemView.findViewById(R.id.like);
            share = (ImageView) itemView.findViewById(R.id.share);
        }

    }

    private class ViewHolder2 extends RecyclerView.ViewHolder {
        private ImageView like;
        private TextView layout2_content, likecount, layout2_timeago;

        ViewHolder2(View itemView) {
            super(itemView);
            like = (ImageView) itemView.findViewById(R.id.like);
            layout2_content = (TextView) itemView.findViewById(R.id.layout2_content);
            layout2_timeago = (TextView) itemView.findViewById(R.id.layout2_timeago);
            likecount = (TextView) itemView.findViewById(R.id.likecount);
        }

    }

    private class ViewHolder3 extends RecyclerView.ViewHolder {
        private LinearLayout batch_layout;

        ViewHolder3(View itemView) {
            super(itemView);
            batch_layout = (LinearLayout) itemView.findViewById(R.id.batchcontainer);
        }

    }

    private class ViewHolder4 extends RecyclerView.ViewHolder {
        private LinearLayout right_team_container, left_team_container, left_batch_container, right_batch_container;
        private TextView left_overs, right_overs, left_scores, right_scores;
        private ImageView game_image;

        ViewHolder4(View itemView) {
            super(itemView);
            right_team_container = (LinearLayout) itemView.findViewById(R.id.right_team_container);
            left_team_container = (LinearLayout) itemView.findViewById(R.id.left_team_container);
            left_batch_container = (LinearLayout) itemView.findViewById(R.id.left_batch_container);
            right_batch_container = (LinearLayout) itemView.findViewById(R.id.right_batch_container);
            left_overs = (TextView) itemView.findViewById(R.id.left_overs);
            right_overs = (TextView) itemView.findViewById(R.id.right_overs);
            left_scores = (TextView) itemView.findViewById(R.id.left_scores);
            right_scores = (TextView) itemView.findViewById(R.id.right_scores);
            game_image = (ImageView) itemView.findViewById(R.id.game_image);
        }

    }

    private class MyBounceInterpolator implements android.view.animation.Interpolator {
        double mAmplitude = 1;
        double mFrequency = 10;

        MyBounceInterpolator(double amplitude, double frequency) {
            mAmplitude = amplitude;
            mFrequency = frequency;
        }

        public float getInterpolation(float time) {
            return (float) (-1 * Math.pow(Math.E, -time / mAmplitude) *
                    Math.cos(mFrequency * time) + 1);
        }
    }


}
