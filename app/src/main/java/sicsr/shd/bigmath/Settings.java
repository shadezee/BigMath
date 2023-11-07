package sicsr.shd.bigmath;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;


public class Settings extends AppCompatActivity {
    //    UI components
    private TextView back;
    //        menu 1
    private SeekBar musVol;
    private SwitchCompat vibSw;
    private SwitchCompat muSw;
    private SwitchCompat fxSw;
    //      menu 2
    private RadioGroup rg;
    private RadioButton thX, thY, thZ;
    private Button applySet;

    //Logic components
    protected AudioManager audio_manager;

    //    Preference components
    private final String SETTINGS = "1";

    private void setSwitches(SharedPreferences pf) {
        String vib = pf.getString("VIB_ON_OFF", "ON");
        String mus = pf.getString("MUSIC_ON_OFF", "ON");
        String fx = pf.getString("FX_ON_OFF", "ON");

        vibSw.setChecked(vib.equals("ON"));
        muSw.setChecked(mus.equals("ON"));
        fxSw.setChecked(fx.equals("ON"));
    }

    private void changedSwitches(SharedPreferences.Editor pfEditor) {
        if (vibSw.isChecked()) {
            pfEditor.putString("VIB_ON_OFF", "ON");
        } else {
            pfEditor.putString("VIB_ON_OFF", "OFF");
        }
        if (muSw.isChecked()) {
            pfEditor.putString("MUSIC_ON_OFF", "ON");
        } else {
            pfEditor.putString("MUSIC_ON_OFF", "OFF");
        }
        if (fxSw.isChecked()) {
            pfEditor.putString("FX_ON_OFF", "ON");
        } else {
            pfEditor.putString("FX_ON_OFF", "OFF");
        }
        pfEditor.apply();
    }


    private void setVolume(SharedPreferences.Editor pfEditor, SeekBar volBar) {

        AudioManager audio_manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        volBar.setMax(audio_manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volBar.setProgress(audio_manager.getStreamVolume(AudioManager.STREAM_MUSIC));

        volBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                audio_manager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
                pfEditor.putInt("MUSIC_VOLUME", i);
                pfEditor.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // menu 1
        fxSw = findViewById(R.id.switchFx);
        vibSw = findViewById(R.id.switchVibrate);
        muSw = findViewById(R.id.switchMusic);
        back = findViewById(R.id.backBtnSett);
        musVol = findViewById(R.id.musicVolSeek);

        // menu 2
        rg = findViewById(R.id.themeGrp);
        thX = findViewById(R.id.rbX);
        thY = findViewById(R.id.rbY);
        thZ = findViewById(R.id.rbZ);

        BigMathTouchEvents touchEventHandler = new BigMathTouchEvents(this);
        ConstraintLayout touch = findViewById(R.id.layout_settings);
        touch.setOnTouchListener(touchEventHandler);


        SoundPool fxPlayer = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        int perc_snap = fxPlayer.load(this, R.raw.perc_snap, 1);
        int sword = fxPlayer.load(this, R.raw.sword, 1);


        // apply settings button
        applySet = findViewById(R.id.applySet);

        // allowing volume change
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // creating preference editors
        SharedPreferences preferences = getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // set volume value
        setVolume(editor, musVol);
        // initialize switches
        setSwitches(preferences);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fxPlayer.play(perc_snap, 1.0f, 1.0f, 1, 0, 1.0f);
                Intent i = new Intent(getApplicationContext(), Home.class);
                startActivity(i);
                finish();
            }
        });

        // on clicking apply
        applySet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set new switch values
                changedSwitches(editor);
                Intent i = new Intent(getApplicationContext(), Home.class);
                fxPlayer.play(sword, 1.0f, 1.0f, 1, 0, 1.0f);
                startActivity(i);
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), Home.class);
        startActivity(i);
        finish();
    }

}