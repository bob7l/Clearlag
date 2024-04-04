package me.minebuilders.clearlag.triggeredremoval;

import me.minebuilders.clearlag.modules.ClearlagModule;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bob7l
 */
public class CleanerHandler {

    private final List<ClearlagModule> cleanerJobs = new ArrayList<>();

    public void addCleanerJob(ClearlagModule module) {
        this.cleanerJobs.add(module);
    }

    public void removeCleanerJob(ClearlagModule module) {
        this.cleanerJobs.remove(module);
    }

    public void startJobs() {

        for (ClearlagModule job : cleanerJobs)
            job.setEnabled();
    }

    public boolean areJobsComplete() {

        for (ClearlagModule job : cleanerJobs)
            if (job.isEnabled())
                return false;

        return true;
    }
}
