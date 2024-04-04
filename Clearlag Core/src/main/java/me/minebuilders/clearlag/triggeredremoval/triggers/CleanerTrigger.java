package me.minebuilders.clearlag.triggeredremoval.triggers;

import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.triggeredremoval.CleanerHandler;

/**
 * @author bob7l
 */
public abstract class CleanerTrigger implements Trigger {

    private boolean triggered = false;

    @ConfigValue
    private int runInterval;

    protected CleanerHandler cleanerHandler;

    public CleanerTrigger(CleanerHandler cleanerHandler) {
        this.cleanerHandler = cleanerHandler;
    }

    @Override
    public boolean runTrigger() {

        if (!triggered) {

            if (shouldTrigger()) {

                triggered = true;

                cleanerHandler.startJobs();
            }

        } else if (isRecovered()) {
            triggered = false;
        }

        return triggered;
    }

    public abstract boolean shouldTrigger();

    public boolean isRecovered() {
        return cleanerHandler.areJobsComplete();
    }

    public CleanerHandler getCleanerHandler() {
        return cleanerHandler;
    }

    @Override
    public int getCheckFrequency() {
        return runInterval;
    }
}
