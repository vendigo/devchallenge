package it.devchallenge.cssgrabber.cache;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Service for working with cache. Delegates cache invalidation to Spring Caching framework and
 * maintains cache timings for urls.
 */
public interface CacheService {
    String STYLES_CACHE_NAME = "STYLES_CACHE";

    void invalidateCache(String url);

    void invalidateCache();

    /**
     * Should be called together with cache population in order to save caching time.
     */
    void markCacheTime(String url);

    Map<String, LocalDateTime> getCacheTimes();
}
