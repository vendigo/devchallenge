package it.devchallenge.hashphone;

import java.time.LocalDateTime;
import java.time.Month;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import it.devchallenge.hashphone.persistence.Migration;
import it.devchallenge.hashphone.persistence.MigrationRepository;
import it.devchallenge.hashphone.utils.FileUtils;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MigrationControllerIntTest {

    private static final String CREATE_URL = "/migration";
    private static final String CREATE_MIGRATION_REQUEST = FileUtils.readSystemResource("migration/migrationRequest.json");

    @Value("${spring.security.user.name}")
    private String username;
    @Value("${spring.security.user.password}")
    private String password;
    @LocalServerPort
    private int localServerPort;
    @Autowired
    private MigrationRepository migrationRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = localServerPort;
        migrationRepository.deleteAll();
    }

    @Test
    void createMigration() {
        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .auth()
            .basic(username, password)
            .body(CREATE_MIGRATION_REQUEST)
            .when()
            .post(CREATE_URL)
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    @Test
    void createMigrationFailsOnOverlap() {
        LocalDateTime startDate = LocalDateTime.of(2019, Month.APRIL, 21, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2019, Month.APRIL, 28, 0, 0);
        migrationRepository.save(new Migration(startDate, endDate, "SHA-1", "salt"));

        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .auth()
            .basic(username, password)
            .body(CREATE_MIGRATION_REQUEST)
            .when()
            .post(CREATE_URL)
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body(Matchers.containsString("Migration overlaps with existing one"));
    }
}
