package co.kr.compig.api.presentation.member.request;

import co.kr.compig.global.code.MemberRegisterType;
import co.kr.compig.global.code.OauthType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SocialLoginRequest {

	private String code;
	private String token;
	@NotNull
	private MemberRegisterType memberRegisterType;
	@NotNull
	private OauthType oauthType;

}
