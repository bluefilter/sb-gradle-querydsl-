package io.querydsl.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.querydsl.common.AwsSecretsManagerUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class AwsSecretsConfig {

    private final AwsSecretsManagerUtil secretsManagerUtil = new AwsSecretsManagerUtil();

    @Bean
    public Map<String, String> awsSecrets() {
        String secretJson = secretsManagerUtil.getSecret("myapp/aquila");

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(secretJson, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse secrets JSON", e);
        }
    }
}