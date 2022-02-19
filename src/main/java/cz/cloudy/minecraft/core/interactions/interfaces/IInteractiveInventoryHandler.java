/*
  User: Cloudy
  Date: 08/02/2022
  Time: 03:54
*/

package cz.cloudy.minecraft.core.interactions.interfaces;

import org.bukkit.entity.Player;

/**
 * @author Cloudy
 */
public interface IInteractiveInventoryHandler {
    /**
     * Invoked when player clicks an item in interactive inventory.
     *
     * @param player Player
     */
    void onClick(Player player);

    /**
     * Invoked when player takes an item from interactive inventory.
     *
     * @param player Player
     */
    void onTake(Player player);
}
