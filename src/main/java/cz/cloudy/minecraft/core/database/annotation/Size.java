/*
  User: Cloudy
  Date: 07/01/2022
  Time: 22:04
*/

package cz.cloudy.minecraft.core.database.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Cloudy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Size {
    /**
     * Size of ID.
     */
    int IDSize      = 11;
    /**
     * Default size.
     */
    int DefaultSize = 32;

    /**
     * Size of database field.
     *
     * @return Integer
     */
    int value();
}
