package me.minebuilders.clearlag;

import me.minebuilders.clearlag.tasks.TPSTask;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ServerCompatibilityTest {
    public interface PaperTPS {
        double[] getTPS();
    }

    private static Server server;

    @BeforeClass
    public static void installServer() {
        server = mock(Server.class, withSettings().extraInterfaces(PaperTPS.class));
        when(server.getLogger()).thenReturn(Logger.getLogger("ClearLagTest"));
        when(server.getName()).thenReturn("Paper");
        when(server.getVersion()).thenReturn("26.3");
        Bukkit.setServer(server);
    }

    @Test
    public void readsReleaseFromPublicApiInsteadOfCraftBukkitPackage() {
        String[][] releases = {
                {"26.3.build.253-stable", "26.3"},
                {"26.3-R0.1-SNAPSHOT", "26.3"},
                {"1.21.4-R0.1-SNAPSHOT", "1.21"},
                {"1.8.8-R0.1-SNAPSHOT", "1.8"}
        };
        for (String[] release : releases) {
            when(server.getBukkitVersion()).thenReturn(release[0]);
            assertEquals(release[0], release[1], Util.getBukkitVersion());
        }
    }

    @Test
    public void readsPaperTPSWithoutVersionedNMSPackages() throws Exception {
        when(((PaperTPS) server).getTPS()).thenReturn(new double[]{18.5, 19.0, 20.0});
        TPSTask task = new TPSTask();
        Class<?> readerClass = Class.forName(TPSTask.class.getName() + "$InternalTPSYoinker");
        Constructor<?> constructor = readerClass.getDeclaredConstructor(TPSTask.class);
        constructor.setAccessible(true);
        Object reader = constructor.newInstance(task);
        Method tick = readerClass.getDeclaredMethod("tick");
        Method average = readerClass.getDeclaredMethod("calculateCurrentAverageTPS");
        tick.setAccessible(true);
        average.setAccessible(true);
        for (int i = 0; i < 60; i++) tick.invoke(reader);
        assertEquals(18.5, (double) average.invoke(reader), 0.0);
        verify((PaperTPS) server).getTPS();
    }
}
