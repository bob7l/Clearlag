package me.minebuilders.clearlag.adapters;

import me.minebuilders.clearlag.Util;
import me.minebuilders.clearlag.reflection.ReflectionUtil;
import org.bukkit.Material;
import org.bukkit.entity.Item;
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
        return (Material.getMaterial("MAP") != null && mcItemSetAge != null && itemField != null);
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
}
