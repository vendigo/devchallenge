package it.devchallenge.sharepassword.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import javax.crypto.SecretKey;

import org.junit.Test;

public class AesEncryptionServiceTest {

    private static final String ENCRYPTED_TEXT = "3wovLB+X5s3LX2ufy/nr4g==";
    private static final String ORIGINAL_TEXT = "Something other";
    private static String SECRET_AS_STRING = "SVGxfZeoK4ZR8WxWjOCVFQ==";
    private final AesEncryptionService encryptionService = new AesEncryptionService();

    @Test
    public void generateSecretKey() {
        SecretKey secretKey = encryptionService.generateSecretKey();
        assertThat(secretKey, notNullValue());
    }

    @Test
    public void keyToString() {
        SecretKey secretKey = encryptionService.generateSecretKey();
        String keyAsString = encryptionService.keyToString(secretKey);
        assertThat(keyAsString, not(isEmptyString()));
    }

    @Test
    public void keyFromString() {
        SecretKey secretKey = encryptionService.keyFromString(SECRET_AS_STRING);
        assertThat(secretKey, notNullValue());
    }

    @Test
    public void encrypt() {
        SecretKey secretKey = encryptionService.keyFromString(SECRET_AS_STRING);
        String encrypted = encryptionService.encrypt(ORIGINAL_TEXT, secretKey);
        assertThat(encrypted, equalTo(ENCRYPTED_TEXT));
    }

    @Test
    public void decrypt() {
        String decrypted = encryptionService.decrypt(ENCRYPTED_TEXT, SECRET_AS_STRING);
        assertThat(decrypted, equalTo(ORIGINAL_TEXT));
    }
}
