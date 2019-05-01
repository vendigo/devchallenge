package it.devchallenge.sharepassword.controller;

import static com.google.common.collect.Iterables.getOnlyElement;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsNull.notNullValue;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import it.devchallenge.sharepassword.model.SharedPasswordEntity;
import it.devchallenge.sharepassword.repository.SharedPasswordRepository;
import it.devchallenge.sharepassword.service.ClockService;
import lombok.SneakyThrows;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SharedPasswordControllerIntTest {

    private static final LocalDateTime NOW = LocalDate.of(2018, Month.OCTOBER, 12).atStartOfDay();
    private static final String PASSWORD = "mySecretPassw0rd";
    private final ClassLoader classLoader = SharedPasswordControllerIntTest.class.getClassLoader();

    @LocalServerPort
    private int port;
    @MockBean
    private ClockService clockService;
    @Autowired
    private SharedPasswordRepository sharedPasswordRepository;

    @Before
    public void setUp() {
        RestAssured.port = port;
        sharedPasswordRepository.deleteAll();
        Mockito.when(clockService.now()).thenReturn(NOW);
    }

    @Test
    public void sharePassword() {
        given()
                .body(testFile("sharePasswordRequest.json"))
                .contentType(ContentType.JSON)
                .when()
                .post("password")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(not(isEmptyOrNullString()));
        SharedPasswordEntity sharedPassword = getOnlyElement(sharedPasswordRepository.findAll());
        assertThat(sharedPassword.getEncryptedPassword(), allOf(notNullValue(),
                not(equalTo(singletonList(PASSWORD)))));
        assertThat(sharedPassword.getVerificationCode(), notNullValue());
        assertThat(sharedPassword.getCreationDate(), equalTo(NOW));
        assertThat(sharedPassword.getExpirationDate(), equalTo(NOW.plusHours(1)));
    }

    @Test
    public void shareThreePasswords() {
        given()
                .body(testFile("shareThreePasswordsRequest.json"))
                .contentType(ContentType.JSON)
                .when()
                .post("password")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(not(isEmptyOrNullString()));
        SharedPasswordEntity sharedPassword = getOnlyElement(sharedPasswordRepository.findAll());
        assertThat(sharedPassword.getEncryptedPassword(), notNullValue());
        assertThat(sharedPassword.getVerificationCode(), notNullValue());
        assertThat(sharedPassword.getCreationDate(), equalTo(NOW));
        assertThat(sharedPassword.getExpirationDate(), equalTo(NOW.plusMinutes(30)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void fetchPassword() {
        SharedPasswordEntity sharedPassword = new SharedPasswordEntity();
        sharedPassword.setId(1L);
        sharedPassword.setVerificationCode("wydJSeQq3J");
        sharedPassword.setEncryptedPassword("9XTtjRHbRDG1b1V01Xp/TvoTQBqH2V7DJKp7IrjHmFc=");
        sharedPassword.setCreationDate(NOW);
        sharedPassword.setExpirationDate(NOW.plusHours(1));
        sharedPasswordRepository.save(sharedPassword);

        when()
                .get("password/MS53eWRKU2VRcTNKLlVjd1JJN05VYkR1MnJnc2V1czNtTVE9PQ==")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("passwords", hasItem(PASSWORD))
                .body("fetchStatus", equalTo("SUCCESS"));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void fetchThreePasswords() {
        SharedPasswordEntity sharedPassword = new SharedPasswordEntity();
        sharedPassword.setId(1L);
        sharedPassword.setVerificationCode("RsjKIJtDvE");
        sharedPassword.setEncryptedPassword("GWcNV+bkjPT/Bzg1G+R92B8N02Zg8zNYAB+CitM9ChyhJoosvlb0ldukKjFWBSco");
        sharedPassword.setCreationDate(NOW);
        sharedPassword.setExpirationDate(NOW.plusMinutes(30));
        sharedPasswordRepository.save(sharedPassword);

        when()
                .get("password/MS5Sc2pLSUp0RHZFLnNIQU82UGg1bHVDcW41MFdrMWpHaXc9PQ==")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("passwords", hasItems(PASSWORD, "anotherPass", "qwerty123"))
                .body("fetchStatus", equalTo("SUCCESS"));
    }

    @Test
    public void fetchPasswordWithWrongLink() {
        when()
                .get("password/somethingWrong")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("password", nullValue())
                .body("fetchStatus", equalTo("WRONG_FORMAT"));
    }


    @SneakyThrows
    private String testFile(String fileName) {
        return IOUtils.toString(classLoader.getResourceAsStream(fileName), Charset.defaultCharset());
    }
}
