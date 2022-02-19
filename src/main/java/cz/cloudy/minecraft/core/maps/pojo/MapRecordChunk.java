/*
  User: Cloudy
  Date: 02/02/2022
  Time: 02:58
*/

package cz.cloudy.minecraft.core.maps.pojo;

import cz.cloudy.minecraft.core.componentsystem.annotations.CheckConfiguration;
import cz.cloudy.minecraft.core.data_transforming.transformers.Int2ToStringTransform;
import cz.cloudy.minecraft.core.database.DatabaseEntity;
import cz.cloudy.minecraft.core.database.annotation.*;
import cz.cloudy.minecraft.core.types.Int2;

/**
 * @author Cloudy
 */
@Table("__map_record_chunk")
@CheckConfiguration("maps.mapController=true")
public class MapRecordChunk
        extends DatabaseEntity {

    /**
     * Map record foreign key value.
     */
    @Column("map_record")
    @ForeignKey
//    @Index
//    @MultiIndex(0)
    protected MapRecord mapRecord;

    /**
     * Chunk position
     */
    @Column("chunk_position")
    @Transform(Int2ToStringTransform.class)
    @Size(16)
//    @Index
//    @MultiIndex(0)
    protected Int2 chunkPosition;

    /**
     * Getter for map record foreign key value.
     *
     * @return Map record
     */
    public MapRecord getMapRecord() {
        return mapRecord;
    }

    /**
     * Setter for map record foreign key value.
     *
     * @param mapRecord Map record
     */
    public void setMapRecord(MapRecord mapRecord) {
        this.mapRecord = mapRecord;
    }

    /**
     * Getter for chunk position.
     *
     * @return Chunk position
     */
    public Int2 getChunkPosition() {
        return chunkPosition;
    }

    /**
     * Setter for chunk position.
     *
     * @param chunkPosition Chunk position
     */
    public void setChunkPosition(Int2 chunkPosition) {
        this.chunkPosition = chunkPosition;
    }
}
