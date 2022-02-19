/*
  User: Cloudy
  Date: 08/01/2022
  Time: 03:56
*/

package cz.cloudy.minecraft.core.database;

import com.google.common.base.Preconditions;
import cz.cloudy.minecraft.core.CorePlugin;
import cz.cloudy.minecraft.core.LoggerFactory;
import cz.cloudy.minecraft.core.componentsystem.ReflectionUtils;
import cz.cloudy.minecraft.core.componentsystem.annotations.Component;
import cz.cloudy.minecraft.core.componentsystem.interfaces.IComponent;
import cz.cloudy.minecraft.core.data_transforming.DataTransformer;
import cz.cloudy.minecraft.core.data_transforming.interfaces.IDataTransformer;
import cz.cloudy.minecraft.core.database.annotation.*;
import cz.cloudy.minecraft.core.database.enums.FetchLevel;
import cz.cloudy.minecraft.core.database.types.ClassScan;
import cz.cloudy.minecraft.core.database.types.FieldScan;
import cz.cloudy.minecraft.core.types.Pair;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Cloudy
 */
@Component(wiredFrom = {Database.class, DatabaseCache.class})
public class DatabaseEntityMapper
        implements IComponent {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseEntityMapper.class);

    /**
     * Class scans of database entities.
     */
    protected static final Map<Class<? extends DatabaseEntity>, ClassScan>       mappedClasses   = new HashMap<>();
    /**
     * Field scans of database entities.
     */
    protected static final Map<Class<? extends DatabaseEntity>, List<FieldScan>> mappedFields    = new HashMap<>();
    /**
     * Entities which requires special conditions to be created.
     * There entities are stashed in case at least one plugin passes there special conditions so these entities could be mapped.
     */
    protected static final List<Class<? extends DatabaseEntity>>                 stashedEntities = new ArrayList<>();

