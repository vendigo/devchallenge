package it.devchallenge.cssgrabber.chrome;

import java.util.List;
import java.util.Map;

/**
 * Encapsulates all logic for working with Remote Chrome.
 */
public interface ChromeClient {
    List<Map<String, String>> getUsedStyles(String url);
}
