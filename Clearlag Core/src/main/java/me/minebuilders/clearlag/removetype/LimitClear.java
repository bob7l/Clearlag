package me.minebuilders.clearlag.removetype;

import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.config.ConfigValueType;
import me.minebuilders.clearlag.modules.ClearModule;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ConfigPath(path = "limit")
public class LimitClear extends ClearModule {

    @ConfigValue
    private boolean item;
    @ConfigValue(valueType = ConfigValueType.MATERIAL_SET)
    private Set<Material> itemFilter;
    @ConfigValue
    private final List<String> worldFilter = new ArrayList<String>();
    @ConfigValue
    private boolean itemframe;
    @ConfigValue
    private boolean fallingBlock;
    @ConfigValue
    private boolean boat;
    @ConfigValue
    private boolean experienceOrb;
    @ConfigValue
    private boolean painting;
    @ConfigValue
    private boolean projectile;
    @ConfigValue
    private boolean primedTnt;
    @ConfigValue
    private boolean minecart;


    @Override
    public boolean isRemovable(Entity e) {
        if (e instanceof Item) return (item && !itemFilter.contains((((Item) e).getItemStack().getType())));
        if (e instanceof ItemFrame) return itemframe;
        if (e instanceof FallingBlock) return fallingBlock;
        if (e instanceof Boat) return (e.isEmpty() && boat);
        if (e instanceof ExperienceOrb) return experienceOrb;
        if (e instanceof Painting) return painting;
        if (e instanceof Projectile) return projectile;
        if (e instanceof TNTPrimed) return primedTnt;
        return e instanceof Minecart && (e.isEmpty() && minecart);
    }

    @Override
    public boolean isWorldEnabled(World w) {
        return !worldFilter.contains(w.getName());
    }

}
