package me.minebuilders.clearlag.commands;

import me.minebuilders.clearlag.language.LanguageValue;
import me.minebuilders.clearlag.language.messages.Message;
import me.minebuilders.clearlag.modules.CommandModule;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.PluginManager;

public class UnloadChunksCmd extends CommandModule {

    @LanguageValue(key = "command.unloadchunks.print")
    private Message unloadPrintMessage;

    @Override
    protected void run(CommandSender sender, String[] args) {

        sender.sendMessage(ChatColor.RED + "This command is not recommended in versions above 1.8. If you are having trouble with chunks, use chunk-gc in the bukkit.yml file.");

        int chunkcount = 0;

        final PluginManager pluginManager = Bukkit.getPluginManager();

        for (World world : Bukkit.getServer().getWorlds()) {

            for (Chunk chunk : world.getLoadedChunks()) {

                if (!world.isChunkInUse(chunk.getX(), chunk.getZ())) {

                    ChunkUnloadEvent event = new ChunkUnloadEvent(chunk);

                    pluginManager.callEvent(event);

                    if (chunk.unload(true)) {
                        chunkcount++;
                    }
                }
            }

        }
        unloadPrintMessage.sendMessage(sender, chunkcount);
    }
}