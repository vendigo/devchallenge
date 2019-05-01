package it.devchallenge.hashphone.service;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import org.junit.Ignore;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.devchallenge.hashphone.persistence.PersistentServiceMock;

@ExtendWith(MockitoExtension.class)
public class HashingServiceTest {

    private static final String SHA1 = "SHA-1";
    private static final String SHA2 = "SHA-2";
    private static final String SHA3 = "SHA-3";
    private static final String SALT = "someSalt";
    private static final String PHONE_NUMBER = "380123456789";

    @Mock
    private HashContextService hashContextService;

    static Stream<Arguments> hashPhoneNumberParams() {
        return Stream.of(
            Arguments.of(SHA1, PHONE_NUMBER, "724f3365aca4b4c23003955cd1715edc248e43f3"),
            Arguments.of(SHA2, PHONE_NUMBER, "7dddf7c96d23b167cca14060a73782e2fae82549b6a0c55ec259ccabc3c3de03"),
            Arguments.of(SHA3, PHONE_NUMBER,
                "774e617a4f6edf172db75dc33d0c8bda8de9189a67342465b7fe783bac269cbcf16897df7b20a595a90f6693818b4cd5")
        );
    }

    @ParameterizedTest
    @MethodSource("hashPhoneNumberParams")
    @Ignore
    void hashPhoneNumber(String algo, String phoneNumber, String expectedHash) {
        when(hashContextService.getHashingParams()).thenReturn(new HashingParams(algo, SALT, null));
        HashingService hashingService = new HashingService(new PersistentServiceMock(), hashContextService);

        String hashValue = hashingService.hashPhoneNumber(phoneNumber).block();

        assertThat(hashValue).isEqualTo(expectedHash);
    }
}
