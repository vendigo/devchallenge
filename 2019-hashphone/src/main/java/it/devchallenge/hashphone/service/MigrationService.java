package it.devchallenge.hashphone.service;

import java.time.Duration;
import java.util.List;

import org.springframework.stereotype.Service;

import it.devchallenge.hashphone.controller.MigrationRequest;
import it.devchallenge.hashphone.persistence.HashedPhone;
import it.devchallenge.hashphone.persistence.Migration;
import it.devchallenge.hashphone.persistence.MigrationRepository;
import it.devchallenge.hashphone.persistence.PersistenceService;
import it.devchallenge.hashphone.validation.ValidationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Service
@AllArgsConstructor
@Slf4j
public class MigrationService {

    private final MigrationRepository migrationRepository;
    private final PersistenceService persistenceService;
    private final ClockService clockService;
    private final HashingService hashingService;
    private final HashContextService hashContextService;

    public void createMigration(MigrationRequest request) {
        Migration newMigration = new Migration(request.getStartDate(), request.getEndDate(), request.getAlgorithm(), request.getSalt());

        migrationRepository.findAll().stream()
            .filter(migration -> intersectWith(migration, newMigration))
            .findFirst()
            .ifPresent(intersectedMigration -> {
                throw new ValidationException("Migration overlaps with existing one: " + intersectedMigration);
            });

        Migration savedMigration = migrationRepository.save(newMigration);

        scheduleMigration(savedMigration);

        log.info("Migration created: {}, ", savedMigration);

    }

    private void scheduleMigration(Migration savedMigration) {
        HashingParams hashingParams = hashContextService.getHashingParams();

        Flux.<HashedPhone>generate(sink -> {
            List<HashedPhone> nonMigrated = persistenceService.findNonMigrated(savedMigration.getId());
            if (nonMigrated.isEmpty()) {
                sink.complete();
            } else {
                nonMigrated.forEach(sink::next);
            }
        })
            .delaySubscription(Duration.between(clockService.now(), savedMigration.getStartDate()))
            .flatMap(hashedPhone -> hashingService.migrateOne(hashedPhone, hashingParams))
            .subscribeOn(Schedulers.parallel())
            .subscribe();
    }

    private boolean intersectWith(Migration migration, Migration newMigration) {
        return migration.getStartDate().isBefore(newMigration.getEndDate()) &&
            newMigration.getStartDate().isBefore(migration.getEndDate());
    }

}
