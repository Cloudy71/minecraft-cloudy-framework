/*
  User: Cloudy
  Date: 26/01/2022
  Time: 23:59
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
public @interface MultiIndex {
    /**
     * Byte id of multi index.
     * Fields sharing same index will be used in the same index.
     *
     * @return Byte
     */
    byte value(); // Index ID for entity

//    boolean unique() default false;
}
