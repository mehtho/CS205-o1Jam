package cs205.a3.scorecalc;

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
