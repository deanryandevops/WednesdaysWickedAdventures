package com.atu.WednesdaysWickedAdventures;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for WednesdaysWickedAdventures.
 * This class serves as the entry point for the Spring Boot application.
 *
 * <p>The {@code @SpringBootApplication} annotation enables Spring Boot's auto-configuration,
 * component scanning, and enables Spring MVC.</p>
 * 
 * @author	Mathieu Bizumuremyi 
 * @version	1.0 
 * @since	07-03-2025
 * 
 */
@SpringBootApplication
public class WednesdaysWickedAdventuresApplication {

    /**
     * The main method that starts the Spring Boot application.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(WednesdaysWickedAdventuresApplication.class, args);
    }

}