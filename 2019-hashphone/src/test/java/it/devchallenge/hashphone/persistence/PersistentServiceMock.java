package it.devchallenge.hashphone.persistence;

import java.util.Collections;
import java.util.List;

import reactor.core.publisher.Mono;

public class PersistentServiceMock implements PersistenceService {

    @Override
    public Mono<HashedPhone> save(HashedPhone hashedPhone, String hashValue, String migrationId) {
        hashedPhone.setHashValue(hashedPhone.getNewHashValue());
        hashedPhone.setNewHashValue(hashValue);
        hashedPhone.setMigrationId(migrationId);
        return Mono.just(hashedPhone);
    }

    @Override
    public Mono<HashedPhone> findHashByPhoneNumber(String phoneNumber) {
        return Mono.empty();
    }

    @Override
    public Mono<HashedPhone> findPhoneNumberByHash(String hashValue) {
        return Mono.empty();
    }

    @Override
    public Mono<HashedPhone> findPhoneNumberByNewHash(String hashValue) {
        return Mono.empty();
    }

    @Override
    public Mono<Boolean> hashValueNotExist(String hashValue) {
        return Mono.just(true);
    }

    @Override
    public List<HashedPhone> findNonMigrated(String migrationId) {
        return Collections.emptyList();
    }
}
