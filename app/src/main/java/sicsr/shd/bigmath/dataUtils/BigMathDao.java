package sicsr.shd.bigmath.dataUtils;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BigMathDao {
    @Query("SELECT COUNT(*) FROM game_scores")
    int getCount();

    @Query("SELECT * FROM game_scores")
    List<BigMathScoresDB> getAll();

    @Query("SELECT SUM(score) FROM game_scores")
    int getTotalScore();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(BigMathScoresDB gameScore);

    @Update
    void update(BigMathScoresDB gameScore);

    @Delete
    void delete(BigMathScoresDB gameScore);
}
