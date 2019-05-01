package it.devchallenge.sharepassword.service;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import javax.crypto.SecretKey;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import it.devchallenge.sharepassword.model.FetchHistoryEntity;
import it.devchallenge.sharepassword.model.FetchPasswordResponse;
import it.devchallenge.sharepassword.model.FetchStatus;
import it.devchallenge.sharepassword.model.ParsedFetchRequest;
import it.devchallenge.sharepassword.model.SharePasswordRequest;
import it.devchallenge.sharepassword.model.SharedPasswordEntity;
import it.devchallenge.sharepassword.repository.FetchHistoryRepository;
import it.devchallenge.sharepassword.repository.SharedPasswordRepository;

@RunWith(MockitoJUnitRunner.class)
public class SharedPasswordServiceTest {

    private static final String SECRET_AS_STRING = "SVGxfZeoK4ZR8WxWjOCVFQ==";
    private static final String PASSWORD = "myPassword";
    private static final String ENCRYPTED_PASSWORD = "3wovLB+X5s3LX2ufy/nr4g==";
    private static final String ENCODED_LINK = "MS52ZXJpZmljYXRpb24uc2VjcmV0";
    private static final long PASSWORD_ID = 1L;
    private static final String VERIFICATION = "verification";
    private static final ParsedFetchRequest REQUEST = new ParsedFetchRequest(PASSWORD_ID, VERIFICATION,
            SECRET_AS_STRING);
    private static final LocalDateTime NOW = LocalDate.of(2018, Month.OCTOBER, 12).atStartOfDay();

    @Mock
    private SharedPasswordRepository sharedPasswordRepository;
    @Mock
    private FetchHistoryRepository fetchHistoryRepository;
    @Mock
    private LinkConverterService linkConverterService;
    @Mock
    private AesEncryptionService aesEncryptionService;
    @Mock
    private SecretKey secretKey;
    @Mock
    private ClockService clockService;
    @InjectMocks
    private SharedPasswordService sharedPasswordService;

    @Before
    public void setUp() {
        when(clockService.now()).thenReturn(NOW);
    }

    @Test
    public void sharePassword() {
        SharePasswordRequest request = new SharePasswordRequest(singletonList(PASSWORD), 60L);
        when(aesEncryptionService.generateSecretKey()).thenReturn(secretKey);
        when(aesEncryptionService.keyToString(secretKey)).thenReturn(SECRET_AS_STRING);
        when(aesEncryptionService.encrypt(PASSWORD, secretKey)).thenReturn(ENCRYPTED_PASSWORD);
        when(sharedPasswordRepository.save(any())).thenReturn(createdSharedPassword());
        when(linkConverterService.buildLink(eq(1L), anyString(), eq(SECRET_AS_STRING))).thenReturn(ENCODED_LINK);

        String link = sharedPasswordService.sharePassword(request);
        assertThat(link, equalTo(ENCODED_LINK));
    }

    @Test
    public void fetchPasswordWrongFormat() {
        when(linkConverterService.parseLink(ENCODED_LINK)).thenThrow(new IllegalArgumentException("Wrong format"));
        FetchPasswordResponse response = sharedPasswordService.fetchPassword(ENCODED_LINK);
        assertThat(response.getFetchStatus(), equalTo(FetchStatus.WRONG_FORMAT));
        verify(fetchHistoryRepository).save(any(FetchHistoryEntity.class));
    }

    @Test
    public void fetchPasswordNotExistentId() {
        when(linkConverterService.parseLink(ENCODED_LINK)).thenReturn(REQUEST);
        when(sharedPasswordRepository.getOne(PASSWORD_ID)).thenReturn(null);
        FetchPasswordResponse response = sharedPasswordService.fetchPassword(ENCODED_LINK);
        assertThat(response.getFetchStatus(), equalTo(FetchStatus.NOT_EXISTENT_ID));
        verify(fetchHistoryRepository).save(any(FetchHistoryEntity.class));
    }

    @Test
    public void fetchPasswordVerificationFailed() {
        when(linkConverterService.parseLink(ENCODED_LINK)).thenReturn(REQUEST);
        SharedPasswordEntity sharedPassword = createdSharedPassword();
        sharedPassword.setVerificationCode("WrongCode");
        when(sharedPasswordRepository.getOne(PASSWORD_ID)).thenReturn(sharedPassword);
        FetchPasswordResponse response = sharedPasswordService.fetchPassword(ENCODED_LINK);
        assertThat(response.getFetchStatus(), equalTo(FetchStatus.VERIFICATION_FAILED));
        verify(fetchHistoryRepository).save(any(FetchHistoryEntity.class));
    }

    @Test
    public void fetchPasswordAlreadyUsed() {
        when(linkConverterService.parseLink(ENCODED_LINK)).thenReturn(REQUEST);
        SharedPasswordEntity sharedPassword = createdSharedPassword();
        sharedPassword.setUsed(true);
        when(sharedPasswordRepository.getOne(PASSWORD_ID)).thenReturn(sharedPassword);
        FetchPasswordResponse response = sharedPasswordService.fetchPassword(ENCODED_LINK);
        assertThat(response.getFetchStatus(), equalTo(FetchStatus.ALREADY_USED));
        verify(fetchHistoryRepository).save(any(FetchHistoryEntity.class));
    }

    @Test
    public void fetchPasswordExpired() {
        when(linkConverterService.parseLink(ENCODED_LINK)).thenReturn(REQUEST);
        SharedPasswordEntity sharedPassword = createdSharedPassword();
        sharedPassword.setExpirationDate(NOW.minusMinutes(2));
        when(sharedPasswordRepository.getOne(PASSWORD_ID)).thenReturn(sharedPassword);
        FetchPasswordResponse response = sharedPasswordService.fetchPassword(ENCODED_LINK);
        assertThat(response.getFetchStatus(), equalTo(FetchStatus.EXPIRED));
        verify(fetchHistoryRepository).save(any(FetchHistoryEntity.class));
    }

    @Test
    public void fetchPasswordSuccess() {
        when(linkConverterService.parseLink(ENCODED_LINK)).thenReturn(REQUEST);
        SharedPasswordEntity sharedPassword = createdSharedPassword();
        when(sharedPasswordRepository.getOne(PASSWORD_ID)).thenReturn(sharedPassword);
        when(aesEncryptionService.decrypt(ENCRYPTED_PASSWORD, SECRET_AS_STRING)).thenReturn(PASSWORD);

        FetchPasswordResponse response = sharedPasswordService.fetchPassword(ENCODED_LINK);
        assertThat(response.getFetchStatus(), equalTo(FetchStatus.SUCCESS));
        assertThat(response.getPasswords(), equalTo(singletonList(PASSWORD)));
        verify(fetchHistoryRepository).save(any(FetchHistoryEntity.class));
    }

    private SharedPasswordEntity createdSharedPassword() {
        SharedPasswordEntity sharedPasswordEntity = new SharedPasswordEntity();
        sharedPasswordEntity.setId(1L);
        sharedPasswordEntity.setExpirationDate(NOW.plusHours(1));
        sharedPasswordEntity.setVerificationCode(VERIFICATION);
        sharedPasswordEntity.setEncryptedPassword(ENCRYPTED_PASSWORD);
        return sharedPasswordEntity;
    }

}
