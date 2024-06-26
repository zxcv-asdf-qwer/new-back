package co.kr.compig.api.application.member;

import static co.kr.compig.api.domain.member.QMember.*;
import static co.kr.compig.global.utils.SecurityUtil.*;

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

import co.kr.compig.api.application.social.SocialLoginService;
import co.kr.compig.api.domain.member.Member;
import co.kr.compig.api.domain.member.MemberGroup;
import co.kr.compig.api.domain.member.MemberGroupRepository;
import co.kr.compig.api.domain.member.MemberRepository;
import co.kr.compig.api.domain.member.MemberRepositoryCustom;
import co.kr.compig.api.infra.auth.keycloak.KeycloakAuthApi;
import co.kr.compig.api.infra.auth.keycloak.model.KeycloakAccessTokenRequest;
import co.kr.compig.api.infra.auth.keycloak.model.KeycloakRefreshTokenRequest;
import co.kr.compig.api.infra.auth.keycloak.model.LogoutRequest;
import co.kr.compig.api.presentation.member.request.AdminMemberCreate;
import co.kr.compig.api.presentation.member.request.AdminMemberUpdate;
import co.kr.compig.api.presentation.member.request.LeaveRequest;
import co.kr.compig.api.presentation.member.request.LoginRequest;
import co.kr.compig.api.presentation.member.request.MemberSearchRequest;
import co.kr.compig.api.presentation.member.request.MemberUpdateRequest;
import co.kr.compig.api.presentation.member.request.SocialCreateRequest;
import co.kr.compig.api.presentation.member.request.SocialLoginRequest;
import co.kr.compig.api.presentation.member.response.AdminMemberResponse;
import co.kr.compig.api.presentation.member.response.LoginResponse;
import co.kr.compig.api.presentation.member.response.MemberResponse;
import co.kr.compig.api.presentation.member.response.SocialUserResponse;
import co.kr.compig.global.code.MemberRegisterType;
import co.kr.compig.global.code.OauthType;
import co.kr.compig.global.code.UseYn;
import co.kr.compig.global.code.UserGroup;
import co.kr.compig.global.error.exception.BizException;
import co.kr.compig.global.error.exception.NotExistDataException;
import co.kr.compig.global.keycloak.KeycloakHandler;
import co.kr.compig.global.keycloak.KeycloakHolder;
import co.kr.compig.global.keycloak.KeycloakProperties;
import co.kr.compig.global.utils.ApplicationContextUtil;
import co.kr.compig.global.utils.GsonLocalDateTimeAdapter;
import co.kr.compig.global.utils.SecurityUtil;
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
	private final KeycloakAuthApi keycloakAuthApi;
	private final KeycloakProperties keycloakProperties;
	private final JPAQueryFactory jpaQueryFactory;

	public String adminCreate(AdminMemberCreate adminMemberCreate) {
		Member member = adminMemberCreate.convertEntity();
		setReferenceDomain(member.getUserGroup(), member);
		member.createUserKeyCloak(null, null);
		member.passwordEncode();

		return memberRepository.save(member).getId();
	}

	public void setReferenceDomain(UserGroup userGroup, Member member) {
		// keycloakHandler를 사용하여 그룹 리스트를 가져옴
		List<GroupRepresentation> groups = keycloakHandler.getGroups().groups();

		// 모든 그룹과 하위 그룹을 포함하는 하나의 리스트로 평탄화
		List<GroupRepresentation> allGroups = groups.stream()
			// 각 그룹에 대해 Stream<Group>을 반환
			.flatMap(group -> Stream.concat(Stream.of(group), group.getSubGroups().stream())).toList();

		Optional<GroupRepresentation> handler = allGroups.stream()
			.filter(group -> group.getName().equals(userGroup.getCode()))
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
		if (!(member.getUserGroup() == UserGroup.SYS_ADMIN || member.getUserGroup() == UserGroup.SYS_USER)) {
			throw new BizException("권한이 없습니다.");
		}
		return member.toAdminMemberResponse();
	}

	public String updateAdminById(String memberId, AdminMemberUpdate adminMemberUpdate) {
		Member memberById = this.getAbleMemberById(memberId);
		memberById.updateAdminMember(adminMemberUpdate);
		setReferenceDomain(memberById.getUserGroup(), memberById);
		memberById.updateUserKeyCloak();
		memberById.passwordEncode();
		return memberById.getId();
	}

	public Object doSocialLogin(SocialLoginRequest socialLoginRequest) {
		SocialLoginService loginService = ApplicationContextUtil.getBean(
				socialLoginRequest.getMemberRegisterType().getServiceName(), SocialLoginService.class)
			.orElseThrow(
				() -> new BizException(String.format("### 로그인 서비스 [%s] 없음###",
					socialLoginRequest.getMemberRegisterType().getServiceName())));
		SocialUserResponse socialUserResponse;
		if (socialLoginRequest.getOauthType() != OauthType.AUTH_CODE) {
			socialUserResponse = loginService.tokenSocialUserResponse(socialLoginRequest);
		} else {
			socialUserResponse = loginService.codeSocialUserResponse(socialLoginRequest);
		}

		Optional<Member> optionalMember = memberRepository.findByEmailAndUseYn(socialUserResponse.getEmail(), UseYn.Y);
		if (optionalMember.isPresent()) {
			Member member = optionalMember.get();
			try {
				// 공통 로직 처리: 키클락 로그인 실행
				return this.getKeycloakAccessToken(member.getEmail(),
					member.getEmail() + member.getInternalRandomKey());
				// 키클락 로그인 실행
			} catch (Exception e) {
				throw new BizException("로그인 실패");
			}
		}
		return socialUserResponse;
	}

	public LoginResponse doLogin(LoginRequest loginRequest) {
		Optional<Member> optionalMember = memberRepository.findByUserIdAndUseYn(loginRequest.getUserId(), UseYn.Y);
		if (optionalMember.isPresent()) {
			try {
				// 공통 로직 처리: 키클락 로그인 실행
				return this.getKeycloakAccessToken(loginRequest.getUserId(), loginRequest.getUserPw());
				// 키클락 로그인 실행
			} catch (Exception e) {
				throw new BizException("로그인 실패");
			}
		}
		throw new BizException("회원가입이 필요합니다.");
	}

	public LoginResponse doSocialCreate(SocialCreateRequest socialCreateRequest) {
		Optional<Member> optionalMember = memberRepository.findByEmailAndUseYn(socialCreateRequest.getEmail(), UseYn.Y);
		if (optionalMember.isPresent()) {
			throw new BizException("이미 가입된 회원 입니다.");
		}
		Member member = optionalMember.orElseGet(() -> {
			// 중복되지 않는 경우 새 회원 생성 후 반환
			Member newMember = socialCreateRequest.converterEntity();
			this.setReferenceDomain(newMember.getUserGroup(), newMember);
			newMember.createUserKeyCloak(socialCreateRequest.getSocialId(), socialCreateRequest.getUserNm());
			newMember.passwordEncode();

			String newMemberId = memberRepository.save(newMember).getId();

			return memberRepository.findById(newMemberId).orElseThrow(() -> new RuntimeException("회원 생성 후 조회 실패"));
		});
		try {
			// 공통 로직 처리: 키클락 로그인 실행
			return this.getKeycloakAccessToken(member.getEmail(),
				member.getEmail() + member.getInternalRandomKey());
			// 키클락 로그인 실행
		} catch (Exception e) {
			throw new BizException("로그인 실패");
		}
	}

	private LoginResponse getKeycloakAccessToken(String userId, String userPw) {
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

		return gson.fromJson(Objects.requireNonNull(response.getBody()).toString(), LoginResponse.class);
	}

	public LoginResponse getKeycloakRefreshToken(String refreshToken) {
		try {
			ResponseEntity<?> response = keycloakAuthApi.getRefreshToken(KeycloakRefreshTokenRequest.builder()
				.client_id(keycloakProperties.getClientId())
				.client_secret(keycloakProperties.getClientSecret())
				.refresh_token(refreshToken)
				.build());
			log.info("keycloak user response");
			log.info(response.toString());

			Gson gson = new GsonBuilder().setPrettyPrinting()
				.registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTimeAdapter())
				.create();

			return gson.fromJson(Objects.requireNonNull(response.getBody()).toString(), LoginResponse.class);
		} catch (Exception e) {
			throw new BizException("access token 재발급 실패");
		}
	}

	public void doUserLeave(String memberId, LeaveRequest leaveRequest) {
		Member member = getAbleMemberById(memberId);
		if (leaveRequest == null) {
			leaveRequest = new LeaveRequest();
		}
		if (member.getMemberRegisterType() != MemberRegisterType.GENERAL) {
			SocialLoginService loginService = ApplicationContextUtil.getBean(
					member.getMemberRegisterType().getServiceName(), SocialLoginService.class)
				.orElseThrow(
					() -> new BizException(String.format("### 로그인 서비스 [%s] 없음###",
						member.getMemberRegisterType().getServiceName())));
			loginService.revoke(leaveRequest);
		}
		member.setLeaveMember(leaveRequest.getLeaveReason());

		try {
			KeycloakHandler keycloakHandler = KeycloakHolder.get();
			keycloakHandler.deleteUser(member.getId());
		} catch (Exception e) {
			log.error("LeaveMember Keycloak Error", e);
		}
	}

	public void logout(String refreshToken) {
		try {
			keycloakAuthApi.logout(LogoutRequest.builder()
				.client_id(keycloakProperties.getClientId())
				.client_secret(keycloakProperties.getClientSecret())
				.refresh_token(refreshToken)
				.build());
		} catch (Exception e) {
			//notthing
		}
	}

	public MemberResponse getMyInfo() {
		Member memberById = this.getMemberById(getMemberId());
		return memberById.toMemberResponse();
	}

	public void updateMember(MemberUpdateRequest memberUpdateRequest) {
		Member memberById = this.getMemberById(SecurityUtil.getMemberId());
		setReferenceDomain(memberUpdateRequest.getUserGroup(), memberById);
		memberById.updateUserKeyCloak();
		memberById.update(memberUpdateRequest);
	}
}