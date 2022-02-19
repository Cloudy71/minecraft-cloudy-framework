/*
  User: Cloudy
  Date: 07/01/2022
  Time: 03:59
*/

package cz.cloudy.minecraft.core.database;

import com.google.common.base.Preconditions;
import cz.cloudy.minecraft.core.LoggerFactory;
import cz.cloudy.minecraft.core.componentsystem.ReflectionUtils;
import cz.cloudy.minecraft.core.componentsystem.annotations.Cached;
import cz.cloudy.minecraft.core.componentsystem.annotations.Component;
import cz.cloudy.minecraft.core.database.enums.DatabaseEngine;
import cz.cloudy.minecraft.core.database.enums.FetchLevel;
import cz.cloudy.minecraft.core.database.interfaces.IDatabaseProcessor;
import cz.cloudy.minecraft.core.database.types.DatabaseConnectionData;
import cz.cloudy.minecraft.core.database.types.FieldScan;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Cloudy
 */
/* TODO: Every plugin replaces processor object. Old processor objects keep connection to DB.
 *       To keep support for different dbs for each plugin, this component must be plugin singleton based.
 */
@Component
public class Database {
    private static final Logger logger = LoggerFactory.getLogger(Database.class);

    @Component
    private DatabaseEntityMapper databaseEntityMapper;

    private IDatabaseProcessor<?> processor = null;

    /**
     * Initializes database processor.
     *
     * @param data Connection data
     */
    public void initializeProcessor(DatabaseConnectionData data) {
        processor = ReflectionUtils.newInstance(data.engine().getEngineClass());
        Preconditions.checkNotNull(processor);

        try {
            logger.info("Connecting to the database");
            boolean connected = processor.connect(data);
            if (connected)
                logger.info("Connected!");
        } catch (SQLException e) {
            logger.error("", e);
        }
    }

    /**
     * Gets current database processor.
     *
     * @return Database processor
     */
    public IDatabaseProcessor<?> getProcessor() {
        return processor;
    }

    /**
     * Generates database connection data.
     *
     * @param engine Database engine
     * @param host   Host
     * @param port   Port
     * @param user   User
     * @param pass   Password
     * @param db     Database
     * @return Database connection data
     */
    public DatabaseConnectionData generateDatabaseConnectionDataFromAttributes(DatabaseEngine engine, String host, int port, String user, String pass,
                                                                               String db) {
        String url = engine.resolveUrl(host, port, db);
        //"mysql://" + host + ":" + port + "/" + db
        return new DatabaseConnectionData(
                engine,
                url,
                user,
                pass
        );
    }

    /**
     * Processes query object.
     *
     * @param query      Query object
     * @param parameters Parameters
     * @return Query result
     */
    public QueryResult processQuery(Query query, Map<String, Object> parameters) {
        try {
            return getProcessor().processQuery(query, parameters);
        } catch (SQLException e) {
            logger.error("Failed to process query", e);
        }
        return null;
    }

    /**
     * Processes query
     *
     * @param query Query
     * @return Query result
     */
    public QueryResult processQuery(Query query) {
        return processQuery(query, null);
    }

    /**
     * Finds entity by its primary key value.
     *
     * @param clazz      Entity type
     * @param primaryKey Primary key value
     * @param fetchLevel Fetch level
     * @param <T>        Entity type generic
     * @return Fetched database entity or null if not found
     */
    @Cached(informative = true)
    public <T extends DatabaseEntity> T findEntity(Class<T> clazz, Object primaryKey, FetchLevel fetchLevel) {
        return databaseEntityMapper.findEntity(clazz, primaryKey, fetchLevel);
    }

    /**
     * Finds entity by its primary key value.
     *
     * @param clazz      Entity type
     * @param primaryKey Primary key value
     * @param <T>        Entity type generic
     * @return Fetched database entity or null if not found
     */
    @Cached(informative = true)
    public <T extends DatabaseEntity> T findEntity(Class<T> clazz, Object primaryKey) {
        return findEntity(clazz, primaryKey, FetchLevel.Primitive);
    }

