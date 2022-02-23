/*
  User: Cloudy
  Date: 20/02/2022
  Time: 03:38
*/

package cz.cloudy.minecraft.core.game.types;

import cz.cloudy.minecraft.core.types.BlockDataConsumer;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

/**
 * @author Cloudy
 * @since 1.18.7
 */
public interface EachBlockDataConsumer<T extends BlockData>
        extends BlockDataConsumer<T> {
    @Override
    default void doNotUse() {
    }

    @Override
    default void accept(T t) {
        accept(t, null);
    }

    void accept(T t, Vector position);
}
