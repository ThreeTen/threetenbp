/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.j2cl.java.time;

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

/**
 * Provides factory methods for concurrency {@link Map maps}, which will be replaced with a regular {@link Map} when transpiled.
 */
public final class JdkCollections {

    public static <K, V> Map<K, V> concurrentHashMap() {
        //return new ConcurrentHashMap<>();
        return Maps.hash();
    }

    public static <K, V> Map<K, V> concurrentHashMap(final int initialCapacity,
                                                     final float loadRatio) {
        //return new ConcurrentHashMap<>(initialCapacity, loadRatio);
        return concurrentHashMap();
    }

    public static <K, V> Map<K, V> concurrentHashMap(final int initialCapacity,
                                                     final float loadRatio,
                                                     final int concurrencyLevel) {
        //return new ConcurrentHashMap<>(initialCapacity, loadRatio, concurrencyLevel);
        return concurrentHashMap();
    }

    public static <K, V> NavigableMap<K, V> concurrentNavigableMap() {
        //return new ConcurrentSkipListMap<>();
        return new TreeMap<>();
    }

    public static <E> List<E> copyOnWriteArrayList() {
        //return new CopyOnWriteArrayList<>();
        return Lists.array();
    }

    public static <E> Set<E> copyOnWriteArraySet() {
        //return new CopyOnWriteArraySet<>();
        return Sets.hash();
    }

    private JdkCollections() {
        throw new UnsupportedOperationException();
    }
}
