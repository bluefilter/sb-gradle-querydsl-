package io.querydsl.controller;

import io.querydsl.entity.CostcoProductThumbnail;
import io.querydsl.service.CostcoProductThumbnailService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/products/thumbnail")
@RequiredArgsConstructor
public class CostcoProductThumbnailController {

    private final CostcoProductThumbnailService service;

    @Operation(summary = "상품 쎔네일 등록 API", tags = {"CostcoProductThumbnail API"})
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CostcoProductThumbnail> uploadProduct(
            @RequestParam("name") String name,
            @RequestParam("thumbnail") MultipartFile file) throws IOException {

        byte[] thumbnailBytes = file.getBytes();
        CostcoProductThumbnail savedProduct = service.saveProduct(name, thumbnailBytes);
        return ResponseEntity.ok(savedProduct);
    }

    @Operation(summary = "전체 상품 썸네일 조회 API", tags = {"CostcoProductThumbnail API"})
    @GetMapping
    public ResponseEntity<List<CostcoProductThumbnail>> getAllProducts() {
        return ResponseEntity.ok(service.getAllProducts());
    }

    @Operation(summary = "특정 상품 썸네일 조회 API", tags = {"CostcoProductThumbnail API"})
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getProductThumbnail(@PathVariable UUID id) {
        return service.getProductById(id)
                .map(product -> ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(product.getThumbnail()))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "특정 상품 썸네일 삭제 API", tags = {"CostcoProductThumbnail API"})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        try {
            service.deleteProduct(id);
            return ResponseEntity.noContent().build();  // 204 No Content 반환
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();  // 404 Not Found 반환
        }
    }

    // ✅ 제품 이름과 썸네일을 함께 수정 API
    @Operation(summary = "특정 상품 이름과 썸네일 수정 API", tags = {"CostcoProductThumbnail API"})
    @PutMapping("/{id}")
    public ResponseEntity<CostcoProductThumbnail> updateProduct(
            @PathVariable UUID id,
            @RequestParam("name") String newName,
            @RequestParam("thumbnail") MultipartFile file) throws IOException {
        try {
            byte[] newThumbnail = file.getBytes();
            CostcoProductThumbnail updatedProduct = service.updateProduct(id, newName, newThumbnail);
            return ResponseEntity.ok(updatedProduct);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();  // 404 Not Found 반환
        }
    }
}