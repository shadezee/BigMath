package sicsr.shd.bigmath;

import android.app.Application;

import androidx.room.Room;

import sicsr.shd.bigmath.dataUtils.BigMathDatabase;

public class DbaseImplementation extends Application {
    private static DbaseImplementation someInstance;
    private BigMathDatabase dbInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        someInstance = this;
        dbInstance = Room.databaseBuilder(getApplicationContext(), BigMathDatabase.class, "game_scores").build();

    }

    public static DbaseImplementation getInstance() {
        return someInstance;
    }

    public BigMathDatabase getDbInstance() {
        return dbInstance;
    }

}
