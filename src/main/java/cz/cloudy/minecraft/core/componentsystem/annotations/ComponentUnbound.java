/*
  User: Cloudy
  Date: 23/02/2022
  Time: 16:29
*/

package cz.cloudy.minecraft.core.componentsystem.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link org.bukkit.event.EventHandler} annotation can be used only on methods which are under Component.
 * This annotation un-bounds event handlers from components.
 *
 * @author Cloudy
 * @since 1.18.7
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ComponentUnbound {
}
