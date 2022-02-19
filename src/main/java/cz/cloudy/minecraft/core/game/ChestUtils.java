/*
  User: Cloudy
  Date: 30/01/2022
  Time: 14:14
*/

package cz.cloudy.minecraft.core.game;

import cz.cloudy.minecraft.core.componentsystem.annotations.Component;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;

/**
 * @author Cloudy
 */
@Component
public class ChestUtils {

    /**
     * Returns base block of chest
     * Base block of single chest is the same chest, but base block of double chest is its left side chest
     *
     * @param refBlock Chest block
     * @return Base block of chest
     */
    public Block getBaseBlock(Block refBlock) {
        if (refBlock == null || !(refBlock.getState() instanceof Chest chest))
            return null;

        if (chest.getInventory().getHolder() instanceof DoubleChest doubleChest) {
            return doubleChest.getLeftSide().getInventory().getLocation().getBlock();
        }
        return chest.getBlock();
    }
}
