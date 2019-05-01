package it.devchallenge.backend.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents point for synchronization: cron expression - when to run and how many pages to sync.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecTime {
    private int pagesToSync;
    private String cron;
}
