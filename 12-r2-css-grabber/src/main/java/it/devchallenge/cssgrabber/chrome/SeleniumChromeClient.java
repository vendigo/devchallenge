package it.devchallenge.cssgrabber.chrome;

import java.util.List;
import java.util.Map;

import org.openqa.selenium.remote.RemoteWebDriver;

import lombok.AllArgsConstructor;

/**
 * Implementation which uses Selenium RemoteWebDriver
 */
@AllArgsConstructor
public class SeleniumChromeClient implements ChromeClient {

    private final WebDriverFactory webDriverFactory;
    private final String styleParserScript;

    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, String>> getUsedStyles(String url) {
        RemoteWebDriver webDriver = null;
        try {
            webDriver = webDriverFactory.createDriver();
            webDriver.get(url);
            return (List<Map<String, String>>) webDriver.executeScript(styleParserScript);
        } finally {
            if (webDriver != null) {
                webDriver.quit();
            }
        }
    }
}
