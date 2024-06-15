package co.kr.compig.api.presentation.member;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.kr.compig.api.application.member.MemberService;
import co.kr.compig.api.presentation.member.request.AdminMemberCreate;
import co.kr.compig.api.presentation.member.request.LoginRequest;
import co.kr.compig.api.presentation.member.request.SocialCreateRequest;
import co.kr.compig.api.presentation.member.request.SocialLoginRequest;
import co.kr.compig.api.presentation.member.response.LoginResponse;
import co.kr.compig.global.dto.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "유저 공통", description = "유저 공통")
@SecurityRequirement(name = "Bearer Authentication")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/pb", produces = "application/json")
public class AnonymousController {
	private final MemberService memberService;

	@Operation(summary = "소셜 로그인")
	@PostMapping(path = "/social-login")
	public ResponseEntity<Response<?>> doSocialLogin(@RequestBody @Valid SocialLoginRequest socialLoginRequest) {
		return ResponseEntity.ok().body(Response.builder()
			.data(memberService.doSocialLogin(socialLoginRequest))
			.build());
	}

	@Operation(summary = "소셜 회원가입")
	@PostMapping(path = "/social")
	public ResponseEntity<Response<?>> doSocialLogin(@RequestBody @Valid SocialCreateRequest socialCreateRequest) {
		return ResponseEntity.ok().body(Response.builder()
			.data(memberService.doSocialCreate(socialCreateRequest))
			.build());
	}

	@Operation(summary = "관리자 로그인")
	@PostMapping(path = "/admin-login")
	public ResponseEntity<Response<LoginResponse>> doLogin(@RequestBody @Valid LoginRequest loginRequest) {
		return ResponseEntity.ok().body(Response.<LoginResponse>builder()
			.data(memberService.doLogin(loginRequest))
			.build());
	}

	@Operation(summary = "관리자 회원가입")
	@PostMapping(path = "/admin")
	public ResponseEntity<Response<?>> adminCreate(
		@RequestBody @Valid AdminMemberCreate adminMemberCreate) {
		return ResponseEntity.ok().body(Response.<Map<String, String>>builder()
			.data(Map.of("memberId", memberService.adminCreate(adminMemberCreate)))
			.build());
	}

}
