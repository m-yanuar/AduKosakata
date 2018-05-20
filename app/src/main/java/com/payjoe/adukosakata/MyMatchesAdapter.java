package com.payjoe.adukosakata;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.payjoe.adukosakata.Models.MyMatches;
import com.payjoe.adukosakata.Models.MyMatchesFragmentModels;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Payjoe on 1/30/2018.
 */

public class MyMatchesAdapter extends RecyclerView.Adapter<MyMatchesAdapter.MyViewHolder>{

    private Context mContext;
    private ArrayList<MyMatchesFragmentModels> userMatchesList;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView userNameEnemy;
        public TextView result;

        public MyViewHolder(View view){
            super(view);
            userNameEnemy = (TextView) view.findViewById(R.id.usernameEnemy);
            result = (TextView) view.findViewById(R.id.result);
        }
    }

    public MyMatchesAdapter(Context mContext, ArrayList<MyMatchesFragmentModels> userMatchesList){
        this.mContext = mContext;
        this.userMatchesList = userMatchesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mymatches_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MyMatchesFragmentModels myMatchesFragmentModels = userMatchesList.get(position);
        holder.userNameEnemy.setText(myMatchesFragmentModels.getEnemyUsername());
        if(myMatchesFragmentModels.getUserScore() > myMatchesFragmentModels.getEnemyScore()){
            holder.result.setText("WIN");
            //Green Text Color
            holder.result.setTextColor(Color.parseColor("#4CAF50"));
        }else if(myMatchesFragmentModels.getUserScore() == myMatchesFragmentModels.getEnemyScore()){
            holder.result.setText("DRAW");
            //Dark Grey Text Color
            holder.result.setTextColor(Color.parseColor("#222222"));
        }else{
            holder.result.setText("LOSE");
            //Red TextColor
            holder.result.setTextColor(Color.parseColor("#F44336"));
        }

    }

    @Override
    public int getItemCount() {
        return userMatchesList.size();
    }
}
