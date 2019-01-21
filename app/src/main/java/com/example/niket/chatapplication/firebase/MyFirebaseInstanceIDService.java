package com.example.niket.chatapplication.firebase;

/**
 * Created by Zoptal.101 on 09/11/16.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.niket.chatapplication.GlobalConstants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "FirebaseIDService";
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    SharedPreferences sharedPreferences;

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        Log.d(TAG, "onTokenRefresh: "+refreshedToken);

        sharedPreferences = getSharedPreferences(GlobalConstants.SHARED_PREFRENCES, Context.MODE_PRIVATE);
        String senderId = sharedPreferences.getString("senderID", "");

        SharedPreferences.Editor editors = sharedPreferences.edit();
        editors.putString(GlobalConstants.FCM_APPLICATION_DEVICEID, refreshedToken);
        editors.putBoolean(GlobalConstants.IS_TOKEN_CHANGED, true);
        editors.commit();

        // Logger.e("Enlte FCM Token", preferences.getString(GlobalConstants.FCM_APPLICATION_DEVICEID, ""));
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
       // sendRegistrationToServer(refreshedToken, senderId);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param refreshedToken
     */
    private void sendRegistrationToServer(String refreshedToken, String senderID) {


        firebaseDatabase = FirebaseDatabase.getInstance();


        if (firebaseDatabase != null)
            databaseReference = firebaseDatabase.getReference("data").child("users");

        databaseReference.child(senderID).child("firebaseTokenID").setValue(refreshedToken);

        //  Implement this method to send token to your app server.

        //new UpdateDeviceTokenPresenter(this, preferences).updateToken(token);
    }
}
