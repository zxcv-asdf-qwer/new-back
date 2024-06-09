package co.kr.compig.api.application.member;

import static co.kr.compig.api.domain.member.QMember.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.keycloak.representations.idm.GroupRepresentation;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import co.kr.compig.api.application.social.LoginServiceImpl;
import co.kr.compig.api.application.social.SocialLoginService;
import co.kr.compig.api.domain.member.Member;
import co.kr.compig.api.domain.member.MemberGroup;
import co.kr.compig.api.domain.member.MemberGroupRepository;
import co.kr.compig.api.domain.member.MemberRepository;
import co.kr.compig.api.domain.member.MemberRepositoryCustom;
import co.kr.compig.api.infra.auth.keycloak.KeycloakAuthApi;
import co.kr.compig.api.infra.auth.keycloak.model.KeycloakAccessTokenRequest;
import co.kr.compig.api.presentation.member.request.AdminMemberCreate;
import co.kr.compig.api.presentation.member.request.AdminMemberUpdate;
import co.kr.compig.api.presentation.member.request.LeaveRequest;
import co.kr.compig.api.presentation.member.request.MemberSearchRequest;
import co.kr.compig.api.presentation.member.response.AdminMemberResponse;
import co.kr.compig.api.presentation.social.request.SocialCreateRequest;
import co.kr.compig.api.presentation.social.request.SocialLoginRequest;
import co.kr.compig.api.presentation.social.response.SocialLoginResponse;
import co.kr.compig.api.presentation.social.response.SocialUserResponse;
import co.kr.compig.global.code.MemberRegisterType;
import co.kr.compig.global.code.OauthType;
import co.kr.compig.global.code.UseYn;
import co.kr.compig.global.code.UserType;
import co.kr.compig.global.error.exception.BizException;
import co.kr.compig.global.error.exception.NotExistDataException;
import co.kr.compig.global.keycloak.KeycloakHandler;
import co.kr.compig.global.keycloak.KeycloakHolder;
import co.kr.compig.global.keycloak.KeycloakProperties;
import co.kr.compig.global.utils.GsonLocalDateTimeAdapter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

	private final List<SocialLoginService> loginServices;
	private final MemberRepository memberRepository;
	private final MemberRepositoryCustom memberRepositoryCustom;
	private final MemberGroupRepository memberGroupRepository;
	private final KeycloakHandler keycloakHandler;
	private final KeycloakAuthApi keycloakAuthApi;
	private final KeycloakProperties keycloakProperties;
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

	public SocialLoginService getLoginService(MemberRegisterType memberRegisterType) {
		for (SocialLoginService loginService : loginServices) {
			if (memberRegisterType.equals(loginService.getServiceName())) {
				log.info("login service name: {}", loginService.getServiceName());
				return loginService;
			}
		}
		return new LoginServiceImpl();
	}

	public Object doSocialLogin(SocialLoginRequest socialLoginRequest) {
		SocialLoginService loginService = this.getLoginService(socialLoginRequest.getMemberRegisterType());
		SocialUserResponse socialUserResponse;
		if (socialLoginRequest.getOauthType() != OauthType.AUTH_CODE) {
			socialUserResponse = loginService.tokeSocialUserResponse(socialLoginRequest);
		} else {
			socialUserResponse = loginService.codeSocialUserResponse(socialLoginRequest);
		}

		Optional<Member> optionalMember = memberRepository.findByEmailAndUseYn(socialUserResponse.getEmail(), UseYn.Y);
		if (optionalMember.isPresent()) {
			Member member = optionalMember.get();
			// 공통 로직 처리: 키클락 로그인 실행
			return this.getKeycloakAccessToken(member.getEmail(), member.getEmail() + member.getInternalRandomKey());
			// 키클락 로그인 실행
		}
		return socialUserResponse;
	}

	public SocialLoginResponse doSocialCreate(SocialCreateRequest socialCreateRequest) {
		Optional<Member> optionalMember = memberRepository.findByEmailAndUseYn(socialCreateRequest.getEmail(), UseYn.Y);
		if (optionalMember.isPresent()) {
			throw new BizException("이미 가입된 회원 입니다.");
		}
		Member member = optionalMember.orElseGet(() -> {
			// 중복되지 않는 경우 새 회원 생성 후 반환
			Member newMember = socialCreateRequest.converterEntity();
			this.setReferenceDomain(socialCreateRequest.getUserType(), newMember);
			newMember.createUserKeyCloak(socialCreateRequest.getSocialId(), socialCreateRequest.getUserNm());
			newMember.passwordEncode();

			String newMemberId = memberRepository.save(newMember).getId();

			return memberRepository.findById(newMemberId).orElseThrow(() -> new RuntimeException("회원 생성 후 조회 실패"));
		});
		// 공통 로직 처리: 키클락 로그인 실행
		return this.getKeycloakAccessToken(member.getEmail(),
			member.getEmail() + member.getInternalRandomKey());
		// 키클락 로그인 실행
	}

	private SocialLoginResponse getKeycloakAccessToken(String userId, String userPw) {
		ResponseEntity<?> response = keycloakAuthApi.getAccessToken(KeycloakAccessTokenRequest.builder()
			.client_id(keycloakProperties.getClientId())
			.client_secret(keycloakProperties.getClientSecret())
			.username(userId)
			.password(userPw)
			.build());
		log.info("keycloak user response");
		log.info(response.toString());

		Gson gson = new GsonBuilder().setPrettyPrinting()
			.registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTimeAdapter())
			.create();

		return gson.fromJson(Objects.requireNonNull(response.getBody()).toString(), SocialLoginResponse.class);
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