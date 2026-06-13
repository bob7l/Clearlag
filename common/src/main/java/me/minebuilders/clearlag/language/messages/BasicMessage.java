package me.minebuilders.clearlag.language.messages;

import me.minebuilders.clearlag.modules.BroadcastHandler;
import org.bukkit.command.CommandSender;

/**
 * @author bob7l
 */
public record BasicMessage(BroadcastHandler broadcastHandler, String rawMessage,
                           String... messageReplaceBits) implements Message {

    public String getRawStringMessage() {
        return rawMessage;
    }

    public String getStringMessage(Object... obj) {

        String message = rawMessage;
        for (int i = 0; i < obj.length && i < messageReplaceBits.length; ++i) {
            message = message.replace(messageReplaceBits[i], obj[i].toString());
        }

        return message;
    }

    public void sendMessage(CommandSender sender, Object... obj) {
        sender.sendMessage(getStringMessage(obj));
    }

    public void broadcastMessage(Object... obj) {
        broadcastHandler.broadcast(getStringMessage(obj));
    }
}
