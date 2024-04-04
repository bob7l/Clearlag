package me.minebuilders.clearlag.listeners;

import me.minebuilders.clearlag.Clearlag;
import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.modules.EventModule;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;

@ConfigPath(path = "spawn-limiter")
public class MobLimitListener extends EventModule implements Runnable {

    @ConfigValue
    private int animals;

    @ConfigValue
    private int mobs;

    @ConfigValue
    private int interval;

    private int sched = -1;

    private boolean canAnimalspawn = true;

    private boolean canMobspawn = true;

    @Override
    public void run() {
        int animals = 0;
        int mobs = 0;

        for (World world : Bukkit.getWorlds()) {
            for (Entity e : world.getEntities()) {
                if (e instanceof Animals || e instanceof Villager) animals++;
                if (e instanceof Creature) mobs++;
            }
        }

        canAnimalspawn = animals < this.animals;

        canMobspawn = mobs < this.mobs;
    }

    @Override
    public void setEnabled() {
        super.setEnabled();

        sched = Bukkit.getScheduler().scheduleSyncRepeatingTask(Clearlag.getInstance(), this, interval * 20L, interval * 20L);
    }

    @Override
    public void setDisabled() {
        super.setDisabled();

        if (sched != -1) Bukkit.getServer().getScheduler().cancelTask(sched);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onMobSpawn(CreatureSpawnEvent e) {
        Entity en = e.getEntity();
        if (!canAnimalspawn && en instanceof Animals)
            e.setCancelled(true);
        else if (!canMobspawn && en instanceof Creature)
            e.setCancelled(true);
    }
}
