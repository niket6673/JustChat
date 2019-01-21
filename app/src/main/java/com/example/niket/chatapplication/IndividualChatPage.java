package com.example.niket.chatapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.niket.chatapplication.Adapter.ChatAdapter;
import com.example.niket.chatapplication.HelperClassForImageBrowseFromGalary_Camera.SelectImageHelper;
import com.example.niket.chatapplication.pojoClass.MessagePojo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class IndividualChatPage extends AppCompatActivity {

    CircleImageView circleImageView;
    SharedPreferences sharedPreferences;
    ImageView attachImageView;

    SelectImageHelper helper;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceBySender, databaseReference;


    Random random;

    String image, senderImage;
    ImageView backButton;

    TextView textView;
    CircleImageView send;

    ImageView emoji, attach;
    EmojiconEditText message;
    View rootView;
    EmojIconActions emojIcon;

    StorageReference storageReference;
    String receiverID, senderID, name, senderName;
    MessagePojo messagePojo;
    ArrayList<MessagePojo> messagePojoArrayList = new ArrayList<>();

    ChatAdapter chatAdapter;
    ImageView option;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    String messageKey;
    ArrayList<String> arrayList = new ArrayList<>();
    private String resultPairName;

    String FCM_DEVICEID;
    public static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    OkHttpClient mClient;
    String firebaseInstanceID;
    String receiverFCMID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_chat_page);
        rootView = findViewById(R.id.linear);

        sharedPreferences = getSharedPreferences(GlobalConstants.SHARED_PREFRENCES, 0);
        firebaseDatabase = FirebaseDatabase.getInstance();

        firebaseInstanceID = FirebaseInstanceId.getInstance().getToken();

        attachImageView = findViewById(R.id.attachFile);
        helper = new SelectImageHelper(this, attachImageView);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        circleImageView = findViewById(R.id.profileimage);
        textView = findViewById(R.id.name);

        option = findViewById(R.id.option);
        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTwoButtonDialog(IndividualChatPage.this);
            }
        });


        send = findViewById(R.id.send);
        //set send button set as enabled=false
        send.setEnabled(false);

        message = findViewById(R.id.editMessage);
        recyclerView = findViewById(R.id.recyclerview_message);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        random = new Random();
        final String s1 = String.valueOf(random.nextInt(263443));
        setSupportActionBar(toolbar);

        SharedPreferences sharedPreferences = getSharedPreferences("save", 0);
        senderID = sharedPreferences.getString("senderID", "");
        senderName = sharedPreferences.getString("senderName", "");
        senderImage = sharedPreferences.getString("senderImage", "");
        receiverFCMID = sharedPreferences.getString("receiverFCMID", "");

        Intent i = getIntent();
        name = i.getStringExtra("name");
        image = i.getStringExtra("image");
        receiverID = i.getStringExtra("ReceiverID");

        if (sharedPreferences.getString("receiverFCMID", "").isEmpty()) {
            receiverFCMID = i.getStringExtra("ReceiverID");
        }
        //receiverFCMID =i.getStringExtra("receiverFCMID");

        //get StorageReference Instance
        storageReference = FirebaseStorage.getInstance().getReference();

        //attach file
        // attach = findViewById(R.id.attachFile);

        //Emoji keyboard
        emoji = findViewById(R.id.emoji);
        emojIcon = new EmojIconActions(IndividualChatPage.this, rootView, message, emoji, "#F44336", "#e8e8e8", "#f4f4f4");
        emojIcon.ShowEmojIcon();


        backButton = findViewById(R.id.backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        attachImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ImageView profile_dialog;
                Dialog dialog = new Dialog(IndividualChatPage.this);
                dialog.setContentView(R.layout.main_profile_dialog);

                profile_dialog = dialog.findViewById(R.id.profile_dialog);

                if (image != null)
                    Glide.with(IndividualChatPage.this).load(image).diskCacheStrategy(DiskCacheStrategy.ALL).into(profile_dialog);

                dialog.show();

                Window window = dialog.getWindow();
                window.setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            }
        });


        /*final String p1 = senderID + "_" + receiverID;
        final String p2 = receiverID + "_" + senderID;
        //resultPairName = p1;


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("data").child("chat");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (snapshot.hasChild(p1)) {
                    resultPairName = p1;
                } else if (snapshot.hasChild(p2)) {
                    resultPairName = p2;
                } else {
                    resultPairName = p1;
                }



                Log.d("j", "onDataChange: " + snapshot.toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/


        //databaseReferenceByReceiver = firebaseDatabase.getReference("data").child("chat").child(receiverID + "_" + senderID);


        Glide.with(this).load(image).diskCacheStrategy(DiskCacheStrategy.ALL).into(circleImageView);
        textView.setText(name);


        //check if edittext is empty or not ,if empty keep send button as non clickable
        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {


                if (editable.toString().trim().length() == -1) {
                    send.setEnabled(false);
                } else {
                    send.setEnabled(true);
                }
            }
        });


        final String p1 = senderID + "_" + receiverID;
        final String p2 = receiverID + "_" + senderID;
        resultPairName = p1;

        databaseReferenceBySender = firebaseDatabase.getReference("data").child("chat");
        databaseReferenceBySender.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                messagePojoArrayList.clear();

                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    if (data.getKey().equalsIgnoreCase(p1)) {
                        resultPairName = p1;
                    } else if (data.getKey().equalsIgnoreCase(p2)) {
                        resultPairName = p2;
                    } else {
                        resultPairName = p1;
                    }


                    for (DataSnapshot data2 : data.getChildren()) {

                        messagePojo = data2.getValue(MessagePojo.class);

                        messageKey = data2.getKey();
                        arrayList.add(messageKey);


                        messagePojoArrayList.add(messagePojo);
                    }


                }

                chatAdapter = new ChatAdapter(IndividualChatPage.this, messagePojoArrayList, senderID, receiverID, arrayList, name, senderName, resultPairName);
                recyclerView.setAdapter(chatAdapter);

                //to set the postion of recyclerview to last
                if (messagePojoArrayList != null && !messagePojoArrayList.isEmpty()) {
                    recyclerView.scrollToPosition(messagePojoArrayList.size() - 1);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


       /* mClient = new OkHttpClient();
        firebaseInstanceID = FirebaseInstanceId.getInstance().getToken();*/


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!TextUtils.isEmpty(message.getText().toString())) {

                    databaseReferenceBySender = firebaseDatabase.getReference("data").child("chat").child(resultPairName);

                    messagePojo = new MessagePojo();
                    messagePojo.setSenderID(senderID);
                    messagePojo.setReceiverID(receiverID);
                    messagePojo.setMessageId(databaseReferenceBySender.push().getKey());
                    messagePojo.setMessage(message.getText().toString());
                    messagePojo.setReceiverName(name);
                    messagePojo.setSenderName(senderName);

                    databaseReferenceBySender.child(messagePojo.getMessageId()).setValue(messagePojo);
                    //databaseReferenceByReceiver.child(senderID + "_" + receiverID).child(messagePojo.getMessageId()).setValue(messagePojo);


                    //sending notification
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put("cFpRRGD6D3s:APA91bE_toqOz4cvZgFUod8BgNIRMNKZvhd73Lh3rQJ6RYswE0BR5EQvEXcLN5oz7RzAtdVkvd_6vRERAVbjZQ6T8O4zl2HvhtQoH_TEI4GiYPbYdBRkSS_E2FfpD7mz3tg0R7t-opuC");

                    //sendMessage(jsonArray, senderName, "How r u", "Http:\\google.com", message.getText().toString());

                    sendNotification(receiverFCMID, senderName, messagePojo.getMessage(), senderImage);

                    //sendNotification(jsonArray, senderName, "How r u", "Http:\\google.com", message.getText().toString());
                    message.setText(null);
                    send.setEnabled(false);

                }

            }


        });



      /*  databaseReferenceBySender.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                messageKey = dataSnapshot.getKey();
                Log.d("12345", "onDataChange: " + messageKey);
                arrayList.add(messageKey);

                MessagePojo messagePojo = dataSnapshot.getValue(MessagePojo.class);
                messagePojoArrayList.add(messagePojo);


                chatAdapter = new ChatAdapter(IndividualChatPage.this, messagePojoArrayList, senderID, receiverID, arrayList, name, senderName);
                recyclerView.setAdapter(chatAdapter);

                //to set the postion of recyclerview to last
                if (messagePojoArrayList != null && !messagePojoArrayList.isEmpty()) {
                    recyclerView.scrollToPosition(messagePojoArrayList.size() - 1);
                }
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

            }


        });*/

        /*databaseReferenceByReceiver.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                messageKey = dataSnapshot.getKey();
                Log.d("12345", "onDataChange: " + messageKey);
                arrayList.add(messageKey);

                MessagePojo messagePojo = dataSnapshot.getValue(MessagePojo.class);
                messagePojoArrayList.add(messagePojo);

                chatAdapter = new ChatAdapter(IndividualChatPage.this, messagePojoArrayList, senderID, receiverID, arrayList, name, senderName);
                recyclerView.setAdapter(chatAdapter);

                //to set the postion of recyclerview to last
                if (messagePojoArrayList != null && !messagePojoArrayList.isEmpty()) {
                    recyclerView.scrollToPosition(messagePojoArrayList.size() - 1);
                }
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

            }


        });*/


    }


    public void showTwoButtonDialog(final Activity context) {
        //Typeface custom_font = Typeface.createFromAsset(context.getAssets(), context.getResources().getString(R.string.font_regular));
        final Dialog dialogBox = new Dialog(context,
                android.R.style.Theme_Translucent_NoTitleBar);
        dialogBox.setContentView(R.layout.popuopwindow);

        RelativeLayout outer = dialogBox.findViewById(R.id.outer);
        TextView referCode = (TextView) dialogBox.findViewById(R.id.refercode_menu);

        TextView logout = (TextView) dialogBox
                .findViewById(R.id.logout_menu);


        outer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBox.dismiss();
            }
        });

        referCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBox.dismiss();
                Intent intent = new Intent(IndividualChatPage.this, ShowYourCoupleCode.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBox.dismiss();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();

                Intent intent = new Intent(IndividualChatPage.this, SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


            }
        });


        dialogBox.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    @Override
    protected void onResume() {
        super.onResume();


    }


    /*public void sendNotification(final JSONArray recipients, final String title, final String body, final String icon, final String message) {

        StringRequest strReq = new StringRequest(com.android.volley.Request.Method.POST,
                GlobalConstants.FCM_MESSAGE_URL,
                new com.android.volley.Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        Log.d("lll", "onResponse: " + response);


                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();

                try {

                   *//* JSONObject notification = new JSONObject();
                    notification.put("body", body);
                    notification.put("title", title);
                    notification.put("icon", icon);

                    JSONObject data = new JSONObject();
                    data.put("message", message);

                    JSONObject root = new JSONObject();
                    root.put("notification", notification);
                    root.put("data", data);
                    root.put("registration_ids", recipients);*//*


                    params.put("Content-Type", "application/json");
                    params.put("Authorization", "key=AIzaSyCwJPwmuUlbwmHlxrA_M7fs0ROa4ex8LX8");


                    //params.put("notification", notification);
                    params.put("data", message);
                    //params.put("registration_ids", recipients);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, "");


    }


    private void processResult(String res) {
        JSONObject jo = null;
        try {
            jo = new JSONObject(res);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jo == null) {
            //Toast.makeText(mContext, "Server Error.", Toast.LENGTH_LONG).show();
            //resultCallback.onError("Error: " + res);
        } else {
            if (jo.has("status")) {

            } else {
                //Toast.makeText(mContext, "Server error.", Toast.LENGTH_LONG).show();
            }
        }
    }*/

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private void sendNotification(final String regToken, final String senderName, final String message, final String image) {
        new AsyncTask<String, String, Response>() {


            @Override
            protected Response doInBackground(String... params) {
                String finalResponse = "";
                Response response = null;
                try {
                    OkHttpClient client = new OkHttpClient();

                    JSONObject json = new JSONObject();

                    JSONObject dataJson = new JSONObject();
                    dataJson.put("body", message);
                    dataJson.put("title", senderName);
                    dataJson.put("icon", image);

                    json.put("notification", dataJson);
                    json.put("to", regToken);

                    RequestBody body = RequestBody.create(JSON, json.toString());

                    Request request = new Request.Builder()
                            .header("Authorization", "key=" + "AIzaSyCwJPwmuUlbwmHlxrA_M7fs0ROa4ex8LX8")
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body)
                            .build();
                    response = client.newCall(request).execute();
                    finalResponse = response.body().string();
                    Log.d("llll", "doInBackground: " + finalResponse);

                } catch (Exception e) {
                    //Log.d(TAG,e+"");
                }
                return response;
            }


            @Override
            protected void onPostExecute(Response s) {
                super.onPostExecute(s);

                Log.d("kkkk", "onPostExecute: " + s);
            }
        }.execute();

    }


  /*  @SuppressLint("StaticFieldLeak")
    public void sendMessage(final JSONArray recipients, final String title, final String body, final String icon, final String message) {

        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    JSONObject root = new JSONObject();

                    JSONObject notification = new JSONObject();
                    notification.put("body", body);
                    notification.put("title", title);
                    notification.put("icon", icon);

                    JSONObject data = new JSONObject();
                    data.put("message", message);

                    root.put("notification", notification);
                    root.put("data", data);
                    root.put("registration_ids", recipients);

                    String result = postToFCM(root.toString());
                    Log.d("Main Activity", "Result: " + result);
                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {


                //Toast.makeText(IndividualChatPage.this, result, Toast.LENGTH_SHORT).show();
                try {

                    JSONObject resultJson = new JSONObject(result);

                    int success, failure;

                    success = resultJson.getInt("success");
                    failure = resultJson.getInt("failure");

                    // Toast.makeText(IndividualChatPage.this, "Message Success: " + success + "Message Failed: " + failure, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(IndividualChatPage.this, "Message Failed, Unknown error occurred.", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    String postToFCM(String bodyString) throws IOException {

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, bodyString);
        Request request = new Request.Builder()
                .url(FCM_MESSAGE_URL)
                .post(body)
                .addHeader("Authorization", "key=" + "AIzaSyCwJPwmuUlbwmHlxrA_M7fs0ROa4ex8LX8")
                .build();
        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }
*/

}


