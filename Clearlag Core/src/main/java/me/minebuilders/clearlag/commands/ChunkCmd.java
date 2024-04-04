package me.minebuilders.clearlag.commands;

import me.minebuilders.clearlag.Util;
import me.minebuilders.clearlag.language.LanguageValue;
import me.minebuilders.clearlag.language.messages.Message;
import me.minebuilders.clearlag.modules.CommandModule;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class ChunkCmd extends CommandModule {

    @LanguageValue(key = "command.chunk.header")
    private Message header;

    @LanguageValue(key = "command.chunk.print")
    private Message outprint;

    @Override
    protected void run(CommandSender sender, String[] args) {

        final int size = (args.length >= 1 && Util.isInteger(args[0])) ? Integer.parseInt(args[0]) : 5;

        final Integer[] sizes = new Integer[size];

        final Chunk[] chunks = new Chunk[size];

        for (World world : Bukkit.getWorlds()) {

            for (Chunk c : world.getLoadedChunks()) {

                final int amount = c.getEntities().length;

                for (int i = 0; i < size; i++) {

                    if (sizes[i] == null || sizes[i] < amount) {

                        Util.shiftRight(chunks, i);
                        Util.shiftRight(sizes, i);

                        chunks[i] = c;
                        sizes[i] = amount;

                        break;
                    }
                }
            }
        }

        header.sendMessage(sender);

        for (int i = 0; i < sizes.length; i++) {

            final Chunk c = chunks[i];

            outprint.sendMessage(sender, (i + 1), c.getWorld().getName(), c.getX(), c.getZ(), sizes[i]);
        }
    }
}