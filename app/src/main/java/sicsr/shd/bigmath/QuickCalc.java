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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.button.MaterialButton;

import java.util.Locale;
import java.util.Random;

public class QuickCalc extends AppCompatActivity {
    //    UI Components
    TextView backBtn;
    TextView helpBtn;
    TextView timerTv;
    TextView quesTv;
    TextView negTv;
    TextView negBtn;
    TextView enteredTv;
    TextView rightWrongTv;
    Button num0, num1, num2, num3, num4, num5, num6, num7, num8, num9;
    MaterialButton backspace;
    Button numSend;
    ImageView star, star1, star2, star3;
    // logical components
    private final int[] secretArray = new int[4];
    private int val1;
    private int val2;
    private int secretNum;
    int totScore;
    private char operation;
    private final int lower = 11;
    private final int upper = 99;
    String text;
    private String enteredNum;
    private String setSpeed;
    int quesNo;
    int gotRight = 0;
    int gotWrong = 0;
    int score = 0;
    private CountDownTimer timer;
    boolean isRapid = false;
    private boolean timerRunning;
    private long timeLeftInMillis;
    MediaPlayer player, present_player;
    SoundPool fxPlayer;
    int perc, perc_snap, sword, iono_break, iono_end;
    private final int rapidTime = 180000;
    private final int suddenTime = 50000;
    private final String SETTINGS = "1";
    SharedPreferences preferences;


