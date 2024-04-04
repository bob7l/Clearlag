package me.minebuilders.clearlag.tasks;

import me.minebuilders.clearlag.Clearlag;
import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.config.ConfigHandler;
import me.minebuilders.clearlag.managers.EntityManager;
import me.minebuilders.clearlag.modules.ClearModule;
import me.minebuilders.clearlag.modules.ClearlagModule;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;

import java.lang.reflect.Method;
import java.util.HashMap;

@ConfigPath(path = "halt-command")
public class HaltTask extends ClearlagModule implements Listener {

    @ConfigValue
    private boolean removeEntities;

    @ConfigValue
    private boolean disableNaturalEntitySpawning;

    @AutoWire
    private EntityManager entityManager;

    @AutoWire
    private ConfigHandler config;

    private HashMap<World, Integer[]> valuelist;

    @Override
    public void setEnabled() {
        super.setEnabled();

        valuelist = new HashMap<>(Bukkit.getWorlds().size());

        for (World w : Bukkit.getWorlds()) {

            if (removeEntities) {

                entityManager.removeEntities(new ClearModule() {

                    @Override
                    public boolean isRemovable(Entity e) {
                        return (((e instanceof Item)) || ((e instanceof TNTPrimed)) || ((e instanceof ExperienceOrb)) || ((e instanceof FallingBlock)) || ((e instanceof Monster)));
                    }

                    @Override
                    public boolean isWorldEnabled(World w) {
                        return true;
                    }

                });
            }

            if (disableNaturalEntitySpawning) {

                Integer[] values = new Integer[6];

                values[0] = w.getAmbientSpawnLimit();
                w.setAmbientSpawnLimit(0);
                values[1] = w.getAnimalSpawnLimit();
                w.setAnimalSpawnLimit(0);
                values[2] = w.getMonsterSpawnLimit();
                w.setMonsterSpawnLimit(0);
                values[3] = (int) w.getTicksPerAnimalSpawns();
                w.setTicksPerAnimalSpawns(0);
                values[4] = (int) w.getTicksPerMonsterSpawns();
                w.setTicksPerMonsterSpawns(0);
                values[5] = w.getWaterAnimalSpawnLimit();
                w.setWaterAnimalSpawnLimit(0);

                valuelist.put(w, values);
            }
        }

        PluginManager pm = Clearlag.getInstance().getServer().getPluginManager();

        Method[] methods = this.getClass().getDeclaredMethods();

        for (final Method method : methods) {

            final EventHandler he = method.getAnnotation(EventHandler.class);

            if (he != null && config.getConfig().getBoolean("halt-command.halted." + config.javaToConfigValue(method.getName()))) {

                Class<?>[] params = method.getParameterTypes();

                if (!Event.class.isAssignableFrom(params[0]) || params.length != 1) {
                    continue;
                }

                final Class<? extends Event> eventClass = params[0].asSubclass(Event.class);

                method.setAccessible(true);

                EventExecutor executor = new EventExecutor() {

                    public void execute(Listener listener, Event event) throws EventException {

                        try {

                            if (!eventClass.isAssignableFrom(event.getClass())) {
                                return;
                            }

                            method.invoke(listener, event);

                        } catch (Exception ex) {
                            throw new EventException(ex.getCause());
                        }
                    }
                };


                pm.registerEvent(eventClass, this, he.priority(), executor, Clearlag.getInstance(), he.ignoreCancelled());
            }
        }
    }

    @Override
    public void setDisabled() {
        super.setDisabled();

        if (!valuelist.isEmpty()) {

            for (World w : valuelist.keySet()) {
                Integer[] values = valuelist.get(w);
                w.setAmbientSpawnLimit(values[0]);
                w.setAnimalSpawnLimit(values[1]);
                w.setMonsterSpawnLimit(values[2]);
                w.setTicksPerAnimalSpawns(values[3]);
                w.setTicksPerMonsterSpawns(values[4]);
                w.setWaterAnimalSpawnLimit(values[5]);
            }
        }

        valuelist = null;

        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void fire(BlockIgniteEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void fireBurn(BlockBurnEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void explosion(EntityExplodeEvent e) {
        e.setCancelled(true);
        e.blockList().clear();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void decay(LeavesDecayEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void blockForm(BlockFormEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void blockSpread(BlockSpreadEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void blockFade(BlockFadeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void blockNaturalChange(BlockFromToEvent e) {
        e.setCancelled(true);
    }

}
