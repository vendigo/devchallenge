package it.devchallenge.cssgrabber.cache;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;

import lombok.AllArgsConstructor;

/**
 * Runs every minute and invalidates cache records older than 10 minutes (Configured via property)
 */
@AllArgsConstructor
public class CacheInvalidationComponent {

    private static final int ONE_MINUTE = 60_000;
    private final Clock clock;
    private final int cacheLiveTimeInMinutes;
    private final CacheService cacheService;

    @Scheduled(fixedRate = ONE_MINUTE)
    public void cleanCache() {
        LocalDateTime now = LocalDateTime.now(clock);
        Map<String, LocalDateTime> cacheTimes = cacheService.getCacheTimes();
        List<String> urls = new ArrayList<>(cacheTimes.keySet());

        for (String url : urls) {
            LocalDateTime populationTime = cacheTimes.get(url);
            if (minutesBetween(now, populationTime) > cacheLiveTimeInMinutes) {
                cacheService.invalidateCache(url);
            }
        }
    }

    private int minutesBetween(final LocalDateTime now, final LocalDateTime populationTime) {
        return (int) Duration.between(populationTime, now).getSeconds() / 60;
    }
}
