/*
  User: Cloudy
  Date: 25/01/2022
  Time: 05:10
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
public @interface Default {
    /**
     * Default database value.
     * String must always be in between quote characters to ensure provided value is always a string.
     *
     * @return String
     */
    String value(); // Literal value => 'String', CURRENT_TIMESTAMP(), ...
}
