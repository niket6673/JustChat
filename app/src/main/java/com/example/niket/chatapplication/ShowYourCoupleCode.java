package com.example.niket.chatapplication;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.niket.chatapplication.databinding.ActivityConnectYourPartnerBinding;
import com.example.niket.chatapplication.databinding.ActivityShowYourCodeBinding;

public class ShowYourCoupleCode extends AppCompatActivity {


    ActivityShowYourCodeBinding binding;
    String senderID;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_your_code);


        sharedPreferences = getSharedPreferences("save", 0);


        senderID = sharedPreferences.getString("senderID", "");
        binding.coupleCodeTv.setText(senderID);

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Share your code");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, binding.coupleCodeTv.getText().toString());
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
            }
        });


        binding.copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

                if (cm != null)
                    cm.setText(binding.coupleCodeTv.getText().toString());

                Toast.makeText(ShowYourCoupleCode.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
