/*
  User: Cloudy
  Date: 01/02/2022
  Time: 02:03
*/

package cz.cloudy.minecraft.core.game;

import cz.cloudy.minecraft.core.componentsystem.annotations.Component;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Cloudy
 */
@Component
public class EntityUtils {

    /**
     * Returns set of entities which are nearby specified center point of specified radius.
     *
     * @param centerPoint Center point
     * @param radius      Radius
     * @return Set of entities
     * @since 1.18.6
     */
    public Set<Entity> getNearbyEntities(World world, Vector centerPoint, float radius) {
        return world.getNearbyEntities(
                            new BoundingBox(
                                    centerPoint.getX() - radius,
                                    centerPoint.getY() - radius,
                                    centerPoint.getZ() - radius,
                                    centerPoint.getX() + radius,
                                    centerPoint.getY() + radius,
                                    centerPoint.getZ() + radius
                            )
                    ).stream()
                    .filter(entity -> entity.getLocation().toVector().distance(centerPoint) <= radius)
                    .collect(Collectors.toSet());
    }

    /**
     * Returns set of entities which are nearby specified center entity of specified radius.
     *
     * @param centerEntity Center entity
     * @param radius       Radius
     * @return Set of entities
     * @since 1.18.6
     */
    public Set<Entity> getNearbyEntities(Entity centerEntity, float radius) {
        return getNearbyEntities(centerEntity.getWorld(), centerEntity.getLocation().toVector(), radius);
    }
}
