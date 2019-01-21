package com.example.niket.chatapplication;

import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.niket.chatapplication.databinding.ActivityConnectYourPartnerBinding;
import com.example.niket.chatapplication.pojoClass.GroupModelClass;
import com.example.niket.chatapplication.pojoClass.SignupModelClass;
import com.example.niket.chatapplication.utils.ThreadManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class ConnectYourPartner extends AppCompatActivity {

    ActivityConnectYourPartnerBinding binding;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference2, databaseReference;
    String senderID;
    SharedPreferences sharedPreferences;
    ProgressDialog dialog;
    ImageView back;


    SignupModelClass signupModelClass;
    private boolean connectedWitPartner;
    private String receiverID;
    private String receiverImage;
    private String receiverName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_connect_your_partner);

        dialog = new ProgressDialog(this);
        dialog.setTitle("please wait....");
        dialog.setMessage("verifying user");
        dialog.setCanceledOnTouchOutside(false);

        sharedPreferences = getSharedPreferences("save", 0);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference2 = firebaseDatabase.getReference("data").child("users");


        receiverID = sharedPreferences.getString("receiverID", "");
        receiverImage = sharedPreferences.getString("receiverImage", "");
        receiverName = sharedPreferences.getString("receiverName", "");

        senderID = sharedPreferences.getString("senderID", "");
        binding.coupleCodeTv.setText(senderID);


        /*DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("data").child("users").child(senderID);
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (snapshot.hasChild("connectedWithPartner")) {

                    SignupModelClass signupModelClass = snapshot.getValue(SignupModelClass.class);

                    try {
                        if (signupModelClass != null) {

                            connectedWitPartner = signupModelClass.isConnectedWithPartner();

                            if (connectedWitPartner) {

                                Log.d("kk", "onDataChange: " + receiverID + " id" + receiverImage + "  " + receiverName);

                                Intent intent = new Intent(ConnectYourPartner.this, IndividualChatPage.class);
                                intent.putExtra("name", receiverName);
                                intent.putExtra("image", receiverImage);
                                intent.putExtra("ReceiverID", receiverID);

                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();

                            }

                        }

                    } catch (Exception e) {

                    }


                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/


        binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share your code");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, binding.coupleCodeTv.getText().toString());
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
            }
        });

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });


        binding.copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

                if (cm != null)
                    cm.setText(binding.coupleCodeTv.getText().toString());

                Toast.makeText(ConnectYourPartner.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });


        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (binding.codeEd.getText().toString().trim().length() == 0) {
                    binding.codeEd.setError("Enter your partner couple code");
                } else if (binding.codeEd.getText().toString().trim().equalsIgnoreCase(senderID)) {
                    binding.codeEd.setError("Invalid code..");
                } else {

                    try {
                        dialog.show();

                        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                boolean userFound = false;
                                for (DataSnapshot data : dataSnapshot.getChildren()) {

                                    String receiverID = data.getKey();

                                    if (binding.codeEd.getText().toString().trim().equalsIgnoreCase(receiverID)) {

                                        signupModelClass = data.getValue(SignupModelClass.class);

                                        dialog.cancel();

                                        userFound = true;

                                        break;

                                    }

                                }

                                if (userFound) {

                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("status", true);
                                    editor.putString("receiverID", signupModelClass.getUserID());
                                    editor.putString("receiverName", signupModelClass.getUsername());
                                    editor.putString("receiverImage", signupModelClass.getUserImage());
                                    editor.putString("receiverFCMID",signupModelClass.getFirebaseTokenID());
                                    editor.commit();


                                    Intent intent = new Intent(ConnectYourPartner.this, IndividualChatPage.class);
                                    intent.putExtra("name", signupModelClass.getUsername());
                                    intent.putExtra("image", signupModelClass.getUserImage());
                                    intent.putExtra("ReceiverID", signupModelClass.getUserID());
                                    intent.putExtra("receiverFCMID", signupModelClass.getFirebaseTokenID());

                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();


                                    //databaseReference2.child(senderID).child("connectedWithPartner").setValue(true);


                                } else {
                                    dialog.cancel();
                                    Toast.makeText(ConnectYourPartner.this, "no user found with this code", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                dialog.cancel();
                                Toast.makeText(ConnectYourPartner.this, "Database error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        dialog.cancel();
                    }


                }

            }
        });


    }
}
