package it.devchallenge.cncmachine.simplebox

import io.restassured.RestAssured
import org.apache.http.HttpStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SimpleBoxControllerIntTest {

    @LocalServerPort
    protected var port: Int = 0

    @BeforeEach
    internal fun setUp() {
        RestAssured.port = port
    }


    @CsvSource(
        "invalidJson.json,'Invalid input format. Invalid json'",
        "nonPositiveNumbers.json,'Invalid input format. Please use only positive numbers'",
        "sheetToSmall.json,'Invalid input format. Too small for producing at least one box'",
    )
    @ParameterizedTest
    fun invalidRequests(requestFileName: String, expectedErrorMessage: String) {
        val actualResponse = RestAssured.given()
            .bodyFromFile("/json/$requestFileName")
            .post("/api/simple_box")
            .then()
            .statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
            .extractResponse()

        assertThat(actualResponse.success).isFalse
        assertThat(actualResponse.error).isEqualTo(expectedErrorMessage)
    }

    @Test
    fun `Place a single box, task sample`() {
        val actualResponse = RestAssured.given()
            .bodyFromFile("/json/singleBox1.json")
            .post("/api/simple_box")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extractResponse()

        assertThat(actualResponse.success).isTrue
        assertThat(actualResponse.amount).isEqualTo(1)
        assertThat(actualResponse.program).hasSize(11)
    }

    @Test
    fun `Place a single box, swap sides`() {
        val actualResponse = RestAssured.given()
            .bodyFromFile("/json/singleBoxSwapSides.json")
            .post("/api/simple_box")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extractResponse()

        assertThat(actualResponse.success).isTrue
        assertThat(actualResponse.amount).isEqualTo(1)
    }

    @Test
    fun `Place a couple of boxes`() {
        val actualResponse = measuringTime {
            RestAssured.given()
                .bodyFromFile("/json/fewBoxes1.json")
                .post("/api/simple_box")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extractResponse()
        }

        assertThat(actualResponse.success).isTrue
        println("Placed ${actualResponse.amount} boxes")
        assertThat(actualResponse.amount).isGreaterThanOrEqualTo(3)
    }

    @Test
    fun `Place many boxes`() {
        val actualResponse = measuringTime {
            RestAssured.given()
                .bodyFromFile("/json/manyBoxes.json")
                .post("/api/simple_box")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extractResponse()
        }

        assertThat(actualResponse.success).isTrue
        println("Placed ${actualResponse.amount} boxes")
        assertThat(actualResponse.amount).isGreaterThanOrEqualTo(100)
    }

    @Test
    fun `Caching works`() {
        val response1 = measuringTime {
            RestAssured.given()
                .bodyFromFile("/json/manyBoxes.json")
                .post("/api/simple_box")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extractResponse()
        }

        val response2 = measuringTime {
            RestAssured.given()
                .bodyFromFile("/json/manyBoxes.json")
                .post("/api/simple_box")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extractResponse()
        }

        println("Placed ${response1.amount} boxes")
        assertThat(response1).isEqualTo(response2)
    }
}
