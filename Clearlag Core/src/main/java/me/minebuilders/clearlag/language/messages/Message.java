package me.minebuilders.clearlag.language.messages;

import org.bukkit.command.CommandSender;

/**
 * @author bob7l
 */
public interface Message {

    String getRawStringMessage();

    String getStringMessage(Object... obj);

    void sendMessage(CommandSender sender, Object... obj);

    void broadcastMessage(Object... obj);

}
