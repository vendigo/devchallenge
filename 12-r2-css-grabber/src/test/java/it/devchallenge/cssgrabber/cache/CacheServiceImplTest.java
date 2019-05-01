package it.devchallenge.cssgrabber.cache;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsMapContaining.hasEntry;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CacheServiceImplTest {

    private static final LocalDateTime CURRENT_TIME = LocalDateTime.of(2018, Month.MAY, 30, 11, 0);
    private static final LocalDateTime PAST_TIME = LocalDateTime.of(2018, Month.MAY, 28, 10, 0);
    private static Clock clock = Clock.fixed(CURRENT_TIME.toInstant(ZoneOffset.UTC), ZoneId.of("UTC"));
    private static final String GOOGLE_URL = "google.com";
    private static final String YOUTUBE_URL = "youtube.com";
    private CacheServiceImpl cacheService;

    @Before
    public void setUp() {
        cacheService = new CacheServiceImpl(clock);
    }

    @Test
    public void invalidateCacheByUrl() {
        Map<String, LocalDateTime> cacheTimes = cacheService.getCacheTimes();
        cacheTimes.put(GOOGLE_URL, CURRENT_TIME);
        cacheTimes.put(YOUTUBE_URL, PAST_TIME);
        cacheService.invalidateCache(GOOGLE_URL);
        assertThat(cacheTimes, hasEntry(YOUTUBE_URL, PAST_TIME));
        assertThat(cacheTimes.keySet(), hasSize(1));
    }

    @Test
    public void invalidateWholeCache() {
        Map<String, LocalDateTime> cacheTimes = cacheService.getCacheTimes();
        cacheTimes.put(GOOGLE_URL, CURRENT_TIME);
        cacheTimes.put(YOUTUBE_URL, CURRENT_TIME);
        cacheService.invalidateCache();
        assertThat(cacheTimes.keySet(), empty());
    }

    @Test
    public void markCacheTime() {
        cacheService.markCacheTime(YOUTUBE_URL);
        assertThat(cacheService.getCacheTimes(), hasEntry(YOUTUBE_URL, CURRENT_TIME));
    }
}
