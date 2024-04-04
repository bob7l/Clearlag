package me.minebuilders.clearlag.config.configvalues;

import me.minebuilders.clearlag.Util;
import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.config.ConfigHandler;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class MaterialIntegerMapCV implements ConfigData<Map<Material, Integer>> {

    @AutoWire
    private ConfigHandler configHandler;

    @Override
    public Map<Material, Integer> getValue(String path) {

        ConfigurationSection section = configHandler.getConfig().getConfigurationSection(path);

        Map<Material, Integer> materialIntegerMap = new HashMap<>();

        if (section != null) {

            for (String key : section.getKeys(false)) {

                Material material = Material.matchMaterial(key);

                if (material == null)
                    material = Material.getMaterial(key);

                if (material != null)
                    materialIntegerMap.put(material, configHandler.getConfig().getInt(path + "." + key));
                else {
                    Util.warning("Item type '" + key + "' does not match any Materials found on your Craftbukkit version.");
                }
            }
        }
        return materialIntegerMap;
    }

}
