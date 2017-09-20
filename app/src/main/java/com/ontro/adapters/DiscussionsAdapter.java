package com.ontro.adapters;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ontro.Constants;
import com.ontro.DiscussionsActivity;
import com.ontro.R;
import com.ontro.dto.DiscussionsCommentModel;
import com.ontro.rest.ApiClient;
import com.ontro.rest.ApiInterface;
import com.ontro.utils.PreferenceHelper;

import org.apache.commons.lang3.StringEscapeUtils;
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

public class DiscussionsAdapter extends RecyclerView.Adapter<DiscussionsAdapter.ViewHolder> {
    private ArrayList<DiscussionsCommentModel> arrayList;
    private Context context;
    private DisplayMetrics dm;
    private PreferenceHelper preferenceHelper;
    private ApiInterface apiInterface;
    private String discussionid;
    private int selectedposition = -1, like_updated_value;
    private String like_selected, auth_token;
    private int likecount = 0;

    public DiscussionsAdapter(DiscussionsActivity discussionsActivity, ArrayList<DiscussionsCommentModel> arrayList, String discussionid) {
        this.context = discussionsActivity;
        this.arrayList = arrayList;
        this.discussionid = discussionid;

        dm = new DisplayMetrics();
        discussionsActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);

        preferenceHelper = new PreferenceHelper(discussionsActivity, Constants.APP_NAME, 0);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Dialog progress = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progress.setContentView(R.layout.progressdialog_layout);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_discussions_comments_layout, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        RelativeLayout.LayoutParams img_params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        img_params.width = (int) (dm.widthPixels * 0.1);
        img_params.height = ((int) (dm.widthPixels * 0.1));
        img_params.addRule(RelativeLayout.CENTER_VERTICAL);
        holder.commender_initial.setLayoutParams(img_params);

        DiscussionsCommentModel m = arrayList.get(holder.getAdapterPosition());
        holder.seen_before.setText(m.getSeen_before());
        String userComment = m.getComment();
        holder.commender_comment.setText(StringEscapeUtils.unescapeJava(userComment));
        holder.likecount.setText(m.getLikecount());
        holder.commender_name.setText(m.getName());
        String initname = String.valueOf(m.getName().charAt(0));
        holder.commender_initial.setText(initname);
        auth_token = "Bearer " + preferenceHelper.getString("user_token", "");

