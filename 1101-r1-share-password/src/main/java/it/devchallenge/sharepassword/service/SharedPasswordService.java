package it.devchallenge.sharepassword.service;

import static it.devchallenge.sharepassword.model.FetchPasswordResponse.error;
import static it.devchallenge.sharepassword.model.FetchPasswordResponse.success;
import static it.devchallenge.sharepassword.model.FetchStatus.ALREADY_USED;
import static it.devchallenge.sharepassword.model.FetchStatus.EXPIRED;
import static it.devchallenge.sharepassword.model.FetchStatus.NOT_EXISTENT_ID;
import static it.devchallenge.sharepassword.model.FetchStatus.VERIFICATION_FAILED;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import it.devchallenge.sharepassword.model.FetchHistoryEntity;
import it.devchallenge.sharepassword.model.FetchPasswordResponse;
import it.devchallenge.sharepassword.model.FetchStatus;
import it.devchallenge.sharepassword.model.ParsedFetchRequest;
import it.devchallenge.sharepassword.model.SharePasswordRequest;
import it.devchallenge.sharepassword.model.SharedPasswordEntity;
import it.devchallenge.sharepassword.repository.FetchHistoryRepository;
import it.devchallenge.sharepassword.repository.SharedPasswordRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@Service
@AllArgsConstructor
public class SharedPasswordService {

    private static final int VERIFICATION_CODE_LENGTH = 10;
    private static final long DEFAULT_EXPIRATION_PERIOD = 180;
    private static final String PASSWORDS_SEPARATOR = "\n";
    private static final String PASSWORDS_SEPARATOR_REGEX = "[\\n]";

    private final SharedPasswordRepository sharedPasswordRepository;
    private final FetchHistoryRepository fetchHistoryRepository;
    private final LinkConverterService linkConverterService;
    private final AesEncryptionService aesEncryptionService;
    private final ClockService clockService;

    @SneakyThrows
    public String sharePassword(SharePasswordRequest request) {
        SecretKey secretKey = aesEncryptionService.generateSecretKey();
        String keyAsString = aesEncryptionService.keyToString(secretKey);
        String verificationCode = randomAlphanumeric(VERIFICATION_CODE_LENGTH);
        String passwords = request.getPasswords().stream()
                .collect(joining(PASSWORDS_SEPARATOR));
        String encryptedPassword = aesEncryptionService.encrypt(passwords, secretKey);
        SharedPasswordEntity sharedPassword = createEntity(verificationCode, encryptedPassword,
                request.getExpireInMinutes());
        return linkConverterService.buildLink(sharedPassword.getId(), verificationCode, keyAsString);
    }

    public FetchPasswordResponse fetchPassword(String linkBody) {
        try {
            ParsedFetchRequest request = linkConverterService.parseLink(linkBody);
            SharedPasswordEntity sharedPassword = sharedPasswordRepository.getOne(request.getPasswordId());

            //noinspection ConstantConditions
            if (sharedPassword == null) {
                return saveStatus(error(NOT_EXISTENT_ID), null);
            }

            if (!sharedPassword.getVerificationCode().equals(request.getVerificationCode())) {
                return saveStatus(error(VERIFICATION_FAILED), sharedPassword);
            }

            if (sharedPassword.isUsed()) {
                return saveStatus(error(ALREADY_USED), sharedPassword);
            }

            LocalDateTime now = clockService.now();
            if (sharedPassword.getExpirationDate().isBefore(now)) {
                return saveStatus(error(EXPIRED), sharedPassword);
            }

            return processSuccess(request, sharedPassword);
        } catch (Exception ex) {
            ex.printStackTrace();
            return saveStatus(error(FetchStatus.WRONG_FORMAT), null);
        }
    }

    private FetchPasswordResponse processSuccess(ParsedFetchRequest request, SharedPasswordEntity sharedPassword) {
        String decryptedPasswords = aesEncryptionService.decrypt(sharedPassword.getEncryptedPassword(),
                request.getSecretKey());
        List<String> passwords = Stream.of(decryptedPasswords.split(PASSWORDS_SEPARATOR_REGEX))
                .collect(toList());
        sharedPassword.setUsed(true);
        sharedPasswordRepository.save(sharedPassword);
        return saveStatus(success(passwords), sharedPassword);
    }

    private FetchPasswordResponse saveStatus(FetchPasswordResponse response, SharedPasswordEntity sharedPassword) {
        FetchHistoryEntity fetchHistory = new FetchHistoryEntity();
        fetchHistory.setFetchStatus(response.getFetchStatus());
        Optional.ofNullable(sharedPassword)
                .map(SharedPasswordEntity::getId)
                .ifPresent(fetchHistory::setPasswordId);
        fetchHistory.setCreatedDate(clockService.now());
        fetchHistoryRepository.save(fetchHistory);
        return response;
    }

    private SharedPasswordEntity createEntity(String verificationCode, String encryptedPassword, Long expireInMinutes) {
        long expirationPeriod = defaultIfNull(expireInMinutes, DEFAULT_EXPIRATION_PERIOD);
        LocalDateTime creationDate = clockService.now();
        LocalDateTime expirationDate = creationDate.plusMinutes(expirationPeriod);

        SharedPasswordEntity sharedPassword = new SharedPasswordEntity(encryptedPassword, verificationCode,
                creationDate, expirationDate);
        return sharedPasswordRepository.save(sharedPassword);
    }
}
