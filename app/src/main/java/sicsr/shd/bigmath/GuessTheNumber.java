package sicsr.shd.bigmath;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.room.Room;

import com.google.android.material.button.MaterialButton;

import java.util.Locale;
import java.util.Random;

import sicsr.shd.bigmath.dataUtils.BigMathDatabase;
import sicsr.shd.bigmath.dataUtils.BigMathScoresDB;

public class GuessTheNumber extends AppCompatActivity {
    //    UI Components
    TextView backBtn;
    MaterialButton backspace;
    TextView helpBtn;
    TextView timerTv;
    TextView quesTv;
    TextView enteredTv;
    Button num0, num1, num2, num3, num4, num5, num6, num7, num8, num9;
//    Button backspace;
    Button numSend;
    ImageView star1, star2, star3;
    //    Logical components
    private CountDownTimer timer;
    boolean isRapid = false;
    private boolean timerRunning;
    private long timeLeftInMillis;
    private String text; // Globally used common variable
    private String numCate;
    private final int[] secretArray = new int[4];
    private String question;
    private int secretNum = 0;
    private int val1 = 0;
    private int val2 = 0;
    private int sumDig = 0;
    int quesNo;
    int gotRight = 0;
    int gotWrong = 0;
    int score = 0;
    int totScore;
    int perc, perc_snap, sword, iono_break, iono_end;
    private int enteredNum = 0;
    private final int rapidTime = 90000;
    private final int suddenTime = 25000;
    SoundPool fxPlayer;
    float fxVol;
    MediaPlayer player;
    MediaPlayer present_player;
    private String setSpeed;
    // Shared Preference
    private SharedPreferences preferences;
    private final String SETTINGS = "1";


    protected void newMusic() {
        player = Home.player;
        try {
            if (player != null && player.isPlaying()) {
                player.stop();
                player.release();
            }
            fxPlayer.play(iono_break, fxVol, fxVol, 1, 1, 1.0f);
            present_player = MediaPlayer.create(getApplicationContext(), R.raw.iono_main);
            present_player.setLooping(true);
            present_player.start();
        } catch (IllegalStateException e) {
        }
    }

