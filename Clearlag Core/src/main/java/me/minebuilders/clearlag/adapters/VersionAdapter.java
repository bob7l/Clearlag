package me.minebuilders.clearlag.adapters;

import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

/**
 * @author bob7l
 */
public interface VersionAdapter {

    boolean isCompatible();

    ItemStack createMapItemStack(MapView mapView);

    boolean isMapItemStackEqual(ItemStack itemStack, ItemStack itemStack2);

    void setItemEntityAge(Item item, int ticks);

}
