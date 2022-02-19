/*
  User: Cloudy
  Date: 07/01/2022
  Time: 03:00
*/

package cz.cloudy.minecraft.core.componentsystem.annotations;

import java.lang.annotation.*;

/**
 * @author Cloudy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(ComponentScans.class)
public @interface ComponentScan {
    /**
     * Name of the package
     *
     * @return String
     */
    String value() default "";

    /**
     * Classes from which component scan will be invoked.
     *
     * @return Class array
     */
    Class<?>[] classes() default {};
}
