/*
  User: Cloudy
  Date: 06/01/2022
  Time: 20:36
*/

package cz.cloudy.minecraft.core.componentsystem.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Cloudy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface Component {
    /**
     * Wire type
     *
     * @since 1.18.6
     */
    enum WireType {
        /**
         * Component can be obtained both ways: getting a component using {@link cz.cloudy.minecraft.core.componentsystem.ComponentLoader} or
         * autowiring it using {@link Component} annotation on field.
         */
        All,
        /**
         * Component can be obtained only by autowiring it using {@link Component} annotation on field.
         */
        AutoWireOnly,
        /**
         * Component be obtained only by using {@link cz.cloudy.minecraft.core.componentsystem.ComponentLoader}.
         */
        ManualOnly
    }

    /**
     * Sets what components are able to wire this component.
     * If value is empty array then no limitations are set, otherwise only selected components are able to wire this component
     * This wiring limit does not affect getting a component using {@link cz.cloudy.minecraft.core.componentsystem.ComponentLoader#get(Class)}
     *
     * @return Class array
     * @since 1.18.6
     */
    Class<?>[] wiredFrom() default {};

    /**
     * Defines how component can be used.
     * There are two usage types: get component using {@link cz.cloudy.minecraft.core.componentsystem.ComponentLoader} or autowire it using
     * {@link Component} annotation on field.
     *
     * @return Wire type
     * @since 1.18.6
     */
    WireType wireType() default WireType.All;
}