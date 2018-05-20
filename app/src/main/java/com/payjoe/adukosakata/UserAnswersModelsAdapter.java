package com.payjoe.adukosakata;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.payjoe.adukosakata.Models.User;
import com.payjoe.adukosakata.Models.UserAnswersModels;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Payjoe on 2/6/2018.
 */

public class UserAnswersModelsAdapter extends RecyclerView.Adapter<UserAnswersModelsAdapter.MyViewHolder>{

    private ArrayList<UserAnswersModels> userAnswersModelsList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView questionForAnswers;
        public TextView userAnswers;

        public MyViewHolder(View view){
            super(view);
            questionForAnswers = (TextView) view.findViewById(R.id.questionForAnswers);
            userAnswers = (TextView) view.findViewById(R.id.userAnswers);
        }
    }

    public UserAnswersModelsAdapter(ArrayList<UserAnswersModels> userAnswersModelsList){
        this.userAnswersModelsList = userAnswersModelsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.postgame_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        UserAnswersModels userAnswersModels = userAnswersModelsList.get(position);
        holder.questionForAnswers.setText(userAnswersModels.getQuestionForAnswers());
        holder.userAnswers.setText(userAnswersModels.getUserAnswers());
        if(userAnswersModels.getResultAnswers()){
            //Green Text Color
            holder.userAnswers.setTextColor(Color.parseColor("#4CAF50"));
        }else{
            //Red Text Color
            holder.userAnswers.setTextColor(Color.parseColor("#F44336"));
        }
    }

    @Override
    public int getItemCount() {
        return userAnswersModelsList.size();
    }
}
