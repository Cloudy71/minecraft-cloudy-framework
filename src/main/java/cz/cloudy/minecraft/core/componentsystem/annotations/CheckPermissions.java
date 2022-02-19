/*
  User: Cloudy
  Date: 17/01/2022
  Time: 01:12
*/

package cz.cloudy.minecraft.core.componentsystem.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Cloudy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface CheckPermissions {
    /**
     * Permissions
     *
     * @return Permission array
     */
    CheckPermission[] value();
}
