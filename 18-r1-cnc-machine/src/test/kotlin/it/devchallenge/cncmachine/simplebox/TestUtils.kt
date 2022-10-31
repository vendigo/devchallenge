package it.devchallenge.cncmachine.simplebox

import io.restassured.http.ContentType
import io.restassured.response.ValidatableResponse
import io.restassured.specification.RequestSpecification
import org.springframework.util.StopWatch

class TestUtils

fun ValidatableResponse.extractResponse(): SimpleBoxResponse {
    return this.extract().body().`as`(SimpleBoxResponse::class.java)
}

fun RequestSpecification.bodyFromFile(fileName: String): RequestSpecification {
    return this.body(getResource(fileName))
        .contentType(ContentType.JSON)
}

fun getResource(fileName: String): String {
    return TestUtils::class.java.getResource(fileName)!!.readText()
}

fun <T> measuringTime(action: () -> T): T {
    val stopWatch = StopWatch()
    stopWatch.start()
    val result = action()
    stopWatch.stop()
    println("Completed in ${stopWatch.totalTimeMillis} ms")
    return result
}
