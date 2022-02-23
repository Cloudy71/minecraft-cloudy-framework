/*
  User: Cloudy
  Date: 22/02/2022
  Time: 00:49
*/

package cz.cloudy.minecraft.core.componentsystem;

import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.hanging.HangingEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.vehicle.VehicleEvent;
import org.bukkit.event.weather.WeatherEvent;
import org.bukkit.event.world.WorldEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Cloudy
 * @since 1.18.7
 */
public class ComponentStaticDataInitializer {
    /**
     *
     */
    protected static final Map<String, List<String>> worldFilters = new HashMap<>();

    private static boolean initialized = false;

    /**
     * Initializes all needed data for AspectJ processing.
     */
    protected static void init() {
        if (initialized)
            return;
        initWorldOnlyAccessors();
        initialized = true;
    }

    private static void initWorldOnlyAccessors() {
        ComponentAspect.addWorldOnlyEventAccessor(BlockEvent.class, blockEvent -> blockEvent.getBlock().getWorld());
        ComponentAspect.addWorldOnlyEventAccessor(EntityEvent.class, entityEvent -> entityEvent.getEntity().getWorld());
        ComponentAspect.addWorldOnlyEventAccessor(HangingEvent.class, hangingEvent -> hangingEvent.getEntity().getWorld());
        ComponentAspect.addWorldOnlyEventAccessor(PlayerEvent.class, playerEvent -> playerEvent.getPlayer().getWorld());
        ComponentAspect.addWorldOnlyEventAccessor(InventoryEvent.class, inventoryEvent -> inventoryEvent.getView().getPlayer().getWorld());
        ComponentAspect.addWorldOnlyEventAccessor(VehicleEvent.class, vehicleEvent -> vehicleEvent.getVehicle().getWorld());
        ComponentAspect.addWorldOnlyEventAccessor(WeatherEvent.class, WeatherEvent::getWorld);
        ComponentAspect.addWorldOnlyEventAccessor(WorldEvent.class, WorldEvent::getWorld);
    }
}
