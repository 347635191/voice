package component;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThreadPoolUtils {
    private static ThreadPoolExecutor executor;

    static {
        int corePoreSize = Runtime.getRuntime().availableProcessors() * 2;
        executor = new ThreadPoolExecutor(corePoreSize, corePoreSize * 2, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public static void execute(Runnable runnable) {
        Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
        executor.execute(runnable);
    }

    public static void shutdown() {
        executor.shutdown();
    }
}
