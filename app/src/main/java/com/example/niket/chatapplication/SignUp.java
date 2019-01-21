package com.example.niket.chatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.niket.chatapplication.HelperClassForImageBrowseFromGalary_Camera.SelectImageHelper;
import com.example.niket.chatapplication.databinding.ActivitySignUpBinding;
import com.example.niket.chatapplication.pojoClass.MyPojo;
import com.example.niket.chatapplication.pojoClass.SignupModelClass;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Random;

public class SignUp extends AppCompatActivity {

    ActivitySignUpBinding binding;
    ProgressDialog dialog;
    SelectImageHelper helper;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    StorageReference storageReference;

    Random random;

    SignupModelClass signupModelClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);

        firebaseDatabase = FirebaseDatabase.getInstance();

        //get selectHelper class instance
        helper = new SelectImageHelper(this, binding.circular);

        //get StorageReference Instance
        storageReference = FirebaseStorage.getInstance().getReference();

        //dialog
        dialog = new ProgressDialog(this);
        dialog.setTitle("please wait.....");
        dialog.setMessage("registering data to Database");
        dialog.setCanceledOnTouchOutside(false);

        random = new Random();
        final String s1 = String.valueOf(random.nextInt(263443));

        binding.circular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //using SelectImageHelper class to browse image
                helper.selectImageOption();
            }
        });


        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        //register new user
        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    Uri file = helper.getURI_FOR_SELECTED_IMAGE();

                    if (binding.username.getText().toString().trim().length() == 0) {
                        binding.username.setError("This field is required");
                    } else if (binding.editTextPassword.getText().toString().trim().length() == 0) {
                        binding.editTextPassword.setError("This field is required");
                    } else if (file == null) {
                        Toast.makeText(SignUp.this, "set profile image", Toast.LENGTH_SHORT).show();
                    } else {
                        dialog.show();

                        //get the DatabaseReference
                        databaseReference = firebaseDatabase.getReference("data").child("users");


                        StorageReference storageReference2 = storageReference.child(s1);

                        storageReference2.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                                signupModelClass = new SignupModelClass();

                                signupModelClass.setUserID(databaseReference.push().getKey());
                                signupModelClass.setUserImage(downloadUrl + "");
                                signupModelClass.setUsername(binding.username.getText().toString());
                                signupModelClass.setPassword(binding.editTextPassword.getText().toString());
                                signupModelClass.setFirebaseTokenID("");

                                databaseReference.child(signupModelClass.getUserID()).setValue(signupModelClass);

                                dialog.cancel();

                                Intent intent = new Intent(SignUp.this, SignInActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                                //intent.putExtra("Profile_image", signupModelClass.getUserImage());
                                //intent.putExtra("username",activityRegisterBinding.editTextName.getText().toString());
                                startActivity(intent);
                                Toast.makeText(SignUp.this, "Registration successfull", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignUp.this, "Database Error", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                } catch (Exception e) {
                    dialog.dismiss();
                    Toast.makeText(SignUp.this, "set your profile image", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        helper.handleResult(requestCode, resultCode, result);  // call this helper class method
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, final @NonNull String[] permissions, final @NonNull int[] grantResults) {
        helper.handleGrantedPermisson(requestCode, grantResults);   // call this helper class method
    }
}
