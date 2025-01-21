package io.querydsl;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(title = "User API", version = "v1", description = "API for managing users")) //Spring Boot 애플리케이션에 Swagger 설정하기
@SpringBootApplication
public class SbGradleQuerydslApplication {

    public static void main(String[] args) {
        SpringApplication.run(SbGradleQuerydslApplication.class, args);
    }

}
