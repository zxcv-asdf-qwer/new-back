package co.kr.compig.api.application.member;

import static co.kr.compig.api.domain.member.QMember.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.keycloak.representations.idm.GroupRepresentation;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.jpa.impl.JPAQueryFactory;

import co.kr.compig.api.domain.member.Member;
import co.kr.compig.api.domain.member.MemberGroup;
import co.kr.compig.api.domain.member.MemberGroupRepository;
import co.kr.compig.api.domain.member.MemberRepository;
import co.kr.compig.api.domain.member.MemberRepositoryCustom;
import co.kr.compig.api.presentation.member.request.AdminMemberCreate;
import co.kr.compig.api.presentation.member.request.AdminMemberUpdate;
import co.kr.compig.api.presentation.member.request.LeaveRequest;
import co.kr.compig.api.presentation.member.request.MemberSearchRequest;
import co.kr.compig.api.presentation.member.response.AdminMemberResponse;
import co.kr.compig.global.code.UseYn;
import co.kr.compig.global.code.UserType;
import co.kr.compig.global.error.exception.BizException;
import co.kr.compig.global.error.exception.NotExistDataException;
import co.kr.compig.global.keycloak.KeycloakHandler;
import co.kr.compig.global.keycloak.KeycloakHolder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

	private final MemberRepository memberRepository;
	private final MemberRepositoryCustom memberRepositoryCustom;
	private final MemberGroupRepository memberGroupRepository;
	private final KeycloakHandler keycloakHandler;
	private final JPAQueryFactory jpaQueryFactory;

	public String adminCreate(AdminMemberCreate adminMemberCreate) {
		Member member = adminMemberCreate.convertEntity();
		setReferenceDomain(member.getUserType(), member);
		member.createUserKeyCloak(null, null);
		member.passwordEncode();

		return memberRepository.save(member).getId();
	}

	public void setReferenceDomain(UserType userType, Member member) {
		// keycloakHandler를 사용하여 그룹 리스트를 가져옴
		List<GroupRepresentation> groups = keycloakHandler.getGroups().groups();

		// 모든 그룹과 하위 그룹을 포함하는 하나의 리스트로 평탄화
		List<GroupRepresentation> allGroups = groups.stream()
			// 각 그룹에 대해 Stream<Group>을 반환
			.flatMap(group -> Stream.concat(Stream.of(group), group.getSubGroups().stream())).toList();

		Optional<GroupRepresentation> handler = allGroups.stream()
			.filter(group -> group.getName().equals(userType.getCode()))
			.findFirst();

		Optional<MemberGroup> memberGroup = memberGroupRepository.findByMember_id(member.getId());

		if (memberGroup.isPresent() && handler.isPresent()) {
			memberGroup.get().updateGroupInfo(handler.get().getId(), handler.get().getName(), handler.get().getPath());
		} else {
			member.addGroups(MemberGroup.builder()
				.groupKey(handler.get().getId())
				.groupNm(handler.get().getName())
				.groupPath(handler.get().getPath())
				.build());
		}
	}

	@Transactional(readOnly = true)
	public Page<AdminMemberResponse> getAdminPage(@Valid MemberSearchRequest memberSearchRequest) {
		return memberRepositoryCustom.getAdminPage(memberSearchRequest);
	}

	@Transactional(readOnly = true)
	public Member getMemberById(String memberId) {
		return memberRepository.findById(memberId).orElseThrow(NotExistDataException::new);
	}

	@Transactional(readOnly = true)
	public Member getAbleMemberById(String memberId) {
		Member member1 = jpaQueryFactory
			.selectFrom(member)
			.where(member.useYn.eq(UseYn.Y)
				.and(member.id.eq(memberId))
			).fetchFirst();
		return Optional.ofNullable(member1).orElseThrow(NotExistDataException::new);
	}

	@Transactional(readOnly = true)
	public Member getMemberByIdSecurity(String memberId) {
		return memberRepository.findById(memberId).orElse(null);
	}

	@Transactional(readOnly = true)
	public AdminMemberResponse getMemberResponseByMemberId(String memberId) {
		Member member = this.getAbleMemberById(memberId);
		if (!(member.getUserType() == UserType.SYS_ADMIN || member.getUserType() == UserType.SYS_USER)) {
			throw new BizException("권한이 없습니다.");
		}
		return member.toAdminMemberResponse();
	}

	public String updateAdminById(String memberId, AdminMemberUpdate adminMemberUpdate) {
		Member memberById = this.getAbleMemberById(memberId);
		memberById.updateAdminMember(adminMemberUpdate);
		setReferenceDomain(memberById.getUserType(), memberById);
		memberById.updateUserKeyCloak();
		memberById.passwordEncode();
		return memberById.getId();
	}

	public void doUserLeave(String memberId) {
		Member member = getAbleMemberById(memberId);
		doUserLeave(member, null);
	}

	public void doUserLeave(Member member, LeaveRequest leaveRequest) {
		if (leaveRequest == null) {
			leaveRequest = new LeaveRequest();
		}

		member.setLeaveMember(leaveRequest.getLeaveReason());

		try {
			KeycloakHandler keycloakHandler = KeycloakHolder.get();
			keycloakHandler.deleteUser(member.getId());
		} catch (Exception e) {
			log.error("LeaveMember Keycloak Error", e);
		}
	}

}