package it.devchallenge.cssgrabber.chrome;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.openqa.selenium.remote.RemoteWebDriver;

@RunWith(MockitoJUnitRunner.class)
public class SeleniumChromeClientTest {

    private static final String parserScript = "return []";
    private static final String GOOGLE_URL = "google.com";
    @Mock
    private WebDriverFactory webDriverFactory;
    @Mock
    private RemoteWebDriver webDriver;

    @Test
    public void getUsedStyles() {
        when(webDriverFactory.createDriver()).thenReturn(webDriver);
        when(webDriver.executeScript(parserScript)).thenReturn(Collections.emptyList());
        SeleniumChromeClient chromeClient = new SeleniumChromeClient(webDriverFactory, parserScript);
        chromeClient.getUsedStyles(GOOGLE_URL);
        verify(webDriver).get(GOOGLE_URL);
        verify(webDriver).executeScript(parserScript);
        verify(webDriver).quit();
    }
}
