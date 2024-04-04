package me.minebuilders.clearlag.config.configvalues;

import me.minebuilders.clearlag.Util;
import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.config.ConfigHandler;
import org.bukkit.Material;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by TCP on 2/3/2016.
 */
public class MaterialSetCV implements ConfigData<EnumSet<Material>> {

    @AutoWire
    private ConfigHandler configHandler;

    @Override
    public EnumSet<Material> getValue(String path) {

        List<String> types = configHandler.getConfig().getStringList(path);

        List<Material> materials = new LinkedList<>();

        for (String str : types) {

            Material material = Material.matchMaterial(str);

            if (material == null)
                material = Material.getMaterial(str);

            if (material != null)
                materials.add(material);
            else {
                Util.warning("Item type '" + str + "' does not match any Materials found on your Craftbukkit version.");
            }
        }

        return (materials.size() > 0 ? EnumSet.copyOf(materials) : EnumSet.noneOf(Material.class));
    }

}
