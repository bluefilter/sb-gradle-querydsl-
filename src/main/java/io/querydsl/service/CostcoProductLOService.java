package io.querydsl.service;

import io.querydsl.entity.CostcoProductLO;
import io.querydsl.repository.CostcoProductLORepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CostcoProductLOService {

    private final CostcoProductLORepository repository;

    private final JdbcTemplate jdbcTemplate;

    // 상품 저장
    public CostcoProductLO saveProduct(String name) {
        CostcoProductLO product = new CostcoProductLO(name);
        return repository.save(product);
    }

    // 전체 상품 조회
    public List<CostcoProductLO> getAllProducts() {
        return repository.findAll();
    }

    // 특정 상품 조회
    public Optional<CostcoProductLO> getProductById(UUID id) {
        return repository.findById(id);
    }

    // 상품 삭제
    @Transactional
    public void deleteProduct(UUID id) {
        repository.deleteById(id);
    }

    @Transactional
    public void saveLargeObject(UUID productId, InputStream videoInputStream) throws SQLException, IOException {
        // Large Object 생성
        String createLoSql = "SELECT lo_creat(0)";
        Long loOid = jdbcTemplate.queryForObject(createLoSql, Long.class);  // OID 반환

        // Large Object 열기 (read-write 모드로)
        String openLoSql = "SELECT lo_open(?, 131072)";  // 131072는 INV_READWRITE 플래그
        Long loFd = jdbcTemplate.queryForObject(openLoSql, Long.class, loOid);  // Large Object 파일 디스크립터 반환

        // 비디오 데이터를 bytea로 변환
        byte[] videoData = inputStreamToByteArray(videoInputStream);

        // Large Object에 비디오 데이터 삽입 (lo_write 사용)
        String writeDataSql = "SELECT lo_write(?, ?)";  // OID와 video bytea 데이터를 삽입
        jdbcTemplate.update(writeDataSql, loFd, videoData);  // lo_write에 파일 디스크립터와 bytea 데이터 전달

        // Large Object 닫기
        String closeLoSql = "SELECT lo_close(?)";
        jdbcTemplate.update(closeLoSql, loFd);

        // 상품 정보에 OID 저장
        String updateSql = "UPDATE costco_products_lo SET video_oid = ? WHERE id = ?";
        jdbcTemplate.update(updateSql, loOid, productId);
    }

    // InputStream을 byte 배열로 변환하는 헬퍼 메소드
    private byte[] inputStreamToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }
        return byteArrayOutputStream.toByteArray();
    }

    public byte[] getLargeObject(UUID productId) throws SQLException {
        String selectSql = "SELECT video_oid FROM costco_products_lo WHERE id = ?";

        return jdbcTemplate.query(connection -> {
            PreparedStatement ps = connection.prepareStatement(selectSql);
            ps.setObject(1, productId);
            return ps;
        }, resultSet -> {
            if (resultSet.next()) {
                Long oid = resultSet.getObject("video_oid", Long.class); // Long 타입으로 명시적으로 가져옴
                if (oid == null) {
                    // video_oid가 null인 경우 null 반환 또는 다른 처리
                    return null;  // null 반환하거나 적절한 메시지를 추가
                }
                return fetchLargeObject(oid); // OID를 통해 실제 Large Object 조회
            }
            return null; // 결과가 없으면 null 반환
        });
    }

    // OID를 사용하여 Large Object를 조회
    private byte[] fetchLargeObject(long oid) throws SQLException {
        String query = "SELECT lo_get(?)";
        return jdbcTemplate.query(connection -> {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setLong(1, oid);
            return ps;
        }, resultSet -> {
            if (resultSet.next()) {
                return resultSet.getBytes(1);
            }
            return null;
        });
    }
}
