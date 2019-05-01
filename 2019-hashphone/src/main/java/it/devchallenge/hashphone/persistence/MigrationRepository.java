package it.devchallenge.hashphone.persistence;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MigrationRepository extends MongoRepository<Migration, String> {

    Optional<Migration> getTopByStartDateBeforeOrderByStartDateDesc(LocalDateTime now);
}
