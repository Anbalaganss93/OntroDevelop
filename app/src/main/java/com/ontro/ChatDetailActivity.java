package com.ontro;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ontro.adapters.ChatRecyclerAdapter;
import com.ontro.customui.ProfileImageView;
import com.ontro.dto.Chat;
import com.ontro.dto.ChatDetailModel;
import com.ontro.dto.ChatUser;
import com.ontro.dto.PlayerTextColor;
import com.ontro.rest.ApiClient;
import com.ontro.rest.ApiInterface;
import com.ontro.utils.PreferenceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatDetailActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    private static final String TAG = "FirebaseChat";
    private static final String MESSAGE_SENT_EVENT = "message_sent";
    private ImageView mNavigationBackImageView, mSendImageView;
    private ProfileImageView mPlayerImageView;
    private TextView mPlayerNameTextView;
    private EditText mInputMessageView;
    private RecyclerView mMessageRecyclerView;
    private RelativeLayout mToolbarLayout;
    private CircularProgressView mProgressBar;
    private LinearLayoutManager mLinearLayoutManager;
    private String mUsername, mPhotoUrl, mOpponentFcmUniqueId, mOpponentFcmToken;
    private FirebaseUser mFirebaseUser;
    private FirebaseAnalytics mFirebaseAnalytics;
    private List<Chat> chatList;
    private ChatRecyclerAdapter mChatRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);
        overridePendingTransition(R.anim.slide_in_right, R.anim.pause);
        initView();
        InitInitialization();
        setTypeFace();
        setListener();
    }

    private void initView() {
        mNavigationBackImageView = (ImageView) findViewById(R.id.activity_chat_detail_iv_back);
        mPlayerImageView = (ProfileImageView) findViewById(R.id.activity_chat_detail_iv_player);
        mInputMessageView = (EditText) findViewById(R.id.activity_chat_detail_et_input_message);
        mPlayerNameTextView = (TextView) findViewById(R.id.activity_chat_detail_tv_player_name);
        mSendImageView = (ImageView) findViewById(R.id.activity_chat_detail_iv_send);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.activity_chat_detail_rv_list);
        mProgressBar = (CircularProgressView) findViewById(R.id.activity_chat_detail_pb);
        mToolbarLayout = (RelativeLayout) findViewById(R.id.activity_chat_detail_rl_toolbar);
    }

    private void InitInitialization() {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser != null) {
            mUsername = mFirebaseUser.getDisplayName();
            mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
        }
        Intent intent = getIntent();
        if (intent != null) {
            ChatUser chatUser = (ChatUser) intent.getSerializableExtra(Constants.BundleKeys.FCM_UID);
            if (chatUser != null) {
                mPlayerNameTextView.setText(chatUser.getUserName());
                mOpponentFcmToken = chatUser.getPlayerFcmToken();
                String photoUrl = chatUser.getImageUrl();
                if (photoUrl != null && !photoUrl.equals("")) {
                    Glide.with(this).load(photoUrl).placeholder(R.drawable.profiledefaultimg).dontAnimate().into(mPlayerImageView);
                } else {
                    Glide.with(this).load(R.drawable.profiledefaultimg).dontAnimate().into(mPlayerImageView);
                }
                mOpponentFcmUniqueId = chatUser.getUniqueId();
                if (mOpponentFcmUniqueId.contains(Constants.DefaultText.TEAM)) {
                    mToolbarLayout.setBackgroundColor(ContextCompat.getColor(ChatDetailActivity.this, R.color.team_chat_toolbar_color));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Window window = getWindow();
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(ContextCompat.getColor(ChatDetailActivity.this, R.color.team_chat_status_bar_color));
                    }
                    setTeamQueryMessage(mOpponentFcmUniqueId);
                } else {
                    setIndividualChatQueryMessage();
                    mToolbarLayout.setBackgroundColor(ContextCompat.getColor(ChatDetailActivity.this, R.color.toolbar_color));
                }
            }
        }
    }

    private void setTypeFace() {
        Typeface typefaceRegular = Typeface.createFromAsset(getAssets(), "fonts/roboto_light.ttf");
        mInputMessageView.setTypeface(typefaceRegular);
    }

    private void setListener() {
        mSendImageView.setOnClickListener(this);
        mNavigationBackImageView.setOnClickListener(this);
        mInputMessageView.addTextChangedListener(this);
    }

    private void setTeamQueryMessage(final String teamChat) {
        final DatabaseReference mQueryReference = FirebaseDatabase.getInstance().getReference();
        chatList = new ArrayList<>();
        mQueryReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(Constants.CHAT)) {
                    mQueryReference.child(Constants.CHAT).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (mChatRecyclerAdapter != null) {
                                mChatRecyclerAdapter.clear();
                            }
                            if (dataSnapshot.hasChild(teamChat)) {
                                mQueryReference.child(Constants.CHAT)
                                        .child(teamChat)
                                        .addChildEventListener(new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                                                Chat chat = dataSnapshot.getValue(Chat.class);
                                                chat.setId(dataSnapshot.getKey());
                                                setAdapter(chat, Constants.DefaultText.TEAM);
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
                                                Log.d(TAG, databaseError.toString());
                                            }
                                        });
                            } else {
                                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(TAG, databaseError.toString());
                        }
                    });
                } else {
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.toString());
            }

        });
    }

    private void setIndividualChatQueryMessage() {
        final String chatFromSenderToReceiver = mFirebaseUser.getUid() + "_" + mOpponentFcmUniqueId;
        final String chatFromReceiverToSender = mOpponentFcmUniqueId + "_" + mFirebaseUser.getUid();
        final DatabaseReference mQueryReference = FirebaseDatabase.getInstance().getReference();
        chatList = new ArrayList<>();
        mQueryReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(Constants.CHAT)) {
                    mQueryReference.child(Constants.CHAT).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (mChatRecyclerAdapter != null) {
                                mChatRecyclerAdapter.clear();
                            }
                            if (dataSnapshot.hasChild(chatFromSenderToReceiver)) {
                                mQueryReference.child(Constants.CHAT)
                                        .child(chatFromSenderToReceiver)
                                        .addChildEventListener(new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                                                Chat chat = dataSnapshot.getValue(Chat.class);
                                                chat.setId(dataSnapshot.getKey());
                                                setAdapter(chat, Constants.DefaultText.INDIVIDUAL);
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
                                                Log.d(TAG, databaseError.toString());
                                            }
                                        });
                            } else if (dataSnapshot.hasChild(chatFromReceiverToSender)) {
                                mQueryReference.child(Constants.CHAT).child(chatFromReceiverToSender).addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                                        Chat chat = dataSnapshot.getValue(Chat.class);
                                        chat.setId(dataSnapshot.getKey());
                                        setAdapter(chat, Constants.DefaultText.INDIVIDUAL);
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
                                        Log.d(TAG, databaseError.toString());
                                    }
                                });

                            } else {
                                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(TAG, databaseError.toString());
                        }
                    });
                } else {
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.toString());
            }

        });
    }

    private void setAdapter(Chat chat, String chatType) {
        if (mChatRecyclerAdapter == null) {
            mChatRecyclerAdapter = new ChatRecyclerAdapter(ChatDetailActivity.this, chatList);
            mMessageRecyclerView.setAdapter(mChatRecyclerAdapter);
        }
        mChatRecyclerAdapter.add(chat, chatType);
        mMessageRecyclerView.smoothScrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_chat_detail_iv_back:
                finish();
                overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
                break;
            case R.id.activity_chat_detail_iv_send:
                if (!TextUtils.isEmpty(mInputMessageView.getText()))
                    if (mOpponentFcmUniqueId.contains(Constants.DefaultText.TEAM)) {
                        sendTeamMessage(mOpponentFcmUniqueId);
                    } else {
                        sendIndividualChatMessage();
                    }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_out_left, R.anim.pause);
    }

    private void sendIndividualChatMessage() {
        final DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        final String chatFromSenderToReceiver = mFirebaseUser.getUid() + "_" + mOpponentFcmUniqueId;
        final String chatFromReceiverToSender = mOpponentFcmUniqueId + "_" + mFirebaseUser.getUid();
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DefaultText.DATE_AND_TIME);
        final String date = sdf.format(currentTime);

        final Chat friendlyMessage = new Chat(mInputMessageView.getText().toString().trim(), mUsername,
                mPhotoUrl, date);
        friendlyMessage.setFcmToken(mOpponentFcmToken);
        friendlyMessage.setChatType(Constants.DefaultText.ONE);
        mFirebaseDatabaseReference.child(Constants.CHAT).getRef()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(chatFromSenderToReceiver)) {
                            Log.e(TAG, "sendMessageToFirebaseUser: " + chatFromSenderToReceiver + " exists");
                            mFirebaseDatabaseReference.child(Constants.CHAT)
                                    .child(chatFromSenderToReceiver)
                                    .push()
                                    .setValue(friendlyMessage);
                            updateChatTime(chatFromSenderToReceiver, date, mInputMessageView.getText().toString());
                        } else if (dataSnapshot.hasChild(chatFromReceiverToSender)) {
                            Log.e(TAG, "sendMessageToFirebaseUser: " + chatFromReceiverToSender + " exists");
                            mFirebaseDatabaseReference.child(Constants.CHAT)
                                    .child(chatFromReceiverToSender)
                                    .push()
                                    .setValue(friendlyMessage);
                            updateChatTime(chatFromReceiverToSender, date, mInputMessageView.getText().toString());
                        } else {
                            Log.e(TAG, "sendMessageToFirebaseUser: success");
                            mFirebaseDatabaseReference.child(Constants.CHAT)
                                    .child(chatFromSenderToReceiver)
                                    .push()
                                    .setValue(friendlyMessage);
                            updateChatTime(chatFromSenderToReceiver, date, mInputMessageView.getText().toString());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        mFirebaseDatabaseReference.child(Constants.DefaultText.NOTIFICATION).getRef()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mFirebaseDatabaseReference.child(Constants.DefaultText.NOTIFICATION)
                                .push()
                                .setValue(friendlyMessage);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        mInputMessageView.setText("");
        mFirebaseAnalytics.logEvent(MESSAGE_SENT_EVENT, null);
    }

    private void sendTeamMessage(final String teamChat) {
        final DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DefaultText.DATE_AND_TIME);
        final String date = sdf.format(currentTime);
        String teamId = mOpponentFcmUniqueId.replace("Team-", "");
        final Chat friendlyMessage = new Chat(mInputMessageView.getText().toString(), mUsername,
                mPhotoUrl, date);
        friendlyMessage.setFcmToken(teamId);
        friendlyMessage.setChatType(Constants.DefaultText.TWO);
        friendlyMessage.setSenderFcmId(mFirebaseUser.getUid());
        FirebaseUserColorHandler firebaseUserColorHandler = new FirebaseUserColorHandler(ChatDetailActivity.this);
        List<PlayerTextColor> playerTextColors = firebaseUserColorHandler.getPlayerColorInfo();
        if (playerTextColors.size() > 0) {
            int count = 0;
            for (PlayerTextColor playerTextColor : playerTextColors) {
                if (playerTextColor.getPlayerName().equals(mUsername)) {
                    friendlyMessage.setUserColor(playerTextColor.getPlayerColor());
                    break;
                } else {
                    count++;
                }
            }
            if (count == playerTextColors.size()) {
                PlayerTextColor playerTextColor = new PlayerTextColor();
                playerTextColor.setPlayerName(mUsername);
                playerTextColor.setPlayerColor(getRandomColor());
                friendlyMessage.setUserColor(playerTextColor.getPlayerColor());
                firebaseUserColorHandler.insertPlayerProfile(playerTextColor);
            }
        } else {
            PlayerTextColor playerTextColor = new PlayerTextColor();
            playerTextColor.setPlayerName(mUsername);
            playerTextColor.setPlayerColor(getRandomColor());
            friendlyMessage.setUserColor(playerTextColor.getPlayerColor());
            firebaseUserColorHandler.insertPlayerProfile(playerTextColor);
        }

        mFirebaseDatabaseReference.child(Constants.CHAT).getRef()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(teamChat)) {
                            mFirebaseDatabaseReference.child(Constants.CHAT)
                                    .child(teamChat)
                                    .push()
                                    .setValue(friendlyMessage);
                            updateChatTime(teamChat, date,mInputMessageView.getText().toString());
                        } else {
                            mFirebaseDatabaseReference.child(Constants.CHAT)
                                    .child(teamChat)
                                    .push()
                                    .setValue(friendlyMessage);
                            updateChatTime(teamChat, date,mInputMessageView.getText().toString());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        mFirebaseDatabaseReference.child(Constants.DefaultText.NOTIFICATION).getRef()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mFirebaseDatabaseReference.child(Constants.DefaultText.NOTIFICATION)
                                .push()
                                .setValue(friendlyMessage);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        mInputMessageView.setText("");
       /* InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mSendImageView.getWindowToken(), 0);*/
        mFirebaseAnalytics.logEvent(MESSAGE_SENT_EVENT, null);
    }

    public int getRandomColor() {
        Random random = new Random();
        final int baseColor = Color.WHITE;
        final int baseRed = Color.red(baseColor);
        final int baseGreen = Color.green(baseColor);
        final int baseBlue = Color.blue(baseColor);
        final int red = (baseRed + random.nextInt(256)) / 2;
        final int green = (baseGreen + random.nextInt(256)) / 2;
        final int blue = (baseBlue + random.nextInt(256)) / 2;
        return Color.rgb(red, green, blue);
    }

    private void updateChatTime(String chatId, String chatTime, String lastMessage) {
        PreferenceHelper preferenceHelper = new PreferenceHelper(ChatDetailActivity.this, Constants.APP_NAME, 0);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        String authToken = "Bearer " + preferenceHelper.getString("user_token", "");
        ChatDetailModel chatDetailModel = new ChatDetailModel();
        chatDetailModel.setChatUniqueId(chatId);
        chatDetailModel.setLatestChatTime(chatTime);

        Call<ResponseBody> call = apiInterface.updateChatTime(authToken, chatDetailModel);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        try {
                            String data = response.body().string();
                            Log.d("RESPONSE", data);
                            JSONObject jsonObject = new JSONObject(data);
                            Log.d(TAG, jsonObject.getString("message"));
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "On failure called");
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        if (charSequence.toString().trim().length() > 0) {
            mSendImageView.setEnabled(true);
        } else {
            mSendImageView.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}

