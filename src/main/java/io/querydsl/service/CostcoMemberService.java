package io.querydsl.service;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.querydsl.dto.CostcoMemberDTO;
import io.querydsl.dto.CostcoMemberUpdateRequestDTO;
import io.querydsl.dto.CostcoOrderDTO;
import io.querydsl.entity.CostcoMember;
import io.querydsl.entity.QCostcoMember;
import io.querydsl.repository.CostcoMemberRepository;
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
public class CostcoMemberService {

    private final CostcoMemberRepository costcoMemberRepository;
    private final JPAQueryFactory queryFactory;
    private final QCostcoMember qCostcoMember = QCostcoMember.costcoMember;

    /**
     * 멤버를 등록하는 서비스 메서드
     *
     * @param memberCreateRequest 생성할 멤버의 정보
     * @return 등록된 멤버 DTO
     */
    @Transactional
    public Map<String, Object> createMember(CostcoMemberUpdateRequestDTO memberCreateRequest) {
        // 생성자를 사용하여 Member 엔티티 생성
        CostcoMember member = new CostcoMember(memberCreateRequest.getName(), memberCreateRequest.getEmail());

        // Member를 데이터베이스에 저장
        CostcoMember savedMember = costcoMemberRepository.save(member);

        // DTO 변환 후 결과 객체에 바로 저장
        return Collections.singletonMap("members", Collections.singletonList(convertToDTO(savedMember, false)));
    }

    @Transactional
    public Map<String, Object> getMemberById(UUID costcoMemberId, boolean includeOrders) {
        // id로 조회하는 쿼리 생성
        CostcoMember member = queryFactory.selectFrom(qCostcoMember)
                .where(qCostcoMember.id.eq(costcoMemberId))
                .fetchOne();  // id로 한 명의 회원만 조회

        // 회원이 존재하면 DTO로 변환하고, 존재하지 않으면 빈 리스트 반환
        return member != null
                ? Collections.singletonMap("members", Collections.singletonList(convertToDTO(member, includeOrders)))
                : Collections.singletonMap("members", Collections.emptyList());
    }

    @Transactional
    public Map<String, Object> getMembersWithOrders(boolean includeOrders, Pageable pageable) {
        Map<String, Object> result = new HashMap<>();

        // 쿼리 생성 및 페이지 처리
        List<CostcoMember> members = queryFactory.selectFrom(qCostcoMember)
                .orderBy(createOrderSpecifier(pageable))  // 정렬 처리 (배열로 변환)/ 정렬 처리
                .offset((long) pageable.getPageNumber() * pageable.getPageSize())  // 페이지 번호에 따른 offset 처리
                .limit(pageable.getPageSize())  // 페이지 크기만큼 데이터 조회
                .fetch();  // 결과 조회

        // DTO로 변환
        List<CostcoMemberDTO> costcoMemberDTOS = members.stream()
                .map(member -> convertToDTO(member, includeOrders))
                .collect(Collectors.toList());

        // 총 항목 수를 구하기 위한 효율적인 방법 (fetchCount() 사용)
        Long totalCount = queryFactory.select(Wildcard.count)
                .from(qCostcoMember)
                .fetchOne();
        totalCount = totalCount == null ? 0 : totalCount;
        // 페이지 결과 생성
        Page<CostcoMemberDTO> page = new PageImpl<>(costcoMemberDTOS, pageable, totalCount);

        result.put("members", page.getContent());
        result.put("totalPages", page.getTotalPages());  // 총 페이지 수
        result.put("totalElements", page.getTotalElements());  // 총 항목 수

        return result;
    }

    @Transactional
    public boolean updateMember(UUID id, CostcoMemberUpdateRequestDTO costcoMemberUpdateRequest) {
        // 멤버 조회
        CostcoMember member = queryFactory.selectFrom(qCostcoMember)
                .where(qCostcoMember.id.eq(costcoMemberUpdateRequest.getId()))
                .fetchOne();

        if (member != null) {
            // 멤버 정보를 업데이트
            member.setName(costcoMemberUpdateRequest.getName());
            member.setEmail(costcoMemberUpdateRequest.getEmail());

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
    public boolean deleteMember(UUID costcoMemberId) {
        // 멤버 조회
        CostcoMember member = queryFactory.selectFrom(qCostcoMember)
                .where(qCostcoMember.id.eq(costcoMemberId))
                .fetchOne();

        if (member != null) {
            // 멤버 삭제
            queryFactory.delete(qCostcoMember)
                    .where(qCostcoMember.id.eq(costcoMemberId))
                    .execute();
            return true;  // 삭제 성공
        } else {
            return false;  // 멤버가 존재하지 않으면 삭제 실패
        }
    }

    private CostcoMemberDTO convertToDTO(CostcoMember costcoMember, boolean includeOrders) {
        // 기본적으로 Member 정보만 포함
        List<CostcoOrderDTO> costcoOrders = null;

        // includeOrders가 true일 때만 CostcoOrders를 포함
        if (includeOrders && costcoMember.getOrders() != null) {
            costcoOrders = costcoMember.getOrders().stream()
                    .map(order -> new CostcoOrderDTO(order.getId(), order.getOrderDate(), order.getTotalAmount())) // CostcoOrders를 DTO로 변환
                    .collect(Collectors.toList());
        }

        return new CostcoMemberDTO(costcoMember.getId(), costcoMember.getName(), costcoMember.getEmail(), costcoOrders);
    }

    private OrderSpecifier<?>[] createOrderSpecifier(Pageable pageable) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        // pageable로부터 제공된 정렬 정보 기반으로 OrderSpecifier 생성
        for (Sort.Order order : pageable.getSort()) {
            // PathBuilder로 CostcoMember의 필드를 동적으로 참조
            PathBuilder<CostcoMember> pathBuilder = new PathBuilder<>(CostcoMember.class, "costcoMember");

            // 정렬할 필드에 대한 Path 객체 생성
            ComparableExpressionBase<?> memberField = switch (order.getProperty()) {
                case "name" -> pathBuilder.getString("name");  // name 필드로 정렬
                case "email" -> pathBuilder.getString("email");  // email 필드로 정렬
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