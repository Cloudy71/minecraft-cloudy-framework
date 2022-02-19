/*
  User: Cloudy
  Date: 07/01/2022
  Time: 22:12
*/

package cz.cloudy.minecraft.core.database.interfaces;

import cz.cloudy.minecraft.core.database.DatabaseEntity;
import cz.cloudy.minecraft.core.database.annotation.Index;
import cz.cloudy.minecraft.core.database.annotation.MultiIndex;
import cz.cloudy.minecraft.core.database.enums.FetchLevel;
import cz.cloudy.minecraft.core.database.types.FieldScan;

import java.util.List;
import java.util.Set;

/**
 * @author Cloudy
 */
public interface IDatabaseMapper {
    /**
     * Maps field scan into database primitive type.
     *
     * @param fieldScan Field scan
     * @return Database primitive type
     */
    String mapFieldScanToDatabaseType(FieldScan fieldScan);

    /**
     * Maps field scan into database field definition.
     *
     * @param fieldScan Field scan
     * @return Database field definition
     */
    String fieldScanToFieldDefinition(FieldScan fieldScan);

    /**
     * Maps field scan into database field constraint.
     *
     * @param fieldScan Field scan
     * @return Database field constraint
     */
    String fieldScanToConstraint(FieldScan fieldScan);

    /**
     * Checks for {@link Index} and {@link MultiIndex} annotations on field scans and creates indexes.
     *
     * @param fieldScanSet Field scans set
     * @return List of index queries
     */
    List<String> fieldScansToIndexes(Set<FieldScan> fieldScanSet);

    //    String buildSelectStringForEntityType(Class<? extends DatabaseEntity> clazz, String prefix, FetchLevel fetchLevel);

    /**
     * Builds fetch data for entity type queries.
     *
     * @param clazz      Entity type
     * @param fetchLevel Fetch level
     * @return Fetch data
     */
    IFetchData buildFetchDataForEntityType(Class<? extends DatabaseEntity> clazz, FetchLevel fetchLevel);
}
