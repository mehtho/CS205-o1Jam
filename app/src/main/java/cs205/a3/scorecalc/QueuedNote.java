package cs205.a3.scorecalc;

/**
 * Object storing information for a note that has been loaded for a song but not yet displayed
 * because it is too early in the song.
 *
 * Stores info such as time and lane.
 */
public class QueuedNote implements Comparable<QueuedNote> {
    private final long time;
    private final int lane;

    public QueuedNote(long time, int lane) {
        this.time = time;
        this.lane = lane;
    }

    public long getTime() {
        return time;
    }

    public int getLane() {
        return lane;
    }

    @Override
    public int compareTo(QueuedNote queuedNote) {
        return (int) (time - queuedNote.time);
    }

    @Override
    public String toString() {
        return String.format("[%d:%d]\n", time, lane);
    }
}
