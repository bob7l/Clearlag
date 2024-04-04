package me.minebuilders.clearlag.listeners;

import me.minebuilders.clearlag.Util;
import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.config.ConfigHandler;
import me.minebuilders.clearlag.modules.EventModule;
import me.minebuilders.clearlag.reflection.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.Map;

@ConfigPath(path = "mob-range")
public class EntityAISpawnListener extends EventModule {

    private final Map<EntityType, Double> mobRanges = new IdentityHashMap<>();

    private Method getHandleMethod,
            getAttriInstanceMethod,
            setAttriMethod;

    private Object followRangeConst;

    @AutoWire
    private ConfigHandler configHandler;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) throws Exception {
        setEntityRange(event.getEntity());
    }

    @Override
    public void setEnabled() {

        try {
            String version = Util.getRawBukkitVersion();

            Class<?> craftEntity = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftEntity");
            Class<?> nmsEntity = Class.forName("net.minecraft.server." + version + ".EntityLiving");
            Class<?> genAttributes = Class.forName("net.minecraft.server." + version + ".GenericAttributes");
            Class<?> attribClass = Class.forName("net.minecraft.server." + version + ".AttributeInstance");

            Field followRangeField = genAttributes.getDeclaredField(version.contains("1_7") ? "b" : "FOLLOW_RANGE");
            followRangeField.setAccessible(true);

            followRangeConst = followRangeField.get(null);

            setAttriMethod = ReflectionUtil.getMethodByName(attribClass, "setValue");
            setAttriMethod.setAccessible(true);

            getHandleMethod = craftEntity.getDeclaredMethod("getHandle");
            getHandleMethod.setAccessible(true);

            getAttriInstanceMethod = ReflectionUtil.getMethodByName(nmsEntity, "getAttributeInstance");
            getAttriInstanceMethod.setAccessible(true);

            for (World w : Bukkit.getWorlds()) {
                for (Entity e : w.getEntities()) {
                    if (e instanceof LivingEntity)
                        setEntityRange(e);
                }
            }

            super.setEnabled();

        } catch (Exception e) {
            Util.warning("Failed to initialize 'mob-range' controller ~ This is possibly caused by an unsupported Bukkit/Spigot server version");
            return;
        }

        Configuration config = configHandler.getConfig();

        for (String s : config.getConfigurationSection("mob-range").getKeys(false)) {
            EntityType type = Util.getEntityTypeFromString(s);

            if (type != null) {
                mobRanges.put(type, config.getDouble("mob-range." + s));
            }
        }
    }

    private void setEntityRange(Entity e) throws Exception {
        Double value = mobRanges.get(e.getType());

        if (value != null) {
            setAttriMethod.invoke(
                    getAttriInstanceMethod.invoke(getHandleMethod.invoke(e), followRangeConst),
                    value
            );
        }
    }
}