package lk.ijse.cmjd112.FoodOrderingSystem;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Spring Boot application entry point for Food Ordering System.
 * Starts the REST API server with JWT authentication and role-based authorization.
 */
@SpringBootApplication
@EnableJpaRepositories
public class FoodOrderingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodOrderingSystemApplication.class, args);
    }
    @Bean
    public ModelMapper mapper(){
        return new ModelMapper();
    }

}

