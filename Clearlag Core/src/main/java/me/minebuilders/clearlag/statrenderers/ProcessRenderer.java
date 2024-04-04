package me.minebuilders.clearlag.statrenderers;

import me.minebuilders.clearlag.RAMUtil;
import me.minebuilders.clearlag.adapters.VersionAdapter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.State.*;

/**
 * @author bob7l
 */
public class ProcessRenderer extends StatRenderer {

    private final int TICK_LENGTH = 50;

    private final int[] stateGroupTable = new int[5];

    private final byte[] stateColorTable = new byte[Thread.State.values().length];

    private final Thread watchingThread;

    private final LinkedList<StateColumn> threadStateColumns = new LinkedList<>();

    private final Lock lock = new ReentrantLock();

    private final Timer samplerTimer;

    public ProcessRenderer(Player observer, int sampleTicks, ItemStack mapItemStack, VersionAdapter versionAdapter, MapView mapView, Thread watchingThread) {
        super(observer, sampleTicks, mapItemStack, versionAdapter, mapView);

        this.watchingThread = watchingThread;

        defineState(RUNNABLE, 0, MapPalette.DARK_GREEN);
        defineState(WAITING, 1, MapPalette.LIGHT_GREEN);
        defineState(NEW, 2, MapPalette.RED);
        defineState(BLOCKED, 3, MapPalette.RED);
        defineState(TIMED_WAITING, 4, MapPalette.BLUE);

        samplerTimer = new Timer("clearlag-thread-prober", true);

        samplerTimer.scheduleAtFixedRate(new SamplerThread(), 1L, 1L);
    }

    private void defineState(Thread.State state, int orderId, byte colorId) {
        stateGroupTable[state.ordinal()] = orderId;
        stateColorTable[orderId] = colorId;
    }

    @Override
    public void cancel() {
        super.cancel();

        samplerTimer.cancel();
    }

    @Override
    public void tick() {
    }

    @Override
    public void draw(MapView mapView, MapCanvas mapCanvas, Player player) {

        int x = 0;

        int sleepTime = 0;

        int totalTime = 0;

        lock.lock();

        if (!threadStateColumns.isEmpty()) {

            totalTime = threadStateColumns.size() * TICK_LENGTH;

            for (StateColumn stateColumn : threadStateColumns) {

                int drawPosition = height - 1;

                for (int i = 0; i < stateColumn.stateTable.length; ++i) {

                    final int length = stateColumn.getStateCount(i);

                    if (length > 0) {

                        if (i == 4)
                            sleepTime += length;

                        final byte color = stateColorTable[i];

                        for (int j = 0; j < length; ++j) {
                            mapCanvas.setPixel(x, drawPosition - j, color);
                        }

                        drawPosition -= length;
                    }
                }
                ++x;
            }
        }

        lock.unlock();

        final int tickLineY = width - 51;

        for (int i = 0; i < width; ++i) {
            mapCanvas.setPixel(i, tickLineY, MapPalette.LIGHT_GRAY);
        }

        if (totalTime != 0) {

            final double threadUsage = (100.0 - (((double) sleepTime / (double) totalTime) * 100.0));

            mapCanvas.drawText(5, 5, MinecraftFont.Font,
                    "§44;Thread-Usage: §16;" + ((int) threadUsage) + "%"
            );

            final double tickLength = Math.round((threadUsage * 0.5) * 10.0) / 10.0;

            mapCanvas.drawText(5, 17, MinecraftFont.Font,
                    "§44;Tick-Length: §16;" + (tickLength) + "ms"
            );

            mapCanvas.drawText(6, 29, MinecraftFont.Font,
                    "§44;Memory: §16;" + RAMUtil.getUsedMemory() + "MB"
            );

        }

    }

    private static class StateColumn {

        private final int[] stateTable = new int[5];

        private int length;

        public void addStateStat(int groupId) {
            stateTable[groupId] += 1;
            ++length;
        }

        public int getStateCount(int groupId) {
            return stateTable[groupId];
        }

        public int getLength() {
            return length;
        }
    }

    private class SamplerThread extends TimerTask {

        private StateColumn currentColumn = new StateColumn();

        @Override
        public void run() {

            if (currentColumn.length >= TICK_LENGTH) {

                lock.lock();

                try {

                    threadStateColumns.addFirst(currentColumn);

                    if (threadStateColumns.size() > width)
                        threadStateColumns.removeLast();

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();

                    currentColumn = new StateColumn();
                }
            }

            currentColumn.addStateStat(stateGroupTable[watchingThread.getState().ordinal()]);
        }
    }
}
