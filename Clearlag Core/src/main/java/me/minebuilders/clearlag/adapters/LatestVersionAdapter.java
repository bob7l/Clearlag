package me.minebuilders.clearlag.adapters;

import me.minebuilders.clearlag.reflection.ReflectionUtil;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapView;

/**
 * @author bob7l
 */
public class LatestVersionAdapter implements VersionAdapter {

    @Override
    public boolean isCompatible() {
        // Check if MapMeta and FILLED_MAP exist (post-1.13)
        boolean hasMapMeta = ReflectionUtil.isClass("org.bukkit.inventory.meta.MapMeta");
        boolean hasFilledMap = Material.getMaterial("FILLED_MAP") != null;
        return hasMapMeta && hasFilledMap;
    }

    @Override
    public ItemStack createMapItemStack(MapView mapView) {
        ItemStack item;
        Material mapMaterial = Material.getMaterial("FILLED_MAP");
        if (mapMaterial == null) {
            // Fallback for 1.8 and earlier
            mapMaterial = Material.MAP;
            item = new ItemStack(mapMaterial);
            // In 1.8, MapView isn't directly supported with ItemMeta; map ID was set via NBT or durability
            // This is a simplified fallback; for full 1.8 support, consider NMS
        } else {
            // For 1.13 and later
            item = new ItemStack(mapMaterial);
            try {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && ReflectionUtil.isClass("org.bukkit.inventory.meta.MapMeta")) {
                    Class<?> mapMetaClass = Class.forName("org.bukkit.inventory.meta.MapMeta");
                    if (mapMetaClass.isInstance(meta)) {
                        java.lang.reflect.Method setMapViewMethod = mapMetaClass.getMethod("setMapView", MapView.class);
                        setMapViewMethod.invoke(meta, mapView);
                        item.setItemMeta(meta);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return item;
    }

    @Override
    public boolean isMapItemStackEqual(ItemStack itemStack, ItemStack itemStack2) {
        if (itemStack == null || itemStack2 == null || itemStack.getType() != itemStack2.getType()) {
            return false;
        }

        Material mapMaterial = Material.getMaterial("FILLED_MAP");
        if (mapMaterial != null && isCompatible()) {
            // For 1.13 and later
            try {
                ItemMeta meta = itemStack.getItemMeta();
                ItemMeta meta2 = itemStack2.getItemMeta();
                if (meta == meta2) return true;
                if (meta == null || meta2 == null) return false;

                if (ReflectionUtil.isClass("org.bukkit.inventory.meta.MapMeta")) {
                    Class<?> mapMetaClass = Class.forName("org.bukkit.inventory.meta.MapMeta");
                    if (mapMetaClass.isInstance(meta) && mapMetaClass.isInstance(meta2)) {
                        java.lang.reflect.Method getMapViewMethod = mapMetaClass.getMethod("getMapView");
                        Object mapView = getMapViewMethod.invoke(meta);
                        Object mapView2 = getMapViewMethod.invoke(meta2);
                        return mapView != null && mapView.equals(mapView2);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            // Fallback for 1.8 and earlier
            // In 1.8, compare based on item type and durability (map ID was stored in durability)
            return itemStack.getDurability() == itemStack2.getDurability();
        }
        return false;
    }

    @Override
    public void setItemEntityAge(Item item, int ticks) {
        if (item != null) {
            item.setTicksLived(ticks);
        }
    }
}