package me.minebuilders.clearlag.tasks;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import me.minebuilders.clearlag.ClearLag;
import me.minebuilders.clearlag.Util;
import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.config.ConfigHandler;
import me.minebuilders.clearlag.modules.TaskModule;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@ConfigPath(path = "lag-spike-helper")
public class LagSpikeTask extends TaskModule {

    @ConfigValue
    private boolean followStack;

    @ConfigValue
    private int minElapsedTime;

    @AutoWire
    private ConfigHandler configHandler;

    private final Thread mainThread = Thread.currentThread();
    private final AtomicInteger tick = new AtomicInteger();
    private final AtomicLong tickTimestamp = new AtomicLong();
    private final AtomicLong tickGarbageCollectorTimeTotal = new AtomicLong();

    private Timer timer;

    private class ThreadWatcherTask extends TimerTask {
        private long lastElapsedTime = 0L;
        private long lastGarbageCollectionTimeTotal = 0;
        private int frozenTick = -100;
        private String frozenLine = "";
        private boolean frozen = false;

        @Override
        public void run() {
            final int currentTick = tick.get();
            if (currentTick < 400) return;

            final long elapsedTime = (System.currentTimeMillis() - tickTimestamp.get());
            if (elapsedTime >= minElapsedTime) {
                frozen = true;
                final StackTraceElement[] trace = mainThread.getStackTrace();

                if (currentTick != frozenTick) {
                    lastGarbageCollectionTimeTotal = tickGarbageCollectorTimeTotal.get();
                    frozenTick = currentTick;

                    Util.warning("Clearlag detected a possible lag spike on tick #" + currentTick + " (" + elapsedTime + "ms elapsed)");
                    Util.warning("Thread: " + mainThread.getName() + " [" + mainThread.getState() + "]");
                    Util.warning("Stack trace:");
                    for (StackTraceElement ste : trace) Util.log(" > " + ste);

                    if (trace.length > 0) frozenLine = trace[0].toString();

                } else if (followStack && trace.length > 0 && !trace[0].toString().equals(frozenLine)) {
                    Util.warning("Thread stack trace (stack moved):");
                    for (StackTraceElement ste : trace) Util.log(" > " + ste);
                    frozenLine = trace[0].toString();
                }

                lastElapsedTime = elapsedTime;
            } else if (frozen) {
                frozenLine = null;
                frozen = false;
                Util.warning("Thread '" + mainThread.getName() + "' is no longer stuck on tick #" + frozenTick);
                Util.warning("Estimated time spent on tick #" + frozenTick + ": " + lastElapsedTime + "ms");
                Util.warning("Garbage collection time during tick: " + (getTotalGCCompleteTime() - lastGarbageCollectionTimeTotal) + "ms");
            }
        }
    }

    private long getTotalGCCompleteTime() {
        long total = 0;
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            total += gc.getCollectionTime();
        }
        return total;
    }

    @Override
    public void run() {
        tick.incrementAndGet();
        tickTimestamp.set(System.currentTimeMillis());
        tickGarbageCollectorTimeTotal.set(getTotalGCCompleteTime());
    }

    @Override
    protected WrappedTask startTask() {
        timer = new Timer(true);
        long interval = configHandler.getConfig().getLong("lag-spike-helper.check-interval");
        timer.scheduleAtFixedRate(new ThreadWatcherTask(), 50L, interval);

        return ClearLag.scheduler().runTimer(this, getInterval(), getInterval());
    }

    @Override
    public void setDisabled() {
        super.setDisabled();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public int getInterval() {
        return 1;
    }
}
