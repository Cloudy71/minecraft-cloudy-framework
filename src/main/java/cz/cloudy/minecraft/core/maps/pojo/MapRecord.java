/*
  User: Cloudy
  Date: 01/02/2022
  Time: 15:49
*/

package cz.cloudy.minecraft.core.maps.pojo;

import cz.cloudy.minecraft.core.componentsystem.annotations.CheckConfiguration;
import cz.cloudy.minecraft.core.data_transforming.transformers.WorldToStringTransformer;
import cz.cloudy.minecraft.core.database.DatabaseEntity;
import cz.cloudy.minecraft.core.database.annotation.*;
import org.bukkit.World;

/**
 * @author Cloudy
 */
@Table("__map_record")
@CheckConfiguration("maps.mapController=true")
public class MapRecord
        extends DatabaseEntity {

    /**
     * World
     */
    @Column("world")
    @Transform(WorldToStringTransformer.class)
//    @Index
//    @MultiIndex(0)
    @Size(32)
    protected World world;

    /**
     * Map id
     */
    @Column("map_id")
//    @Index
    protected int mapId;

    /**
     * Usage count
     */
    @Column("usage_number")
    @Default("0")
//    @Index
//    @MultiIndex(0)
    protected int usage;

    /**
     * Getter for world.
     *
     * @return World
     */
    public World getWorld() {
        return world;
    }

    /**
     * Setter for world.
     *
     * @param world World
     */
    public void setWorld(World world) {
        this.world = world;
    }

    /**
     * Getter for map id.
     *
     * @return Map id
     */
    public int getMapId() {
        return mapId;
    }

    /**
     * Setter for map id.
     *
     * @param mapId Map id
     */
    public void setMapId(int mapId) {
        this.mapId = mapId;
    }

    /**
     * Getter for usage count.
     *
     * @return Usage count
     */
    public int getUsage() {
        return usage;
    }

    /**
     * Setter for usage count.
     *
     * @param usage Usage count
     */
    public void setUsage(int usage) {
        this.usage = usage;
    }
}
