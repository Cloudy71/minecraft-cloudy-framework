package cz.cloudy.minecraft.core.await;

/**
 * @param <T> Awaiting object data
 * @author Cloudy
 */
@FunctionalInterface
public interface AwaitConsumer<T> {
    /**
     * Invoked when consumer is registered in await list.
     */
    default void created() {
    }

    /**
     * Runs process for consumer.
     *
     * @param obj Awaiting object data
     */
    default void process(T obj) {
        accept(this, obj);
    }

    /**
     * Dismisses current consumer and sets it for future removal.
     */
    default void dismiss() {
        Await.dismiss(this);
    }

    /**
     * Accept method for consumer.
     *
     * @param consumer This object
     * @param obj      Awaiting object data
     */
    void accept(AwaitConsumer<T> consumer, T obj);
}
