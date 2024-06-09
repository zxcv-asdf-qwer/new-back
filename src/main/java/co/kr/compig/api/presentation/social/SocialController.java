package co.kr.compig.api.presentation.social;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.kr.compig.api.application.member.MemberService;
import co.kr.compig.api.presentation.social.request.SocialCreateRequest;
import co.kr.compig.api.presentation.social.request.SocialLoginRequest;
import co.kr.compig.global.dto.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "소셜로그인", description = "소셜로그인 관련 API")
@RestController
@RequestMapping(path = "/pb/social")
@RequiredArgsConstructor
public class SocialController {

	private final MemberService memberService;

	@Operation(summary = "소셜 로그인")
	@PostMapping(path = "/login")
	public ResponseEntity<Response<?>> doSocialLogin(@RequestBody SocialLoginRequest socialLoginRequest) {
		return ResponseEntity.ok().body(Response.builder()
			.data(memberService.doSocialLogin(socialLoginRequest))
			.build());
	}

	@Operation(summary = "소셜 회원가입")
	@PostMapping
	public ResponseEntity<Response<?>> doSocialLogin(@RequestBody SocialCreateRequest socialCreateRequest) {
		return ResponseEntity.ok().body(Response.builder()
			.data(memberService.doSocialCreate(socialCreateRequest))
			.build());
	}
}
