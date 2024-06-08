package co.kr.compig.global.utils;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import co.kr.compig.api.application.member.MemberService;
import co.kr.compig.api.domain.member.Member;
import co.kr.compig.global.error.exception.BizException;
import co.kr.compig.global.security.CustomOauth2User;
import co.kr.compig.global.security.CustomOauth2UserAuthenticatedToken;

@Component
public class SecurityUtil {
	private static MemberService memberService;

	@Autowired
	public SecurityUtil(MemberService service) {
		memberService = service;
	}

	public static Member getCurrentMember() {
		String memberId = getMemberId();
		if (memberId == "SCHEDULER") {
			return Member.builder().id("SCHEDULER").build();
		}

		if (memberId != null) {
			return memberService.getMemberByIdSecurity(memberId);
		}

		return null;
	}

	public final static String TEST_TOKEN = "testToken";

	/**
	 * SecurityContextHolder에 userId, role에 대한 테스트 토큰 삽입
	 */
	public static void setUser(String userId, String role) {
		Assert.notNull(userId, "userId is require");
		Assert.notNull(role, "role is require");

		if (role.split("ROLE_").length == 1) {
			role = "ROLE_" + role;
		}

		CustomOauth2UserAuthenticatedToken token =
			new CustomOauth2UserAuthenticatedToken(
				new Jwt(SecurityUtil.TEST_TOKEN, Instant.now(), Instant.MAX, Map.of("header", "header"),
					Map.of("claim", "claim")),
				Set.of(new SimpleGrantedAuthority(role)),
				CustomOauth2User.builder().userId(userId).build());

		SecurityContextHolder.getContext().setAuthentication(token);
	}

	public static void setISOToken(CustomOauth2UserAuthenticatedToken token) {
		SecurityContextHolder.getContext().setAuthentication(token);
	}

	/**
	 * 인증정보 없는 경우 null 리턴
	 * @return {@link CustomOauth2User#getUserId()}
	 */
	public static String getUserId() {
		CustomOauth2User user = getCustomOauth2User();

		return user == null ? null : user.getUserId();
	}

	public static String getMemberId() {
		CustomOauth2User user = getCustomOauth2User();

		return user == null ? null : user.getId();
	}

	/**
	 * 인증정보 없으면 null 리턴
	 *
	 * @return {@link CustomOauth2User} 토큰 사용자 정보
	 */
	public static CustomOauth2User getCustomOauth2User() {
		CustomOauth2UserAuthenticatedToken authenticatedToken = getAuthentication();

		return authenticatedToken == null ? null : authenticatedToken.getCredentials();
	}

	/**
	 * 인증정보 없으면 null 리턴
	 *
	 * @return JWT 토큰
	 */
	public static Jwt getToken() {
		CustomOauth2UserAuthenticatedToken authenticatedToken = getAuthentication();

		return authenticatedToken == null ? null : authenticatedToken.getToken();
	}

	/**
	 * 인증정보 없으면 null 리턴
	 *
	 * @return JWT 토큰 value
	 */
	public static String getTokenValue() {
		Jwt token = getToken();

		if (token != null && !token.getTokenValue().equalsIgnoreCase(TEST_TOKEN)) {
			return token.getTokenValue();
		} else {
			return null;
		}
	}

	/**
	 * @return 권한 정보
	 */
	public static Collection<GrantedAuthority> getRole() {
		CustomOauth2UserAuthenticatedToken authenticatedToken = getAuthentication();

		return authenticatedToken == null ? Collections.emptyList()
			: authenticatedToken.getAuthorities();
	}

	private static CustomOauth2UserAuthenticatedToken getAuthentication() {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			if (authentication != null) {
				return (CustomOauth2UserAuthenticatedToken)authentication;
			} else {
				throw new BizException("알 수 없는 인증 정보");
			}

		} catch (Exception e) {
			return null;
		}
	}
}
