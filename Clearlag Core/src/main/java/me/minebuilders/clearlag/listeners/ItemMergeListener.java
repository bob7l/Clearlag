package me.minebuilders.clearlag.listeners;

import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.modules.EventModule;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

@ConfigPath(path = "item-merger")
public class ItemMergeListener extends EventModule {

    @ConfigValue
    private int radius;

    //Yes yes. Very ugly/outdated code. Ignore this class.. Shouldn't even be used anymore in Spigot
    @EventHandler
    public void onItemDrop(ItemSpawnEvent event) {

        ItemStack i = event.getEntity().getItemStack();
        Material type = i.getType();
        int c = i.getAmount();
        MaterialData data = i.getData();

        for (Entity entity : event.getEntity().getNearbyEntities(radius, radius, radius)) {

            if (entity instanceof Item && !entity.isDead()) {

                ItemStack ni = ((Item) entity).getItemStack();

                if (!entity.isDead() && type == ni.getType() && data.equals(ni.getData()) && i.getDurability() == ni.getDurability()
                        && i.getMaxStackSize() >= (ni.getAmount() + c)) {

                    entity.remove();

                    i.setAmount(ni.getAmount() + c);

                    return;
                }
            }
        }
    }

}
