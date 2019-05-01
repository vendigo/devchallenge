package it.devchallenge.backend.config;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.yaml.snakeyaml.Yaml;


@Configuration
@EnableScheduling
@EnableCaching
public class AppConfig {

    @Bean
    public ScheduleConfig scheduleConfig(@Value("classpath:schedule-config.yml") Resource configFile) throws Exception {
        Yaml parser = new Yaml();
        try (InputStream inputStream = configFile.getInputStream()) {
            return parser.loadAs(inputStream, ScheduleConfig.class);
        }
    }
}
