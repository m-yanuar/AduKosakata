package com.payjoe.adukosakata;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.payjoe.adukosakata.Fragment.RanksFragment;
import com.payjoe.adukosakata.Models.User;

public class DetailUserActivity extends AppCompatActivity {
    //Inisialisasi View
    public TextView usernameText;
    public TextView userRankText;
    public TextView userWinText;
    public TextView userLossText;
    public TextView userMatchesText;
    public TextView userDrawText;
    public AppCompatButton backButton;

    //Inisialisasi Firebase
    public FirebaseAuth mFirebaseAuth;
    private DatabaseReference mUsers;
    public DatabaseReference mDatabase;

    //Inisialisas User Variable
    public String userUserID;
    public String userUsername;
    public int userMatches;
    public int userWin;
    public int userLoss;
    public int userRank;
    public int userDraw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_user);
        getSupportActionBar().hide();

        mUsers = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabase = FirebaseDatabase.getInstance().getReference();

        usernameText = (TextView) findViewById(R.id.user_name);
        userRankText = (TextView) findViewById(R.id.user_ranking);
        userWinText = (TextView) findViewById(R.id.user_win);
        userLossText = (TextView) findViewById(R.id.user_loss);
        userDrawText = (TextView) findViewById(R.id.user_draw);
        userMatchesText = (TextView) findViewById(R.id.user_matches);
        backButton = (AppCompatButton) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBackButton();
            }
        });

        getUserID();
    }

    public void getUserID(){
        Bundle intent = getIntent().getExtras();
        userUserID = intent.getString("userUserID");
        Log.v("DetailUserActivity", "Current User UserID: " + userUserID);
        getUserInfo();
    }

    public void getUserInfo(){
        Query mUserInfo = mUsers.child(userUserID);
        mUserInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userUsername = user.username;
                userMatches = user.matches;
                userWin = user.win;
                userLoss = user.loss;
                userRank = user.rank;
                attachUserInfo();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });
    }

    public void attachUserInfo(){
        usernameText.setText(userUsername);
        userRankText.setText(String.valueOf(userRank));
        userWinText.setText(String.valueOf(userWin));
        userLossText.setText(String.valueOf(userLoss));
        userDrawText.setText(String.valueOf(userDraw));
        userMatchesText.setText(String.valueOf(userMatches));
    }

    public void setBackButton(){
       // Intent intent = new Intent(this, MainActivity.class);
        //startActivity(intent);
        Log.v("DetailUserActivity", "getbackStackEntryCount : " + getFragmentManager().getBackStackEntryCount());
        if(getFragmentManager().getBackStackEntryCount() > 0){
            getFragmentManager().popBackStackImmediate();
        }
        finish();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        //Intent intent = new Intent(this, MainActivity.class);
        //startActivity(intent);
        Log.v("DetailUserActivity", "getbackStackEntryCount : " + getFragmentManager().getBackStackEntryCount());
        if(getFragmentManager().getBackStackEntryCount() > 0){
            getFragmentManager().popBackStackImmediate();
        }
        finish();
    }
}
