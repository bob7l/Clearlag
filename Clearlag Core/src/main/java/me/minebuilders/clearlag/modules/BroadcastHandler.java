package me.minebuilders.clearlag.modules;

import me.minebuilders.clearlag.Util;
import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.annotations.ConfigValue;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author bob7l
 */
@ConfigPath(path = "global-broadcasts")
public class BroadcastHandler extends ClearlagModule {

    @ConfigValue
    private boolean usePermissionForBroadcasts;

    @ConfigValue
    private String permission;

    @ConfigValue
    private boolean async;

    private Broadcaster broadcaster = new DefaultBroadcaster();

    public void broadcast(String message) {
        this.broadcast(new String[]{message});
    }

    public void broadcast(String[] message) {

        if (isEnabled()) {

            for (int i = 0; i < message.length; ++i)
                message[i] = Util.color(message[i]);

            if (async && !(broadcaster instanceof AsyncBroadcaster))
                broadcaster = new AsyncBroadcaster(broadcaster);

            broadcaster.broadcast(message);
        }
    }

    public Broadcaster getBroadcaster() {
        return broadcaster;
    }

    public void setBroadcaster(Broadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    public interface Broadcaster {

        void broadcast(String[] message);

    }

    private class DefaultBroadcaster implements Broadcaster {

        @Override
        public void broadcast(String[] message) {

            if (usePermissionForBroadcasts) {

                for (String str : message)
                    Bukkit.broadcast(str, permission);

            } else

                for (Player p : Bukkit.getOnlinePlayers()) {

                    for (String str : message)
                        p.sendMessage(str);
                }
        }
    }

    private static class AsyncBroadcaster implements Broadcaster {

        private final ExecutorService executor = Executors.newSingleThreadExecutor();

        private final Broadcaster wrappedBroadcaster;

        public AsyncBroadcaster(Broadcaster wrappedBroadcaster) {
            this.wrappedBroadcaster = wrappedBroadcaster;
        }

        @Override
        public void broadcast(String[] message) {
            executor.submit(() -> wrappedBroadcaster.broadcast(message));
        }
    }
}