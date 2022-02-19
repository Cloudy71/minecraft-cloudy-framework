package cz.cloudy.minecraft.core.componentsystem.annotations;

import cz.cloudy.minecraft.core.database.DatabaseEntity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

/**
 * @author Cloudy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Cached {
    /**
     * If set to true, there is its own caching system implemented.
     * Otherwise, parameter-value caching system is used.
     *
     * @return Boolean
     */
    boolean informative() default false;

    /**
     * In case there is need to clear cached method value.
     * This can be achieved by using {@link cz.cloudy.minecraft.core.componentsystem.ComponentCache#clear(String)} method.
     *
     * @return String
     * @since 1.18.6
     */
    String id() default "";

    /**
     * Cache even if method's return value is null.
     *
     * @return Boolean
     * @since 1.18.6
     */
    boolean saveNull() default false;

    /**
     * The maximum of entries in method cache.
     * The maximum does not take effect on the maximum of entries in all caches with same cache id.
     * If 0 set, the maximum is unlimited ({@link Integer#MAX_VALUE})
     *
     * @return Integer
     * @since 1.18.6
     */
    int maxSaves() default 0;

    /**
     * The time in ticks elapsed after the last cache update to clear the whole method cache.
     * If 0 set, cache can be cleared only manually using {@link cz.cloudy.minecraft.core.componentsystem.ComponentCache#clear(String)}.
     *
     * @return Integer
     * @since 1.18.6
     */
    int clearAfter() default 0;

    /**
     * If cache value is {@link DatabaseEntity}, it is automatically checked if this entity is still replicated.
     * If cache value is {@link Collection} of {@link DatabaseEntity}, it will automatically remove entities which are not replicated.
     * Its default value is set to false due to its potential performance needs in case the return value is a collection.
     *
     * @return Boolean
     * @since 1.18.6
     */
    boolean entityReplicationCheck() default false;
}
