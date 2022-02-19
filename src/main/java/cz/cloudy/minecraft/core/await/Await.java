package cz.cloudy.minecraft.core.await;

import cz.cloudy.minecraft.core.componentsystem.types.CommandData;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerEvent;

import java.util.*;

/**
 * @author Cloudy
 */
// TODO: Add timed consumers
public class Await {
    /**
     *
     */
    protected static final Map<Class<? extends Event>, Map<Object, List<AwaitConsumer<?>>>> events                 =
            new HashMap<>();
    /**
     *
     */
    protected static final Map<String, Map<UUID, List<AwaitConsumer<CommandData>>>>         commands               =
            new HashMap<>();
    /**
     *
     */
    protected static final Set<AwaitConsumer<?>>                                            dismissedConsumersList = new HashSet<>();

    /**
     * Awaits for specified player event.
     *
     * @param player    Player
     * @param eventType EventType
     * @param consumers Consumers
     * @param <T>       Event type
     */
    @SafeVarargs
    public static <T extends PlayerEvent> void playerEvent(Player player, Class<T> eventType, AwaitConsumer<T>... consumers) {
        for (AwaitConsumer<T> consumer : consumers) {
            consumer.created();
        }
        events.computeIfAbsent(eventType, aClass -> new HashMap<>())
              .computeIfAbsent(player.getUniqueId(), o -> new ArrayList<>())
              .addAll(Arrays.stream(consumers).toList());
    }

    /**
     * Awaits for specified player command.
     *
     * @param player    Player
     * @param command   Command
     * @param consumers Consumers
     */
    @SafeVarargs
    public static void playerCommand(Player player, String command, AwaitConsumer<CommandData>... consumers) {
        for (AwaitConsumer<CommandData> consumer : consumers) {
            consumer.created();
        }
        commands.computeIfAbsent(command, s -> new HashMap<>())
                .computeIfAbsent(player.getUniqueId(), uuid -> new ArrayList<>())
                .addAll(Arrays.stream(consumers).toList());
    }

    /**
     * Dismisses all awaits for player command.
     *
     * @param player  Player
     * @param command Command
     */
    public static void dismissPlayerCommand(Player player, String command) {
        if (!commands.containsKey(command) || !commands.get(command).containsKey(player.getUniqueId()))
            return;

        for (AwaitConsumer<CommandData> consumer : commands.get(command).get(player.getUniqueId())) {
            consumer.dismiss();
        }
    }

    /**
     * Processes all awaits for specified event data.
     *
     * @param event Event data
     */
    @SuppressWarnings("unchecked")
    public static void process(Event event) {
        if (!events.containsKey(event.getClass()))
            return;

        Map<Object, List<AwaitConsumer<?>>> consumerMap = events.get(event.getClass());
        List<AwaitConsumer<?>> consumerList;
        Object key = null;
        if (event instanceof PlayerEvent playerEvent)
            key = playerEvent.getPlayer().getUniqueId();

        consumerList = consumerMap.get(key);
        if (consumerList == null)
            return;

        List<AwaitConsumer<?>> localDismissList = new ArrayList<>();
        for (AwaitConsumer<?> consumer : consumerList) {

            // Don't run if it was already dismissed, just pass it into remove list.
            if (dismissedConsumersList.contains(consumer)) {
                localDismissList.add(consumer);
                continue;
            }
            ((AwaitConsumer<Event>) consumer).process(event);
            // If it was dismissed after consumer process, still pass it into remove list.
            if (dismissedConsumersList.contains(consumer))
                localDismissList.add(consumer);
        }
        for (AwaitConsumer<?> consumer : localDismissList) {
            consumerList.remove(consumer);
            dismissedConsumersList.remove(consumer);
        }
    }

    /**
     * Processes all awaits for command data.
     *
     * @param commandData Command data
     */
    @SuppressWarnings("SuspiciousMethodCalls")
    public static void process(CommandData commandData) {
        if (!commandData.isPlayer() || !commands.containsKey(commandData.command().getName()))
            return;

        Map<UUID, List<AwaitConsumer<CommandData>>> consumerMap = commands.get(commandData.command().getName());
        List<AwaitConsumer<CommandData>> consumerList = consumerMap.get(commandData.getPlayer().getUniqueId());
        if (consumerList == null)
            return;

        List<AwaitConsumer<?>> localDismissList = new ArrayList<>();
        for (AwaitConsumer<?> consumer : consumerList) {

            // Don't run if it was already dismissed, just pass it into remove list.
            if (dismissedConsumersList.contains(consumer)) {
                localDismissList.add(consumer);
                continue;
            }
            ((AwaitConsumer<CommandData>) consumer).process(commandData);
            // If it was dismissed after consumer process, still pass it into remove list.
            if (dismissedConsumersList.contains(consumer))
                localDismissList.add(consumer);
        }
        for (AwaitConsumer<?> consumer : localDismissList) {
            consumerList.remove(consumer);
            dismissedConsumersList.remove(consumer);
        }
    }

    /**
     * Dismisses consumer instance and sets it for removal in the future.
     *
     * @param awaitConsumer Consumer instance
     */
    protected static void dismiss(AwaitConsumer<?> awaitConsumer) {
        if (awaitConsumer == null)
            return;

        dismissedConsumersList.add(awaitConsumer);
    }
}
