/*
  User: Cloudy
  Date: 06/01/2022
  Time: 18:44
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
public @interface Column {
    /**
     * Database column name
     *
     * @return String
     */
    String value();
}
