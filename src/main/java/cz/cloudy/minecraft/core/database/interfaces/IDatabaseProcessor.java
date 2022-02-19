/*
  User: Cloudy
  Date: 07/01/2022
  Time: 02:58
*/

package cz.cloudy.minecraft.core.database.interfaces;

import cz.cloudy.minecraft.core.componentsystem.ReflectionUtils;
import cz.cloudy.minecraft.core.database.DatabaseEntity;
import cz.cloudy.minecraft.core.database.Query;
import cz.cloudy.minecraft.core.database.QueryResult;
import cz.cloudy.minecraft.core.database.enums.FetchLevel;
import cz.cloudy.minecraft.core.database.types.DatabaseConnectionData;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Cloudy
 */
public interface IDatabaseProcessor<T extends IDatabaseMapper> {
    /**
     * Registered mappers for processors.
     */
    Map<Class<? extends IDatabaseMapper>, IDatabaseMapper> mappers = new HashMap<>();

    /**
     * Connects to the database.
     *
     * @param data Database connection data
     * @return True if connection was successful
     * @throws SQLException If any database error occurred
     */
    boolean connect(DatabaseConnectionData data) throws SQLException;

    /**
     * Checks for connection to the database.
     *
     * @return True if connection to the database is alive
     * @throws SQLException If any database error occurred
     */
    boolean isConnected() throws SQLException;

//    /**
//     * @return
//     */
//    IDatabaseMapper getMapper();

    /**
     * Gets this processor's mapper.
     * Mapper is automatically created when called the first time.
     *
     * @return Processor's mapper
     */
    default T getMapper() {
        ParameterizedType parameterizedType = null;
        for (Type genericInterface : getClass().getGenericInterfaces()) {
            if (genericInterface != IDatabaseProcessor.class)
                continue;
            parameterizedType = (ParameterizedType) genericInterface;
        }
        if (parameterizedType == null) {
            parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        }
        Class<? extends IDatabaseMapper> type = (Class<? extends IDatabaseMapper>) parameterizedType.getActualTypeArguments()[0];
        if (!mappers.containsKey(type)) {
            mappers.put(type, ReflectionUtils.newInstance(type));
        }
        return (T) mappers.get(type);
    }

    /**
     * Builds table structure in database.
     */
    void buildTableStructure();

    /**
     * Build constraint structure in database.
     */
    void buildConstraintStructure();

    /**
     * Processes query object
     *
     * @param query      Query
     * @param parameters Parameters
     * @return Query result
     * @throws SQLException If any database error occurred
     */
    QueryResult processQuery(Query query, Map<String, Object> parameters) throws SQLException;

    /**
     * Finds entity data.
     *
     * @param clazz      Entity type
     * @param primaryKey Primary key value
     * @param fetchLevel Fetch level
     * @return Query result
     */
    QueryResult findEntityData(Class<? extends DatabaseEntity> clazz, Object primaryKey, FetchLevel fetchLevel);

    /**
     * Finds entity data.
     *
     * @param clazz      Entity type
     * @param conditions Conditions
     * @param parameters Parameters
     * @param fetchLevel Fetch level
     * @param from       Offset
     * @param limit      Limit
     * @return Query result
     */
    QueryResult findEntityData(Class<? extends DatabaseEntity> clazz, String conditions, Map<String, Object> parameters, FetchLevel fetchLevel, int from,
                               int limit);

    /**
     * Loads entity
     *
     * @param entity     Entity
     * @param fetchLevel Fetch level
     * @return Query result
     */
    QueryResult loadEntity(DatabaseEntity entity, FetchLevel fetchLevel);

    /**
     * Saves new non-replicated entity.
     *
     * @param entity Entity
     * @return Generated or set primary key value
     */
    Object saveEntityNew(DatabaseEntity entity);

    /**
     * Saves already replicated entity.
     *
     * @param entity Entity
     */
    void saveEntityExisting(DatabaseEntity entity);

    /**
     * Deletes existing entity.
     *
     * @param entity Entity
     */
    void deleteEntity(DatabaseEntity entity);

    /**
     * Deletes entity by its primary key value
     *
     * @param type       Entity type
     * @param primaryKey Primary key value
     * @since 1.18.6
     */
    void deleteEntity(Class<? extends DatabaseEntity> type, Object primaryKey);

    /**
     * Deletes entities by conditions.
     *
     * @param type       Entity type
     * @param conditions Conditions
     * @param parameters Parameters
     * @since 1.18.5
     */
    void deleteEntities(Class<? extends DatabaseEntity> type, String conditions, Map<String, Object> parameters);
}
