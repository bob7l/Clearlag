package me.minebuilders.clearlag.language;

import me.minebuilders.clearlag.Util;
import me.minebuilders.clearlag.language.messages.BasicMessage;
import me.minebuilders.clearlag.language.messages.Message;
import me.minebuilders.clearlag.language.messages.MessageBlock;
import me.minebuilders.clearlag.language.messages.MessageTree;
import me.minebuilders.clearlag.modules.BroadcastHandler;
import me.minebuilders.clearlag.modules.Module;
import org.bukkit.ChatColor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author bob7l
 *////
public class LanguageLoader {

    private Map<String, Message> fallbackLanguageMap = null;

    private Map<String, Message> languageMap = null;

    private final BroadcastHandler broadcastHandler;

    public LanguageLoader(BroadcastHandler broadcastHandler) {
        this.broadcastHandler = broadcastHandler;
    }

    public void wireInMessages(Object object) throws Exception {

        Class<?> clazz = object.getClass();

        while (clazz != null && clazz != Object.class && clazz != Module.class) {

            for (Field field : clazz.getDeclaredFields()) {

                if (field.isAnnotationPresent(LanguageValue.class)) {

                    field.setAccessible(true);

                    Message message = getMessageByKey(field.getAnnotation(LanguageValue.class).key());

                    if (message == null)
                        message = new BasicMessage(broadcastHandler, ChatColor.RED + "!Missing-From-Language-File!");

                    field.set(object, message);
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    public Message getMessageByKey(String key) {

        Message message = languageMap.get(key);

        if (message == null) {

            if (fallbackLanguageMap != null)
                message = fallbackLanguageMap.get(key);
            else
                message = new BasicMessage(broadcastHandler, ChatColor.RED + "!Missing-From-Language-File!");
        }

        return message;
    }

    public void setLanguageMap(InputStream input) throws Exception {
        languageMap = loadLanguage(input);
    }

    public void setFallbackLanguageMap(InputStream input) throws Exception {
        fallbackLanguageMap = loadLanguage(input);
    }

    private Map<String, Message> loadLanguage(InputStream in) throws Exception {

        final Map<String, Message> loadedMessageMap = new HashMap<>();

        final Map<String, MessageTree> loadedMessageTrees = new HashMap<>();

        final BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

        String line;

        while ((line = reader.readLine()) != null) {

            if (line.startsWith("#") || line.trim().isEmpty())
                continue;

            final String[] lineParts = line.split("=", 2);

            final String[] keyBits = lineParts[0].split("\\(", 2);

            String[] replaceValues = lineParts[0].substring(lineParts[0].indexOf("(") + 1, lineParts[0].indexOf(")")).split(",");

            if (replaceValues.length == 1 && replaceValues[0].isEmpty())
                replaceValues = new String[0];

            final String message = Util.color(lineParts[1]);

            //Replace the representation of a replaced value with a shortend version. Small optimization...
//            int replaceVariableVal = 0;
//
//            for (int i = 0; i < replaceValues.length; ++i) {
//
//                String replacerElement;
//
//                do {
//                    replacerElement = "@" + (replaceVariableVal++);
//                } while (message.contains(replacerElement));
//
//                message = message.replace(replaceValues[i], replacerElement);
//
//                replaceValues[i] = replacerElement;
//            }


            final Message insertingMessage;

            if (message.trim().equals("{")) {

                List<String> currentMessageBlock = new LinkedList<>();

                while ((line = reader.readLine()) != null && !line.trim().equals("}"))
                    currentMessageBlock.add(Util.color(line));

                insertingMessage = new MessageBlock(broadcastHandler, currentMessageBlock.toArray(new String[0]), replaceValues);


            } else {

                insertingMessage = new BasicMessage(broadcastHandler, message, replaceValues);

            }

            final int treeEndLength = keyBits[0].lastIndexOf(".") + 1;

            final String treeKey = keyBits[0].substring(0, treeEndLength);

            final String treeMessageKey = keyBits[0].substring(treeEndLength);

            MessageTree tree = loadedMessageTrees.get(treeKey);

            if (tree == null) {
                tree = new MessageTree();
                loadedMessageTrees.put(treeKey, tree);
                loadedMessageMap.put(treeKey, tree);
            }

            tree.addMessage(treeMessageKey, insertingMessage);

            loadedMessageMap.put(keyBits[0], insertingMessage);
        }

        return loadedMessageMap;
    }
}
