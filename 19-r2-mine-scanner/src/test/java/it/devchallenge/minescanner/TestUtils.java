package it.devchallenge.minescanner;

import lombok.SneakyThrows;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.InputStream;

public class TestUtils {

    @SneakyThrows
    public static String readSystemResource(String location) {
        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream(location)) {
            return new String(inputStream.readAllBytes());
        }
    }

    @SneakyThrows
    public static void assertJson(String expectedJson, String actualJson) {
        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT);
    }
}
