/*
  User: Cloudy
  Date: 07/01/2022
  Time: 21:53
*/

package cz.cloudy.minecraft.core.types;

/**
 * @author Cloudy
 */
public class Pair<K, V> {
    private final K key;
    private final V value;

    /**
     * Default constructor.
     * @param key Key
     * @param value Value
     */
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Getter for key.
     * @return Key
     */
    public K getKey() {
        return key;
    }

    /**
     * Getter for value.
     * @return Value
     */
    public V getValue() {
        return value;
    }
}
