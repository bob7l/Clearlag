package me.minebuilders.clearlag.listeners;

import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.modules.EventModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;

@ConfigPath(path = "firespread-reducer")
public class FireSpreadListener extends EventModule {

    private long nextAllowedSpread = System.currentTimeMillis();

    @ConfigValue
    private int time;

//    //Remove me
//    @EventHandler
//    public void onMap(MapInitializeEvent event) {
//
//        System.out.println("YEEES!!");
//
//        event.getMap().setScale(MapView.Scale.NORMAL);
//
//        final List<MapRenderer> list = new ArrayList<>(event.getMap().getRenderers());
//
//        for (MapRenderer mapRenderer : list)
//            event.getMap().removeRenderer(mapRenderer);
//
//        event.getMap().getRenderers().clear();
//
//        event.getMap().addRenderer(memoryRenderer);
//
//    }
    @EventHandler
    public void fireSpread(BlockIgniteEvent event) {

        if (event.getCause() == IgniteCause.SPREAD) {

            if (System.currentTimeMillis() > nextAllowedSpread) {
                nextAllowedSpread = (System.currentTimeMillis() + time);
            } else {
                event.setCancelled(true);
            }
        }
    }

}