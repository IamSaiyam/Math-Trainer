package com.example.android.mathtrainer;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.audiofx.AcousticEchoCanceler;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import io.github.muddz.styleabletoast.StyleableToast;

public class MainActivity extends AppCompatActivity {

    TextView timerTv, scoreTv, questionTv, option1Tv, option2Tv, option3Tv, option4Tv, gameOverTv, statusTv;
    Button playAgainBtn;
    View homeView, gameView, playBtn, status;
    int correctPostion, yourPoints=0, attempted=0, answer;
    Random rand;
    Boolean gameOn = false, screen2 = false;
    MediaPlayer mediaPlayerEnd, mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playBtn = findViewById(R.id.playBtn);
        timerTv = findViewById(R.id.timerTv);
        scoreTv = findViewById(R.id.scoreTv);
        questionTv = findViewById(R.id.questionTv);
        option1Tv = findViewById(R.id.option1);
        option2Tv = findViewById(R.id.option2);
        option3Tv = findViewById(R.id.option3);
        option4Tv = findViewById(R.id.option4);
        gameOverTv = findViewById(R.id.gameOverTv);
        playAgainBtn = findViewById(R.id.playAgainBtn);
        status = findViewById(R.id.status);
        statusTv = findViewById(R.id.statusTV);
        homeView = findViewById(R.id.homeView);
        gameView = findViewById(R.id.gameView);

        gameView.setVisibility(View.GONE);
        gameOverTv.setVisibility(View.GONE);
        playAgainBtn.setVisibility(View.GONE);

        rand = new Random();
        status.setAlpha(0);


        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.setVisibility(View.VISIBLE);
                homeView.setVisibility(View.GONE);
                gameOn = true;
                screen2 = true;
                generateQuestion();
                startTimer();
            }
        });

        playAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameOn = true;
                gameOverTv.setVisibility(View.GONE);
                playAgainBtn.setVisibility(View.GONE);
                generateQuestion();
                startTimer();
                resetScore();
                option1Tv.setEnabled(true);
                option2Tv.setEnabled(true);
                option3Tv.setEnabled(true);
                option4Tv.setEnabled(true);
            }
        });

    }

    private void resetScore() {
        scoreTv.setText("Score: 0/0" );
        yourPoints = 0;
        attempted = 0;
    }

    public void startTimer(){
        new CountDownTimer(45000, 1000){

            @Override
            public void onTick(long millisUntilFinished) {
                String secondsLeft = String.valueOf(millisUntilFinished/1000);
                if(secondsLeft.length()<2){
                    timerTv.setText("00:0" + secondsLeft);
                }else {
                    timerTv.setText("00:" + millisUntilFinished / 1000);
                }
            }

            @Override
            public void onFinish() {
                gameOn = false;
                mediaPlayerEnd = MediaPlayer.create(getApplicationContext(), R.raw.abe_yaar);
                mediaPlayerEnd.start();
                option1Tv.setEnabled(false);
                option2Tv.setEnabled(false);
                option3Tv.setEnabled(false);
                option4Tv.setEnabled(false);
                gameOverTv.setVisibility(View.VISIBLE);
                playAgainBtn.setVisibility(View.VISIBLE);
            }

        }.start();
    }

    public char getRandomElement(List<Character> list)
    {
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }

    public void generateQuestion(){

        if(gameOn) {
            int a = rand.nextInt(21);
            int b = rand.nextInt(21);

            List<Character> symbols = new ArrayList<>();
            symbols.add('+');
            symbols.add('-');
            symbols.add('x');
            symbols.add('/');

            char symbol = getRandomElement(symbols);

            questionTv.setText(a + " " + symbol + " " + b + " = ?");

            ArrayList<Integer> options = new ArrayList<>();
            correctPostion = rand.nextInt(4);
            switch(symbol){
                case '+':
                    answer = a + b;
                    break;
                case '-':
                    answer  = a - b;
                    break;
                case 'x':
                    answer = a * b;
                    break;
                case '/':
                    answer = a / b;
                    break;
                default:
            }

            for (int i = 0; i < 4; i++) {
                if (i == correctPostion) {
                    options.add(answer);
                } else {
                    int wrongAnswer;
                    wrongAnswer = rand.nextInt(41);
                    while (wrongAnswer == answer) {
                        wrongAnswer = rand.nextInt(41);
                    }
                    options.add(wrongAnswer);
                }
            }

            option1Tv.setText("" + options.get(0));
            option2Tv.setText("" + options.get(1));
            option3Tv.setText("" + options.get(2));
            option4Tv.setText("" + options.get(3));
        }

    }

    public void selectAnswer(View view){
        mediaPlayer = MediaPlayer.create(this, R.raw.answer);
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        status.setAlpha(0f);
        attempted++;
        if(String.valueOf(correctPostion).equals(view.getTag().toString())){
            statusTv.setText("Correct!");
            statusTv.setBackgroundColor(Color.parseColor("#FF99CC00"));
            mediaPlayer = MediaPlayer.create(this, R.raw.answer);
            mediaPlayer.start();
            yourPoints++;
        }else {
            status.setVisibility(View.VISIBLE);
            statusTv.setText("Wrong! Correct was " + answer);
            statusTv.setBackgroundColor(Color.parseColor("#F67067"));

        }

        status.animate()
                .alpha(1f)
                .setDuration(300);
        scoreTv.setText("Score: " + yourPoints + "/" + attempted);
        generateQuestion();
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            if(!screen2) {
                this.finish();
                mediaPlayerEnd.stop();
                mediaPlayerEnd.release();
            }
            else {
                gameView.setVisibility(View.GONE);
                homeView.setVisibility(View.VISIBLE);
                screen2 = false;
            }
            return;
        }


        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press Back again to go back.", Toast.LENGTH_SHORT).show();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 3000);
    }

}