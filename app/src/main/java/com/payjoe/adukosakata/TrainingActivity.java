package com.payjoe.adukosakata;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.payjoe.adukosakata.Models.KosakataModels;
import com.payjoe.adukosakata.Models.UserAnswersTraining;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TrainingActivity extends AppCompatActivity {
    //Inisialisasi array integer questionIndex
    public ArrayList<Integer> questionIndex = new ArrayList<Integer>();
    public List<List<Integer>> randomAnswerIndex = new ArrayList<List<Integer>>();

    //Inisialisasi array correctAnswers
    public ArrayList<String> correctAnswersArray = new ArrayList<>();

    //Inisialisasi View
    private TextView rightAnswerTextView;
    private TextView wrongAnswerTextView;
    private TextView blankAnswerTextView;
    private TextView questionCounterTextView;
    private TextView questionTextView;
    private LinearLayout counter_field_LinearLayout;
    private AppCompatButton answerButton_1;
    private AppCompatButton answerButton_2;
    private AppCompatButton answerButton_3;
    private AppCompatButton answerButton_4;
    public AppCompatButton endButton;
    private CardView questionCardView;
    private ProgressBar progressBar;

    //Inisialisasi Database Firebase;
    private DatabaseReference mDatabase;
    private DatabaseReference mMatches;
    private DatabaseReference mKosakata;
    public FirebaseAuth mFirebaseAuth;
    ChildEventListener userMatchesChildEventListener;

    // Penghitung Jumlah Soal
    public int counter = 1;

    //Timer view
    public TextView timerTextView;

    // Timer
    public CountDownTimer timer;
    public int secondsLeft = 0;

    //User Variables
    public int rightAnswerScore = 0;
    public int wrongAnswerScore = 0;
    public int blankAnswerScore = 0;

    // Question & Answer
    public List<UserAnswersTraining> userAnswersList = new ArrayList<>();
    public String correctAnswer;
    public int kategoriSize;
    public List<String> kosakataKategoriSoal = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        getSupportActionBar().hide();
        //Set Database Reference
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mKosakata = mDatabase.child("kosakata");
        mDatabase.child("kategori").keepSynced(true);
        mKosakata.keepSynced(true);

        //Set View
        questionTextView = (TextView) findViewById(R.id.question_text);
        questionCounterTextView = (TextView) findViewById(R.id.question_counter);
        answerButton_1 = (AppCompatButton) findViewById(R.id.answerButton_1);
        answerButton_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAnswerButton_1();
            }
        });
        answerButton_2 = (AppCompatButton) findViewById(R.id.answerButton_2);
        answerButton_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAnswerButton_2();
            }
        });
        answerButton_3 = (AppCompatButton) findViewById(R.id.answerButton_3);
        answerButton_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAnswerButton_3();
            }
        });
        answerButton_4 = (AppCompatButton) findViewById(R.id.answerButton_4);
        answerButton_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAnswerButton_4();
            }
        });
        endButton = (AppCompatButton) findViewById(R.id.end_button);
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEndButton();
            }
        });
        timerTextView = (TextView) findViewById(R.id.timer);
        counter_field_LinearLayout = (LinearLayout)findViewById(R.id.counter_field);
        questionCardView = (CardView) findViewById(R.id.question_cardView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_training);

        //Set score View
        rightAnswerTextView = (TextView) findViewById(R.id.right_answer);
        wrongAnswerTextView = (TextView) findViewById(R.id.wrong_answer);
        blankAnswerTextView = (TextView) findViewById(R.id.blank_answer);

    }
    @Override
    public void onBackPressed(){

    }

    @Override
    public void onStart(){
        super.onStart();
        mDatabase.child("kosakata").keepSynced(true);
        getQuestion();
    }


    public void initView(){
        progressBar.setVisibility(View.GONE);
        questionCardView.setVisibility(View.VISIBLE);
        counter_field_LinearLayout.setVisibility(View.VISIBLE);
        timerTextView.setVisibility(View.VISIBLE);
        answerButton_1.setVisibility(View.VISIBLE);
        answerButton_2.setVisibility(View.VISIBLE);
        answerButton_3.setVisibility(View.VISIBLE);
        answerButton_4.setVisibility(View.VISIBLE);
    }

    public void getQuestion(){
        Random rand = new Random();
        int questionIndex = rand.nextInt(234 - 1) + 1;
        final String stringQuestionIndex = String.valueOf(questionIndex);
        Log.v("TrainigActivity", "Current Question Index: " + stringQuestionIndex);
        mKosakata.child(stringQuestionIndex).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                KosakataModels kosakata = dataSnapshot.getValue(KosakataModels.class);
                //Set value kosakata dari firebase ke questionTextView
                questionTextView.setText(kosakata.ing.toUpperCase());
                getCorrectAnswer(stringQuestionIndex);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getCorrectAnswer(final String stringQuestionIndex){
        mKosakata.child(stringQuestionIndex).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                KosakataModels kosakata = dataSnapshot.getValue(KosakataModels.class);
                correctAnswer = kosakata.indo;
                String kategoriSoal = kosakata.kategori;
                Log.v("TrainingActivity", "Kategori Soal : " + kategoriSoal);
                Log.v("TrainingActivity", "Index Kategori Soal : " + kosakata.index);
                getKategoriSoal(kategoriSoal, stringQuestionIndex);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getKategoriSoal(final String kategoriSoal, final String correctAnswerIndex){
        //mengambil size Kategori
        mDatabase.child("kategori").child(kategoriSoal).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String stringKategoriSize = String.valueOf(dataSnapshot.getValue());
                kategoriSize = Integer.valueOf(stringKategoriSize);
                Log.v("TrainingActivity", "Size Kategori: " + kategoriSize);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //mengambil data kosakata sesuai kategori dimasukkan ke array KategoriSoal
        mKosakata.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child: dataSnapshot.getChildren()){
                    String kosakataID = child.getKey();
                    mKosakata.child(kosakataID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            KosakataModels kosakata = dataSnapshot.getValue(KosakataModels.class);
                            if(kosakata.kategori.equals(kategoriSoal)){
                                kosakataKategoriSoal.add(String.valueOf(kosakata.index));
                                Log.v("TrainingActivity", "Added kosakata : " + kosakata.indo);
                                if(kosakataKategoriSoal.size() == kategoriSize){
                                    //Log.v("TrainingActivity", "KategoriSoal : " + kategoriSoal);
                                    //Log.v("TrainingActivity", "KategoriSoal Value: " + kosakataKategoriSoal);
                                    //Log.v("TrainingActivity", "KategoriSoal Size: " + kosakataKategoriSoal.size());
                                    getRandomAnswers(kosakataKategoriSoal, correctAnswerIndex);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                Log.v("TrainingActivity", "KategoriSoal Size from Firebase: " + kategoriSize);
                Log.v("TrainingActivity", "KategoriSoal Size after Queried: " + kosakataKategoriSoal.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getRandomAnswers(List<String> kosakataKategoriSoal, final String correctAnswerIndex){
        //membuat array berisi 1 jawaban benar dan 3 jawaban salah
        List<String> indexAnswerArray = new ArrayList<>();
        //1 jawaban benar
        indexAnswerArray.add(correctAnswerIndex);
        //3 jawaban salah
        while(indexAnswerArray.size()<4){
            Random rand = new Random();
            int randomnumber = rand.nextInt(kosakataKategoriSoal.size());
            String answerIndex = kosakataKategoriSoal.get(randomnumber);
            if(indexAnswerArray.indexOf(answerIndex) > -1){
                continue;
            }
            indexAnswerArray.add(answerIndex);
        }

        //mengacak urutan value dalam array indexAnswerArray
        Collections.shuffle(indexAnswerArray);
        Log.v("TrainingActivity", "indexAnswerArray : " + indexAnswerArray);
        Log.v("TrainingActivity", "indexAnswerArray Size : " + indexAnswerArray.size());
        setAnswersButton(indexAnswerArray);
    }

    public void setAnswersButton(List<String> indexAnswer){
        String indexAnswer_1 = indexAnswer.get(0);
        String indexAnswer_2 = indexAnswer.get(1);
        String indexAnswer_3 = indexAnswer.get(2);
        String indexAnswer_4 = indexAnswer.get(3);

        Log.v("TrainingActivity", "Index Answer 1: " + indexAnswer_1);
        Log.v("TrainingActivity", "Index Answer 2: " + indexAnswer_2);
        Log.v("TrainingActivity", "Index Answer 3: " + indexAnswer_3);
        Log.v("TrainingActivity", "Index Answer 4: " + indexAnswer_4);

        mKosakata.child(indexAnswer_1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                KosakataModels kosakata = dataSnapshot.getValue(KosakataModels.class);
                answerButton_1.setText(kosakata.indo.toUpperCase());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mKosakata.child(indexAnswer_2).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                KosakataModels kosakata = dataSnapshot.getValue(KosakataModels.class);
                answerButton_2.setText(kosakata.indo.toUpperCase());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mKosakata.child(indexAnswer_3).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                KosakataModels kosakata = dataSnapshot.getValue(KosakataModels.class);
                answerButton_3.setText(kosakata.indo.toUpperCase());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mKosakata.child(indexAnswer_4).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                KosakataModels kosakata = dataSnapshot.getValue(KosakataModels.class);
                answerButton_4.setText(kosakata.indo.toUpperCase());
                if(counter==1){
                    initView();
                }
                startTimer();
                buttonOn();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Method CountDownTimer, untuk memberikan waktu pada user menjawab selama 6 detik
     */
    public void startTimer(){
        timer = new CountDownTimer(6000, 100) {
            @Override
            public void onTick(long l) {
                if(Math.round((float)l / 1000.0f) != secondsLeft){
                    secondsLeft = Math.round((float)l / 1000.0f);
                    Log.v("TrainingActivity", "Timer Started! : " + secondsLeft);
                    timerTextView.setText(String.valueOf(secondsLeft));
                }
            }

            @Override
            public void onFinish() {
                secondsLeft = 0;
                Log.v("TrainingActivity", "Timer Ended!");
                counter = counter + 1;
                userNoAnswer(counter);
            }
        };
        timer.start();
    }

    public void cancelTimer(){
        secondsLeft = 0;
        timer.cancel();
        Log.v("TrainingActivity", "Timer Ended!");
        Log.v("TrainingActivity", "Seconds Left on Question"+ counter + ": "+ secondsLeft);
    }
    public void setAnswerButton_1(){
        buttonOff();
        counter = counter + 1;
        Button clickedButton = answerButton_1;
        //Membersihkan Timer
        cancelTimer();
        checkAnswer(counter, questionTextView.getText().toString(), clickedButton.getText().toString(), clickedButton);
    }

    public void setAnswerButton_2(){
        buttonOff();
        counter = counter + 1;
        Button clickedButton = answerButton_2;
        //Membersihkan Timer
        cancelTimer();
        checkAnswer(counter, questionTextView.getText().toString(), clickedButton.getText().toString(), clickedButton);
    }

    public void setAnswerButton_3(){
        buttonOff();
        counter = counter + 1;
        Button clickedButton = answerButton_3;
        //Membersihkan Timer
        cancelTimer();
        checkAnswer(counter, questionTextView.getText().toString(), clickedButton.getText().toString(), clickedButton);
    }

    public void setAnswerButton_4(){
        buttonOff();
        counter = counter + 1;
        Button clickedButton = answerButton_4;
        //Membersihkan Timer
        cancelTimer();
        checkAnswer(counter, questionTextView.getText().toString(), clickedButton.getText().toString(), clickedButton);
    }

    public void userNoAnswer(final int answersCounter){
        buttonOff();
        //Membersihkan Timer
        cancelTimer();

        int questionCounter = answersCounter - 1;

        String stringCurrentBlankScore = blankAnswerTextView.getText().toString();
        int curentBlankScore = Integer.valueOf(stringCurrentBlankScore);
        blankAnswerScore = curentBlankScore + 1;
        blankAnswerTextView.setText(String.valueOf(blankAnswerScore));

        String questionForAnswers = questionTextView.getText().toString();
        String userAnswersBlank = "";

        String stringButton_1 = answerButton_1.getText().toString();
        String stringButton_2 = answerButton_2.getText().toString();
        String stringButton_3 = answerButton_3.getText().toString();
        String stringButton_4 = answerButton_4.getText().toString();

        if(correctAnswer.toLowerCase().equals(stringButton_1.toLowerCase())){
            answerButton_1.setTextColor(getApplication().getResources().getColor(R.color.green));
        }else{
            answerButton_1.setTextColor(getApplication().getResources().getColor(R.color.red));
        }

        if(correctAnswer.toLowerCase().equals(stringButton_2.toLowerCase())){
            answerButton_2.setTextColor(getApplication().getResources().getColor(R.color.green));
        }else{
            answerButton_2.setTextColor(getApplication().getResources().getColor(R.color.red));
        }

        if(correctAnswer.toLowerCase().equals(stringButton_3.toLowerCase())){
            answerButton_3.setTextColor(getApplication().getResources().getColor(R.color.green));
        }else{
            answerButton_3.setTextColor(getApplication().getResources().getColor(R.color.red));
        }

        if(correctAnswer.toLowerCase().equals(stringButton_4.toLowerCase())){
            answerButton_4.setTextColor(getApplication().getResources().getColor(R.color.green));
        }else{
            answerButton_4.setTextColor(getApplication().getResources().getColor(R.color.red));
        }

        UserAnswersTraining userAnswersTraining = new UserAnswersTraining(questionCounter, questionForAnswers, userAnswersBlank, false);
        userAnswersList.add(userAnswersTraining);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Ubah warna button setelah delay 1500ms
                answerButton_1.setTextColor(getApplication().getResources().getColor(R.color.white));
                answerButton_2.setTextColor(getApplication().getResources().getColor(R.color.white));
                answerButton_3.setTextColor(getApplication().getResources().getColor(R.color.white));
                answerButton_4.setTextColor(getApplication().getResources().getColor(R.color.white));
                nextQuestion(answersCounter);
            }
        }, 1000);

    }

    public void checkAnswer(final int answersCounter, String questionForAnswers, String userAnswers, final Button clickedButton){
        int questionCounter = answersCounter - 1;
        if(userAnswers.toLowerCase().equals(correctAnswer.toLowerCase())){
            // Warna button Text berubah menjadi hijau
            clickedButton.setTextColor(getApplication().getResources().getColor(R.color.green));

            String stringCurrentRightScore = rightAnswerTextView.getText().toString();
            int currentRightScore = Integer.valueOf(stringCurrentRightScore);
            rightAnswerScore = currentRightScore+1;
            rightAnswerTextView.setText(String.valueOf(rightAnswerScore));

            UserAnswersTraining userAnswersTraining = new UserAnswersTraining(questionCounter, questionForAnswers, userAnswers, true);
            userAnswersList.add(userAnswersTraining);
        }else{
            String stringButton_1 = answerButton_1.getText().toString();
            String stringButton_2 = answerButton_2.getText().toString();
            String stringButton_3 = answerButton_3.getText().toString();
            String stringButton_4 = answerButton_4.getText().toString();

            if(correctAnswer.toLowerCase().equals(stringButton_1.toLowerCase())){
                answerButton_1.setTextColor(getApplication().getResources().getColor(R.color.green));
            }

            if(correctAnswer.toLowerCase().equals(stringButton_2.toLowerCase())){
                answerButton_2.setTextColor(getApplication().getResources().getColor(R.color.green));
            }

            if(correctAnswer.toLowerCase().equals(stringButton_3.toLowerCase())){
                answerButton_3.setTextColor(getApplication().getResources().getColor(R.color.green));
            }

            if(correctAnswer.toLowerCase().equals(stringButton_4.toLowerCase())){
                answerButton_4.setTextColor(getApplication().getResources().getColor(R.color.green));
            }
            // Warna button Text berubah menjadi merah
            clickedButton.setTextColor(getApplication().getResources().getColor(R.color.red));

            String stringCurrentWrongScore = wrongAnswerTextView.getText().toString();
            int currentWrongScore = Integer.valueOf(stringCurrentWrongScore);
            wrongAnswerScore = currentWrongScore+1;
            wrongAnswerTextView.setText(String.valueOf(wrongAnswerScore));

            UserAnswersTraining userAnswersTraining = new UserAnswersTraining(questionCounter, questionForAnswers, userAnswers, false);
            userAnswersList.add(userAnswersTraining);
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Ubah warna button setelah delay 1500ms
                answerButton_1.setTextColor(getApplication().getResources().getColor(R.color.white));
                answerButton_2.setTextColor(getApplication().getResources().getColor(R.color.white));
                answerButton_3.setTextColor(getApplication().getResources().getColor(R.color.white));
                answerButton_4.setTextColor(getApplication().getResources().getColor(R.color.white));
                nextQuestion(answersCounter);
            }
        }, 1000);
    }

    public void buttonOn(){
        answerButton_1.setEnabled(true);
        answerButton_2.setEnabled(true);
        answerButton_3.setEnabled(true);
        answerButton_4.setEnabled(true);
    }

    public void buttonOff(){
        answerButton_1.setEnabled(false);
        answerButton_2.setEnabled(false);
        answerButton_3.setEnabled(false);
        answerButton_4.setEnabled(false);
    }

    public void nextQuestion(int answersCounter){
        kosakataKategoriSoal.clear();
        getQuestion();
        questionCounterTextView.setText(String.valueOf(answersCounter));
    }

    public void setIntent(){
        int finalCounter  = counter-1;
        Intent intent = new Intent(this, PostTrainingActivity.class);
        intent.putParcelableArrayListExtra("userAnswersList", (ArrayList<UserAnswersTraining>) userAnswersList);
        intent.putExtra("counter", finalCounter);
        intent.putExtra("rightAnswerScore", rightAnswerScore);
        intent.putExtra("wrongAnswerScore", wrongAnswerScore);
        intent.putExtra("blankAnswerScore", blankAnswerScore);
        startActivity(intent);
        finish();
    }

    public void setEndButton(){
        if(userAnswersList.size() < 1){
            setIntent();
        }else{
            cancelTimer();
            setIntent();
        }
    }
}
