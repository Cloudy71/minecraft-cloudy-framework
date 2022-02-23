/*
  User: Cloudy
  Date: 20/02/2022
  Time: 01:33
*/

package cz.cloudy.minecraft.core.game;

import com.google.common.base.Preconditions;
import cz.cloudy.minecraft.core.CoreRunnerPlugin;
import cz.cloudy.minecraft.core.componentsystem.annotations.Benchmarked;
import cz.cloudy.minecraft.core.componentsystem.annotations.Component;
import cz.cloudy.minecraft.core.game.annotations.BulkBuildType;
import cz.cloudy.minecraft.core.game.types.EachBlockDataConsumer;
import cz.cloudy.minecraft.core.game.types.SingleBlockDataConsumer;
import cz.cloudy.minecraft.core.math.VectorUtils;
import cz.cloudy.minecraft.core.types.BlockDataConsumer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.BlockVector;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * TODO: Split jobs into seperate tasks.
 *
 * @author Cloudy
 * @since 1.18.7
 */
@Component
public class BulkWorldBuilder {
    private static final int BLOCK_QUEUE_TASK_PERIOD  = 1;
    private static final int BLOCK_QUEUE_TASK_MAXIMUM = 60000;

    private static class BlockQueueEntry {
        public World     world;
        public int       x;
        public int       y;
        public int       z;
        public BlockData blockData;

        public BlockQueueEntry(World world, int x, int y, int z, BlockData blockData) {
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
            this.blockData = blockData;
        }
    }

    /**
     *
     */
    protected BulkBuildType.BuildType buildType = BulkBuildType.BuildType.Bukkit;

    private final Queue<BlockQueueEntry> blockQueueEntries = new ArrayDeque<>();

    private Integer blockQueueTask = null;

    private void setBlock(World world, int x, int y, int z, Object data) {
        if (buildType == BulkBuildType.BuildType.Bukkit) {
            world.setBlockData(x, y, z, (BlockData) data);
        } else if (buildType == BulkBuildType.BuildType.Bukkit_Threaded) {
            blockQueueEntries.add(new BlockQueueEntry(world, x, y, z, (BlockData) data));
            startThreadedTask();
        } else if (buildType == BulkBuildType.BuildType.NMSChunk) {

            throw new RuntimeException("Implementation missing.");
        }
    }

