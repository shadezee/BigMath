package sicsr.shd.bigmath;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class BigMathTouchEvents implements View.OnTouchListener{
    private GestureDetector gestureDetector;

    public BigMathTouchEvents(Context context) {
        gestureDetector = new GestureDetector(context, new BigMathGestureListener());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
}
