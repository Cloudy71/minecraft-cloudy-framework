package cz.cloudy.minecraft.core.particles;

import cz.cloudy.minecraft.core.CoreRunnerPlugin;
import cz.cloudy.minecraft.core.componentsystem.annotations.Component;
import org.bukkit.*;
import org.bukkit.util.Vector;

import java.util.ArrayList;

/**
 * @author Cloudy
 */
@Component
public class Particles {

    /**
     * Collects all created particle jobs and merges them into one.
     *
     * @param jobs Particle jobs
     * @return Merged particle job
     */
    public ParticleJob collection(ParticleJob... jobs) {
        ParticleJob job = new ParticleJob(new ArrayList<>());
        for (ParticleJob particleJob : jobs) {
            job.taskIds.addAll(particleJob.taskIds);
        }
        return job;
    }

    /**
     * Creates pulse line particle job.
     *
     * @param world          World
     * @param from           From vector
     * @param to             To vector
     * @param color          Color
     * @param size           Size
     * @param step           Step
     * @param pulseCount     Pulse count
     * @param pulseTickDelay Pulse tick delay
     * @return Particle job
     */
    public ParticleJob pulseLine(World world, Vector from, Vector to, Color color, float size, float step, int pulseCount, int pulseTickDelay) {
        ParticleJob job = new ParticleJob(new ArrayList<>());
        for (int i = 0; i < pulseCount; ++i) {
            job.taskIds.add(Bukkit.getScheduler().scheduleSyncDelayedTask(
                    CoreRunnerPlugin.singleton,
                    () -> {
                        float distance = (float) from.distance(to);
                        Vector add = to.clone().subtract(from).normalize().multiply(step);
                        Vector loc = from.clone();
                        for (float j = 0f; j < distance; j += step) {
                            world.spawnParticle(
                                    Particle.REDSTONE,
                                    new Location(world, loc.getX(), loc.getY(), loc.getZ()),
                                    1,
                                    new Particle.DustOptions(color, size)
                            );
                            loc.add(add);
                        }
                    },
                    (long) i * pulseTickDelay
            ));
        }
        return job;
    }

    /**
     * Creates pulse line particle job.
     *
     * @param world          World
     * @param from           From vector
     * @param to             To vector
     * @param color          Color
     * @param size           Size
     * @param pulseCount     Pulse count
     * @param pulseTickDelay Pulse tick delay
     * @return Particle job
     */
    public ParticleJob pulseLine(World world, Vector from, Vector to, Color color, float size, int pulseCount, int pulseTickDelay) {
        return pulseLine(world, from, to, color, size, size * .1f, pulseCount, pulseTickDelay);
    }

    /**
     * Creates pulse line particle job.
     *
     * @param world          World
     * @param from           From vector
     * @param to             To vector
     * @param color          Color
     * @param pulseCount     Pulse count
     * @param pulseTickDelay Pulse tick delay
     * @return Particle job
     */
    public ParticleJob pulseLine(World world, Vector from, Vector to, Color color, int pulseCount, int pulseTickDelay) {
        return pulseLine(world, from, to, color, 1f, pulseCount, pulseTickDelay);
    }
}
