package me.minebuilders.clearlag.language.messages;

import me.minebuilders.clearlag.modules.BroadcastHandler;
import org.bukkit.command.CommandSender;

/**
 * @author bob7l
 */
public record MessageBlock(BroadcastHandler broadcastHandler, String[] rawMessage,
                           String... messageReplaceBits) implements Message {

    public String getRawStringMessage() {
        return rawMessage[0];
    }

    public String getStringMessage(Object... obj) {
        final StringBuilder sb = new StringBuilder();
        for (String message : rawMessage) {
            for (int i = 0; i < obj.length && i < messageReplaceBits.length; ++i) {
                message = message.replace(messageReplaceBits[i], obj[i].toString());
            }

            sb.append(sb.isEmpty() ? System.lineSeparator() + message : message);
        }

        return sb.toString();
    }

    public void sendMessage(CommandSender sender, Object... obj) {
        for (String message : rawMessage) {
            for (int i = 0; i < obj.length && i < messageReplaceBits.length; ++i) {
                message = message.replace(messageReplaceBits[i], obj[i].toString());
            }

            sender.sendMessage(message);
        }
    }

    public void broadcastMessage(Object... obj) {
        for (String message : rawMessage) {
            for (int i = 0; i < obj.length && i < messageReplaceBits.length; ++i) {
                message = message.replace(messageReplaceBits[i], obj[i].toString());
            }

            broadcastHandler.broadcast(getStringMessage(obj));
        }
    }
}
