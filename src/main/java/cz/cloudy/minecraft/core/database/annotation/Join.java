package cz.cloudy.minecraft.core.database.annotation;

import cz.cloudy.minecraft.core.database.DatabaseEntity;
import cz.cloudy.minecraft.core.database.enums.FetchLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Cloudy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Join {
    /**
     * Entity type.
     *
     * @return Class
     */
    Class<? extends DatabaseEntity> table();

    /**
     * Where condition.
     *
     * @return String
     */
    String where();

    /**
     * Join entities fetch level.
     *
     * @return Fetch level
     * @since 1.18.5
     */
    FetchLevel fetchLevel() default FetchLevel.Primitive;
}
