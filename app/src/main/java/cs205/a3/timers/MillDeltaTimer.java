package cs205.a3.timers;

/**
 * A timer used to calculate the milliseconds since the song started
 */
public class MillDeltaTimer {
    private long startTime;

    public long getDelta() {
        return System.currentTimeMillis() - startTime;
    }

    public void start() {
        this.startTime = System.currentTimeMillis();
    }
}
