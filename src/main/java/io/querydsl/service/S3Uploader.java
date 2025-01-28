package io.querydsl.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class S3Uploader {

    private final AmazonS3 amazonS3;

    // Logger 객체 생성
    private static final Logger logger = LoggerFactory.getLogger(S3Uploader.class);


    public S3Uploader(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public String upload(MultipartFile multipartFile, String bucketName, String dirName) throws IOException {
        // 파일 이름에서 공백을 제거한 새로운 파일 이름 생성
        String originalFileName = multipartFile.getOriginalFilename();

        // UUID를 파일명에 추가
        String uuid = UUID.randomUUID().toString();

        // originalFileName이 null이 아닌지 확인 (조건문으로 수정)
        if (originalFileName == null) {
            throw new IllegalArgumentException("Original file name must not be null");
        }

        String uniqueFileName = uuid + "_" + originalFileName.replaceAll("\\s", "_");

        String fileName = dirName + "/" + uniqueFileName;

        File uploadFile = convert(multipartFile);

        String uploadImageUrl = putS3(uploadFile, bucketName, fileName);
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }

    private File convert(MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();

        // originalFileName이 null이 아닌지 확인 (조건문으로 수정)
        if (originalFileName == null) {
            throw new IllegalArgumentException("Original file name must not be null");
        }

        String uniqueFileName = uuid + "_" + originalFileName.replaceAll("\\s", "_");

        File convertFile = new File(uniqueFileName);
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            } catch (IOException e) {
                logger.error("파일 변환 중 오류 발생: {}", e.getMessage());
                throw e;
            }
            return convertFile;
        }
        throw new IllegalArgumentException(String.format("파일 변환에 실패했습니다. %s", originalFileName));
    }

    private String putS3(File uploadFile, String bucketName, String fileName) {
        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            logger.info("파일이 삭제되었습니다.");
        } else {
            logger.info("파일이 삭제되지 못했습니다.");
        }
    }

    public void deleteFile(String bucketName, String fileName) {
        // URL 디코딩을 통해 원래의 파일 이름을 가져옵니다.
        String decodedFileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8);
        logger.info("Deleting file from S3: {}", decodedFileName);
        amazonS3.deleteObject(bucketName, decodedFileName);
    }

    public String updateFile(MultipartFile newFile, String bucketName, String oldFileName, String dirName) throws IOException {
        // 기존 파일 삭제
        logger.info("S3 oldFileName: {}", oldFileName);
        deleteFile(bucketName, oldFileName);
        // 새 파일 업로드
        return upload(newFile, bucketName, dirName);
    }
}