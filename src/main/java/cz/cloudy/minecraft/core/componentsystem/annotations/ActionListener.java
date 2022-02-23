/*
  User: Cloudy
  Date: 20/01/2022
  Time: 23:50
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
@Target(ElementType.METHOD)
public @interface ActionListener {
    /**
     * The name of action to listen to.
     * Name must always contain a plugin's name from which action comes as a prefix (e.g. "LoginSystem.login")
     *
     * @return name
     */
    String value(); // Listener name

    /**
     * ActionListener's run priority.
     * Higher means earlier in execution order.
     *
     * @return priority
     */
    int priority() default 0;
}
