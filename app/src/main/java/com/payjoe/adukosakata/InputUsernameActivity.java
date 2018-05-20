package com.payjoe.adukosakata;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.payjoe.adukosakata.Models.Ranks;
import com.payjoe.adukosakata.Models.User;

public class InputUsernameActivity extends AppCompatActivity {
    //inisialisasi variabel
    private static final String REQUIRED = "required";

    //Inisialisasi view
    private Button okButton;
    private EditText usernameField;
    private ProgressBar progressBar;

    //Inisialisasi Firebase
    public FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_username);
        getSupportActionBar().hide();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        usernameField = (EditText)findViewById(R.id.username_field);
        okButton = (Button)findViewById(R.id.ok_button);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUser();
            }
        });
    }

    public void addUser(){
        final String username = usernameField.getText().toString();
        //username harus dimasukkan
        if(TextUtils.isEmpty(username)){
            usernameField.setError(REQUIRED);
            return;
        }

        final String userId = mFirebaseAuth.getInstance().getCurrentUser().getUid();
        final String email = mFirebaseAuth.getInstance().getCurrentUser().getEmail();
        final int matches = 0;
        final int win = 0;
        final int loss = 0;
        final int rank = 0;
        final int draw = 0;

        //input pada node "users" dan node "ranks"
        writeNewUser(username, userId, matches, email, win, loss, rank, draw);
        writeRankUser(username, rank, userId);
        //intent ke MainActivity
        startActivity(new Intent(InputUsernameActivity.this, MainActivity.class));
        finish();
    }

    public void writeNewUser(String username, String userId, int matches, String email, int win, int loss, int rank, int draw){
        User user = new User(username, email, matches, win, loss, rank, draw);
        mDatabase.child("users").child(userId).setValue(user);
    }

    public void writeRankUser(String username, int rank, String userID){
        Ranks userRank = new Ranks(username, rank, userID);
        mDatabase.child("ranks").child(userID).setValue(userRank);
    }
}
