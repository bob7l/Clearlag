package me.minebuilders.clearlag.statrenderers;

import me.minebuilders.clearlag.ClearLag;
import me.minebuilders.clearlag.adapters.VersionAdapter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.*;
import org.jetbrains.annotations.NotNull;
import com.tcoded.folialib.wrapper.task.WrappedTask;

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

    private final WrappedTask taskId;

    public StatRenderer(Player observer, int sampleTicks, ItemStack mapItemStack, VersionAdapter versionAdapter, MapView mapView) {
        this.observer = observer;
        this.mapView = mapView;
        this.mapItemStack = mapItemStack;
        this.versionAdapter = versionAdapter;
        this.sampleTicks = sampleTicks;

        taskId = ClearLag.scheduler().runTimer(this, sampleTicks, sampleTicks);
    }

    public void cancel() {

        mapView.removeRenderer(this);

        ClearLag.scheduler().cancelTask(taskId);
    }

    public abstract void tick();

    public abstract void draw(MapView mapView, MapCanvas mapCanvas, Player player);

    @Override
    public void run() {
        if (!observer.isOnline() || !versionAdapter.isMapItemStackEqual(observer.getItemInHand(), mapItemStack)) {
            cancel();
            return;
        }

        pendingRefresh = true;
        tick();
    }

    @Override
    public void render(@NotNull MapView mapView, MapCanvas mapCanvas, @NotNull Player player) {
        final MapCursorCollection mapCursorCollection = mapCanvas.getCursors();

        while (mapCursorCollection.size() > 0) {
            mapCursorCollection.removeCursor(mapCursorCollection.getCursor(0));
        }

        if (!pendingRefresh) {
            return;
        }

        for (int i = width; i >= 0; --i) {
            for (int j = height; j >= 0; --j) {
                mapCanvas.setPixel(i, j, MapPalette.TRANSPARENT);
            }
        }

        draw(mapView, mapCanvas, player);
        pendingRefresh = false;
    }
}
