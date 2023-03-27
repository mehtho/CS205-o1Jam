package cs205.a3.game.animations;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FlashUpdaterPool {
    final ExecutorService pool;

    public FlashUpdaterPool() {
        final int cpuCores = Math.max(Runtime.getRuntime().availableProcessors() - 1, 1);
        pool = Executors.newFixedThreadPool(cpuCores);
    }

    public void submit(final Runnable task) {
        pool.submit(task);
    }
}