    protected void changeColor(int i) {
        ColorFilter ogColor = star1.getColorFilter();

        if (i == 0) {
            star1.setColorFilter(Color.RED);
            star2.setColorFilter(Color.RED);
            star3.setColorFilter(Color.RED);

        } else {
            star1.setColorFilter(Color.GREEN);
            star2.setColorFilter(Color.GREEN);
            star3.setColorFilter(Color.GREEN);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                star1.setColorFilter(ogColor);
                star2.setColorFilter(ogColor);
                star3.setColorFilter(ogColor);
            }
        }, 700);
    }

    protected String getNumType(int secret) {
        if ((secret % 2) == 0) {
            numCate = ("Even.");
        } else {
            boolean isPrime = false;
            for (int i = 2; i <= secret / 2; ++i) {
                if (secret % i == 0) {
                    isPrime = true;
                    break;
                }
            }
            if (!isPrime) {
                numCate = "Odd.";
            } else {
                numCate = "Prime.";
            }
        }
        return numCate;
    }

    protected int getSumDigits(int secret) {
        sumDig = 0;
        while (secret > 0) {
            int digit = secret % 10;
            sumDig += digit;
            secret /= 10;
        }
        return sumDig;
    }

    protected void getQuestion() {

        quesNo++;
        if (setSpeed.equals("RAPID_FIRE")) {
            if (!isRapid) {
                startTimer(rapidTime);
                isRapid = true;
            }

        }
        if (setSpeed.equals("SUDDEN_DEATH")) {
            try {
                pauseTimer();
            } catch (NullPointerException n) {
            }
            startTimer(suddenTime);
        }

        // get secrets
        getSecretNumbers();
        val1 = secretArray[0];
        val2 = secretArray[1];
        secretNum = secretArray[2];

        // Testing purposes;
//        Toast.makeText(getApplicationContext(), secretNum + "", Toast.LENGTH_SHORT).show();

        // get hints
        numCate = getNumType(secretNum);
        sumDig = getSumDigits(secretNum);

        // set text to question
        String str1 = getString(R.string.ques_1) + " " + numCate + ".";
        String str2 = getString(R.string.ques_2) + "\n" + getString(R.string.ques_3) + " " + val1 + " " + getString(R.string.ques_4) + " " + val2 + ".";
        String str3 = getString(R.string.ques_5) + " " + sumDig + ".";
        question = str1 + "\n" + str2 + "\n" + str3 + "\n";
        quesTv.setText(question);
        fxPlayer.play(sword, fxVol, fxVol, 1, 0, 1.0f);

    }

    protected int[] getSecretNumbers() {
        int[] array1 = {10, 20, 30, 40, 50};
        int[] array2 = {50, 60, 70, 80, 90};

        Random rand = new Random();
        int index = rand.nextInt(array1.length);

        int min = array1[index];

        int max = array2[index];

        secretNum = new Random().nextInt(max - min) + min;
        secretArray[0] = min;
        secretArray[1] = max;
        secretArray[2] = secretNum;
        return secretArray;
    }

    protected void checkInput(int recieved, int answer) {
        if (recieved == answer) {
            score = score + 10;
            gotRight++;
            changeColor(1);
            enteredTv.setText(R.string.correctAns);
            getQuestion();
        } else {
            changeColor(0);
            enteredTv.setText(R.string.wrongAns);
            gotWrong++;
            if (setSpeed.equals("RAPID_FIRE")) {
                getQuestion();
            }
            if ((setSpeed.equals("SUDDEN_DEATH"))) {
                pauseTimer();
                finalMethod();
            }
        }
    }

    protected String filterEnteredText(String textOnEnteredTv) {
        if (textOnEnteredTv.equals(getString(R.string.correctAns)) | textOnEnteredTv.equals(getString(R.string.wrongAns))) {
            textOnEnteredTv = "";
        }
        return textOnEnteredTv;
    }

    private void finalMethod() {
        fxPlayer.play(iono_end, fxVol, fxVol, 1, 0, 1.0f);
        try {
            if (present_player.isPlaying()) {
                present_player.stop();
                present_player.release();
            }
        } catch (IllegalStateException | NullPointerException e) {
        }

        // database code
        new Thread(new Runnable() {
            @Override
            public void run() {
                BigMathDatabase dBase = Room.databaseBuilder(getApplicationContext(), BigMathDatabase.class, "game_scores").build();
                BigMathScoresDB obj = new BigMathScoresDB("GuessTheNumber", score, quesNo);
                dBase.scoreDao().insert(obj);
                totScore = dBase.scoreDao().getTotalScore();
            }
        });

        SharedPreferences.Editor prefEditor = preferences.edit();
        int oldScore = Integer.parseInt(preferences.getString("FIN_TOT_SCORE", "0"));
        totScore = score + oldScore;
        prefEditor.putString("FIN_TOT_SCORE", totScore + "");
        prefEditor.apply();

        Intent i = new Intent(getApplicationContext(), ResultActivity.class);
        i.putExtra("GOTTEN_RIGHT", gotRight + "");
        i.putExtra("TOTAL_QUES", quesNo + "");
        i.putExtra("SCORE", score + "");
        i.putExtra("TOT_SCORE", totScore + "");

        i.putExtra("GAME_PLAYED", ("GuessTheNumber" + " - " + setSpeed));
        startActivity(i);
        finish();

    }

    private void updateTimer() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d %02d", minutes, seconds);
        timerTv.setText(timeLeftFormatted);
    }

    private void pauseTimer() {
        timer.cancel();
        timerRunning = false;
    }

    protected void resumeTimer() {
        startTimer(timeLeftInMillis);
        timerRunning = true;
    }

    private void startTimer(long durationInMillis) {
        if (timer != null) {
            timer.cancel();
        }

        timer = new CountDownTimer(durationInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                finalMethod();
            }
        }.start();

        timerRunning = true;
    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_the_number);
        // UI Components
        {
            star1 = findViewById(R.id.star1);
            star2 = findViewById(R.id.star2);
            star3 = findViewById(R.id.star3);

            backBtn = findViewById(R.id.backBtnSett4);
            helpBtn = findViewById(R.id.helpButt);
            timerTv = findViewById(R.id.timerTv);
            quesTv = findViewById(R.id.quesTv);
            enteredTv = findViewById(R.id.enteredTv);

            num0 = findViewById(R.id.num0);
            num1 = findViewById(R.id.num1);
            num2 = findViewById(R.id.num2);
            num3 = findViewById(R.id.num3);
            num4 = findViewById(R.id.num4);
            num5 = findViewById(R.id.num5);
            num6 = findViewById(R.id.num6);
            num7 = findViewById(R.id.num7);
            num8 = findViewById(R.id.num8);
            num9 = findViewById(R.id.num9);
            backspace = findViewById(R.id.backspbutt);
            numSend = findViewById(R.id.enterButt);
        }

        BigMathTouchEvents touchEventHandler = new BigMathTouchEvents(this);
        ConstraintLayout touch = findViewById(R.id.layout_guessTheNumber);
        touch.setOnTouchListener(touchEventHandler);

        preferences = getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        String statusSwitch = preferences.getString("MUSIC_ON_OFF", "ON");
        if (preferences.getString("FX_ON_OFF", "ON").equals("ON")) {
            fxVol = 1.0f;
        } else {
            fxVol = 0.0f;
        }

        fxPlayer = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        perc = fxPlayer.load(this, R.raw.perc, 1);
        perc_snap = fxPlayer.load(this, R.raw.perc_snap, 1);
        sword = fxPlayer.load(this, R.raw.sword, 1);
        iono_break = fxPlayer.load(this, R.raw.iono_break, 1);
        iono_end = fxPlayer.load(this, R.raw.iono_end, 1);

        if (statusSwitch.equals("ON")) {
            newMusic();
        }

        Intent j = getIntent();
        setSpeed = j.getExtras().getString("SPEED");

        quesNo = 0;

        getQuestion();

        numSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fxPlayer.play(perc, fxVol, fxVol, 1, 0, 1.0f);

                if (enteredTv.length() > 0) {
                    enteredNum = Integer.parseInt(enteredTv.getText().toString());
                    enteredTv.setText(null);
                    checkInput(enteredNum, secretNum);
                }
            }
        });
        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!setSpeed.equals("CASUAL")) {
                    pauseTimer();
                }
                fxPlayer.play(perc_snap, fxVol, fxVol, 1, 0, 1.0f);

                AlertDialog.Builder builder = new AlertDialog.Builder(GuessTheNumber.this);
                View v = getLayoutInflater().inflate(R.layout.help_dig, null);
                builder.setView(v);
                AlertDialog dialog = builder.create();

                Button done = v.findViewById(R.id.popClose);
                TextView help1 = v.findViewById(R.id.helpTv);
                TextView help2 = v.findViewById(R.id.helpTv1);
                TextView gameType = v.findViewById(R.id.gameType);
                String str2 = setSpeed + "";
                String gameTypeStr = "GuessTheNumber" + " - " + setSpeed;

                Window window = dialog.getWindow();
                if (window != null) {
                    window.setBackgroundDrawableResource(android.R.color.transparent);
                }
                gameType.setText(gameTypeStr);
                if (str2.equals("RAPID_FIRE")) {
                    help1.setText(R.string.gtnRpd1);
                    help2.setText(R.string.gtnRpd2);
                }
                if (str2.equals("SUDDEN_DEATH")) {
                    help1.setText(R.string.gtnSud1);
                    help2.setText(R.string.gtnSud2);
                }
                if (str2.equals("CASUAL")) {
                    help1.setText(R.string.gtnCas1);
                    help2.setText(R.string.gtnCas2);
                }

                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        resumeTimer();
                        fxPlayer.play(sword, fxVol, fxVol, 1, 0, 1.0f);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!setSpeed.equals("CASUAL")) {
                    pauseTimer();
                }
                fxPlayer.play(perc_snap, fxVol, fxVol, 1, 0, 1.0f);

                AlertDialog.Builder builder = new AlertDialog.Builder(GuessTheNumber.this);
                View v = getLayoutInflater().inflate(R.layout.quit_dlg, null);
                builder.setView(v);
                AlertDialog dialog = builder.create();

                Button quitYes = v.findViewById(R.id.qtYes);
                Button quitNo = v.findViewById(R.id.qtNo);
                Window window = dialog.getWindow();
                if (window != null) {
                    window.setBackgroundDrawableResource(android.R.color.transparent);
                }

                quitYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fxPlayer.play(sword, fxVol, fxVol, 1, 0, 1.0f);
                        dialog.dismiss();
                        finish();
                    }
                });

                quitNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        resumeTimer();
                        fxPlayer.play(sword, fxVol, fxVol, 1, 0, 1.0f);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    text = enteredTv.getText().toString();
                    int indText = text.length();
                    String last = "" + (text.charAt((indText) - 1));
                    String space = "";
                    enteredTv.setText((text.replace(last, space)) + "");
                } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                }
            }
        });
        // num pad
        {
            num0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    text = filterEnteredText(enteredTv.getText().toString());
                    enteredTv.setText(text + getString(R.string.num0));
                }
            });
            num1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    text = filterEnteredText(enteredTv.getText().toString());
                    enteredTv.setText(text + getString(R.string.num1));
                }
            });
            num2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    text = filterEnteredText(enteredTv.getText().toString());
                    enteredTv.setText(text + getString(R.string.num2));
                }
            });
            num3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    text = filterEnteredText(enteredTv.getText().toString());
                    enteredTv.setText(text + getString(R.string.num3));
                }
            });
            num4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    text = filterEnteredText(enteredTv.getText().toString());
                    enteredTv.setText(text + getString(R.string.num4));
                }
            });
            num5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    text = filterEnteredText(enteredTv.getText().toString());
                    enteredTv.setText(text + getString(R.string.num5));
                }
            });
            num6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    text = filterEnteredText(enteredTv.getText().toString());
                    enteredTv.setText(text + getString(R.string.num6));
                }
            });
            num7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    text = filterEnteredText(enteredTv.getText().toString());
                    enteredTv.setText(text + getString(R.string.num7));
                }
            });
            num8.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    text = filterEnteredText(enteredTv.getText().toString());
                    enteredTv.setText(text + getString(R.string.num8));
                }
            });
            num9.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    text = filterEnteredText(enteredTv.getText().toString());
                    enteredTv.setText(text + getString(R.string.num9));
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GuessTheNumber.this);
        View v = getLayoutInflater().inflate(R.layout.quit_dlg, null);
        builder.setView(v);
        AlertDialog dialog = builder.create();

        Button quitYes = v.findViewById(R.id.qtYes);
        Button quitNo = v.findViewById(R.id.qtNo);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }

        quitYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fxPlayer.play(sword, fxVol, fxVol, 1, 0, 1.0f);
                dialog.dismiss();
                finish();
            }
        });

        quitNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resumeTimer();
                fxPlayer.play(sword, fxVol, fxVol, 1, 0, 1.0f);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}