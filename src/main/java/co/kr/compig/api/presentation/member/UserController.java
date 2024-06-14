package co.kr.compig.api.presentation.member;

import java.net.URI;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.kr.compig.api.application.member.MemberService;
import co.kr.compig.api.presentation.member.request.LeaveRequest;
import co.kr.compig.api.presentation.member.request.MemberUpdateRequest;
import co.kr.compig.api.presentation.member.response.MemberResponse;
import co.kr.compig.global.dto.Response;
import co.kr.compig.global.utils.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "유저 공통", description = "유저 공통")
@SecurityRequirement(name = "Bearer Authentication")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users", produces = "application/json")
public class UserController {
	private final MemberService memberService;

	@Operation(summary = "내정보 가져오기", description = "내정보 가져오기")
	@GetMapping
	public ResponseEntity<Response<MemberResponse>> findMe() {
		return ResponseEntity.ok().body(Response.<MemberResponse>builder().data(memberService.getMyInfo()).build());
	}

	@Operation(summary = "로그아웃", description = "로그아웃")
	@GetMapping("/logout")
	public ResponseEntity<Response<?>> getAdminPage(@ParameterObject @RequestParam String refreshToken) {
		memberService.logout(refreshToken);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "수정")
	@PutMapping
	public ResponseEntity<Response<?>> userUpdate(
		@RequestBody MemberUpdateRequest memberUpdateRequest) {
		memberService.updateMember(memberUpdateRequest);
		return ResponseEntity.created(URI.create("/users")).build();
	}

	@Operation(summary = "탈퇴")
	@PutMapping("/leave")
	public ResponseEntity<Response<?>> doUserLeave(@RequestBody LeaveRequest leaveRequest) {
		memberService.doUserLeave(SecurityUtil.getMemberId(), leaveRequest);
		return ResponseEntity.ok().build();
	}
}
