/*
  User: Cloudy
  Date: 17/02/2022
  Time: 22:35
*/

package cz.cloudy.minecraft.core.componentsystem;

import cz.cloudy.minecraft.core.componentsystem.annotations.Component;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Cloudy
 * @since 1.18.6
 */
@Component
public class ComponentCache {
    /**
     * Clears all methods annotated with {@link cz.cloudy.minecraft.core.componentsystem.annotations.Cached} and id field set to id parameter.
     *
     * @param id Cache id
     */
    public void clear(String id) {
        List<Method> methods = ComponentAspect.cacheIdMap.get(id);
        if (methods == null)
            return;

        for (Method method : methods) {
            ComponentAspect.cachedCache.remove(method);
        }
        ComponentAspect.cacheIdMap.remove(id);
    }
}
