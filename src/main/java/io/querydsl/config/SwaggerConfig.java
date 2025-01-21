package io.querydsl.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.oas.models.info.Info;

import java.util.List;

@Configuration
public class SwaggerConfig {

//    @Bean
//    public OpenAPI customOpenAPI() {
//        return new OpenAPI()
//                .info(new Info()
//                        .title("API Documentation")
//                        .version("1.0"))
//                .tags(List.of(
//                        new Tag().name("Server API").description("Server 정보 조회 API")
//                        // new Tag().name("Product API").description("상품 관리와 관련된 API"),
//                        // new Tag().name("Order API").description("주문 처리와 관련된 API")
//                ));
//    }
}
