package com.payjoe.adukosakata;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.payjoe.adukosakata.Models.Ranks;

/**
 * Created by Payjoe on 3/2/2018.
 */

public class RanksHolder extends RecyclerView.ViewHolder {
    public TextView userNameTextView;
    public TextView ranksValueTextView;

    public RanksHolder(View itemView) {
        super(itemView);
        userNameTextView = (TextView) itemView.findViewById(R.id.user_rank_name);
        ranksValueTextView = (TextView) itemView.findViewById(R.id.user_rank_value);
    }

    public void bindToList(Ranks ranks){
        Log.v("RanksHolder", "Current Username: " + ranks.username);
        String stringUsername = String.valueOf(ranks.username);
        userNameTextView.setText(stringUsername);

        Log.v("RanksHolder", "Current Rank: " + ranks.rank);
        String stringRank = String.valueOf(ranks.rank);
        ranksValueTextView.setText(stringRank);
    }
}
