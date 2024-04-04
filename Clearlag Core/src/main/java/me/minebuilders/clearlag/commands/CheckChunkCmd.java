package me.minebuilders.clearlag.commands;

import me.minebuilders.clearlag.language.LanguageValue;
import me.minebuilders.clearlag.language.messages.Message;
import me.minebuilders.clearlag.language.messages.MessageTree;
import me.minebuilders.clearlag.modules.CommandModule;
import org.bukkit.Chunk;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.IdentityHashMap;
import java.util.Map;

public class CheckChunkCmd extends CommandModule {

    @LanguageValue(key = "command.checkchunk.line")
    private Message lineMessage;

    @LanguageValue(key = "command.checkchunk.")
    private MessageTree lang;

    @Override
    protected void run(Player sender, String[] args) {

        final Chunk c = sender.getLocation().getChunk();

        final Map<Class<?>, Integer> tileEntityCountMap = new IdentityHashMap<>(100);

        final Map<EntityType, Integer> entityCountMap = new IdentityHashMap<>(100);


        for (BlockState bt : c.getTileEntities())
            tileEntityCountMap.merge(bt.getClass(), 1, Integer::sum);


        for (Entity e : c.getEntities()) {

            final EntityType type = e.getType();

            entityCountMap.merge(type, 1, Integer::sum);
        }

        lang.sendMessage("header", sender);

        lang.sendMessage("tilelist", sender);

        for (Map.Entry<Class<?>, Integer> tileEntry : tileEntityCountMap.entrySet())
            lineMessage.sendMessage(sender, tileEntry.getValue(), tileEntry.getKey().getSimpleName().replace("Craft", ""));


        lang.sendMessage("entitylist", sender);

        for (Map.Entry<EntityType, Integer> entityEntry : entityCountMap.entrySet())
            lineMessage.sendMessage(sender, entityEntry.getValue(), entityEntry.getKey().name().toLowerCase().replace("_", " "));


        lang.sendMessage("footer", sender);
    }

}
