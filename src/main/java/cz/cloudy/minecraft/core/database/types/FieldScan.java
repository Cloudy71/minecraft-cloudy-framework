/*
  User: Cloudy
  Date: 07/01/2022
  Time: 21:57
*/

package cz.cloudy.minecraft.core.database.types;

import cz.cloudy.minecraft.core.componentsystem.ComponentLoader;
import cz.cloudy.minecraft.core.database.DatabaseEntity;
import cz.cloudy.minecraft.core.database.DatabaseEntityMapper;
import cz.cloudy.minecraft.core.database.annotation.*;

import java.lang.reflect.Field;

/**
 * @author Cloudy
 */
public record FieldScan(ClassScan classScan, Field field,
                        Column column, PrimaryKey primaryKey,
                        ForeignKey foreignKey, Lazy lazy,
                        Size size, Null nullable,
                        AutoIncrement autoIncrement,
                        Protected protected_, Transform transform,
                        Default default_, Index index,
                        MultiIndex multiIndex) {

    // ==============================================================

    /**
     * Returns field's final type.
     * Final type can differ from implementation due to {@link Transform} annotation.
     * If this annotation occurs on such field, transforms final type is returned.
     *
     * @return Field's final type
     */
    public Class<?> getDatabaseClass() {
        return ComponentLoader.get(DatabaseEntityMapper.class).getFieldScanDatabaseClass(this);
    }

    /**
     * Gets value from field and transforms it into database usable type.
     * Database usable type is considered primitive type like {@link Integer} or {@link String} as well as {@link Transform#value()} final type.
     *
     * @param entity Entity
     * @return Database usable type
     */
    public Object getDatabaseValue(DatabaseEntity entity) {
        return ComponentLoader.get(DatabaseEntityMapper.class).getFieldScanDatabaseValue(entity, this);
    }
}
