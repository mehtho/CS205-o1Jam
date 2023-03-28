package cs205.a3.menus;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import cs205.a3.scorecalc.Score;
import cs205.a3.song.SongReference;

public class NIOThreadPool {
    final ExecutorService pool;

    public NIOThreadPool() {
        final int cpuCores = Math.max(Runtime.getRuntime().availableProcessors() - 1, 1);
        pool = Executors.newFixedThreadPool(cpuCores);
    }

    public Future<List<SongReference>> submitSongCall(final Callable<List<SongReference>> call) {
        return pool.submit(call);
    }

    public Future<List<Score>> submitScoreCall(final Callable<List<Score>> call) {
        return pool.submit(call);
    }
}
