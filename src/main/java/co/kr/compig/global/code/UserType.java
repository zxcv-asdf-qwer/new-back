package co.kr.compig.global.code;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserType implements BaseEnumCode<String> {
	SYS_ADMIN("SYS_ADMIN", "슈퍼관리자"),
	SYS_USER("SYS_USER", "내부사용자"),
	USER("USER", "회원"),
	BLACK("BLACK", "블랙리스트");

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
