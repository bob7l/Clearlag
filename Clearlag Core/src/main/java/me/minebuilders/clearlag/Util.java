package me.minebuilders.clearlag;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

public class Util {

    private static final Logger log = Logger.getLogger("Minecraft");

    public static void log(String m) {
        log.info("[ClearLag] " + m);
    }

    public static void warning(String m) {
        log.warning("[ClearLag] " + m);
    }

    public static void msg(String m, CommandSender s) {
        s.sendMessage(color("&6[&aClearLag&6] &a" + m));
    }

    public static void scm(String m, CommandSender s) {
        s.sendMessage(color(m));
    }

    public static ChatColor getChatColorByNumberLength(int variable, int yellowSize, int redSize) {
        return (variable >= redSize ? ChatColor.RED : variable >= yellowSize ? ChatColor.YELLOW : ChatColor.GREEN);
    }

    public static void shiftRight(Object[] list, int dropIndex) {

        if (list.length < 2) return;

        System.arraycopy(list, dropIndex, list, dropIndex + 1, list.length - 1 - dropIndex);

    }

    public static void postToMainThread(Runnable runnable) {
        Bukkit.getScheduler().runTask(Clearlag.getInstance(), runnable);
    }

    public static boolean isInteger(String s) {
        return isInteger(s, 10);
    }

    public static boolean isInteger(String s, int radix) {
        if (s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) return false;
                else continue;
            }
            if (Character.digit(s.charAt(i), radix) < 0) return false;
        }
        return true;
    }

    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String[] cloneAndReplaceStringArr(String[] stringArr, String key, String replaced) {

        final String[] clone = new String[stringArr.length];

        for (int i = 0; i < stringArr.length; ++i) {
            clone[i] = stringArr[i].replace(key, replaced);
        }

        return clone;
    }

    public static EntityType getEntityTypeFromString(String s) {
        @SuppressWarnings("deprecation")
        EntityType et = EntityType.fromName(s);

        if (et != null) {
            return et;
        }

        s = s.replace("_", "").replace(" ", "");
        for (EntityType e : EntityType.values()) {
            if (e != null) {
                String name = e.name().replace("_", "");
                if (name.equalsIgnoreCase(s)) {
                    return e;
                }
            }
        }
        return null;
    }

    public static String getRawBukkitVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    public static String getBukkitVersion() {

        String[] v = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3].split("-")[0].split("_");

        return v[0].replace("v", "") + "." + v[1];
    }

    public static Date parseTime(String time) {

        try {

            String[] frag = time.split("-");

            if (frag.length < 2)
                return new Date();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            return dateFormat.parse(frag[0] + "-" + frag[1] + "-" + frag[2]);

        } catch (Exception e) {
            return new Date();
        }
    }

    public static String getTime(long time) {

        long seconds = Math.abs(time) / 1000L;

        final StringBuilder message = new StringBuilder();

        if (seconds >= 86400) {
            int days = (int) (seconds / 86400);
            seconds %= 86400;

            message.append(days).append(days > 1 ? " days" : " day");
        }

        if (seconds >= 3600) {
            int hours = (int) (seconds / 3600);
            seconds %= 3600;

            if (message.length() > 0) message.append(", ");

            message.append(hours).append(hours > 1 ? " hours" : " hour");
        }

        if (seconds >= 60) {
            int min = (int) (seconds / 60);
            seconds %= 60;

            if (message.length() > 0) message.append(", ");

            message.append(min).append(min > 1 ? " minutes" : " minute");
        }

        if (seconds >= 0) {
            if (message.length() > 0) message.append(", ");

            message.append(seconds).append(seconds > 1 ? " seconds" : " second");
        }

        return message.toString();
    }

}
