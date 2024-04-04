package me.minebuilders.clearlag.modules;


import me.minebuilders.clearlag.Clearlag;
import org.bukkit.Bukkit;

public abstract class TaskModule extends ClearlagModule implements Runnable {

    protected int taskid = -2;

    @Override
    public void setEnabled() {
        super.setEnabled();

        taskid = startTask();
    }

    protected int startTask() {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(Clearlag.getInstance(), this, getInterval(), getInterval());
    }

    @Override
    public void setDisabled() {
        super.setDisabled();
        Bukkit.getScheduler().cancelTask(taskid);
    }

    public int getInterval() {
        return 20;
    }

}
