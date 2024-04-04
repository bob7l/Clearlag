package me.minebuilders.clearlag.tasks;

import me.minebuilders.clearlag.Util;
import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.annotations.ConfigModule;
import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.config.ConfigValueType;
import me.minebuilders.clearlag.managers.EntityManager;
import me.minebuilders.clearlag.modules.BroadcastHandler;
import me.minebuilders.clearlag.modules.TaskModule;
import me.minebuilders.clearlag.removetype.AutoClear;

import java.util.HashMap;

@ConfigPath(path = "auto-removal")
public class ClearTask extends TaskModule {

    @ConfigModule
    private final AutoClear autoClear = new AutoClear();

    @ConfigValue
    private int autoremovalInterval;

    @ConfigValue(valueType = ConfigValueType.COLORED_STRINGS)
    private String[] broadcastMessage;

    @ConfigValue(valueType = ConfigValueType.WARN_ARRAY)
    private HashMap<Integer, String[]> warnings;

    @ConfigValue(valueType = ConfigValueType.PRIMITIVE)
    private boolean broadcastRemoval;

    @AutoWire
    private EntityManager entityManager;

    @AutoWire
    private BroadcastHandler broadcastHandler;

    private int interval = 0;

    public void run() {

        final String[] broadcastWarning = warnings.get(++interval);

        if (broadcastWarning != null)
            broadcastHandler.broadcast(Util.cloneAndReplaceStringArr(broadcastWarning, "+remaining", "" + (autoremovalInterval - interval)));


        if (interval >= autoremovalInterval) {

            if (broadcastRemoval)
                broadcastHandler.broadcast(Util.cloneAndReplaceStringArr(broadcastMessage, "+RemoveAmount", String.valueOf(entityManager.removeEntities(autoClear))));

            interval = 0;
        }
    }

}
