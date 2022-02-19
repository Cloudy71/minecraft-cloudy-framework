/*
  User: Cloudy
  Date: 17/01/2022
  Time: 00:59
*/

package cz.cloudy.minecraft.core.componentsystem.annotations;

import java.lang.annotation.*;

/**
 * @author Cloudy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Repeatable(CheckConditions.class)
// TODO: Rework into parsed language
public @interface CheckCondition {
    /**
     * Constant
     */
    String SENDER_IS_PLAYER = "sender_is_player";
    /**
     * Constant
     */
    String ARGS_IS_0        = "args_is_0";
    /**
     * Constant
     */
    String ARGS_IS_1        = "args_is_1";
    /**
     * Constant
     */
    String ARGS_IS_2        = "args_is_2";
    /**
     * Constant
     */
    String ARGS_IS_3        = "args_is_3";
    /**
     * Constant
     */
    String ARGS_IS_4        = "args_is_4";

    /**
     * Condition value
     *
     * @return String
     */
    String value(); // Condition
}
