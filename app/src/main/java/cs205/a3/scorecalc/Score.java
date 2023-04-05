package cs205.a3.scorecalc;

/**
 * Object to store information related to score, such as its ID, the score, the associated
 * username and the associated song ID.
 */
public class Score {
    private String id;
    private long score;
    private String name;
    private String songId;

    public Score(String id, long score, String name, String songId) {
        this.id = id;
        this.score = score;
        this.name = name;
        this.songId = songId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }
}
