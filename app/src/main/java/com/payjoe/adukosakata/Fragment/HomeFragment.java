package com.payjoe.adukosakata.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.payjoe.adukosakata.FindMatchActivity;
import com.payjoe.adukosakata.MainActivity;
import com.payjoe.adukosakata.Models.User;
import com.payjoe.adukosakata.Models.UserInfoMatch;
import com.payjoe.adukosakata.R;
import com.payjoe.adukosakata.TrainingActivity;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Payjoe on 11/16/2017.
 */

public class HomeFragment extends Fragment implements View.OnClickListener{
    //Inisialisasi View
    public TextView usernameText;
    public TextView userRankText;
    public TextView userWinText;
    public TextView userLossText;
    public TextView userDrawText;
    public TextView userMatchesText;


    //Inisialisasi Firebase
    public FirebaseAuth mFirebaseAuth;
    private DatabaseReference mUsers;
    public DatabaseReference mDatabase;
    public DatabaseReference mFindMatch;

    //Inisialisas User Variable
    public String userUsername;
    public String userUserID;
    public int userMatches;
    public int userWin;
    public int userLoss;
    public int userDraw;
    public int userRank;
    public int userScore;
    public boolean userFinished;

    public String findMatchID;
    public Context context;
    public boolean cantJoin;

    public HomeFragment(){
        // Untuk Constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Inisialisasi database reference
        mUsers = FirebaseDatabase.getInstance().getReference().child("users");
        mUsers.keepSynced(true);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFindMatch = mDatabase.child("find-match");
        mFindMatch.keepSynced(true);

    }

    //menampilkan fragment_home
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        //Mengambil Data user dari database Firebase;
        getUserData();

        // Inflate layout ke fragment ini
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        //Inisialisasi View
        Button playButton = (Button) rootView.findViewById(R.id.play_button);
        Button trainingButton = (Button) rootView.findViewById(R.id.training_button);
        usernameText = (TextView) rootView.findViewById(R.id.user_name);
        userRankText = (TextView) rootView.findViewById(R.id.user_ranking);
        userWinText = (TextView) rootView.findViewById(R.id.user_win);
        userLossText = (TextView) rootView.findViewById(R.id.user_loss);
        userDrawText = (TextView) rootView.findViewById(R.id.user_draw);
        userMatchesText = (TextView) rootView.findViewById(R.id.user_matches);

