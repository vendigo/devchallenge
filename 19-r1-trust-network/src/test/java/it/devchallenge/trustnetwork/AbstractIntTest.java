package it.devchallenge.trustnetwork;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class AbstractIntTest {

    @Container
    private static Neo4jContainer<?> neo4j = new Neo4jContainer<>(DockerImageName.parse("neo4j:4.4"))
            .withoutAuthentication();

    @Autowired
    protected Neo4jClient client;

    protected void execute(String query) {
        client.query(query).run();
    }

    @DynamicPropertySource
    static void registerNeo4jProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.neo4j.uri", neo4j::getBoltUrl);
    }

    @Value("${local.server.port}")
    void initRestAssured(int localPort) {
        RestAssured.port = localPort;
    }
}