//    protected static final com.google.common.collect.Table<Class<? extends DatabaseEntity>, Object, DatabaseEntity> entityTable =
//            HashBasedTable.create();

    @Component
    private Database database;

    @Component
    private DataTransformer dataTransformer;

    @Component
    private DatabaseCache cache;

    @Override
    public void onClassScan(CorePlugin caller, Class<?>[] classes) {
        List<Class<? extends DatabaseEntity>> removeList = new ArrayList<>();
        for (Class<? extends DatabaseEntity> stashedEntity : stashedEntities) {
            if (!caller.getComponentLoader().checkConfiguration(caller, stashedEntity))
                continue;

            logger.info("Mapping \"{}\" database entity", stashedEntity.getSimpleName());
            removeList.add(stashedEntity);
            mapEntityClass(stashedEntity);
        }
        stashedEntities.removeAll(removeList);

        for (Class<?> clazz : classes) {
            if (!DatabaseEntity.class.isAssignableFrom(clazz) || clazz == DatabaseEntity.class)
                continue;
            Class<? extends DatabaseEntity> entityClass = (Class<? extends DatabaseEntity>) clazz;
            if (!caller.getComponentLoader().checkConfiguration(caller, clazz)) {
                stashedEntities.add(entityClass);
                continue;
            }
            logger.info("Mapping \"{}\" database entity", clazz.getSimpleName());
            mapEntityClass(entityClass);
        }
    }

    /**
     * Returns field's final type.
     * Final type can differ from implementation due to {@link Transform} annotation.
     * If this annotation occurs on such field, transforms final type is returned.
     *
     * @param fieldScan Field scan
     * @return Field's final type
     */
    public Class<?> getFieldScanDatabaseClass(FieldScan fieldScan) {
        if (fieldScan.transform() != null)
            return dataTransformer.getUnknownDataTransformer(fieldScan.transform().value()).getTypes().getValue();
        return fieldScan.field().getType();
    }

    /**
     * Gets value from field and transforms it into database usable type.
     * Database usable type is considered primitive type like {@link Integer} or {@link String} as well as {@link Transform#value()} final type.
     *
     * @param entity    Entity
     * @param fieldScan Field scan
     * @return Database usable type
     */
    public Object getFieldScanDatabaseValue(DatabaseEntity entity, FieldScan fieldScan) {
        Object value = ReflectionUtils.getValueOpt(fieldScan.field(), entity).orElse(null);
        if (value == null)
            return null;
        if (fieldScan.transform() != null)
            return getForwardTransformedValue(fieldScan, value);
//            return ((IDataTransformer) dataTransformer.getUnknownDataTransformer(fieldScan.transform().value())).transform0to1(value);
        if (fieldScan.foreignKey() != null) {
            DatabaseEntity foreignEntity = (DatabaseEntity) value;
            return getFieldScanDatabaseValue(foreignEntity, getPrimaryKeyFieldScan(foreignEntity.getClass()));
        }
        return value;
    }

    /**
     * Transforms primitive type values into needed primitive types.
     * This is used mainly when database field is of {@link Long} type, but database returns {@link Integer}.
     *
     * @param value   Value
     * @param newType Required type
     * @return Transformed primitive
     */
    protected Object getPrimitiveTransformedValue(Object value, Class<?> newType) {
        Class<?> currentType = value.getClass();

        if (newType == byte.class)
            newType = Byte.class;
        else if (newType == short.class)
            newType = Short.class;
        else if (newType == int.class)
            newType = Integer.class;
        else if (newType == long.class)
            newType = Long.class;
        else if (newType == float.class)
            newType = Float.class;
        else if (newType == double.class)
            newType = Double.class;
        else if (newType == boolean.class)
            newType = Boolean.class;

        if ((currentType != String.class && currentType != Byte.class && currentType != Short.class && currentType != Integer.class &&
             currentType != Long.class && currentType != Float.class && currentType != Double.class && currentType != Boolean.class) ||
            (newType != String.class && newType != Byte.class && newType != Short.class && newType != Integer.class &&
             newType != Long.class && newType != Float.class && newType != Double.class && newType != Boolean.class))
            return value;

        if (currentType == String.class) {
            String v = (String) value;
            if (newType == String.class)
                return value;
            if (newType == Byte.class)
                return Byte.parseByte(v);
            if (newType == Short.class)
                return Short.parseShort(v);
            if (newType == Integer.class)
                return Integer.parseInt(v);
            if (newType == Long.class)
                return Long.parseLong(v);
            if (newType == Float.class)
                return Float.parseFloat(v);
            if (newType == Double.class)
                return Double.parseDouble(v);
            return Boolean.parseBoolean(v);
        }
        if (currentType == Boolean.class) {
            Boolean v = (Boolean) value;
            if (newType == String.class)
                return v ? "true" : "false";
            if (newType == Byte.class)
                return v ? (byte) 1 : (byte) 0;
            if (newType == Short.class)
                return v ? (short) 1 : (short) 0;
            if (newType == Integer.class)
                return v ? 1 : 0;
            if (newType == Long.class)
                return v ? 1L : 0L;
            if (newType == Float.class)
                return v ? 1f : 0f;
            if (newType == Double.class)
                return v ? 1d : 0d;
            return v;
        }

        Number n = (Number) value;
        if (newType == Byte.class)
            return n.byteValue();
        if (newType == Short.class)
            return n.shortValue();
        if (newType == Integer.class)
            return n.intValue();
        if (newType == Long.class)
            return n.longValue();
        if (newType == Float.class)
            return n.floatValue();
        if (newType == Double.class)
            return n.doubleValue();

        return value;
    }

    /**
     * Transforms field into its final type.
     * Is used only if field has {@link Transform} annotation.
     *
     * @param fieldScan Field scan
     * @param value     Value
     * @return Transformed value
     */
    protected Object getForwardTransformedValue(FieldScan fieldScan, Object value) {
        if (value == null)
            return null;

        Class<?> type = fieldScan.field().getType();
        if (fieldScan.transform() != null) {
            IDataTransformer transformer = dataTransformer.getUnknownDataTransformer(fieldScan.transform().value());
            Class<?> requiredType = (Class<?>) transformer.getTypes().getKey();
            if (value.getClass() == requiredType)
                value = transformer.transform0to1(value);
        }

        return value;
    }

    /**
     * Transforms field into its base type.
     * Is used only if field has {@link Transform} annotation.
     *
     * @param fieldScan Field scan
     * @param value     Value
     * @return Transformed value
     */
    protected Object getBackTransformedValue(FieldScan fieldScan, Object value) {
        if (value == null)
            return null;

        Class<?> type = fieldScan.field().getType();
        value = getPrimitiveTransformedValue(value, type);
        if (fieldScan.transform() != null) {
            IDataTransformer transformer = dataTransformer.getUnknownDataTransformer(fieldScan.transform().value());
            Class<?> requiredType = (Class<?>) transformer.getTypes().getValue();
            value = getPrimitiveTransformedValue(value, requiredType);
            if (value.getClass() == requiredType)
                value = transformer.transform1to0(value);
        } else if (value.getClass() == Timestamp.class) {
            Timestamp timestamp = (Timestamp) value;
            if (type == ZonedDateTime.class)
                value = ZonedDateTime.of(timestamp.toLocalDateTime(), ZoneOffset.ofHours(1));
        } else if (value.getClass() == LocalDateTime.class) {
            LocalDateTime localDateTime = (LocalDateTime) value;
            if (type == ZonedDateTime.class)
                value = ZonedDateTime.of(localDateTime, ZoneOffset.ofHours(1));
        }

        return value;
    }

    /**
     * Maps entity fields.
     *
     * @param clazz        Type
     * @param childClasses Child types
     */
    // TODO: Join scans
    protected void mapEntityFields(Class<? extends DatabaseEntity> clazz, List<Class<? extends DatabaseEntity>> childClasses) {
        childClasses.add(clazz);
        // TODO: Fix mapping, with two entities, abstract class does not map second enttiy object with primary key
//        if (!mappedFields.containsKey(clazz)) {
        for (Field field : clazz.getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column == null)
                continue;

            PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
            ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
            Lazy lazy = field.getAnnotation(Lazy.class);
            Size size = field.getAnnotation(Size.class);
            Null nullable = field.getAnnotation(Null.class);
            AutoIncrement autoIncrement = field.getAnnotation(AutoIncrement.class);
            Protected protected_ = field.getAnnotation(Protected.class);
            Transform transform = field.getAnnotation(Transform.class);
            Default default_ = field.getAnnotation(Default.class);
            Index index = field.getAnnotation(Index.class);
            MultiIndex multiIndex = field.getAnnotation(MultiIndex.class);

            // TODO: This is important since mapper can't handle Lazy inits with primary key... Fix in future
            Preconditions.checkState(lazy == null || primaryKey == null, "Primary key field cannot be lazily fetched");
            Preconditions.checkState(primaryKey == null || foreignKey == null, "Primary key field cannot be foreign object yet");
            Preconditions.checkState(primaryKey == null || (index == null && multiIndex == null), "Primary key fields are automatically indexed");
            field.setAccessible(true);

            for (Class<? extends DatabaseEntity> childClass : childClasses) {
                if ((mappedFields.containsKey(childClass) &&
                     mappedFields.get(childClass).stream().anyMatch(fieldScan -> fieldScan.column().value().equals(column.value()))) ||
                    (primaryKey != null && getPrimaryKeyFieldScan(childClass) != null))
                    continue;
                ClassScan classScan = mappedClasses.getOrDefault(childClass, null);
                FieldScan fieldScan = new FieldScan(
                        classScan,
                        field,
                        column,
                        primaryKey,
                        foreignKey,
                        lazy,
                        size,
                        nullable,
                        autoIncrement,
                        protected_,
                        transform,
                        default_,
                        index,
                        multiIndex
                );
                mappedFields.computeIfAbsent(childClass, aClass -> new ArrayList<>()).add(fieldScan);
            }
        }
