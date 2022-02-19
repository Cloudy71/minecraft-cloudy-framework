package cz.cloudy.minecraft.core.await;

import cz.cloudy.minecraft.core.CoreRunnerPlugin;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

/**
 * @param <T> Awaiting consumer data
 * @author Cloudy
 */
public interface AwaitTimedConsumer<T>
        extends AwaitConsumer<T> {
    /**
     *
     */
    Map<AwaitConsumer<?>, Integer> tasks = new HashMap<>();

    @Override
    default void created() {
        if (tasks.containsKey(this))
            return;

        final AwaitTimedConsumer<T> self = this;
        tasks.put(this, Bukkit.getScheduler().scheduleSyncDelayedTask(
                CoreRunnerPlugin.singleton,
                () -> {
                    timeout();
                    AwaitConsumer.super.dismiss();
                    tasks.remove(self);
                },
                ticks()
        ));
    }

    @Override
    default void dismiss() {
        AwaitConsumer.super.dismiss();
        Integer taskId;
        if ((taskId = tasks.get(this)) == null)
            return;

        Bukkit.getScheduler().cancelTask(taskId);
        tasks.remove(this);
    }

    @Override
    default void process(T obj) {
        AwaitConsumer.super.process(obj);
    }

    /**
     * The time in ticks for how long this await consumer processes.
     *
     * @return Time in ticks
     */
    int ticks();

    /**
     * Invoked when {@link AwaitTimedConsumer#ticks()} time elapsed.
     */
    default void timeout() {
    }
}
