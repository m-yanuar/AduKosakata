package com.payjoe.adukosakata;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.AppCompatButton;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.payjoe.adukosakata.Models.KosakataModels;
import com.payjoe.adukosakata.Models.User;
import com.payjoe.adukosakata.Models.UserAnswersFirebase;
import com.payjoe.adukosakata.Models.UserAnswersModels;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InGameActivity extends AppCompatActivity {

    //Inisialisasi array integer questionIndex
    public ArrayList<Integer> questionIndex = new ArrayList<Integer>();
    public List<List<Integer>> randomAnswerIndex = new ArrayList<List<Integer>>();

    //Inisialisasi array correctAnswers
    public ArrayList<String> correctAnswersArray = new ArrayList<>();

    //Inisialisasi User & Enemy View
    private TextView userUsernameTextView;
    private TextView enemyUsernameTextView;
    private TextView userScoreTextView;
    private TextView enemyScoreTextView;

    //Inisialisasi User & Enemy Variable;
    public String userUsername;
    public String enemyUsername;
    public String userUserID;
    public String enemyUserID;
    public String userMatchIDIndex;
    public int userScore = 0;
    public int enemyScore;
    public String matchID;
    public int userFinalScore;
    public int enemyFinalScore;
    public int userRank;

    //Inisialisasi Question & Answer Field View
    private LinearLayout counter_field_LinearLayout;
    private TextView questionTextView;
    private TextView questionCounterTextView;
    private AppCompatButton answerButton_1;
    private AppCompatButton answerButton_2;
    private AppCompatButton answerButton_3;
    private AppCompatButton answerButton_4;
    private ProgressBar progressBar;
    private CardView questionCardView;

    //Inisialisasi Database Firebase;
    private DatabaseReference mDatabase;
    private DatabaseReference mMatches;
    public FirebaseAuth mFirebaseAuth;
    ChildEventListener userMatchesChildEventListener;
    Query mUserMatchesRef;
    public DatabaseReference mUserFinished;
    Query mQuestionIndexRef;
    ValueEventListener mQuestionIndexRefListener;
    Query mRandomAnswerRef;
    ValueEventListener mRandomAnswerRefListener;

    // Penghitung Jumlah Soal
    public int counter = 0;

    //Timer view
    public TextView timerTextView;

    // Timer
    public CountDownTimer timer;
    public int secondsLeft = 0;

    public List<UserAnswersModels> userAnswersList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        //Set Database Reference
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("kosakata").keepSynced(true);
        //Get User ID
        userUserID = mFirebaseAuth.getInstance().getCurrentUser().getUid();
        setContentView(R.layout.activity_in_game);

        //Set user & enemy TextView
        userUsernameTextView = (TextView) findViewById(R.id.user_a);
        enemyUsernameTextView = (TextView) findViewById(R.id.user_b);
        userScoreTextView = (TextView) findViewById(R.id.score_a);
        enemyScoreTextView = (TextView) findViewById(R.id.score_b);

        //Set View
        questionCardView = (CardView) findViewById(R.id.question_cardView);
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
        timerTextView = (TextView) findViewById(R.id.timer);
        counter_field_LinearLayout = (LinearLayout)findViewById(R.id.counter_field);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_findMatch);
    }

    @Override
    public void onBackPressed(){

    }

    @Override
    public void onStart(){
        super.onStart();
        //Get Match ID
        mDatabase.child("matches").keepSynced(true);
        getMatchId();
    }

    @Override
    public void onStop(){
        super.onStop();
        mDatabase.child("matches").keepSynced(false);
        mUserMatchesRef.removeEventListener(userMatchesChildEventListener);
        mQuestionIndexRef.removeEventListener(mQuestionIndexRefListener);
        mRandomAnswerRef.removeEventListener(mRandomAnswerRefListener);
    }

    //Mengambil data intent dari FindMatchActivity
    public void getUserInfo(){
        //Mengambil data username milik user & enemy dari FindMatchActivity
        Bundle intent = getIntent().getExtras();
        userUsername = intent.getString("userUsername");
        enemyUsername = intent.getString("enemyUsername");
        enemyUserID = intent.getString("enemyUserID");
        Log.v("InGameActivity", "User UserName : " + userUsername);
        Log.v("InGameActivity", "Enemy UserName : " + enemyUsername);
        Log.v("InGameActivity", "Enemy USerID : " + enemyUserID);
        userUsernameTextView.setText(userUsername);
        enemyUsernameTextView.setText(enemyUsername);
        listenEnemyScore(matchID, enemyUserID);
    }

    /**
     * Method untuk mengambil Match_id dari permainan
     */
    public void getMatchId (){
        mUserMatchesRef = mDatabase.child("user-matches").child(userUserID).orderByKey().limitToLast(1);
        userMatchesChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.v("InGameActivity", "Match ID Children : " + dataSnapshot.getChildrenCount());
                Log.v("InGameActivity", "Users current Match ID Index : " + dataSnapshot.getKey());
                Log.v("InGameActivity", "Users current Match ID : " + dataSnapshot.child("matchId").getValue());
                Log.v("InGameActivity", "Current Match Status : " + dataSnapshot.child("finished").getValue());
                userMatchIDIndex = String.valueOf(dataSnapshot.getKey());
                matchID = String.valueOf(dataSnapshot.child("matchId").getValue());

                mUserFinished = mDatabase.child("matches").child(matchID)
                        .child("listOfPlayers");
                mUserFinished.keepSynced(true);
                mQuestionIndexRef = mDatabase.child("matches").child(matchID).child("indexQuestion");
                mQuestionIndexRef.keepSynced(true);
                mRandomAnswerRef = mDatabase.child("matches").child(matchID).child("indexRandomAnswer");
                mRandomAnswerRef.keepSynced(true);

                getUserInfo();
                getQuestionIndex();
                getRandomAnswerIndex();
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
        mUserMatchesRef.addChildEventListener(userMatchesChildEventListener);
    }

    /**
     * Method untuk memantau score musuh
     * @param matchId Match_id yang sedang dimainkan
     * @param enemyId user_id milik musuh
     */
    public void listenEnemyScore(String matchId, String enemyId){
        Query mEnemyScore = mDatabase.child("matches").child(matchId).child("listOfPlayers").child(enemyId).child("score");
        mEnemyScore.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.v("InGameActivity", "Enemy Score: " + dataSnapshot.getValue());
                    int currentEnemyScoreInteger;
                    if(dataSnapshot.getValue() == null){
                        currentEnemyScoreInteger = 0;
                    }else{
                        String currentEnemyScore = String.valueOf(dataSnapshot.getValue());
                        currentEnemyScoreInteger = Integer.valueOf(currentEnemyScore);
                    }
                    enemyScoreTextView.setText(String.valueOf(currentEnemyScoreInteger));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Method untuk mengambil Index Soal yang sudah tersedia di Firebase
     * (index soal dihasilkan dari cloud functions firebase)
     */
    public void getQuestionIndex(){
        //Mengakses node index soal
        mQuestionIndexRefListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child: dataSnapshot.getChildren()){
                        //mengambil index soal dan diconvert ke string
                        String currentIndex = String.valueOf(child.getValue());
                        //Log.v("InGameActivity", "CurrentIndex : " + currentIndex);

                        //Mengkonversi String Index soal ke Integer
                        int currentIndexInteger = Integer.parseInt(currentIndex);
                        //Log.v("InGameActivity", "CurrentIndex converted to Int : " + currentIndexInteger);
                        //Masukkan index soal ke array questionIndex
                        questionIndex.add(currentIndexInteger);
                    }
                    Log.v("InGameActivity", "These are Question Index : " + questionIndex);
                    Log.v("InGameActivity", "These are Question Index size : " + questionIndex.size());

                    /**
                     * Setelah mengakses semua index soal, kemudian memanggil method getQuestion()
                     * dan getAnswer()
                     */
                    getQuestion(counter);
                    getAnswer();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mQuestionIndexRef.addValueEventListener(mQuestionIndexRefListener);
    }

    /**
     * Method untuk mengambil index Jawaban Random yang sudah tersedia di Firebase
     * (index Jawaban Random dihasilkan dari cloud functions firebase)
     */
    public void getRandomAnswerIndex(){
        //Mengakses index Jawaban Random
        mRandomAnswerRefListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child: dataSnapshot.getChildren()){
                        //Mengambil 4 value Jawaban setiap satu index Jawaban Random dan dikonversi dari object ke String
                        String index_0 = String.valueOf(child.child("0").getValue());
                        String index_1 = String.valueOf(child.child("1").getValue());
                        String index_2 = String.valueOf(child.child("2").getValue());
                        String index_3 = String.valueOf(child.child("3").getValue());

                        //Mengkonversi 4 value Jawaban dari String ke Integer
                        int integerIndex_0 = Integer.parseInt(index_0);
                        int integerIndex_1 = Integer.parseInt(index_1);
                        int integerIndex_2 = Integer.parseInt(index_2);
                        int integerIndex_3 = Integer.parseInt(index_3);

                        //Inisialisasi Array Lokal untuk menampung 4 Jawaban
                        List<Integer> randomAnswerArray = new ArrayList<>();
                        //Memasukkan Hasil konversi Integer ke array RandomAnswerArray
                        randomAnswerArray.add(integerIndex_0);
                        randomAnswerArray.add(integerIndex_1);
                        randomAnswerArray.add(integerIndex_2);
                        randomAnswerArray.add(integerIndex_3);
                        //Memasukkan array local RandomAnswerArray ke Array randomAnswerIndex
                        randomAnswerIndex.add(randomAnswerArray);

                        Log.v("InGameActivity", "This is RandomAnswer Array: " + randomAnswerArray);
                        Log.v("InGameActivity", "This is RandomAnswer Array Size: " + randomAnswerArray.size());
                    }
                    Log.v("InGameActivity", "This is RandomAnswer Array Combined in One Array: " + randomAnswerIndex);
                    Log.v("InGameActivity", "This is RandomAnswer Array Combined in One Array Size: " + randomAnswerIndex.size());
                    //Memanggil method getRandomAnswer()
                    getRandomAnswer(counter);
                    //Memanggil method initView()
                    initView();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mRandomAnswerRef.addValueEventListener(mRandomAnswerRefListener);
    }

    /**
     * Method untuk mengambil data kosakata berdasarkan questionIndex/indexSoal
     * @param counterQuestion mengambil urutan soal terkini
     */
    public void getQuestion(int counterQuestion){
        //mengambil indexSoal berdasarkan counterQuestion
        int currentQuestionIndex = questionIndex.get(counterQuestion);
        //Mengakses path kosakata berdasarkan value currenQuestionIndex
        mDatabase.child("kosakata").child(String.valueOf(currentQuestionIndex)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                KosakataModels kosakata = dataSnapshot.getValue(KosakataModels.class);
                //Set value kosakata dari firebase ke questionTextView
                questionTextView.setText(kosakata.ing.toUpperCase());
                Log.v("InGameActivity", "Current Question" + kosakata.ing);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Method untuk mengambil Kunci Jawaban/kosakata indonesia
     */
    public void getAnswer(){
        for(int i = 0; i < questionIndex.size(); i++) {
            int answerIndexInteger = questionIndex.get(i);
            String answerIndex = String.valueOf(answerIndexInteger);
            //Mengakses path kosakata untuk mengambil kunci jawaban
            mDatabase.child("kosakata").child(answerIndex).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Mengambil kunci jawaban
                    KosakataModels kosakata = dataSnapshot.getValue(KosakataModels.class);
                    //Memasukkan kunci jawaban ke array correctAnswersArray
                    correctAnswersArray.add(kosakata.indo.toUpperCase());
                    Log.v("InGameActivity", "This is the Correct Answers : " + kosakata.indo);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void logcatAnswer(){
        Log.v("InGameActivity", "These Are the Correct Answers: " + correctAnswersArray);
        Log.v("InGameActivity", "These Are the Correct Answers Size: " + correctAnswersArray.size());
    }


    /**
     * Method untuk mengambil RandomAnswer/JawabanRandom
     * @param counter urutan soal terkini
     */
    public void getRandomAnswer(int counter){
        //Mengambil index RandomAnswer, digunakan untuk mengambil kosakata indonesia
        int currentRandomAnswerIndex_0 = randomAnswerIndex.get(counter).get(0);
        int currentRandomAnswerIndex_1 = randomAnswerIndex.get(counter).get(1);
        int currentRandomAnswerIndex_2 = randomAnswerIndex.get(counter).get(2);
        int currentRandomAnswerIndex_3 = randomAnswerIndex.get(counter).get(3);

        //Path masing-masing RandomAnswer menuju node kosakata
        Query mRandomAnswerIndex_0 = mDatabase.child("kosakata").child(String.valueOf(currentRandomAnswerIndex_0));
        Query mRandomAnswerIndex_1 = mDatabase.child("kosakata").child(String.valueOf(currentRandomAnswerIndex_1));
        Query mRandomAnswerIndex_2 = mDatabase.child("kosakata").child(String.valueOf(currentRandomAnswerIndex_2));
        Query mRandomAnswerIndex_3 = mDatabase.child("kosakata").child(String.valueOf(currentRandomAnswerIndex_3));

        //Mengambil RandomAnswer ke-1
        mRandomAnswerIndex_0.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                KosakataModels kosakata = dataSnapshot.getValue(KosakataModels.class);
                //Set value RandomAnswer ke Button 1
                answerButton_1.setText(kosakata.indo.toUpperCase());
                Log.v("InGameActivity", "Answer Button 1 : " + kosakata.indo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Mengambil RandomAnswer ke-2
        mRandomAnswerIndex_1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                KosakataModels kosakata = dataSnapshot.getValue(KosakataModels.class);
                //Set value RandomAnswer ke Button 2
                answerButton_2.setText(kosakata.indo.toUpperCase());
                Log.v("InGameActivity", "Answer Button 2 : " + kosakata.indo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Mengambil RandomAnswer ke-3
        mRandomAnswerIndex_2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                KosakataModels kosakata = dataSnapshot.getValue(KosakataModels.class);
                //Set value RandomAnswer ke Button 3
                answerButton_3.setText(kosakata.indo.toUpperCase());
                Log.v("InGameActivity", "Answer Button 3 : " + kosakata.indo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Mengambil RandomAnswer ke-4
        mRandomAnswerIndex_3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                KosakataModels kosakata = dataSnapshot.getValue(KosakataModels.class);
                //Set value RandomAnswer ke Button 4
                answerButton_4.setText(kosakata.indo.toUpperCase());
                Log.v("InGameActivity", "Answer Button 4 : " + kosakata.indo);
                logcatAnswer();
                startTimer();
                buttonOn();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Menampilkan View pada tampilan In-Game, dan menyembunyikan progressBar
     */
    public void initView(){
        progressBar.setVisibility(View.GONE);
        questionCardView.setVisibility(View.VISIBLE);
        timerTextView.setVisibility(View.VISIBLE);
        counter_field_LinearLayout.setVisibility(View.VISIBLE);
        questionTextView.setVisibility(View.VISIBLE);
        answerButton_1.setVisibility(View.VISIBLE);
        answerButton_2.setVisibility(View.VISIBLE);
        answerButton_3.setVisibility(View.VISIBLE);
        answerButton_4.setVisibility(View.VISIBLE);
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
                    Log.v("InGameActivity", "Timer Started! : " + secondsLeft);
                    timerTextView.setText(String.valueOf(secondsLeft));
                }
            }

            @Override
            public void onFinish() {
                secondsLeft = 0;
                Log.v("InGameActivity ", "Timer Ended!");
                counter = counter + 1;
                userNoAnswer(counter);
            }
        };
        timer.start();
    }

    //Method untuk Membersihkan Timer
    public void cancelTimer(){
        secondsLeft = 0;
        timer.cancel();
        Log.v("InGameActivity ", "Timer Ended!");
        Log.v("InGameActivity ", "Seconds Left on Question"+ counter + ": "+ secondsLeft);
    }

    // Aksi yg dilakukan ketika Button 1 diklik
    public void setAnswerButton_1() {
        buttonOff();
        counter = counter + 1;
        Button clickedButton = answerButton_1;
        //Membersihkan Timer
        cancelTimer();
        answersResult(counter, questionTextView.getText().toString(), answerButton_1.getText().toString(), clickedButton);
    }

    // Aksi yg dilakukan ketika Button 2 diklik
    public void setAnswerButton_2() {
        buttonOff();
        counter = counter + 1;
        Button clickedButton = answerButton_2;
        //Membersihkan Timer
        cancelTimer();
        answersResult(counter, questionTextView.getText().toString(), clickedButton.getText().toString(), clickedButton);
    }

    // Aksi yg dilakukan ketika Button 3 diklik
    public void setAnswerButton_3(){
        buttonOff();
        counter = counter + 1;
        Button clickedButton = answerButton_3;
        //Membersihkan Timer
        cancelTimer();
        answersResult(counter, questionTextView.getText().toString(), clickedButton.getText().toString(), clickedButton);
    }

    // Aksi yg dilakukan ketika Button 4 diklik
    public void setAnswerButton_4(){
        buttonOff();
        counter = counter + 1;
        Button clickedButton = answerButton_4;
        //Membersihkan Timer
        cancelTimer();
        answersResult(counter, questionTextView.getText().toString(), clickedButton.getText().toString(), clickedButton);
    }


    public void userNoAnswer(final int answersCounter){
        buttonOff();
        //Membersihkan Timer
        cancelTimer();
        int mustBeCorrectAnswersIndex = answersCounter - 1;
        //Inisialisasi variable kunci jawaban
        String mustBeCorrectAnswers = correctAnswersArray.get(mustBeCorrectAnswersIndex);

        String questionForAnswers = questionTextView.getText().toString();
        String userAnswersBlank = "";

        String stringButton_1 = answerButton_1.getText().toString();
        String stringButton_2 = answerButton_2.getText().toString();
        String stringButton_3 = answerButton_3.getText().toString();
        String stringButton_4 = answerButton_4.getText().toString();

        if(mustBeCorrectAnswers.toLowerCase().equals(stringButton_1.toLowerCase())){
            answerButton_1.setTextColor(getApplication().getResources().getColor(R.color.green));
        }else{
            answerButton_1.setTextColor(getApplication().getResources().getColor(R.color.red));
        }

        if(mustBeCorrectAnswers.toLowerCase().equals(stringButton_2.toLowerCase())){
            answerButton_2.setTextColor(getApplication().getResources().getColor(R.color.green));
        }else{
            answerButton_2.setTextColor(getApplication().getResources().getColor(R.color.red));
        }

        if(mustBeCorrectAnswers.toLowerCase().equals(stringButton_3.toLowerCase())){
            answerButton_3.setTextColor(getApplication().getResources().getColor(R.color.green));
        }else{
            answerButton_3.setTextColor(getApplication().getResources().getColor(R.color.red));
        }

        if(mustBeCorrectAnswers.toLowerCase().equals(stringButton_4.toLowerCase())){
            answerButton_4.setTextColor(getApplication().getResources().getColor(R.color.green));
        }else{
            answerButton_4.setTextColor(getApplication().getResources().getColor(R.color.red));
        }

        UserAnswersModels userAnswersModels = new UserAnswersModels(questionForAnswers, userAnswersBlank, false);
        userAnswersList.add(userAnswersModels);
        //Memasukkan Jawaban user ke Firebase menggunakan method pushUserAnswers
        pushUserAnswers(questionForAnswers, userAnswersBlank, false);

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

    /**
     * Pengecekan Jawaban User dengan membandingkan value dari correctAnswersArray dengan userAnswers
     *
     * @param answersCounter informasi pertanyaan ke-sekian misal, pertanyaan #1, pertanyaan #2
     * @param userAnswers   string dari jawaban yang diklik oleh user, value diambil dari Text Button
     * @param questionForAnswers string dari soal yang muncul di interface, value diambil dari Text View
     * @param clickedButton button ke-sekian yang dilik oleh user, button #1 button #2 button #3
     *                      atau button 4
     */

    public void answersResult(final int answersCounter, String questionForAnswers, String userAnswers, final Button clickedButton){
        int mustBeCorrectAnswersIndex = answersCounter - 1;
        //Inisialisasi variable kunci jawaban
        String mustBeCorrectAnswers = correctAnswersArray.get(mustBeCorrectAnswersIndex);
        Log.v("InGameActivity", "Jawaban yang dipilih User adalah : " + userAnswers);
        Log.v("InGameActivity", "Jawaban yang benar adalah : " + mustBeCorrectAnswers);

        //Percabangan apabila kunci jawaban sama dengan jawaban user
        if(mustBeCorrectAnswers.toLowerCase().equals(userAnswers.toLowerCase())){

            // Warna button Text berubah menjadi hijau
            clickedButton.setTextColor(getApplication().getResources().getColor(R.color.green));

            /** Masukkan jawaban user(userAnswers) ke UserAnswers Models
             *  dikumpulkan dalam userAnswersList
             */
            UserAnswersModels userAnswersModels = new UserAnswersModels(questionForAnswers, userAnswers, true);
            userAnswersList.add(userAnswersModels);
            //Memasukkan Jawaban user ke Firebase menggunakan method pushUserAnswers
            pushUserAnswers(questionForAnswers, userAnswers, true);

            userScore = userScore + 1;
            //Store userScore ke Database Firebase
            userScoreTextView.setText(String.valueOf(userScore));
            pushUserScore(userScore);

            // Jika tidak sama maka
        }else{

            // Warna button Text berubah menjadi merah
            clickedButton.setTextColor(getApplication().getResources().getColor(R.color.red));
            UserAnswersModels userAnswersModels = new UserAnswersModels(questionForAnswers, userAnswers, false);
            userAnswersList.add(userAnswersModels);
            //Memasukkan Jawaban user ke Firebase menggunakan method pushUserAnswers
            pushUserAnswers(questionForAnswers, userAnswers, false);
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Ubah warna button setelah delay 1500ms
                clickedButton.setTextColor(getApplication().getResources().getColor(R.color.white));
                nextQuestion(answersCounter);
            }
        }, 1000);
    }

    /**
     * Method untuk mengirim jawaban user ke Firebase pada node matches
     * @param questionForAnswer Soal yang telah dijawab user
     * @param userAnswer Jawaban user
     * @param resultAnswer hasil jawaban user, apakah benar atau salah
     */
    public void pushUserAnswers(String questionForAnswer, String userAnswer, boolean resultAnswer){
        int pushCounter = counter;
        String pushCounterString = String.valueOf(pushCounter);
        DatabaseReference mUserAnswersList = mDatabase.child("matches").child(matchID)
                .child("listOfPlayers").child(userUserID).child("userAnswers").child(String.valueOf(pushCounterString));
        UserAnswersFirebase userAnswersList = new UserAnswersFirebase(questionForAnswer, userAnswer, resultAnswer);
        mUserAnswersList.setValue(userAnswersList);
        Log.v("InGameActivity", "Question number: " + pushCounter + " Push UserAnswer done.");
    }

    /**
     * Method untuk mengirim score user ke firebase pada node matches
     * @param userScore score terkini yang dimiliki user
     */
    public void pushUserScore(int userScore){
        DatabaseReference mUserScore = mDatabase.child("matches").child(matchID)
                .child("listOfPlayers").child(userUserID).child("score");
        mUserScore.setValue(userScore);
        Log.v("InGameActivity", "Question number: " + counter + " Push UserScore done.");
    }

    /**
     * Mengirim status match ke firebase pada node user-matches dan node matches
     */
    public void pushMatchStatus(){
        //update status node ListOfPlayers
        mUserFinished.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if(mutableData.getValue()==null){
                    return Transaction.success(mutableData);
                }

                mutableData.child(userUserID).child("finished").setValue(true);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
        //mUserFinished.setValue(true);


        //update status node user-matches
        DatabaseReference mMatchStatus = mDatabase.child("user-matches").child(userUserID)
                .child(userMatchIDIndex).child("finished");
        mMatchStatus.setValue(true);
        Log.v("InGameActivity", "Push Match Status done.");
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

    // Function untuk masuk ke Pertanyaan Selanjutnya
    public void nextQuestion(final int questionCounter) {
        // Mengubah urutan dari 0...3(urutan array) menjadi 1...4(urutan soal)
        final int questionCounterIndex = questionCounter + 1;
        /**
         * Jika Soal sudah mencapai soal ke-10, maka masuk ke PostGameActivity sekaligus mengirimkan
         * ArrayList userAnswersList ke PostGameActivity
         */
        if (questionCounterIndex > 10) {
            //Membersihkan Timer
            counter = 0;
            secondsLeft = 0;
            //Memanggil method void pushMatchStatus()
            pushMatchStatus();
            //Memanggil method void updateUserRank()
            //updateUserRank();
            //Intent ke PostGameActivity
            Intent intent = new Intent(this, PostGameFirebaseRecycler.class);
            /*Mengirimkan Data userAnswersList ke PostGameActivity
            intent.putParcelableArrayListExtra("ListOfUserAnswers", (ArrayList<UserAnswersModels>)userAnswersList);*/
            intent.putExtra("matchID", matchID);
            startActivity(intent);
            finish();
        } else {
            // Set TextView dan Button untuk Pertanyaan Selanjutnya
            questionCounterTextView.setText(String.valueOf(questionCounterIndex));
            // Mengambil Soal dan Jawaban Random selanjutnya
            getQuestion(questionCounter);
            getRandomAnswer(questionCounter);
        }
    }
}