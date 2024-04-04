package me.minebuilders.clearlag.tasks;

import me.minebuilders.clearlag.Clearlag;
import me.minebuilders.clearlag.Util;
import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.config.ConfigHandler;
import me.minebuilders.clearlag.modules.TaskModule;
import org.bukkit.Bukkit;

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

    private final Thread mainThread;

    private final AtomicInteger tick = new AtomicInteger();

    private final AtomicLong tickTimestamp = new AtomicLong();

    private final AtomicLong tickGarbageCollectorTimeTotal = new AtomicLong();

    private Timer timer = null;

    public LagSpikeTask() {
        this.mainThread = Thread.currentThread();
    }

    private class ThreadWatcherTask extends TimerTask {

        private long lastElaspedTime = 0L;

        private long lastGarbageCollectionTimeTotal = 0;

        private int frozenTick = -100;

        private String frozenLine = "";

        private boolean frozen = false;

        public void run() {

            final int currentTick = tick.get();

            if (currentTick < 400)
                return;

            final long elapsedTime = (System.currentTimeMillis() - tickTimestamp.get());

            if (elapsedTime >= minElapsedTime) {

                frozen = true;

                final StackTraceElement[] stackTraceElements = mainThread.getStackTrace();

                if (currentTick != frozenTick) {

                    lastGarbageCollectionTimeTotal = tickGarbageCollectorTimeTotal.get();

                    frozenTick = currentTick;

                    Util.warning("Clearlag has detected a possible lag spike on tick #" + currentTick + " (Tick is currently at " + elapsedTime + " milliseconds)");

                    Util.warning("Thread name: " + mainThread.getName() + " Id: " + mainThread.getId());

                    Util.warning("Thread state: " + mainThread.getState());

                    Util.warning("Thread stack-trace: ");

                    if (stackTraceElements.length > 0) {

                        for (StackTraceElement ste : stackTraceElements)
                            Util.log(" > " + ste);

                        frozenLine = stackTraceElements[0].toString();
                    }

                } else if (followStack && stackTraceElements.length > 0 && !stackTraceElements[0].toString().equals(frozenLine)) {

                    Util.warning("Thread stack-trace (Stack moved): ");

                    for (StackTraceElement ste : stackTraceElements)
                        Util.log(" > " + ste);

                    frozenLine = stackTraceElements[0].toString();
                }

                lastElaspedTime = elapsedTime;

            } else if (frozen) {

                frozenLine = null;

                frozen = false;

                Util.warning("Thread '" + mainThread.getName() + "' is no longer stuck on tick #" + frozenTick);
                Util.warning("Estimated time spent on tick #" + frozenTick + ": " + lastElaspedTime);
                Util.warning("Garbage collection time during tick: " + (getTotalGCCompleteTime() - lastGarbageCollectionTimeTotal));
            }

        }

    }

    private long getTotalGCCompleteTime() {

        long totalGarbageCollections = 0;

        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans())
            totalGarbageCollections += gc.getCollectionTime();

        return totalGarbageCollections;
    }

    @Override
    public void run() {

        tick.incrementAndGet();

        tickTimestamp.set(System.currentTimeMillis());

        tickGarbageCollectorTimeTotal.set(getTotalGCCompleteTime());
    }

    protected int startTask() {

        timer = new Timer(true);

        timer.scheduleAtFixedRate(new ThreadWatcherTask(), 50, configHandler.getConfig().getLong("lag-spike-helper.check-interval"));

        return Bukkit.getScheduler().runTaskTimer(Clearlag.getInstance(), this, getInterval(), getInterval()).getTaskId();
    }

    @Override
    public void setDisabled() {
        super.setDisabled();

        Bukkit.getScheduler().cancelTask(taskid);

        timer.cancel();

        timer = null;
    }

    public int getInterval() {
        return 1;
    }
}
