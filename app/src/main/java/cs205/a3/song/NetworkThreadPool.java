package cs205.a3.song;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import cs205.a3.scorecalc.Score;

/**
 * Thread pool for tasks requiring network access
 */
public class NetworkThreadPool {
    final ExecutorService pool;

    public NetworkThreadPool() {
        final int cpuCores = Math.max(Runtime.getRuntime().availableProcessors() - 1, 1);
        pool = Executors.newFixedThreadPool(cpuCores);
    }

    public Future<List<SongReference>> submitSongCall(final Callable<List<SongReference>> call) {
        return pool.submit(call);
    }

    public Future<List<Score>> submitScoreCall(final Callable<List<Score>> call) {
        return pool.submit(call);
    }

    public Future<?> submitTask(final Runnable runnable) {
        return pool.submit(runnable);
    }
}
