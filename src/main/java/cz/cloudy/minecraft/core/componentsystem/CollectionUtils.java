package cz.cloudy.minecraft.core.componentsystem;

import java.util.*;

/**
 * @author Cloudy
 */
public class CollectionUtils {
    /**
     * Checks if collection type is supported for build.
     *
     * @param type Collection type
     * @return True if collection type is supported
     */
    public static boolean isCollectionTypeSupported(Class<? extends Collection<?>> type) {
        return Set.class.isAssignableFrom(type) || List.class.isAssignableFrom(type);
    }

    /**
     * Creates new collection by collection type and adds initial items.
     *
     * @param type         Collection type
     * @param initialItems Initial items
     * @param <T>          Collection type generic
     * @return New collection or null if collection type is unsupported
     */
    public static <T> Collection<T> getCollection(Class<Collection<T>> type, Collection<T> initialItems) {
        if (!isCollectionTypeSupported(type))
            return null;

        if (Set.class.isAssignableFrom(type))
            return new HashSet<>(initialItems);
        if (List.class.isAssignableFrom(type))
            return new ArrayList<>(initialItems);
        return null;
    }
}
