package it.devchallenge.hashphone.persistence;

import java.util.List;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Mono;

public interface HashedPhoneRepository extends ReactiveMongoRepository<HashedPhone, String> {

    Mono<HashedPhone> findByHashValue(String hashValue);

    Mono<HashedPhone> findByNewHashValue(String hashValue);

    Mono<HashedPhone> findByPhoneNumber(String phoneNumber);

    List<HashedPhone> findTop1000ByMigrationIdNot(String migrationId);
}
