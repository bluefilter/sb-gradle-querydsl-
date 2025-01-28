package io.querydsl.controller;

import io.querydsl.dto.ApiResponseDTO;
import io.querydsl.service.S3Downloader;
import io.querydsl.service.S3Manager;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;

@RestController
@RequestMapping(value = "/api/aws/s3", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class S3Controller {

    private final S3Manager s3Manager;
    private final S3Downloader s3Downloader;

    /**
     * @param bucketName 버켓 이름
     * @return 버켓 존재 여부
     */
    @Operation(summary = "버켓 존재 여부 확인 API", tags = {"S3 API"})
    @GetMapping("/bucket/exists")
    public Callable<?> doesBucketExist(
            @RequestParam String bucketName) {

        return () -> {
            // 결과 객체 생성
            ApiResponseDTO result = new ApiResponseDTO();

            if (s3Manager.doesBucketExist(bucketName)) {
                result.setMessage("The bucket exists.");  // 파일이 존재함
            } else {
                result.setMessage("The bucket does not exist.");  // 파일이 존재하지 않음
            }

            return result;
        };
    }

    /**
     * @param fileName 파일 이름
     * @return 파일 존재 여부
     */
    @Operation(summary = "파일 존재 여부 확인 API", tags = {"S3 API"})
    @GetMapping("/file/exists")
    public Callable<?> doesFileExist(
            @RequestParam String bucketName,
            @RequestParam String fileName) {

        return () -> {
            // 결과 객체 생성
            ApiResponseDTO result = new ApiResponseDTO();

            if (s3Downloader.doesFileExist(bucketName, fileName)) {
                result.setMessage("The file exists.");  // 파일이 존재함
            } else {
                result.setMessage("The file does not exist.");  // 파일이 존재하지 않음
            }

            return result;
        };
    }

}