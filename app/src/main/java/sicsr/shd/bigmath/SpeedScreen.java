package sicsr.shd.bigmath;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

public class SpeedScreen extends AppCompatActivity {
    //    UI Components
    private TextView back;
    private TextView gameNameTv;
    private MaterialButton rap;
    private MaterialButton sud;
    private MaterialButton cas;
    private Button apply;
    //    Logic components
    Boolean fire;
    Boolean death;
    Boolean lazy;
    SoundPool fxPlayer;
    float fxVol;
    private final String SETTINGS = "1";

    protected void startAct(String mode) {
//        fxPlayer.play(sword, fxVol, fxVol, 1, 0, 1.0f);
        if (mode.equals("GTN")) {
            Intent j = new Intent(getApplicationContext(), GuessTheNumber.class);
            if (fire) {
                j.putExtra("SPEED", "RAPID_FIRE");
            }
            if (death) {
                j.putExtra("SPEED", "SUDDEN_DEATH");
            }
            if (lazy) {
                j.putExtra("SPEED", "CASUAL");
            }
            startActivity((j));
        } else if (mode.equals("QC")) {
            Intent j = new Intent(getApplicationContext(), QuickCalc.class);
            if (fire) {
                j.putExtra("SPEED", "RAPID_FIRE");
            }
            if (death) {
                j.putExtra("SPEED", "SUDDEN_DEATH");
            }
            if (lazy) {
                j.putExtra("SPEED", "CASUAL");
            }
            startActivity((j));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_screen);

        back = findViewById(R.id.backBtnSett3);
        rap = findViewById(R.id.rapBtn);
        sud = findViewById(R.id.sudBtn);
        cas = findViewById(R.id.casBtn);
        gameNameTv = findViewById(R.id.gameNameTv);
        apply = findViewById(R.id.startBtn);

        BigMathTouchEvents touchEventHandler = new BigMathTouchEvents(this);
        ConstraintLayout touch = findViewById(R.id.layout_speedScreen);
        touch.setOnTouchListener(touchEventHandler);

        SharedPreferences preferences = getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        if (preferences.getString("FX_ON_OFF", "ON").equals("ON")) {
            fxVol = 1.0f;
        } else {
            fxVol = 0.0f;
        }

        SoundPool fxPlayer = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        int perc_snap = fxPlayer.load(this, R.raw.perc_snap, 1);
        int sword = fxPlayer.load(this, R.raw.sword, 1);
        int kalimba_c = fxPlayer.load(this, R.raw.kalimba_c, 1);
        int kalimba_d = fxPlayer.load(this, R.raw.kalimba_d, 1);
        int kalimba_e = fxPlayer.load(this, R.raw.kalimba_e, 1);

        Intent j = getIntent();
        String mode = j.getExtras().getString("GAME_MODE", null);

        if (mode.equals("GTN")) {
            rap.setText(R.string.rapidGTN);
            sud.setText(R.string.suddenGTN);
            cas.setText(R.string.casGTN);
            gameNameTv.setText(getString(R.string.gtn));}
        if (mode.equals("QC")) {
            rap.setText(R.string.rapidQC);
            sud.setText(R.string.suddenQC);
            cas.setText(R.string.casQC);
            gameNameTv.setText(getString(R.string.calc));

        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fxPlayer.play(perc_snap, fxVol, fxVol, 1, 0, 1.0f);
                finish();
            }
        });
        apply.setEnabled(false);
        rap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fxPlayer.play(kalimba_c, fxVol, fxVol, 1, 0, 1.0f);
                rap.setTextColor(getResources().getColor(R.color.amaura));
                rap.setIconTintResource((R.color.amaura));
                cas.setTextColor(getResources().getColor(R.color.heatran));
                cas.setIconTintResource((R.color.heatran));
                sud.setTextColor(getResources().getColor(R.color.heatran));
                sud.setIconTintResource((R.color.heatran));
                apply.setEnabled(true);
                fire = true;
                lazy = false;
                death = false;

                fxPlayer.play(sword, fxVol, fxVol, 1, 0, 1.0f);
                startAct(mode);
            }
        });
        sud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fxPlayer.play(kalimba_d, fxVol, fxVol, 1, 0, 1.0f);
                sud.setTextColor(getResources().getColor(R.color.amaura));
                sud.setIconTintResource((R.color.amaura));
                rap.setTextColor(getResources().getColor(R.color.heatran));
                rap.setIconTintResource((R.color.heatran));
                cas.setTextColor(getResources().getColor(R.color.heatran));
                cas.setIconTintResource((R.color.heatran));
                apply.setEnabled(true);
                death = true;
                fire = false;
                lazy = false;
                fxPlayer.play(sword, fxVol, fxVol, 1, 0, 1.0f);
                startAct(mode);

            }
        });
        cas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fxPlayer.play(kalimba_e, fxVol, fxVol, 1, 0, 1.0f);

                cas.setTextColor(getResources().getColor(R.color.amaura));
                cas.setIconTintResource((R.color.amaura));
                rap.setTextColor(getResources().getColor(R.color.heatran));
                rap.setIconTintResource((R.color.heatran));
                sud.setTextColor(getResources().getColor(R.color.heatran));
                sud.setIconTintResource((R.color.heatran));
                apply.setEnabled(true);
                lazy = true;
                fire = false;
                death = false;
                fxPlayer.play(sword, fxVol, fxVol, 1, 0, 1.0f);
                startAct(mode);

            }
        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fxPlayer.play(sword, fxVol, fxVol, 1, 0, 1.0f);
                Intent i = getIntent();
                String mode = i.getExtras().getString("GAME_MODE");
                if (mode.equals("GTN")) {
                    Intent j = new Intent(getApplicationContext(), GuessTheNumber.class);
                    if (fire) {
                        j.putExtra("SPEED", "RAPID_FIRE");
                    }
                    if (death) {
                        j.putExtra("SPEED", "SUDDEN_DEATH");
                    }
                    if (lazy) {
                        j.putExtra("SPEED", "CASUAL");
                    }
                    startActivity((j));
                } else if (mode.equals("QC")) {
                    Intent j = new Intent(getApplicationContext(), QuickCalc.class);
                    if (fire) {
                        j.putExtra("SPEED", "RAPID_FIRE");
                    }
                    if (death) {
                        j.putExtra("SPEED", "SUDDEN_DEATH");
                    }
                    if (lazy) {
                        j.putExtra("SPEED", "CASUAL");
                    }
                    startActivity((j));
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}