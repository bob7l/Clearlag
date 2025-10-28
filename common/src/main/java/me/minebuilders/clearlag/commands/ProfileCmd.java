package me.minebuilders.clearlag.commands;

import me.minebuilders.clearlag.*;
import me.minebuilders.clearlag.exceptions.WrongCommandArgumentException;
import me.minebuilders.clearlag.language.LanguageValue;
import me.minebuilders.clearlag.language.messages.Message;
import me.minebuilders.clearlag.language.messages.MessageTree;
import me.minebuilders.clearlag.modules.CommandModule;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bob7l
 */
public class ProfileCmd extends CommandModule {

    @LanguageValue(key = "command.profile.")
    private MessageTree lang;

    @LanguageValue(key = "command.profile.line")
    private Message lineMessage;

    private final ProfilerFactory[] profilerFactories = new ProfilerFactory[]{
            new ProfilerFactory("Redstone", RedstoneProfileSession.class),
            new ProfilerFactory("FlowingLiquid", FlowingLiquidProfileSession.class),
            new ProfilerFactory("BlockUpdate", BlockUpdateProfileSession.class),
            new ProfilerFactory("EntitySpawn", EntitySpawnProfileSession.class)
    };

    public ProfileCmd() {
        argLength = 2;
    }

    @Override
    protected void run(final CommandSender sender, String[] args) throws WrongCommandArgumentException {
        if (!Util.isInteger(args[0])) {
            throw new WrongCommandArgumentException(lang.getMessage("invalidtime"), args[0]);
        }

        final Callback<Map<ChunkKey, MutableInt>> callback = chunkKeyMutableIntMap -> {
            if (chunkKeyMutableIntMap.isEmpty()) {
                lang.sendMessage("nosamples", sender);
                return;
            }

            final int size = 10;
            final Integer[] sizes = new Integer[size];
            final ChunkKey[] chunks = new ChunkKey[size];
            for (Map.Entry<ChunkKey, MutableInt> entry : chunkKeyMutableIntMap.entrySet()) {
                final int amount = entry.getValue().getValue();
                for (int i = 0; i < size; i++) {
                    if (sizes[i] == null || sizes[i] < amount) {
                        Util.shiftRight(chunks, i);
                        Util.shiftRight(sizes, i);

                        chunks[i] = entry.getKey();
                        sizes[i] = amount;

                        break;
                    }
                }
            }

            lang.sendMessage("header", sender);

            for (int i = 0; i < sizes.length; i++) {
                if (sizes[i] == null || chunks[i] == null) {
                    break;
                }

                ChunkKey c = chunks[i];
                lineMessage.sendMessage(sender, (i + 1), c.getWorld().getName(), c.getX(), c.getZ(), sizes[i]);
            }
        };

        ProfileSession profileSession = null;
        for (ProfilerFactory factory : profilerFactories) {
            if (factory.id().equalsIgnoreCase(args[1])) {
                profileSession = factory.constructProfiler(callback);
                break;
            }
        }

        if (profileSession == null) {
            final StringBuilder sb = new StringBuilder();
            for (ProfilerFactory factory : profilerFactories) {
                if (!sb.isEmpty())
                    sb.append(", ");
                sb.append(factory.id());
            }

            throw new WrongCommandArgumentException(lang.getMessage("invalidprofiler"), args[1], sb.toString());
        }

        ClearLag.scheduler().runLater(profileSession, Integer.parseInt(args[0]) * 20L);
        Bukkit.getPluginManager().registerEvents(profileSession, ClearLag.getInstance());

        lang.sendMessage("started", sender, args[0]);
    }

    private record ProfilerFactory(String id, Class<? extends ProfileSession> sessionClass) {

        public ProfileSession constructProfiler(Callback<Map<ChunkKey, MutableInt>> callback) {
                try {
                    return (ProfileSession) sessionClass.getDeclaredConstructors()[0].newInstance(callback);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }

    private static abstract class ProfileSession implements Listener, Runnable {

        final Map<ChunkKey, MutableInt> chunkMap = new ConcurrentHashMap<>();
        final Callback<Map<ChunkKey, MutableInt>> callback;

        public ProfileSession(Callback<Map<ChunkKey, MutableInt>> callback) {
            this.callback = callback;
        }

        @Override
        public void run() {
            HandlerList.unregisterAll(this);
            callback.call(chunkMap);
        }

        protected void incrementMap(Chunk chunk) {
            final ChunkKey key = new ChunkKey(chunk);
            chunkMap.compute(key, (k, v) -> {
                if (v == null) {
                    return new MutableInt(1);
                }

                v.increment();
                return v;
            });
        }
    }

    private static class RedstoneProfileSession extends ProfileSession {

        public RedstoneProfileSession(Callback<Map<ChunkKey, MutableInt>> callback) {
            super(callback);
        }

        @EventHandler(ignoreCancelled = true)
        public void onRedstoneUpdate(BlockRedstoneEvent event) {
            incrementMap(event.getBlock().getChunk());
        }
    }

    private static class FlowingLiquidProfileSession extends ProfileSession {

        public FlowingLiquidProfileSession(Callback<Map<ChunkKey, MutableInt>> callback) {
            super(callback);
        }

        @EventHandler(ignoreCancelled = true)
        public void onFlow(BlockFromToEvent event) {
            incrementMap(event.getBlock().getChunk());
        }
    }

    private static class BlockUpdateProfileSession extends ProfileSession {

        public BlockUpdateProfileSession(Callback<Map<ChunkKey, MutableInt>> callback) {
            super(callback);
        }

        @EventHandler(ignoreCancelled = true)
        public void onBlockUpdate(BlockPhysicsEvent event) {
            incrementMap(event.getBlock().getChunk());
        }
    }

    private static class EntitySpawnProfileSession extends ProfileSession {

        public EntitySpawnProfileSession(Callback<Map<ChunkKey, MutableInt>> callback) {
            super(callback);
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
        public void onEntitySpawn(EntitySpawnEvent event) {
            incrementMap(event.getLocation().getChunk());
        }
    }
}
