package io.querydsl.service;


import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.querydsl.dto.*;
import io.querydsl.entity.CostcoProduct;
import io.querydsl.entity.QCostcoProduct;
import io.querydsl.repository.CostcoProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CostcoProductService {

    private final CostcoProductRepository cpRepo;
    private final JPAQueryFactory queryFactory;
    private final QCostcoProduct qCostcoProduct = QCostcoProduct.costcoProduct;


    /**
     * 상품을 등록하는 서비스 메서드
     *
     * @param requestDTO 생성할 상품의 정보
     * @return 등록된 상품 DTO
     */
    @Transactional
    public Map<String, Object> createProduct(CostcoProductUpdateRequestDTO requestDTO) {
        // 요청 DTO를 통해 엔티티 생성
        CostcoProduct product = new CostcoProduct(requestDTO.getName(), requestDTO.getPrice());

        // 데이터베이스에 저장
        CostcoProduct savedProduct = cpRepo.save(product);

        // DTO 변환 후 반환
        return Map.of("products", List.of(CostcoProductDTO.fromEntity(savedProduct)));
    }


    @Transactional
    public Map<String, Object> getProductById(UUID id) {
        // id로 조회하는 쿼리 생성
        CostcoProduct cp = queryFactory.selectFrom(qCostcoProduct)
                .where(qCostcoProduct.id.eq(id))
                .fetchOne();  // id로 한 명의 회원만 조회

        // 회원이 존재하면 DTO로 변환하고, 존재하지 않으면 빈 리스트 반환
        return cp != null
                ? Collections.singletonMap("products", Collections.singletonList(CostcoProductDTO.fromEntity(cp)))
                : Collections.singletonMap("products", Collections.emptyList());
    }


    @Transactional
    public boolean updateProduct(CostcoProductUpdateRequestDTO requestDTO) {
        // 멤버 조회
        CostcoProduct cp = queryFactory.selectFrom(qCostcoProduct)
                .where(qCostcoProduct.id.eq(requestDTO.getId()))
                .fetchOne();

        if (cp != null) {
            // 멤버 정보를 업데이트
            cp.setName(requestDTO.getName());
            cp.setPrice(requestDTO.getPrice());

            // 트랜잭션이 끝날 때 자동으로 flush()가 호출되어
            // 엔티티 상태가 DB에 반영됩니다. 따라서 명시적으로 flush()를 호출할 필요가 없습니다.
            // 엔티티의 변경 사항은 트랜잭션이 커밋될 때 자동으로 반영되므로 성능 최적화 및 코드 간결성을 위해
            // flush()를 생략하는 것이 좋습니다.

            // 엔티티 상태가 변경된 것을 즉시 DB에 반영하려면 명시적으로 entityManager.flush()를 호출할 수 있으나,
            // 일반적으로는 트랜잭션 커밋 시 자동으로 반영되므로 생략합니다.
            // 엔티티 상태가 변경된 것을 반영하기 위해 flush() 호출 (옵션)
            // entityManager.flush(); // 엔티티 변경 사항을 즉시 DB에 반영

            return true;
        } else {
            return false;  // 멤버가 존재하지 않으면 null 반환
        }
    }


    @Transactional
    public boolean deleteProduct(UUID id) {
        // 멤버 조회
        CostcoProduct cp = queryFactory.selectFrom(qCostcoProduct)
                .where(qCostcoProduct.id.eq(id))
                .fetchOne();

        if (cp != null) {
            // 상품 삭제
            queryFactory.delete(qCostcoProduct)
                    .where(qCostcoProduct.id.eq(id))
                    .execute();
            return true;
        } else {
            return false;
        }
    }


    @Transactional
    public Map<String, Object> getProducts(Pageable pageable) {
        Map<String, Object> result = new HashMap<>();

        // 쿼리 생성 및 페이지 처리
        List<CostcoProduct> cps = queryFactory.selectFrom(qCostcoProduct)
                .orderBy(createOrderSpecifier(pageable))  // 정렬 처리 (배열로 변환)/ 정렬 처리
                .offset((long) pageable.getPageNumber() * pageable.getPageSize())  // 페이지 번호에 따른 offset 처리
                .limit(pageable.getPageSize())  // 페이지 크기만큼 데이터 조회
                .fetch();  // 결과 조회

        // DTO로 변환
        List<CostcoProductDTO> cpDtos = cps.stream()
                .map(CostcoProductDTO::fromEntity)
                .collect(Collectors.toList());

        // 총 항목 수를 구하기 위한 효율적인 방법 (fetchCount() 사용)
        Long totalCount = queryFactory.select(Wildcard.count)
                .from(qCostcoProduct)
                .fetchOne();
        totalCount = totalCount == null ? 0 : totalCount;

        // 페이지 결과 생성
        Page<CostcoProductDTO> page = new PageImpl<>(cpDtos, pageable, totalCount);

        result.put("products", page.getContent());
        result.put("totalPages", page.getTotalPages());  // 총 페이지 수
        result.put("totalElements", page.getTotalElements());  // 총 항목 수

        return result;
    }


    private OrderSpecifier<?>[] createOrderSpecifier(Pageable pageable) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        // pageable로부터 제공된 정렬 정보 기반으로 OrderSpecifier 생성
        for (Sort.Order order : pageable.getSort()) {
            // PathBuilder로 CostcoMember의 필드를 동적으로 참조
            PathBuilder<CostcoProduct> pathBuilder = new PathBuilder<>(CostcoProduct.class, "costcoProduct");

            // 정렬할 필드에 대한 Path 객체 생성
            ComparableExpressionBase<?> memberField = switch (order.getProperty()) {
                case "name" -> pathBuilder.getString("name");  // name 필드로 정렬
                case "price" -> pathBuilder.getString("price");  // email 필드로 정렬
                case "id" -> pathBuilder.getComparable("id", UUID.class);  // id 필드로 정렬 (UUID 타입)
                // 다른 필드들도 추가 가능
                default -> pathBuilder.getComparable("id", UUID.class);  // 기본 정렬 필드
            };

            // OrderSpecifier 추가 (ASC/DESC에 맞춰 정렬)
            if (order.getDirection().isAscending()) {
                orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, memberField));
            } else {
                orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, memberField));
            }
        }

        return orderSpecifiers.toArray(new OrderSpecifier[0]);
    }

}