        //Button Listener
        playButton.setOnClickListener(this);
        trainingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTrainingButton();
            }
        });
        context = getActivity().getApplicationContext();
        return rootView;
    }

    public void findMatch(){
        userUserID = mFirebaseAuth.getInstance().getCurrentUser().getUid();
        mUsers.child(userUserID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        userUsername = user.username;
                        userRank = user.rank;
                        userScore = 0;
                        userFinished = false;
                        slotCheckTransaction();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        Log.v("HomeFragment", "Current User ID : " + userUserID );
    }

    public void slotCheckTransaction(){
        /**mFindMatch.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    firstArriver();
                }
                if(dataSnapshot.hasChildren()){
                    for(DataSnapshot child:dataSnapshot.getChildren()){
                        findMatchID = child.getKey();
                        String stringNumberPlayer = String.valueOf(child.child("numberPlayer").getValue());
                        Integer numberPlayer = Integer.valueOf(stringNumberPlayer);
                        Log.v("HomeFragment", "Current FindMatchID : " + findMatchID);
                        Log.v("HomeFragment", "Current NumberPlayer : " + numberPlayer);
                        if(numberPlayer<2){
                            secondArriver(findMatchID);
                            Log.v("HomeFragment", "Second Arriver MatchID : " + findMatchID);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });**/

        mFindMatch.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if(mutableData.getValue() == null){
                    //first Arriver
                    UserInfoMatch userInfo = new UserInfoMatch(userUserID, userUsername, userRank, userScore, userFinished);
                    DatabaseReference pushRef = mDatabase.child("find-match");

                    String key = pushRef.push().getKey();
                    findMatchID = key;
                    mutableData.child(key).child(userUserID).setValue(userInfo);
                    mutableData.child(key).child("numberPlayer").setValue(1);

                    Log.v("HomeFragment", "Write data to find-match node done.");
                }else{
                    //second Arriver
                    for(MutableData child : mutableData.getChildren()){
                        findMatchID = child.getKey();
                        String stringNumberPlayer = String.valueOf(child.child("numberPlayer").getValue());
                        Integer numberPlayer = Integer.valueOf(stringNumberPlayer);
                        Log.v("HomeFragment", "Current FindMatchID : " + findMatchID);
                        Log.v("HomeFragment", "Current NumberPlayer : " + numberPlayer);
                        if(numberPlayer<2){
                            secondArriver(findMatchID);
                            Log.v("HomeFragment", "Second Arriver MatchID : " + findMatchID);
                            return Transaction.abort();
                        }else{
                            cantJoin = true;
                        }
                    }
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if(b){
                    if(cantJoin){
                        Toast.makeText(getActivity().getApplicationContext(), "Can't Join Check", Toast.LENGTH_SHORT).show();
                    }else{
                        Intent intent = new Intent(getActivity(), FindMatchActivity.class);
                        intent.putExtra("findMatchID", findMatchID);
                        intent.putExtra("userUsername", userUsername);
                        intent.putExtra("userRank", userRank);

                        Log.v(String.valueOf(this), "This is userUsername [Home] : " + userUsername);
                        Log.v(String.valueOf(this), "This is userRank [Home] : " + userRank);

                        startActivity(intent);
                    }

                }
            }
        });
    }

    public void firstArriver(){
        mFindMatch.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                UserInfoMatch userInfo = new UserInfoMatch(userUserID, userUsername, userRank, userScore, userFinished);
                DatabaseReference pushRef = mDatabase.child("find-match");

                String key = pushRef.push().getKey();
                findMatchID = key;
                mutableData.child(key).child(userUserID).setValue(userInfo);
                mutableData.child(key).child("numberPlayer").setValue(1);

                Log.v("HomFragment", "Write data to find-match node done.");

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if(b){
                    Intent intent = new Intent(getActivity(), FindMatchActivity.class);
                    intent.putExtra("findMatchID", findMatchID);
                    intent.putExtra("userUsername", userUsername);
                    intent.putExtra("userRank", userRank);

                    Log.v(String.valueOf(this), "This is userUsername [Home] : " + userUsername);
                    Log.v(String.valueOf(this), "This is userRank [Home] : " + userRank);

                    startActivity(intent);
                }
            }
        });

    }

    public void secondArriver(final String findMatchID){
        mFindMatch.child(findMatchID).child("numberPlayer").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if(mutableData.getValue()==null){
                    return Transaction.success(mutableData);
                }
                String stringPlayer = String.valueOf(mutableData.getValue());
                Integer player = Integer.valueOf(stringPlayer);
                Log.v("HomeFragment", "NumberPlayer: "+ player);
                Integer playerUpdate = player +1;
                mutableData.setValue(playerUpdate);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                Log.v("HomeFragment", "Error: " + databaseError);
                Log.v("HomeFragment", "Committed: " + b);
                Log.v("HomeFragment", "Current Data: " + dataSnapshot);
                if(b){
                    mFindMatch.child(findMatchID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.v("HomeFragment", "Current Data try: " + dataSnapshot);
                            String stringPlayer = String.valueOf(dataSnapshot.child("numberPlayer").getValue());
                            Integer player = Integer.valueOf(stringPlayer);
                            if(player<3){
                                UserInfoMatch userInfo = new UserInfoMatch(userUserID, userUsername, userRank, userScore, userFinished);
                                mFindMatch.child(findMatchID).child(userUserID).setValue(userInfo);
                                toFindMatchActivity(userUsername, userRank, findMatchID);
                                Log.v("HomeFragment", "Slot Open, Match ID :" + findMatchID);
                                Log.v("HomeFragment", "Current Mutable Data [2]:" + dataSnapshot);
                            }else{
                                Toast.makeText(getActivity().getApplicationContext(), "Can't Join", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

    public void createRoom(MutableData mutableData){
        UserInfoMatch userInfo = new UserInfoMatch(userUserID, userUsername, userRank, userScore, userFinished);
        DatabaseReference pushRef = mDatabase.child("find-match");

        String key = pushRef.push().getKey();
        findMatchID = key;
        mutableData.child(key).child(userUserID).setValue(userInfo);
        mutableData.child(key).child("numberPlayer").setValue(1);

        Log.v("HomFragment", "Write data to find-match node done.");

    }

    /**
     * Masuk ke FindMatchAcitivty
     * @param userUsername nama user
     * @param userRank ranking user
     * @param key findmatchID
     */
    public void toFindMatchActivity(String userUsername, int userRank, String key){
        Intent intent = new Intent(getActivity(), FindMatchActivity.class);
        intent.putExtra("userUsername", userUsername);
        intent.putExtra("userRank", userRank);
        intent.putExtra("findMatchID", key);
        startActivity(intent);
        //getActivity().finish();
    }


    @Override
    public void onClick(View view) {
        findMatch();
    }


    public void setTrainingButton(){
        Log.v("HomeFragment", "Clicked Training Button");
        Intent intent = new Intent(getActivity(), TrainingActivity.class);
        startActivity(intent);
    }

    public void getUserData(){
        final String userId = mFirebaseAuth.getInstance().getCurrentUser().getUid();
        mUsers.child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        //Get data user dari database Firebase
                        userUsername = user.username;
                        userMatches = user.matches;
                        userWin = user.win;
                        userLoss = user.loss;
                        userRank = user.rank;
                        userDraw = user.draw;

                        //Set View
                        usernameText.setText(userUsername);
                        userMatchesText.setText(String.valueOf(userMatches));
                        userWinText.setText(String.valueOf(userWin));
                        userLossText.setText(String.valueOf(userLoss));
                        userDrawText.setText(String.valueOf(userDraw));
                        userRankText.setText(String.valueOf(userRank));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

}