//        }
        if (DatabaseEntity.class.isAssignableFrom(clazz.getSuperclass())) {
            mapEntityFields((Class<? extends DatabaseEntity>) clazz.getSuperclass(), childClasses);
        }
    }

    /**
     * Maps entity type.
     *
     * @param clazz type
     */
    public void mapEntityClass(Class<? extends DatabaseEntity> clazz) {
        if (mappedClasses.containsKey(clazz))
            return;

        mapEntityClassInternal(clazz);
        mapEntityFields(clazz, new ArrayList<>());
        cache.addCacheForEntityType(clazz);
    }

    private void mapEntityClassInternal(Class<? extends DatabaseEntity> clazz) {
        if (mappedClasses.containsKey(clazz))
            return;

        Table table = clazz.getAnnotation(Table.class);
        if (table == null)
            return;

        ClassScan classScan = new ClassScan(
                clazz,
                table
        );
        mappedClasses.put(clazz, classScan);

        if (DatabaseEntity.class.isAssignableFrom(clazz.getSuperclass()) && clazz.getSuperclass() != DatabaseEntity.class)
            mapEntityClassInternal((Class<? extends DatabaseEntity>) clazz.getSuperclass());
    }

    /**
     * Gets all field scans for entity type.
     *
     * @param clazz Entity type
     * @return Field scans
     */
    public List<FieldScan> getFieldScansForEntityType(Class<? extends DatabaseEntity> clazz) {
        if (!mappedFields.containsKey(clazz))
            return Collections.emptyList();

        return new ArrayList<>(mappedFields.get(clazz));
    }

    /**
     * Gets class scan for entity type.
     *
     * @param clazz Entity type
     * @return Class scan
     */
    public ClassScan getClassScanForEntityType(Class<? extends DatabaseEntity> clazz) {
        if (!mappedClasses.containsKey(clazz))
            return null;

        return mappedClasses.get(clazz);
    }

    /**
     * Gets all mapped entity types.
     *
     * @return All mapped entity types
     */
    public Set<Pair<ClassScan, Set<FieldScan>>> getMappedDatabaseEntityTypes() {
        Set<Pair<ClassScan, Set<FieldScan>>> set = new HashSet<>();
        for (ClassScan clazz : mappedClasses.values()) {
            set.add(new Pair<>(clazz, new HashSet<>(mappedFields.get(clazz.clazz()))));
        }
        return set;
    }

    /**
     * Gets all mapped but database un-constructed entity types.
     *
     * @return All mapped un-constructed entity types
     */
    public Set<Pair<ClassScan, Set<FieldScan>>> getMappedUnConstructedDatabaseEntityTypes() {
        Set<Pair<ClassScan, Set<FieldScan>>> set = new HashSet<>();
        for (Pair<ClassScan, Set<FieldScan>> mappedDatabaseEntityClass : getMappedDatabaseEntityTypes()) {
            if (mappedDatabaseEntityClass.getKey().isConstructed())
                continue;

            set.add(mappedDatabaseEntityClass);
        }
        return set;
    }

    /**
     * Gets entity type's primary key field scan.
     *
     * @param clazz Entity type
     * @return Primary key field scan or null if not found
     */
    public FieldScan getPrimaryKeyFieldScan(Class<? extends DatabaseEntity> clazz) {
        if (!mappedFields.containsKey(clazz))
            return null;
        List<FieldScan> scans = mappedFields.get(clazz);
        return scans.stream()
                    .filter(fieldScan -> fieldScan.primaryKey() != null)
                    .findFirst()
                    .orElse(null);
    }

    /**
     * Maps fetched data from database into newly created entity.
     *
     * @param clazz      Entity type
     * @param data       Entity data
     * @param dataPrefix Data prefix
     * @param fetchLevel Fetch level
     * @param <T>        Entity type generic
     * @return New entity
     */
    protected <T extends DatabaseEntity> T mapDataToNewEntity(Class<T> clazz, Map<String, Object> data, String dataPrefix, FetchLevel fetchLevel) {
        T entity;
        try {
            entity = clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            logger.error("Failed to instantiate entity", e);
            return null;
        }
        entity.replicated = true;
        entity.fetchLevel = fetchLevel;
        mapDataToEntity(entity, data, dataPrefix, fetchLevel);
        FieldScan primaryKeyField = getPrimaryKeyFieldScan(clazz);
        cache.addPrimaryCacheEntity(entity, ReflectionUtils.getValueOpt(primaryKeyField.field(), entity).orElseThrow());
//        entityTable.put(
//                clazz,
//                ReflectionUtils.getValue(primaryKeyField.field(), entity).orElseThrow(),
//                entity
//        );
        // Check for FetchLevel of object
        // To avoid having Primitive fetch level assigned even though entity is fully loaded
        if (fetchLevel == FetchLevel.Primitive) {
            List<FieldScan> fieldScans = getFieldScansForEntityType(clazz);
            boolean lazyObjectExists = false;
            for (FieldScan fieldScan : fieldScans) {
                if (fieldScan.lazy() == null)
                    continue;
                lazyObjectExists = true;
                break;
            }
            if (!lazyObjectExists)
                entity.fetchLevel = FetchLevel.Full;
        }
//        List<FieldScan> fieldScans = getFieldScansForEntityClass(clazz);
//        boolean fullStatus = true;
//        for (FieldScan fieldScan : fieldScans) {
//            Object value;
//            try {
//                value = fieldScan.field().get(entity);
//            } catch (IllegalAccessException e) {
//                logger.error("Failed to get value from field " + fieldScan.column().value(), e);
//                fullStatus = false;
//                break;
//            }
//            fullStatus = value != null &&
//                         (!DatabaseEntity.class.isAssignableFrom(value.getClass()) || ((DatabaseEntity) value).isFetched());
//
//            if (!fullStatus)
//                break;
//        }
//        if (fullStatus)
//            entity.fetchLevel = FetchLevel.Full;
        return entity;
    }

    /**
     * Maps fetched data from database into already created entity.
     *
     * @param entity     Entity
     * @param data       Data
     * @param dataPrefix Data prefix
     * @param fetchLevel Fetch level
     */
    protected void mapDataToEntity(DatabaseEntity entity, Map<String, Object> data, String dataPrefix, FetchLevel fetchLevel) {
        List<FieldScan> fields = getFieldScansForEntityType(entity.getClass());
        fields = fields.stream()
                       .sorted((o1, o2) -> o1.primaryKey() != null ? -1
                               : (o2.primaryKey() != null ? 1 : (o1.lazy() == null ? -1 : (o2.lazy() == null ? 1 : 0))))
                       .collect(Collectors.toList());
        // TODO: Go field by field and search for values with correct field key
        // TODO: It's better than filling values by data map, because we could get non primary key attribute for foreign object first
        for (FieldScan field : fields) {
            String columnName = dataPrefix + field.column().value();
            Class<? extends DatabaseEntity> foreignObjectClass = null;
            FieldScan foreignObjectPrimaryKeyField = null;
            if (field.foreignKey() != null) {
                foreignObjectClass = (Class<? extends DatabaseEntity>) field.field().getType();
                foreignObjectPrimaryKeyField = getPrimaryKeyFieldScan(foreignObjectClass);
                columnName += "__" + foreignObjectPrimaryKeyField.column().value();
            }
            if (!data.containsKey(columnName))
                continue;

            Object dataValue;
            if (foreignObjectClass != null) {
                Object primaryKeyValue = getBackTransformedValue(foreignObjectPrimaryKeyField, data.get(columnName));
                dataValue = null;
                if (primaryKeyValue != null) {
                    FetchLevel newFetchLevel = field.lazy() == null ? fetchLevel : (fetchLevel == FetchLevel.Full ? FetchLevel.Primitive : FetchLevel.None);
                    if ((dataValue = cache.getPrimaryCacheEntity(foreignObjectClass, primaryKeyValue)) != null) {
                        mapDataToEntity(
                                (DatabaseEntity) dataValue,
                                data,
                                dataPrefix + field.column().value() + "__",
                                newFetchLevel
                        );
                    } else
                        dataValue = mapDataToNewEntity(
                                foreignObjectClass,
                                data,
                                dataPrefix + field.column().value() + "__",
                                newFetchLevel
                        );
                }
            } else {
                dataValue = data.get(columnName);
            }

            ReflectionUtils.setValue(field.field(), entity, getBackTransformedValue(field, dataValue));
        }

    }

    private <T extends DatabaseEntity> T getEntityFromCache(Class<T> clazz, Object primaryKey, FetchLevel fetchLevel) {
        T entity;
        if ((entity = cache.getPrimaryCacheEntity(clazz, primaryKey)) == null)
            return null;

        Preconditions.checkNotNull(entity);
        if (entity.fetchLevel.isLowerThan(fetchLevel))
            loadEntity(entity, fetchLevel);
        return entity;
    }

    /**
     * Finds entity by its primary key value.
     *
     * @param clazz      Entity type
     * @param primaryKey Primary key value
     * @param fetchLevel Fetch level
     * @param <T>        Entity type generic
     * @return Fetched entity or null if not found
     */
    protected <T extends DatabaseEntity> T findEntity(Class<T> clazz, Object primaryKey, FetchLevel fetchLevel) {
        // TODO: Find entity and if fetchLevel is lower than selected, use fetchEntity
        T cachedEntity = getEntityFromCache(clazz, primaryKey, fetchLevel);
        if (cachedEntity != null)
            return cachedEntity;

        QueryResult result = database.getProcessor().findEntityData(clazz, primaryKey, fetchLevel);
        if (result == null || result.getRowCount() == 0)
            return null;

        return mapDataToNewEntity(clazz, result.getDataMap(0), "", fetchLevel);
    }

    /**
     * Finds entity by specified conditions.
     *
     * @param clazz      Entity type
     * @param conditions Conditions
     * @param parameters Parameters
     * @param fetchLevel Fetch level
     * @param <T>        Entity type generic
     * @return Fetched entity or null if not found
     */
    protected <T extends DatabaseEntity> T findEntity(Class<T> clazz, String conditions, Map<String, Object> parameters, FetchLevel fetchLevel) {
        QueryResult result = database.getProcessor().findEntityData(clazz, conditions, parameters, fetchLevel, 0, 1);
        if (result == null || result.getRowCount() == 0)
            return null;

        FieldScan primaryKeyField = getPrimaryKeyFieldScan(clazz);
        Preconditions.checkNotNull(primaryKeyField);
        Map<String, Object> data = result.getDataMap(0);
        Preconditions.checkState(data.containsKey(primaryKeyField.column().value()));
        Object primaryKeyValue = getBackTransformedValue(primaryKeyField, data.get(primaryKeyField.column().value()));
        DatabaseEntity entity;
        if ((entity = cache.getPrimaryCacheEntity(clazz, primaryKeyValue)) != null) {
            if (entity.fetchLevel.isLowerThan(fetchLevel))
                mapDataToEntity(entity, data, "", fetchLevel);
            return (T) entity;
        }

        return mapDataToNewEntity(clazz, data, "", fetchLevel);
    }

    /**
     * Finds all entities by specified conditions.
     *
     * @param clazz      Entity type
     * @param conditions Conditions
     * @param parameters Parameters
     * @param fetchLevel Fetch level
     * @param <T>        Entity type generic
     * @return Set of fetched entities or null if error occurred
     */
    protected <T extends DatabaseEntity> Set<T> findEntities(Class<T> clazz, String conditions, Map<String, Object> parameters, FetchLevel fetchLevel) {
        Set<T> allEntities;
        if (conditions == null && parameters == null && (allEntities = cache.getAllCacheEntities(clazz)) != null) {
            return allEntities;
        }

        QueryResult result = database.getProcessor().findEntityData(clazz, conditions, parameters, fetchLevel, -1, -1);
        if (result == null)
            return null;

        FieldScan primaryKeyField = getPrimaryKeyFieldScan(clazz);
        Preconditions.checkNotNull(primaryKeyField);
        List<T> entities = new ArrayList<>();
        for (Map<String, Object> map : result.getDataMapTable()) {
            Preconditions.checkState(map.containsKey(primaryKeyField.column().value()));
            Object primaryKeyValue = getBackTransformedValue(primaryKeyField, map.get(primaryKeyField.column().value()));
            DatabaseEntity entity;
            if ((entity = cache.getPrimaryCacheEntity(clazz, primaryKeyValue)) != null) {
                if (entity.fetchLevel.isLowerThan(fetchLevel))
                    mapDataToEntity(entity, map, "", fetchLevel);
                entities.add((T) entity);
            } else
                entities.add(mapDataToNewEntity(clazz, map, "", fetchLevel));
        }

        Set<T> dataSet = new HashSet<>(entities);
        if (conditions == null && parameters == null)
            cache.addAllCacheEntities(clazz, (Set<DatabaseEntity>) dataSet);

        return dataSet;
    }

    /**
     * Deletes entity by its primary key value
     *
     * @param type       Entity type
     * @param primaryKey Primary key value
     */
    protected void deleteEntity(Class<? extends DatabaseEntity> type, Object primaryKey) {
        DatabaseEntity entity;
        if ((entity = cache.getPrimaryCacheEntity(type, primaryKey)) != null) {
            entity.delete();
            return;
        }

        database.getProcessor().deleteEntity(type, primaryKey);
        cache.removePrimaryCacheEntity(type, primaryKey);
        DatabaseAspect.clearJoinsFor(type, null);
    }

    /**
     * Deletes entities by specified conditions.
     *
     * @param type       Entity type
     * @param conditions Conditions
     * @param parameters Parameters
     */
    protected void deleteEntities(Class<? extends DatabaseEntity> type, String conditions, Map<String, Object> parameters) {
        database.getProcessor().deleteEntities(type, conditions, parameters);
        cache.clearPrimaryCache(type);
        cache.clearAllCacheEntities(type);
        DatabaseAspect.clearJoinsFor(type, null);
    }

    /**
     * Loads entity into new fetch level.
     *
     * @param entity     Entity
     * @param fetchLevel Fetch level
     */
    protected void loadEntity(DatabaseEntity entity, FetchLevel fetchLevel) {
        if (entity.fetchLevel == fetchLevel || entity.fetchLevel.isHigherThan(fetchLevel))
            return;

        QueryResult result = database.getProcessor().loadEntity(entity, fetchLevel);
        if (result == null)
            return; // TODO: Probably throw an exception
        mapDataToEntity(entity, result.getDataMap(0), "", fetchLevel);
        entity.replicated = true;
        entity.fetchLevel = fetchLevel;
    }

    /**
     * Saves entity.
     * If it's not replicated, it's inserted into database.
     * Otherwise an update is executed.
     *
     * @param entity Entity
     */
    protected void saveEntity(DatabaseEntity entity) {
        if (entity.replicated) {
            database.getProcessor().saveEntityExisting(entity);
            return;
        }

        Object primaryKey = database.getProcessor().saveEntityNew(entity);
        FieldScan primaryKeyFieldScan = getPrimaryKeyFieldScan(entity.getClass());
        Object primaryKeyValue = getBackTransformedValue(primaryKeyFieldScan, primaryKey);
        ReflectionUtils.setValue(primaryKeyFieldScan.field(), entity, primaryKeyValue);
        entity.replicated = true;
        cache.addPrimaryCacheEntity(entity, primaryKeyValue);
        cache.clearAllCacheEntities(entity.getClass());
        DatabaseAspect.clearJoinsFor(entity.getClass(), null);
    }

    /**
     * Deletes specific entity.
     *
     * @param entity Entity
     */
    protected void deleteEntity(DatabaseEntity entity) {
        if (!entity.replicated)
            return;

        FieldScan primaryKeyFieldScan = getPrimaryKeyFieldScan(entity.getClass());
        Object primaryKeyValue = ReflectionUtils.getValueOpt(primaryKeyFieldScan.field(), entity).orElseThrow();
        cache.removePrimaryCacheEntity(entity.getClass(), primaryKeyValue);
        database.getProcessor().deleteEntity(entity);
        cache.clearAllCacheEntities(entity.getClass());
        DatabaseAspect.clearJoinsFor(entity.getClass(), entity);
        entity.replicated = false;
    }
}
