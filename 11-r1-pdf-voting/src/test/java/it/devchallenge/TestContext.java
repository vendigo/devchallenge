package it.devchallenge;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestContext {
    @Bean
    public CommandLineRunner fileParserRunner() {
        return new TestFileParserRunner();
    }
}
