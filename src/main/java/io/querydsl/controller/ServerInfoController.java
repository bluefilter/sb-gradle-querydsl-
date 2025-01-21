package io.querydsl.controller;

import io.querydsl.config.EndpointConfig;
import io.querydsl.dto.ApiResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/server", produces = {MediaType.APPLICATION_JSON_VALUE})
public class ServerInfoController {

    private static final Logger log = LoggerFactory.getLogger(ServerInfoController.class);

    // Environment 필드 선언, @RequiredArgsConstructor 가 생성자에서 자동으로 주입
    private final Environment environment;

    @Value("${server.port}")
    String serverPort;

    private final EndpointConfig endpointConfig;

    @Operation(summary = "Server Endpoints API", tags = {"Server API"})
    @GetMapping("/endpoints")
    public Callable<?> getRegisteredEndpoints() {
        ApiResponseDTO result = new ApiResponseDTO();
        result.addData("endpoints", endpointConfig.getEndpoints());
        return () -> result;
    }

    @Operation(summary = "Server Info API", tags = {"Server API"})
    @GetMapping(value = "/info")
    public Callable<?> getInfo() {
        log.info("get");

        ApiResponseDTO result = new ApiResponseDTO();

        try {
            result.addData("activeProfile", environment.getActiveProfiles()[0]);
            // 아래 두 가지 방식으로 값을 가져올 수 있다.
            //result.put("port", environment.getProperty("server.port"));
            result.addData("server port", serverPort);
            result.addData("zoneId", getZoneId());
            result.addData("localDateTimeNow", getLocalDateTimeNow());
        } catch (Exception e) {
            result.setStatus("error");
            result.setMessage(e.getMessage());
        }

        return () -> result;
    }

    String getLocalDateTimeNow() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm:ss"));
    }

    String getZoneId() {
        ZoneId zone = ZoneId.systemDefault();
        return zone.toString();
    }

}
