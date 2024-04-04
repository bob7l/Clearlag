package me.minebuilders.clearlag.triggeredremoval.cleanermodules;

import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.config.ConfigValueType;
import me.minebuilders.clearlag.modules.BroadcastHandler;
import me.minebuilders.clearlag.modules.ClearlagModule;
import me.minebuilders.clearlag.modules.TaskModule;

import java.util.HashMap;

/**
 * @author bob7l
 */
public class WarningJob extends TaskModule {

    @ConfigValue(valueType = ConfigValueType.WARN_ARRAY)
    private HashMap<Integer, String[]> warnings;

    @ConfigValue
    private int executeJobTime;

    @AutoWire
    private BroadcastHandler broadcastHandler;

    private ClearlagModule wrappedJob;

    private int timer = 0;

    public WarningJob(ClearlagModule wrappedJob) {
        this.wrappedJob = wrappedJob;
    }

    public ClearlagModule getWrappedJob() {
        return wrappedJob;
    }

    public void setWrappedJob(ClearlagModule wrappedJob) {
        this.wrappedJob = wrappedJob;
    }

    @Override
    public void setEnabled() {
        super.setEnabled();

        timer = 0;
    }

    @Override
    public void run() {

        final String[] broadcastWarning = warnings.get(++timer);

        if (broadcastWarning != null) {

            for (String message : broadcastWarning)
                broadcastHandler.broadcast(message.replace("+remaining", "" + (executeJobTime - timer)));
        }

        if (timer >= executeJobTime) {

            super.setDisabled();

            wrappedJob.setEnabled();
        }
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() || wrappedJob.isEnabled();
    }

    @Override
    public void setDisabled() {

        if (super.isEnabled())
            super.setDisabled();
        else
            wrappedJob.setDisabled();
    }
}
