package com.ontro.firebase;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ontro.Constants;

/**
 * Created by Mohanraj on 4/28/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String TAG = "Firebase";
    public static FirebaseNotificationListener firebaseNotificationListener;

    public static MyFirebaseMessagingService getNewInstance(FirebaseNotificationListener listener) {
        MyFirebaseMessagingService myFirebaseMessagingService = new MyFirebaseMessagingService();
        firebaseNotificationListener = listener;
        return myFirebaseMessagingService;
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From : " + remoteMessage.getFrom());
        if (firebaseNotificationListener != null) {
        /**
         * Getting data message from firebase
         * */
            if (remoteMessage.getData().size() > 0) {
                Log.d(TAG, remoteMessage.getData().toString());
            }
            /**
             * Getting notification message from firebase
             * */
            if(remoteMessage.getNotification() != null) {
                Constants.notificationview=1;
                if (remoteMessage.getData().containsKey("player_id")) {
                    Constants.invitecountindicator = 1;
                }
                firebaseNotificationListener.sendNotification(remoteMessage);
            }
        } else {
            Log.d(TAG, Constants.Messages.NOTIFICATION_LISTENER_NULL);
        }
    }

    public interface FirebaseNotificationListener {

        void sendNotification(RemoteMessage remoteMessage);
    }
}
