/*
  User: Cloudy
  Date: 21/02/2022
  Time: 15:34
*/

package cz.cloudy.minecraft.core.componentsystem.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TODO: Filtering for example for survival worlds...
 * TODO: Ability to use WorldOnly on Classes so all event handlers are filtered.
 *
 * @author Cloudy
 * @since 1.18.7
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface WorldOnly {
    /**
     * World names for which the method will be interactive.
     * If method was invoked for instance (e.g. Event, CommandData, ...) which is in not-listed world, such method will not be invoked.
     *
     * @return String array
     */
    String[] worldNames() default {};

    /**
     * Instead of using world names there's possibility to register several filters for selected worlds.
     * Using such filter automatically looks up for worlds in the filter and uses them exactly same as writing filter's world names into {@link WorldOnly#worldNames()}.
     *
     * @return String
     */
    String filter() default "";
}
