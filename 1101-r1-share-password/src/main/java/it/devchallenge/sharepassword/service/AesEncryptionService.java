package it.devchallenge.sharepassword.service;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import lombok.SneakyThrows;

@Service
public class AesEncryptionService {
    private static final String ALGORITHM = "AES";
    private static final int BLOCK_SIZE = 128;

    @SneakyThrows
    String encrypt(String plainText, SecretKey secretKey) {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        byte[] plainTextByte = plainText.getBytes();
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedByte = cipher.doFinal(plainTextByte);
        return Base64.getEncoder().encodeToString(encryptedByte);
    }

    @SneakyThrows
    String decrypt(String encryptedText, String secretAsString) {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        byte[] encryptedTextByte = Base64.getDecoder().decode(encryptedText);
        SecretKey secretKey = keyFromString(secretAsString);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
        return new String(decryptedByte);
    }

    SecretKey keyFromString(String secretAsString) {
        byte[] keyBytes = Base64.getDecoder().decode(secretAsString);
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, ALGORITHM);
    }

    String keyToString(SecretKey secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    @SneakyThrows
    SecretKey generateSecretKey() {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(BLOCK_SIZE);
        return keyGenerator.generateKey();
    }
}
