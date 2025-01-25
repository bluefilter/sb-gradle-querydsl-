package io.querydsl.controller;

import io.querydsl.dto.ApiResponseDTO;
import io.querydsl.service.S3Downloader;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;

@RestController
@RequestMapping(value = "/api/s3", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class S3DownloadController {

    private final S3Downloader s3Downloader;


    /**
     * @param fileName 파일 이름
     * @return 파일 존재 여부
     */
    @Operation(summary = "id로 멤버 조회 API", tags = {"S3 API"})
    @GetMapping("/exists")
    public Callable<?> getMemberById(
            @RequestParam String fileName) {

        return () -> {
            // 결과 객체 생성
            ApiResponseDTO result = new ApiResponseDTO();

            if (s3Downloader.doesFileExist(fileName)) {
                result.setMessage("The file exists.");  // 파일이 존재함
            } else {
                result.setMessage("The file does not exist.");  // 파일이 존재하지 않음
            }

            return result;
        };
    }

}