package com.payjoe.adukosakata;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.payjoe.adukosakata.Models.UserAnswersFirebase;
import com.payjoe.adukosakata.Models.UserAnswersModels;
import com.payjoe.adukosakata.Models.UserAnswersTraining;

import java.util.ArrayList;

/**
 * Created by Payjoe on 3/19/2018.
 */

public class PostTrainingAdapter extends RecyclerView.Adapter<PostTrainingAdapter.MyViewHolder> {
    private ArrayList<UserAnswersTraining> userAnswersTrainingsList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView questionForAnswers;
        public TextView userAnswers;
        public TextView questionCounter;

        public MyViewHolder(View view){
            super(view);
            questionForAnswers = (TextView) view.findViewById(R.id.questionForAnswers);
            userAnswers = (TextView) view.findViewById(R.id.userAnswers);
            questionCounter = (TextView) view.findViewById(R.id.question_counter);
        }

    }

    public PostTrainingAdapter(ArrayList<UserAnswersTraining> userAnswersTrainingsList){
        this.userAnswersTrainingsList = userAnswersTrainingsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.posttraining_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        UserAnswersTraining userAnswersTraining = userAnswersTrainingsList.get(position);
        holder.questionCounter.setText(String.valueOf(userAnswersTraining.getIndex()));
        holder.questionForAnswers.setText(userAnswersTraining.getQuestionForAnswers());
        holder.userAnswers.setText(userAnswersTraining.getUserAnswers());
        if(userAnswersTraining.getResultAnswers()){
            //Green Text Color
            holder.userAnswers.setTextColor(Color.parseColor("#4CAF50"));
        }else{
            //Red Text Color
            holder.userAnswers.setTextColor(Color.parseColor("#F44336"));
        }
    }

    @Override
    public int getItemCount() {
        return userAnswersTrainingsList.size();
    }

}
