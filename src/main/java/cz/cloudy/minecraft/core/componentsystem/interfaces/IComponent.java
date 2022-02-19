/*
  User: Cloudy
  Date: 10/01/2022
  Time: 02:21
*/

package cz.cloudy.minecraft.core.componentsystem.interfaces;

import cz.cloudy.minecraft.core.CorePlugin;
import cz.cloudy.minecraft.core.componentsystem.ComponentLoader;
import org.bukkit.plugin.Plugin;

/**
 * @author Cloudy
 */
public interface IComponent {
    /**
     * Is invoked when component is fully loaded.
     */
    default void onLoad() {
    }

    /**
     * Is invoked when new component scan occurred.
     *
     * @param caller  Plugin
     * @param classes Classes
     */
    default void onClassScan(CorePlugin caller, Class<?>[] classes) {
    }

    /**
     * After server start
     * Is called after all components are loaded and database connection was made
     */
    default void onStart() {

    }

    /**
     * Returns component's plugin.
     *
     * @return Component's plugin
     */
    default Plugin getPlugin() {
        return ComponentLoader.getComponentOwner(this);
    }

    /**
     * Notifies all action listeners with specified name.
     *
     * @param name Name
     * @param data Data
     */
    default void notifyActionListeners(String name, Object... data) {
        ComponentLoader.notifyActionListeners(getClass(), name, data);
    }
}
