package it.devchallenge.backend.config;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Holds schedule for updating articles
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleConfig {
    private List<ExecTime> execTimes;

    /**
     * Return max pagesToSync from configuration
     */
    public int getMaxPagesToLoad() {
        return execTimes.stream()
                .mapToInt(ExecTime::getPagesToSync).max()
                .orElseThrow(()->new IllegalArgumentException("No configuration provided"));
    }
}
