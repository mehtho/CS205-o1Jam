package cs205.a3.scorecalc;

public class Score {
    private long score;
    private String name;
    private String songId;

    public Score(long score, String name, String songId) {
        this.score = score;
        this.name = name;
        this.songId = songId;
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
