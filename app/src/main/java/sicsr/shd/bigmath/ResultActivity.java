package sicsr.shd.bigmath;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {
    TextView game;
    TextView ques;
    TextView right;
    TextView score;
    TextView totScore;
    Button ok;
    Button rep;

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        game = findViewById(R.id.rsltGmType);
        ques = findViewById(R.id.attemptTv);
        right = findViewById(R.id.correctTv);
        score = findViewById(R.id.scoreTv);
        totScore = findViewById(R.id.totScoreTv);
        ok = findViewById(R.id.okBut);
        rep = findViewById(R.id.repBut);

        BigMathTouchEvents touchEventHandler = new BigMathTouchEvents(this);
        ConstraintLayout touch = findViewById(R.id.layout_result);
        touch.setOnTouchListener(touchEventHandler);

        Intent i = getIntent();
        String gotRight = i.getExtras().getString("GOTTEN_RIGHT", null);
        String totQues = i.getExtras().getString("TOTAL_QUES", null);
        String gotScore = i.getExtras().getString("SCORE", null);
        String receivedTotScore = i.getExtras().getString("TOT_SCORE", null);
        String gamePlayed = i.getExtras().getString("GAME_PLAYED", null);


        game.setText(gamePlayed);
        ques.setText(getString(R.string.attempted) + " " + totQues);
        right.setText(getString(R.string.correct) + " " + gotRight);
        score.setText(getString(R.string.score) + " " + gotScore);
        totScore.setText(getString(R.string.totScore) + " " + receivedTotScore);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Home.class);
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