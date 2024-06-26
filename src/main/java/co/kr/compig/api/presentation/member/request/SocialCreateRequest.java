package co.kr.compig.api.presentation.member.request;

import static co.kr.compig.global.utils.KeyGen.*;

import co.kr.compig.api.domain.member.Member;
import co.kr.compig.global.code.MemberRegisterType;
import co.kr.compig.global.code.UserGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialCreateRequest {

	@NotBlank
	private String socialId; //소셜 로그인 id
	@NotBlank
	private String userNm; //사용자 이름 ex)홍 길동
	@NotBlank
	private String email; //이메일
	@NotNull
	private MemberRegisterType memberRegisterType; //소셜로그인 타입
	private String telNo; // 핸드폰 번호
	private boolean marketingAppPush; // 앱 푸시알림 수신동의

	public Member converterEntity() {
		String randomTimeKey = getRandomTimeKey();
		return Member.builder()
			.userNm(this.userNm)
			.userId(this.email)
			.email(this.email)
			.userPw(this.email + randomTimeKey)
			.internalRandomKey(randomTimeKey)
			.memberRegisterType(this.memberRegisterType)
			.userGroup(UserGroup.USER)
			.telNo(this.telNo)
			.build();
	}
}
