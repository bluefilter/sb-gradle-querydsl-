package io.querydsl.controller;

import io.querydsl.common.PageUtils;
import io.querydsl.dto.ApiResponseDTO;
import io.querydsl.dto.CostcoMemberUpdateRequestDTO;
import io.querydsl.service.CostcoMemberService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.Callable;

@RestController
@RequestMapping(value = "/api/members", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class CostcoMemberController {

    private final CostcoMemberService costcoMemberService;

    /**
     * 멤버를 등록하는 API
     *
     * @param memberCreateRequest 생성할 멤버의 정보 (RequestBody로 받음)
     * @return 등록된 멤버의 DTO
     */
    @Operation(summary = "멤버 등록 API", tags = {"CostcoMember API"})
    @PostMapping
    public Callable<?> createMember(
            @RequestBody CostcoMemberUpdateRequestDTO memberCreateRequest) {
        return () -> {
            ApiResponseDTO response = new ApiResponseDTO();

            // 응답 객체에 등록된 멤버 정보 설정
            response.setResult(costcoMemberService.createMember(memberCreateRequest));

            return response;
        };
    }

    /**
     * 모든 멤버 조회
     *
     * @return 모든 Member 목록
     */
    @Operation(summary = "멤버 조회 API", tags = {"CostcoMember API"})
    @GetMapping
    public Callable<?> getAllMembers(
            @RequestParam(value = "includeOrders", defaultValue = "false") boolean includeOrders,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "id,asc") String sort) {

        // Pageable을 생성하는 메서드 호출
        Pageable pageable = PageUtils.createPageable(page, size, sort);

        return () -> {
            // 페이지와 정렬 정보 파싱

            // ApiResponseDTO 객체 생성 및 데이터 반환
            ApiResponseDTO result = new ApiResponseDTO();
            result.setResult(costcoMemberService.getMembersWithOrders(includeOrders, pageable));

            return result;
        };
    }

    /**
     * id로 멤버 조회
     *
     * @param id      멤버의 ID
     * @param includeOrders 주문 정보를 포함할지 여부
     * @return 조회된 멤버 DTO
     */
    @Operation(summary = "id로 멤버 조회 API", tags = {"CostcoMember API"})
    @GetMapping("/{id}")
    public Callable<?> getMemberById(
            @PathVariable UUID id,  // URL 경로에서 memberId를 받아옴
            @RequestParam(value = "includeOrders", defaultValue = "false") boolean includeOrders) {

        return () -> {
            // 결과 객체 생성
            ApiResponseDTO result = new ApiResponseDTO();

            result.setResult(costcoMemberService.getMemberById(id, includeOrders));  // 조회된 멤버 DTO를 반환

            return result;
        }; // 성공적으로 조회된 경우 200 OK 반환
    }

    /**
     * 멤버 정보 수정
     *
     * @param id 수정할 멤버의 ID
     * @return 수정된 멤버 DTO
     */
    @Operation(summary = "멤버 정보 수정 API", tags = {"CostcoMember API"})
    @PutMapping("/{id}")
    public Callable<?> updateMember(
            @PathVariable UUID id,
            @RequestBody CostcoMemberUpdateRequestDTO costcoMemberUpdateRequestDTO) {

        return () -> {
            ApiResponseDTO result = new ApiResponseDTO();

            if (costcoMemberService.updateMember(id, costcoMemberUpdateRequestDTO)) {
                result.setMessage("Member updated successfully");  // 수정된 멤버 반환
            } else {
                result.setMessage("Member not found or update failed");  // 멤버가 없거나 수정 실패
            }

            return result;
        };
    }

    /**
     * 멤버 삭제
     *
     * @param id 삭제할 멤버의 ID
     * @return 삭제 성공 여부
     */
    @Operation(summary = "멤버 삭제 API", tags = {"CostcoMember API"})
    @DeleteMapping("/{id}")
    public Callable<?> deleteMember(@PathVariable UUID id) {

        return () -> {
            ApiResponseDTO result = new ApiResponseDTO();
            boolean isDeleted = costcoMemberService.deleteMember(id);

            if (isDeleted) {
                result.setMessage("Member deleted successfully");  // 삭제 성공
            } else {
                result.setMessage("Member not found or delete failed");  // 멤버가 없거나 삭제 실패
            }

            return result;
        };
    }
}