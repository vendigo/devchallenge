package it.devchallenge.cssgrabber.cache;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CacheServiceImpl implements CacheService {

    @Getter
    private final ConcurrentHashMap<String, LocalDateTime> cacheTimes = new ConcurrentHashMap<>();
    private final Clock clock;

    @Override
    @CacheEvict(cacheNames = STYLES_CACHE_NAME)
    public void invalidateCache(String url) {
        log.info("Invalidating cache for key: {}", url);
        cacheTimes.remove(url);
    }

    @Override
    @CacheEvict(cacheNames = STYLES_CACHE_NAME, allEntries = true)
    public void invalidateCache() {
        log.info("Invalidating whole cache");
        cacheTimes.clear();
    }

    @Override
    public void markCacheTime(final String url) {
        cacheTimes.put(url, LocalDateTime.now(clock));
    }

}
