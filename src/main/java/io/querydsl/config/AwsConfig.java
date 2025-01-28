package io.querydsl.config;

import lombok.Getter;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Getter
@Configuration
public class AwsConfig {

    @Value("#{awsSecrets['accessKey']}")
    private String accessKey;

    @Value("#{awsSecrets['secretKey']}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region; // region.static 값 매핑
}