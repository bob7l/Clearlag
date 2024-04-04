package me.minebuilders.clearlag.adapters;

import me.minebuilders.clearlag.reflection.ReflectionUtil;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

/**
 * @author bob7l
 */
public class LatestVersionAdapter implements VersionAdapter {

    @Override
    public boolean isCompatible() {
        return (ReflectionUtil.isClass("org.bukkit.inventory.meta.MapMeta") && Material.getMaterial("FILLED_MAP") != null);
    }

    @Override
    public ItemStack createMapItemStack(MapView mapView) {

        final ItemStack item = new ItemStack(Material.FILLED_MAP);

        final MapMeta meta = (MapMeta) item.getItemMeta();

        meta.setMapView(mapView);

        item.setItemMeta(meta);

        return item;
    }

    @Override
    public boolean isMapItemStackEqual(ItemStack itemStack, ItemStack itemStack2) {

        if (itemStack.getType() != itemStack2.getType())
            return false;

        final MapMeta meta = (MapMeta) itemStack.getItemMeta();
        final MapMeta meta2 = (MapMeta) itemStack2.getItemMeta();

        if (meta == meta2)
            return true;

        if (meta == null || meta2 == null || meta.getMapView() == null || meta2.getMapView() == null)
            return false;

        return meta.getMapView().equals(meta2.getMapView());
    }

    @Override
    public void setItemEntityAge(Item item, int ticks) {
        item.setTicksLived(ticks);
    }
}