    private void startThreadedTask() {
        if (blockQueueTask != null)
            return;

        blockQueueTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                CoreRunnerPlugin.singleton,
                new Runnable() {
                    @Override
                    @Benchmarked
                    public void run() {
                        for (int i = 0; i < BLOCK_QUEUE_TASK_MAXIMUM; ++i) {
                            BlockQueueEntry entry = blockQueueEntries.poll();
                            if (entry == null)
                                break;

                            entry.world.setBlockData(entry.x, entry.y, entry.z, entry.blockData);
//                            entry.world.setType(entry.x, entry.y, entry.z, entry.blockData.getMaterial());
                        }

                        if (blockQueueEntries.isEmpty()) {
                            Bukkit.getScheduler().cancelTask(blockQueueTask);
                            blockQueueTask = null;
                        }
                    }
                },
                1,
                BLOCK_QUEUE_TASK_PERIOD
        );
    }

    public void setBuildType(BulkBuildType.BuildType buildType) {
        this.buildType = buildType;
    }

    // region Cuboid

    /**
     * Builds cuboid structure from startLocation location to end location.
     *
     * @param startLocation Start location
     * @param endLocation   End location
     * @param material      Block material
     * @param consumer      Block data consumer
     * @param <T>           Block data generic
     */
    public <T extends BlockData> void buildCuboid(Location startLocation, Location endLocation, Material material, BlockDataConsumer<T> consumer) {
        Preconditions.checkState(startLocation.getWorld().equals(endLocation.getWorld()), "Locations must be in same world.");

        World world = startLocation.getWorld();
        Vector startVector = startLocation.toVector();
        Vector endVector = endLocation.toVector();

        VectorUtils.boundingBoxNormalize(startVector, endVector);

        Vector start = new BlockVector(startVector.getBlockX(), startVector.getBlockY(), startVector.getBlockZ());
        Vector end = new BlockVector(endVector.getBlockX(), endVector.getBlockY(), endVector.getBlockZ());

        int sizeX = (int) (end.getX() - start.getX());
        int sizeY = (int) (end.getY() - start.getY());
        int sizeZ = (int) (end.getZ() - start.getZ());

        if ((sizeX | sizeY | sizeZ) == 0)
            return;

        BlockData blockData = null;
        if (consumer == null || consumer instanceof SingleBlockDataConsumer<T>) {
            blockData = material.createBlockData();
            if (consumer != null)
                consumer.accept((T) blockData);
        }

        int len = sizeX * sizeY * sizeZ;
        for (int i = 0; i < len; ++i) {
            int z = i / sizeZ / sizeY;
            int zCount = z * sizeZ * sizeY;
            int y = (i - zCount) / sizeY;
            int x = i - zCount - (y * sizeY);

            x += (int) start.getX();
            y += (int) start.getY();
            z += (int) start.getZ();
            BlockData currentBlockData;
            if (blockData == null) {
                currentBlockData = material.createBlockData();
                ((EachBlockDataConsumer<T>) consumer).accept((T) currentBlockData, new Vector(x, y, z));
            } else
                currentBlockData = blockData;
            setBlock(world, x, y, z, currentBlockData);
        }
    }

    /**
     * Builds cuboid structure from startLocation location to end location.
     *
     * @param startLocation Start location
     * @param endLocation   End location
     * @param material      Block material
     */
    public void buildCuboid(Location startLocation, Location endLocation, Material material) {
        buildCuboid(startLocation, endLocation, material, null);
    }

    /**
     * Builds cuboid structure in world from start position to end position.
     *
     * @param start    Start position
     * @param end      End position
     * @param material Block material
     * @param consumer Block data consumer
     * @param <T>      Block data generic
     */
    public <T extends BlockData> void buildCuboid(World world, Vector start, Vector end, Material material, BlockDataConsumer<T> consumer) {
        buildCuboid(
                new Location(world, start.getX(), start.getY(), start.getZ()),
                new Location(world, end.getX(), end.getY(), end.getZ()),
                material,
                consumer
        );
    }

    /**
     * Builds cuboid structure in world from start position to end position.
     *
     * @param start    Start position
     * @param end      End position
     * @param material Block material
     */
    public void buildCuboid(World world, Vector start, Vector end, Material material) {
        buildCuboid(world, start, end, material, null);
    }

    // endregion

    // region Sphere

    // endregion

    // region Copy

    /**
     * Copies blocks from sourceBox in sourceWorld and pastes it into destinationWorld on destinationPoint which is the lowest corner.
     *
     * @param sourceWorld      Source World
     * @param sourceBox        Source Box
     * @param destinationWorld Destination World
     * @param destinationPoint Destination Point
     */
    public void copyPaste(World sourceWorld, BoundingBox sourceBox, World destinationWorld, Vector destinationPoint) {
        int sizeX = (int) (sourceBox.getMaxX() - sourceBox.getMinX());
        int sizeY = (int) (sourceBox.getMaxY() - sourceBox.getMinY());
        int sizeZ = (int) (sourceBox.getMaxZ() - sourceBox.getMinZ());

        if ((sizeX | sizeY | sizeZ) == 0)
            return;

        Vector start = new Vector((int) sourceBox.getMinX(), (int) sourceBox.getMinY(), (int) sourceBox.getMinZ());

        int len = sizeX * sizeY * sizeZ;
        for (int i = 0; i < len; ++i) {
            int z = i / sizeZ / sizeY;
            int zCount = z * sizeZ * sizeY;
            int y = (i - zCount) / sizeY;
            int x = i - zCount - (y * sizeY);

            int sX = x + (int) start.getX();
            int sY = y + (int) start.getY();
            int sZ = z + (int) start.getZ();

            x += (int) destinationPoint.getX();
            y += (int) destinationPoint.getY();
            z += (int) destinationPoint.getZ();

            setBlock(destinationWorld, x, y, z, sourceWorld.getBlockData(sX, sY, sZ));
        }
    }

    // endregion
}
