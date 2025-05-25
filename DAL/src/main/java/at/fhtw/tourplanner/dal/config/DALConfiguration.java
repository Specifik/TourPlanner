package at.fhtw.tourplanner.dal.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "at.fhtw.tourplanner.model")
@EnableJpaRepositories(basePackages = "at.fhtw.tourplanner.dal.repository")
public class DALConfiguration {

    private static ConfigurableApplicationContext context;

    public static void initialize() {
        if (context == null || !context.isActive()) {
            // Run Spring Boot application in background
            System.setProperty("spring.main.web-application-type", "none");
            context = SpringApplication.run(DALConfiguration.class);
        }
    }

    public static ConfigurableApplicationContext getContext() {
        if (context == null || !context.isActive()) {
            initialize();
        }
        return context;
    }

    public static void shutdown() {
        if (context != null && context.isActive()) {
            context.close();
        }
    }
}