package it.devchallenge.hashphone.service;

import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;

import com.google.common.hash.HashFunction;

import it.devchallenge.hashphone.persistence.HashedPhone;
import it.devchallenge.hashphone.persistence.PersistenceService;
import it.devchallenge.hashphone.validation.ValidationException;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@SuppressWarnings("UnstableApiUsage")
@Service
@Slf4j
@AllArgsConstructor
public class HashingService {

    private static final String PHONE_NUMBER_PATTERN = "380\\d{9}";
    private static final int RANDOM_SALT_LENGTH = 32;
    private static final int COLLISION_RETRY_ATTEMPTS = 10;
    private static final String CONVERT_IS_DISABLED_ERROR = "Migration is not running, /convert endpoint is disabled";

    private final PersistenceService persistenceService;
    private final HashContextService hashContextService;

    public Mono<String> hashPhoneNumber(String phoneNumber) {
        log.info("Processing phone number: {}", phoneNumber);
        Validate.matchesPattern(phoneNumber, PHONE_NUMBER_PATTERN);
        HashingParams hashingParams = hashContextService.getHashingParams();
        return persistenceService.findHashByPhoneNumber(phoneNumber)
            .doOnNext(existingHashValue -> log.debug("Hash for phone number {} is already saved in db: {}", phoneNumber,
                existingHashValue))
            .flatMap(existingHashValue -> migrateIfNeeded(existingHashValue, hashingParams))
            .switchIfEmpty(generateHash(new HashedPhone(phoneNumber), hashingParams))
            .map(HashedPhone::getNewHashValue);
    }

    private Mono<HashedPhone> migrateIfNeeded(HashedPhone existingHashValue, HashingParams hashingParams) {
        if (needToMigrate(existingHashValue, hashingParams)) {
            log.debug("Migrating hashedValue: {} and hashing params: {}", existingHashValue, hashingParams);
            return generateHash(existingHashValue, hashingParams);
        }
        log.debug("Returning existing hashedValue without migration: {}", existingHashValue);
        return Mono.just(existingHashValue);
    }

    private boolean needToMigrate(HashedPhone existingHashValue, HashingParams hashingParams) {
        return hashingParams.isMigration() &&
            !hashingParams.getMigrationId().equals(existingHashValue.getMigrationId());
    }

    private Mono<HashedPhone> generateHash(HashedPhone hashedPhone, HashingParams hashingParams) {
        HashFunction hashFunction = HashingAlgorithm.getByAlgoName(hashingParams.getAlgorithm());
        String phoneNumber = hashedPhone.getPhoneNumber();
        return generateHashMono(phoneNumber, hashingParams.getSalt(), hashFunction)
            .repeatWhenEmpty(COLLISION_RETRY_ATTEMPTS, tryNum -> repeatMono(phoneNumber, hashFunction))
            .flatMap(hashValue -> persistenceService.save(hashedPhone, hashValue, hashingParams.getMigrationId()))
            .doOnNext(hashValue -> log.debug("Phone number {} saved with hash: {}", phoneNumber, hashValue));
    }

    private Mono<String> generateHashMono(String phoneNumber, String salt, HashFunction hashFunction) {
        return Mono.fromSupplier(() -> hash(phoneNumber, salt, hashFunction))
            .filterWhen(persistenceService::hashValueNotExist);
    }

    private Mono<String> repeatMono(String phoneNumber, HashFunction hashFunction) {
        return Mono.fromSupplier(this::generateRandomSalt)
            .flatMap(randomSalt -> generateHashMono(phoneNumber, randomSalt, hashFunction));
    }

    public Mono<String> findPhoneNumber(String hashValue) {
        log.info("Searching for phoneNumber by hash: {}", hashValue);
        return persistenceService.findPhoneNumberByNewHash(hashValue)
            .switchIfEmpty(persistenceService.findPhoneNumberByHash(hashValue))
            .map(HashedPhone::getPhoneNumber)
            .doOnNext(phoneNumber -> log.debug("Found phone: {} by hash: {}", phoneNumber, hashValue));
    }

    private String generateRandomSalt() {
        String randomSalt = RandomStringUtils.randomAlphabetic(RANDOM_SALT_LENGTH);
        log.debug("Using preconfigured salt lead to collision, random salt was generated: {}", randomSalt);
        return randomSalt;
    }

    @SneakyThrows
    private String hash(String input, String salt, HashFunction hashFunction) {
        log.debug("Hashing input: {} with salt: {}", input, salt);
        return hashFunction.hashString(input + salt, StandardCharsets.UTF_8).toString();
    }

    Mono<Void> migrateOne(HashedPhone hashedPhone, HashingParams hashingParams) {
        return migrateIfNeeded(hashedPhone, hashingParams)
        .then();
    }

    public Mono<String> migrateHash(String oldHashValue) {
        HashingParams hashingParams = hashContextService.getHashingParams();

        if (!hashingParams.isMigration()) {
            throw new ValidationException(CONVERT_IS_DISABLED_ERROR);
        }

        return persistenceService.findPhoneNumberByHash(oldHashValue)
            .switchIfEmpty(persistenceService.findPhoneNumberByNewHash(oldHashValue))
            .flatMap(existingHashValue -> migrateIfNeeded(existingHashValue, hashingParams))
            .map(HashedPhone::getNewHashValue);
    }
}
