package sicsr.shd.bigmath;

import android.os.CountDownTimer;

public class BigMathTimer {

    private CountDownTimer countDownTimer;
    private long timeRemaining;

    public BigMathTimer(long timeInMillis) {
        this.timeRemaining = timeInMillis;
        this.countDownTimer = new CountDownTimer(timeInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                timeRemaining = 0;
            }
        };
    }

    public void start() {
        countDownTimer.start();
    }

    public void pause() {
        countDownTimer.cancel();
    }

    public void resume() {
        countDownTimer = new CountDownTimer(timeRemaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                timeRemaining = 0;
            }
        };
        countDownTimer.start();
    }

    public long getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(long timeRemaining) {
        this.timeRemaining = timeRemaining;
    }
}


