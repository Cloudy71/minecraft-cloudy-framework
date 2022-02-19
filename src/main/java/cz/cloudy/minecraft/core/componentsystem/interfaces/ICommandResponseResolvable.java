/*
  User: Cloudy
  Date: 17/01/2022
  Time: 00:26
*/

package cz.cloudy.minecraft.core.componentsystem.interfaces;

import cz.cloudy.minecraft.core.componentsystem.types.CommandData;
import net.kyori.adventure.text.Component;

/**
 * @author Cloudy
 */
public interface ICommandResponseResolvable {
    /**
     * Creates new component from provided data.
     *
     * @param commandData Command data
     * @return Component
     */
    Component getComponent(CommandData commandData);
}
