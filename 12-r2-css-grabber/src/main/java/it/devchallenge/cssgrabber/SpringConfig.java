package it.devchallenge.cssgrabber;

import java.net.URL;
import java.nio.charset.Charset;
import java.time.Clock;

import org.apache.commons.io.IOUtils;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import it.devchallenge.cssgrabber.cache.CacheInvalidationComponent;
import it.devchallenge.cssgrabber.cache.CacheService;
import it.devchallenge.cssgrabber.chrome.ChromeClient;
import it.devchallenge.cssgrabber.chrome.SeleniumChromeClient;
import it.devchallenge.cssgrabber.chrome.WebDriverFactory;

@Configuration
public class SpringConfig {

    @Value("${selenium.hub.url}")
    private String seleniumHubUrl;
    @Value("classpath:parseStyleSheets.js")
    private Resource styleParserScript;
    @Value("${cache.live.time.in.minutes}")
    private int cacheLiveTimeInMinutes;
    @Value("${page.load.timeout.in.seconds}")
    private int pageLoadTimeoutInSeconds;
    @Value("${script.execution.timeout.in.seconds}")
    private int scriptTimeoutInSeconds;

    @Bean
    public WebDriverFactory webDriverFactory() throws Exception {
        return new WebDriverFactory(pageLoadTimeoutInSeconds, scriptTimeoutInSeconds, chromeOptions(), new URL(seleniumHubUrl));
    }

    @Bean
    public ChromeClient chromeClient() throws Exception {
        String script = IOUtils.toString(styleParserScript.getURI(), Charset.defaultCharset());
        return new SeleniumChromeClient(webDriverFactory(), script);
    }

    @Bean
    public ChromeOptions chromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.setAcceptInsecureCerts(true);
        options.setHeadless(true);
        return options;
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public CacheInvalidationComponent cacheInvalidationComponent(CacheService cacheService) {
        return new CacheInvalidationComponent(clock(), cacheLiveTimeInMinutes, cacheService);
    }
}
