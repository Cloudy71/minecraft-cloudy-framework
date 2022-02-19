package cz.cloudy.minecraft.core.particles;

import org.bukkit.Bukkit;

import java.util.List;

/**
 * @author Cloudy
 */
public class ParticleJob {
    /**
     * Bukkit task id list.
     */
    protected List<Integer> taskIds;

    /**
     * Default constructor with task id list.
     *
     * @param taskIds Task id list
     */
    protected ParticleJob(List<Integer> taskIds) {
        this.taskIds = taskIds;
    }

    /**
     * Stops particle job.
     */
    public void stop() {
        taskIds.forEach(integer -> Bukkit.getScheduler().cancelTask(integer));
        taskIds.clear();
        taskIds = null;
    }
}
