package com.example.candycrush;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    GridLayout gridLayout;
    DatabaseHelper db;
    LinearLayout mainLayout;
    TextView scoreResult,tempsRestant;
    int[] candies;

    ImageView pause;
    private CountDownTimer countDownTimer;

    private int initialTime = Integer.parseInt(Menu.selectedTemps.replace(" s", ""))*1000; // Initial time in milliseconds (30 seconds)
    private long remainingTime = initialTime;
    int widthOfBlock, noOfBlock=8, widthOfScreen, heightOfScreen;
    ArrayList<ImageView> candy = new ArrayList<>();
    int candyToBeDragged, candyToBeReplace, notCandy=R.drawable.ic_launcher_background;
    Handler handler, finishHandler;

    int interval = 100, score = 0;


    PopupWindow popupWindow;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DatabaseHelper(this);
        mainLayout = findViewById(R.id.mainLayout);
        pause = findViewById(R.id.pause);
        scoreResult = findViewById(R.id.score);
        tempsRestant = findViewById(R.id.time);

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View pauseView = inflater.inflate(R.layout.activity_pause, null);

                Button continuer = pauseView.findViewById(R.id.continuer);
                Button recommencer = pauseView.findViewById(R.id.recommencer);
                Button quitter = pauseView.findViewById(R.id.quitter);

                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                boolean focusable = true;
                popupWindow = new PopupWindow(pauseView, width, height, focusable);

                continuer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

                recommencer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                });

                quitter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), Menu.class);
                        startActivity(intent);
                    }
                });
                mainLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        popupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
                    }
                });
            }
        });

        //Selection du theme
        if(Menu.selectedTheme.equals("Classic")){
            mainLayout.setBackgroundResource(R.drawable.classic);
            candies= new int[]{
                    R.drawable.bluecandy,
                    R.drawable.greencandy,
                    R.drawable.redcandy,
                    R.drawable.yellowcandy,
                    R.drawable.orangecandy,
                    R.drawable.purplecandy
            };
        }
        if(Menu.selectedTheme.equals("Halloween")){
            mainLayout.setBackgroundResource(R.drawable.halloween);
            candies= new int[]{
                    R.drawable.bluespecial,
                    R.drawable.greenspecial,
                    R.drawable.redcandy,
                    R.drawable.yellowspecial,
                    R.drawable.orangespecial,
                    R.drawable.purplespecial
            };
        }
        if(Menu.selectedTheme.equals("Special")){
            mainLayout.setBackgroundResource(R.drawable.special);
            candies= new int[]{
                    R.drawable.blue3,
                    R.drawable.green3,
                    R.drawable.red3,
                    R.drawable.yellow3,
                    R.drawable.orange3,
                    R.drawable.purple3
            };
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        widthOfScreen   = displayMetrics.widthPixels;
        heightOfScreen  = displayMetrics.heightPixels;
        widthOfBlock = widthOfScreen / noOfBlock;
        createBoard();
        for(final ImageView imageView: candy){
            imageView.setOnTouchListener(new OnSwipeListener(this)
            {
                @Override
                void onSwipeLeft() {
                    super.onSwipeLeft();
                    candyToBeDragged = imageView.getId();
                    candyToBeReplace = candyToBeDragged - 1 ;
                    candyInterchange();
                }

                @Override
                void onSwipeRight() {
                    super.onSwipeRight();
                    candyToBeDragged = imageView.getId();
                    candyToBeReplace = candyToBeDragged + 1 ;
                    candyInterchange();
                }

                @Override
                void onSwipeTop() {
                    super.onSwipeTop();
                    candyToBeDragged = imageView.getId();
                    candyToBeReplace = candyToBeDragged - noOfBlock ;
                    candyInterchange();
                }

                @Override
                void onSwipeBottom() {
                    super.onSwipeBottom();
                    candyToBeDragged = imageView.getId();
                    candyToBeReplace = candyToBeDragged + noOfBlock ;
                    candyInterchange();
                }
            });

        }
        handler = new Handler();
        startRepeat();
        startCountdownTimer();
        finishHandler = new Handler();

        startFinishHandler();
    }



    private void startCountdownTimer() {
        countDownTimer = new CountDownTimer(initialTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update remaining time
                remainingTime = millisUntilFinished;
                // Update UI to display remaining time
                updateTimerUI();
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }
    private void updateTimerUI() {
        int seconds = (int) (remainingTime / 1000);
        String timeString = String.format(Locale.getDefault(), "%02d:%02d", seconds / 60, seconds % 60);
        tempsRestant.setText(timeString);
    }
    private void startFinishHandler() {
        finishHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Finish the activity after 30 seconds
                int finalScore = gameOver();
                Score scoreJoueur = new Score(Menu.nomJoueur,Menu.selectedTheme,Integer.parseInt(Menu.selectedTemps.replace(" s", "")),finalScore);
                db.insertScore(scoreJoueur.getNom(), scoreJoueur.getTheme(), scoreJoueur.getTemps(), scoreJoueur.getScore());
            }
        }, initialTime); // 30 seconds (30,000 milliseconds)
    }
    private int gameOver() {
//        Toast.makeText(this, "Jeu terminé! Votre score est: " + score, Toast.LENGTH_SHORT).show();
        finish();
        return score;
    }

    private void checkRowForThree(){
        for (int i=0; i<62; i++){
            int choseCandy = (int) candy.get(i).getTag();
            boolean isBlank = choseCandy == notCandy;
            Integer[] notValid = {6,7,14,15,22,23,30,31,38,39,46,47,54,55};
            List<Integer> list = Arrays.asList(notValid);
            if(!list.contains(i)){
                int x=i;
                if((int) candy.get(x++).getTag()==choseCandy && !isBlank &&
                        (int) candy.get(x++).getTag()==choseCandy &&
                        (int) candy.get(x).getTag()==choseCandy){
                    score += 3;
                    scoreResult.setText(String.valueOf(score));
                    candy.get(x).setImageResource(notCandy);
                    candy.get(x).setTag(notCandy);
                    x--;
                    candy.get(x).setImageResource(notCandy);
                    candy.get(x).setTag(notCandy);
                    x--;
                    candy.get(x).setImageResource(notCandy);
                    candy.get(x).setTag(notCandy);
                }
            }
        }
        moveDownCandies();
    }

    private void checkColumnForThree(){
        for (int i=0; i<47; i++){
            int choseCandy = (int) candy.get(i).getTag();
            boolean isBlank = choseCandy == notCandy;
            int x=i;
            if((int) candy.get(x).getTag()==choseCandy && !isBlank &&
                    (int) candy.get(x+noOfBlock).getTag()==choseCandy &&
                    (int) candy.get(x+2*noOfBlock).getTag()==choseCandy){
                score += 3;
                scoreResult.setText(String.valueOf(score));
                candy.get(x).setImageResource(notCandy);
                candy.get(x).setTag(notCandy);
                x = x + noOfBlock;
                candy.get(x).setImageResource(notCandy);
                candy.get(x).setTag(notCandy);
                x = x + noOfBlock;
                candy.get(x).setImageResource(notCandy);
                candy.get(x).setTag(notCandy);
            }
        }
        moveDownCandies();
    }

    private void moveDownCandies(){
        Integer[] firstRow = {1,2,3,4,5,6,7};
        List<Integer> arrayList = Arrays.asList(firstRow);
        for(int i = 55; i >= 0; i--){
            if((int) candy.get(i+noOfBlock).getTag() == notCandy){
                candy.get(i+noOfBlock).setImageResource((int) candy.get(i).getTag());
                candy.get(i+noOfBlock).setTag(candy.get(i).getTag());
                candy.get(i).setImageResource(notCandy);
                candy.get(i).setTag(notCandy);

                if(arrayList.contains(i) && (int) candy.get(i).getTag() == notCandy){
                    int randomColor = (int) Math.floor(Math.random() * candies.length);
                    candy.get(i).setImageResource(candies[randomColor]);
                    candy.get(i).setTag(candies[randomColor]);
                }
            }
        }
        for(int i = 0; i < 8; i++){
            if((int) candy.get(i).getTag() == notCandy){
                int randomColor = (int) Math.floor(Math.random() * candies.length);
                candy.get(i).setImageResource(candies[randomColor]);
                candy.get(i).setTag(candies[randomColor]);
            }
        }
    }

    Runnable reapeatChecker = new Runnable() {
        @Override
        public void run() {
            try {
                checkRowForThree();
                checkColumnForThree();
                moveDownCandies();
            }
            finally {
                handler.postDelayed(reapeatChecker,interval);
            }
        }
    };

    void startRepeat(){
        reapeatChecker.run();
    }
    private void candyInterchange(){
        int background = (int) candy.get(candyToBeReplace).getTag();
        int background1 = (int) candy.get(candyToBeDragged).getTag();
        candy.get(candyToBeDragged).setImageResource(background);
        candy.get(candyToBeReplace).setImageResource(background1);
        candy.get(candyToBeDragged).setTag(background);
        candy.get(candyToBeReplace).setTag(background1);
    }

    private void createBoard() {
        gridLayout = findViewById(R.id.board);
        gridLayout.setRowCount(noOfBlock);
        gridLayout.setColumnCount(noOfBlock);
        // set up square
        gridLayout.getLayoutParams().width = widthOfScreen;
        gridLayout.getLayoutParams().height = widthOfScreen ;
        for(int i = 0; i< noOfBlock*noOfBlock; i++){
            ImageView imageView = new ImageView(this);
            imageView.setId(i);
            imageView.setLayoutParams(new android.view.ViewGroup.LayoutParams(widthOfBlock, widthOfBlock));
            imageView.setMaxHeight(widthOfBlock);
            imageView.setMaxWidth(widthOfBlock);
            //create value random
            int randomCandy = (int) Math.floor(Math.random() * candies.length);
            imageView.setImageResource(candies[randomCandy]);
            imageView.setTag(candies[randomCandy]);
            candy.add(imageView);
            gridLayout.addView(imageView);
        }
    }
    protected void onDestroy() {
        super.onDestroy();
        }
}