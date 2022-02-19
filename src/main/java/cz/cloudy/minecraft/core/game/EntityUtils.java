/*
  User: Cloudy
  Date: 01/02/2022
  Time: 02:03
*/

package cz.cloudy.minecraft.core.game;

import cz.cloudy.minecraft.core.componentsystem.annotations.Component;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.Metadatable;
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
     * Returns metadata of metadatable object
     * If object doesn't have such metadata, default value is returned
     *
     * @param entity Metadatable object
     * @param name   Metadata name
     * @param def    Default value if no metadata with name are found
     * @param <T>    Return type
     * @return Metadata value or default if metadata with specified name are missing
     * @deprecated Use {@link MetaUtils#getMetadata(Metadatable, String, Object)} instead
     */
    @Deprecated(forRemoval = true, since = "1.18.5")
    public <T> T getMetadata(Metadatable entity, String name, T def) {
        if (!entity.hasMetadata(name))
            return def;

        return (T) entity.getMetadata(name).get(0).value();
    }

    /**
     * Returns metadata of metadatable object
     * If object doesn't have such metadata, null is returned
     *
     * @param entity Metadatable object
     * @param name   Metadata name
     * @param <T>    Return type
     * @return Metadata value or null if metadata with specified name are missing
     * @deprecated Use {@link MetaUtils#getMetadata(Metadatable, String)} instead
     */
    @Deprecated(forRemoval = true, since = "1.18.5")
    public <T> T getMetadata(Metadatable entity, String name) {
        return getMetadata(entity, name, null);
    }

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