    float fxVol;

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
            star.setColorFilter(Color.RED);
            star1.setColorFilter(Color.RED);
            star2.setColorFilter(Color.RED);
            star3.setColorFilter(Color.RED);

        } else {
            star.setColorFilter(Color.GREEN);
            star1.setColorFilter(Color.GREEN);
            star2.setColorFilter(Color.GREEN);
            star3.setColorFilter(Color.GREEN);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                star.setColorFilter(ogColor);
                star1.setColorFilter(ogColor);
                star2.setColorFilter(ogColor);
                star3.setColorFilter(ogColor);
            }
        }, 700);
    }

    protected int[] getSecrets() {
        char[] signs = {'x', '-', '+'};
        Random rm = new Random();

        val1 = rm.nextInt(upper - lower) + lower;
        val2 = rm.nextInt(upper - lower) + lower;
        int index = rm.nextInt(signs.length);
        operation = signs[index];

        if (operation == 'x') {
            secretNum = val1 * val2;
        } else if (operation == '-') {
            if (val2 > val1) {
                secretNum = val2 - val1;
            } else {
                secretNum = val1 - val2;
            }
        } else {
            secretNum = val1 + val2;
        }
        secretArray[0] = val1;
        secretArray[1] = val2;
        secretArray[2] = secretNum;
        return secretArray;
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

        getSecrets();

        String str1 = secretArray[0] + " ";
        String str2 = operation + " ";
        String str3 = secretArray[1] + " =";
        quesTv.setText(str1 + str2 + str3);
        Toast.makeText(getApplicationContext(), secretNum + "", Toast.LENGTH_SHORT).show();
    }

    protected void checkInput(String recieved, int answer) {
        String checkNeg = negTv.getText().toString();
        if (checkNeg.equals("-")) {
            recieved = checkNeg + recieved;
        }
        int receivedInt = Integer.parseInt(recieved);
        if (receivedInt == answer) {
            score = score + 100;
            gotRight++;
            rightWrongTv.setText(getString(R.string.correctAns));
            changeColor(1);
            getQuestion();
        } else {
            changeColor(0);
            rightWrongTv.setText(getString(R.string.wrongAns));
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

    private void finalMethod() {
        fxPlayer.play(iono_end, fxVol, fxVol, 1, 0, 1.0f);
        try {
            if (present_player.isPlaying()) {
                present_player.stop();
                present_player.release();
            }
        } catch (IllegalStateException | NullPointerException e) {

        }

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
        setContentView(R.layout.activity_quick_calc);

        // UI Components
        {
            star = findViewById(R.id.star);
            star1 = findViewById(R.id.star1);
            star2 = findViewById(R.id.star2);
            star3 = findViewById(R.id.star3);

            backBtn = findViewById(R.id.backBtnSett4);
            helpBtn = findViewById(R.id.helpButt);
            timerTv = findViewById(R.id.timerTv);
            quesTv = findViewById(R.id.quesTv);
            negTv = findViewById(R.id.negTv);
            enteredTv = findViewById(R.id.enteredTv);
            rightWrongTv = findViewById(R.id.righWrongTv);


            negBtn = findViewById(R.id.numNegative);
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
        ConstraintLayout touch = findViewById(R.id.layout_quickCalc);
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

        negBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (negTv.getText().toString().equals("-")) {
                    negTv.setText("");
                    negBtn.setTextColor((getResources().getColor(R.color.heatran)));
                    negBtn.setBackgroundColor((getResources().getColor(R.color.mankey)));
                } else {
                    negTv.setText("-");
                    negBtn.setBackgroundColor((getResources().getColor(R.color.heatran)));
                    negBtn.setTextColor((getResources().getColor(R.color.mankey)));
                }

            }
        });

        numSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fxPlayer.play(perc, fxVol, fxVol, 1, 0, 1.0f);
                if (enteredTv.length() > 0) {
                    enteredNum = (enteredTv.getText().toString());
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

                AlertDialog.Builder builder = new AlertDialog.Builder(QuickCalc.this);
                View v = getLayoutInflater().inflate(R.layout.help_dig, null);
                builder.setView(v);
                AlertDialog dialog = builder.create();

                Button done = v.findViewById(R.id.popClose);
                TextView help1 = v.findViewById(R.id.helpTv);
                TextView help2 = v.findViewById(R.id.helpTv1);
                TextView gameType = v.findViewById(R.id.gameType);
                String str2 = setSpeed + "";
                String gameTypeStr = "QuickCalc" + " - " + setSpeed;

                Window window = dialog.getWindow();
                if (window != null) {
                    window.setBackgroundDrawableResource(android.R.color.transparent);
                }
                gameType.setText(gameTypeStr);
                if (str2.equals("RAPID_FIRE")) {
                    help1.setText(R.string.qcRpd1);
                    help2.setText(R.string.qcRpd2);
                }
                if (str2.equals("SUDDEN_DEATH")) {
                    help1.setText(R.string.qcSud1);
                    help2.setText(R.string.qcSud2);
                }
                if (str2.equals("CASUAL")) {
                    help1.setText(R.string.qcCas1);
                    help2.setText(R.string.qcCas2);
                }

                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!setSpeed.equals("CASUAL")) {
                            resumeTimer();
                        }

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
                fxPlayer.play(perc_snap, 1.0f, 1.0f, 1, 0, 1.0f);

                AlertDialog.Builder builder = new AlertDialog.Builder(QuickCalc.this);
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
                    text = enteredTv.getText().toString();
                    enteredTv.setText(text + getString(R.string.num0));
                }
            });
            num1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    text = enteredTv.getText().toString();
                    enteredTv.setText(text + getString(R.string.num1));
                }
            });
            num2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    text = enteredTv.getText().toString();
                    enteredTv.setText(text + getString(R.string.num2));
                }
            });
            num3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    text = enteredTv.getText().toString();
                    enteredTv.setText(text + getString(R.string.num3));
                }
            });
            num4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    text = enteredTv.getText().toString();
                    enteredTv.setText(text + getString(R.string.num4));
                }
            });
            num5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    text = enteredTv.getText().toString();
                    enteredTv.setText(text + getString(R.string.num5));
                }
            });
            num6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    text = enteredTv.getText().toString();
                    enteredTv.setText(text + getString(R.string.num6));
                }
            });
            num7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    text = enteredTv.getText().toString();
                    enteredTv.setText(text + getString(R.string.num7));
                }
            });
            num8.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    text = enteredTv.getText().toString();
                    enteredTv.setText(text + getString(R.string.num8));
                }
            });
            num9.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    text = enteredTv.getText().toString();
                    enteredTv.setText(text + getString(R.string.num9));
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(QuickCalc.this);
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

                fxPlayer.play(sword, fxVol, fxVol, 1, 0, 1.0f);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}