package io.querydsl.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class S3Downloader {
    // Logger 객체 생성
    private static final Logger logger = LoggerFactory.getLogger(S3Downloader.class);

    private final AmazonS3 amazonS3;

    public S3Downloader(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }


    // 파일 다운로드
    public S3Object download(String bucket, String fileName) {
        // S3에서 파일을 가져옵니다.
        logger.info("S3에서 파일을 다운로드합니다: {}", fileName);
        try {
            return amazonS3.getObject(new GetObjectRequest(bucket, fileName));
        } catch (AmazonS3Exception e) {
            logger.error("S3 파일 다운로드 중 오류 발생: {}", e.getMessage());
            throw new IllegalArgumentException("파일 다운로드에 실패했습니다.", e);
        }
    }


    // 파일을 InputStream으로 다운로드
    public InputStream downloadFileAsStream(String bucket, String fileName) {
        S3Object s3Object = download(bucket, fileName);

        // S3Object에서 InputStream을 가져오고 try-with-resources로 처리
        try (S3ObjectInputStream inputStream = s3Object.getObjectContent()) {
            return inputStream;  // 호출자가 InputStream을 사용할 수 있도록 반환
        } catch (IOException e) {
            logger.error("파일 다운로드 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("파일 다운로드에 실패했습니다.", e);
        }
    }


    // 파일의 URL을 반환
    public String getFileUrl(String bucket, String fileName) {
        // S3 파일의 URL을 가져옵니다.
        return amazonS3.getUrl(bucket, fileName).toString();
    }


    // 파일이 존재하는지 확인
    public boolean doesFileExist(String bucket, String fileName) {
        try {
            amazonS3.getObjectMetadata(bucket, fileName);
            return true;  // 파일이 존재하는 경우
        } catch (AmazonS3Exception e) {
            logger.warn("파일이 존재하지 않습니다: {}", fileName);
            return false;  // 파일이 존재하지 않는 경우
        }
    }
}
