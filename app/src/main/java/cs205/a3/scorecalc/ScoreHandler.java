package cs205.a3.scorecalc;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A thread to handle button presses and adjust score and combo counter
 * Is the "Consumer" in the producer-consumer pattern
 */
public class ScoreHandler implements Runnable {
    private final BlockingQueue<Long> blockingQueue;
    private volatile long score;
    private volatile int combo;
    private volatile boolean isActive;

    public ScoreHandler() {
        blockingQueue = new LinkedBlockingQueue<>();
        isActive = true;
    }

    @Override
    public void run() {
        while (isActive) {
            synchronized (this) {
                try {
                    final long inp = blockingQueue.take();

                    if (inp == -1) {
                        combo = 0;
                    } else {
                        /**
                         * Scoring algorithm in relation to combo inspired by popular
                         * rhythm game osu!
                         */
                        final long toAdd = (long) (inp * ((1.0 + (combo++ / 25.0))));
                        score += toAdd;
                    }
                } catch (InterruptedException e) {
                    // Do nothing
                }
            }
        }
    }

    public void enqueueScore(long score) {
        blockingQueue.offer(score);
    }

    public long getScore() {
        return score;
    }

    public int getCombo() {
        return combo;
    }

    public void deactivate() {
        this.isActive = false;
    }
}
