/*
  User: Cloudy
  Date: 01/02/2022
  Time: 02:03
*/

package cz.cloudy.minecraft.core.game;

import cz.cloudy.minecraft.core.componentsystem.annotations.Component;
import org.bukkit.metadata.Metadatable;

/**
 * @author Cloudy
 */
@Component
public class EntityUtils {

    public <T> T getMetadata(Metadatable entity, String name, T def) {
        if (!entity.hasMetadata(name))
            return def;

        return (T) entity.getMetadata(name).get(0).value();
    }

    public <T> T getMetadata(Metadatable entity, String name) {
        return getMetadata(entity, name, null);
    }
}
