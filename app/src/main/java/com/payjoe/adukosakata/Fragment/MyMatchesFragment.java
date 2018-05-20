package com.payjoe.adukosakata.Fragment;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.payjoe.adukosakata.Models.MyMatches;
import com.payjoe.adukosakata.Models.MyMatchesFragmentModels;
import com.payjoe.adukosakata.Models.UserAnswersFirebase;
import com.payjoe.adukosakata.MyMatchesAdapter;
import com.payjoe.adukosakata.PostGameFirebaseHolder;
import com.payjoe.adukosakata.PostGameFirebaseRecycler;
import com.payjoe.adukosakata.R;
import com.payjoe.adukosakata.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Payjoe on 11/16/2017.
 */

public class MyMatchesFragment extends Fragment{

    //Set View
    private RecyclerView recyclerView;
    private MyMatchesAdapter mMyMatchesAdapter;

    //Variable yang dibutuhkan
    public String userUserID;
    public String matchID;

    //Inisiasi Firebase
    private DatabaseReference mDatabase;
    private DatabaseReference mMatches;
    public FirebaseAuth mFirebaseAuth;
    Query mMatchIDRef;
    public int matchesSize;
    ChildEventListener childEventListener;

    //Variabel sementara untuk userMatchesList
    public String currentEnemyID;
    public String currentEnemyUsername;
    public int currentEnemyScore;
    public String currentUserID;
    public String currentUserUsername;
    public int currentUserScore;

    //Array User Matches
    public ArrayList<MyMatchesFragmentModels> userMatchesList = new ArrayList<>();

    public MyMatchesFragment(){
        // Untuk Constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    // menampilkan fragment_mymatches
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate layout ke fragment ini
        View rootView =  inflater.inflate(R.layout.fragment_mymatches, container, false);

        // Set RecyclerView
        recyclerView = (RecyclerView) rootView.findViewById(R.id.myMatchesRecycler_view);

        // Set LinearLayoutManager config
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //Set Path Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userUserID = mFirebaseAuth.getInstance().getCurrentUser().getUid();
        mMatches = mDatabase.child("user-matches").child(userUserID);
        mMatchIDRef = mMatches;

        //Menghitung jumlah matches
        mMatches.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String stringMatchesSize = String.valueOf(dataSnapshot.getChildrenCount());
                matchesSize = Integer.valueOf(stringMatchesSize);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Path Listener
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                matchID = String.valueOf(dataSnapshot.child("matchId").getValue());
                DatabaseReference matchesRef = mDatabase.child("matches").child(matchID);
                matchesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.v("MyMatchesFragment ", "These Are user Match ID: " + dataSnapshot.getKey());
                        String currentMatchID = dataSnapshot.getKey();
                        DataSnapshot listOfPlayers = dataSnapshot.child("listOfPlayers");
                        for(DataSnapshot child: listOfPlayers.getChildren()){
                            if(!child.getKey().equals(userUserID) && !child.getKey().equals("numberPlayer")){
                                Log.v("MyMatchesFragment", "Enemy ID in current Match ID: " + child.getKey());
                                currentEnemyID = child.getKey();
                                Log.v("MyMatchesFragment", "Enemy Username in current Match ID: " + child.child("username").getValue());
                                currentEnemyUsername = String.valueOf(child.child("username").getValue());
                                Log.v("MyMatchesFragment", "Enemy Score in current Match ID: " + child.child("score").getValue());
                                String stringCurrentEnemyScore = String.valueOf(child.child("score").getValue());
                                currentEnemyScore = Integer.valueOf(stringCurrentEnemyScore);
                            }else if(child.getKey().equals(userUserID)){
                                Log.v("MyMatchesFragment", "User ID in current Match ID: " + child.getKey());
                                currentUserID = child.getKey();
                                Log.v("MyMatchesFragment", "User Username in current Match ID: " + child.child("username").getValue());
                                currentUserUsername = String.valueOf(child.child("username").getValue());
                                Log.v("MyMatchesFragment", "User Score in current Match ID: " + child.child("score").getValue());
                                String stringCurrentUserScore = String.valueOf(child.child("score").getValue());
                                Integer intCurrentUserScore = Integer.valueOf(stringCurrentUserScore);
                                currentUserScore = Integer.valueOf(stringCurrentUserScore);
                            }
                        }
                        MyMatchesFragmentModels myMatchesFragmentModels =
                                new MyMatchesFragmentModels
                                        (currentMatchID, currentEnemyID, currentEnemyUsername, currentEnemyScore, currentUserID, currentUserUsername, currentUserScore);
                        userMatchesList.add(myMatchesFragmentModels);
                        Log.v("MyMatchesFragment", "These are Matches Info: Succeed");
                        Log.v("MyMatchesFragment", "These are Array of UserMatchesList Size: " + userMatchesList.size());
                        Log.v("MyMatchesFragment", "These are Array of Matches Size: " + matchesSize);
                        if(userMatchesList.size() == matchesSize){
                            initAdapter();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        //Set Listener untuk mMatchIDRef -> ChildEvenetListener
        mMatchIDRef.addChildEventListener(childEventListener);

        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        FirebaseDatabase.getInstance().getReference().child("user-matches").keepSynced(true);
        Log.v("MyMatchesFragment", "This is OnStart Execution");
        initTouchListener();
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mMatchIDRef.removeEventListener(childEventListener);
        userMatchesList.clear();
        mMyMatchesAdapter.notifyDataSetChanged();
        mDatabase.child("user-matches").child(userUserID).keepSynced(false);
    }

    public void initAdapter(){
        mMyMatchesAdapter = new MyMatchesAdapter(getActivity().getApplicationContext(), userMatchesList);
        recyclerView.setAdapter(mMyMatchesAdapter);
        Log.v("MyMatchesFragment", "Initation MyMatchesAdapter done.");
    }

    public void initTouchListener(){
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                MyMatchesFragmentModels myMatchesFragmentModels = userMatchesList.get(position);
                //Toast.makeText(getActivity().getApplicationContext(), "Current Match ID: " + myMatchesFragmentModels.getMatchID(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity().getApplicationContext(), PostGameFirebaseRecycler.class);
                intent.putExtra("matchID", String.valueOf(myMatchesFragmentModels.getMatchID()));
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        Log.v("MyMatchesFragment", "Initation Touch Listener done.");
    }
}
