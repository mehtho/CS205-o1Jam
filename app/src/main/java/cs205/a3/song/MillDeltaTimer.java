package cs205.a3.song;

public class MillDeltaTimer {
    private long startTime;

    public long getDelta() {
        return System.currentTimeMillis() - startTime;
    }

    public void start() {
        this.startTime = System.currentTimeMillis();
    }
}
