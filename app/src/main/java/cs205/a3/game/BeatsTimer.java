package cs205.a3.game;

/**
 * A class used to time notes
 */
public class BeatsTimer {
    private long startTime;
    private long prevFrame;

    public void start() {
        this.startTime = System.currentTimeMillis();
        this.prevFrame = 0L;
    }

    public int getBeats() {
        long currentTime = System.currentTimeMillis();
        long totalFrames = (currentTime - startTime) / 20;

        long delta = totalFrames - prevFrame;

        prevFrame = totalFrames;
        return (int)delta;
    }
}
