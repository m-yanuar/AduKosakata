package com.payjoe.adukosakata.Fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.payjoe.adukosakata.DetailUserActivity;
import com.payjoe.adukosakata.Models.Ranks;
import com.payjoe.adukosakata.Models.UserAnswersFirebase;
import com.payjoe.adukosakata.R;
import com.payjoe.adukosakata.RanksAdapter;
import com.payjoe.adukosakata.RanksHolder;
import com.payjoe.adukosakata.RecyclerTouchListener;
import com.payjoe.adukosakata.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Payjoe on 11/16/2017.
 */

public class RanksFragment extends Fragment{
    private List<Ranks> ranksList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RanksAdapter mRanksAdapter;
    private FirebaseRecyclerAdapter<Ranks, RanksHolder> mAdapter;

    //Inisiasi Firebase
    private DatabaseReference mDatabase;
    private DatabaseReference mRanksPath;
    public FirebaseAuth mFirebaseAuth;

    public RanksFragment(){
        // Untuk Constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    //menampilkan fragment_ranks
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate layout ke fragment ini
        View rootView =  inflater.inflate(R.layout.fragment_ranks, container, false);
        //Set RecyclerView
        recyclerView = (RecyclerView) rootView.findViewById(R.id.ranksRecycler_view);
        mRanksAdapter = new RanksAdapter(ranksList);

        //Set LinearLayoutManager config
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity().getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //Set Firebase Path
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRanksPath = mDatabase.child("ranks");
        Query mRanks = mRanksPath;
        mRanks.orderByChild("rank");
        FirebaseRecyclerOptions<Ranks> options =
                new FirebaseRecyclerOptions.Builder<Ranks>()
                        .setQuery(mRanks.orderByChild("rank").limitToLast(10), Ranks.class)
                        .build();

        mAdapter = new FirebaseRecyclerAdapter<Ranks, RanksHolder>(options) {
            @Override
            public RanksHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ranks_list_row, parent, false);
                return new RanksHolder(view);
            }

            @Override
            protected void onBindViewHolder(RanksHolder holder, int position, final Ranks model) {
                holder.bindToList(model);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(getActivity().getApplicationContext(), "Current Username: " + model.username + " And the rank: " + model.rank, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity().getApplicationContext(), DetailUserActivity.class);
                        intent.putExtra("userUserID", model.userid);
                        startActivity(intent);
                    }
                });
            }
        };

        recyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        if(mAdapter != null){
            mAdapter.startListening();
            Log.v("RanksFragment", "mAdapter Start Listening.");
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        if(mAdapter != null){
            mAdapter.stopListening();
            Log.v("RanksFragment", "mAdapter Stop Listening");
        }
    }
}
