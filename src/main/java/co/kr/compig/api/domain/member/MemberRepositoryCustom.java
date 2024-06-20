package co.kr.compig.api.domain.member;

import static co.kr.compig.api.domain.member.QMember.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.google.common.base.CaseFormat;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import co.kr.compig.api.presentation.member.request.MemberSearchRequest;
import co.kr.compig.api.presentation.member.response.AdminMemberResponse;
import co.kr.compig.global.code.UseYn;
import co.kr.compig.global.code.UserGroup;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;

	private JPAQuery<?> createBaseQuery(BooleanExpression predicate) {
		return jpaQueryFactory
			.from(member)
			.where(predicate);
	}

	private BooleanExpression createPredicate(MemberSearchRequest request) {
		BooleanExpression predicate = Expressions.asBoolean(true).isTrue();

		if (request.getUserNm() != null) {
			predicate = predicate.and(member.userNm.contains(request.getUserNm()));
		}

		if (request.getTelNo() != null) {
			predicate = predicate.and(member.telNo.contains(request.getTelNo()));
		}

		if (request.getFromCreatedOn() != null) {
			predicate = predicate.and(
				member.createdAndModified.createdOn.goe(request.getFromCreatedOn())); //크거나 같고(.goe)
		}

		if (request.getToCreatedOn() != null) {
			predicate = predicate.and(member.createdAndModified.createdOn.loe(request.getToCreatedOn())); //작거나 같은(.loe)
		}

		return predicate;
	}

	private void applySorting(JPAQuery<?> query, Pageable pageable) {
		for (Sort.Order order : pageable.getSort()) {
			Path<Object> target = Expressions.path(Object.class, member,
				CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_CAMEL, order.getProperty()));
			@SuppressWarnings({"rawtypes", "unchecked"})
			OrderSpecifier<?> orderSpecifier = new OrderSpecifier(
				order.isAscending() ? Order.ASC : Order.DESC, target);
			query.orderBy(orderSpecifier);
		}
	}

	public Page<AdminMemberResponse> getAdminPage(MemberSearchRequest request) {
		BooleanExpression predicate = createPredicate(request);

		JPAQuery<Member> query = createBaseQuery(predicate)
			.select(member)
			.where(member.userType.eq(UserGroup.SYS_ADMIN)
				.or(member.userType.eq(UserGroup.SYS_USER))
			)
			.where(member.useYn.eq(UseYn.Y));
		Pageable pageable = request.pageable();

		//정렬
		applySorting(query, pageable);

		List<Member> members = query
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize()) // 페이징
			.fetch();

		List<AdminMemberResponse> responses = members.stream()
			.map(Member::toAdminMemberResponse)
			.collect(Collectors.toList());

		JPAQuery<Long> countQuery = createBaseQuery(predicate)
			.select(member.count())
			.where(member.userType.eq(UserGroup.SYS_ADMIN)
				.or(member.userType.eq(UserGroup.SYS_USER)));

		return PageableExecutionUtils.getPage(responses, pageable, countQuery::fetchOne);
	}

}
