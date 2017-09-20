package com.ontro.firebase;


import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Mohanraj on 4/28/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Firebase", "Refreshed token: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {
       /* PreferenceHelper preferenceHelper = new PreferenceHelper(this, Constants.APP_NAME, 0);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        String authToken = "Bearer " + preferenceHelper.getString("user_token", "");
        NotificationTokenRequest notificationTokenRequest = new NotificationTokenRequest();
        notificationTokenRequest.setFcmToken(refreshedToken);
        Call<ResponseBody> call = apiInterface.sendFcmTokenToServer(authToken, notificationTokenRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                   if (response.body() != null && response.code() == 200) {
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                       JSONObject jsonObject = new JSONObject(data);
                       String msg = jsonObject.getString("message");
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                String error = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(error);
                                String msg = jsonObject.getString("message");
                            } catch (JSONException e) {
                                e.printStackTrace();
                                String error = response.message();
                            }
                        } else {
                            String error = response.message();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });*/
    }
}
