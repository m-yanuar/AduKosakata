package com.payjoe.adukosakata;

import android.content.Intent;
import android.provider.ContactsContract;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.payjoe.adukosakata.Models.User;
import com.payjoe.adukosakata.Models.UserAnswersFirebase;
import com.payjoe.adukosakata.Models.UserAnswersModels;
import com.payjoe.adukosakata.Models.UserInfoMatch;

public class PostGameFirebaseRecycler extends AppCompatActivity {
    //Inisiasi View
    private RecyclerView userAnswerFirebaseRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private LinearLayout winLoseLinearLayout;
    private TextView winLoseTextView;
    public AppCompatButton exitButton;

    //Inisiasi firebase
    private DatabaseReference mDatabase;
    private DatabaseReference mUserAnswers;
    private FirebaseRecyclerAdapter<UserAnswersFirebase, PostGameFirebaseHolder> mAdapter;
    private DatabaseReference mFindMatchRef;
    public FirebaseAuth mFirebaseAuth;

    //Variabel untuk mengakses match yang telah dimainkan user
    public String matchID;
    public DatabaseReference mRefUsers;

    //Variabel User
    public String userUserID;
    public String userUsername; //path users
    public String userScore; //path matches/matchid/

    //Variabel Enemy
    public String enemyUserID;
    public String enemyUsername;
    public String enemyScore;
    Query enemyInfoRef;
    public String enemyStatus;
    ValueEventListener enemyEventListener;

    //Inisialisasi User & Enemy View
    private TextView userUsernameTextView;
    private TextView enemyUsernameTextView;
    private TextView userScoreTextView;
    private TextView enemyScoreTextView;

