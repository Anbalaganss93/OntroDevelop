package com.ontro.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.ontro.ChatDetailActivity;
import com.ontro.Constants;
import com.ontro.R;
import com.ontro.adapters.ChatAdapter;
import com.ontro.dto.ChatUser;
import com.ontro.rest.ApiClient;
import com.ontro.rest.ApiInterface;
import com.ontro.utils.CommonUtils;
import com.ontro.utils.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment implements ValueEventListener, ChatAdapter.ChatAdapterListener, SwipeRefreshLayout.OnRefreshListener {
    private SuperRecyclerView mFirebaseChatUserList;
    private List<ChatUser> mChatUser;
    private List<ChatUser> mChatUserList = new ArrayList<>();
    private ChatAdapter mChatAdapter;
    private TextView mNoChatHistoryView;
    private ValueEventListener valueEventListener;
    private Dialog mProgressBar;
    private ProgressDialog mProgressDialog;
    private PreferenceHelper preferenceHelper;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int loadTeamCount = 0;
    private int teamCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chats, container, false);
        mFirebaseChatUserList = (SuperRecyclerView) v.findViewById(R.id.fragment_chats_rv);
        mNoChatHistoryView = (TextView) v.findViewById(R.id.fragment_chats_tv_no_history);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.fragment_chat_swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mFirebaseChatUserList.setLayoutManager(mLayoutManager);
        setProgressBar();
        valueEventListener = this;
        setAdapter();
        return v;
    }

    private void setProgressBar() {
        mProgressBar = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        mProgressBar.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressBar.setContentView(R.layout.progressdialog_layout);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Initializing chat list...");
        preferenceHelper = new PreferenceHelper(getActivity(), Constants.APP_NAME, 0);
    }

    private void setAdapter() {
        List<ChatUser> mAdapterChatUser = new ArrayList<>();
        mChatAdapter = new ChatAdapter(getActivity(), this, mAdapterChatUser);
        mFirebaseChatUserList.setAdapter(mChatAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (CommonUtils.isNetworkAvailable(getActivity())) {
            loadTeamCount = 0;
            teamCount = 0;
            mChatUser = new ArrayList<>();
            mChatUserList = new ArrayList<>();
            if (mChatAdapter != null) {
                mChatAdapter.clear();
            }
            if (preferenceHelper.contains("initial_progress")) {
                if(!mProgressDialog.isShowing()) {
                    mProgressBar.show();
                }
            } else {
                mProgressDialog.show();
                preferenceHelper.save("initial_progress", "true");
            }
            getFirebaseUserList();
        } else {
            Toast.makeText(getActivity(), Constants.INTERNET_ERROR, Toast.LENGTH_LONG).show();
        }
    }

    private void getFirebaseUserList() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        String authToken = "Bearer " + preferenceHelper.getString("user_token", "");
        Call<ResponseBody> call = apiInterface.getFirebaseUserList(authToken);
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                        JSONObject jsonObject = new JSONObject(data);
                        JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
                        List<ChatUser> userList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            ChatUser chatUser = new ChatUser();
                            chatUser.setUniqueId(jsonArray.getJSONObject(i).getString("fcm_uid"));
                            chatUser.setUserName(jsonArray.getJSONObject(i).getString("name"));
                            chatUser.setPlayerFcmToken(jsonArray.getJSONObject(i).getString("fcm_token"));
                            chatUser.setImageUrl(jsonArray.getJSONObject(i).getString("profile_image"));
                            userList.add(chatUser);
                        }
                        mChatUser.addAll(userList);

                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if (snapshot.hasChild(Constants.CHAT)) {
                                    DatabaseReference mUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.CHAT);
                                    mUserDatabaseReference.addValueEventListener(valueEventListener);
                                } else {
                                    addTeamChat();
                                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                        mProgressDialog.dismiss();
                                    }
                                    if (mProgressBar != null && mProgressBar.isShowing()) {
                                        mProgressBar.dismiss();
                                    }
                                    if (mSwipeRefreshLayout.isRefreshing()) {
                                        mSwipeRefreshLayout.setRefreshing(false);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e("Database Error", databaseError.toString());
                            }
                        });
                    } else {
                        CommonUtils.ErrorHandleMethod(getActivity(), response);
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                        if (mProgressBar != null && mProgressBar.isShowing()) {
                            mProgressBar.dismiss();
                        }
                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                CommonUtils.ServerFailureHandleMethod(getActivity(), t);
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                if (mProgressBar != null && mProgressBar.isShowing()) {
                    mProgressBar.dismiss();
                }
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        final int chatCount = (int) dataSnapshot.getChildrenCount();
        FirebaseDatabase.getInstance().getReference()
                .child(Constants.CHAT).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                getOpponentUniqueId(dataSnapshot.getKey(), chatCount);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Database Error", databaseError.toString());
            }
        });
    }

    private void getOpponentUniqueId(String chatId, int chatCount) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String uniqueId = chatId.replace("_", "");
            uniqueId = uniqueId.replace(firebaseUser.getUid(), "");
            getChatUserFromAuthenticatedUsers(uniqueId, chatCount, chatId);
        }
    }

    private void getChatUserFromAuthenticatedUsers(String uniqueId, int chatCount, String chatId) {
        loadTeamCount = loadTeamCount + 1;
        for (int i = 0; i < mChatUser.size(); i++) {
            ChatUser chatUser = mChatUser.get(i);
            if (uniqueId.equals(chatUser.getUniqueId())) {
                getLatestChatTime(chatUser, chatId, mChatUser.size());
            }
        }
        if (loadTeamCount == chatCount) {
            addTeamChat();
        }
    }

    private void getLatestChatTime(final ChatUser chatUser, final String chatId, final int length) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        String authToken = "Bearer " + preferenceHelper.getString("user_token", "");
        Call<ResponseBody> call = apiInterface.getLatestChatTime(authToken, chatId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        try {
                            String data = response.body().string();
                            JSONObject jsonObject = new JSONObject(data);
                            JSONObject dataObject = new JSONObject(jsonObject.getString("data"));
                            String lastChatDate = dataObject.getString("latest_chat");
                            chatUser.setLastChatTime(lastChatDate);
                            if (chatId.contains(Constants.DefaultText.TEAM)) {
                                if ((length - 1) == teamCount) {
                                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                        mProgressDialog.dismiss();
                                    }
                                    if (mProgressBar != null && mProgressBar.isShowing()) {
                                        mProgressBar.dismiss();
                                    }
                                    if (mSwipeRefreshLayout.isRefreshing()) {
                                        mSwipeRefreshLayout.setRefreshing(false);
                                    }
                                    mChatUserList.add(chatUser);
                                    addUserToAdapter(mChatUserList);
                                } else {
                                    mChatUserList.add(chatUser);
                                    teamCount++;
                                }
                            } else {
                                mChatUserList.add(chatUser);
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                CommonUtils.ServerFailureHandleMethod(getActivity(), t);
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                if (mProgressBar != null && mProgressBar.isShowing()) {
                    mProgressBar.dismiss();
                }
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void addTeamChat() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        String authToken = "Bearer " + preferenceHelper.getString("user_token", "");
        Call<ResponseBody> call = apiInterface.MyTeam(authToken);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                        JSONObject json = new JSONObject(data);
                        if (!json.getString("data").equals("0")) {
                            JSONArray array = new JSONArray(json.getString("data"));
                            if (array != null) {
                                if (array.length() > 0) {
                                    for (int i = 0; i < array.length(); i++) {
                                        ChatUser chatUser = new ChatUser();
                                        String teamId = array.getJSONObject(i).getString("team_id");
                                        chatUser.setUniqueId(Constants.DefaultText.TEAM + Constants.DefaultText.HYPHEN + teamId);
                                        chatUser.setUserName(array.getJSONObject(i).getString("team_name"));
                                        chatUser.setImageUrl(array.getJSONObject(i).getString("team_logo"));
                                        getLatestChatTime(chatUser, chatUser.getUniqueId(), array.length());
                                    }
                                } else {
                                    if (mChatUserList.size() == 0) {
                                        mNoChatHistoryView.setVisibility(View.VISIBLE);
                                    } else {
                                        mNoChatHistoryView.setVisibility(View.GONE);
//                                        addUserToAdapter(mChatUserList);
                                    }
                                }
                            }
                        } else {
                            mNoChatHistoryView.setVisibility(View.VISIBLE);
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                            }
                            if (mProgressBar != null && mProgressBar.isShowing()) {
                                mProgressBar.dismiss();
                            }
                            if (mSwipeRefreshLayout.isRefreshing()) {
                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                        }
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                String error = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(error);
                                String msg = jsonObject.getString("message");
                                String code = jsonObject.getString("code");
                                if (!code.equals("500")) {
                                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), R.string.error500, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                String error = response.message();
                                Toast.makeText(getActivity(), R.string.error500, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String error = response.message();
                            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                CommonUtils.ServerFailureHandleMethod(getActivity(), t);
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                if (mProgressBar != null && mProgressBar.isShowing()) {
                    mProgressBar.dismiss();
                }
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void addUserToAdapter(List<ChatUser> chatUser) {
        Collections.sort(chatUser, new Comparator<ChatUser>() {
            @Override
            public int compare(ChatUser chatUser1, ChatUser chatUser2) {
                return chatUser2.getLastChatTime().compareTo(chatUser1.getLastChatTime());
            }
        });

        for (int i = 0; i < chatUser.size(); i++) {
            mChatAdapter.add(chatUser.get(i));
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.e("Database Error", databaseError.toString());
    }

    @Override
    public void onItemClicked(View view, int position) {
        Intent intent = new Intent(getActivity(), ChatDetailActivity.class);
        intent.putExtra(Constants.BundleKeys.FCM_UID, mChatAdapter.getChatUsers().get(position));
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        if (CommonUtils.isNetworkAvailable(getActivity())) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            if (mProgressBar != null && mProgressBar.isShowing()) {
                mProgressBar.dismiss();
            }
            mChatUser = new ArrayList<>();
            mChatUserList = new ArrayList<>();
            loadTeamCount = 0;
            teamCount = 0;
            if (mChatAdapter != null) {
                mChatAdapter.clear();
            }
            getFirebaseUserList();
        } else {
            Toast.makeText(getActivity(), Constants.INTERNET_ERROR, Toast.LENGTH_LONG).show();
        }
    }
}