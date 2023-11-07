package sicsr.shd.bigmath;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    TextView tv;
    Vibrator vibrator;
    private final String SETTINGS = "1";

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
        // below line eradicates dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        Handler handle = new Handler();
        SharedPreferences preferences = getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        vibrate(preferences);
        // go to home page to pick single or multiplayer style after 5 seconds of logo screen
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                toHome();
            }
        }, 3000);

    }

    private void toHome() {
        Intent toHome = new Intent(getApplicationContext(), Home.class);
        toHome.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(toHome);
        finish();
    }
}