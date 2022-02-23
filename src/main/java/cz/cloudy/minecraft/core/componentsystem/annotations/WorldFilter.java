/*
  User: Cloudy
  Date: 22/02/2022
  Time: 19:40
*/

package cz.cloudy.minecraft.core.componentsystem.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Cloudy
 * @since 1.18.7
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface WorldFilter {
    /**
     * World filter name.
     * This annotation is used in cooperation with {@link WorldOnly#filter()}.
     *
     * @return String
     */
    String name();
}
