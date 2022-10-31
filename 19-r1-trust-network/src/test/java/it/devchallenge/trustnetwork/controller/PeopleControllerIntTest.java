package it.devchallenge.trustnetwork.controller;

import io.restassured.http.ContentType;
import it.devchallenge.trustnetwork.AbstractIntTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import static io.restassured.RestAssured.given;
import static it.devchallenge.trustnetwork.TestUtils.assertJson;
import static it.devchallenge.trustnetwork.TestUtils.readSystemResource;
import static it.devchallenge.trustnetwork.service.ValidationService.*;
import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
public class PeopleControllerIntTest extends AbstractIntTest {

    private static final String CREATE_PERSON_REQUEST = readSystemResource("data/createPersonRequest.json");
    private static final String CREATE_CONNECTION_REQUEST = readSystemResource("data/createConnectionRequest.json");
    private static final String UPDATE_CONNECTIONS_REQUEST = readSystemResource("data/updateConnectionsRequest.json");

    @BeforeEach
    void setUp() {
        execute("MATCH p=(a)--(b) DELETE p;");
        execute("MATCH (a) DELETE a;");
    }

    @Test
    void createPersonAlreadyExist() {
        execute("CREATE (p1:Person{id: 'Harry'})");

        given()
                .contentType(ContentType.JSON)
                .body(CREATE_PERSON_REQUEST)
                .when()
                .post("/api/people")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Matchers.equalTo(PERSON_ALREADY_EXISTS));

        var people = client.query("MATCH (p:Person{id: 'Harry'}) return p").fetch().all();
        assertThat(people).hasSize(1);
    }

    @Test
    void createPersonIdNull() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                           {"topics": ["books"]}
                        """)
                .when()
                .post("/api/people")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Matchers.equalTo(ID_NOT_NULL));

        var people = client.query("MATCH (p:Person) return p").fetch().all();
        assertThat(people).isEmpty();
    }

    @Test
    void createPerson() {
        String actualResponse = given()
                .contentType(ContentType.JSON)
                .body(CREATE_PERSON_REQUEST)
                .when()
                .post("/api/people")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .body()
                .asString();
        assertJson(CREATE_PERSON_REQUEST, actualResponse);
        var harry = client.query("MATCH (p:Person{id: 'Harry', topics: ['magic', 'flying']}) return p")
                .fetch().all();
        assertThat(harry).isNotEmpty();
    }

    @Test
    void createConnection() {
        execute("CREATE (p1:Person{id: 'Harry'}), (p2:Person{id: 'Rone'});");

        given()
                .contentType(ContentType.JSON)
                .body(CREATE_CONNECTION_REQUEST)
                .when()
                .post("/api/people/Harry/trust_connections")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        var result = client.query("""
                MATCH p=(:Person{id: 'Harry'})-[:trust{trustLevel: 10}]->(:Person{id: 'Rone'}) return p
                """).fetch();
        assertThat(result.all()).hasSize(1);
    }

    @Test
    void createConnectionInvalidTrustLevel() {
        execute("CREATE (p1:Person{id: 'Harry'}), (p2:Person{id: 'Rone'});");

        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "Rone": -2
                        }
                        """)
                .when()
                .post("/api/people/Harry/trust_connections")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Matchers.equalTo(INVALID_TRUST_LEVEL));

        var result = client.query("""
                MATCH p=(:Person{id: 'Harry'})-[:trust]->(:Person{id: 'Rone'}) return p
                """).fetch();
        assertThat(result.all()).isEmpty();
    }

    @Test
    void createConnectionNotExistentToPerson() {
        execute("CREATE (p1:Person{id: 'Harry'})");

        given()
                .contentType(ContentType.JSON)
                .body(UPDATE_CONNECTIONS_REQUEST)
                .when()
                .post("/api/people/Harry/trust_connections")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Matchers.equalTo(PERSON_NOT_FOUND));

        var result = client.query("""
                MATCH p=(:Person{id: 'Harry'})-[:trust]->(:Person{id: 'Rone'}) return p
                """).fetch();
        assertThat(result.all()).isEmpty();
    }

    @Test
    void createConnectionNotExistentFromPerson() {
        execute("CREATE (p1:Person{id: 'Rone'})");

        given()
                .contentType(ContentType.JSON)
                .body(UPDATE_CONNECTIONS_REQUEST)
                .when()
                .post("/api/people/Harry/trust_connections")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Matchers.equalTo(PERSON_NOT_FOUND));

        var result = client.query("""
                MATCH p=(:Person{id: 'Harry'})-[:trust]->(:Person{id: 'Rone'}) return p
                """).fetch();
        assertThat(result.all()).isEmpty();
    }

    @Test
    void updateConnections() {
        execute("""
                CREATE (harry:Person{id: 'Harry'}), (rone:Person{id: 'Rone'}), (hermione:Person{id: 'Hermione'}), (:Person{id: 'Greg'})
                CREATE (harry)-[:trust{trustLevel: 10}]->(rone)
                CREATE (harry)-[:trust{trustLevel: 8}]->(hermione)
                """);

        given()
                .contentType(ContentType.JSON)
                .body(UPDATE_CONNECTIONS_REQUEST)
                .when()
                .post("/api/people/Harry/trust_connections")
                .then()
                .statusCode(HttpStatus.CREATED.value());
        var allPaths = client.query("""
                MATCH p=(:Person{id: 'Harry'})-[:trust]->(:Person) return p
                """).fetch().all();
        var paths8 = client.query("""
                MATCH p=(:Person{id: 'Harry'})-[:trust{trustLevel: 8}]->(:Person) return p
                """).fetch().all();
        assertThat(allPaths).hasSize(3);
        assertThat(paths8).hasSize(3);
    }
}
