/*
  User: Cloudy
  Date: 17/01/2022
  Time: 00:41
*/

package cz.cloudy.minecraft.core.componentsystem.annotations;

import java.lang.annotation.*;

/**
 * @author Cloudy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Repeatable(CheckPermissions.class)
public @interface CheckPermission {
    /**
     * Constant for Operator
     */
    String OP = "OP";

    /**
     * Permission name
     * @return String
     */
    String value(); // Permission name
}
