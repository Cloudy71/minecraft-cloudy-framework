/*
  User: Cloudy
  Date: 19/02/2022
  Time: 15:23
*/

package cz.cloudy.minecraft.core.types;

/**
 * @author Cloudy
 */
public class ImmutablePair<K, V>
        extends Pair<K, V> {
    /**
     * Default constructor.
     *
     * @param key   Key
     * @param value Value
     */
    public ImmutablePair(K key, V value) {
        super(key, value);
    }

    @Override
    public void setKey(K key) {
        throw new RuntimeException("Key cannot be changed for immutable instance.");
    }

    @Override
    public void setValue(V value) {
        throw new RuntimeException("Value cannot be changed for immutable instance.");
    }
}
