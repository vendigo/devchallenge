package it.devchallenge.backend;

import java.util.HashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestUtils {

    @Value
    public static class Entry<K, V> {
        private final K key;
        private final V value;
    }

    public static <K, V> Entry<K, V> entry(K key, V value) {
        return new Entry<>(key, value);
    }

    @SafeVarargs
    public static <K, V> Map<K, V> mapOf(Entry<K, V>... entries) {
        Map<K, V> result = new HashMap<>();

        for (Entry<K, V> entry : entries) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
