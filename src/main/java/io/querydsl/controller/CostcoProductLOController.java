package io.querydsl.controller;

import io.querydsl.dto.ApiResponseDTO;
import io.querydsl.entity.CostcoProductLO;
import io.querydsl.service.CostcoProductLOService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

@RestController
@RequestMapping("/api/products/lo")
@RequiredArgsConstructor
public class CostcoProductLOController {

    private final CostcoProductLOService service;

    @Operation(summary = "상품 등록 API", tags = {"CostcoProductLO API"})
    @PostMapping
    public ResponseEntity<CostcoProductLO> uploadProduct(@RequestParam("name") String name) {
        CostcoProductLO savedProduct = service.saveProduct(name);
        return ResponseEntity.ok(savedProduct);
    }

    @Operation(summary = "전체 상품 조회 API", tags = {"CostcoProductLO API"})
    @GetMapping
    public ResponseEntity<List<CostcoProductLO>> getAllProducts() {
        List<CostcoProductLO> products = service.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "특정 상품 조회 API", tags = {"CostcoProductLO API"})
    @GetMapping("/{id}")
    public ResponseEntity<CostcoProductLO> getProductById(@PathVariable UUID id) {
        return service.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "상품 삭제 API", tags = {"CostcoProductLO API"})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        service.deleteProduct(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @Operation(summary = "상품의 비디오 파일 저장 API", tags = {"CostcoProductLO API"})
    @PostMapping(value = "/{id}/video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Callable<?> saveLargeObject(
            @PathVariable UUID id,
            @RequestParam("video") MultipartFile videoFile) throws SQLException, IOException {

        return () -> {
            // 비디오 파일을 InputStream으로 변환
            InputStream videoInputStream = videoFile.getInputStream();

            // 비디오 파일을 특정 상품에 저장
            ApiResponseDTO result = new ApiResponseDTO();
            service.saveLargeObject(id, videoInputStream);
            result.setMessage("Video saved successfully.");
            return result;
        };
    }


    // 특정 상품의 large object (비디오 파일) 조회 API
    @Operation(summary = "상품의 비디오 파일 조회 API", tags = {"CostcoProductLO API"})
    @GetMapping("/{id}/video")
    public Callable<?> getLargeObject(@PathVariable UUID id) throws SQLException {
        return () -> {
            ApiResponseDTO result = new ApiResponseDTO();
            byte[] videoData = service.getLargeObject(id);

            if (videoData != null) {
                result.getResult().put("video", videoData);
//                return ResponseEntity.ok()
//                        .contentType(MediaType.APPLICATION_OCTET_STREAM) // 파일 다운로드를 위한 미디어 타입
//                        .body(videoData);
            } else {
                result.setMessage("not found.");
                // return ResponseEntity.notFound().build();
            }
            return result;
        };
    }
}
