/*
  User: Cloudy
  Date: 20/01/2022
  Time: 23:57
*/

package cz.cloudy.minecraft.core.componentsystem.types;

import cz.cloudy.minecraft.core.CorePlugin;
import cz.cloudy.minecraft.core.componentsystem.annotations.Component;

/**
 * @author Cloudy
 */
public record ComponentData(Object component, Component annotation, CorePlugin plugin) {

}
