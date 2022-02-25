/*
  User: Cloudy
  Date: 08/02/2022
  Time: 15:02
*/

package cz.cloudy.minecraft.core.game;

import cz.cloudy.minecraft.core.componentsystem.ComponentLoader;
import cz.cloudy.minecraft.core.componentsystem.annotations.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;

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
     * Creates string of component object.
     *
     * @param component Component
     * @return String
     * @since 1.18.7.1
     */
    public String getText(net.kyori.adventure.text.Component component) {
        if (!(component instanceof TextComponent tx))
            return null;

        return tx.content() +
               String.join(
                       "",
                       component.children()
                                .stream()
                                .map(c -> c instanceof TextComponent txx
                                        ? (
                                        (txx.color() != null ? ChatColor.valueOf(txx.color().toString().toUpperCase()).toString() : "") +
                                        (txx.style().hasDecoration(TextDecoration.BOLD) ? ChatColor.BOLD : "") +
                                        (txx.style().hasDecoration(TextDecoration.ITALIC) ? ChatColor.ITALIC : "") +
                                        (txx.style().hasDecoration(TextDecoration.STRIKETHROUGH) ? ChatColor.STRIKETHROUGH : "") +
                                        (txx.style().hasDecoration(TextDecoration.UNDERLINED) ? ChatColor.UNDERLINE : "") +
                                        (txx.style().hasDecoration(TextDecoration.OBFUSCATED) ? ChatColor.MAGIC : "") +
                                        txx.content()
                                )
                                        : "")
                                .toArray(String[]::new)
               );
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

    /**
     * Creates string of component object.
     *
     * @param component Component
     * @return String
     * @since 1.18.7.1
     */
    public static String get(net.kyori.adventure.text.Component component) {
        return ComponentLoader.get(TextUtils.class).getText(component);
    }
}
