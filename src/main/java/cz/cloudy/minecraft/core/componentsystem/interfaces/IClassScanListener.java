/*
  User: Cloudy
  Date: 07/01/2022
  Time: 16:19
*/

package cz.cloudy.minecraft.core.componentsystem.interfaces;

/**
 * @author Cloudy
 */
public interface IClassScanListener {
    /**
     * Is invoked when new component scan occurred.
     *
     * @param classes Classes
     */
    void scan(Class<?>[] classes);
}
