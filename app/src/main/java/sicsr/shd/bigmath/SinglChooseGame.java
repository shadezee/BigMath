package sicsr.shd.bigmath;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SinglChooseGame extends AppCompatActivity {
    //    UI components
    private TextView back;
    private Button toGtn;
    private Button toQuickCalc;
    // Logic components
    String mode;
    float fxVol;
    //    Shared preferences
    private final String SETTINGS = "1";
    MediaPlayer player;


    private void openSpeed(String mode) {
        Intent i = new Intent(getApplicationContext(), SpeedScreen.class);
        i.putExtra("GAME_MODE", mode);
        try {
            if (player.isPlaying()) {
                player.stop();
                player.release();
            }
        } catch (NullPointerException | IllegalStateException e) {
        }
        startActivity(i);
    }

    private void makeMusic(SharedPreferences pf, SharedPreferences.Editor pfEditor) {
        String statusSwitch = pf.getString("MUSIC_ON_OFF", "ON");
        if (statusSwitch.equals("ON")) {
            player = MediaPlayer.create(getApplicationContext(), R.raw.iono_home);
            player.setLooping(true);
            player.start();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singl_choose_game);

        back = findViewById(R.id.backBtnSett2);
        toGtn = findViewById(R.id.gtn);
        toQuickCalc = findViewById(R.id.qc);

        BigMathTouchEvents touchEventHandler = new BigMathTouchEvents(this);
        ConstraintLayout touch = findViewById(R.id.layout_singleChooseGame);
        touch.setOnTouchListener(touchEventHandler);

        SharedPreferences preferences = getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        if (preferences.getString("FX_ON_OFF", "ON").equals("ON")) {
            fxVol = 1.0f;
        } else {
            fxVol = 0.0f;
        }


        SoundPool fxPlayer = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        int perc = fxPlayer.load(this, R.raw.perc, 1);
        int perc_snap = fxPlayer.load(this, R.raw.perc_snap, 1);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fxPlayer.play(perc_snap, fxVol, fxVol, 1, 0, 1.0f);

                try {
                    if (player.isPlaying()) {
                        player.stop();
                        player.release();
                    }
                } catch (NullPointerException n) {

                } catch (IllegalStateException i) {

                }
                Intent i = new Intent(getApplicationContext(), Home.class);
                startActivity(i);
                finish();
            }
        });

        toGtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fxPlayer.play(perc, fxVol, fxVol, 1, 0, 1.0f);
                openSpeed("GTN")
                ;
            }
        });

        toQuickCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fxPlayer.play(perc, fxVol, fxVol, 1, 0, 1.0f);
                openSpeed("QC");
            }
        });
    }

    @Override
    public void onBackPressed() {
        try {
            if (player.isPlaying()) {
                player.stop();
                player.release();
            }
        } catch (Exception n) {
            Log.e("SinglChooseGame", "An error occurred: ", n);
        }

        Intent i = new Intent(getApplicationContext(), Home.class);
        startActivity(i);
        finish();
    }

}