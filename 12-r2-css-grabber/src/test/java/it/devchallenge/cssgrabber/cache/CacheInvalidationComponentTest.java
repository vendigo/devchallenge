package it.devchallenge.cssgrabber.cache;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.ImmutableMap;

@RunWith(MockitoJUnitRunner.class)
public class CacheInvalidationComponentTest {

    private static final LocalDateTime CURRENT_TIME = LocalDateTime.of(2018, Month.MAY, 30, 11, 0);
    private static final String GOOGLE_URL = "google.com";
    private static final String YOUTUBE_URL = "youtube.com";
    private static Clock clock = Clock.fixed(CURRENT_TIME.toInstant(ZoneOffset.UTC), ZoneId.of("UTC"));

    @Mock
    private CacheService cacheService;

    @Test
    public void cleanCache() {
        final CacheInvalidationComponent cacheInvalidationComponent = new CacheInvalidationComponent(clock, 10, cacheService);

        Map<String, LocalDateTime> cacheTimes = ImmutableMap.<String, LocalDateTime>builder()
            .put(GOOGLE_URL, LocalDateTime.from(CURRENT_TIME).minusMinutes(3))
            .put(YOUTUBE_URL, LocalDateTime.from(CURRENT_TIME).minusMinutes(20))
            .build();

        when(cacheService.getCacheTimes()).thenReturn(cacheTimes);

        cacheInvalidationComponent.cleanCache();
        verify(cacheService).invalidateCache(YOUTUBE_URL);
    }
}
