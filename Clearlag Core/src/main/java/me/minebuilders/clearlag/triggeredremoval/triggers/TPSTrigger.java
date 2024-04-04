package me.minebuilders.clearlag.triggeredremoval.triggers;

import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.tasks.TPSTask;
import me.minebuilders.clearlag.triggeredremoval.CleanerHandler;

/**
 * @author bob7l
 */
public class TPSTrigger extends CleanerTrigger {

    @ConfigValue
    private double  tpsTrigger;

    @ConfigValue
    private double tpsRecover;

    @AutoWire
    private TPSTask tpsTask;

    public TPSTrigger(CleanerHandler cleanerHandler) {
        super(cleanerHandler);
    }

    @Override
    public boolean shouldTrigger() {
        return tpsTask.getTPS() <= tpsTrigger;
    }

    @Override
    public boolean isRecovered() {
        return super.isRecovered() && tpsTask.getTPS() >= tpsRecover;
    }
}
