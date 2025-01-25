package io.querydsl.common;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageUtils {

    /**
     * 페이지와 정렬 정보를 기반으로 Pageable 객체를 생성하는 메서드
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @param sort 정렬 기준 (예: "name,asc" 또는 "price,desc")
     * @return Pageable 객체
     */
    public static Pageable createPageable(int page, int size, String sort) {
        // sort 값을 ',' 기준으로 분리
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        String sortDirection = sortParams.length > 1 ? sortParams[1] : "asc"; // 방향이 없으면 기본값 'asc'로 설정

        // 정렬 방향 결정 (기본적으로 오름차순, desc면 내림차순으로 설정)
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

        // Pageable 객체 생성 후 반환
        return PageRequest.of(page, size, Sort.by(direction, sortField)); // 정렬 필드와 방향 설정
    }


}
