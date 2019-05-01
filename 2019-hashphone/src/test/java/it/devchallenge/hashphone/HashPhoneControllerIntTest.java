package it.devchallenge.hashphone;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.Month;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.restassured.RestAssured;
import it.devchallenge.hashphone.persistence.HashedPhone;
import it.devchallenge.hashphone.persistence.HashedPhoneRepository;
import it.devchallenge.hashphone.persistence.Migration;
import it.devchallenge.hashphone.persistence.MigrationRepository;
import it.devchallenge.hashphone.service.ClockService;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HashPhoneControllerIntTest {

    private static final String PHONE_NUMBER_1 = "380931234567";
    private static final String PHONE_NUMBER_1_HASH = "2872f653ae33e60276d9d094fe9efadffd852562";
    private static final String PHONE_NUMBER_1_ANOTHER_HASH = "04ec422bf36dfb21348c9e72b5bef94ccba1d02d03a5fc0d7e84549b76d4a3bc";
    private static final String EXISTED_HASH = "someHash";
    private static final String PHONE_NUMBER_2 = "380937654321";
    private static final String FIND_URL = "/find";
    private static final String HASH_URL = "/hash";
    private static final String CONVERT_URL = "/convert";
    private static final String HASH_VALUE_PARAM = "hashValue";
    private static final String HASHING_ALGO = "SHA-1";
    private static final String ANOTHER_ALGO = "SHA-2";
    private static final String HASHING_SALT = "someSalt";
    private static final String ANOTHER_SALT = "anotherSalt";
    private static final LocalDateTime NOW_DATETIME = LocalDateTime.of(2019, Month.APRIL, 20, 14, 0);

    @Value("${spring.security.user.name}")
    private String username;
    @Value("${spring.security.user.password}")
    private String password;
    @LocalServerPort
    private int localServerPort;
    @Autowired
    private HashedPhoneRepository hashedPhoneRepository;
    @Autowired
    private MigrationRepository migrationRepository;
    @MockBean
    private ClockService clockService;

    @BeforeEach
    void setUp() {
        RestAssured.port = localServerPort;
        hashedPhoneRepository.deleteAll().block();
        migrationRepository.deleteAll();
        when(clockService.now()).thenReturn(NOW_DATETIME);
    }

    @Test
    void hashPhone() {
        RestAssured
            .given()
            .auth()
            .basic(username, password)
            .body(PHONE_NUMBER_1)
            .when()
            .post(HASH_URL)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(equalTo(PHONE_NUMBER_1_HASH));
    }

    @Test
    void hashPhoneUsesParamsFromLastMigration() {
        migrationRepository.save(new Migration(NOW_DATETIME.minusDays(2L), NOW_DATETIME.minusDays(1L),
            ANOTHER_ALGO, ANOTHER_SALT));

        RestAssured
            .given()
            .auth()
            .basic(username, password)
            .body(PHONE_NUMBER_1)
            .when()
            .post(HASH_URL)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(equalTo(PHONE_NUMBER_1_ANOTHER_HASH));
    }

    @Test
    void hashPhoneReturnsExistedValue() {
        hashedPhoneRepository.save(new HashedPhone(EXISTED_HASH, PHONE_NUMBER_1)).block();

        RestAssured
            .given()
            .auth()
            .basic(username, password)
            .body(PHONE_NUMBER_1)
            .when()
            .post(HASH_URL)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(equalTo(EXISTED_HASH));
    }

    @Test
    void hashPhoneReturnsExistingMigratedValue() {
        Migration migration = migrationRepository.save(new Migration(NOW_DATETIME.minusDays(3L), NOW_DATETIME.minusDays(2L),
            HASHING_ALGO, HASHING_SALT));
        hashedPhoneRepository.save(new HashedPhone(PHONE_NUMBER_1_ANOTHER_HASH, EXISTED_HASH, PHONE_NUMBER_1, migration.getId())).block();

        RestAssured
            .given()
            .auth()
            .basic(username, password)
            .body(PHONE_NUMBER_1)
            .when()
            .post(HASH_URL)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(equalTo(EXISTED_HASH));
    }

    @Test
    void hashPhoneCauseMigration() {
        hashedPhoneRepository.save(new HashedPhone(EXISTED_HASH, PHONE_NUMBER_1)).block();
        migrationRepository.save(new Migration(NOW_DATETIME.minusHours(1L), NOW_DATETIME.plusDays(1L),
            HASHING_ALGO, HASHING_SALT));

        RestAssured
            .given()
            .auth()
            .basic(username, password)
            .body(PHONE_NUMBER_1)
            .when()
            .post(HASH_URL)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(equalTo(PHONE_NUMBER_1_HASH));
    }

    @Test
    void convertHash() {
        hashedPhoneRepository.save(new HashedPhone(EXISTED_HASH, PHONE_NUMBER_1)).block();

        migrationRepository.save(new Migration(NOW_DATETIME.minusHours(1L), NOW_DATETIME.plusDays(1L),
            HASHING_ALGO, HASHING_SALT));

        RestAssured
            .given()
            .auth()
            .basic(username, password)
            .body(EXISTED_HASH)
            .when()
            .post(CONVERT_URL)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(equalTo(PHONE_NUMBER_1_HASH));
    }

    @Test
    void convertHashOnMigrated() {
        String migrationId = migrationRepository.save(new Migration(NOW_DATETIME.minusHours(1L), NOW_DATETIME.plusDays(1L),
            HASHING_ALGO, HASHING_SALT)).getId();
        HashedPhone hashedPhone = new HashedPhone(EXISTED_HASH, PHONE_NUMBER_1_HASH, PHONE_NUMBER_1, migrationId);
        hashedPhoneRepository.save(hashedPhone).block();

        RestAssured
            .given()
            .auth()
            .basic(username, password)
            .body(EXISTED_HASH)
            .when()
            .post(CONVERT_URL)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(equalTo(PHONE_NUMBER_1_HASH));
    }

    @Test
    void convertFailsWithoutMigration() {
        RestAssured
            .given()
            .auth()
            .basic(username, password)
            .body(EXISTED_HASH)
            .when()
            .post(CONVERT_URL)
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body(containsString("Migration is not running, /convert endpoint is disabled"));
    }

    @Test
    void phoneHashCollisionAvoided() {
        hashedPhoneRepository.save(new HashedPhone(PHONE_NUMBER_1_HASH, PHONE_NUMBER_2)).block();

        RestAssured
            .given()
            .auth()
            .basic(username, password)
            .body(PHONE_NUMBER_1)
            .when()
            .post(HASH_URL)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(not(equalTo(PHONE_NUMBER_1_HASH)));
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "490931234567", "38068123456", "3806812345678", "38068123d456" })
    void hashInvalidPhone(String phoneNumber) {
        RestAssured
            .given()
            .auth()
            .basic(username, password)
            .body(phoneNumber)
            .when()
            .post(HASH_URL)
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void endpointIsSecured() {
        RestAssured
            .given()
            .auth()
            .basic(username, "wrongPassword")
            .body(PHONE_NUMBER_1)
            .when()
            .post(HASH_URL)
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void findPhone() {
        hashedPhoneRepository.save(new HashedPhone(EXISTED_HASH, PHONE_NUMBER_1)).block();

        RestAssured
            .given()
            .auth()
            .basic(username, password)
            .param(HASH_VALUE_PARAM, EXISTED_HASH)
            .when()
            .get(FIND_URL)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(equalTo(PHONE_NUMBER_1));
    }

    @Test
    void findPhoneAfterMigration() {
        Migration migration = migrationRepository.save(new Migration(NOW_DATETIME.minusDays(3L), NOW_DATETIME.minusDays(2L),
            HASHING_ALGO, HASHING_SALT));
        hashedPhoneRepository.save(new HashedPhone(PHONE_NUMBER_1_ANOTHER_HASH, EXISTED_HASH, PHONE_NUMBER_1, migration.getId())).block();

        RestAssured
            .given()
            .auth()
            .basic(username, password)
            .param(HASH_VALUE_PARAM, EXISTED_HASH)
            .when()
            .get(FIND_URL)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(equalTo(PHONE_NUMBER_1));
    }

    @Test
    void findPhoneDuringMigrationByNewHash() {
        String migrationId = migrationRepository.save(new Migration(NOW_DATETIME.minusHours(1L), NOW_DATETIME.plusDays(1L), HASHING_ALGO, HASHING_SALT)).getId();
        hashedPhoneRepository.save(new HashedPhone(PHONE_NUMBER_1_ANOTHER_HASH, EXISTED_HASH, PHONE_NUMBER_1, migrationId)).block();

        RestAssured
            .given()
            .auth()
            .basic(username, password)
            .param(HASH_VALUE_PARAM, EXISTED_HASH)
            .when()
            .get(FIND_URL)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(equalTo(PHONE_NUMBER_1));
    }

    @Test
    void findPhoneDuringMigrationByOldHash() {
        String migrationId = migrationRepository.save(new Migration(NOW_DATETIME.minusHours(1L), NOW_DATETIME.plusDays(1L), HASHING_ALGO, HASHING_SALT)).getId();
        hashedPhoneRepository.save(new HashedPhone(EXISTED_HASH, PHONE_NUMBER_1_ANOTHER_HASH, PHONE_NUMBER_1, migrationId)).block();

        RestAssured
            .given()
            .auth()
            .basic(username, password)
            .param(HASH_VALUE_PARAM, EXISTED_HASH)
            .when()
            .get(FIND_URL)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(equalTo(PHONE_NUMBER_1));
    }

    @Test
    void phoneNotFound() {
        hashedPhoneRepository.save(new HashedPhone(EXISTED_HASH, PHONE_NUMBER_1)).block();

        RestAssured
            .given()
            .auth()
            .basic(username, password)
            .param(HASH_VALUE_PARAM, PHONE_NUMBER_1_HASH)
            .when()
            .get(FIND_URL)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(isEmptyString());
    }
}
