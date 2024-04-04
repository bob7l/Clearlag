package me.minebuilders.clearlag.listeners;

import me.minebuilders.clearlag.adapters.VersionAdapter;
import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.config.ConfigValueType;
import me.minebuilders.clearlag.modules.EventModule;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ItemSpawnEvent;

import java.util.Map;

@ConfigPath(path = "item-spawn-age-setter")
public class ItemLivetimeListener extends EventModule {

    @ConfigValue(valueType = ConfigValueType.MATERIAL_INT_MAP)
    private Map<Material, Integer> items;

    @AutoWire
    private VersionAdapter versionAdapter;

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onItemSpawn(ItemSpawnEvent event) {

        final Integer age = items.get(event.getEntity().getItemStack().getType());

        if (age != null) {

            final int ageInTicks = age * 20;

            if (event.getEntity().getTicksLived() < ageInTicks || event.getEntity().getTicksLived() == 0) {
                versionAdapter.setItemEntityAge(event.getEntity(), age * 20);
            }
        }
    }
}
