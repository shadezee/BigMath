package sicsr.shd.bigmath.dataUtils;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "game_scores")
public class BigMathScoresDB {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "game")
    public String gameName;
    @ColumnInfo(name = "score")
    public int earnedScore;
    @ColumnInfo(name = "attempted")
    public int questions;
    public BigMathScoresDB(String gameName, int earnedScore, int questions) {
        this.gameName = gameName;
        this.earnedScore = earnedScore;
        this.questions = questions;
    }

    public void setId(int id) {
        this.id = id;
    }

}
