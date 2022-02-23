/*
  User: Cloudy
  Date: 20/02/2022
  Time: 03:36
*/

package cz.cloudy.minecraft.core.game.types;

import cz.cloudy.minecraft.core.types.BlockDataConsumer;
import org.bukkit.block.data.BlockData;

/**
 * @author Cloudy
 * @since 1.18.7
 */
public interface SingleBlockDataConsumer<T extends BlockData>
        extends BlockDataConsumer<T> {
    @Override
    default void doNotUse() {
    }
}
