package interfaces;

import java.util.Optional;

public interface Cache<K, V> {
    void set(K key, V value);

    Optional<V> get(K key);

    void invalidate(K key);

    int size();

    boolean isEmpty();

    void clear();
}
