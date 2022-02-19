/*
  User: Cloudy
  Date: 08/02/2022
  Time: 15:02
*/

package cz.cloudy.minecraft.core.game;

import cz.cloudy.minecraft.core.componentsystem.ComponentLoader;
import cz.cloudy.minecraft.core.componentsystem.annotations.Component;

/**
 * @author Cloudy
 */
@Component
public class TextUtils {

    /**
     * Creates text component with specified string
     *
     * @param string Text string
     * @return New text component
     */
    public net.kyori.adventure.text.Component getText(String string) {
        return net.kyori.adventure.text.Component.text(string);
    }

    /**
     * Creates text component with specified string
     *
     * @param string Text string
     * @return New text component
     */
    public static net.kyori.adventure.text.Component get(String string) {
        return ComponentLoader.get(TextUtils.class).getText(string);
    }
}