        holder.likebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DiscussionsCommentModel m = arrayList.get(holder.getAdapterPosition());
                likecount = Integer.parseInt(m.getLikecount());
                selectedposition = holder.getAdapterPosition();
                like_selected = "1";
                String commentid = m.getComment_id();
                if (m.getLikestatus().equals("0")) {

                    holder.likebutton.setImageResource(R.drawable.ic_like_filled);
                    like_updated_value = likecount + 1;
                    m.setLikecount(String.valueOf(like_updated_value));
                    String likestatus = m.getLikestatus().equals("0") ? "1" : "0";
                    m.setLikestatus(likestatus);
                    holder.likecount.setText(String.valueOf(like_updated_value));

                    Animation myAnim = AnimationUtils.loadAnimation(context, R.anim.bounce);
                    MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                    myAnim.setInterpolator(interpolator);
                    holder.likebutton.startAnimation(myAnim);
                    Call<ResponseBody> call = apiInterface.Like(commentid, auth_token);
                    Discussion_LFULUF_servercall(call, holder);
                } else {
                    if (likecount > 0) {
                        like_updated_value = likecount - 1;
                        m.setLikecount(String.valueOf(like_updated_value));
                        String likestatus = m.getLikestatus().equals("0") ? "1" : "0";
                        m.setLikestatus(likestatus);
                        holder.likecount.setText(String.valueOf(like_updated_value));

                        holder.likebutton.setImageResource(R.drawable.ic_like);
                        m.setLikecount(String.valueOf(like_updated_value));
                        Call<ResponseBody> call = apiInterface.UnLike(commentid, auth_token);
                        Discussion_LFULUF_servercall(call, holder);
                    } else {
                        m.setLikecount("0");
                        holder.likecount.setText(String.valueOf(0));
                    }
                }
            }
        });

        holder.flagbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedposition = holder.getAdapterPosition();
                like_selected = "0";
                DiscussionsCommentModel m = arrayList.get(holder.getAdapterPosition());
                String commentid = m.getComment_id();
                if (m.getLikestatus().equals("0")) {
                    String flagstatus = m.getFlagstatus().equals("0") ? "1" : "0";
                    m.setFlagstatus(flagstatus);
                    holder.flagbutton.setImageResource(R.drawable.report_comment_red);
                    Animation myAnim = AnimationUtils.loadAnimation(context, R.anim.bounce);
                    MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                    myAnim.setInterpolator(interpolator);

                    Call<ResponseBody> call = apiInterface.CommentFlag(commentid, auth_token);
                    Discussion_LFULUF_servercall(call, holder);
                } else {
                    String flagstatus = m.getFlagstatus().equals("0") ? "1" : "0";
                    m.setFlagstatus(flagstatus);
                    holder.flagbutton.setImageResource(R.drawable.report_comment);
                    Call<ResponseBody> call = apiInterface.CommentUnFlag(discussionid, auth_token);
                    Discussion_LFULUF_servercall(call, holder);
                }
            }
        });

        if (m.getLikestatus().equals("0")) {
            holder.likebutton.setImageResource(R.drawable.ic_like);
        } else {
            holder.likebutton.setImageResource(R.drawable.ic_like_filled);
        }

        if (m.getFlagstatus().equals("0")) {
            holder.flagbutton.setImageResource(R.drawable.report_comment);
        } else {
            holder.flagbutton.setImageResource(R.drawable.report_comment_red);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    private void Discussion_LFULUF_servercall(Call<ResponseBody> call, final ViewHolder holder) {
//        progress.show();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    DiscussionsCommentModel m = arrayList.get(selectedposition);
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        JSONObject json = new JSONObject(data);
                        Log.d("RESPONSE", data);
                        if (!json.getString("status").equals("success")) {
                            if (response.body() != null) {
                                String error = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(error);
                                String msg = jsonObject.getString("message");
                                String code = jsonObject.getString("code");
                                if (like_selected.equals("1")) {
                                    if (m.getLikestatus().equals("0")) {
                                        holder.likebutton.setImageResource(R.drawable.ic_like);
                                        like_updated_value = likecount - 1;
                                        m.setLikecount(String.valueOf(like_updated_value));
                                    } else {
                                        holder.likebutton.setImageResource(R.drawable.ic_like);
                                        like_updated_value = likecount + 1;
                                        holder.likebutton.setImageResource(R.drawable.ic_like_filled);
                                    }
                                } else {
                                    if (m.getFlagstatus().equals("0")) {
                                        holder.flagbutton.setImageResource(R.drawable.report_comment);
                                    } else {
                                        holder.flagbutton.setImageResource(R.drawable.report_comment_red);
                                    }
                                }
                                if (!code.equals("500")) {
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                String error = response.message();
                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                            }
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

                /*if (progress != null & progress.isShowing()) {
                    progress.dismiss();
                }*/
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView commender_initial, seen_before, commender_name, commender_comment, likecount;
        private ImageView likebutton, flagbutton;

        public ViewHolder(View itemView) {
            super(itemView);
            commender_initial = (TextView) itemView.findViewById(R.id.commender_initial);
            seen_before = (TextView) itemView.findViewById(R.id.seen_before);
            commender_name = (TextView) itemView.findViewById(R.id.commender_name);
            commender_comment = (TextView) itemView.findViewById(R.id.commender_comment);
            likecount = (TextView) itemView.findViewById(R.id.likecount);
            likebutton = (ImageView) itemView.findViewById(R.id.likebutton);
            flagbutton = (ImageView) itemView.findViewById(R.id.flagbutton);
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
