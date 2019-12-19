package org.threeten.bp.jdk8;

import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Provides factory methods for concurrency {@link Map maps}, which will be replaced with a regular {@link Map} when transpiled.
 */
public final class JdkCollections {

    public static <K, V> Map<K, V> concurrentHashMap() {
        return new ConcurrentHashMap<>();
    }

    public static <K, V> Map<K, V> concurrentHashMap(final int initialCapacity,
                                                     final float loadRatio) {
        return new ConcurrentHashMap<>(initialCapacity, loadRatio);
    }

    public static <K, V> Map<K, V> concurrentHashMap(final int initialCapacity,
                                                     final float loadRatio,
                                                     final int concurrencyLevel) {
        return new ConcurrentHashMap<>(initialCapacity, loadRatio, concurrencyLevel);
    }

    public static <K, V> NavigableMap<K, V> concurrentNavigableMap() {
        return new ConcurrentSkipListMap<>();
    }

    private JdkCollections() {
        throw new UnsupportedOperationException();
    }
}
