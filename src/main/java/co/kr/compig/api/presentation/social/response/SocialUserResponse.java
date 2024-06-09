package co.kr.compig.api.presentation.social.response;

import co.kr.compig.global.code.MemberRegisterType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class SocialUserResponse {

	private MemberRegisterType memberRegisterType;
	private String socialId;//social userId
	private String email;
	private String name;
	private String gender;
	private String birthday;

}
