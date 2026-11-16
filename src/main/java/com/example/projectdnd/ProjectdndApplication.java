package com.example.projectdnd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Project DnD Spring Boot application.
 * Bootstraps the application context, web server, security, and database layers.
 */
@SpringBootApplication
public class ProjectdndApplication {

	/**
	 * Launches the Spring Boot application.
	 *
	 * @param args command-line arguments passed during startup
	 */
	public static void main(String[] args) {
		SpringApplication.run(ProjectdndApplication.class, args);
	}

}
