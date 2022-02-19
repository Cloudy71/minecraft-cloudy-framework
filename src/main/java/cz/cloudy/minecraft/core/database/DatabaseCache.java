/*
  User: Cloudy
  Date: 02/02/2022
  Time: 22:06
*/

package cz.cloudy.minecraft.core.database;

import cz.cloudy.minecraft.core.componentsystem.ReflectionUtils;
import cz.cloudy.minecraft.core.componentsystem.annotations.Component;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Cloudy
 */
@Component(wiredFrom = {Database.class, DatabaseEntityMapper.class})
public class DatabaseCache {
    private static final Map<Class<? extends DatabaseEntity>, Map<Object, DatabaseEntity>> primaryCache =
            new HashMap<>();
    private static final Map<Class<? extends DatabaseEntity>, Set<DatabaseEntity>>         allCache     =
            new HashMap<>();
    //    private static final Map<Class<? extends DatabaseEntity>, Map<Object, Map<Method, Object>>> joinCache =
//            new HashMap<>();  // TODO: Implement join cache including cache clear if value is Collection
    private static final Map<Method, Map<Object, Object>>                                  joinCache    = new HashMap<>(); // Method -> PrimaryValue -> Object

    @Component
    private DatabaseEntityMapper entityMapper;

    /**
     * Clears all database caches
     *
     * @since 1.18.5
     */
    public void clear() {
        primaryCache.clear();
        allCache.clear();
        joinCache.clear();
    }

    /**
     * Creates cache for entity type
     *
     * @param type Entity type
     */
    protected void addCacheForEntityType(Class<? extends DatabaseEntity> type) {
        primaryCache.put(type, new HashMap<>());
    }

    /**
     * Maps entity by its primary key value.
     *
     * @param entity       Entity
     * @param primaryValue Primary key value
     */
    protected void addPrimaryCacheEntity(@NotNull DatabaseEntity entity, Object primaryValue) {
        if (primaryValue == null)
            return;
        Map<Object, DatabaseEntity> map = primaryCache.get(entity.getClass());
        if (map == null)
            return;
        map.put(primaryValue, entity);
    }

    /**
     * Gets entity by its primary key value.
     *
     * @param type         Entity type
     * @param primaryValue Primary key value
     * @param <T>          Entity type generic
     * @return Entity or null if none found
     */
    @Nullable
    protected <T extends DatabaseEntity> T getPrimaryCacheEntity(@NotNull Class<T> type, Object primaryValue) {
        if (primaryValue == null)
            return null;
        Map<Object, DatabaseEntity> map = primaryCache.get(type);
        if (map == null)
            return null;
        return (T) map.get(primaryValue);
    }

    /**
     * Removes entity by its primary key value.
     *
     * @param entity       Entity type
     * @param primaryValue Primary key value
     */
    protected void removePrimaryCacheEntity(Class<? extends DatabaseEntity> entity, Object primaryValue) {
        if (primaryValue == null)
            return;
        Map<Object, DatabaseEntity> map = primaryCache.get(entity);
        if (map == null)
            return;
        map.remove(primaryValue);
    }

    /**
     * Clears primary key cache by entity type.
     *
     * @param type Entity type
     */
    protected void clearPrimaryCache(@NotNull Class<? extends DatabaseEntity> type) {
        primaryCache.remove(type);
    }

    /**
     * Adds all cache entities.
     * Is used when fetching all entity type entities.
     *
     * @param type     Entity type
     * @param entities Entities
     */
    protected void addAllCacheEntities(Class<? extends DatabaseEntity> type, @NotNull Set<DatabaseEntity> entities) {
        allCache.put(type, entities);
    }

    /**
     * Gets all cache entities.
     *
     * @param type Entity type
     * @param <T>  Entity type generic
     * @return Entity set or null if never fetched
     */
    @Nullable
    protected <T> Set<T> getAllCacheEntities(@NotNull Class<T> type) {
        return (Set<T>) allCache.get(type);
    }

