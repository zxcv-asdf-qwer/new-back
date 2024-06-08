package co.kr.compig.global.code;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MemberRegisterType implements BaseEnumCode<String> {
	GENERAL("GENERAL", "일반 회원가입"), // 일반 회원가입
	KAKAO("KAKAO", "카카오 회원가입"), // 카카오 회원가입
	NAVER("NAVER", "네이버 회원가입"), // 네이버 회원가입
	APPLE("APPLE", "애플 회원가입"), // 애플 회원가입
	GOOGLE("GOOGLE", "구글 회원가입"), // 구글 회원가입
	;

	private final String code;
	private final String desc;

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getDesc() {
		return desc;
	}

}
