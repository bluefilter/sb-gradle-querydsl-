package io.querydsl.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Component
public class EndpointConfig implements ApplicationListener<ContextRefreshedEvent> {

    private final List<String> endpoints = new ArrayList<>();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // Application Context에서 RequestMappingHandlerMapping 빈 가져오기
        RequestMappingHandlerMapping handlerMapping = event.getApplicationContext()
                .getBean(RequestMappingHandlerMapping.class);

        // 모든 매핑 정보 가져오기
        Map<?, ?> handlerMethods = handlerMapping.getHandlerMethods();

        // 경로 정보 저장
        handlerMethods.forEach((key, value) -> endpoints.add(key.toString()));
    }
}
