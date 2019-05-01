package it.devchallenge.hashphone.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.devchallenge.hashphone.persistence.Migration;
import it.devchallenge.hashphone.persistence.MigrationRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class HashContextService {

    private final String defaultAlgorithm;
    private final String defaultSalt;
    private final ClockService clockService;
    private final MigrationRepository migrationRepository;

    public HashContextService(ClockService clockService,
        @Value("${hash.phone.default.algorithm}") String defaultAlgorithm,
        @Value("${hash.phone.default.salt}") String defaultSalt, MigrationRepository migrationRepository) {
        this.clockService = clockService;
        this.defaultAlgorithm = defaultAlgorithm;
        this.defaultSalt = defaultSalt;
        this.migrationRepository = migrationRepository;
    }

    private Optional<Migration> getLastMigration() {
        return migrationRepository.getTopByStartDateBeforeOrderByStartDateDesc(clockService.now());
    }

    public HashingParams getHashingParams() {
        HashingParams hashingParams = getLastMigration()
            .map(migration -> new HashingParams(migration.getAlgorithm(), migration.getSalt(), isMigrationActive(migration)))
            .orElseGet(() -> new HashingParams(defaultAlgorithm, defaultSalt, null));
        log.debug("Got hashing params: {}", hashingParams);
        return hashingParams;
    }

    private String isMigrationActive(Migration migration) {
        LocalDateTime now = clockService.now();
        boolean isMigration = now.isAfter(migration.getStartDate()) && now.isBefore(migration.getEndDate());
        return isMigration ? migration.getId() : null;
    }
}
