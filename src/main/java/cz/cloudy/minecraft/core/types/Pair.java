/*
  User: Cloudy
  Date: 07/01/2022
  Time: 21:53
*/

package cz.cloudy.minecraft.core.types;

/**
 * Pair is not immutable since 1.18.7. To use immutable type of Pair, use {@link ImmutablePair}.
 *
 * @author Cloudy
 */
public class Pair<K, V> {
    private K key;
    private V value;

    /**
     * Default constructor.
     *
     * @param key   Key
     * @param value Value
     */
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Setter for key.
     *
     * @param key Key
     * @since 1.18.7
     */
    public void setKey(K key) {
        this.key = key;
    }

    /**
     * Getter for key.
     *
     * @return Key
     */
    public K getKey() {
        return key;
    }

    /**
     * Setter for value.
     *
     * @param value Value
     * @since 1.18.7
     */
    public void setValue(V value) {
        this.value = value;
    }

    /**
     * Getter for value.
     *
     * @return Value
     */
    public V getValue() {
        return value;
    }
}