    /**
     * Finds entity by database conditions.
     *
     * @param clazz      Entity type
     * @param conditions Database conditions
     * @param parameters Parameters
     * @param fetchLevel Fetch level
     * @param <T>        Entity type generic
     * @return Fetched database entity or null if not found
     */
    public <T extends DatabaseEntity> T findEntity(Class<T> clazz, String conditions, Map<String, Object> parameters, FetchLevel fetchLevel) {
        return databaseEntityMapper.findEntity(clazz, conditions, parameters, fetchLevel);
    }

    /**
     * Finds entities by database conditions.
     *
     * @param clazz      Entity type
     * @param conditions Database conditions
     * @param parameters Parameters
     * @param fetchLevel Fetch level
     * @param <T>        Entity type generic
     * @return Fetched database entities
     */
    public <T extends DatabaseEntity> Set<T> findEntities(Class<T> clazz, String conditions, Map<String, Object> parameters, FetchLevel fetchLevel) {
        return databaseEntityMapper.findEntities(clazz, conditions, parameters, fetchLevel);
    }

    /**
     * Finds all entities.
     *
     * @param clazz      Entity type
     * @param fetchLevel Fetch level
     * @param <T>        Entity type generic
     * @return Fetched database entities
     */
    @Cached(informative = true)
    public <T extends DatabaseEntity> Set<T> findEntities(Class<T> clazz, FetchLevel fetchLevel) {
        return findEntities(clazz, null, null, fetchLevel);
    }

    /**
     * Finds all entities.
     *
     * @param clazz Entity type
     * @param <T>   Entity type generic
     * @return Fetched database entities
     */
    @Cached(informative = true)
    public <T extends DatabaseEntity> Set<T> findEntities(Class<T> clazz) {
        return findEntities(clazz, FetchLevel.Primitive);
    }

    /**
     * Deletes entity by its primary key value
     *
     * @param type       Entity type
     * @param primaryKey Entity primary key value
     * @since 1.18.6
     */
    public void deleteEntity(Class<? extends DatabaseEntity> type, Object primaryKey) {
        databaseEntityMapper.deleteEntity(type, primaryKey);
    }

    /**
     * Deletes all entities of specified type and conditions
     *
     * @param type       Entity type
     * @param conditions Conditions
     * @param parameters Parameters
     * @since 1.18.5
     */
    public void deleteEntities(Class<? extends DatabaseEntity> type, String conditions, Map<String, Object> parameters) {
        databaseEntityMapper.deleteEntities(type, conditions, parameters);
    }

    /**
     * Loads entity.
     *
     * @param entity     Entity
     * @param fetchLevel Fetch level
     */
    protected void loadEntity(DatabaseEntity entity, FetchLevel fetchLevel) {
        databaseEntityMapper.loadEntity(entity, fetchLevel);
    }

    /**
     * Loads entity.
     *
     * @param entity Entity
     */
    protected void saveEntity(DatabaseEntity entity) {
        databaseEntityMapper.saveEntity(entity);
    }

    /**
     * Deep saves entity.
     *
     * @param entity Entity
     */
    protected void fullSaveEntity(DatabaseEntity entity) {
        databaseEntityMapper.saveEntity(entity);
        List<FieldScan> fields = databaseEntityMapper.getFieldScansForEntityType(entity.getClass());
        for (FieldScan field : fields) {
            if (field.foreignKey() == null)
                continue;
            DatabaseEntity obj = (DatabaseEntity) ReflectionUtils.getValueOpt(field.field(), entity).orElse(null);
            if (obj == null)
                return;

            fullSaveEntity(obj);
        }
    }

    /**
     * Deletes entity
     *
     * @param entity Entity
     */
    protected void deleteEntity(DatabaseEntity entity) {
        databaseEntityMapper.deleteEntity(entity);
    }
}