    public DatabaseReference mRefUserAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_game_firebase_recycler);

        getSupportActionBar().hide();
        //Set View
        winLoseLinearLayout = (LinearLayout) findViewById(R.id.win_lose_field_color);
        winLoseTextView = (TextView) findViewById(R.id.win_lose_text);
        exitButton = (AppCompatButton) findViewById(R.id.exit_button);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setExitButton();
            }
        });

        //Set user & enemy TextView
        userUsernameTextView = (TextView) findViewById(R.id.user_a);
        enemyUsernameTextView = (TextView) findViewById(R.id.user_b);
        userScoreTextView = (TextView) findViewById(R.id.score_a);
        enemyScoreTextView = (TextView) findViewById(R.id.score_b);

        //Set mDatabase
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRefUsers = mDatabase.child("users");

        //Set RecyclerView
        userAnswerFirebaseRecyclerView = (RecyclerView) findViewById(R.id.userAnswersFirebaseRecycler_view);

        //Set LinearLayoutManager
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        userAnswerFirebaseRecyclerView.setLayoutManager(linearLayoutManager);
        userAnswerFirebaseRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
        userAnswerFirebaseRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    @Override
    public void onStart(){
        super.onStart();
        // Get MatchID

        getMatchID();
    }

    @Override
    public void onStop(){
        super.onStop();
        if(mAdapter != null){
            mAdapter.stopListening();
        }
    }

    public void getMatchID(){
        //Get User UserID
        userUserID = mFirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.v("PostGameFirebase", "This is User UserID: " + userUserID);
        // Get MatchID from InGameActivity
        Bundle intent = getIntent().getExtras();
        matchID = intent.getString("matchID");
        Log.v("PostGameFirebase", "This is Match ID: " + matchID);
        getEnemyUserID(matchID);
    }

    public void getEnemyUserID(String matchID){
        Log.v("PostGameFirebase", "This is Match ID (getEnemyUserID): " + matchID);
        Query mListPlayersRef = mDatabase.child("matches").child(String.valueOf(matchID)).child("listOfPlayers");
        mListPlayersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() == 3){
                    for(DataSnapshot child: dataSnapshot.getChildren()){
                        Log.v("PostGameFirebase", "This is the children of List Players : " + child.getKey());
                        if(!child.getKey().equals(userUserID) && !child.getKey().equals("numberPlayer")){
                            enemyUserID = child.getKey();
                            Log.v("PostGameFirebase", "This is EnemyUserID : " + enemyUserID);
                            // Get user & enemy information
                            getUserInfo();
                        }
                    }
                }else{
                    for(DataSnapshot child: dataSnapshot.getChildren()){
                        Log.v("PostGameFirebase", "This is the children of List Players: " + child.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("PostGameFirebase", "Something Wrong Happened.", databaseError.toException());
                Toast.makeText(PostGameFirebaseRecycler.this, "Something Wrong Happened.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getUserInfo(){
        //User Query
        Query userInfoRef = mDatabase.child("matches").child(matchID).child("listOfPlayers").child(userUserID);
        userInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserInfoMatch userInfoMatch = dataSnapshot.getValue(UserInfoMatch.class);

                userUsername = userInfoMatch.username;
                userUsernameTextView.setText(userUsername);
                userScore = String.valueOf(userInfoMatch.score);
                userScoreTextView.setText(userScore);

                Log.v("PostGameFirebase", "This is the userUsername: " + userUsername);
                Log.v("PostGameFirebase", "This is the userScore: " + userScore);

                getEnemyInfo();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getEnemyInfo(){
        //Enemy Query
        enemyInfoRef = mDatabase.child("matches").child(matchID).child("listOfPlayers").child(enemyUserID);
        enemyEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserInfoMatch userInfoMatch = dataSnapshot.getValue(UserInfoMatch.class);
                if(userInfoMatch.finished){
                    Log.v("PostGameFirebase", "Enemy has done playing: " + userInfoMatch.finished);
                    enemyUsername = userInfoMatch.username;
                    enemyUsernameTextView.setText(enemyUsername);
                    enemyScore = String.valueOf(userInfoMatch.score);
                    enemyScoreTextView.setText(enemyScore);

                    Log.v("PostGameFirebase", "This is the enemyUsername: " + enemyUsername);
                    Log.v("PostGameFirebase", "This is the enemyScore: " + enemyScore);

                    setWinLoseDisplay();
                }else{
                    Log.v("PostGameFirebase", "Enemy has done playing: " + userInfoMatch.finished);
                    winLoseTextView.setText("Waiting Enemy...");
                    winLoseLinearLayout.setBackgroundColor(getResources().getColor(R.color.dark_grey));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        enemyInfoRef.addValueEventListener(enemyEventListener);
    }


    public void setExitButton(){
        if(getFragmentManager().getBackStackEntryCount() > 0){
            getFragmentManager().popBackStackImmediate();
            finish();
        }else{
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            //menghapus listener dari enemy info path
            enemyInfoRef.removeEventListener(enemyEventListener);
            finish();
        }
        Log.v("PostGameFirebase", "Exitting..");
    }

    public void initFirebaseRecycler(){
        //Set userAnswers Path Firebase
        mUserAnswers = mDatabase.child("matches").child(matchID).child("listOfPlayers")
                .child(userUserID).child("userAnswers");

        Query query = mUserAnswers;

        FirebaseRecyclerOptions<UserAnswersFirebase> options =
                new FirebaseRecyclerOptions.Builder<UserAnswersFirebase>()
                        .setQuery(query, UserAnswersFirebase.class)
                        .build();

        mAdapter = new FirebaseRecyclerAdapter<UserAnswersFirebase, PostGameFirebaseHolder>(options) {
            @Override
            public PostGameFirebaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.postgame_list_row, parent, false);
                return new PostGameFirebaseHolder(view);
            }
            @Override
            protected void onBindViewHolder(PostGameFirebaseHolder viewHolder, int position, final UserAnswersFirebase model) {
                final DatabaseReference ListRef = getRef(position);
                final String indexAnswer = ListRef.getKey();

                viewHolder.bindToList(model);
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(PostGameFirebaseRecycler.this, "This Question : " + model.questionForAnswers + "  This userAnswer : " + model.userAnswers, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        userAnswerFirebaseRecyclerView.setAdapter(mAdapter);
        if(mAdapter != null){
            mAdapter.startListening();
        }
    }

    public void setWinLoseDisplay(){
        if(Integer.valueOf(userScore) > Integer.valueOf(enemyScore)){
            winLoseTextView.setText("WIN");
            winLoseLinearLayout.setBackgroundColor(getResources().getColor(R.color.green));
        }else if(userScore.equals(enemyScore)){
            winLoseTextView.setText("DRAW");
            winLoseLinearLayout.setBackgroundColor(getResources().getColor(R.color.button));
        }
        else{
            winLoseTextView.setText("LOSE");
            winLoseLinearLayout.setBackgroundColor(getResources().getColor(R.color.red));
        }
        initFirebaseRecycler();
    }
}
