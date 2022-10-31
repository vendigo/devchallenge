package it.devchallenge.trustnetwork.controller;

import io.restassured.http.ContentType;
import it.devchallenge.trustnetwork.AbstractIntTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static it.devchallenge.trustnetwork.TestUtils.assertJson;
import static it.devchallenge.trustnetwork.TestUtils.readSystemResource;
import static it.devchallenge.trustnetwork.service.ValidationService.*;

@DirtiesContext
class MessageControllerIntTest extends AbstractIntTest {

    @BeforeEach
    void setUp() {
        execute("MATCH p=(a)--(b) DELETE p;");
        execute("MATCH (a) DELETE a;");
        setUpGraph();
    }

    static Stream<Arguments> invalidRequestsArgs() {
        return Stream.of(
                Arguments.of("/api/messages", "data/requestNotExistentPerson.json", PERSON_NOT_FOUND),
                Arguments.of("/api/messages", "data/requestNotValidTrustLevel.json", INVALID_TRUST_LEVEL),
                Arguments.of("/api/messages", "data/requestTopicsNull.json", ALL_FIELDS_REQUIRED),
                Arguments.of("/api/path", "data/requestNotExistentPerson.json", PERSON_NOT_FOUND),
                Arguments.of("/api/path", "data/requestNotValidTrustLevel.json", INVALID_TRUST_LEVEL),
                Arguments.of("/api/path", "data/requestTopicsNull.json", ALL_FIELDS_REQUIRED)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidRequestsArgs")
    void invalidRequests(String endpoint, String requestPath, String expectedError) {
        given()
                .contentType(ContentType.JSON)
                .body(readSystemResource(requestPath))
                .when()
                .post(endpoint)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Matchers.equalTo(expectedError));
    }

    static Stream<Arguments> messagesArgs() {
        return Stream.of(
                Arguments.of("data/messageRequest1.json", "data/expectedMessageResponse1.json"),
                Arguments.of("data/messageRequest2.json", "data/expectedMessageResponse2.json"),
                Arguments.of("data/messageRequest3.json", "data/expectedMessageResponse3.json"),
                Arguments.of("data/messageRequest4.json", "data/expectedMessageResponse4.json")
        );
    }

    @ParameterizedTest
    @MethodSource("messagesArgs")
    void messages(String requestPath, String expectedResponsePath) {
        String actualResponse = given()
                .contentType(ContentType.JSON)
                .body(readSystemResource(requestPath))
                .when()
                .post("/api/messages")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .body()
                .asString();
        assertJson(readSystemResource(expectedResponsePath), actualResponse);
    }

    static Stream<Arguments> pathArgs() {
        return Stream.of(
                Arguments.of("data/messageRequest1.json", "data/expectedPathResponse1.json"),
                Arguments.of("data/messageRequest4.json", "data/expectedPathResponse2.json"),
                Arguments.of("data/messageRequest5.json", "data/expectedPathResponse3.json"),
                Arguments.of("data/messageRequest6.json", "data/expectedPathResponse4.json"),
                Arguments.of("data/messageRequest7.json", "data/expectedPathResponse5.json"),
                Arguments.of("data/messageRequest8.json", "data/expectedPathResponse6.json")
        );
    }

    @ParameterizedTest
    @MethodSource("pathArgs")
    void path(String requestPath, String expectedResponsePath) {
        String actualResponse = given()
                .contentType(ContentType.JSON)
                .body(readSystemResource(requestPath))
                .when()
                .post("/api/path")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .body()
                .asString();
        assertJson(readSystemResource(expectedResponsePath), actualResponse);
    }

    private void setUpGraph() {
        execute("""
                CREATE (harry:Person{id: 'Harry', topics: ['magic']})
                CREATE (rone:Person{id: 'Rone', topics: ['snacks']})
                CREATE (hermione:Person{id: 'Hermione', topics: ['magic', 'books']})
                CREATE (greg:Person{id: 'Greg', topics: ['books', 'snacks']})
                CREATE (jinnie:Person{id: 'Jinnie', topics: ['books', 'candies']})
                CREATE (snape:Person{id: 'Snape', topics: ['poisons', 'magic']})
                CREATE (malfoy:Person{id: 'Malfoy', topics: ['magic']})
                CREATE (bill:Person{id: 'Bill', topics: ['bombs', 'snacks']})
                CREATE (beatrice:Person{id: 'Beatrice', topics: ['books', 'darkness', 'candies']})
                                
                CREATE (harry)-[:trust{trustLevel: 10}]->(rone)
                CREATE (harry)-[:trust{trustLevel: 10}]->(hermione)
                CREATE (rone)-[:trust{trustLevel: 10}]->(jinnie)
                CREATE (rone)-[:trust{trustLevel: 10}]->(bill)
                CREATE (bill)-[:trust{trustLevel: 10}]->(jinnie)
                CREATE (hermione)-[:trust{trustLevel: 6}]->(greg)
                CREATE (harry)-[:trust{trustLevel: 4}]->(snape)
                CREATE (snape)-[:trust{trustLevel: 6}]->(malfoy)
                CREATE (snape)-[:trust{trustLevel: 8}]->(beatrice)
                CREATE (malfoy)-[:trust{trustLevel: 7}]->(greg)
                CREATE (greg)-[:trust{trustLevel: 6}]->(malfoy)
                CREATE (malfoy)-[:trust{trustLevel: 7}]->(beatrice)
                CREATE (greg)-[:trust{trustLevel: 3}]->(harry)
                 """);
    }

}
