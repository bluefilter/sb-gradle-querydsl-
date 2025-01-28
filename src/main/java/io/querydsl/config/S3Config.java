package io.querydsl.config;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;

@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final AwsConfig awsConfig;

    @Bean
    public AmazonS3 s3Client() {
        // AwsConfig에서 자격 증명과 설정 값을 가져옴
        AWSCredentials credentials = new BasicAWSCredentials(
                awsConfig.getAccessKey(),
                awsConfig.getSecretKey()
        );

        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(awsConfig.getRegion())
                .enablePathStyleAccess()  // LocalStack에서 사용
                .build();
    }
}
