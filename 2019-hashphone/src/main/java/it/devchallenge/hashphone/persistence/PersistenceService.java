package it.devchallenge.hashphone.persistence;

import java.util.List;

import reactor.core.publisher.Mono;

public interface PersistenceService {

    Mono<HashedPhone> save(HashedPhone hashedPhone, String hashValue, String migrationId);

    Mono<HashedPhone> findHashByPhoneNumber(String phoneNumber);

    Mono<HashedPhone> findPhoneNumberByHash(String hashValue);

    Mono<HashedPhone> findPhoneNumberByNewHash(String hashValue);

    Mono<Boolean> hashValueNotExist(String hashValue);

    List<HashedPhone> findNonMigrated(String migrationId);
}
