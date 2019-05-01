package it.devchallenge.hashphone.persistence;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class MongoPersistenceService implements PersistenceService {

    private final HashedPhoneRepository hashedPhoneRepository;

    public Mono<HashedPhone> save(HashedPhone hashedPhone, String hashValue, String migrationId) {
        hashedPhone.setHashValue(hashedPhone.getNewHashValue());
        hashedPhone.setNewHashValue(hashValue);
        hashedPhone.setMigrationId(migrationId);
        return hashedPhoneRepository.save(hashedPhone);
    }

    public Mono<HashedPhone> findHashByPhoneNumber(String phoneNumber) {
        return hashedPhoneRepository.findByPhoneNumber(phoneNumber);
    }

    public Mono<HashedPhone> findPhoneNumberByHash(String hashValue) {
        return hashedPhoneRepository.findByHashValue(hashValue);
    }

    @Override
    public Mono<HashedPhone> findPhoneNumberByNewHash(String hashValue) {
        return hashedPhoneRepository.findByNewHashValue(hashValue);
    }

    public Mono<Boolean> hashValueNotExist(String hashValue) {
        return hashedPhoneRepository.findByNewHashValue(hashValue)
            .map($ -> Boolean.FALSE)
            .defaultIfEmpty(Boolean.TRUE);
    }

    @Override
    public List<HashedPhone> findNonMigrated(String migrationId) {
        return hashedPhoneRepository.findTop1000ByMigrationIdNot(migrationId);
    }
}
