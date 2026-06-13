package me.minebuilders.clearlag.adapters;

import me.minebuilders.clearlag.Util;
import me.minebuilders.clearlag.reflection.ReflectionUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author bob7l
 */
public class LegacyVersionAdapter implements VersionAdapter {

    private static final Field itemField = ReflectionUtil.getField(
            ReflectionUtil.getClass("org.bukkit.craftbukkit." + Util.getRawBukkitVersion() + ".entity", "CraftItem"),
            "item"
    );

    private static final Field mcItemSetAge = ReflectionUtil.getField(
            ReflectionUtil.getClass("net.minecraft.server." + Util.getRawBukkitVersion(), "EntityItem"),
            "age"
    );

    @Override
    public boolean isCompatible() {
        return Material.getMaterial("MAP") != null;
    }

    @Override
    public ItemStack createMapItemStack(MapView mapView) {
        final ItemStack mapItemStack = new ItemStack(Material.MAP, 1);
        try {
            final Method method = MapView.class.getDeclaredMethod("getId");
            method.setAccessible(true);
            short id = (short) method.invoke(mapView);
            mapItemStack.setDurability(id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mapItemStack;
    }

    @Override
    public boolean isMapItemStackEqual(ItemStack itemStack, ItemStack itemStack2) {
        return itemStack.getDurability() == itemStack2.getDurability();
    }

    @Override
    public void setItemEntityAge(Item item, int age) {
        try {
            item.setTicksLived(age);
            final Object nmsEntity = itemField.get(item);
            mcItemSetAge.set(nmsEntity, age);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendActionBar(Player player, String message) {
        try {
            player.spigot().sendMessage(
                    ChatMessageType.ACTION_BAR,
                    TextComponent.fromLegacyText(message)
            );
            return;
        } catch (Throwable ignored) {
        }

        try {
            final String ver = Util.getRawBukkitVersion();
            final Class<?> chatBaseComponent = ReflectionUtil.getClass("net.minecraft.server." + ver, "IChatBaseComponent");
            final Class<?> packetClass = ReflectionUtil.getClass("net.minecraft.server." + ver, "PacketPlayOutChat");
            final Class<?> chatSerializer = chatBaseComponent.getDeclaredClasses()[0];

            final String json = "{\"text\":\"" + message.replace("\\", "\\\\").replace("\"", "\\\"") + "\"}";
            final Object component = chatSerializer.getMethod("a", String.class).invoke(null, json);

            final Object packet = packetClass
                    .getConstructor(chatBaseComponent, byte.class)
                    .newInstance(component, (byte) 2);

            final Object handle = player.getClass().getMethod("getHandle").invoke(player);
            final Object connection = handle.getClass().getField("playerConnection").get(handle);
            connection.getClass()
                    .getMethod("sendPacket", ReflectionUtil.getClass("net.minecraft.server." + ver, "Packet"))
                    .invoke(connection, packet);
        } catch (Throwable e) {
            player.sendMessage(message);
        }
    }
}