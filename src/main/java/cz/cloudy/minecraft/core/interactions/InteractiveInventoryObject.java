/*
  User: Cloudy
  Date: 08/02/2022
  Time: 03:52
*/

package cz.cloudy.minecraft.core.interactions;

import cz.cloudy.minecraft.core.componentsystem.ComponentLoader;
import cz.cloudy.minecraft.core.interactions.interfaces.IInteractiveInventoryHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * @author Cloudy
 */
public class InteractiveInventoryObject {
    /**
     * Id.
     */
    protected final int       id;
    /**
     * Title.
     */
    protected final String    title;
    /**
     * Inventory.
     */
    protected final Inventory inventory;

    /**
     * Button map.
     */
    protected final Map<Integer, IInteractiveInventoryHandler> buttonMap = new HashMap<>();
    /**
     * Viewing players.
     */
    protected final List<UUID>                                 players   = new ArrayList<>();

    /**
     * Default constructor.
     *
     * @param id        Id
     * @param title     Title
     * @param inventory Inventory
     */
    protected InteractiveInventoryObject(int id, String title, Inventory inventory) {
        this.id = id;
        this.title = title;
        this.inventory = inventory;
    }

    /**
     * Getter for inventory id.
     *
     * @return Inventory id
     */
    public int getId() {
        return id;
    }

    /**
     * Adds button item stack into inventory.
     *
     * @param itemStack Item stack
     * @param position  Position index
     * @param handler   Handler
     * @return Self
     */
    public InteractiveInventoryObject addButton(ItemStack itemStack, int position, IInteractiveInventoryHandler handler) {
        inventory.setItem(position, itemStack);
        if (handler != null)
            buttonMap.put(position, handler);
        return this;
    }

    /**
     * Opens inventory for player.
     *
     * @param player Player
     */
    public void open(Player player) {
        if (id == -1)
            return;
        ComponentLoader.get(InteractiveInventory.class).openGlobalInventory(player, id);
    }

    /**
     * Destroys inventory.
     */
    public void destroy() {
        if (id != -1)
            ComponentLoader.get(InteractiveInventory.class).destroyGlobalInventory(id);
        else
            for (UUID player : players)
                ComponentLoader.get(InteractiveInventory.class).destroyOnetimeInventory(title, Bukkit.getPlayer(player));
    }
}