    /**
     * Clears all entity cache entities.
     *
     * @param type Entity type
     */
    protected void clearAllCacheEntities(Class<? extends DatabaseEntity> type) {
        allCache.remove(type);
    }

    /**
     * Gets primary key value from entity.
     *
     * @param entity Entity
     * @return Entity's primary key value
     */
    private Object getPrimaryValue(@NotNull DatabaseEntity entity) {
        return ReflectionUtils.getValueOpt(entityMapper.getPrimaryKeyFieldScan(entity.getClass()).field(), entity).orElse(null);
    }

    /**
     * Adds join cache.
     *
     * @param entity Entity
     * @param method Method
     * @param value  Value
     */
    protected void addJoinCache(@NotNull DatabaseEntity entity, Method method, Object value) {
        Object primaryValue = getPrimaryValue(entity);
        if (primaryValue == null)
            return;
        joinCache.computeIfAbsent(method, m -> new HashMap<>())
                 .computeIfAbsent(primaryValue, o -> value);
//        joinCache.computeIfAbsent(entity.getClass(), c -> new HashMap<>())
//                .computeIfAbsent(primaryValue, o -> new HashMap<>())
//                .computeIfAbsent(method, m -> value);
    }

    /**
     * Gets join cache.
     *
     * @param entity Entity
     * @param method Method
     * @return Join cache object
     */
    protected Object getJoinCache(@NotNull DatabaseEntity entity, Method method) {
        Map<Object, Object> primaryValueMap = joinCache.get(method);
        if (primaryValueMap == null)
            return null;
        Object primaryValue = getPrimaryValue(entity);
        if (primaryValue == null)
            return null;
        return primaryValueMap.get(primaryValue);
//        Map<Object, Map<Method, Object>> primaryValueMap = joinCache.get(entity.getClass());
//        if (primaryValueMap == null)
//            return null;
//        Object primaryValue = getPrimaryValue(entity);
//        if (primaryValue == null)
//            return null;
//        Map<Method, Object> methodMap = primaryValueMap.get(primaryValue);
//        if (methodMap == null)
//            return null;
//        return methodMap.get(method);
    }

    /**
     * Clears join cache.
     *
     * @param method          Method
     * @param comparingEntity Comparing entity
     */
    protected void clearJoinCache(Method method, @NotNull DatabaseEntity comparingEntity) {
        Map<Object, Object> primaryValueMap = joinCache.get(method);
        if (primaryValueMap == null)
            return;
        List<Object> keysToRemove = new ArrayList<>();
        for (Map.Entry<Object, Object> entry : primaryValueMap.entrySet()) {
            if (!entry.getValue().equals(comparingEntity))
                continue;
            keysToRemove.add(entry.getKey());
        }
        for (Object key : keysToRemove) {
            primaryValueMap.remove(key);
        }
//        Object primaryValue = getPrimaryValue(entity);
//        if (primaryValue == null)
//            return;
//        primaryValueMap.remove(primaryValue);
//        Map<Object, Map<Method, Object>> primaryValueMap = joinCache.get(entity.getClass());
//        if (primaryValueMap == null)
//            return;
//        Object primaryValue = getPrimaryValue(entity);
//        if (primaryValue == null)
//            return;
//        Map<Method, Object> methodMap = primaryValueMap.get(primaryValue);
//        if (methodMap == null)
//            return;
//        methodMap.remove(method);
    }

    /**
     * Clears all method's join cache.
     *
     * @param method Method
     */
    protected void clearJoinCacheMethodAll(Method method) {
        joinCache.remove(method);
//        Map<Object, Map<Method, Object>> primaryValueMap = joinCache.get(type);
//        if (primaryValueMap == null)
//            return;
//
//        for (Map.Entry<Object, Map<Method, Object>> entry : primaryValueMap.entrySet()) {
//            entry.getValue().remove(method);
//        }
    }
}
