package com.example.niket.chatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.niket.chatapplication.databinding.ActivitySignInBinding;
import com.example.niket.chatapplication.pojoClass.MessagePojo;
import com.example.niket.chatapplication.pojoClass.MyPojo;
import com.example.niket.chatapplication.pojoClass.SignupModelClass;
import com.example.niket.chatapplication.search.SearchViewActivity;
import com.example.niket.chatapplication.utils.ThreadManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class SignInActivity extends AppCompatActivity {
    ActivitySignInBinding binding;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    SharedPreferences sharedPreferences;
    ProgressDialog dialog;
    SignupModelClass signupModelClass;
    String receiverID, receiverName, receiverImage, groupID, senderId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in);

        firebaseDatabase = FirebaseDatabase.getInstance();


        if (firebaseDatabase != null)
            databaseReference = firebaseDatabase.getReference("data").child("users");

        dialog = new ProgressDialog(this);
        dialog.setTitle("please wait....");
        dialog.setMessage("verifying user");
        dialog.setCanceledOnTouchOutside(false);


        sharedPreferences = getSharedPreferences("save", 0);

        receiverID = sharedPreferences.getString("receiverID", "");
        receiverImage = sharedPreferences.getString("receiverImage", "");
        receiverName = sharedPreferences.getString("receiverName", "");

        groupID = sharedPreferences.getString("groupID", "");
        senderId = sharedPreferences.getString("senderID", "");


        if (sharedPreferences.getBoolean("status", false)) {

            Intent intent = new Intent(SignInActivity.this, IndividualChatPage.class);
            intent.putExtra("name", receiverName);
            intent.putExtra("image", receiverImage);
            intent.putExtra("ReceiverID", receiverID);
            intent.putExtra("groupID", groupID);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, SignUp.class);
                startActivity(intent);
            }
        });

        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    if (binding.username.getText().toString().trim().length() == 0) {
                        binding.username.setError("This field is required");
                    } else if (binding.editTextPassword.getText().toString().trim().length() == 0) {
                        binding.editTextPassword.setError("This field is required");
                    } else {

                        dialog.show();

                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                boolean userFound = false;

                                for (DataSnapshot data : dataSnapshot.getChildren()) {

                                    signupModelClass = data.getValue(SignupModelClass.class);

                                    if (binding.username.getText().toString().equals(signupModelClass.getUsername())
                                            && binding.editTextPassword.getText().toString().equals(signupModelClass.getPassword())) {

                                        dialog.cancel();

                                        userFound = true;

                                        break;
                                    }


                                }

                                if (userFound) {


                                    ThreadManager.getInstance().doWork(new ThreadManager.CustomRunnable() {
                                        @Override
                                        public void onBackground() {

                                            String firebaseInstanceID = FirebaseInstanceId.getInstance().getToken();


                                            if (firebaseInstanceID != null && !firebaseInstanceID.equalsIgnoreCase("")) {
                                                databaseReference = firebaseDatabase.getReference("data").child("users");

                                                //databaseReference.child(senderID).child("connectedWithPartner").setValue(true);
                                                databaseReference.child(signupModelClass.getUserID()).child("firebaseTokenID").setValue(firebaseInstanceID);

                                            }
                                        }

                                    });

                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    // editor.putBoolean("status", true);
                                    editor.putString("senderID", signupModelClass.getUserID());
                                    editor.putString("senderName", signupModelClass.getUsername());
                                    editor.putString("senderImage", signupModelClass.getUserImage());

                                    editor.commit();

                                    Intent intent = new Intent(SignInActivity.this, ConnectYourPartner.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);

                                } else {
                                    dialog.cancel();
                                    Toast.makeText(SignInActivity.this, "invalid Credentials.......", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(SignInActivity.this, "Database error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }


                } catch (Exception e) {
                    dialog.cancel();
                }

            }
        });

    }
}
