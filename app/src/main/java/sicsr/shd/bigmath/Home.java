// 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 github token
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
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import android.util.Log;


public class Home extends AppCompatActivity {
    //    UI components
    private ImageView set;
    private Button singlePlayerBtn, multiPlayerBtn;
    private TextView quoteView;
    private TextView pointsView;
    //    Logic variables
    private String mode;
    static MediaPlayer player;
    float fxVol;
    private Intent pickGameActivity;
    Vibrator vibrator;
    private final String SETTINGS = "1";

    // set math related quote
    private void setQuote() {
        // selecting a random quote from strings resources
        int[] quoteData = {R.string.quote1, R.string.quote2, R.string.quote3, R.string.quote4, R.string.quote5,
                R.string.quote6, R.string.quote7, R.string.quote8, R.string.quote9, R.string.quote10};
        int randomIndex = new Random().nextInt(10);
        String quote = getString(quoteData[randomIndex]);
        quoteView.setText(quote);
    }

    private void makeMusic(SharedPreferences pf, SharedPreferences.Editor pfEditor) {
        String statusSwitch = pf.getString("MUSIC_ON_OFF", "ON");

        if (statusSwitch.equals("ON")) {
            player = MediaPlayer.create(getApplicationContext(), R.raw.iono_home);
            player.setLooping(true);
            player.start();
        }
    }

    private void vibrate(SharedPreferences pf) {
        if (vibrator.hasVibrator()) {
            if ((pf.getString("VIB_ON_OFF", "ON").equals("ON"))) {
                vibrator.vibrate(250);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toast.makeText(getApplicationContext(), getString(R.string.app_version) + "", Toast.LENGTH_SHORT).show();

        quoteView = findViewById(R.id.quoteView);
        singlePlayerBtn = findViewById(R.id.sinPlBtn);
        pointsView = findViewById(R.id.totPointsTv);
        multiPlayerBtn = findViewById(R.id.mulPlBtn);
        set = findViewById(R.id.settings);

        // Create touch event handler and attach to TextView
        BigMathTouchEvents touchEventHandler = new BigMathTouchEvents(this);
        ConstraintLayout touch = findViewById(R.id.layout_home);
        touch.setOnTouchListener(touchEventHandler);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        SharedPreferences preferences = getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        String totDisplay = preferences.getString("FIN_TOT_SCORE", "0") + " pts.";
        pointsView.setText(totDisplay);

        // start background music
        makeMusic(preferences, editor);


        SoundPool fxPlayer = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        int perc = fxPlayer.load(this, R.raw.perc, 1);
        int perc_snap = fxPlayer.load(this, R.raw.perc_snap, 1);

        // set math quote
        setQuote();

        if (preferences.getString("FX_ON_OFF", "ON").equals("ON")) {
            fxVol = 1.0f;
        } else {
            fxVol = 0.0f;
        }
        // settings button
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fxPlayer.play(perc_snap, fxVol, fxVol, 1, 0, fxVol);
                try {
                    if (player.isPlaying()) {
                        player.stop();
                        player.release();
                    } else {
                        Toast.makeText(Home.this, "Hmm", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("HOME", "An error occurred: ", e);
                    Log.e("HOME", "The error occurred at line: " + e.getStackTrace()[0].getLineNumber() + " in " + e.getStackTrace()[0].getFileName(), e);
                }
                Intent i = new Intent(getApplicationContext(), Settings.class);
                startActivity(i);
                finish();
            }
        });

        // SinglePlayer mode
        singlePlayerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrate(preferences);
                fxPlayer.play(perc, fxVol, fxVol, 1, 0, 1.0f);

                mode = "0";

                pickGameActivity = new Intent(getApplicationContext(), SinglChooseGame.class);
                pickGameActivity.putExtra("MODE", mode);
                startActivity(pickGameActivity);
            }
        });

        // Multiplayer mode
        multiPlayerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrate(preferences);
                fxPlayer.play(perc, fxVol, fxVol, 1, 0, 1.0f);
                mode = "1";
                Toast.makeText(getApplicationContext(), "Hold on tight while we work on this!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
