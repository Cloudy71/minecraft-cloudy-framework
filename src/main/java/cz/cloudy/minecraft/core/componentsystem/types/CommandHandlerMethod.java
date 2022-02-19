/*
  User: Cloudy
  Date: 16/01/2022
  Time: 02:11
*/

package cz.cloudy.minecraft.core.componentsystem.types;

import java.lang.reflect.Method;

/**
 * @author Cloudy
 */
public class CommandHandlerMethod {
    private final Object component;
    private final Method method;

    /**
     * -
     *
     * @param component -
     * @param method    -
     */
    public CommandHandlerMethod(Object component, Method method) {
        this.component = component;
        this.method = method;
    }

    /**
     * Getter for component
     *
     * @return Component
     */
    public Object getComponent() {
        return component;
    }

    /**
     * Getter for method
     *
     * @return Method
     */
    public Method getMethod() {
        return method;
    }
}
