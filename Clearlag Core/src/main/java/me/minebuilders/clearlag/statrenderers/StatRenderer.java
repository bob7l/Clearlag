package me.minebuilders.clearlag.statrenderers;

import me.minebuilders.clearlag.Clearlag;
import me.minebuilders.clearlag.adapters.VersionAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.*;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author bob7l
 */
public abstract class StatRenderer extends MapRenderer implements Runnable {

    protected boolean pendingRefresh = true;

    protected int width = 128; //256

    protected int height = 128; //256

    protected final Player observer;

    protected final ItemStack mapItemStack;

    protected final MapView mapView;

    protected final int sampleTicks;

    private final VersionAdapter versionAdapter;

    private final BukkitTask taskId;

    public StatRenderer(Player observer, int sampleTicks, ItemStack mapItemStack, VersionAdapter versionAdapter, MapView mapView) {
        this.observer = observer;
        this.mapView = mapView;
        this.mapItemStack = mapItemStack;
        this.versionAdapter = versionAdapter;
        this.sampleTicks = sampleTicks;

        taskId = Bukkit.getScheduler().runTaskTimer(Clearlag.getInstance(), this, sampleTicks, sampleTicks);
    }

    public void cancel() {

        mapView.removeRenderer(this);

        taskId.cancel();
    }

    public abstract void tick();

    public abstract void draw(MapView mapView, MapCanvas mapCanvas, Player player);

    @Override
    public void run() {

        if (!observer.isOnline() || observer.getItemInHand() == null || !versionAdapter.isMapItemStackEqual(observer.getItemInHand(), mapItemStack)) {

            cancel();

            return;
        }

        pendingRefresh = true;

        tick();
    }

    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {

        final MapCursorCollection mapCursorCollection = mapCanvas.getCursors();

        while (mapCursorCollection.size() > 0)
            mapCursorCollection.removeCursor(mapCursorCollection.getCursor(0));

        if (!pendingRefresh)
            return;

        for (int i = width; i >= 0; --i) {
            for (int j = height; j >= 0; --j) {
                mapCanvas.setPixel(i, j, MapPalette.TRANSPARENT);
            }
        }

        draw(mapView, mapCanvas, player);

        pendingRefresh = false;
    }
}
