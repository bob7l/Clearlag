package me.minebuilders.clearlag.commands;

import me.minebuilders.clearlag.Callback;
import me.minebuilders.clearlag.Clearlag;
import me.minebuilders.clearlag.RAMUtil;
import me.minebuilders.clearlag.Util;
import me.minebuilders.clearlag.exceptions.WrongCommandArgumentException;
import me.minebuilders.clearlag.language.LanguageValue;
import me.minebuilders.clearlag.language.messages.MessageTree;
import me.minebuilders.clearlag.modules.CommandModule;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author bob7l
 */
public class SampleMemoryCmd extends CommandModule {

    @LanguageValue(key = "command.samplememory.")
    private MessageTree lang;

    public SampleMemoryCmd() {
        argLength = 1;
    }

    @Override
    protected void run(CommandSender sender, String[] args) throws WrongCommandArgumentException {

        if (!Util.isInteger(args[0]))
            throw new WrongCommandArgumentException(lang.getMessage("invalidinteger"), args[0]);

        lang.sendMessage("begin", sender, args[0]);

        //Todo: Replace this pointless code with new Java 8 features...
        new MemorySamlier(Integer.parseInt(args[0]) * 20, (s) -> {

            int validMemorySamples = 0;

            long highestMemory = s.memoryList.get(0);
            long lowestMemory = highestMemory;

            long totalMemoryUsage = 0;

            for (long memory : s.memoryList) {

                if (memory > 0) {

                    if (memory > highestMemory)
                        highestMemory = memory;

                    if (memory < lowestMemory)
                        lowestMemory = memory;

                    totalMemoryUsage += memory;

                    ++validMemorySamples;
                }
            }

            long averageMemory = (totalMemoryUsage / validMemorySamples);

            lang.sendMessage("header", sender);

            lang.sendMessage("memory", sender,
                    Util.getChatColorByNumberLength((int) highestMemory, 100, 200) + Long.toString(highestMemory),
                    Util.getChatColorByNumberLength((int) averageMemory, 30, 100) + Long.toString(averageMemory)
            );


            //Todo: Replace this pointless code with new Java 8 features...
            if (s.gcCollectionTickstamps.size() > 1) {

                long highestGC = s.gcCollectionTickstamps.get(0).data;
                long lowestGC = highestGC;

                long totalGC = 0;

                int totalBetweenGCTime = 0;

                Sample lastSample = null;

                for (Sample sample : s.gcCollectionTickstamps) {

                    if (highestGC < sample.data)
                        highestGC = sample.data;

                    if (lowestGC > sample.data)
                        lowestGC = sample.data;

                    totalGC += sample.data;

                    if (lastSample != null) {
                        totalBetweenGCTime += (sample.timeStamp - lastSample.timeStamp);
                    }

                    lastSample = sample;
                }

                long averageGC = (totalGC / s.gcCollectionTickstamps.size());

                int averageBetweenTime = (totalBetweenGCTime / (s.gcCollectionTickstamps.size() - 1));


                lang.sendMessage("gc", sender,
                        s.gcCollectionTickstamps.size(),
                        Util.getChatColorByNumberLength((int) highestGC, 100, 200) + "" + highestGC,
                        Util.getChatColorByNumberLength((int) lowestGC, 100, 200) + "" + lowestGC,
                        Util.getChatColorByNumberLength((int) averageGC, 100, 200) + "" + averageGC,
                        averageBetweenTime
                );

            } else {
                lang.sendMessage("notenoughtime", sender);
            }


        }).runTaskTimer(Clearlag.getInstance(), 1L, 1L);
    }

    private static class MemorySamlier extends BukkitRunnable {

        private int currentTick = 0;

        private final int runTicks;

        private long gcCollections = getTotalGCEvents();

        private long gcLastPauseTime = getTotalGCCompleteTime();

        private long lastMemoryUsed = RAMUtil.getUsedMemory();

        private final List<Sample> gcCollectionTickstamps = new LinkedList<>();

        private final List<Long> memoryList;

        private final Callback<MemorySamlier> callback;

        public MemorySamlier(int runTicks, Callback<MemorySamlier> callback) {
            this.runTicks = runTicks;
            this.callback = callback;

            this.memoryList = new ArrayList<>(runTicks);
        }

        @Override
        public void run() {

            long currentGcCollections = getTotalGCEvents();

            if (currentGcCollections != gcCollections) {

                long currentTotalGCPauseTime = getTotalGCCompleteTime();

                gcCollectionTickstamps.add(new Sample(currentTick, currentTotalGCPauseTime - gcLastPauseTime));

                gcCollections = currentGcCollections;
                gcLastPauseTime = currentTotalGCPauseTime;

            }

            long memoryUsedDif = (RAMUtil.getUsedMemory() - lastMemoryUsed);

            memoryList.add(memoryUsedDif);

            lastMemoryUsed = RAMUtil.getUsedMemory();

            if (++currentTick > runTicks) {
                cancel();
                callback.call(this);
            }
        }

        private long getTotalGCEvents() {

            long totalGarbageCollections = 0;

            for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {

                long count = gc.getCollectionCount();

                if (count >= 0) {
                    totalGarbageCollections += count;
                }
            }

            return totalGarbageCollections;
        }

        private long getTotalGCCompleteTime() {

            long totalGarbageCollections = 0;

            for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {

                long count = gc.getCollectionTime();

                if (count >= 0) {
                    totalGarbageCollections += count;
                }
            }

            return totalGarbageCollections;
        }
    }

    private static class Sample {

        private final int timeStamp;

        private final long data;

        public Sample(int timeStamp, long data) {
            this.timeStamp = timeStamp;
            this.data = data;
        }
    }
}
