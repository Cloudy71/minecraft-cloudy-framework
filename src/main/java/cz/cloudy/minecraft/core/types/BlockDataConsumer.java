/*
  User: Cloudy
  Date: 20/02/2022
  Time: 03:37
*/

package cz.cloudy.minecraft.core.types;

import org.bukkit.block.data.BlockData;

/**
 * @author Cloudy
 * @since 1.18.7
 */
public interface BlockDataConsumer<T extends BlockData> {
    /**
     * Informative method of not using this interface directly.
     */
    void doNotUse();

    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     */
    void accept(T t);
}
