/*
  User: Cloudy
  Date: 19/02/2022
  Time: 13:37
*/

package cz.cloudy.minecraft.core.componentsystem.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @author Cloudy
 * @since 1.18.6
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Benchmarked {

    /**
     * Time unit in which statistics will be outputted.
     *
     * @return Time unit
     * @since 1.18.7
     */
    TimeUnit unit() default TimeUnit.MILLISECONDS;
}
