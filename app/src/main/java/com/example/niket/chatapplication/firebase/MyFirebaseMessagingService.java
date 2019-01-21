package com.example.niket.chatapplication.firebase;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.niket.chatapplication.GlobalConstants;
import com.example.niket.chatapplication.IndividualChatPage;
import com.example.niket.chatapplication.R;
import com.example.niket.chatapplication.SignInActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    String message = "", title = "Enlte", coins = "";
    Bitmap ImgBitmap = null;
    private Context mContext;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        //showNotification(remoteMessage.getData().get("data"));

        String notify = remoteMessage.getData().get("notification");

        Log.d(TAG, "onMessageReceived: " + notify);


        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]
       /* String notificationIdChat = "0";
        mContext = this;
        // (developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ

        Intent intent = new Intent(this, IndividualChatPage.class);

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            //init shared preferences...
            SharedPreferences preferences = getSharedPreferences(GlobalConstants.SHARED_PREFRENCES, MODE_PRIVATE);
            String savedUserId = preferences.getString("senderID", "");
            String username = preferences.getString("senderName", "");
            String userId = null;

            try {
                userId = remoteMessage.getData().get("user_id");
            } catch (Exception e) {

            }

            message = remoteMessage.getData().get("message");
            title = remoteMessage.getData().get("caption_text");

            try {
                notificationIdChat = remoteMessage.getData().get("user");
                message = remoteMessage.getData().get("message");
                // message = StringFormatter.convertUTF8ToString(message);
                if (notificationIdChat != null && notificationIdChat.length() > 0) {
                    intent.putExtra("user_id", notificationIdChat);
                    intent.putExtra("message", message);
                }
                if (savedUserId.equals(notificationIdChat)) {

                }
            } catch (Exception e) {

            }


            // Also if you intend on generating your own notifications as a result of a received FCM
            // message, here is where that should be initiated. See sendNotification method below.


            // [END receive_message]


        }*/
    }

    private void showNotification(String message) {
        Intent i = new Intent(this, SignInActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("FCM Test")
                .setContentText(message)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(0, builder.build());
    }
}
