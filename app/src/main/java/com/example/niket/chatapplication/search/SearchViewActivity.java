package com.example.niket.chatapplication.search;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.niket.chatapplication.R;
import com.example.niket.chatapplication.SignInActivity;
import com.example.niket.chatapplication.pojoClass.SignupModelClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchViewActivity extends AppCompatActivity {
    String senderId;
    LinearLayout linearLayout;

    ImageView back, option;

    EditText editTextSearch;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    SearchViewAdapter searchViewAdapter;

    // vertical recyclerview
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    ArrayList<SignupModelClass> SignupModelClassArrayList;
    Query query;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);


        sharedPreferences = getSharedPreferences("save", 0);
        senderId = sharedPreferences.getString("senderID", null);


        editTextSearch = findViewById(R.id.searchEdittext);
        linearLayout = findViewById(R.id.linearSearch);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        option = findViewById(R.id.option);
        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showTwoButtonDialog(SearchViewActivity.this);

            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("data").child("users");


        recyclerView = findViewById(R.id.recyclerview_search);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);


        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!charSequence.toString().equalsIgnoreCase("")) {
                    query = firebaseDatabase.getReference("data").child("users")
                            .orderByChild("username").startAt(charSequence.toString().toLowerCase().trim()).endAt(charSequence.toString().toLowerCase().trim() + "\uf8ff");

                    query.addValueEventListener(valueEventListener);
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editTextSearch.getText().toString().trim().equalsIgnoreCase("")) {
                    recyclerView.setAdapter(null);
                }

            }
        });
    }


    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            SignupModelClassArrayList = new ArrayList<>();


            for (DataSnapshot data2 : dataSnapshot.getChildren()) {

                SignupModelClass SignupModelClass = new SignupModelClass();
                SignupModelClass.setUsername(data2.child("username").getValue(String.class));
                SignupModelClass.setUserImage(data2.child("userImage").getValue(String.class));
                SignupModelClass.setUserID(data2.child("userID").getValue(String.class));

                SignupModelClassArrayList.add(SignupModelClass);

            }

            searchViewAdapter = new SearchViewAdapter(SearchViewActivity.this, SignupModelClassArrayList, senderId);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setAdapter(searchViewAdapter);
            searchViewAdapter.notifyDataSetChanged();

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }


    public void showTwoButtonDialog(final Activity context) {
        //Typeface custom_font = Typeface.createFromAsset(context.getAssets(), context.getResources().getString(R.string.font_regular));
        final Dialog dialogBox = new Dialog(context,
                android.R.style.Theme_Translucent_NoTitleBar);
        dialogBox.setContentView(R.layout.popuopwindow);

        RelativeLayout outer = dialogBox.findViewById(R.id.outer);
        TextView detailhead = (TextView) dialogBox.findViewById(R.id.detailhead);

        TextView logout = (TextView) dialogBox
                .findViewById(R.id.logout_menu);


        outer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBox.dismiss();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBox.dismiss();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();

                Intent intent = new Intent(SearchViewActivity.this, SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


            }
        });


        dialogBox.show();
    }

}
