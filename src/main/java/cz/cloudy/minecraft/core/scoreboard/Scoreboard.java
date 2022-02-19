/*
  User: Cloudy
  Date: 30/01/2022
  Time: 14:29
*/

package cz.cloudy.minecraft.core.scoreboard;

import cz.cloudy.minecraft.core.componentsystem.annotations.Component;
import cz.cloudy.minecraft.core.componentsystem.interfaces.IComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Cloudy
 */
@Component
public class Scoreboard
        implements IComponent, Listener {
    /**
     * Scoreboard refresh interval.
     */
    public static int SCOREBOARD_REFRESH_INTERVAL = 20 * 5;

    /**
     * Scoreboard map for every connected player.
     */
    protected Map<Player, List<ScoreboardObject>> entries = new HashMap<>();

    private boolean schedulerCreated = false;

    @Override
    public void onStart() {
        if (schedulerCreated)
            return;

        Bukkit.getScheduler().scheduleSyncRepeatingTask(
                getPlugin(),
                () -> {
                    if (entries.isEmpty())
                        return;
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        for (ScoreboardObject object : entries.get(player)) {
                            object.update();
                        }
                    }
                },
                0,
                SCOREBOARD_REFRESH_INTERVAL
        );
        schedulerCreated = true;
    }

    /**
     * Adds scoreboard to the player.
     *
     * @param player Player
     * @param object Scoreboard object
     * @return Provided scoreboard object
     */
    @Nullable
    public ScoreboardObject addScoreboard(Player player, ScoreboardObject object) {
        if (object == null)
            return null;
        object.create(player);
        entries.computeIfAbsent(player, p -> new ArrayList<>()).add(object);
        return object;
    }

    /**
     * Gets player's scoreboard by name.
     *
     * @param player Player
     * @param name   Name
     * @return Scoreboard object
     */
    @Nullable
    public ScoreboardObject getScoreboard(Player player, String name) {
        if (!entries.containsKey(player))
            return null;

        return entries.get(player).stream()
                      .filter(scoreboardObject -> scoreboardObject.name.equals(name))
                      .findFirst()
                      .orElse(null);
    }

    /**
     * Gets player's scoreboard by its logic type.
     *
     * @param player     Player
     * @param logicClass Logic type
     * @return Scoreboard object
     */
    @Nullable
    public ScoreboardObject getScoreboard(Player player, Class<? extends ScoreboardLogic> logicClass) {
        if (!entries.containsKey(player))
            return null;

        return entries.get(player).stream()
                      .filter(scoreboardObject -> scoreboardObject.logic != null && scoreboardObject.logic.getClass() == logicClass)
                      .findFirst()
                      .orElse(null);
    }

//    @Nullable
//    public <T extends ScoreboardObject> T getScoreboard(Player player, Class<? extends ScoreboardObject> objectClass) {
//
//    }

    @EventHandler
    private void onPlayerQuitEvent(PlayerQuitEvent e) {
        entries.remove(e.getPlayer());
    }
}
