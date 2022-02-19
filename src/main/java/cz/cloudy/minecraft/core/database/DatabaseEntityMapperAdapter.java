/*
  User: Cloudy
  Date: 17/02/2022
  Time: 14:30
*/

package cz.cloudy.minecraft.core.database;

import cz.cloudy.minecraft.core.database.annotation.Transform;
import cz.cloudy.minecraft.core.database.types.FieldScan;

/**
 * @author Cloudy
 * @since 1.18.6
 */
public class DatabaseEntityMapperAdapter {
    private DatabaseEntityMapperAdapter() {}

    /**
     * Transforms field into its final type.
     * Is used only if field has {@link Transform} annotation.
     *
     * @param mapper    Database entity mapper
     * @param fieldScan Field scan
     * @param value     Value
     * @return Transformed value
     */
    public static Object getForwardTransformedValue(DatabaseEntityMapper mapper, FieldScan fieldScan, Object value) {
        return mapper.getForwardTransformedValue(fieldScan, value);
    }

    /**
     * Transforms field into its base type.
     * Is used only if field has {@link Transform} annotation.
     *
     * @param mapper    Database entity mapper
     * @param fieldScan Field scan
     * @param value     Value
     * @return Transformed value
     */
    public static Object getBackTransformedValue(DatabaseEntityMapper mapper, FieldScan fieldScan, Object value) {
        return mapper.getBackTransformedValue(fieldScan, value);
    }
}
