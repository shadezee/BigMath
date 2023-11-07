package sicsr.shd.bigmath.dataUtils;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {BigMathScoresDB.class}, version = 1)
public abstract class BigMathDatabase extends RoomDatabase {
    public abstract BigMathDao scoreDao();
}
