/*
  User: Cloudy
  Date: 19/02/2022
  Time: 14:52
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
@Target({ElementType.METHOD})
public @interface Cooldown {
    /**
     * The cooldown duration in milliseconds.
     *
     * @return Integer
     */
    long value();

    /**
     * If set to true, last returned value from this method will be given,
     * otherwise a null is given.
     *
     * @return Boolean
     */
    boolean returnLastValue() default true;
}
