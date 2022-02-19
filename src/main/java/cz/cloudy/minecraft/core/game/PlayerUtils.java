/*
  User: Cloudy
  Date: 17/02/2022
  Time: 15:31
*/

package cz.cloudy.minecraft.core.game;

import cz.cloudy.minecraft.core.componentsystem.annotations.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @author Cloudy
 * @since 1.18.6
 */
@Component
public class PlayerUtils {

    /**
     * Generates UUID by player's name
     *
     * @param name Player's name
     * @return Generated UUID
     */
    public UUID getUuidByName(String name) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
    }
}
