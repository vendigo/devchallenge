package it.devchallenge.cssgrabber.chrome;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import lombok.AllArgsConstructor;

/**
 * Encapsulates creation/configuration of RemoteWebDriver
 */
@AllArgsConstructor
public class WebDriverFactory {

    private final int pageLoadTimeoutInSeconds;
    private final int scriptExecutionTimeoutInSeconds;
    private final ChromeOptions chromeOptions;
    private final URL seleniumHubUrl;

    public RemoteWebDriver createDriver() {
        RemoteWebDriver webDriver = new RemoteWebDriver(seleniumHubUrl, chromeOptions);
        webDriver.manage().timeouts().pageLoadTimeout(pageLoadTimeoutInSeconds, TimeUnit.SECONDS);
        webDriver.manage().timeouts().setScriptTimeout(scriptExecutionTimeoutInSeconds, TimeUnit.SECONDS);
        return webDriver;
    }
}
