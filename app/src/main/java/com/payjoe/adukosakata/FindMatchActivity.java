package com.payjoe.adukosakata;

import android.content.Intent;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.payjoe.adukosakata.Models.User;
import com.payjoe.adukosakata.Models.UserInfoMatch;

import org.w3c.dom.Text;

public class FindMatchActivity extends AppCompatActivity implements View.OnClickListener {
    //Inisialisasi View
    private ProgressBar progressBar;
    private Button cancelButton;

    //User View
    private TextView userUsernameTextView;
    private TextView userRankTextView;

    //Enemy View
    private TextView enemyUsernameTextView;
    private TextView enemyRankTextView;
    private TextView enemyTextRankTextView;

    //Inisialisasi Variabel User
    public String userUsername;
    public int userRank;
    public String userUserID;

    //Inisialisasi Variabel Enemy
    public String enemyUsername;
    public int enemyRank;
    public String enemyUserID;
    Query enemyUserIDRef;
    ValueEventListener enemyUserIDRefValueEventListener;

    public String findMatchID;

    //Inisialisasi Database Reference
    private DatabaseReference mDatabase;
    private DatabaseReference mFindMatchRef;
    public FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_match);

        getSupportActionBar().hide();
        //Set Database Reference
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFindMatchRef = mDatabase.child("find-match");

        //Set View
        cancelButton = (Button)findViewById(R.id.cancel_button);
        progressBar = (ProgressBar)findViewById(R.id.progressBarFindMatch);

        //Set User View
        userUsernameTextView = (TextView)findViewById(R.id.user_name);
        userRankTextView = (TextView)findViewById(R.id.user_rank_point);

        //Set Enemy View
        enemyUsernameTextView = (TextView) findViewById(R.id.enemy_name);
        enemyRankTextView = (TextView)findViewById(R.id.enemy_rank_point);
        enemyTextRankTextView = (TextView)findViewById(R.id.enemy_rank_text);

        //Get User username & rank dari Home Fragment
        Bundle intent = getIntent().getExtras();
        userUsername = intent.getString("userUsername");
        userRank = intent.getInt("userRank");
        findMatchID = intent.getString("findMatchID");

        //Set UserID milik User melalui FirebaseAuth
        userUserID = mFirebaseAuth.getInstance().getCurrentUser().getUid();

        //Set Progressbar to Visible
        progressBar.setVisibility(View.VISIBLE);

        //Set Value User View
        userUsernameTextView.setText(userUsername);
        userRankTextView.setText(String.valueOf(userRank));
        //Masuk ke InGame Activity dan Mengirimkan data Username dan Rank dari User dan Enemy
        //enterInGame(userUsername, userRank, enemyUsername, enemyRank);
        cancelButton.setOnClickListener(this);
    }
    @Override
    public void onStart(){
        super.onStart();
        //Mengambil data Enemy kemudian di-storekan ke Variabel Enemy
        getEnemyUserID();
    }

    @Override
    public void onStop(){
        super.onStop();
        enemyUserIDRef.removeEventListener(enemyUserIDRefValueEventListener);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.cancel_button:
                removeUserFromFindMatch();
                startActivity(new Intent(FindMatchActivity.this, MainActivity.class));
                finish();

            default:
                return;
        }
    }

    @Override
    public void onBackPressed(){

    }

    private void getFindMatchID(){
        Query mFindMatchIDRef = mDatabase.child("find-match");
    }

    private void removeUserFromFindMatch(){
        final String userId = getUid();
        mDatabase.child("find-match").child(findMatchID).child("numberPlayer").removeValue();
        mDatabase.child("find-match").child(findMatchID).child(userId).removeValue();
    }

    //Mengambil UserID milik user melalui FirebaseAuth
    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    /**
     * Mengambil value UserID musuh dari node find-match kemudian di-storekan pada variabel
     * @enemyUserID
     */
    public void getEnemyUserID(){
        enemyUserIDRef = mDatabase.child("find-match").child(findMatchID);
        enemyUserIDRefValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() == 3){
                    for(DataSnapshot child: dataSnapshot.getChildren()){
                        Log.v("FindMatchActivity", "This is the children of find match : " + child.getKey());
                        if(!child.getKey().equals(userUserID) && !child.getKey().equals("numberPlayer")){
                            enemyUserID = child.getKey();
                            Log.v("FindMatchActivity", "This is the Enemy of find match : " + enemyUserID);
                            getEnemyData();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }else{
                    for(DataSnapshot child: dataSnapshot.getChildren()){
                        Log.v("FindMatchActivity", "This is the children of find match : " + child.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("FindMatchActivity", "Something Wrong Happened.", databaseError.toException());
                Toast.makeText(FindMatchActivity.this, "Something Wrong Happened.", Toast.LENGTH_SHORT).show();
            }
        };
        enemyUserIDRef.addValueEventListener(enemyUserIDRefValueEventListener);
    }

    /**
     * Mengakses node enemyID dalam node find-match kemudian,
     * Men-storekan identitas Musuh dari hasil getEnemyUserID, variabel yang diambil meliputi
     * username Musuh dan rank Musuh
     */
    public void getEnemyData(){
        mDatabase.child("users").child(enemyUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User enemyInfoMatch = dataSnapshot.getValue(User.class);

                //Men-storekan value enemy Username dan enemy Rank dari node find-match
                enemyUsername = enemyInfoMatch.username;
                enemyRank = enemyInfoMatch.rank;

                //Set Value Enemy View
                enemyUsernameTextView.setText(enemyUsername);
                enemyRankTextView.setText(String.valueOf(enemyRank));

                //Set the visibility
                enemyUsernameTextView.setVisibility(View.VISIBLE);
                enemyRankTextView.setVisibility(View.VISIBLE);
                enemyTextRankTextView.setVisibility(View.VISIBLE);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Masuk InGameActivity setelah delay 3 detik
                        enterInGame(userUsername, userRank, enemyUsername, enemyRank, enemyUserID);
                    }
                }, 3500);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    /**
     * Masuk ke InGameActivity dengan mengirimkan variabel berikut :
     * @param userUsername username milik user
     * @param userRank rank milik user
     * @param enemyUsername username milik musuh
     * @param enemyRank rank milik musuh
     * @param enemyUserID userID milik musuh
     */
    public void enterInGame(String userUsername, int userRank, String enemyUsername, int enemyRank, String enemyUserID){
        Intent intent = new Intent(this, InGameActivity.class);
        intent.putExtra("userUsername", userUsername);
        intent.putExtra("userRank", userRank);
        intent.putExtra("enemyUsername", enemyUsername);
        intent.putExtra("enemyRank", enemyRank);
        intent.putExtra("enemyUserID", enemyUserID);
        startActivity(intent);
        finish();
    }
}
