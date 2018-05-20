package com.payjoe.adukosakata;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.payjoe.adukosakata.Models.UserAnswersModels;
import com.payjoe.adukosakata.Models.UserAnswersTraining;

import java.util.ArrayList;

public class PostTrainingActivity extends AppCompatActivity {
    //Inisialisasi View
    private TextView rightAnswerTextView;
    private TextView wrongAnswerTextView;
    private TextView blankAnswerTextView;
    private TextView questionCounterTextView;
    private RecyclerView postTrainingRecyclerView;
    public AppCompatButton exitButton;

    private PostTrainingAdapter mUserAnswersTrainingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_post_training);

        //Set View
        rightAnswerTextView = (TextView) findViewById(R.id.right_answer);
        wrongAnswerTextView = (TextView) findViewById(R.id.wrong_answer);
        blankAnswerTextView = (TextView) findViewById(R.id.blank_answer);
        questionCounterTextView = (TextView) findViewById(R.id.question_counter);
        postTrainingRecyclerView = (RecyclerView) findViewById(R.id.postTrainingRecycler_view);
        exitButton = (AppCompatButton) findViewById(R.id.exit_postTraining);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setExitButton();
            }
        });

        Intent intent = getIntent();
        ArrayList<UserAnswersTraining> listOfUserAnswers = intent.getParcelableArrayListExtra("userAnswersList");
        int rightAnswerScore = intent.getIntExtra("rightAnswerScore", 0);
        int wrongAnswerScore = intent.getIntExtra("wrongAnswerScore", 0);
        int blankAnswerScore = intent.getIntExtra("blankAnswerScore", 0);
        int questionCounter = intent.getIntExtra("counter", 0);

        rightAnswerTextView.setText(String.valueOf(rightAnswerScore));
        wrongAnswerTextView.setText(String.valueOf(wrongAnswerScore));
        blankAnswerTextView.setText(String.valueOf(blankAnswerScore));
        questionCounterTextView.setText(String.valueOf(questionCounter));

        //Set RecyclerView
        postTrainingRecyclerView = (RecyclerView) findViewById(R.id.postTrainingRecycler_view);
        mUserAnswersTrainingAdapter = new PostTrainingAdapter(listOfUserAnswers);
        postTrainingRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManagerUserAnswers = new LinearLayoutManager(getApplicationContext());
        postTrainingRecyclerView.setLayoutManager(mLayoutManagerUserAnswers);
        postTrainingRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
        postTrainingRecyclerView.setItemAnimator(new DefaultItemAnimator());
        postTrainingRecyclerView.setAdapter(mUserAnswersTrainingAdapter);
    }

    public void setExitButton(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
