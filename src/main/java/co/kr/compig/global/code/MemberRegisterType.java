package co.kr.compig.global.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberRegisterType implements BaseEnumCode<String> {
	GENERAL("GENERAL", "일반 회원가입", "defaultLoginServiceImpl"), // 일반 회원가입
	KAKAO("KAKAO", "카카오 회원가입", "kakaoLoginServiceImpl"), // 카카오 회원가입
	NAVER("NAVER", "네이버 회원가입", "naverLoginServiceImpl"), // 네이버 회원가입
	APPLE("APPLE", "애플 회원가입", "appleLoginServiceImpl"), // 애플 회원가입
	GOOGLE("GOOGLE", "구글 회원가입", "googleLoginServiceImpl"), // 구글 회원가입
	;

	private final String code;
	private final String desc;
	private final String serviceName;

}
