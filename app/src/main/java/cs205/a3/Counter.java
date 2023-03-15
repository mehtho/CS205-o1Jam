package cs205.a3;

/**
 * A class used as a frame-rate counter.
 */
public class Counter {
    private long value = 0L;

    public void increment() {
        ++value;
    }

    public long getValue() {
        final long result = value;
        value = 0L;
        return result;
    }
}
