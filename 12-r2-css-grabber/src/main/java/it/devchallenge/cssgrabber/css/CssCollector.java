package it.devchallenge.cssgrabber.css;

import java.util.Map;

/**
 * Merges CSS rules into one StyleSheet
 */
class CssCollector {

    static void combine(Map<String, Map<String, String>> left, Map<String, Map<String, String>> right) {
        right.forEach((selector, properties) -> mergeMap(left, properties, selector));
    }

    static void accumulate(Map<String, Map<String, String>> styles, CssItem item) {
        String selector = item.getSelector();
        Map<String, String> currentValues = item.getCssValues();
        mergeMap(styles, currentValues, selector);
    }

    private static void mergeMap(Map<String, Map<String, String>> target, Map<String, String> map, String key) {
        if (!target.containsKey(key)) {
            target.put(key, map);
        } else {
            target.get(key).putAll(map);
        }
    }
}
