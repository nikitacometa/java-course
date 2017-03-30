/**
 * Created by wackloner on 30-Mar-17.
 */
public class Timer {
    private long startTime;

    public void start() {
        startTime = System.nanoTime();
    }

    public long getTime() {
        return (System.nanoTime() - startTime) / 1_000_000;
    }
}
