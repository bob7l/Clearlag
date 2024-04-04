package me.minebuilders.clearlag.tasks;

import me.minebuilders.clearlag.Clearlag;
import me.minebuilders.clearlag.Util;
import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.config.ConfigHandler;
import me.minebuilders.clearlag.events.TPSUpdateEvent;
import me.minebuilders.clearlag.modules.TaskModule;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.lang.reflect.Field;
import java.util.Arrays;

@ConfigPath(path = "settings")
public class TPSTask extends TaskModule {

    private final double[] tpsHistory = new double[10];

    private int index = 0;

    private TPSCalculator tpsCalculator;

    @AutoWire
    private ConfigHandler configHandler;

    private int elapsedTicks = 0;

    @Override
    public void setEnabled() {
        super.setEnabled();

        Arrays.fill(tpsHistory, 20.0);
    }

    @Override
    protected int startTask() {

        elapsedTicks = 0;

        if (configHandler.getConfig().getBoolean("settings.use-internal-tps")) {

            try {

                tpsCalculator = new InternalTPSYoinker();

            } catch (Exception e) {

                Util.warning("Clearlag failed to use the internal TPS tracker during initialization. Reverted to estimation... (" + e.getMessage() + ")");

                tpsCalculator = new EstimatedTPSCalculator();
            }

        } else {
            tpsCalculator = new EstimatedTPSCalculator();
        }


        return Bukkit.getScheduler().scheduleSyncRepeatingTask(Clearlag.getInstance(), this, 120L, getInterval());
    }

    public double getTPS() {
        double tpsSum = 0.0;

        for (double d : tpsHistory) {
            tpsSum += d;
        }

        return Math.round((tpsSum / 10.0) * 100.0) / 100.0;
    }

    public String getStringTPS() {
        return (getColor() + String.valueOf(getTPS()));
    }

    public ChatColor getColor() {
        double tps = getTPS();

        if (tps > 17) return ChatColor.GREEN;
        if (tps > 13) return ChatColor.GOLD;

        return ChatColor.RED;
    }

    @Override
    public void run() {

        tpsCalculator.tick();

        if (elapsedTicks % 20 == 0) {

            double tps = tpsCalculator.calculateCurrentAverageTPS();

            if (tps > 0 && tps <= 21.0) {

                tpsHistory[index++] = tps;

                if (index >= tpsHistory.length) {

                    index = 0;

                    Bukkit.getPluginManager().callEvent(new TPSUpdateEvent(getTPS()));
                }
            }
        }
    }

    @Override
    public int getInterval() {
        return 1;
    }

    private interface TPSCalculator {

        double calculateCurrentAverageTPS();

        void tick();

    }

    private class EstimatedTPSCalculator implements TPSCalculator {

        private long lasTimestamp = -1;

        private final int[] tickLengths = new int[20];

        private double tps = 20.0;

        public EstimatedTPSCalculator() {
            Arrays.fill(tickLengths, 50);
        }

        @Override
        public void tick() {

            final long currentTime = System.currentTimeMillis();

            if (lasTimestamp != -1) {

                int elaspedTime = (int) (currentTime - lasTimestamp);

                if (elaspedTime == 49 || elaspedTime == 51)
                    elaspedTime = 50;

                tickLengths[elapsedTicks % 20] = elaspedTime;
            }

            lasTimestamp = currentTime;

            if (++elapsedTicks % 20 == 0) {

                double tickSum = 0.0;

                for (int tickLength : tickLengths)
                    tickSum += tickLength;

                final double tickLength = (tickSum / 20.0);

                tps = (50.0 / tickLength) * 20.0;
            }
        }

        @Override
        public double calculateCurrentAverageTPS() {
            return tps;
        }
    }

    private class InternalTPSYoinker implements TPSCalculator {

        private final Field recentTpsField;

        private final Object minecraftServerInstance;

        private double tps = 20.0;

        public InternalTPSYoinker() throws Exception {

            Class<?> minecraftServerClazz = Class.forName("net.minecraft.server." + Util.getRawBukkitVersion() + ".MinecraftServer");

            minecraftServerInstance = minecraftServerClazz.getDeclaredMethod("getServer").invoke(null);

            recentTpsField = minecraftServerClazz.getDeclaredField("recentTps");

            recentTpsField.setAccessible(true);

        }

        @Override
        public void tick() {

            if (++elapsedTicks % 60 == 0) {
                try {
                    tps = ((double[]) recentTpsField.get(minecraftServerInstance))[0];
                } catch (IllegalAccessException e) {

                    Util.warning("Clearlag failed to use the internal TPS tracker during runtime. Reverted to estimation... (" + e.getMessage() + ")");

                    tpsCalculator = new EstimatedTPSCalculator();
                }
            }
        }

        @Override
        public double calculateCurrentAverageTPS() {
            return tps;
        }
    }
}
