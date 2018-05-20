package com.payjoe.adukosakata;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.payjoe.adukosakata.Models.UserAnswersFirebase;

/**
 * Created by Payjoe on 2/26/2018.
 */

public class PostGameFirebaseHolder extends RecyclerView.ViewHolder{
    public TextView questionForAnswer;
    public TextView userAnswer;

    public PostGameFirebaseHolder(View itemView){
        super(itemView);
        questionForAnswer = (TextView)itemView.findViewById(R.id.questionForAnswers);
        userAnswer = (TextView)itemView.findViewById(R.id.userAnswers);
    }

    public void bindToList(UserAnswersFirebase userAnswersFirebase){
        Log.v("PostGameFirebaseHolder", "Current Question For Answers : " + userAnswersFirebase.questionForAnswers);
        String stringQuestionForAnswers = String.valueOf(userAnswersFirebase.questionForAnswers);
        questionForAnswer.setText(stringQuestionForAnswers);

        Log.v("PostGameFirebaseHolder", "Current Answers : " + userAnswersFirebase.userAnswers);
        String stringUserAnswers = String.valueOf(userAnswersFirebase.userAnswers);
        userAnswer.setText(stringUserAnswers);

        Log.v("PostGameFirebaseHolder", "Current Result Answers : " + userAnswersFirebase.resultAnswers);
        String stringResultAnswers = String.valueOf(userAnswersFirebase.resultAnswers);
        Boolean booleanResultAnswers = Boolean.valueOf(stringResultAnswers);
        if(booleanResultAnswers){
            //Green TextColor
            userAnswer.setTextColor(Color.parseColor("#4CAF50"));
        }else{
            //Red TextColor
            userAnswer.setTextColor(Color.parseColor("#F44336"));
        }
    }
}
