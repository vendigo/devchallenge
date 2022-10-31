package it.devchallenge.minescanner;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static it.devchallenge.minescanner.TestUtils.assertJson;
import static it.devchallenge.minescanner.TestUtils.readSystemResource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MineScannerControllerIntTest {

    @Value("${local.server.port}")
    void initRestAssured(int localPort) {
        RestAssured.port = localPort;
    }

    static Stream<Arguments> scanMinesArgs() {
        return Stream.of(
                Arguments.of("data/invalidNoPrefixRequest.json", "data/invalidNoPrefixResponse.json", HttpStatus.UNPROCESSABLE_ENTITY),
                Arguments.of("data/invalidColorSchemeRequest.json", "data/invalidColorSchemeResponse.json", HttpStatus.UNPROCESSABLE_ENTITY),
                Arguments.of("data/invalidGridRequest.json", "data/invalidGridResponse.json", HttpStatus.UNPROCESSABLE_ENTITY),
                Arguments.of("data/invalidNotImageRequest.json", "data/invalidNotImageResponse.json", HttpStatus.UNPROCESSABLE_ENTITY),
                Arguments.of("data/blackGridRequest.json", "data/blackGridResponse.json", HttpStatus.OK),
                Arguments.of("data/simpleGridRequest.json", "data/simpleGridResponse.json", HttpStatus.OK),
                Arguments.of("data/bigCellRequest.json", "data/bigCellResponse.json", HttpStatus.OK)
        );
    }

    @ParameterizedTest
    @MethodSource("scanMinesArgs")
    void scanMines(String requestPath, String expectedResponsePath, HttpStatus expectedStatus) {
        String actualResponse = given()
                .contentType(ContentType.JSON)
                .body(readSystemResource(requestPath))
                .when()
                .post("/api/image-input")
                .then()
                .statusCode(expectedStatus.value())
                .extract()
                .body()
                .asString();
        assertJson(readSystemResource(expectedResponsePath), actualResponse);
    }
}
