package it.devchallenge.cssgrabber;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.collect.ImmutableMap;

import it.devchallenge.cssgrabber.chrome.WebDriverFactory;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CssGrabberIntegrationTest {

    private static final Map<String, String> CSS_ITEM1 = ImmutableMap.<String, String>builder()
        .put("selector", "body")
        .put("cssText", "body { font-family: Arial; background-color: rgb(255, 255, 255); }")
        .build();
    private static final Map<String, String> CSS_ITEM2 = ImmutableMap.<String, String>builder()
        .put("selector", "p")
        .put("cssText", "p { font-size:12pt; font-family:Arial; margin-top:1pt; }")
        .build();
    private static final Map<String, String> CSS_ITEM3 = ImmutableMap.<String, String>builder()
        .put("selector", "p")
        .put("cssText", "p { font-weight:bold; font-size:18pt; }")
        .build();
    private static final String GOOGLE_URL = "google.com";
    private static final String YOUTUBE_URL = "youtube.com";
    private static final String FINAL_CSS = "body{font-family:Arial;background-color:rgb(255, 255, 255);}" +
        "p{font-size:18pt;font-family:Arial;margin-top:1pt;font-weight:bold;}";
    private static final String STYLES_ENDPOINT_URL = "/styles";
    private static final String ERROR_MESSAGE = "ERROR: Failed to process url";

    @MockBean
    private WebDriverFactory webDriverFactory;
    @Autowired
    private TestRestTemplate testRestTemplate;

    @SuppressWarnings("unchecked")
    @Test
    @DirtiesContext
    public void getStyles() {
        RemoteWebDriver webDriver = mock(RemoteWebDriver.class);
        when(webDriverFactory.createDriver())
            .thenReturn(webDriver);
        doNothing().when(webDriver).get(anyString());
        when(webDriver.executeScript(anyString())).thenReturn(Arrays.asList(CSS_ITEM1, CSS_ITEM2, CSS_ITEM3));

        ResponseEntity<Map> response = testRestTemplate
            .postForEntity(STYLES_ENDPOINT_URL, Arrays.asList(GOOGLE_URL, YOUTUBE_URL), Map.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        Map<String, String> body = response.getBody();
        assertThat(body, hasEntry(GOOGLE_URL, FINAL_CSS));
        assertThat(body, hasEntry(YOUTUBE_URL, FINAL_CSS));
        assertThat(body.keySet(), hasSize(2));
    }

    @SuppressWarnings("unchecked")
    @Test
    @DirtiesContext
    public void getStylesError() {
        when(webDriverFactory.createDriver()).thenThrow(new RuntimeException("Unable to create driver"));
        ResponseEntity<Map> response = testRestTemplate
            .postForEntity(STYLES_ENDPOINT_URL, Collections.singletonList(GOOGLE_URL), Map.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        Map<String, String> body = response.getBody();
        assertThat(body, hasEntry(GOOGLE_URL, ERROR_MESSAGE));
        assertThat(body.keySet(), hasSize(1));
    }
}
