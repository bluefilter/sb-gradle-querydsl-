package io.querydsl.controller;

import io.querydsl.common.PageUtils;
import io.querydsl.dto.ApiResponseDTO;
import io.querydsl.dto.CostcoProductUpdateRequestDTO;
import io.querydsl.service.CostcoProductService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.Callable;

@RestController
@RequestMapping(value = "/api/products", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class CostcoProductController {

    private final CostcoProductService productService;

    /**
     * 상품을 등록하는 API
     *
     * @param costcoProductUpdateRequestDTO 생성할 상품의 정보 (RequestBody로 받음)
     * @return 등록된 상품 정보
     */
    @Operation(summary = "상품 등록 API", tags = {"CostcoProduct API"})
    @PostMapping
    public Callable<?> createProduct(
            @RequestBody CostcoProductUpdateRequestDTO costcoProductUpdateRequestDTO) {
        return () -> {
            ApiResponseDTO response = new ApiResponseDTO();

            // 응답 객체에 등록된 멤버 정보 설정
            response.setResult(productService.createProduct(costcoProductUpdateRequestDTO));

            return response;
        };
    }

    /**
     * 모든 상품 조회
     *
     * @return 모든 상품 목록
     */
    @Operation(summary = "멤버 조회 API", tags = {"CostcoProduct API"})
    @GetMapping
    public Callable<?> getAllMembers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "id,asc") String sort) {

        // Pageable을 생성하는 메서드 호출
        Pageable pageable = PageUtils.createPageable(page, size, sort);

        return () -> {
            // ApiResponseDTO 객체 생성 및 데이터 반환
            ApiResponseDTO result = new ApiResponseDTO();
            result.setResult(productService.getProducts(pageable));

            return result;
        };
    }

    /**
     * id로 멤버 조회
     *
     * @param id 멤버의 ID
     * @return 조회된 멤버 DTO
     */
    @Operation(summary = "id로 멤버 조회 API", tags = {"CostcoProduct API"})
    @GetMapping("/{id}")
    public Callable<?> getMemberById(
            @PathVariable UUID id) {

        return () -> {
            // 결과 객체 생성
            ApiResponseDTO result = new ApiResponseDTO();

            result.setResult(productService.getProductById(id));

            return result;
        };
    }


    /**
     * 상품 정보 수정
     *
     * @param id 수정할 상품의 ID
     * @return 수정 여부
     */
    @Operation(summary = "멤버 정보 수정 API", tags = {"CostcoProduct API"})
    @PutMapping("/{id}")
    public Callable<?> updateProduct(
            @PathVariable UUID id,
            @RequestBody CostcoProductUpdateRequestDTO costcoProductUpdateRequestDTO) {

        return () -> {
            ApiResponseDTO result = new ApiResponseDTO();

            if (productService.updateProduct(id, costcoProductUpdateRequestDTO)) {
                result.setMessage("Product updated successfully");  // 수정된 멤버 반환
            } else {
                result.setMessage("Product not found or update failed");  // 멤버가 없거나 수정 실패
            }

            return result;
        };
    }


    /**
     * 상품 삭제
     *
     * @param id 삭제할 상품의 ID
     * @return 삭제 성공 여부
     */
    @Operation(summary = "멤버 삭제 API", tags = {"CostcoProduct API"})
    @DeleteMapping("/{id}")
    public Callable<?> deleteProduct(@PathVariable UUID id) {

        return () -> {
            ApiResponseDTO result = new ApiResponseDTO();
            boolean isDeleted = productService.deleteProduct(id);

            if (isDeleted) {
                result.setMessage("Product deleted successfully");  // 삭제 성공
            } else {
                result.setMessage("Product not found or delete failed");  // 멤버가 없거나 삭제 실패
            }

            return result;
        };
    }
}