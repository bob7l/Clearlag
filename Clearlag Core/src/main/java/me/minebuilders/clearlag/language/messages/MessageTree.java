package me.minebuilders.clearlag.language.messages;

import me.minebuilders.clearlag.modules.BroadcastHandler;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bob7l
 */
public class MessageTree implements Message {

    private final Map<String, Message> messageList = new HashMap<>(5);

    private int currentReadIndex = 0;

    public void addMessage(String key, Message message) {
        this.messageList.put(key, message);
    }

    public void removeMessage(String key) {
        this.messageList.remove(key);
    }

    @Override
    public String getRawStringMessage() {
        return "N/A";
    }

    public Message getMessage(String key) {

        final Message message = messageList.get(key);

        if (message == null)
            return new BasicMessage(new BroadcastHandler(), "Your language file is missing an entry for key '" + key + "'!");

        return message;
    }

    private Message getNextMessage() {

        final Message message = messageList.get(currentReadIndex++);

        if (messageList.size() >= currentReadIndex)
            resetReadIndex();

        return message;
    }

    @Override
    public String getStringMessage(Object... obj) {
        return getNextMessage().getStringMessage(obj);
    }

    @Override
    public void sendMessage(CommandSender sender, Object... obj) {
        getNextMessage().sendMessage(sender, obj);
    }

    public void sendMessage(String key, CommandSender sender, Object... obj) {
        getMessage(key).sendMessage(sender, obj);
    }

    public void broadcastMessage(String key, Object... obj) {
        getMessage(key).broadcastMessage(obj);
    }

    @Override
    public void broadcastMessage(Object... obj) {
        getNextMessage().broadcastMessage(obj);
    }


    public void resetReadIndex() {
        currentReadIndex = 0;
    }

}
