/*
  User: Cloudy
  Date: 07/01/2022
  Time: 16:15
*/

package cz.cloudy.minecraft.core.componentsystem.interfaces;

/**
 * @author Cloudy
 */
public interface IComponentLoadListener {
    /**
     * Is invoked when new component is loaded.
     *
     * @param component Component
     */
    void load(Object component);
}
