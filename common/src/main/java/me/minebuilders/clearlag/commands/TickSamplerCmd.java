package me.minebuilders.clearlag.commands;

import me.minebuilders.clearlag.Callback;
import me.minebuilders.clearlag.ClearLag;
import me.minebuilders.clearlag.Util;
import me.minebuilders.clearlag.language.LanguageValue;
import me.minebuilders.clearlag.language.messages.Message;
import me.minebuilders.clearlag.language.messages.MessageTree;
import me.minebuilders.clearlag.modules.CommandModule;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IntSummaryStatistics;
import java.util.List;

public class TickSamplerCmd extends CommandModule {

    @LanguageValue(key = "command.sampleticks.")
    private MessageTree lang;

    public TickSamplerCmd() {
        name = "sampleticks";
    }

    @Override
    protected void run(final CommandSender sender, String[] args) {

        int sampleTickCycles = 1;
        if (args.length > 0 && Util.isInteger(args[0])) {
            sampleTickCycles = Integer.parseInt(args[0]);
            if (sampleTickCycles <= 0) {
                sampleTickCycles = 1;
            }
        }

        final Thread serverThread = Thread.currentThread();
        final Callback<Collection<Integer>> callback;

        if (args.length > 1 && args[1].equalsIgnoreCase("raw")) {
            callback = (sample) -> {
                lang.sendMessage("rawheader", sender);
                final Message rawLineMessage = lang.getMessage("rawprint");
                for (Integer time : sample) {
                    rawLineMessage.sendMessage(sender, (Util.getChatColorByNumberLength(time, 40, 50).toString() + time));
                }
            };
        } else {
            callback = (samples) -> {
                final IntSummaryStatistics stats = samples.stream()
                        .mapToInt((x) -> x)
                        .summaryStatistics();

                int spikes = 0;
                for (int sample : samples) {
                    if (sample > 50) {
                        ++spikes;
                    }
                }

                lang.sendMessage("print", sender,
                        (Util.getChatColorByNumberLength(stats.getMax(), 40, 50).toString() + stats.getMax()),
                        (Util.getChatColorByNumberLength(stats.getMin(), 40, 50).toString() + stats.getMin()),
                        (Util.getChatColorByNumberLength((int) Math.round(stats.getAverage()), 40, 50).toString() + Math.round(stats.getAverage())),
                        (Util.getChatColorByNumberLength(spikes, 1, 2).toString() + spikes)
                );
            };

        }

        new TimingThread(serverThread, callback, sampleTickCycles).start();
        lang.sendMessage("start", sender, serverThread.getName(), sampleTickCycles);
    }

    private static class TimingThread extends Thread {

        private final Thread watchingThread;

        private final List<Integer> fullTickTimings;

        private final Callback<Collection<Integer>> callback;

        private int sampleTickCycles;

        private long lastTimestamp = -1L;

        public TimingThread(Thread watchingThread, Callback<Collection<Integer>> callback, int sampleTickCycles) {
            this.watchingThread = watchingThread;
            this.callback = callback;
            this.sampleTickCycles = ++sampleTickCycles;
            this.fullTickTimings = new ArrayList<>(sampleTickCycles);
        }

        public void start() {
            ClearLag.scheduler().runTimer(task -> {
                final long now = System.currentTimeMillis();
                if (lastTimestamp == -1L) {
                    lastTimestamp = now;
                    return;
                }

                fullTickTimings.add((int) (now - lastTimestamp));
                lastTimestamp = now;

                if (fullTickTimings.size() >= (this.sampleTickCycles - 1)) {
                    task.cancel();
                    ClearLag.scheduler().runNextTick(nextTask -> callback.call(fullTickTimings));
                }
            }, 1L, 1L);
        }
    }
}
