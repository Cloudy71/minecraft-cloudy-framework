/*
  User: Cloudy
  Date: 16/01/2022
  Time: 02:03
*/

package cz.cloudy.minecraft.core.componentsystem.types;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Cloudy
 */
public record CommandData(CommandSender sender, Command command, String[] arguments) {
    // ==================================================

    /**
     * Checks if command sender is a player
     *
     * @return True if command sender is a player
     */
    public boolean isPlayer() {
        return sender instanceof Player;
    }

    /**
     * Returns command sender as player.
     *
     * @return Command sender as player
     */
    public Player getPlayer() {
        return (Player) sender;
    }
}
