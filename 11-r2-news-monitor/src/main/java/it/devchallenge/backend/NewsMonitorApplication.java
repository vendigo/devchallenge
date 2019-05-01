package it.devchallenge.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the application. Launch web app on embedded Tomcat.
 */
@SpringBootApplication
public class NewsMonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewsMonitorApplication.class, args);
	}
}
