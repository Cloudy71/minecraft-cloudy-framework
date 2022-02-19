/*
  User: Cloudy
  Date: 17/02/2022
  Time: 01:21
*/

package cz.cloudy.minecraft.core.game;

import cz.cloudy.minecraft.core.componentsystem.annotations.Component;
import org.bukkit.metadata.Metadatable;

/**
 * @author Cloudy
 * @since 1.18.5
 */
@Component
public class MetaUtils {

    /**
     * Returns metadata of metadatable object
     * If object doesn't have such metadata, default value is returned
     *
     * @param metadatable Metadatable object
     * @param name        Metadata name
     * @param def         Default value if no metadata with name are found
     * @param <T>         Return type
     * @return Metadata value or default if metadata with specified name are missing
     */
    public <T> T getMetadata(Metadatable metadatable, String name, T def) {
        if (!metadatable.hasMetadata(name))
            return def;

        return (T) metadatable.getMetadata(name).get(0).value();
    }

    /**
     * Returns metadata of metadatable object
     * If object doesn't have such metadata, null is returned
     *
     * @param metadatable Metadatable object
     * @param name        Metadata name
     * @param <T>         Return type
     * @return Metadata value or null if metadata with specified name are missing
     */
    public <T> T getMetadata(Metadatable metadatable, String name) {
        return getMetadata(metadatable, name, null);
    }
}
