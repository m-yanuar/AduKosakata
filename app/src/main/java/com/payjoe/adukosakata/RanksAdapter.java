package com.payjoe.adukosakata;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.payjoe.adukosakata.Models.Ranks;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Payjoe on 1/30/2018.
 */

public class RanksAdapter extends RecyclerView.Adapter<RanksAdapter.MyViewHolder>{

    private List<Ranks> ranksList;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView userName;
        public TextView ranksValue;

        public MyViewHolder(View view){
            super(view);
            userName = (TextView) view.findViewById(R.id.user_rank_name);
            ranksValue = (TextView) view.findViewById(R.id.user_rank_value);

        }
    }

    public RanksAdapter(List<Ranks> ranksList){this.ranksList = ranksList;}

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ranks_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Ranks ranks = ranksList.get(position);
        holder.ranksValue.setText(String.valueOf(ranks.getRank()));
        holder.userName.setText(ranks.getUsername());
    }

    @Override
    public int getItemCount() {
        return ranksList.size();
    }
}
