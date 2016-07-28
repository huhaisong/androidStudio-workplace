package a.myndk;

/**
 * Created by 333 on 2016/7/6.
 */
public class SystemClock implements Clock {
    public SystemClock() {
    }

    public long nanoTime() {
        return System.nanoTime();
    }
}
