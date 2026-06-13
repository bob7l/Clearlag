package me.minebuilders.clearlag.modules;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import me.minebuilders.clearlag.ClearLag;

public abstract class TaskModule extends ClearlagModule implements Runnable {

    private WrappedTask task;

    @Override
    public void setEnabled() {
        super.setEnabled();

        task = startTask();
    }

    protected WrappedTask startTask() {
        return ClearLag.scheduler().runTimer(this, getInterval(), getInterval());
    }

    @Override
    public void setDisabled() {
        super.setDisabled();
        if (task != null) {
            ClearLag.scheduler().cancelTask(task);
            task = null;
        }
    }

    public int getInterval() {
        return 20;
    }
}
