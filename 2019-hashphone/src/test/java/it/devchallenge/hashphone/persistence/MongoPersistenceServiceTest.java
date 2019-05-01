package it.devchallenge.hashphone.persistence;


import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class MongoPersistenceServiceTest {

    private static final String PHONE_NUMBER = "380123456789";
    private static final String HASHVALUE = "hashvalue";
    private static final HashedPhone HASHED_PHONE = new HashedPhone(HASHVALUE, PHONE_NUMBER);

    @Mock
    private HashedPhoneRepository hashedPhoneRepository;
    @InjectMocks
    private MongoPersistenceService persistenceService;

    @Test
    void hashValueExist() {
        when(hashedPhoneRepository.findByNewHashValue(any(String.class)))
            .thenReturn(Mono.just(HASHED_PHONE));

        Boolean notExist = persistenceService.hashValueNotExist(HASHVALUE).block();

        assertThat(notExist).isFalse();
    }

    @Test
    void hashValueNotExist() {
        when(hashedPhoneRepository.findByNewHashValue(any(String.class)))
            .thenReturn(Mono.empty());

        Boolean notExist = persistenceService.hashValueNotExist(HASHVALUE).block();

        assertThat(notExist).isTrue();
    }
}
