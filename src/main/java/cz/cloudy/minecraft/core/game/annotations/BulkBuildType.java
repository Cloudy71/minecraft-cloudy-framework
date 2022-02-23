/*
  User: Cloudy
  Date: 20/02/2022
  Time: 01:44
*/

package cz.cloudy.minecraft.core.game.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TODO
 *
 * @author Cloudy
 * @since 1.18.7
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface BulkBuildType {
    /**
     * Enumerator for build types used in {@link cz.cloudy.minecraft.core.game.BulkWorldBuilder}.
     */
    enum BuildType {
        /**
         * Default build type. (60K/s)
         */
        Bukkit,
        /**
         * Default build type but scheduled into multiple parts to keep server alive.
         */
        Bukkit_Threaded,
        /**
         * Direct NMS chunk update method. (2.19M/s)
         * Does not send update to logged players.
         */
        NMSChunk,
    }

    /**
     * Uses build type for bulk block building.
     *
     * @return Build type
     */
    BuildType value();
}
