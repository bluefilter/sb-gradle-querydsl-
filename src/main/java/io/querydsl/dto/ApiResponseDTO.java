package io.querydsl.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.LinkedHashMap;
import java.util.Map;


@Getter
@Setter
public class ApiResponseDTO {
    // Getter, Setter
    private HttpStatus httpStatus;  // HTTP 상태 코드
    private String status;  // 성공 / 실패 상태
    private String message; // 에러 메시지 또는 상태 메시지
    private Map<String, Object> result;    // 실제 데이터 (데이터 타입에 따라 다르게 할당)

    // 기본 생성자
    public ApiResponseDTO() {

        this.httpStatus = HttpStatus.OK;  // 기본값을 200 (OK)으로 설정
        this.result = new LinkedHashMap<>(); // 기본 생성자에서 Map 초기화
    }

    // 상태와 데이터를 설정할 수 있는 생성자
    public ApiResponseDTO(String status, String message, Map<String, Object> data) {
        this.httpStatus = HttpStatus.OK;  // 기본값을 200 (OK)으로 설정
        this.status = status;
        this.message = message != null ? message : getDefaultMessage(status); // 상태에 따른 기본 메시지 설정
        this.result = new LinkedHashMap<>(data != null ? data : new LinkedHashMap<>()); // 데이터 병합
    }

    // 데이터를 추가하는 유틸리티 메서드
    public ApiResponseDTO addData(String key, Object value) {
        this.result.put(key, value);
        return this; // 메서드 체이닝을 위해 return this 추가
    }

    // 상태를 설정하는 메서드 (메서드 체이닝을 위해 return this 추가)
    public ApiResponseDTO setStatus(String status, String message) {
        this.status = status;
        this.message = message != null ? message : getDefaultMessage(status);
        return this;
    }

    // result를 병합하는 메서드 (메서드 체이닝을 위해 return this 추가)
    public ApiResponseDTO mergeResult(Map<String, Object> newResult) {
        if (newResult != null && !newResult.isEmpty()) {
            this.result.putAll(newResult); // 기존 result에 새로운 데이터를 병합
        }
        return this;
    }

    // 상태에 따른 기본 메시지 반환
    private String getDefaultMessage(String status) {
        if ("success".equals(status)) {
            return "Operation was successful";
        } else if ("error".equals(status)) {
            return "An error occurred";
        }
        return "Unknown status"; // 기본값
    }
}
